package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ComputerEntity
import com.example.ui.theme.*
import com.example.viewmodel.MonitorViewModel
import com.example.ui.utils.tr

@Composable
fun ComputersScreen(
    viewModel: MonitorViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var showAddDialog by remember { mutableStateOf(false) }
    var computerToEdit by remember { mutableStateOf<ComputerEntity?>(null) }
    val limitReached = state.computers.size >= 1

    // Refresh discovery every time the screen is opened
    LaunchedEffect(Unit) {
        viewModel.refreshDiscovery()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
        ) {
            // ---- TopAppBar Header Section ----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(Background)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { viewModel.setShowComputersScreen(false) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver".tr(),
                        tint = OnSurfaceText
                    )
                }
                Text(
                    text = "Mis Computadores".tr(),
                    color = OnSurfaceText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // ---- Body Section ----
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "VINCULA Y GESTIONA MÚLTIPLES EQUIPOS PARA MONITOREO".tr(),
                    color = OnSurfaceTextVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Discovered Computers Section (Local Discovery)
                if (state.discoveredComputers.isNotEmpty()) {
                    Text(
                        text = "EQUIPOS DETECTADOS EN LA RED".tr(),
                        color = PrimaryNeonGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    if (limitReached) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = StatusWarning.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, StatusWarning.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "Límite de la versión básica: solo puedes registrar una sola PC. Elimina la PC registrada para vincular o agregar otra.".tr(),
                                color = StatusWarning,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                    state.discoveredComputers.forEach { discovered ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .then(
                                    if (!limitReached) {
                                        Modifier.clickable {
                                            computerToEdit = ComputerEntity(
                                                name = discovered.name,
                                                ipAddress = discovered.ip,
                                                port = discovered.port,
                                                username = "admin",
                                                password = "admin"
                                            )
                                        }
                                    } else Modifier
                                ),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, PrimaryNeonGreen.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                PrimaryNeonGreen.copy(alpha = 0.1f),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Monitor,
                                            contentDescription = "Equipo detectado".tr(),
                                            tint = PrimaryNeonGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = discovered.name,
                                            color = OnSurfaceText,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${discovered.ip}:${discovered.port}",
                                            color = OnSurfaceTextVariant,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Vincular".tr(),
                                        color = PrimaryNeonGreen,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Vincular".tr(),
                                        tint = PrimaryNeonGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (state.computers.isEmpty()) {
                    // Empty list placeholder
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, BorderSlate)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(PrimaryNeonGreen.copy(alpha = 0.1f), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Monitor,
                                    contentDescription = "Sin computadores".tr(),
                                    tint = PrimaryNeonGreen,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Text(
                                text = "No tienes computadores agregados".tr(),
                                color = OnSurfaceText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Presiona el botón de abajo para agregar tu primer equipo e ingresar su IP y puerto para iniciar el monitoreo.".tr(),
                                color = OnSurfaceTextVariant,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }
                } else {
                    // List of computers
                    state.computers.forEach { computer ->
                        val isActive = computer.id == state.activeComputerId
                        val borderStroke = if (isActive) BorderStroke(1.5.dp, PrimaryNeonGreen) else BorderStroke(1.dp, BorderSlate)
                        val cardBg = if (isActive) SurfaceCharcoal else SurfaceCharcoal.copy(alpha = 0.7f)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.selectComputer(computer.id) },
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            shape = RoundedCornerShape(12.dp),
                            border = borderStroke
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(
                                                if (isActive) PrimaryNeonGreen.copy(alpha = 0.15f) else SurfaceContainerLow,
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Monitor,
                                            contentDescription = "Dispositivo".tr(),
                                            tint = if (isActive) PrimaryNeonGreen else OnSurfaceTextVariant,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = computer.name,
                                                color = OnSurfaceText,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            if (isActive) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(PrimaryContainerGreen, shape = RoundedCornerShape(100.dp))
                                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "ACTIVO".tr(),
                                                        color = OnPrimaryGreen,
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = "${computer.ipAddress}:${computer.port}",
                                            color = OnSurfaceTextVariant,
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                // Action Buttons (Edit / Delete)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    IconButton(onClick = { computerToEdit = computer }) {
                                        Icon(
                                            imageVector = Icons.Filled.Edit,
                                            contentDescription = "Editar computador".tr(),
                                            tint = SecondaryBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    IconButton(onClick = { viewModel.deleteComputer(computer.id) }) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "Eliminar computador".tr(),
                                            tint = StatusCritical,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ---- Bottom Add Button ----
            Button(
                onClick = { if (!limitReached) showAddDialog = true },
                enabled = !limitReached,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (limitReached) SurfaceContainerLow else PrimaryNeonGreen,
                    disabledContainerColor = SurfaceContainerLow
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (limitReached) {
                    Text(
                        text = "LÍMITE: 1 PC (VERSIÓN BÁSICA)".tr(),
                        color = OnSurfaceTextVariant.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Agregar".tr(),
                        tint = OnPrimaryGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AGREGAR COMPUTADOR".tr(),
                        color = OnPrimaryGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }
            }
        }

        // Dialog overlays
        if (showAddDialog) {
            ComputerFormDialog(
                title = "Agregar Computador".tr(),
                onDismiss = { showAddDialog = false },
                onSave = { name, ip, port, user, pass, key ->
                    viewModel.addComputer(name, ip, port, user, pass, key)
                    showAddDialog = false
                },
                viewModel = viewModel
            )
        }

        computerToEdit?.let { computer ->
            ComputerFormDialog(
                title = if (computer.id == 0) "Agregar Computador".tr() else "Editar Computador".tr(),
                initialComputer = computer,
                onDismiss = { computerToEdit = null },
                onSave = { name, ip, port, user, pass, key ->
                    if (computer.id == 0) {
                        viewModel.addComputer(name, ip, port, user, pass, key)
                    } else {
                        viewModel.updateComputer(computer.id, name, ip, port, user, pass, key)
                    }
                    computerToEdit = null
                },
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun ComputerFormDialog(
    title: String,
    initialComputer: ComputerEntity? = null,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String) -> Unit, // Added String for apiKey
    viewModel: MonitorViewModel
) {
    val state by viewModel.uiState.collectAsState()
    var name by remember { mutableStateOf(initialComputer?.name ?: "") }
    var ip by remember { mutableStateOf(initialComputer?.ipAddress ?: "") }
    var port by remember { mutableStateOf(initialComputer?.port ?: "8765") }
    var username by remember { mutableStateOf(initialComputer?.username ?: "admin") }
    var password by remember { mutableStateOf(initialComputer?.password ?: "admin") }
    var apiKey by remember { mutableStateOf(initialComputer?.apiKey ?: "") }

    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title.tr(),
                color = OnSurfaceText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Name Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; showError = false },
                    label = { Text("Nombre del Equipo".tr(), color = OnSurfaceTextVariant) },
                    placeholder = { Text("Ej: PC Oficina".tr(), color = OnSurfaceTextVariant.copy(alpha = 0.4f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryNeonGreen,
                        unfocusedBorderColor = BorderSlate,
                        focusedTextColor = OnSurfaceText,
                        unfocusedTextColor = OnSurfaceText
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // IP Address
                    OutlinedTextField(
                        value = ip,
                        onValueChange = { ip = it; showError = false },
                        label = { Text("Dirección IP".tr(), color = OnSurfaceTextVariant) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryNeonGreen,
                            unfocusedBorderColor = BorderSlate,
                            focusedTextColor = OnSurfaceText,
                            unfocusedTextColor = OnSurfaceText
                        ),
                        modifier = Modifier.weight(0.7f),
                        singleLine = true
                    )

                    // Port
                    OutlinedTextField(
                        value = port,
                        onValueChange = { port = it; showError = false },
                        label = { Text("Puerto".tr(), color = OnSurfaceTextVariant) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryNeonGreen,
                            unfocusedBorderColor = BorderSlate,
                            focusedTextColor = OnSurfaceText,
                            unfocusedTextColor = OnSurfaceText
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.3f),
                        singleLine = true
                    )
                }

                // --- PIN Pairing Section ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceContainerLow, shape = RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Vincular con PIN".tr(),
                            color = OnSurfaceText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (apiKey.isNotBlank()) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Linked",
                                tint = StatusHealthy,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = state.pairingPin,
                            onValueChange = { viewModel.updatePairingPin(it) },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("000000", fontSize = 14.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryNeonGreen,
                                unfocusedBorderColor = BorderSlate
                            ),
                            singleLine = true
                        )
                        
                        Button(
                            onClick = { 
                                viewModel.pairWithPin(customIp = ip, customPort = port) { key ->
                                    if (key != null) {
                                        apiKey = key
                                    }
                                }
                            },
                            enabled = state.pairingPin.length == 6 && !state.isPairing && ip.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (apiKey.isBlank()) PrimaryNeonGreen else SurfaceVariant
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            if (state.isPairing) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = OnPrimaryGreen, strokeWidth = 2.dp)
                            } else {
                                Text(
                                    if (apiKey.isBlank()) "VINCULAR".tr() else "RE-VINCULAR".tr(),
                                    fontSize = 11.sp, 
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    if (state.pairingError != null) {
                        Text(state.pairingError!!.tr(), color = StatusCritical, fontSize = 10.sp)
                    }
                }

                // API Key (Read-only after pairing, or manual entry)
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("API Key del Agente".tr(), color = OnSurfaceTextVariant) },
                    placeholder = { Text("Se llenará automáticamente al vincular".tr(), fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryNeonGreen,
                        unfocusedBorderColor = BorderSlate,
                        focusedTextColor = PrimaryNeonGreen,
                        unfocusedTextColor = PrimaryNeonGreen
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 10.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                )

                // Username (Basic Auth)
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it; showError = false },
                    label = { Text("Usuario (Agente)".tr(), color = OnSurfaceTextVariant) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SecondaryBlue,
                        unfocusedBorderColor = BorderSlate,
                        focusedTextColor = OnSurfaceText,
                        unfocusedTextColor = OnSurfaceText
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Password / API Key
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; showError = false },
                    label = { Text("Contraseña / API Key".tr(), color = OnSurfaceTextVariant) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SecondaryBlue,
                        unfocusedBorderColor = BorderSlate,
                        focusedTextColor = OnSurfaceText,
                        unfocusedTextColor = OnSurfaceText
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (showError) {
                    Text(
                        text = "Por favor, completa los campos de Nombre e IP.".tr(),
                        color = StatusCritical,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || ip.isBlank()) {
                        showError = true
                    } else {
                        onSave(name.trim(), ip.trim(), port.trim(), username.trim(), password.trim(), apiKey)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeonGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Guardar".tr(), color = OnPrimaryGreen, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancelar".tr(), color = OnSurfaceTextVariant)
            }
        },
        containerColor = SurfaceCharcoal,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 6.dp
    )
}
