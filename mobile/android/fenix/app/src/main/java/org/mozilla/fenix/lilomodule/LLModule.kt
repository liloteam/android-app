package org.mozilla.fenix.lilomodule

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import org.mozilla.fenix.LLAppEngine
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.ext.settings
import org.mozilla.fenix.lilomodule.settings.LLSettings

object LLModule {

    fun initializeLilo(context: Context) {
        // Customize the user agent string for Lilo.
        val engineSettings = context.components.core.engine.settings
        context.components.core.engine.settings.userAgentString =
            LLAppEngine.customizedUserAgent(context, engineSettings.userAgentString)

        // Enable Firebase Analytics according to the user's preference.
        val analytics = FirebaseAnalytics.getInstance(context)
        analytics.setAnalyticsCollectionEnabled(context.settings().isTelemetryEnabled)

        // Force the offer to translate option to be disabled.
        val settings = LLSettings(context)
        settings.updateOfferTranslationOption(false)
    }

    fun onDismissSearchDialog(dismiss: (() -> Unit)?) {
        dismiss?.invoke()
    }

    fun setShouldAddHomeTab(shouldAddHomeTab: Boolean) {
        LLAppEngine.shouldAddHomeTab = shouldAddHomeTab
    }

}
