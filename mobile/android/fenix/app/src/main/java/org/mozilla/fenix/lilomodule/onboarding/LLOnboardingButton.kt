package org.mozilla.fenix.lilomodule.onboarding

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import mozilla.components.compose.base.button.PrimaryButton
import mozilla.components.compose.base.button.SecondaryButton
import org.mozilla.fenix.LiloTheme
import org.mozilla.fenix.onboarding.view.Action
import org.mozilla.fenix.onboarding.view.OnboardingPageState
import org.mozilla.fenix.theme.FirefoxTheme

@Composable
fun LLOnboardingPrimaryButton(
    pageState: OnboardingPageState,
    primaryBackgroundColor: Color? = null,
    primaryTextColor: Color? = null
) {
    LiloTheme {
        val backgroundColor = primaryBackgroundColor?: MaterialTheme.colorScheme.secondary
        val textColor = primaryTextColor?: MaterialTheme.colorScheme.onSecondary
        PrimaryButton(
            modifier = Modifier
                .width(width = FirefoxTheme.layout.size.maxWidth.small)
                .height(height = 50.dp)
                .semantics {
                    testTag = pageState.title + "onboarding_card.positive_button"
                },
            backgroundColor = backgroundColor,
            textColor = textColor,
            text = pageState.primaryButton.text,
            onClick = pageState.primaryButton.onClick,
        )
    }
}

@Composable
fun LLOnboardingSecondaryButton(
    pageState: OnboardingPageState,
    secondaryButton: Action,
    primaryBackgroundColor: Color? = null,
    primaryTextColor: Color? = null
) {
    LiloTheme {
        val backgroundColor = primaryBackgroundColor?: MaterialTheme.colorScheme.onSecondary
        val textColor = primaryTextColor?: MaterialTheme.colorScheme.secondary
        SecondaryButton(
            modifier = Modifier
                .width(width = FirefoxTheme.layout.size.maxWidth.small)
                .height(height = 50.dp)
                .semantics {
                    testTag = pageState.title + "onboarding_card.negative_button"
                },
            backgroundColor = backgroundColor,
            textColor = textColor,
            text = secondaryButton.text,
            onClick = secondaryButton.onClick,
        )
    }
}
