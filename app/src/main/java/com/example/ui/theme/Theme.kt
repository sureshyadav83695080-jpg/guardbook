package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkSecurityColorScheme = darkColorScheme(
    primary = GlowingCyan,
    onPrimary = DeepNavy,
    secondary = GlowingBlue,
    onSecondary = Color.White,
    background = DeepNavy,
    onBackground = LightText,
    surface = GlassSurface,
    onSurface = LightText,
    error = GlowRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark Theme for GLOWING SECURITY look
    dynamicColor: Boolean = false, // Disable dynamic colors to preserve our unique custom glassmorphism styling
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkSecurityColorScheme,
        typography = Typography,
        content = content
    )
}
