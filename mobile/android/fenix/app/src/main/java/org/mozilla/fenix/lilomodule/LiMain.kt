package org.mozilla.fenix.lilomodule

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import org.mozilla.fenix.LiSettings
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.ext.settings

class LiMain {
    companion object {
        fun initializeLilo(context: Context) {
            // Customize the user agent string for Lilo.
            val engineSettings = context.components.core.engine.settings
            context.components.core.engine.settings.userAgentString = LiSettings.customizedUserAgent(context, engineSettings.userAgentString)

            // Enable Firebase Analytics according to the user's preference.
            val analytics = FirebaseAnalytics.getInstance(context)
            analytics.setAnalyticsCollectionEnabled(context.settings().isTelemetryEnabled)
        }
    }
}
