package org.mozilla.fenix

import android.content.Context
import mozilla.components.support.base.log.logger.Logger
import org.mozilla.fenix.liloapp.core.R
import java.io.IOException
import java.util.Locale

/**
 * Manages localized assets for the Lilo onboarding experience.
 * 
 * This class handles the selection of appropriate image resources based on the user's locale.
 * It automatically detects available localized asset directories and falls back to default
 * resources when a localized version is not available.
 * 
 * **Supported asset directory structure:**
 * - `assets/images/` - Default images (fallback)
 * - `assets/images-fr/` - French localized images
 *
 * @param context The Android context used to access assets
 * @param locale The locale to use for asset selection (defaults to system locale)
 */
data class LLOnboardingAssets(
    val context: Context,
    val locale: Locale = Locale.getDefault()
) {
    val logger = Logger("LILO:ONB")

    enum class PageType {
        WELCOME,
        THEME,
        LOGIN;

        /**
         * Returns the URI for the image associated with this page type.
         * Checks if a localized version exists in the assets directory.
         * Falls back to the default "images" directory if no localized version is found.
         */
        fun imageUri(context: Context, locale: Locale): String? {
            val imageName = when (this) {
                WELCOME -> LLAppConstants.OnboardingImage.WELCOME
                THEME -> null
                LOGIN -> LLAppConstants.OnboardingImage.ACCOUNT
            } ?: return null

            // Get the localized directory path
            val localizedPath = getLocalizedImagePath(context, locale)
            
            return "file:///android_asset/$localizedPath/$imageName"
        }

        /**
         * Determines the appropriate image directory based on locale support.
         * Returns "images-{lang}" if the localized directory exists, otherwise "images".
         */
        private fun getLocalizedImagePath(context: Context, locale: Locale): String {
            val language = locale.language
            val localizedDirectory = "images-$language"
            
            return if (isAssetDirectoryExists(context, localizedDirectory)) {
                localizedDirectory
            } else {
                "images" // Default fallback
            }
        }

        /**
         * Check if a directory exists in the assets folder.
         */
        private fun isAssetDirectoryExists(context: Context, path: String): Boolean {
            return try {
                val list = context.assets.list(path)
                list != null && list.isNotEmpty()
            } catch (e: IOException) {
                false
            }
        }
    }

    /**
     * Get the image URI for a specific page type using the current locale.
     */
    fun getImageUri(pageType: PageType): String? {
        return pageType.imageUri(context, locale)
    }

}
