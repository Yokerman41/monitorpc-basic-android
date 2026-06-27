package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography =
  Typography(
    bodyLarge =
      TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
      )
  )

// Techno-Minimalism Custom TextStyles
val MetricXl = TextStyle(
    fontFamily = FontFamily.Default,
    fontSize = 48.sp,
    fontWeight = FontWeight.Bold,
    lineHeight = 52.sp,
    letterSpacing = (-0.02).sp,
    fontFeatureSettings = "tnum"
)

val MetricLg = TextStyle(
    fontFamily = FontFamily.Default,
    fontSize = 32.sp,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 38.sp,
    fontFeatureSettings = "tnum"
)

val MonoData = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontSize = 14.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 20.sp,
    fontFeatureSettings = "tnum"
)

val LabelSm = TextStyle(
    fontFamily = FontFamily.Default,
    fontSize = 12.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 16.sp,
    letterSpacing = 0.05.sp
)

