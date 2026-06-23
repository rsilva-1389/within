package com.within.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = WithinPrimary,
    onPrimary = WithinOnPrimary,
    secondary = WithinSecondary,
    onSecondary = Encouraging,
    tertiary = Warm,
    onTertiary = Encouraging,
    background = WithinBackground,
    onBackground = WithinOnBackground,
    surface = WithinSurface,
    onSurface = WithinOnSurface,
    surfaceVariant = Color(0xFF003060),
    onSurfaceVariant = Gentle,
)

@Composable
fun WithinTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = WithinTypography,
        content = content
    )
}
