package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.MonitorViewModel
import com.example.viewmodel.ProcessItem
import com.example.ui.theme.*
import com.example.ui.utils.tr
import kotlinx.coroutines.delay

@Composable
fun ProcessesScreen(
    viewModel: MonitorViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    
    // Auto-refresh processes every 3 seconds when screen is visible
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.refreshProcesses()
            delay(3000)
        }
    }

    val filteredProcesses = remember(state.processes, state.processesSearchKeyword, state.processesSortByCpu) {
        val filtered = if (state.processesSearchKeyword.isEmpty()) {
            state.processes
        } else {
            state.processes.filter {
                it.name.contains(state.processesSearchKeyword, ignoreCase = true) ||
                it.pid.toString().contains(state.processesSearchKeyword)
            }
        }
        // #14: Sort by CPU or by RAM
        if (state.processesSortByCpu) {
            filtered.sortedByDescending { it.cpu }
        } else {
            filtered.sortedByDescending { it.memoryMb }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Block
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = { viewModel.setShowProcessesScreen(false) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver".tr(),
                        tint = OnSurfaceText
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Administrador de Tareas".tr(),
                        color = OnSurfaceText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "PROCESOS EN TIEMPO REAL".tr(),
                        color = OnSurfaceTextVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }
            }

            // Sort toggle + Refresh
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sort toggle: CPU / RAM
                OutlinedButton(
                    onClick = { viewModel.toggleProcessSortByCpu() },
                    border = BorderStroke(1.dp, if (state.processesSortByCpu) PrimaryNeonGreen else BorderSlate),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (state.processesSortByCpu) PrimaryNeonGreen else OnSurfaceTextVariant
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = if (state.processesSortByCpu) Icons.Filled.Speed else Icons.Filled.Memory,
                        contentDescription = "toggle sort",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = if (state.processesSortByCpu) "CPU".tr() else "RAM".tr(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = { viewModel.refreshProcesses() },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = SurfaceCharcoal,
                        contentColor = PrimaryNeonGreen
                    ),
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, BorderSlate, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Refrescar procesos",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Search Bar
        TextField(
            value = state.processesSearchKeyword,
            onValueChange = { viewModel.updateSearchKeyword(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar por nombre o PID...".tr(), color = OnSurfaceTextVariant.copy(alpha = 0.5f)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Buscar",
                    tint = OnSurfaceTextVariant
                )
            },
            trailingIcon = {
                if (state.processesSearchKeyword.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchKeyword("") }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Limpiar",
                            tint = OnSurfaceTextVariant
                        )
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceCharcoal,
                unfocusedContainerColor = SurfaceCharcoal,
                focusedIndicatorColor = PrimaryNeonGreen,
                unfocusedIndicatorColor = BorderSlate,
                focusedTextColor = OnSurfaceText,
                unfocusedTextColor = OnSurfaceText
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )

        // Summary Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceCharcoal, RoundedCornerShape(8.dp))
                .border(1.dp, BorderSlate, RoundedCornerShape(8.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total Procesos".tr(), color = OnSurfaceTextVariant, fontSize = 11.sp)
                Text(state.totalProcesses.toString(), color = OnSurfaceText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.width(1.dp).height(32.dp).background(BorderSlate))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total Hilos".tr(), color = OnSurfaceTextVariant, fontSize = 11.sp)
                Text(state.totalThreads.toString(), color = OnSurfaceText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.width(1.dp).height(32.dp).background(BorderSlate))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Filtrados".tr(), color = OnSurfaceTextVariant, fontSize = 11.sp)
                Text(filteredProcesses.size.toString(), color = PrimaryNeonGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Processes List
        if (filteredProcesses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontraron procesos activos".tr(),
                    color = OnSurfaceTextVariant,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredProcesses, key = { it.pid }) { process ->
                    ProcessCard(
                        process = process,
                        onKillClick = { viewModel.killProcess(process.pid) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProcessCard(
    process: ProcessItem,
    onKillClick: () -> Unit
) {
    var showKillConfirm by remember { mutableStateOf(false) }

    val icon = when (process.iconName) {
        "browser" -> Icons.Filled.Language
        "terminal" -> Icons.Filled.Terminal
        "code" -> Icons.Filled.Code
        else -> Icons.Filled.SettingsInputComponent
    }

    val statusColor = when (process.status) {
        "High Load" -> StatusCritical
        "Normal" -> StatusWarning
        "Stable" -> StatusHealthy
        else -> OnSurfaceTextVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderSlate, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = process.iconName,
                        tint = PrimaryNeonGreen,
                        modifier = Modifier
                            .size(36.dp)
                            .background(PrimaryNeonGreen.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                            .padding(6.dp)
                    )
                    
                    Column {
                        Text(
                            text = process.name,
                            color = OnSurfaceText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "PID: ${process.pid}",
                            color = OnSurfaceTextVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                // CPU & RAM Metrics Block
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${process.cpu}% CPU",
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${process.memoryMb} MB",
                            color = OnSurfaceTextVariant,
                            fontSize = 12.sp
                        )
                    }

                    IconButton(
                        onClick = { showKillConfirm = !showKillConfirm },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = StatusCritical.copy(alpha = 0.1f),
                            contentColor = StatusCritical
                        ),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteForever,
                            contentDescription = "Finalizar proceso",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = showKillConfirm,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(StatusCritical.copy(alpha = 0.05f), RoundedCornerShape(6.dp))
                        .border(1.dp, StatusCritical.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "¿Finalizar proceso?".tr(),
                        color = StatusCritical,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = { showKillConfirm = false },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .weight(1f)
                                .border(1.dp, BorderSlate, RoundedCornerShape(4.dp))
                        ) {
                            Text("No", color = OnSurfaceText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = {
                                showKillConfirm = false
                                onKillClick()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusCritical),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .weight(1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("Sí", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
