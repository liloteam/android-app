package org.mozilla.fenix.lilomodule

import android.content.Context
import org.mozilla.fenix.LiSettings
import org.mozilla.fenix.ext.components

class LiMain {
    companion object {
        fun initializeLilo(context: Context) {
            val engineSettings = context.components.core.engine.settings
            context.components.core.engine.settings.userAgentString = LiSettings.customizedUserAgent(context, engineSettings.userAgentString)
        }
    }
}
