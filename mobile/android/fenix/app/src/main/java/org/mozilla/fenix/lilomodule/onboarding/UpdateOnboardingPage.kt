package org.mozilla.fenix.lilomodule.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.mozilla.fenix.UpdateOnboardingPageType
import org.mozilla.fenix.LLSvgFromAssets
import org.mozilla.fenix.LiloTheme
import org.mozilla.fenix.onboarding.view.OnboardingPageState
import org.mozilla.fenix.theme.FirefoxTheme

/**
 * Parses custom placeholders (like {bold}text{/bold}) from a string
 * and converts them to an AnnotatedString with bold formatting.
 * Since HTML tags are stripped by getString(), we use custom placeholders instead.
 */
private fun parseBoldPlaceholders(
    text: String,
    defaultColor: Color
): AnnotatedString {
    // Check if the string contains bold placeholders
    val hasBoldPlaceholders = text.contains("{bold}") && text.contains("{/bold}")
    
    if (!hasBoldPlaceholders) {
        // No bold placeholders found, return plain text
        return AnnotatedString(text)
    }
    
    // Use regex to find all {bold}...{/bold} placeholders
    val boldPattern = Regex("\\{bold\\}(.*?)\\{/bold\\}", RegexOption.DOT_MATCHES_ALL)
    
    return buildAnnotatedString {
        var lastIndex = 0
        
        // Find all matches
        boldPattern.findAll(text).forEach { matchResult ->
            // Append text before the match
            if (matchResult.range.first > lastIndex) {
                append(text.substring(lastIndex, matchResult.range.first))
            }
            
            // Append bold text (group 1 contains the text between placeholders)
            val boldText = matchResult.groupValues[1]
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.ExtraBold,
                    color = defaultColor
                )
            ) {
                append(boldText)
            }
            
            lastIndex = matchResult.range.last + 1
        }
        
        // Append remaining text after last match
        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }
}

/**
 * Composable for displaying an update onboarding page.
 * The layout consists of:
 * - Image (first third of the screen)
 * - Title (below the image)
 * - Body text (below the title)
 * - Navigation buttons (at the bottom)
 */
@Composable
fun UpdateOnboardingPage(
    modifier: Modifier = Modifier,
    pageType: UpdateOnboardingPageType,
    pageState: OnboardingPageState,
    pageImageUri: String?
) {
    LiloTheme {
        BoxWithConstraints(
            modifier = Modifier.Companion
                .background(Color.White)
                .padding(bottom = if (pageState.secondaryButton == null) 32.dp else 24.dp)
                .then(modifier),
        ) {
            val boxWithConstraintsScope = this

            Column(
                modifier = Modifier.Companion
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Companion.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                // Image section (first third)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Companion.Center,
                ) {
                    pageImageUri?.also { uri ->
                        LLSvgFromAssets(
                            modifier = Modifier.fillMaxWidth(),
                            assetUri = uri,
                        )
                    }
                }

                // Content section (title and body)
                Column(
                    modifier = Modifier.Companion.padding(horizontal = 32.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = pageState.title,
                        color = LiloTheme.colors.blue,
                        textAlign = TextAlign.Left,
                        style = FirefoxTheme.typography.headline6,
                        modifier = Modifier.fillMaxWidth(0.7f)
                    )

                    Spacer(modifier = Modifier.Companion.height(16.dp))

                    // Parse custom placeholders (like {bold}text{/bold}) from string and convert to AnnotatedString
                    val annotatedText = parseBoldPlaceholders(
                        text = pageState.description,
                        defaultColor = FirefoxTheme.colors.textSecondary
                    )

                    Text(
                        text = annotatedText,
                        textAlign = TextAlign.Left,
                        style = FirefoxTheme.typography.body2,
                    )
                }

                // Navigation buttons
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .height(height = 130.dp)
                        .padding(horizontal = 32.dp),
                ) {

                    val primaryBackgroundColor = Color.White
                    val primaryTextColor = LiloTheme.colors.green
                    LLOnboardingPrimaryButton(
                        pageState,
                        primaryBackgroundColor,
                        primaryTextColor,
                        buttonBorderColor = LiloTheme.colors.gray
                    )

                    if (pageState.secondaryButton != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LLOnboardingSecondaryButton(pageState, pageState.secondaryButton, Color.Transparent, Color.Black)
                    }

                }
            }
        }
    }
}
