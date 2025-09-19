package org.mozilla.fenix.lilomodule

import android.content.Context
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import mozilla.components.support.base.log.logger.Logger
import org.mozilla.fenix.LLAppEngine
import org.mozilla.fenix.LLLegacySettingsSharedPreferences
import org.mozilla.fenix.components.menu.MenuDialogFragment
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.ext.settings
import org.mozilla.fenix.lilomodule.components.menu.goToLoginPage
import org.mozilla.fenix.lilomodule.settings.LLSettings

object LLModule {

    val logger = Logger("LLModule")

    fun initializeLilo(context: Context) {
        // Customize the user agent string for Lilo.
        val engineSettings = context.components.core.engine.settings
        context.components.core.engine.settings.userAgentString =
            LLAppEngine.customizedUserAgent(context, engineSettings.userAgentString)

        // Enable Firebase Analytics according to the user's preference.
        val analytics = FirebaseAnalytics.getInstance(context)
        analytics.setAnalyticsCollectionEnabled(context.settings().isTelemetryEnabled)


        val settings = LLSettings(context)
        // Force the offer to translate option to be disabled.
        settings.updateOfferTranslationOption(false)

        // Check if it's the first run of the app and if so, check if there is a user key
        // for migration.
        settings.checkForUserKeyIfFirstRun()?.let {
            logger.debug("LILO:DBG: User key from migration: $it")
            // If the user key exists from a previous app version then the app should call
            // both:
            // - The API to retrieve the user's favors
            // - a special Lilo's search web page which would rewrite the required cookies.
            // TODO This last point has to be confirmed!

        }?:run {
            logger.debug("LILO:DBG: No user key from migration")
            // Nothing to do!
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

}
