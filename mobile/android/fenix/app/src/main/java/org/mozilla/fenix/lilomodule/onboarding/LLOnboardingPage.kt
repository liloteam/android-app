package org.mozilla.fenix.lilomodule.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import mozilla.components.compose.base.button.PrimaryButton
import mozilla.components.compose.base.button.SecondaryButton
import org.mozilla.fenix.LLOnboardingAssets
import org.mozilla.fenix.R
import org.mozilla.fenix.LLSvgFromAssets
import org.mozilla.fenix.LiloTheme
import org.mozilla.fenix.compose.LinkText
import org.mozilla.fenix.onboarding.view.OnboardingPageState
import org.mozilla.fenix.onboarding.view.OnboardingPageUiData
import org.mozilla.fenix.onboarding.view.OnboardingTermsOfServiceEventHandler
import org.mozilla.fenix.theme.FirefoxTheme

@Composable
@Suppress("LongMethod")
fun LLOnboardingPage(
    type: OnboardingPageUiData.Type,
    termsOfServiceEventHandler: OnboardingTermsOfServiceEventHandler,
    pageState: OnboardingPageState,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val onboardingAssets = LLOnboardingAssets(context)

    val pageType = when (type) {
        OnboardingPageUiData.Type.TERMS_OF_SERVICE -> LLOnboardingAssets.PageType.WELCOME
        OnboardingPageUiData.Type.SYNC_SIGN_IN -> LLOnboardingAssets.PageType.LOGIN
        else -> null
    }

    LiloTheme {
        BoxWithConstraints(
            modifier = Modifier
                .background(Color.White)
                .padding(bottom = if (pageState.secondaryButton == null) 32.dp else 24.dp)
                .then(modifier),
        ) {
            val boxWithConstraintsScope = this
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                if (onDismiss != null) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.mozac_ic_cross_24),
                            contentDescription = stringResource(R.string.onboarding_home_content_description_close_button),
                            tint = FirefoxTheme.colors.iconPrimary,
                        )
                    }
                } else {
                    Spacer(Modifier)
                }

                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    pageType?.let { type ->
                        onboardingAssets.getImageUri(type)?.also { uri ->
                            LLSvgFromAssets(
                                modifier = Modifier.fillMaxWidth(),
                                assetUri = uri,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {

                    if (pageType == LLOnboardingAssets.PageType.WELCOME) {
                        LLTermsOfServiceOnboardingPageBodyText(
                            pageState = pageState,
                            eventHandler = termsOfServiceEventHandler
                        )
                    }
                    else {
                        pageState.privacyCaption?.let { privacyCaption ->
                            LinkText(
                                text = privacyCaption.text,
                                linkTextStates = listOf(privacyCaption.linkTextState),
                            )
                        }
                    }

                    val primaryBackgroundColor = when(pageType) {
                        LLOnboardingAssets.PageType.WELCOME -> LiloTheme.colors.gray
                        else -> MaterialTheme.colorScheme.secondary
                    }
                    val primaryTextColor = when(pageType) {
                        LLOnboardingAssets.PageType.WELCOME -> LiloTheme.colors.green
                        else -> MaterialTheme.colorScheme.onSecondary
                    }
                    LLOnboardingPrimaryButton(pageState, primaryBackgroundColor, primaryTextColor)

                    if (pageState.secondaryButton != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LLOnboardingSecondaryButton(pageState, pageState.secondaryButton)
                    }
                }

                LaunchedEffect(pageState) {
                    pageState.onRecordImpressionEvent()
                }
            }
        }
    }
}
