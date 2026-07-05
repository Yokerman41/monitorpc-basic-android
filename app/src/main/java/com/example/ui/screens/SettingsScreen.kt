package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.MonitorViewModel
import com.example.viewmodel.TestStatus
import com.example.ui.utils.tr
import kotlinx.coroutines.launch
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf

data class ThemePreset(
    val id: String,
    val name: String,
    val json: String,
    val primaryColor: Color,
    val backgroundColor: Color,
    val secondaryColor: Color
)

private val themePresets = listOf(
    ThemePreset(
        id = "default",
        name = "Por Defecto",
        json = "",
        primaryColor = Color(0xFF4EDE9D), // PrimaryNeonGreen
        backgroundColor = Color(0xFF131315), // Background
        secondaryColor = Color(0xFFADC6FF)  // SecondaryBlue
    ),
    ThemePreset(
        id = "cyberpunk_overclock",
        name = "Cyberpunk Overclock",
        json = "{\"primary\":\"#00F5FF\", \"background\":\"#0A0B0E\", \"surface\":\"#1E222B\", \"onSurfaceVariant\":\"#64748B\", \"secondary\":\"#FF007F\", \"normal\":\"#00F5FF\", \"alerta\":\"#FF007F\", \"critico\":\"#FF3333\"}",
        primaryColor = Color(0xFF00F5FF),
        backgroundColor = Color(0xFF0A0B0E),
        secondaryColor = Color(0xFFFF007F)
    ),
    ThemePreset(
        id = "matrix_terminal",
        name = "Matrix Terminal",
        json = "{\"primary\":\"#39FF14\", \"background\":\"#050705\", \"surface\":\"#141F14\", \"onSurfaceVariant\":\"#4A6B4A\", \"secondary\":\"#4A6B4A\", \"normal\":\"#39FF14\", \"alerta\":\"#7FFF00\", \"critico\":\"#FFCC00\"}",
        primaryColor = Color(0xFF39FF14),
        backgroundColor = Color(0xFF050705),
        secondaryColor = Color(0xFF4A6B4A)
    ),
    ThemePreset(
        id = "stealth_obsidian",
        name = "Stealth Obsidian",
        json = "{\"primary\":\"#FFFFFF\", \"background\":\"#121214\", \"surface\":\"#1A1A1E\", \"onSurfaceVariant\":\"#94A3B8\", \"secondary\":\"#94A3B8\", \"normal\":\"#E2E8F0\", \"alerta\":\"#F59E0B\", \"critico\":\"#EF4444\"}",
        primaryColor = Color(0xFFFFFFFF),
        backgroundColor = Color(0xFF121214),
        secondaryColor = Color(0xFF94A3B8)
    ),
    ThemePreset(
        id = "solarized_deep",
        name = "Solarized Deep",
        json = "{\"primary\":\"#2AA198\", \"background\":\"#002B36\", \"surface\":\"#073642\", \"onSurfaceVariant\":\"#586E75\", \"secondary\":\"#268BD2\", \"normal\":\"#268BD2\", \"alerta\":\"#B58900\", \"critico\":\"#CB4B16\"}",
        primaryColor = Color(0xFF2AA198),
        backgroundColor = Color(0xFF002B36),
        secondaryColor = Color(0xFF268BD2)
    ),
    ThemePreset(
        id = "vaporwave_retro",
        name = "Vaporwave Retro",
        json = "{\"primary\":\"#F72585\", \"background\":\"#1A0B2E\", \"surface\":\"#2D124D\", \"onSurfaceVariant\":\"#9D4EDD\", \"secondary\":\"#4CC9F0\", \"normal\":\"#7209B7\", \"alerta\":\"#4CC9F0\", \"critico\":\"#F72585\"}",
        primaryColor = Color(0xFFF72585),
        backgroundColor = Color(0xFF1A0B2E),
        secondaryColor = Color(0xFF4CC9F0)
    )
)

@Composable
fun SettingsScreen(
    viewModel: MonitorViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isGeneralExpanded by remember { mutableStateOf(false) }
    var isAlertsExpanded by remember { mutableStateOf(false) }
    var isThemeExpanded by remember { mutableStateOf(false) }

    // Spinning loader rotation animation for Testing state
    val infiniteTransition = rememberInfiniteTransition(label = "spinTransition")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "spinAngle"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Background)
                .verticalScroll(scrollState)
                .padding(bottom = 120.dp) // extra padding to keep layouts visible of bottom elements
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

                Icon(
                    imageVector = Icons.Filled.Sensors,
                    contentDescription = "sensors settings indicator",
                    tint = OnSurfaceTextVariant,
                    modifier = Modifier.size(22.dp)
                )
            }

            // ---- Settings Body Context ----
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section Title Block
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Configuración de Conexión".tr(),
                        color = OnSurfaceText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "AJUSTES DE RED DEL MOTOR DE MONITOREO".tr(),
                        color = OnSurfaceTextVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }

                // Shortcut button to computers management screen
                OutlinedButton(
                    onClick = { viewModel.setShowComputersScreen(true) },
                    border = BorderStroke(1.dp, PrimaryNeonGreen.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryNeonGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Monitor,
                        contentDescription = "gestionar equipos",
                        tint = PrimaryNeonGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GESTIONAR EQUIPOS / SERVIDORES".tr(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                // Selector de modo de conexión
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderSlate)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Modo de Conexión".tr(),
                            color = OnSurfaceText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val isWifi = state.connectionMode == "WIFI"
                            
                            // Botón WIFI
                            Button(
                                onClick = { viewModel.updateConnectionMode("WIFI") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isWifi) PrimaryNeonGreen else SurfaceContainerLowest,
                                    contentColor = if (isWifi) Background else OnSurfaceText
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, if (isWifi) Color.Transparent else BorderSlate)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Wifi,
                                    contentDescription = "wifi mode icon",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "WiFi".tr(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            
                            // Botón USB
                            Button(
                                onClick = { viewModel.updateConnectionMode("ADB") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (!isWifi) SecondaryBlue else SurfaceContainerLowest,
                                    contentColor = if (!isWifi) Background else OnSurfaceText
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, if (!isWifi) Color.Transparent else BorderSlate)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Usb,
                                    contentDescription = "usb mode icon",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "USB (ADB)".tr(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                val configuration = LocalConfiguration.current
                val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                val isWideScreen = configuration.screenWidthDp >= 600
                val useTwoColumns = isLandscape || isWideScreen

                if (useTwoColumns) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Left Column: Menu headers list
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SettingsMenuHeaderCard(
                                title = "Preferencias Generales".tr(),
                                icon = Icons.Filled.Sensors,
                                iconColor = SecondaryBlue,
                                subtitle = "Unidad de temperatura y auto-apagado".tr(),
                                isSelected = isGeneralExpanded,
                                onClick = {
                                    isGeneralExpanded = !isGeneralExpanded
                                    if (isGeneralExpanded) {
                                        isAlertsExpanded = false
                                        isThemeExpanded = false
                                    }
                                }
                            )
                            
                            SettingsMenuHeaderCard(
                                title = "Umbrales de Alerta".tr(),
                                icon = Icons.Filled.Warning,
                                iconColor = StatusWarning,
                                subtitle = "Límites de temperatura y carga para notificaciones".tr(),
                                isSelected = isAlertsExpanded,
                                onClick = {
                                    isAlertsExpanded = !isAlertsExpanded
                                    if (isAlertsExpanded) {
                                        isGeneralExpanded = false
                                        isThemeExpanded = false
                                    }
                                }
                            )

                            SettingsMenuHeaderCard(
                                title = "Personalización Estética".tr(),
                                icon = Icons.Filled.Palette,
                                iconColor = PrimaryNeonGreen,
                                subtitle = "Selecciona un tema para toda la aplicación".tr(),
                                isSelected = isThemeExpanded,
                                onClick = {
                                    isThemeExpanded = !isThemeExpanded
                                    if (isThemeExpanded) {
                                        isGeneralExpanded = false
                                        isAlertsExpanded = false
                                    }
                                }
                            )
                        }

                        // Right Column: Expanded content details view
                        Column(
                            modifier = Modifier.weight(1.2f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            val anyExpanded = isGeneralExpanded || isAlertsExpanded || isThemeExpanded
                            if (anyExpanded) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, BorderSlate)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        if (isGeneralExpanded) {
                                            Text(
                                                text = "Preferencias Generales".tr(),
                                                color = OnSurfaceText,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            GeneralPreferencesContent(state, viewModel)
                                        } else if (isAlertsExpanded) {
                                            Text(
                                                text = "Umbrales de Alerta".tr(),
                                                color = OnSurfaceText,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            AlertThresholdsContent(state, viewModel)
                                        } else if (isThemeExpanded) {
                                            Text(
                                                text = "Personalización Estética".tr(),
                                                color = OnSurfaceText,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            AestheticPersonalizationContent(state, viewModel)
                                        }
                                    }
                                }
                            } else {
                                // Default details instructions placeholder card
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, BorderSlate.copy(alpha = 0.5f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Selecciona una sección de configuración a la izquierda para ver su contenido.".tr(),
                                            color = OnSurfaceTextVariant,
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    GeneralPreferencesSection(
                        state = state,
                        viewModel = viewModel,
                        isExpanded = isGeneralExpanded,
                        onToggleExpand = { isGeneralExpanded = !isGeneralExpanded }
                    )
                    AlertThresholdsSection(
                        state = state,
                        viewModel = viewModel,
                        isExpanded = isAlertsExpanded,
                        onToggleExpand = { isAlertsExpanded = !isAlertsExpanded }
                    )
                    AestheticPersonalizationSection(
                        state = state,
                        viewModel = viewModel,
                        isExpanded = isThemeExpanded,
                        onToggleExpand = { isThemeExpanded = !isThemeExpanded }
                    )
                }

                // Global Action buttons
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Guardar button
                    Button(
                        onClick = {
                            viewModel.saveConfig()
                            scope.launch {
                                snackbarHostState.showSnackbar("Configuración guardada en base de datos".tr())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeonGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = "Disk save configurations indicator icon",
                            tint = OnPrimaryGreen,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "GUARDAR CONFIGURACIÓN".tr(),
                            color = OnPrimaryGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }

                    // Test connection button
                    val isTesting = state.testConnectionRunStatus is TestStatus.TESTING
                    OutlinedButton(
                        onClick = {
                            if (!isTesting) {
                                viewModel.testConnection()
                            }
                        },
                        border = BorderStroke(1.dp, BorderSlate),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurfaceText),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isTesting) {
                            Icon(
                                imageVector = Icons.Filled.Sync,
                                contentDescription = "testing status connection indicator spinner",
                                tint = PrimaryNeonGreen,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .rotate(rotationAngle)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Router,
                                contentDescription = "wifi signal host check connection button mark",
                                tint = OnSurfaceTextVariant,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                        Text(
                            text = if (isTesting) "PROBANDO CONEXIÓN...".tr() else "TEST CONNECTION".tr(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }

                    // Connection feedback panel
                    val showFeedbackPanel = state.testConnectionRunStatus is TestStatus.SUCCESS ||
                        state.testConnectionRunStatus is TestStatus.FAILURE
                    AnimatedVisibility(
                        visible = showFeedbackPanel,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        val status = state.testConnectionRunStatus
                        val feedbackColor = if (status is TestStatus.SUCCESS) StatusHealthy else StatusWarning
                        val feedbackIcon = if (status is TestStatus.SUCCESS)
                            Icons.Filled.CheckCircle else Icons.Filled.Warning
                        val feedbackMsg = when (status) {
                            is TestStatus.SUCCESS -> status.message
                            is TestStatus.FAILURE -> status.message
                            else -> ""
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceContainerLowest, shape = RoundedCornerShape(8.dp))
                                .border(
                                    BorderStroke(1.5.dp, feedbackColor),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = feedbackIcon,
                                contentDescription = "Status icon",
                                tint = feedbackColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = feedbackMsg,
                                color = feedbackColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 96.dp)
        ) { data ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, BorderSlate),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "info snap",
                        tint = PrimaryNeonGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(text = data.visuals.message, color = OnSurfaceText, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun GeneralPreferencesContent(
    state: com.example.viewmodel.MonitorUiState,
    viewModel: MonitorViewModel
) {
    CustomDropdown(
        label = "Unidad de Temperatura".tr(),
        selectedValue = if (state.tempUnit == "C") "Celsius (°C)" else "Fahrenheit (°F)",
        options = listOf(
            "C" to "Celsius (°C)",
            "F" to "Fahrenheit (°F)"
        ),
        onValueChange = { viewModel.updateTempUnit(it) }
    )
}

@Composable
private fun GeneralPreferencesSection(
    state: com.example.viewmodel.MonitorUiState,
    viewModel: MonitorViewModel,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    CollapsibleCard(
        title = "Preferencias Generales".tr(),
        icon = Icons.Filled.Sensors,
        iconColor = SecondaryBlue,
        subtitle = "Unidad de temperatura y auto-apagado".tr(),
        isExpanded = isExpanded,
        onToggleExpand = onToggleExpand
    ) {
        GeneralPreferencesContent(state, viewModel)
    }
}

@Composable
private fun AlertThresholdsContent(
    state: com.example.viewmodel.MonitorUiState,
    viewModel: MonitorViewModel
) {
    // CPU Temp Slider
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Temp. Máxima CPU".tr(),
                color = OnSurfaceTextVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${state.cpuTempLimit}°C",
                color = PrimaryNeonGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = state.cpuTempLimit.toFloat(),
            onValueChange = { viewModel.updateCpuTempLimit(it.toInt()) },
            valueRange = 50f..100f,
            colors = SliderDefaults.colors(
                thumbColor = PrimaryNeonGreen,
                activeTrackColor = PrimaryNeonGreen,
                inactiveTrackColor = SurfaceContainerLowest
            )
        )
    }

    // CPU Load Slider
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Carga Máxima CPU".tr(),
                color = OnSurfaceTextVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${state.cpuLoadLimit}%",
                color = PrimaryNeonGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = state.cpuLoadLimit.toFloat(),
            onValueChange = { viewModel.updateCpuLoadLimit(it.toInt()) },
            valueRange = 50f..100f,
            colors = SliderDefaults.colors(
                thumbColor = PrimaryNeonGreen,
                activeTrackColor = PrimaryNeonGreen,
                inactiveTrackColor = SurfaceContainerLowest
            )
        )
    }

    // GPU Temp Slider
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Temp. Máxima GPU".tr(),
                color = OnSurfaceTextVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${state.gpuTempLimit}°C",
                color = SecondaryBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = state.gpuTempLimit.toFloat(),
            onValueChange = { viewModel.updateGpuTempLimit(it.toInt()) },
            valueRange = 50f..100f,
            colors = SliderDefaults.colors(
                thumbColor = SecondaryBlue,
                activeTrackColor = SecondaryBlue,
                inactiveTrackColor = SurfaceContainerLowest
            )
        )
    }

    // GPU Load Slider
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Carga Máxima GPU".tr(),
                color = OnSurfaceTextVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${state.gpuLoadLimit}%",
                color = SecondaryBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = state.gpuLoadLimit.toFloat(),
            onValueChange = { viewModel.updateGpuLoadLimit(it.toInt()) },
            valueRange = 50f..100f,
            colors = SliderDefaults.colors(
                thumbColor = SecondaryBlue,
                activeTrackColor = SecondaryBlue,
                inactiveTrackColor = SurfaceContainerLowest
            )
        )
    }
}

@Composable
private fun AlertThresholdsSection(
    state: com.example.viewmodel.MonitorUiState,
    viewModel: MonitorViewModel,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    CollapsibleCard(
        title = "Umbrales de Alerta".tr(),
        icon = Icons.Filled.Warning,
        iconColor = StatusWarning,
        subtitle = "Límites de temperatura y carga para notificaciones".tr(),
        isExpanded = isExpanded,
        onToggleExpand = onToggleExpand
    ) {
        AlertThresholdsContent(state, viewModel)
    }
}

@Composable
private fun SecurityContent(
    state: com.example.viewmodel.MonitorUiState,
    viewModel: MonitorViewModel
) {
    // Basic Auth Username Text Field
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Usuario (Basic Auth)".tr(),
            color = OnSurfaceTextVariant,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        TextField(
            value = state.username,
            onValueChange = { viewModel.updateUsername(it) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "secure lock user config icon",
                    tint = OnSurfaceTextVariant,
                    modifier = Modifier.size(16.dp)
                )
            },
            placeholder = { Text("admin", color = OnSurfaceTextVariant.copy(alpha = 0.3f)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceContainerLowest,
                unfocusedContainerColor = SurfaceContainerLowest,
                focusedIndicatorColor = SecondaryBlue,
                unfocusedIndicatorColor = BorderSlate,
                focusedTextColor = OnSurfaceText,
                unfocusedTextColor = OnSurfaceText
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
    }

    // Password Settings Input
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Contraseña".tr(),
            color = OnSurfaceTextVariant,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        TextField(
            value = state.password,
            onValueChange = { viewModel.updatePassword(it) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "secure password block config icon",
                    tint = OnSurfaceTextVariant,
                    modifier = Modifier.size(16.dp)
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            placeholder = { Text("••••••••", color = OnSurfaceTextVariant.copy(alpha = 0.3f)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceContainerLowest,
                unfocusedContainerColor = SurfaceContainerLowest,
                focusedIndicatorColor = SecondaryBlue,
                unfocusedIndicatorColor = BorderSlate,
                focusedTextColor = OnSurfaceText,
                unfocusedTextColor = OnSurfaceText
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
    }

    // PIN Pairing Section
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainerLow, shape = RoundedCornerShape(12.dp))
            .border(1.dp, BorderSlate, shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(
                    imageVector = Icons.Filled.Shield,
                    contentDescription = "Pairing icon",
                    tint = PrimaryNeonGreen,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Vincular con PIN".tr(),
                    color = OnSurfaceText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (state.apiKey.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Linked",
                    tint = StatusHealthy,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Text(
            text = "Ingresa el PIN de 6 dígitos que aparece en tu PC".tr(),
            color = OnSurfaceTextVariant,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = state.pairingPin,
                onValueChange = { viewModel.updatePairingPin(it) },
                modifier = Modifier.weight(1f),
                placeholder = { Text("000000", color = OnSurfaceTextVariant.copy(alpha = 0.3f)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SurfaceContainerLowest,
                    unfocusedContainerColor = SurfaceContainerLowest,
                    focusedIndicatorColor = PrimaryNeonGreen,
                    unfocusedIndicatorColor = BorderSlate,
                    focusedTextColor = OnSurfaceText,
                    unfocusedTextColor = OnSurfaceText
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                )
            )

            Button(
                onClick = { viewModel.pairWithPin() },
                enabled = state.pairingPin.length == 6 && !state.isPairing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryNeonGreen,
                    disabledContainerColor = SurfaceVariant
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(54.dp)
            ) {
                if (state.isPairing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = OnPrimaryGreen,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "VINCULAR".tr(),
                        color = OnPrimaryGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (state.pairingError != null) {
            Text(
                text = state.pairingError!!.tr(),
                color = StatusCritical,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        var showManualKey by remember { mutableStateOf(false) }
        Column {
            Text(
                text = if (showManualKey) "Ocultar API Key".tr() else "Ver API Key actual".tr(),
                color = OnSurfaceTextVariant,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { showManualKey = !showManualKey }
                    .padding(vertical = 4.dp)
            )
            
            if (showManualKey) {
                TextField(
                    value = state.apiKey,
                    onValueChange = { viewModel.updateApiKey(it) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    textStyle = TextStyle(fontSize = 10.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Black.copy(alpha = 0.2f),
                        unfocusedContainerColor = Color.Black.copy(alpha = 0.2f)
                    ),
                    readOnly = true
                )
            }
        }
    }
}

@Composable
private fun SecuritySection(
    state: com.example.viewmodel.MonitorUiState,
    viewModel: MonitorViewModel,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    CollapsibleCard(
        title = "Seguridad".tr(),
        icon = Icons.Filled.Shield,
        iconColor = SecondaryBlue,
        subtitle = "Usuario y contraseña para autenticación básica".tr(),
        isExpanded = isExpanded,
        onToggleExpand = onToggleExpand
    ) {
        SecurityContent(state, viewModel)
    }
}

@Composable
private fun AestheticPersonalizationContent(
    state: com.example.viewmodel.MonitorUiState,
    viewModel: MonitorViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Temas Disponibles".tr(),
            color = OnSurfaceText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        themePresets.forEach { preset ->
            val isSelected = (preset.json == state.customThemeColors) ||
                    (preset.id == "default" && state.customThemeColors.isEmpty())
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.updateCustomThemeColors(preset.json) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) SurfaceContainerLowest else SurfaceContainerLow
                ),
                border = BorderStroke(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) PrimaryNeonGreen else BorderSlate
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = preset.name,
                        color = if (isSelected) PrimaryNeonGreen else OnSurfaceText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(preset.backgroundColor)
                                .border(0.5.dp, OnSurfaceTextVariant.copy(alpha = 0.5f), CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(preset.primaryColor)
                        )
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(preset.secondaryColor)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AestheticPersonalizationSection(
    state: com.example.viewmodel.MonitorUiState,
    viewModel: MonitorViewModel,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    CollapsibleCard(
        title = "Personalización Estética".tr(),
        icon = Icons.Filled.Palette,
        iconColor = PrimaryNeonGreen,
        subtitle = "Selecciona un tema para toda la aplicación".tr(),
        isExpanded = isExpanded,
        onToggleExpand = onToggleExpand
    ) {
        AestheticPersonalizationContent(state, viewModel)
    }
}

@Composable
fun SettingsMenuHeaderCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    subtitle: String? = null,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SurfaceContainerLowest else SurfaceCharcoal
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) PrimaryNeonGreen else BorderSlate
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "$title icon",
                    tint = if (isSelected) PrimaryNeonGreen else iconColor,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = title,
                        color = if (isSelected) PrimaryNeonGreen else OnSurfaceText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            color = OnSurfaceTextVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = "select indicator",
                tint = if (isSelected) PrimaryNeonGreen else OnSurfaceTextVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun CollapsibleCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    subtitle: String? = null,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderSlate)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "$title icon",
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = title,
                            color = OnSurfaceText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                color = OnSurfaceTextVariant,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = OnSurfaceTextVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun CustomDropdown(
    label: String,
    selectedValue: String,
    options: List<Pair<String, String>>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            color = OnSurfaceTextVariant,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLowest, shape = RoundedCornerShape(8.dp))
                    .border(1.dp, BorderSlate, shape = RoundedCornerShape(8.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedValue,
                    color = OnSurfaceText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "dropdown arrow",
                    tint = OnSurfaceTextVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(SurfaceCharcoal)
                    .border(1.dp, BorderSlate, shape = RoundedCornerShape(8.dp))
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option.second,
                                color = OnSurfaceText,
                                fontSize = 14.sp
                            )
                        },
                        onClick = {
                            onValueChange(option.first)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
