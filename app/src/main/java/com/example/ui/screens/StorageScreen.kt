package com.example.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DiskMainCircularGauge
import com.example.ui.theme.*
import com.example.ui.utils.tr
import com.example.ui.utils.formatTemp
import com.example.viewmodel.DriveInfo
import com.example.viewmodel.MonitorViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StorageScreen(
    viewModel: MonitorViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(scrollState)
            .padding(bottom = 100.dp) // space for bottom navbar
    ) {
        // ---- TopAppBar Header Section ----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Background)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Monitor,
                    contentDescription = "monitor",
                    tint = PrimaryNeonGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Sensors,
                    contentDescription = "sensors diagnostic badge",
                    tint = OnSurfaceTextVariant,
                    modifier = Modifier.size(22.dp)
                )
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            isRefreshing = true
                            delay(1000)
                            isRefreshing = false
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "refresh button icon",
                        tint = if (isRefreshing) PrimaryNeonGreen else OnSurfaceTextVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val isWideScreen = configuration.screenWidthDp >= 600
        val useTwoColumns = isLandscape || isWideScreen

        // ---- Storage Screen Main Content ----
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen Title Section
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "SISTEMA DE ALMACENAMIENTO".tr(),
                    color = PrimaryNeonGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Salud de Discos".tr(),
                    color = OnSurfaceText,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (useTwoColumns) {
                // Top section: Global Capacity on the left (without circular gauge), SMART metrics on the right (matching height)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        GlobalCapacityVisualizerCard(
                            state = state,
                            showGauge = false,
                            modifier = Modifier.fillMaxHeight()
                        )
                    }
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        SmartMetricsTableCard(
                            state = state,
                            fillHeight = true,
                            modifier = Modifier.fillMaxHeight()
                        )
                    }
                }

                // Bottom section: Individual drives arranged side-by-side in symmetric columns
                state.drives.chunked(2).forEach { driveRow ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        driveRow.forEach { drive ->
                            Box(modifier = Modifier.weight(1f)) {
                                DriveStateCard(drive = drive, tempUnit = state.tempUnit)
                            }
                        }
                        if (driveRow.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            } else {
                state.drives.forEach { drive ->
                    DriveStateCard(drive = drive, tempUnit = state.tempUnit)
                }
                SmartMetricsTableCard(state, fillHeight = false)
                GlobalCapacityVisualizerCard(state, showGauge = true, modifier = Modifier.padding(top = 12.dp))
            }
        }
    }
}

@Composable
fun DriveStateCard(
    drive: DriveInfo,
    tempUnit: String,
    modifier: Modifier = Modifier
) {
    val accentColor = when (drive.status) {
        "Excelente", "Estable" -> StatusHealthy
        "Atención" -> StatusWarning
        else -> StatusCritical
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderSlate)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Side status accent line
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
                    .background(accentColor)
            )

            Column(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header block
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(bottom = 2.dp)
                        ) {
                            val innerIcon = when (drive.iconName) {
                                "memory" -> Icons.Filled.Memory
                                "storage" -> Icons.Filled.Storage
                                else -> Icons.Filled.Storage
                            }
                            Icon(
                                imageVector = innerIcon,
                                contentDescription = drive.type,
                                tint = PrimaryNeonGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = drive.type,
                                color = OnSurfaceTextVariant,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = drive.name,
                            color = OnSurfaceText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${drive.lifePct}%",
                            color = accentColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum")
                        )
                        Text(
                            text = "VIDA ÚTIL".tr(),
                            color = OnSurfaceTextVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Sub details grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Estado Box
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(SurfaceContainerLowest, shape = RoundedCornerShape(8.dp))
                            .border(1.dp, BorderSlate, shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Column {
                            Text(
                                text = "ESTADO".tr(),
                                color = OnSurfaceTextVariant,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (drive.status == "Atención") {
                                    Icon(
                                        imageVector = Icons.Filled.Warning,
                                        contentDescription = "attention warning icon",
                                        tint = StatusWarning,
                                        modifier = Modifier.size(13.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "healthy check circle icon",
                                        tint = StatusHealthy,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                                Text(
                                    text = drive.status.tr(),
                                    color = accentColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Temperature Box
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(SurfaceContainerLowest, shape = RoundedCornerShape(8.dp))
                            .border(1.dp, BorderSlate, shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Column {
                            Text(
                                text = "TEMP".tr(),
                                color = OnSurfaceTextVariant,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.DeviceThermostat,
                                    contentDescription = "device thermostat temp",
                                    tint = if (drive.tempCelsius > 50) StatusCritical else OnSurfaceTextVariant,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = formatTemp(drive.tempCelsius, tempUnit),
                                    color = if (drive.tempCelsius > 50) StatusCritical else OnSurfaceText,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum")
                                )
                            }
                        }
                    }
                }

                // Space consumption progress
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Espacio Usado".tr(),
                            color = OnSurfaceTextVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        val usedLabel = if (drive.totalGb >= 1000.0) {
                            "${String.format("%.1f", drive.usedGb / 1000.0)} TB / ${String.format("%.1f", drive.totalGb / 1000.0)} TB"
                        } else {
                            "${drive.usedGb.toInt()} GB / ${drive.totalGb.toInt()} GB"
                        }
                        Text(
                            text = usedLabel,
                            color = OnSurfaceText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum")
                        )
                    }

                    val ratio = (drive.usedGb / drive.totalGb).toFloat()
                    val animatedSpace by animateFloatAsState(
                        targetValue = ratio,
                        animationSpec = spring(stiffness = Spring.StiffnessLow),
                        label = "driveSpace"
                    )

                    val barColor = when (drive.status) {
                        "Excelente" -> PrimaryNeonGreen
                        "Atención" -> StatusWarning
                        else -> DataBlue
                    }

                    LinearProgressIndicator(
                        progress = { animatedSpace },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(100.dp)),
                        color = barColor,
                        trackColor = SurfaceContainerHighest
                    )
                }
            }
        }
    }
}

@Composable
fun SmartMetricsTableCard(
    state: com.example.viewmodel.MonitorUiState,
    fillHeight: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Analytics,
                contentDescription = "smart analytics",
                tint = PrimaryNeonGreen,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Métricas SMART Detalladas".tr(),
                color = OnSurfaceText,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Card(
            modifier = if (fillHeight) Modifier.fillMaxWidth().weight(1f) else Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, BorderSlate)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceContainerLow, shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "ATRIBUTO".tr(),
                        color = OnSurfaceTextVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(2.2f)
                    )
                    Text(
                        text = "ACT".tr(),
                        color = OnSurfaceTextVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.8f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "PEOR".tr(),
                        color = OnSurfaceTextVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.8f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "ESTADO".tr(),
                        color = OnSurfaceTextVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.2f),
                        textAlign = TextAlign.End
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(BorderSlate)
                )

                state.smartMetrics.forEachIndexed { index, metric ->
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = metric.attribute,
                                color = OnSurfaceText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(2.2f)
                            )
                            Text(
                                text = metric.value.toString(),
                                color = OnSurfaceText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(0.8f),
                                textAlign = TextAlign.Center,
                                style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum")
                            )
                            Text(
                                text = metric.worst.toString(),
                                color = OnSurfaceText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(0.8f),
                                textAlign = TextAlign.Center,
                                style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum")
                            )
                            Text(
                                text = metric.status.tr(),
                                color = if (metric.status == "OK") StatusHealthy else StatusWarning,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1.2f),
                                textAlign = TextAlign.End
                            )
                        }
                        if (index < state.smartMetrics.size - 1) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(0.5.dp)
                                    .background(BorderSlate.copy(alpha = 0.5f))
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GlobalCapacityVisualizerCard(
    state: com.example.viewmodel.MonitorUiState,
    showGauge: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderSlate)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (showGauge) {
                DiskMainCircularGauge(
                    loadPercentage = state.globalStorageUsedPct,
                    rawLabel = "OCUPADO".tr(),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Resumen de Capacidad Global".tr(),
                    color = OnSurfaceText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(SurfaceContainerLow, shape = RoundedCornerShape(8.dp))
                            .border(1.dp, BorderSlate, shape = RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TOTAL".tr(),
                            color = OnSurfaceTextVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${state.globalStorageTotalTb} TB",
                            color = OnSurfaceText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum")
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(SurfaceContainerLow, shape = RoundedCornerShape(8.dp))
                            .border(1.dp, BorderSlate, shape = RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "USADO".tr(),
                            color = OnSurfaceTextVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${state.globalStorageUsedTb} TB",
                            color = PrimaryNeonGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum")
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(SurfaceContainerLow, shape = RoundedCornerShape(8.dp))
                            .border(1.dp, BorderSlate, shape = RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "LIBRE".tr(),
                            color = OnSurfaceTextVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${state.globalStorageFreeTb} TB",
                            color = OnSurfaceTextVariant,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum")
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(SurfaceContainerLow, shape = RoundedCornerShape(8.dp))
                            .border(1.dp, BorderSlate, shape = RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "LECTURA".tr(),
                            color = OnSurfaceTextVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = state.diskReadSpeedFormatted,
                            color = PrimaryNeonGreen,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum")
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(SurfaceContainerLow, shape = RoundedCornerShape(8.dp))
                            .border(1.dp, BorderSlate, shape = RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ESCRITURA".tr(),
                            color = OnSurfaceTextVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = state.diskWriteSpeedFormatted,
                            color = DataBlue,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum")
                        )
                    }
                }
            }
        }
    }
}
