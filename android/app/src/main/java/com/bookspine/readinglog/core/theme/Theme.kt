package com.bookspine.readinglog.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ForestScheme = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = BgDeep,
    primaryContainer = SurfaceAlt,
    onPrimaryContainer = PrimaryGreenSoft,
    secondary = SecondaryTeal,
    onSecondary = TextPrimary,
    secondaryContainer = SecondaryTeal,
    onSecondaryContainer = TextPrimary,
    tertiary = AccentRed,
    onTertiary = Color.White,
    background = BgBase,
    onBackground = TextPrimary,
    surface = SurfaceGreen,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceAlt,
    onSurfaceVariant = TextSecondary,
    outline = SecondaryTeal,
    outlineVariant = TextMuted,
    error = AccentRed,
    onError = Color.White
)

private val ParchmentScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    primaryContainer = ParchmentSurfaceAlt,
    onPrimaryContainer = ParchmentText,
    secondary = SecondaryTeal,
    onSecondary = Color.White,
    secondaryContainer = ParchmentSurfaceAlt,
    onSecondaryContainer = ParchmentText,
    tertiary = AccentRed,
    onTertiary = Color.White,
    background = ParchmentBg,
    onBackground = ParchmentText,
    surface = ParchmentSurface,
    onSurface = ParchmentText,
    surfaceVariant = ParchmentSurfaceAlt,
    onSurfaceVariant = ParchmentTextSecondary,
    outline = ParchmentTextMuted,
    outlineVariant = ParchmentTextMuted,
    error = AccentRed,
    onError = Color.White
)

@Composable
fun AppTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) ForestScheme else ParchmentScheme,
        typography = AppTypography,
        content = content
    )
}
