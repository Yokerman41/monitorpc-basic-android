package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Static constants for fallback/theme definition
val StaticBackground = Color(0xFF131315)
val StaticSurfaceCharcoal = Color(0xFF18181B)
val StaticBorderSlate = Color(0xFF27272A)

val StaticPrimaryNeonGreen = Color(0xFF4EDE9D) // Main accent
val StaticOnPrimaryGreen = Color(0xFF003824)
val StaticPrimaryContainerGreen = Color(0xFF10B981)
val StaticOnPrimaryContainerGreen = Color(0xFF00422b)

val StaticSecondaryBlue = Color(0xFFADC6FF)
val StaticOnSecondaryBlue = Color(0xFF002E6A)
val StaticSecondaryContainerBlue = Color(0xFF0566D9)
val StaticOnSecondaryContainerBlue = Color(0xFFE6ECFF)

// Composable dynamic getters linked to the theme ColorScheme
val Background: Color
    @Composable
    get() = MaterialTheme.colorScheme.background

val SurfaceCharcoal: Color
    @Composable
    get() = MaterialTheme.colorScheme.surface

val BorderSlate: Color
    @Composable
    get() = MaterialTheme.colorScheme.outline

val PrimaryNeonGreen: Color
    @Composable
    get() = MaterialTheme.colorScheme.primary

val SecondaryBlue: Color
    @Composable
    get() = MaterialTheme.colorScheme.secondary

val OnPrimaryGreen: Color
    @Composable
    get() = MaterialTheme.colorScheme.onPrimary

val PrimaryContainerGreen: Color
    @Composable
    get() = MaterialTheme.colorScheme.primaryContainer

val OnPrimaryContainerGreen: Color
    @Composable
    get() = MaterialTheme.colorScheme.onPrimaryContainer

val OnSecondaryBlue: Color
    @Composable
    get() = MaterialTheme.colorScheme.onSecondary

val SecondaryContainerBlue: Color
    @Composable
    get() = MaterialTheme.colorScheme.secondaryContainer

val OnSecondaryContainerBlue: Color
    @Composable
    get() = MaterialTheme.colorScheme.onSecondaryContainer

val DataBlue = Color(0xFF60A5FA)

val StatusHealthy: Color
    @Composable
    get() = LocalCustomColors.current.statusHealthy

val StatusWarning: Color
    @Composable
    get() = LocalCustomColors.current.statusWarning

val StatusCritical: Color
    @Composable
    get() = LocalCustomColors.current.statusCritical

val SurfaceVariant: Color
    @Composable
    get() = MaterialTheme.colorScheme.surfaceVariant

val OnSurfaceText: Color
    @Composable
    get() = MaterialTheme.colorScheme.onSurface

val OnSurfaceTextVariant: Color
    @Composable
    get() = MaterialTheme.colorScheme.onSurfaceVariant

val SurfaceContainerLowest: Color
    @Composable
    get() = MaterialTheme.colorScheme.surfaceContainerLowest

val SurfaceContainerLow: Color
    @Composable
    get() = MaterialTheme.colorScheme.surfaceContainerLow

val SurfaceContainerHigh: Color
    @Composable
    get() = MaterialTheme.colorScheme.surfaceContainerHigh

val SurfaceContainerHighest: Color
    @Composable
    get() = MaterialTheme.colorScheme.surfaceContainerHighest

