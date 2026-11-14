package com.example.alimentared.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EcoForestGreen,
    secondary = EcoSageGreen,
    tertiary = EcoMintGreen,
    background = Color(0xFF141F16),
    surface = Color(0xFF1B2B1E),
    onPrimary = Color.White,
    onSecondary = EcoTextPrimary,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = EcoForestGreen,
    secondary = EcoSageGreen,
    tertiary = EcoMintGreen,
    background = EcoBackground,
    surface = EcoSurface,
    onPrimary = Color.White,
    onSecondary = EcoTextPrimary,
    onBackground = EcoTextPrimary,
    onSurface = EcoTextPrimary
)

@Composable
fun AlimentaRedTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
