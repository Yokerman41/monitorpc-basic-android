package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// 1. Circular Gauge for CPU & GPU loads
@Composable
fun CpuMainCircularGauge(
    loadPercentage: Int,
    modifier: Modifier = Modifier
) {
    val animatedPercentage by animateFloatAsState(
        targetValue = loadPercentage.toFloat(),
        animationSpec = tween(durationMillis = 800),
        label = "cpuGauge"
    )
    val primaryColor = PrimaryNeonGreen
    val surfaceVariantColor = SurfaceVariant

    Box(
        modifier = modifier.size(192.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val radius = diameter / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            // Background raw gray path ring
            drawCircle(
                color = surfaceVariantColor,
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )

            // Dynamic load arc (emerald neon green)
            val sweepAngle = (animatedPercentage / 100f) * 360f
            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(diameter, diameter),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$loadPercentage%",
                color = OnSurfaceText,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp,
                style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum")
            )
            Text(
                text = "CARGA",
                color = OnSurfaceTextVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
        }
    }
}

// 2. Circular Gauge for Disk Storage loads
@Composable
fun DiskMainCircularGauge(
    loadPercentage: Int,
    rawLabel: String,
    modifier: Modifier = Modifier
) {
    val animatedPercentage by animateFloatAsState(
        targetValue = loadPercentage.toFloat(),
        animationSpec = tween(durationMillis = 1000),
        label = "diskGauge"
    )
    val primaryColor = PrimaryNeonGreen
    val surfaceVariantColor = SurfaceVariant

    Box(
        modifier = modifier.size(192.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val radius = diameter / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            // Gray background circle
            drawCircle(
                color = surfaceVariantColor,
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )

            // Fill arc (primary emerald green)
            val sweepAngle = (animatedPercentage / 100f) * 360f
            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(diameter, diameter),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$loadPercentage%",
                color = OnSurfaceText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum")
            )
            Text(
                text = rawLabel,
                color = OnSurfaceTextVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
        }
    }
}

// 3. Double Line Graph for Core Detalle (CPU Load in Green, Temperature in Blue)
@Composable
fun CpuLoadVsTempLineChart(
    loadHistory: List<Int>,
    tempHistory: List<Int>,
    modifier: Modifier = Modifier
) {
    val primaryColor = PrimaryNeonGreen
    val borderSlateColor = BorderSlate

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val width = size.width
        val height = size.height

        // 1. Grid guide lines
        val lineSpacing = height / 4f
        for (i in 1..3) {
            val y = lineSpacing * i
            drawLine(
                color = borderSlateColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f,
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                    floatArrayOf(10f, 10f),
                    0f
                )
            )
        }

        // Draw temperature line (Blue line)
        if (tempHistory.isNotEmpty()) {
            val tempPoints = tempHistory.mapIndexed { index, temp ->
                val x = if (tempHistory.size > 1) {
                    width * (index.toFloat() / (tempHistory.size - 1))
                } else {
                    0f
                }
                val y = height * (1f - temp.coerceIn(0, 100) / 100f)
                Offset(x, y)
            }
            val tempPath = Path().apply {
                moveTo(tempPoints[0].x, tempPoints[0].y)
                for (i in 1 until tempPoints.size) {
                    val prevPoint = tempPoints[i - 1]
                    val currentPoint = tempPoints[i]
                    val controlX = (prevPoint.x + currentPoint.x) / 2
                    cubicTo(
                        controlX, prevPoint.y,
                        controlX, currentPoint.y,
                        currentPoint.x, currentPoint.y
                    )
                }
            }
            drawPath(
                path = tempPath,
                color = DataBlue,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Draw overall Load line (Emerald green) with shade gradient below it
        if (loadHistory.isNotEmpty()) {
            val loadPoints = loadHistory.mapIndexed { index, load ->
                val x = if (loadHistory.size > 1) {
                    width * (index.toFloat() / (loadHistory.size - 1))
                } else {
                    0f
                }
                val y = height * (1f - load.coerceIn(0, 100) / 100f)
                Offset(x, y)
            }

            val loadPath = Path().apply {
                moveTo(loadPoints[0].x, loadPoints[0].y)
                for (i in 1 until loadPoints.size) {
                    val prevPoint = loadPoints[i - 1]
                    val currentPoint = loadPoints[i]
                    val controlX = (prevPoint.x + currentPoint.x) / 2
                    cubicTo(
                        controlX, prevPoint.y,
                        controlX, currentPoint.y,
                        currentPoint.x, currentPoint.y
                    )
                }
            }

            // Glow shade below load path
            val gradientPath = Path().apply {
                addPath(loadPath)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(
                path = gradientPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.25f),
                        Color.Transparent
                    )
                )
            )

            drawPath(
                path = loadPath,
                color = primaryColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

// 4. Node based Fan Speed Curve configuration line drawing
@Composable
fun FanCurveChart(
    modifier: Modifier = Modifier
) {
    val primaryColor = PrimaryNeonGreen
    val borderSlateColor = BorderSlate

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.5f)
    ) {
        val width = size.width
        val height = size.height

        // Background boundary grids
        drawLine(color = borderSlateColor, start = Offset(0f, 0f), end = Offset(0f, height), strokeWidth = 2f)
        drawLine(color = borderSlateColor, start = Offset(0f, height), end = Offset(width, height), strokeWidth = 2f)

        // Pre-defined nodes styled in the original screen mockup
        val nodePoints = listOf(
            Offset(0f, height),
            Offset(width * 0.2f, height),
            Offset(width * 0.5f, height * 0.6f),
            Offset(width * 0.8f, height * 0.2f),
            Offset(width, 0f)
        )

        // Draw connection path
        val path = Path().apply {
            moveTo(nodePoints[0].x, nodePoints[0].y)
            lineTo(nodePoints[1].x, nodePoints[1].y)
            lineTo(nodePoints[2].x, nodePoints[2].y)
            lineTo(nodePoints[3].x, nodePoints[3].y)
            lineTo(nodePoints[4].x, nodePoints[4].y)
        }

        drawPath(
            path = path,
            color = primaryColor,
            style = Stroke(width = 2.dp.toPx())
        )

        // Draw joints node points
        nodePoints.forEach { point ->
            drawCircle(
                color = primaryColor,
                radius = 3.dp.toPx(),
                center = point
            )
        }

        // Draw active pulsating node dot (around 42% speed / 72% height, colored in light dynamic blue)
        drawCircle(
            color = DataBlue,
            radius = 5.dp.toPx(),
            center = Offset(width * 0.42f, height * 0.72f)
        )
    }
}

// 5. Sparkline 10-bar CPU Load stats history ("Historial 60s")
@Composable
fun SparklineCpuChart(
    sparklineHistory: List<Int>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp)
            .background(SurfaceCharcoal.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        sparklineHistory.forEachIndexed { index, value ->
            // Scale and map bar height ratio
            val barHeightFactor = (value.coerceIn(10, 100) / 100f)
            val showGlowTop = index == 4 || index == 7 || index == 9 // mimicking screenshot specs

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(barHeightFactor)
                    .background(
                        color = if (showGlowTop) PrimaryNeonGreen else PrimaryNeonGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                    )
            ) {
                // Top border line overlay for specific highlighting
                if (showGlowTop) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(Color.White)
                    )
                }
            }
        }
    }
}

@Composable
fun SparklineTelemetryLineChart(
    history: List<Int>,
    strokeColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        val width = size.width
        val height = size.height
        if (history.size < 2) return@Canvas

        val maxVal = 100 // Scale to maximum possible telemetry value (100%)
        val minVal = 0
        val range = maxVal - minVal

        val points = history.mapIndexed { index, valRaw ->
            val x = width * (index.toFloat() / (history.size - 1))
            val ratio = if (range > 0) (valRaw - minVal).toFloat() / range else 0f
            val y = height * (1f - ratio.coerceIn(0f, 1f))
            Offset(x, y)
        }

        val path = Path().apply {
            moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) {
                val prevPoint = points[i - 1]
                val currentPoint = points[i]
                val controlX = (prevPoint.x + currentPoint.x) / 2
                cubicTo(
                    controlX, prevPoint.y,
                    controlX, currentPoint.y,
                    currentPoint.x, currentPoint.y
                )
            }
        }

        // Draw shadow/glow gradient
        val gradientPath = Path().apply {
            addPath(path)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = gradientPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    strokeColor.copy(alpha = 0.25f),
                    Color.Transparent
                )
            )
        )

        // Draw stroke path
        drawPath(
            path = path,
            color = strokeColor,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

