package org.mozilla.fenix

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import org.mozilla.fenix.liloapp.core.R

/**
 * Lilo color palette
 */
data class LiloColors(
    val darkBlue: Color,
    val blue: Color,
    val lightBlue: Color,
    val green: Color,
    val darkGreen: Color,
    val gray: Color,
)

/**
 * Local provider for Lilo colors
 */
val LocalLiloColors = staticCompositionLocalOf {
    LiloColors(
        darkBlue = Color.Unspecified,
        blue = Color.Unspecified,
        lightBlue = Color.Unspecified,
        green = Color.Unspecified,
        darkGreen = Color.Unspecified,
        gray = Color.Unspecified,
    )
}

/**
 * Lilo Light Color Scheme
 */
@Composable
private fun liloLightColorScheme() = lightColorScheme(
    primary = colorResource(R.color.lilo_blue),
    onPrimary = colorResource(R.color.lilo_gray),
    primaryContainer = colorResource(R.color.lilo_light_blue),
    onPrimaryContainer = colorResource(R.color.lilo_dark_blue),
    
    secondary = colorResource(R.color.lilo_green),
    onSecondary = colorResource(R.color.lilo_gray),
    secondaryContainer = colorResource(R.color.lilo_dark_green),
    onSecondaryContainer = Color.White,
    
    background = Color.White,
    onBackground = colorResource(R.color.lilo_dark_blue),
    surface = Color.White,
    onSurface = colorResource(R.color.lilo_dark_blue),
    surfaceVariant = colorResource(R.color.lilo_gray),
    onSurfaceVariant = colorResource(R.color.lilo_dark_blue),
)

/**
 * Lilo Dark Color Scheme
 */
@Composable
private fun liloDarkColorScheme() = darkColorScheme(
    primary = colorResource(R.color.lilo_light_blue),
    onPrimary = colorResource(R.color.lilo_dark_blue),
    primaryContainer = colorResource(R.color.lilo_blue),
    onPrimaryContainer = Color.White,
    
    secondary = colorResource(R.color.lilo_green),
    onSecondary = colorResource(R.color.lilo_dark_blue),
    secondaryContainer = colorResource(R.color.lilo_dark_green),
    onSecondaryContainer = Color.White,
    
    background = colorResource(R.color.lilo_dark_blue),
    onBackground = Color.White,
    surface = colorResource(R.color.lilo_dark_blue),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1A1A2E),
    onSurfaceVariant = colorResource(R.color.lilo_gray),
)

/**
 * Lilo color palette instance
 */
@Composable
private fun liloColors() = LiloColors(
    darkBlue = colorResource(R.color.lilo_dark_blue),
    blue = colorResource(R.color.lilo_blue),
    lightBlue = colorResource(R.color.lilo_light_blue),
    green = colorResource(R.color.lilo_green),
    darkGreen = colorResource(R.color.lilo_dark_green),
    gray = colorResource(R.color.lilo_gray),
)

/**
 * Lilo Theme
 * 
 * Apply this theme to the composable hierarchy to use Lilo's design system.
 * 
 * Example usage:
 * ```
 * LiloTheme {
 *     // Your content here
 *     MyScreen()
 * }
 * ```
 * 
 * Access colors:
 * ```
 * // Material Design colors
 * MaterialTheme.colorScheme.primary
 * 
 * // Custom Lilo colors
 * LiloTheme.colors.darkBlue
 * ```
 */
@Composable
fun LiloTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        liloDarkColorScheme()
    } else {
        liloLightColorScheme()
    }

    val liloColors = liloColors()

    CompositionLocalProvider(LocalLiloColors provides liloColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}

/**
 * Access to Lilo theme values
 */
object LiloTheme {
    /**
     * Returns the current [LiloColors]
     */
    val colors: LiloColors
        @Composable
        get() = LocalLiloColors.current
}

