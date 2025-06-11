package com.abdownloadmanager.shared.ui.theme

import androidx.compose.animation.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle

@Composable
fun ABDownloaderTheme(
    myColors: ABDownloaderThemeColors = ABDownloaderThemeColors(),
    uiScale: Float = 1f,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isSystemInDarkTheme()) {
        darkColorScheme(
            primary = myColors.primary,
            onPrimary = myColors.onPrimary,
            primaryContainer = myColors.primaryVariant,
            onPrimaryContainer = myColors.onPrimary,
            secondary = myColors.secondary,
            onSecondary = myColors.onSecondary,
            secondaryContainer = myColors.secondaryVariant,
            onSecondaryContainer = myColors.onSecondary,
            background = Color(0xFF202020),
            onBackground = Color(0xFFE4E4E4),
            surface = Color(0xFF2D2D2D),
            onSurface = Color(0xFFE4E4E4),
            error = myColors.error,
            onError = myColors.onError,
            surfaceVariant = Color(0xFF3D3D3D),
            onSurfaceVariant = Color(0xFFE4E4E4)
        )
    } else {
        lightColorScheme(
            primary = myColors.primary,
            onPrimary = myColors.onPrimary,
            primaryContainer = myColors.primaryVariant,
            onPrimaryContainer = myColors.onPrimary,
            secondary = myColors.secondary,
            onSecondary = myColors.onSecondary,
            secondaryContainer = myColors.secondaryVariant,
            onSecondaryContainer = myColors.onSecondary,
            background = Color(0xFFF5F5F5),
            onBackground = Color(0xFF1F1F1F),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF1F1F1F),
            error = myColors.error,
            onError = myColors.onError,
            surfaceVariant = Color(0xFFF0F0F0),
            onSurfaceVariant = Color(0xFF1F1F1F)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                content()
            }
        }
    )
}

data class ABDownloaderThemeColors(
    val primary: Color = Color(0xFF0078D4), // Windows 11 accent color
    val primaryVariant: Color = Color(0xFF106EBE),
    val secondary: Color = Color(0xFF2B88D8),
    val secondaryVariant: Color = Color(0xFF1A6FC1),
    val background: Color = Color(0xFFF5F5F5),
    val surface: Color = Color(0xFFFFFFFF),
    val error: Color = Color(0xFFD83B01),
    val onPrimary: Color = Color(0xFFFFFFFF),
    val onSecondary: Color = Color(0xFFFFFFFF),
    val onBackground: Color = Color(0xFF1F1F1F),
    val onSurface: Color = Color(0xFF1F1F1F),
    val onError: Color = Color(0xFFFFFFFF),
    val elevation: Dp = 8.dp,
    val animationDuration: Int = 300
)

private val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp)
)

private val Typography = Typography(
    headlineLarge = TextStyle(
        fontSize = 32.sp,
        lineHeight = 40.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontSize = 28.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontSize = 24.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontSize = 11.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp
    )
)
