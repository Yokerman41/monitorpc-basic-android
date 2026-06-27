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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    modifier: Modifier = Modifier
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
                var expanded by remember { mutableStateOf(false) }

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

        if (state.computers.isEmpty()) {
            EmptyDashboardState(onAddPcClick = { viewModel.setShowComputersScreen(true) })
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp), // compacted padding (was 16.dp)
                verticalArrangement = Arrangement.spacedBy(8.dp) // compacted spacing (was 12.dp)
            ) {
                // Elegant System Info & Active Alerts Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderSlate)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp), // compacted padding (was 16.dp)
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
                        val alerts = mutableListOf<String>()
                        if (state.cpuOverallTemp > state.cpuTempLimit) {
                            alerts.add("${"Alerta".tr()}: CPU Temp (${state.cpuOverallTemp}°C) > ${state.cpuTempLimit}°C")
                        }
                        if (state.cpuOverallLoad > state.cpuLoadLimit) {
                            alerts.add("${"Alerta".tr()}: CPU Load (${state.cpuOverallLoad}%) > ${state.cpuLoadLimit}%")
                        }
                        if (state.gpuTemp > state.gpuTempLimit) {
                            alerts.add("${"Alerta".tr()}: GPU Temp (${state.gpuTemp}°C) > ${state.gpuTempLimit}°C")
                        }
                        if (state.gpuLoad > state.gpuLoadLimit) {
                            alerts.add("${"Alerta".tr()}: GPU Load (${state.gpuLoad}%) > ${state.gpuLoadLimit}%")
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

                // 1. CPU Section
                val hasCpuAlert = state.cpuOverallTemp > state.cpuTempLimit || state.cpuOverallLoad > state.cpuLoadLimit
                val cpuCardBorderColor = if (hasCpuAlert) StatusCritical else BorderSlate
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, cpuCardBorderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp), // compacted padding
                        verticalArrangement = Arrangement.spacedBy(10.dp) // compacted spacing (was 16.dp)
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
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                )
                                Text(
                                    text = state.cpuName,
                                    color = OnSurfaceText,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Icon(
                                imageVector = Icons.Filled.Memory,
                                contentDescription = "CPU Icon",
                                tint = PrimaryNeonGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // CPU Stats Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricBox(label = "Uso".tr(), value = "${state.cpuOverallLoad}%", color = PrimaryNeonGreen, modifier = Modifier.weight(1f))
                            MetricBox(label = "Velocidad".tr(), value = "${state.cpuOverallSpeedGhz} GHz", color = Color(0xFF8B5CF6), modifier = Modifier.weight(1f))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricBox(label = "Procesos".tr(), value = "${state.totalProcesses}", color = Color(0xFFF59E0B), modifier = Modifier.weight(1f))
                            MetricBox(label = "Subproc.".tr(), value = "${state.totalThreads}", color = Color(0xFF06B6D4), modifier = Modifier.weight(1f))
                        }

                        // Sparkline Chart (Vertical fluctuation) - compacted height (was 56.dp)
                        SparklineTelemetryLineChart(
                            history = state.cpuSparklineHistory,
                            strokeColor = PrimaryNeonGreen,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        )

                        // CPU Temperature Warning Capsule
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceContainerLow, shape = RoundedCornerShape(8.dp))
                                .padding(10.dp), // compacted padding
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Thermostat,
                                    contentDescription = "CPU temp warning",
                                    tint = StatusWarning,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Temperatura".tr(),
                                    color = OnSurfaceText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = formatTemp(state.cpuOverallTemp, state.tempUnit),
                                color = OnSurfaceText,
                                fontSize = 28.sp, // larger size (was 24.sp)
                                fontWeight = FontWeight.Bold,
                                style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum")
                            )
                        }
                    }
                }

                // 2. GPU Section
                val hasGpuAlert = state.gpuTemp > state.gpuTempLimit || state.gpuLoad > state.gpuLoadLimit
                val gpuCardBorderColor = if (hasGpuAlert) StatusCritical else BorderSlate
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, gpuCardBorderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp), // compacted padding
                        verticalArrangement = Arrangement.spacedBy(10.dp) // compacted spacing
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
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                )
                                Text(
                                    text = state.gpuModel,
                                    color = OnSurfaceText,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Icon(
                                imageVector = Icons.Filled.Memory,
                                contentDescription = "GPU Icon",
                                tint = PrimaryNeonGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // GPU Stats Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            MetricBox(label = "Carga Total".tr(), value = "${state.gpuLoad}%", color = PrimaryNeonGreen, modifier = Modifier.weight(1f))
                            MetricBox(label = "Carga 3D".tr(), value = "${state.gpuLoad3d}%", color = Color(0xFF8B5CF6), modifier = Modifier.weight(1f))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            MetricBox(label = "Encode".tr(), value = "${state.gpuVideoEncode}%", color = Color(0xFFF59E0B), modifier = Modifier.weight(1f))
                            MetricBox(label = "Decode".tr(), value = "${state.gpuVideoDecode}%", color = Color(0xFF06B6D4), modifier = Modifier.weight(1f))
                        }
                        
                        // VRAM
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceContainerLow, shape = RoundedCornerShape(8.dp))
                                .border(BorderStroke(1.dp, BorderSlate.copy(alpha = 0.5f)), shape = RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Memoria VRAM".tr(),
                                color = OnSurfaceTextVariant,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${state.gpuVramUsedGb} GB / ${state.gpuVramTotalGb} GB",
                                color = DataBlue,
                                fontSize = 16.sp, // larger size (was 13.sp)
                                fontWeight = FontWeight.Bold,
                                style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum")
                            )
                        }

                        // Sparkline Chart (Vertical fluctuation) - compacted height
                        SparklineTelemetryLineChart(
                            history = state.gpuSparklineHistory,
                            strokeColor = PrimaryNeonGreen,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        )

                        // GPU Temperature Warning Capsule
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceContainerLow, shape = RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Thermostat,
                                    contentDescription = "temp warning",
                                    tint = StatusWarning,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Temperatura".tr(),
                                    color = OnSurfaceText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = formatTemp(state.gpuTemp, state.tempUnit),
                                color = OnSurfaceText,
                                fontSize = 28.sp, // larger size
                                fontWeight = FontWeight.Bold,
                                style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum")
                            )
                        }

                        if (state.fps > 0.0) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SurfaceContainerLow, shape = RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Memory,
                                        contentDescription = "FPS Icon",
                                        tint = PrimaryNeonGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Rendimiento OSD (RTSS)".tr(),
                                        color = OnSurfaceText,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Text(
                                    text = "${state.fps.toInt()} FPS",
                                    color = PrimaryNeonGreen,
                                    fontSize = 28.sp, // larger size
                                    fontWeight = FontWeight.Bold,
                                    style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum")
                                )
                            }
                        }
                    }
                }

                // 3. RAM Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderSlate)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp), // compacted padding
                        verticalArrangement = Arrangement.spacedBy(10.dp) // compacted spacing
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MEMORIA RAM".tr(),
                                color = OnSurfaceTextVariant,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // RAM detail block
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceContainerLow, shape = RoundedCornerShape(8.dp))
                                .border(BorderStroke(1.dp, BorderSlate.copy(alpha = 0.5f)), shape = RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                val pct = if (state.ramTotalGb > 0) ((state.ramUsedGb / state.ramTotalGb) * 100).toInt() else 0
                                Text(
                                    text = "RAM en Uso".tr(),
                                    color = OnSurfaceTextVariant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${state.ramUsedGb} GB / ${state.ramTotalGb.toInt()} GB ($pct%)",
                                    color = PrimaryNeonGreen,
                                    fontSize = 19.sp, // larger size (was 16.sp)
                                    fontWeight = FontWeight.Bold,
                                    style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum")
                                )
                            }
                        }

                        // Sparkline Chart (Vertical fluctuation) - compacted height
                        SparklineTelemetryLineChart(
                            history = state.ramSparklineHistory,
                            strokeColor = PrimaryNeonGreen,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        )
                    }
                }

                // 4. Atmospheric / Motor Invisible Card - compacted height (was 112.dp)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp),
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
                            .padding(12.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Motor Invisible".tr(),
                                color = OnSurfaceText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Análisis de flujo térmico en tiempo real habilitado.".tr(),
                                color = OnSurfaceTextVariant,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(SurfaceContainerLow, shape = RoundedCornerShape(8.dp))
            .border(BorderStroke(1.dp, BorderSlate.copy(alpha = 0.5f)), shape = RoundedCornerShape(8.dp))
            .padding(vertical = 6.dp, horizontal = 4.dp), // compacted padding (was 10.dp)
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(1.dp) // compacted spacing
    ) {
        Text(
            text = label,
            color = OnSurfaceTextVariant,
            fontSize = 10.sp, // larger labels (was 9.sp)
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            color = color,
            fontSize = 18.sp, // larger value size (was 12.sp)
            fontWeight = FontWeight.ExtraBold, // bolder font
            style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum")
        )
    }
}
