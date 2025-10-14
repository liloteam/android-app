package org.mozilla.fenix.lilomodule.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.mozilla.fenix.compose.LinkText
import org.mozilla.fenix.compose.LinkTextState
import org.mozilla.fenix.onboarding.view.OnboardingPageState
import org.mozilla.fenix.onboarding.view.OnboardingTermsOfServiceEventHandler
import org.mozilla.fenix.theme.FirefoxTheme

@Composable
fun LLTermsOfServiceOnboardingPageBodyText(
    pageState: OnboardingPageState,
    eventHandler: OnboardingTermsOfServiceEventHandler,
) {
    pageState.termsOfService?.let {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 10.dp),
        ) {
            val lineTwoState = LinkTextState(
                text = it.lineTwoLinkText,
                url = it.lineTwoLinkUrl,
                onClick = eventHandler::onPrivacyNoticeLinkClicked,
            )

            LinkText(
                text = it.lineTwoText.updateFirstPlaceholder(it.lineTwoLinkText),
                linkTextStates = listOf(
                    lineTwoState,
                ),
                style = FirefoxTheme.typography.caption.copy(
                    textAlign = TextAlign.Center,
                    color = FirefoxTheme.colors.textSecondary,
                ),
                shouldApplyAccessibleSize = true,
            )

        }
    }
}

private fun String.updateFirstPlaceholder(text: String) = replace("%1\$s", text)
