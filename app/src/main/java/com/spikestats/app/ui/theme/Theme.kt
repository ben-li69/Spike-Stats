package com.spikestats.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SpikeDarkColorScheme = darkColorScheme(
    primary = ValorantRed,
    secondary = ValorantGreen,
    background = ValorantDark,
    surface = ValorantDarkElevated,
    onPrimary = ValorantLight,
    onBackground = ValorantLight,
    onSurface = ValorantLight
)

@Composable
fun SpikeStatsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SpikeDarkColorScheme,
        typography = Typography,
        content = content
    )
}
