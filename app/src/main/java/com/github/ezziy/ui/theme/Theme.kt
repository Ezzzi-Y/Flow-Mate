package com.github.ezziy.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AppleBlue,
    onPrimary = AppleSurface,
    secondary = AppleSecondary,
    onSecondary = AppleTextPrimary,
    background = AppleBackground,
    onBackground = AppleTextPrimary,
    surface = AppleSurface,
    onSurface = AppleTextPrimary,
    surfaceVariant = AppleSurfaceDim,
    onSurfaceVariant = AppleTextSecondary,
    outline = AppleOutline,
)

@Composable
fun FlawMateTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
