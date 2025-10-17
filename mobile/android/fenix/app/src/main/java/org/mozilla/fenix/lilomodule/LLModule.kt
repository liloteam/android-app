package org.mozilla.fenix.lilomodule

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.takeWhile
import mozilla.components.browser.state.selector.findTab
import mozilla.components.browser.state.selector.findTabOrCustomTabOrSelectedTab
import mozilla.components.browser.state.selector.selectedTab
import mozilla.components.browser.state.state.SessionState
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.concept.engine.EngineSession
import mozilla.components.lib.state.ext.flowScoped
import mozilla.components.support.base.log.logger.Logger
import mozilla.liloapp.migration.MigrationManager
import mozilla.liloapp.migration.LLWebStorageFeature
import mozilla.liloapp.migration.MigrationDataStore
import mozilla.liloapp.migration.localstorage.LocalStorageActivity
import mozilla.liloapp.migration.localstorage.LocalStorageHelper
import org.mozilla.fenix.FenixApplication
import org.mozilla.fenix.LLAppConstants
import org.mozilla.fenix.LLAppEngine
import org.mozilla.fenix.R
import org.mozilla.fenix.browser.BrowserFragment
import org.mozilla.fenix.components.menu.MenuDialogFragment
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.ext.getPreferenceKey
import org.mozilla.fenix.ext.settings
import org.mozilla.fenix.lilomodule.bookmark.BookmarkSaver
import org.mozilla.fenix.lilomodule.browser.initializeLiloUI
import org.mozilla.fenix.lilomodule.components.menu.goToLoginPage
import org.mozilla.fenix.lilomodule.search.LLSearchEngine
import org.mozilla.fenix.lilomodule.settings.LLSettings
import org.mozilla.fenix.settings.SupportUtils

object LLModule {
    private val logger = Logger("LILO:MODULE")
    private val shouldForceMigration = false // For debug purpose: set to true to force the migration process starting the app

    private var localStorageSaved = false
    private var shouldDoMigration = false
    private var shouldShowWebIntro = false

    private var webStorageFeature: LLWebStorageFeature? = null


    fun initializeLilo(context: Context) {
        val liloSettings = LLSettings(context)
        val isFirstRun = liloSettings.isFirstRun
        shouldDoMigration = liloSettings.isDdgUpdate && (isFirstRun || shouldForceMigration)

        // Force the menu banner not to be shown despite the Nimbus settings.
        val fenixSettings = context.settings()
        fenixSettings.shouldShowMenuBanner = false

        // Customize the user agent string for Lilo.
        val engineSettings = context.components.core.engine.settings
        context.components.core.engine.settings.userAgentString =
            LLAppEngine.customizedUserAgent(context, engineSettings.userAgentString)

        // Enable Firebase Analytics according to the user's preference.
        val analytics = FirebaseAnalytics.getInstance(context)
        analytics.setAnalyticsCollectionEnabled(fenixSettings.isTelemetryEnabled)

        // Select the Lilo search engine if not already selected.
        LLSearchEngine(logger).setupLiloSearchEngine(context)

        // Force the offer to translate option to be disabled.
        liloSettings.updateOfferTranslationOption(false)

        // Start the migration process if necessary.
        startMigration(context, liloSettings)

        // Do not show the onboarding if the app is updated from a previous ddg version.
        if (liloSettings.isDdgUpdate) { context.components.fenixOnboarding.finish() }

        // Define if the intro Lilo Web page should be shown.
        shouldShowWebIntro = isFirstRun && !liloSettings.isDdgUpdate

        // TEMPORARY: Never show the onboarding.
        // TODO: Must to be removed when the onboarding is fully implemented.
        //context.components.fenixOnboarding.finish()

        // Configure the search menu.
        configureSearchMenu(context)

        // Configure observers of both activity and fragment lifecycles.
        configureObservers(context)
    }
    

    val homeUrl: Uri?
        get() {
            val appUrl = if (!LLAppEngine.shouldShowLoginAsHome) LLAppConstants.AppURL.HOME else LLAppConstants.AppURL.LOGIN
            val home = appUrl.url(shouldShowWebIntro)
            if (shouldShowWebIntro) {
                shouldShowWebIntro = false
            }
            logger.info("Home URL: $home")
            return home
        }

    fun onDismissSearchDialog(dismiss: (() -> Unit)?) {
        dismiss?.invoke()
    }

    fun setShouldAddHomeTab(shouldAddHomeTab: Boolean) {
        LLAppEngine.shouldAddHomeTab = shouldAddHomeTab
    }

    fun goToLoginPage(fragment: Fragment?) {
        (fragment as? MenuDialogFragment)?.let { it.goToLoginPage() }
    }

    fun initializeLiloBrowser(fragment: BrowserFragment, sessionId: String?) {
        fragment.initializeLiloUI()

        if (shouldDoMigration) {
            observePageLoadForLocalStorage(fragment, sessionId)
        }
    }

    fun showLoginInsteadOfHome() {
        LLAppEngine.shouldShowLoginAsHome = true
    }

    private fun startMigration(context: Context, settings: LLSettings) {
        if (!shouldDoMigration) { return }

        // Cookies migration
            // Initialize the web storage feature and migration the cookies while the extension is connected.
            if (webStorageFeature == null) {
                val runtime = context.components.core.geckoRuntime
                val webStorage = LLWebStorageFeature.install(runtime) {
                    logger.info("WSFeature: Port connected")
                    migrateCookies(context)
                }
                webStorageFeature = webStorage
            }

        // User key migration
            // Check if it's the first run of the app and if so, check if there is a user key
            // for migration.
            settings.checkForLegacyUserKey()?.let { userKey ->
                logger.info("LILO:DBG: User key from migration: $userKey")
                // If the user key exists from a previous app version then the app shall call
                // the Lilo's API to retrieve the user's favors

                // Bookmarks migration
                if (userKey.isNotEmpty()) {
                    // Retrieve the user's bookmarks from the Lilo backend.
                    fetchBookmarks(context, userKey)
                }
            } ?: run {
                logger.info("LILO:DBG: No user key from migration")
                // Nothing to do!
            }

        // Local storage migration
            (context as? FenixApplication)?.let { app ->
                readLocalStorage(app)
            }

        // Tabs migration
            val migrationManager = MigrationManager(context)
            migrationManager.restoreTabs()
        }

    /**
     * Fetch bookmarks from the API and save them
     */
    private fun fetchBookmarks(context: Context, userToken: String) {
        val client = context.components.core.client
        val migrationManager = MigrationManager(context)
        migrationManager.fetchRemoteBookmarks(client, userToken) { bookmarks ->
            logger.debug("Bookmarks fetched successfully")
            val bookmarkSaver = BookmarkSaver(context)
            bookmarkSaver.savingBookmarks(bookmarks)
        }
    }

    /**
     * Read the local storage of the Android Webview.
     * This function has to be called from an activity context due to the Webview being
     * added to the layout of the activity.
     */
    private fun readLocalStorage(application: FenixApplication) {
        Handler(Looper.getMainLooper()).post {
            val intent = Intent(application, LocalStorageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            application.startActivity(intent)
        }
    }

    private fun migrateCookies(context: Context) {
        val migrationManager = MigrationManager(context)
        logger.info("LILO:DBG: Look for cookies...")
        migrationManager.migrateAllCookies(webStorageFeature)
    }

    private fun observePageLoadForLocalStorage(fragment: BrowserFragment, sessionId: String?) {

        logger.info("LocalStorage: Observing page load...")
        if (shouldDoMigration && !localStorageSaved) {

            // If the current url doesn't match with the target url then do nothing
            val currentUrl = fragment.requireContext().components.core.store.state.selectedTab?.content?.url
            logger.debug("currentUrl: $currentUrl")
            if (!isTargetUrl(currentUrl, LLAppConstants.homeHost)) {
                return
            }

            // Observe the progress loading to be done
            val store = fragment.requireContext().components.core.store
            store.flowScoped(fragment.viewLifecycleOwner) { flow ->
                flow.mapNotNull { state -> state.findTabOrCustomTabOrSelectedTab(sessionId) }
                    .distinctUntilChangedBy { tab -> tab.content.progress }
                    .takeWhile { tab ->
                        val condition = tab.content.progress == 100 && isTargetUrl(tab.content.url, LLAppConstants.homeHost)
                        if (condition) {
                            logger.debug("Page finished loading: ${tab.content.url}")

                            // When the page is fully loaded, migrate the local storage items if they exist.
                            getLocalStorageRecords()?.let { records ->
                                MigrationManager(fragment.requireContext()).migrateLocalStorageItems(
                                    webStorageFeature,
                                    records
                                ) {
                                    localStorageSaved = true // Only save once
                                    cleanLocalStorageRecords()
                                }
                            }
                        }

                        // When the condition is met, return false to stop observing
                        !condition
                    }
                    .collect { tab ->
                        logger.debug("progress: ${tab.content.progress} - ${tab.content.url}")
                    }
            }

        }
    }

    fun getLocalStorageRecords(): Map<String, String>? {
        return if (MigrationDataStore.localStorageRecords.isNullOrEmpty()) null else MigrationDataStore.localStorageRecords
    }

    fun cleanLocalStorageRecords() {
        MigrationDataStore.localStorageRecords = null
    }

    private fun isTargetUrl(url: String?, targetUrl: String): Boolean {
        return url?.contains(targetUrl) == true
    }

    private fun getEngineSessionForTab(store: BrowserStore, tab: SessionState): EngineSession? {
        return store.state.findTab(tab.id)?.engineState?.engineSession
    }

    private fun configureSearchMenu(context: Context) {
        val prefs = context.settings().preferences
        prefs.edit {
            // Search menu
            putBoolean(context.getPreferenceKey(R.string.pref_key_search_browsing_history), false)
            putBoolean(context.getPreferenceKey(R.string.pref_key_search_bookmarks), false)
            putBoolean(context.getPreferenceKey(R.string.pref_key_search_synced_tabs), false)
            putBoolean(context.getPreferenceKey(R.string.pref_key_show_sponsored_suggestions), false)
            putBoolean(context.getPreferenceKey(R.string.pref_key_show_voice_search), false)

            // Privacy and security menu
            putBoolean(context.getPreferenceKey(R.string.pref_key_tracking_protection), false)
            putBoolean(context.getPreferenceKey(R.string.pref_key_delete_browsing_data_on_quit), false)
            putBoolean(context.getPreferenceKey(R.string.pref_key_cookie_banner_private_mode), false)
        }

    }

    private fun setGeckoLocalStorageItems(items: Map<String, String>, store: BrowserStore, tab: SessionState) {
        getEngineSessionForTab(store, tab)?.let { session ->
            val localStorage = LocalStorageHelper(logger)
            localStorage.setGeckoLocalStorageItems(session, items)
        }
    }

    private fun configureObservers(context: Context) {
        (context as? FenixApplication)?.let { app ->
            context.registerActivityLifecycleCallbacks(LLActivityObserver())
        }
    }

    object LLSupportUtils {
        enum class URLType {
            PRIVACY_NOTICE,
            TERMS_OF_SERVICE,
            SUPPORT,
            GOOGLE_URL,
            FAQ,
            ABOUT;
        }

        fun getLiloUrl(urlType: URLType) : String {
            return when (urlType) {
                URLType.PRIVACY_NOTICE -> LLAppConstants.AppURL.PRIVACY_POLICY.url()?.toString()?:LLAppConstants.homeUrl
                URLType.TERMS_OF_SERVICE -> LLAppConstants.AppURL.TERMS_OF_SERVICE.url()?.toString()?:LLAppConstants.homeUrl
                URLType.SUPPORT -> LLAppConstants.supportURL
                URLType.GOOGLE_URL -> LLAppConstants.GOOGLE_URL
                URLType.FAQ -> LLAppConstants.FAQ_URL
                URLType.ABOUT -> LLAppConstants.AppURL.ABOUT.url()?.toString()?:LLAppConstants.homeUrl

            }
        }

        fun getLiloUrl(page: SupportUtils.MozillaPage) : String {
            return when (page) {
                SupportUtils.MozillaPage.PRIVATE_NOTICE -> getLiloUrl(URLType.PRIVACY_NOTICE)
                SupportUtils.MozillaPage.MANIFESTO -> getLiloUrl(URLType.ABOUT)
                SupportUtils.MozillaPage.TERMS_OF_SERVICE -> getLiloUrl(URLType.TERMS_OF_SERVICE)
            }
        }
    }
}
