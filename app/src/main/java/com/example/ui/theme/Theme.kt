package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EnpantallaColorScheme = lightColorScheme(
    primary = GoldPrimary,
    onPrimary = TextOnGold,
    primaryContainer = GoldLight,
    onPrimaryContainer = GoldDark,
    secondary = GoldDark,
    onSecondary = PureWhite,
    secondaryContainer = GoldLight,
    onSecondaryContainer = TextPrimary,
    tertiary = TextPrimary,
    onTertiary = PureWhite,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BorderLight,
    outlineVariant = BorderSubtle
)

@Composable
fun EnpantallaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EnpantallaColorScheme,
        typography = Typography,
        content = content
    )
}
