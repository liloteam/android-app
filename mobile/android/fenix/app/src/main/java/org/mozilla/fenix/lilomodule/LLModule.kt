package org.mozilla.fenix.lilomodule

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.takeWhile
import mozilla.components.browser.state.action.EngineAction
import mozilla.components.browser.state.selector.findTab
import mozilla.components.browser.state.selector.findTabOrCustomTabOrSelectedTab
import mozilla.components.browser.state.selector.selectedTab
import mozilla.components.browser.state.state.SessionState
import mozilla.components.browser.state.state.TabSessionState
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
import org.mozilla.fenix.browser.BrowserFragment
import org.mozilla.fenix.components.menu.MenuDialogFragment
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.ext.settings
import org.mozilla.fenix.lilomodule.browser.initializeLiloUI
import org.mozilla.fenix.lilomodule.components.menu.goToLoginPage
import org.mozilla.fenix.lilomodule.search.LLSearchEngine
import org.mozilla.fenix.lilomodule.settings.LLSettings

object LLModule {
    private val logger = Logger("LILO:MODULE")
    private val shouldForceMigration = true

    private var localStorageSaved = false
    private var shouldDoMigration = false

    private var webStorageFeature: LLWebStorageFeature? = null


    fun initializeLilo(context: Context) {
        val settings = LLSettings(context)
        shouldDoMigration = settings.isFirstRun || shouldForceMigration

        // Customize the user agent string for Lilo.
        val engineSettings = context.components.core.engine.settings
        context.components.core.engine.settings.userAgentString =
            LLAppEngine.customizedUserAgent(context, engineSettings.userAgentString)

        // Enable Firebase Analytics according to the user's preference.
        val analytics = FirebaseAnalytics.getInstance(context)
        analytics.setAnalyticsCollectionEnabled(context.settings().isTelemetryEnabled)

        // Select the Lilo search engine if not already selected.
        LLSearchEngine(logger).setupLiloSearchEngine(context)

        // Force the offer to translate option to be disabled.
        settings.updateOfferTranslationOption(false)

        // Cookies migration
        if (shouldDoMigration) {
            // Initialize the web storage feature and migration the cookies while the extension is connected.
            if (webStorageFeature == null) {
                val runtime = context.components.core.geckoRuntime
                val webStorage = LLWebStorageFeature.install(runtime) {
                    logger.info("WSFeature: Port connected")
                    migrateCookies(context)
                }
                webStorageFeature = webStorage
            }
        }

        // User key migration
        if (shouldDoMigration) {
            // Check if it's the first run of the app and if so, check if there is a user key
            // for migration.
            settings.checkForLegacyUserKey()?.let {
                logger.info("LILO:DBG: User key from migration: $it")
                // If the user key exists from a previous app version then the app shall call
                // the API to retrieve the user's favors
                // TODO: Implement the API call to get the favors from the user key.

            } ?: run {
                logger.info("LILO:DBG: No user key from migration")
                // Nothing to do!
            }
        }

        // Local storage migration
        if (shouldDoMigration) {
            (context as? FenixApplication)?.let { app ->
                readLocalStorage(app)
            }
        }

        // Tabs migration
        if (shouldDoMigration) {
            val migrationManager = MigrationManager(context)
            migrationManager.restoreTabs()
        }

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

    private fun setGeckoLocalStorageItems(items: Map<String, String>, store: BrowserStore, tab: SessionState) {
        getEngineSessionForTab(store, tab)?.let { session ->
            val localStorage = LocalStorageHelper(logger)
            localStorage.setGeckoLocalStorageItems(session, items)
        }
    }
}
