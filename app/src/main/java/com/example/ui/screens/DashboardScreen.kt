package com.example.ui.screens

import com.example.ui.utils.tr
import com.example.ui.utils.formatTemp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.MonitorViewModel
import com.example.ui.components.SparklineTelemetryLineChart

@Composable
fun DashboardScreen(
    viewModel: MonitorViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(scrollState)
            .padding(bottom = 90.dp) // compacted bottom padding
    ) {
        // ---- TopAppBar Header Section ----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // compacted header height (was 64.dp)
                .background(Background)
                .padding(horizontal = 12.dp),
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
                    modifier = Modifier.size(22.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                var expanded by remember { mutableStateOf(value = false) }

                Box {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.clickable { expanded = true }
                    ) {
                        Text(
                            text = "DISPOSITIVO".tr(),
                            color = OnSurfaceTextVariant,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                        val activeCompName = state.computers.find { it.id == state.activeComputerId }?.name ?: "(Pc)"
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = activeCompName,
                                color = PrimaryNeonGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Filled.ArrowDownward,
                                contentDescription = "Selector de dispositivo",
                                tint = PrimaryNeonGreen,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(SurfaceCharcoal)
                    ) {
                        state.computers.forEach { computer ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = computer.name,
                                        color = if (computer.id == state.activeComputerId) PrimaryNeonGreen else OnSurfaceText
                                    )
                                },
                                onClick = {
                                    expanded = false
                                    viewModel.selectComputer(computer.id)
                                }
                            )
                        }
                    }
                }

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(28.dp)
                        .background(BorderSlate)
                )

                // Capsule Status: "En línea" / "Desconectado"
                val statusColor = if (state.isConnected) StatusHealthy else StatusCritical
                val statusLabel = if (state.isConnected) "En línea".tr() else "Desconectado".tr()

                Row(
                    modifier = Modifier
                        .background(SurfaceCharcoal, shape = RoundedCornerShape(100.dp))
                        .border(1.dp, BorderSlate, shape = RoundedCornerShape(100.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(statusColor, shape = CircleShape)
                    )
                    Text(
                        text = statusLabel,
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    imageVector = Icons.Filled.Sensors,
                    contentDescription = "Sensors icon",
                    tint = OnSurfaceTextVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val isWideScreen = configuration.screenWidthDp >= 600
        val useTwoColumns = isLandscape || isWideScreen

        if (state.computers.isEmpty()) {
            EmptyDashboardState(onAddPcClick = { viewModel.setShowComputersScreen(true) })
        } else {
            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        CpuCard(state = state, modifier = Modifier.fillMaxHeight())
                    }
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        GpuCard(state = state, modifier = Modifier.fillMaxHeight())
                    }
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        RamCard(state = state, modifier = Modifier.fillMaxHeight())
                    }
                }
            } else if (isWideScreen) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SystemInfoCard(state = state)
                        CpuCard(state = state)
                        RamCard(state = state)
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GpuCard(state = state)
                        MotorInvisibleCard()
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SystemInfoCard(state = state)
                    CpuCard(state = state)
                    GpuCard(state = state)
                    RamCard(state = state)
                    MotorInvisibleCard()
                }
            }
        }
    }
}

@Composable
fun EmptyDashboardState(
    onAddPcClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(PrimaryNeonGreen.copy(alpha = 0.05f), shape = CircleShape)
                .border(2.dp, Brush.verticalGradient(listOf(PrimaryNeonGreen, Color.Transparent)), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Monitor,
                contentDescription = "No PC icon",
                tint = PrimaryNeonGreen,
                modifier = Modifier.size(48.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Bienvenido a monitorPC".tr(),
                color = OnSurfaceText,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Parece que aún no tienes ningún equipo registrado. Comienza vinculando tu PC mediante el PIN de 6 dígitos para ver las métricas en tiempo real.".tr(),
                color = OnSurfaceTextVariant,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }

        Button(
            onClick = onAddPcClick,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeonGreen),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Router,
                contentDescription = "add PC",
                tint = OnPrimaryGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "AGREGA TU PRIMERA PC".tr(),
                color = OnPrimaryGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        Text(
            text = "Asegúrate de tener el agente ejecutándose en tu computadora.".tr(),
            color = OnSurfaceTextVariant.copy(alpha = 0.7f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MetricBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
    isLarge: Boolean = false
) {
    Column(
        modifier = modifier
            .background(SurfaceContainerLow, shape = RoundedCornerShape(8.dp))
            .border(BorderStroke(1.dp, BorderSlate.copy(alpha = 0.5f)), shape = RoundedCornerShape(8.dp))
            .padding(
                vertical = if (isLarge) 8.dp else 5.dp,
                horizontal = if (isLarge) 8.dp else 4.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(if (isLarge) 2.dp else 1.dp)
    ) {
        Text(
            text = label,
            color = OnSurfaceTextVariant,
            fontSize = if (isLarge) 12.sp else 9.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            softWrap = false,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
        Text(
            text = value,
            color = color,
            fontSize = if (isLarge) 20.sp else 11.sp,
            fontWeight = FontWeight.ExtraBold,
            style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum"),
            maxLines = 1,
            softWrap = false,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SystemInfoCard(
    state: com.example.viewmodel.MonitorUiState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderSlate)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = state.pcOsName,
                        color = OnSurfaceText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${"Uptime".tr()}: ${state.pcUptimeFormatted}",
                        color = OnSurfaceTextVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum")
                    )
                }
                
                if (state.connectionError != null) {
                    Text(
                        text = state.connectionError!!,
                        color = StatusWarning,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(StatusWarning.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            // Active Warning Alerts
            val alerts = remember(state.cpuOverallTemp, state.cpuOverallLoad, state.gpuTemp, state.gpuLoad, state.cpuTempLimit, state.cpuLoadLimit, state.gpuTempLimit, state.gpuLoadLimit) {
                val list = mutableListOf<String>()
                if (state.cpuOverallTemp > state.cpuTempLimit) {
                    list.add("${"Alerta".tr()}: CPU Temp (${state.cpuOverallTemp}°C) > ${state.cpuTempLimit}°C")
                }
                if (state.cpuOverallLoad > state.cpuLoadLimit) {
                    list.add("${"Alerta".tr()}: CPU Load (${state.cpuOverallLoad}%) > ${state.cpuLoadLimit}%")
                }
                if (state.gpuTemp > state.gpuTempLimit) {
                    list.add("${"Alerta".tr()}: GPU Temp (${state.gpuTemp}°C) > ${state.gpuTempLimit}°C")
                }
                if (state.gpuLoad > state.gpuLoadLimit) {
                    list.add("${"Alerta".tr()}: GPU Load (${state.gpuLoad}%) > ${state.gpuLoadLimit}%")
                }
                list
            }

            if (alerts.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(BorderSlate)
                )
                alerts.forEach { alert ->
                     Row(
                         verticalAlignment = Alignment.CenterVertically,
                         horizontalArrangement = Arrangement.spacedBy(6.dp),
                         modifier = Modifier.fillMaxWidth()
                     ) {
                         Icon(
                             imageVector = Icons.Filled.Warning,
                             contentDescription = "alert warning icon",
                             tint = StatusCritical,
                             modifier = Modifier.size(14.dp)
                         )
                         Text(
                             text = alert,
                             color = StatusCritical,
                             fontSize = 11.sp,
                             fontWeight = FontWeight.Bold
                         )
                     }
                }
            }
        }
    }
}

@Composable
fun CpuCard(
    state: com.example.viewmodel.MonitorUiState,
    modifier: Modifier = Modifier
) {
    val hasCpuAlert = (state.cpuOverallTemp > state.cpuTempLimit) || (state.cpuOverallLoad > state.cpuLoadLimit)
    val cpuCardBorderColor = if (hasCpuAlert) StatusCritical else BorderSlate
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, cpuCardBorderColor)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight().padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PROCESADOR (CPU)".tr(),
                        color = OnSurfaceTextVariant,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = state.cpuName,
                        color = OnSurfaceText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Icon(
                    imageVector = Icons.Filled.Memory,
                    contentDescription = "CPU Icon",
                    tint = PrimaryNeonGreen,
                    modifier = Modifier.size(16.dp)
                )
            }

            // CPU Stats Row 1 (2 columns - Large)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MetricBox(label = "Uso".tr(), value = "${state.cpuOverallLoad}%", color = PrimaryNeonGreen, modifier = Modifier.weight(1f), isLarge = true)
                MetricBox(label = "Temperatura".tr(), value = formatTemp(state.cpuOverallTemp, state.tempUnit), color = StatusWarning, modifier = Modifier.weight(1f), isLarge = true)
            }
            
            // CPU Stats Row 2 (4 columns - Small)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MetricBox(label = "Velocidad".tr(), value = "${state.cpuOverallSpeedGhz} GHz", color = Color(0xFF8B5CF6), modifier = Modifier.weight(1f), isLarge = false)
                MetricBox(label = "Procesos".tr(), value = state.totalProcesses.toString(), color = Color(0xFFF59E0B), modifier = Modifier.weight(1f), isLarge = false)
                MetricBox(label = "Hilos".tr(), value = "${state.totalThreads}", color = Color(0xFF06B6D4), modifier = Modifier.weight(1f), isLarge = false)
                MetricBox(label = "Voltaje".tr(), value = "${state.cpuVoltage} V", color = DataBlue, modifier = Modifier.weight(1f), isLarge = false)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Sparkline Chart (Vertical fluctuation)
            SparklineTelemetryLineChart(
                history = state.cpuSparklineHistory,
                strokeColor = PrimaryNeonGreen,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
            )
        }
    }
}

@Composable
fun GpuCard(
    state: com.example.viewmodel.MonitorUiState,
    modifier: Modifier = Modifier
) {
    val hasGpuAlert = state.gpuTemp > state.gpuTempLimit || state.gpuLoad > state.gpuLoadLimit
    val gpuCardBorderColor = if (hasGpuAlert) StatusCritical else BorderSlate
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, gpuCardBorderColor)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight().padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "GRÁFICOS (GPU)".tr(),
                        color = OnSurfaceTextVariant,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = state.gpuModel,
                        color = OnSurfaceText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Icon(
                    imageVector = Icons.Filled.Memory,
                    contentDescription = "GPU Icon",
                    tint = PrimaryNeonGreen,
                    modifier = Modifier.size(16.dp)
                )
            }

            // GPU Stats Row 1 (2 columns - Large)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MetricBox(label = "Uso".tr(), value = "${state.gpuLoad}%", color = PrimaryNeonGreen, modifier = Modifier.weight(1f), isLarge = true)
                MetricBox(label = "Temperatura".tr(), value = formatTemp(state.gpuTemp, state.tempUnit), color = StatusWarning, modifier = Modifier.weight(1f), isLarge = true)
            }
            
            // GPU Stats Row 2 (4 columns - Small)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val vramDisplay = "${state.gpuVramUsedGb.toInt()}G/${state.gpuVramTotalGb.toInt()}G"
                MetricBox(label = "VRAM".tr(), value = vramDisplay, color = DataBlue, modifier = Modifier.weight(1f), isLarge = false)
                
                val rightValue = if (state.fps > 0.0) "${state.fps.toInt()} FPS" else "${state.gpuLoad3d}%"
                val rightLabel = if (state.fps > 0.0) "FPS".tr() else "Carga 3D".tr()
                MetricBox(label = rightLabel, value = rightValue, color = Color(0xFF8B5CF6), modifier = Modifier.weight(1f), isLarge = false)
                
                MetricBox(label = "Encode".tr(), value = "${state.gpuVideoEncode}%", color = Color(0xFFF59E0B), modifier = Modifier.weight(1f), isLarge = false)
                MetricBox(label = "Decode".tr(), value = "${state.gpuVideoDecode}%", color = Color(0xFF06B6D4), modifier = Modifier.weight(1f), isLarge = false)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Sparkline Chart (Vertical fluctuation)
            SparklineTelemetryLineChart(
                history = state.gpuSparklineHistory,
                strokeColor = PrimaryNeonGreen,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
            )
        }
    }
}

@Composable
fun RamCard(
    state: com.example.viewmodel.MonitorUiState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderSlate)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight().padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "MEMORIA RAM".tr(),
                        color = OnSurfaceTextVariant,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = state.ramManufacturer,
                        color = OnSurfaceText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Icon(
                    imageVector = Icons.Filled.Memory,
                    contentDescription = "RAM Icon",
                    tint = PrimaryNeonGreen,
                    modifier = Modifier.size(16.dp)
                )
            }

            // RAM Stats Row (2 columns - Large)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val pct = if (state.ramTotalGb > 0) ((state.ramUsedGb / state.ramTotalGb) * 100).toInt() else 0
                MetricBox(label = "Uso".tr(), value = "${state.ramUsedGb} GB", color = PrimaryNeonGreen, modifier = Modifier.weight(1f), isLarge = true)
                MetricBox(label = "Total".tr(), value = "${state.ramTotalGb.toInt()} GB", color = DataBlue, modifier = Modifier.weight(1f), isLarge = true)
            }

            // RAM Stats Row 2 (4 columns - Small)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MetricBox(label = "Marca".tr(), value = state.ramManufacturer, color = Color(0xFF8B5CF6), modifier = Modifier.weight(1f), isLarge = false)
                MetricBox(label = "Tipo".tr(), value = state.ramType, color = Color(0xFFF59E0B), modifier = Modifier.weight(1f), isLarge = false)
                MetricBox(label = "Reloj".tr(), value = "${state.ramSpeedMhz} MHz", color = Color(0xFF06B6D4), modifier = Modifier.weight(1f), isLarge = false)
                MetricBox(label = "Módulos".tr(), value = "${state.ramModulesCount}", color = DataBlue, modifier = Modifier.weight(1f), isLarge = false)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Sparkline Chart (Vertical fluctuation)
            SparklineTelemetryLineChart(
                history = state.ramSparklineHistory,
                strokeColor = PrimaryNeonGreen,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
            )
        }
    }
}

@Composable
fun MotorInvisibleCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderSlate)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(SurfaceCharcoal, Color(0xFF1E293B)),
                        startX = 0f,
                        endX = Float.POSITIVE_INFINITY
                    )
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Motor Invisible".tr(),
                    color = OnSurfaceText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Análisis térmico habilitado".tr(),
                    color = OnSurfaceTextVariant,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
