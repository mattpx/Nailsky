package com.nailsky.app.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

private val LightColors = lightColorScheme(
    primary = SkyBlue,
    onPrimary = DeepNavy,
    primaryContainer = SkyContainer,
    onPrimaryContainer = DeepNavy,
    secondary = BlushPink,
    onSecondary = DeepNavy,
    secondaryContainer = BlushContainer,
    onSecondaryContainer = DeepNavy,
    tertiary = HeartPink,
    onTertiary = CloudWhite,
    background = SkyMist,
    onBackground = DeepNavy,
    surface = PearlSurface,
    onSurface = DeepNavy,
    surfaceVariant = Color(0xFFF0EEF5),
    onSurfaceVariant = SlateText,
    surfaceTint = SkyBlue,
    outline = SoftOutline,
    outlineVariant = Color(0xFFE7E4EE),
    surfaceContainerLowest = CloudWhite,
    surfaceContainerLow = Color(0xFFFCFAFD),
    surfaceContainer = PearlSurface,
    surfaceContainerHigh = Color(0xFFF3F1F8),
    surfaceContainerHighest = Color(0xFFECE9F3),
)

private val DarkColors = darkColorScheme(
    primary = NightPrimary,
    onPrimary = Color(0xFF0A2438),
    primaryContainer = Color(0xFF1E3E5C),
    onPrimaryContainer = Color(0xFFD6EEFF),
    secondary = NightSecondary,
    onSecondary = Color(0xFF47152D),
    secondaryContainer = Color(0xFF53243C),
    onSecondaryContainer = Color(0xFFFFD9E6),
    tertiary = NightSecondary,
    onTertiary = Color(0xFF47152D),
    background = NightBackground,
    onBackground = NightOnSurface,
    surface = NightSurface,
    onSurface = NightOnSurface,
    surfaceVariant = NightSurfaceHigh,
    onSurfaceVariant = NightOnSurfaceVariant,
    surfaceTint = NightPrimary,
    outline = NightOutline,
    outlineVariant = Color(0xFF2A3450),
    surfaceContainerLowest = NightBackground,
    surfaceContainerLow = Color(0xFF121726),
    surfaceContainer = NightSurface,
    surfaceContainerHigh = NightSurfaceHigh,
    surfaceContainerHighest = NightSurfaceHighest,
)

/** True when NailSky is rendering in dark mode (lets placeholder gradients use deeper tones). */
val LocalNailSkyDarkTheme = staticCompositionLocalOf { false }

@Composable
fun NailSkyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalNailSkyDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = NailSkyTypography,
            shapes = NailSkyShapes,
            content = content,
        )
    }
}

/** Renders a preview in both light and dark mode. */
@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFF8F6FA)
@Preview(
    name = "Dark",
    showBackground = true,
    backgroundColor = 0xFF0C101B,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
annotation class ThemePreviews
