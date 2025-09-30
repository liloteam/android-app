package org.mozilla.fenix.lilomodule

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import mozilla.components.support.base.log.logger.Logger
import mozilla.liloapp.migration.MigrationManager
import mozilla.liloapp.migration.LLWebStorageFeature
import mozilla.liloapp.migration.localstorage.LocalStorageHelper
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
    val logger = Logger("LILO:LOG")

    var webStorageFeature: LLWebStorageFeature? = null

    fun initializeLilo(context: Context) {
        // Customize the user agent string for Lilo.
        val engineSettings = context.components.core.engine.settings
        context.components.core.engine.settings.userAgentString =
            LLAppEngine.customizedUserAgent(context, engineSettings.userAgentString)

        if (webStorageFeature == null) {
            val runtime = context.components.core.geckoRuntime
            val webStorage = LLWebStorageFeature.install(runtime) {
                logger.info("WSFeature: Port connected")
                val migrationManager = MigrationManager(context)
                logger.info("LILO:DBG: Look for cookies...")
                migrationManager.migrateAllCookies(webStorageFeature)
            }
            webStorageFeature = webStorage
        }

        // Enable Firebase Analytics according to the user's preference.
        val analytics = FirebaseAnalytics.getInstance(context)
        analytics.setAnalyticsCollectionEnabled(context.settings().isTelemetryEnabled)

        // Select the Lilo search engine if not already selected.
        LLSearchEngine(logger).setupLiloSearchEngine(context)

        val settings = LLSettings(context)
        // Force the offer to translate option to be disabled.
        settings.updateOfferTranslationOption(false)

        // Check if it's the first run of the app and if so, check if there is a user key
        // for migration.
        settings.checkForUserKeyIfFirstRun()?.let {
            logger.info("LILO:DBG: User key from migration: $it")
            // If the user key exists from a previous app version then the app shall call
            // the API to retrieve the user's favors
            // TODO: Implement the API call to get the favors from the user key.

        }?:run {
            logger.info("LILO:DBG: No user key from migration")
            // Nothing to do!
        }

        logger.info("LILO:DBG: Lilo initializing - restoring tabs if any")
        val migrationManager = MigrationManager(context)
        migrationManager.restoreTabs()

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

    fun initializeLiloBrowser(fragment: BrowserFragment) {
        fragment.initializeLiloUI()
    }

    /**
     * Read the local storage of the Android Webview.
     * This function has to be called from an activity context due to the Webview being
     * added to the layout of the activity.
     */
    fun readLocalStorage(activity: Activity) {
        val rootLayout = FrameLayout(activity)
        activity.setContentView(rootLayout)

        val storage = LocalStorageHelper(logger, activity.applicationContext)
        val webView = storage.syncWebView()

        rootLayout.addView(webView, ViewGroup.LayoutParams(0, 0))
        Handler(Looper.getMainLooper()).postDelayed({
            storage.getAll { result ->
                logger.debug("Local storage: $result")
            }
        }, 2000)
    }
}
