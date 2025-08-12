package org.mozilla.fenix.lilo

import android.content.Context
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.ext.settings

class LiMain {
    companion object {
        fun initializeLilo(context: Context) {
            val engineSettings = context.components.core.engine.settings
            context.components.core.engine.settings.userAgentString = LiSettings.customizeUserAgent(context, engineSettings.userAgentString)

        }
    }
}
