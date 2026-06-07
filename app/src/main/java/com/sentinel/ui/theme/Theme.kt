package com.sentinel.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Cyber-security inspired palette — dark mode default. */
object SentinelColors {
    val CyberCyan = Color(0xFF00E5FF)
    val CyberGreen = Color(0xFF00E676)
    val CyberRed = Color(0xFFFF5252)
    val CyberOrange = Color(0xFFFFAB40)
    val CyberPurple = Color(0xFF7C4DFF)
    val DarkBackground = Color(0xFF0A0E14)
    val DarkSurface = Color(0xFF121820)
    val DarkSurfaceVariant = Color(0xFF1A2332)
    val DarkOnSurface = Color(0xFFE8EDF4)
    val DarkOnSurfaceMuted = Color(0xFF8899AA)
    val GridLine = Color(0xFF1E2A3A)
}

private val DarkColorScheme = darkColorScheme(
    primary = SentinelColors.CyberCyan,
    onPrimary = Color(0xFF002028),
    primaryContainer = Color(0xFF003544),
    onPrimaryContainer = SentinelColors.CyberCyan,
    secondary = SentinelColors.CyberGreen,
    onSecondary = Color(0xFF002110),
    tertiary = SentinelColors.CyberPurple,
    background = SentinelColors.DarkBackground,
    onBackground = SentinelColors.DarkOnSurface,
    surface = SentinelColors.DarkSurface,
    onSurface = SentinelColors.DarkOnSurface,
    surfaceVariant = SentinelColors.DarkSurfaceVariant,
    onSurfaceVariant = SentinelColors.DarkOnSurfaceMuted,
    error = SentinelColors.CyberRed,
    outline = SentinelColors.GridLine
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006876),
    onPrimary = Color.White,
    secondary = Color(0xFF006D3B),
    background = Color(0xFFF5F7FA),
    surface = Color.White,
    onSurface = Color(0xFF1A2332)
)

@Composable
fun SentinelTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SentinelTypography,
        content = content
    )
}
