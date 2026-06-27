package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import org.json.JSONObject

import androidx.compose.ui.graphics.luminance

data class CustomThemeColors(
    val statusHealthy: Color = Color(0xFF22C55E),
    val statusWarning: Color = Color(0xFFF59E0B),
    val statusCritical: Color = Color(0xFFEF4444)
)

val LocalCustomColors = androidx.compose.runtime.staticCompositionLocalOf {
    CustomThemeColors()
}

private val DarkColorScheme = darkColorScheme(
    primary = StaticPrimaryNeonGreen,
    onPrimary = StaticOnPrimaryGreen,
    primaryContainer = StaticPrimaryContainerGreen,
    onPrimaryContainer = StaticOnPrimaryContainerGreen,
    secondary = StaticSecondaryBlue,
    onSecondary = StaticOnSecondaryBlue,
    secondaryContainer = StaticSecondaryContainerBlue,
    onSecondaryContainer = StaticOnSecondaryContainerBlue,
    background = StaticBackground,
    onBackground = Color(0xFFE5E1E4),
    surface = StaticSurfaceCharcoal,
    onSurface = Color(0xFFE5E1E4),
    surfaceVariant = Color(0xFF353437),
    onSurfaceVariant = Color(0xFFB0C0B7),
    outline = StaticBorderSlate,
    error = Color(0xFFEF4444),
    onError = Color(0xFF0E0E10),
    surfaceContainerLowest = Color(0xFF0E0E10),
    surfaceContainerLow = Color(0xFF1C1B1D),
    surfaceContainerHigh = Color(0xFF2A2A2C),
    surfaceContainerHighest = Color(0xFF353437)
)

fun adjustColorLuminance(color: Color, factor: Float): Color {
    return Color(
        android.graphics.Color.argb(
            255,
            (color.red * 255 * factor).toInt().coerceIn(0, 255),
            (color.green * 255 * factor).toInt().coerceIn(0, 255),
            (color.blue * 255 * factor).toInt().coerceIn(0, 255)
        )
    )
}

fun parseCustomColorScheme(jsonStr: String): ColorScheme? {
    if (jsonStr.isBlank()) return null
    return try {
        val obj = JSONObject(jsonStr)
        val primaryHex = obj.optString("primary", "")
        val backgroundHex = obj.optString("background", "")
        val secondaryHex = obj.optString("secondary", "")
        val surfaceHex = obj.optString("surface", "")
        val onSurfaceVariantHex = obj.optString("onSurfaceVariant", "")
        
        var primary = DarkColorScheme.primary
        var background = DarkColorScheme.background
        var surface = DarkColorScheme.surface
        var secondary = DarkColorScheme.secondary
        
        if (primaryHex.isNotEmpty()) {
            primary = Color(android.graphics.Color.parseColor(primaryHex))
        }
        if (backgroundHex.isNotEmpty()) {
            background = Color(android.graphics.Color.parseColor(backgroundHex))
        }
        if (surfaceHex.isNotEmpty()) {
            surface = Color(android.graphics.Color.parseColor(surfaceHex))
        } else if (backgroundHex.isNotEmpty()) {
            surface = Color(android.graphics.Color.parseColor(backgroundHex))
        }
        if (secondaryHex.isNotEmpty()) {
            secondary = Color(android.graphics.Color.parseColor(secondaryHex))
        }
        
        val isPrimaryLight = primary.luminance() > 0.5f
        val onPrimary = if (isPrimaryLight) Color(0xFF131315) else Color.White
        val primaryContainer = primary
        val onPrimaryContainer = onPrimary
        
        val isSecondaryLight = secondary.luminance() > 0.5f
        val onSecondary = if (isSecondaryLight) Color(0xFF131315) else Color.White
        val secondaryContainer = secondary
        val onSecondaryContainer = onSecondary
        
        val isBgLight = background.luminance() > 0.5f
        val onBgColor = if (isBgLight) Color(0xFF131315) else Color(0xFFE5E1E4)
        
        var onSurfaceVariant = DarkColorScheme.onSurfaceVariant
        if (onSurfaceVariantHex.isNotEmpty()) {
            onSurfaceVariant = Color(android.graphics.Color.parseColor(onSurfaceVariantHex))
        }
        
        val isSurfaceLight = surface.luminance() > 0.5f
        val outline = if (isSurfaceLight) {
            adjustColorLuminance(surface, 0.8f)
        } else {
            // slightly lighter border
            Color(android.graphics.Color.argb(
                255,
                (surface.red * 255 * 1.5f + 15).toInt().coerceIn(0, 255),
                (surface.green * 255 * 1.5f + 15).toInt().coerceIn(0, 255),
                (surface.blue * 255 * 1.5f + 15).toInt().coerceIn(0, 255)
            ))
        }
        
        val surfaceContainerLowest = if (isBgLight) adjustColorLuminance(background, 0.9f) else adjustColorLuminance(background, 0.7f)
        val surfaceContainerLow = if (isSurfaceLight) adjustColorLuminance(surface, 0.95f) else adjustColorLuminance(surface, 0.85f)
        val surfaceContainerHigh = if (isSurfaceLight) adjustColorLuminance(surface, 1.05f) else adjustColorLuminance(surface, 1.15f)
        val surfaceContainerHighest = if (isSurfaceLight) adjustColorLuminance(surface, 1.1f) else adjustColorLuminance(surface, 1.30f)
        
        DarkColorScheme.copy(
            primary = primary,
            onPrimary = onPrimary,
            primaryContainer = primaryContainer,
            onPrimaryContainer = onPrimaryContainer,
            secondary = secondary,
            onSecondary = onSecondary,
            secondaryContainer = secondaryContainer,
            onSecondaryContainer = onSecondaryContainer,
            background = background,
            onBackground = onBgColor,
            surface = surface,
            onSurface = onBgColor,
            onSurfaceVariant = onSurfaceVariant,
            outline = outline,
            surfaceContainerLowest = surfaceContainerLowest,
            surfaceContainerLow = surfaceContainerLow,
            surfaceContainerHigh = surfaceContainerHigh,
            surfaceContainerHighest = surfaceContainerHighest
        )
    } catch (e: Exception) {
        null
    }
}

@Composable
fun MyApplicationTheme(
    themeJson: String = "",
    content: @Composable () -> Unit
) {
    val colorScheme = remember(themeJson) {
        parseCustomColorScheme(themeJson) ?: DarkColorScheme
    }
    
    val customColors = remember(themeJson) {
        if (themeJson.isBlank()) {
            CustomThemeColors()
        } else {
            try {
                val obj = JSONObject(themeJson)
                val normalHex = obj.optString("normal", "")
                val alertaHex = obj.optString("alerta", "")
                val criticoHex = obj.optString("critico", "")
                
                CustomThemeColors(
                    statusHealthy = if (normalHex.isNotEmpty()) Color(android.graphics.Color.parseColor(normalHex)) else Color(0xFF22C55E),
                    statusWarning = if (alertaHex.isNotEmpty()) Color(android.graphics.Color.parseColor(alertaHex)) else Color(0xFFF59E0B),
                    statusCritical = if (criticoHex.isNotEmpty()) Color(android.graphics.Color.parseColor(criticoHex)) else Color(0xFFEF4444)
                )
            } catch (e: Exception) {
                CustomThemeColors()
            }
        }
    }
    
    androidx.compose.runtime.CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
