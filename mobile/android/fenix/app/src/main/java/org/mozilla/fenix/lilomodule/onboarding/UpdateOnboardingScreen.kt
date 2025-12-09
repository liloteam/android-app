package org.mozilla.fenix.lilomodule.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.mozilla.fenix.UpdateOnboardingPageType
import org.mozilla.fenix.compose.PagerIndicator
import org.mozilla.fenix.onboarding.view.Action
import org.mozilla.fenix.onboarding.view.OnboardingPageState

/**
 * Screen displaying the update onboarding flow with 2 pages.
 *
 * @param pages List of update onboarding pages to display.
 * @param onFinish Invoked when the onboarding is completed.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UpdateOnboardingScreen(
    pages: List<UpdateOnboardingPageType>,
    onFinish: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { pages.size })

    BackHandler(enabled = pagerState.currentPage > 0) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage - 1)
        }
    }

    val scrollToNextPageOrFinish: () -> Unit = {
        if (pagerState.currentPage >= pages.lastIndex) {
            onFinish()
        } else {
            coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        }
    }

    Column(
        modifier = Modifier.Companion
            .fillMaxSize()
            .background(Color.White)
            // No status bar padding because the dialog hides the status bar to let the image reach the top.
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            val pageType = pages[page]
            val updatePageState = pageType.getState(context)

            val pageState = OnboardingPageState(
                imageRes = 0,
                title = updatePageState.title,
                description = updatePageState.body,
                privacyCaption = null,
                primaryButton = Action(
                    text = updatePageState.primaryButtonTitle,
                    onClick = {
                        if (page == pages.lastIndex) {
                            // Last page - finish onboarding
                            onFinish()
                        } else {
                            // Go to next page
                            scrollToNextPageOrFinish()
                        }
                    },
                ),
                secondaryButton = updatePageState.secondaryButtonTitle?.let {
                    Action(
                        text = it,
                        onClick = {
                            // Secondary button clicked - finish onboarding
                            onFinish()
                        },
                    )
                },
            )

            UpdateOnboardingPage(
                pageType = pageType,
                pageState = pageState,
                pageImageUri = updatePageState.imageUri,
            )
        }

        // Page indicator
        PagerIndicator(
            pagerState = pagerState,
            modifier = Modifier.Companion.padding(vertical = 16.dp),
        )
    }
}
