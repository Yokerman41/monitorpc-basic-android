package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ConnectedTv
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Tv
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
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import com.example.viewmodel.MonitorViewModel
import com.example.ui.utils.tr

@Composable
fun TutorialDialog(
    viewModel: MonitorViewModel,
    onDismiss: () -> Unit
) {
    var currentSlide by remember { mutableStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BorderSlate)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Indicator dots on top
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    repeat(3) { index ->
                        val dotColor = if (index == currentSlide) PrimaryNeonGreen else BorderSlate
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(dotColor, shape = CircleShape)
                        )
                    }
                }

                // Slide contents switcher
                when (currentSlide) {
                    0 -> {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(PrimaryNeonGreen.copy(alpha = 0.1f), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Monitor,
                                contentDescription = "Monitor icon",
                                tint = PrimaryNeonGreen,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Text(
                            text = "¡Bienvenido a monitorPC!".tr(),
                            color = OnSurfaceText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Esta aplicación te permite monitorear el estado, temperaturas y rendimiento de tu computadora directamente desde tu dispositivo Android de forma remota.".tr(),
                            color = OnSurfaceTextVariant,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                    1 -> {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(SecondaryBlue.copy(alpha = 0.1f), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Router,
                                contentDescription = "Connection icon",
                                tint = SecondaryBlue,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Text(
                            text = "Conecta tu Computadora".tr(),
                            color = OnSurfaceText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = ("1. Ejecuta el archivo 'run_agent.bat' en tu PC.\n" +
                                   "2. Abre la sección de Ajustes (Settings) en la app.\n" +
                                   "3. Ingresa la dirección IP de tu computadora para vincular los datos en tiempo real.").tr(),
                            color = OnSurfaceTextVariant,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Start,
                            lineHeight = 18.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    2 -> {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(PrimaryNeonGreen.copy(alpha = 0.1f), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Tv,
                                contentDescription = "Mirror control icon",
                                tint = PrimaryNeonGreen,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Text(
                            text = "Telemetría y Almacenamiento".tr(),
                            color = OnSurfaceText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Monitorea la carga, velocidad y temperatura de tu CPU y GPU en tiempo real. Además, analiza el estado de salud de tus discos y memoria RAM de manera sencilla.".tr(),
                            color = OnSurfaceTextVariant,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom control actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentSlide > 0) {
                        TextButton(onClick = { currentSlide-- }) {
                            Text(text = "Atrás".tr(), color = OnSurfaceTextVariant)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(48.dp))
                    }

                    Button(
                        onClick = {
                            if (currentSlide < 2) {
                                currentSlide++
                            } else {
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeonGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (currentSlide < 2) "Siguiente".tr() else "Comenzar".tr(),
                                color = OnPrimaryGreen,
                                fontWeight = FontWeight.Bold
                            )
                            if (currentSlide < 2) {
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = "Next",
                                    tint = OnPrimaryGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
