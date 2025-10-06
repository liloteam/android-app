package org.mozilla.fenix.lilomodule.translations.preferences.downloadlanguages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.mozilla.fenix.R
import org.mozilla.fenix.compose.LinkText
import org.mozilla.fenix.compose.LinkTextState
import org.mozilla.fenix.theme.FirefoxTheme

/**
 * Displays a header for the download languages preference.
 * The learn more link is removed for Lilo.
 */
@Composable
fun LLDownloadLanguagesHeaderPreference(
    learnMoreUrl: String,
    onLearnMoreClicked: () -> Unit,
) {
    val learnMoreText =
        stringResource(id = R.string.download_languages_header_learn_more_preference)
    val learnMoreState = LinkTextState(
        text = learnMoreText,
        url = learnMoreUrl,
        onClick = { onLearnMoreClicked() },
    )
    Box(
        modifier = Modifier
            .padding(
                start = 72.dp,
                top = 6.dp,
                bottom = 6.dp,
                end = 16.dp,
            )
            .semantics { heading() },
    ) {
        LinkText(
            text = stringResource(
                R.string.download_languages_header_preference,
                ""
            ),
            linkTextStates = listOf(),
            style = FirefoxTheme.typography.subtitle1.copy(
                color = FirefoxTheme.colors.textPrimary,
            ),
            linkTextDecoration = TextDecoration.Underline,
        )
    }

    Spacer(modifier = Modifier.size(8.dp))
}
