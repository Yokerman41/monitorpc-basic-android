package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.MonitorViewModel
import com.example.viewmodel.ScriptItem
import com.example.ui.theme.*
import com.example.ui.utils.tr
import kotlinx.coroutines.delay

@Composable
fun ScriptsScreen(
    viewModel: MonitorViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.refreshScripts()
    }

    LaunchedEffect(snackbarMessage) {
        if (snackbarMessage != null) {
            delay(2000)
            snackbarMessage = null
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
                IconButton(onClick = { viewModel.setShowScriptsScreen(false) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver".tr(),
                        tint = OnSurfaceText
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Comandos Rápidos".tr(),
                        color = OnSurfaceText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "LANZAR SCRIPTS Y ACCIONES".tr(),
                        color = OnSurfaceTextVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }
            }
            
            IconButton(
                onClick = { viewModel.refreshScripts() },
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
                    contentDescription = "Refrescar scripts",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Info notice card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderSlate.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Información",
                    tint = SecondaryBlue,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Para agregar o editar comandos, modifica el archivo scripts.json en la carpeta del agente de tu PC y presiona refrescar.".tr(),
                    color = OnSurfaceTextVariant,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Scripts Grid List
        if (state.scripts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CodeOff,
                        contentDescription = "No hay scripts",
                        tint = OnSurfaceTextVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No se encontraron scripts disponibles".tr(),
                        color = OnSurfaceTextVariant,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.scripts) { script ->
                    ScriptCard(
                        script = script,
                        onExecuteClick = {
                            viewModel.runScript(script.id) {
                                snackbarMessage = "Comando '${script.name}' ejecutado con éxito".tr()
                            }
                        }
                    )
                }
            }
        }

        // Notification Banner
        AnimatedVisibility(
            visible = snackbarMessage != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryNeonGreen, RoundedCornerShape(8.dp))
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = snackbarMessage ?: "",
                    color = Color(0xFF003824),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ScriptCard(
    script: ScriptItem,
    onExecuteClick: () -> Unit
) {
    val icon = when {
        script.id.contains("lock", ignoreCase = true) -> Icons.Filled.Lock
        script.id.contains("calc", ignoreCase = true) -> Icons.Filled.Calculate
        script.id.contains("notepad", ignoreCase = true) -> Icons.Filled.Description
        script.id.contains("shutdown", ignoreCase = true) -> Icons.Filled.PowerSettingsNew
        script.id.contains("steam", ignoreCase = true) -> Icons.Filled.VideogameAsset
        else -> Icons.Filled.PlayArrow
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .border(1.dp, BorderSlate, RoundedCornerShape(12.dp))
            .clickable {
                onExecuteClick()
            },
        colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = script.id,
                    tint = PrimaryNeonGreen,
                    modifier = Modifier
                        .size(36.dp)
                        .background(PrimaryNeonGreen.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                )

                Icon(
                    imageVector = Icons.Filled.ArrowOutward,
                    contentDescription = "Ejecutar",
                    tint = OnSurfaceTextVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = script.name,
                    color = OnSurfaceText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1
                )
                Text(
                    text = script.command,
                    color = OnSurfaceTextVariant,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}
