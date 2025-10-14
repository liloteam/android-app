package org.mozilla.fenix

import android.content.Context
import mozilla.components.support.base.log.logger.Logger
import org.mozilla.fenix.liloapp.core.R

data class LLOnboardingAssets(
    val context: Context,
) {
    val logger = Logger("LILO:ONB")

    enum class PageType {
        WELCOME,
        THEME,
        LOGIN;

        fun imageUri(): String? {
            val path = "file:///android_asset/images"
            val name =  when (this) {
                WELCOME -> LLAppConstants.OnboardingImage.WELCOME
                THEME -> null
                LOGIN -> LLAppConstants.OnboardingImage.ACCOUNT
            }
            return name?.let { "$path/$it" }
        }
    }

}
