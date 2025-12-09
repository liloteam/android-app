package org.mozilla.fenix

import android.content.Context
import android.graphics.drawable.Drawable
import org.mozilla.fenix.liloapp.core.R
import java.util.Locale

/**
 * Represents the type of update onboarding page.
 */
enum class UpdateOnboardingPageType {
    CONFIDENTIALITY,
    NAVIGATION;


    fun getState(context: Context): UpdateOnboardingPageState {
        val onboardingAssets = LLOnboardingAssets(context)
        return when (this) {
            CONFIDENTIALITY -> UpdateOnboardingPageState(
                imageUri = onboardingAssets.getImageUri(LLOnboardingAssets.PageType.CONFIDENTIALITY, Locale.ENGLISH),//AppCompatResources.getDrawable(context,R.drawable.onboarding_header_confidentiality),
                title = context.getString(R.string.onboardingConfidentialityTitle),
                body = context.getString(R.string.onboardingConfidentialityBody),
                primaryButtonTitle = context.getString(R.string.onboardingConfidentialityPrimaryButtonTitle),
                secondaryButtonTitle = context.getString(R.string.onboardingConfidentialitySecondaryButtonTitle),
            )
            NAVIGATION -> UpdateOnboardingPageState(
                imageUri = onboardingAssets.getImageUri(LLOnboardingAssets.PageType.NAVIGATION, Locale.ENGLISH),//AppCompatResources.getDrawable(context,R.drawable.onboarding_header_navigation),
                title = context.getString(R.string.onboardingNavigationTitle),
                body = context.getString(R.string.onboardingNavigationBody),
                primaryButtonTitle = context.getString(R.string.onboardingNavigationPrimaryButtonTitle),
                secondaryButtonTitle = null,
            )
        }
    }

}

/**
 * Data class containing resources for an update onboarding page type.
 */
data class UpdateOnboardingPageState(
    val imageUri: String?,
    val title: String,
    val body: String,
    val primaryButtonTitle: String,
    val secondaryButtonTitle: String? = null,
)
