package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ShopyTealLight,
    onPrimary = Color.Black,
    primaryContainer = ShopyTealDark,
    onPrimaryContainer = ShopyTealContainer,
    secondary = ShopyCoral,
    onSecondary = Color.White,
    secondaryContainer = ShopyCoralDark,
    onSecondaryContainer = ShopyCoralContainer,
    tertiary = ShopyGold,
    background = Color(0xFF101415),
    surface = Color(0xFF1A1F20),
    surfaceVariant = Color(0xFF262C2E),
    onBackground = Color(0xFFE1E3E4),
    onSurface = Color(0xFFE1E3E4),
    outline = Color(0xFF3F484A)
)

private val LightColorScheme = lightColorScheme(
    primary = ShopyTeal,
    onPrimary = Color.White,
    primaryContainer = ShopyTealContainer,
    onPrimaryContainer = ShopyOnTealContainer,
    secondary = ShopyCoral,
    onSecondary = Color.White,
    secondaryContainer = ShopyCoralContainer,
    onSecondaryContainer = ShopyOnCoralContainer,
    tertiary = ShopyGold,
    background = NeutralLight,
    surface = NeutralWhite,
    surfaceVariant = Color(0xFFF1F5F7),
    onBackground = NeutralDark,
    onSurface = NeutralDark,
    outline = NeutralBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // false to preserve signature ShopyExpress branding
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
