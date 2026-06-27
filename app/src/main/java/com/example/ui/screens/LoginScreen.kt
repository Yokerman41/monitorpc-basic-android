package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.MonitorViewModel
import com.example.ui.utils.tr

@Composable
fun LoginScreen(
    viewModel: MonitorViewModel,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }

    // Infinite transition for the logo animation
    val infiniteTransition = rememberInfiniteTransition(label = "logoAnimation")
    
    // Soft swaying (rotation)
    val rotation by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoRotation"
    )

    // Soft breathing (scale)
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
        // Futuristic abstract glowing background dots
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(PrimaryNeonGreen.copy(alpha = 0.05f), Color.Transparent),
                        radius = 800f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // App Logo Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(180.dp) // Large circular container
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            rotationZ = rotation
                        }
                        .background(PrimaryNeonGreen.copy(alpha = 0.03f), shape = CircleShape)
                        .border(1.dp, PrimaryNeonGreen.copy(alpha = 0.2f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.app_icon_image),
                        contentDescription = "Logo monitorPC",
                        modifier = Modifier
                            .size(150.dp) // Adjusted size for the new metallic ribbon shape
                            .padding(8.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                }
                Text(
                    text = "monitorPC",
                    color = OnSurfaceText,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Monitoreo del sistema en tiempo real".tr(),
                    color = OnSurfaceTextVariant,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Glassmorphic Login Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderSlate)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (isRegistering) "CREAR CUENTA".tr() else "INICIAR SESIÓN".tr(),
                        color = OnSurfaceTextVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    // Username Input Field
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Usuario".tr(),
                            color = OnSurfaceTextVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        TextField(
                            value = username,
                            onValueChange = {
                                username = it
                                showError = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = "User icon",
                                    tint = OnSurfaceTextVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            placeholder = { Text("admin", color = OnSurfaceTextVariant.copy(alpha = 0.3f)) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = SurfaceContainerLowest,
                                unfocusedContainerColor = SurfaceContainerLowest,
                                focusedIndicatorColor = PrimaryNeonGreen,
                                unfocusedIndicatorColor = BorderSlate,
                                focusedTextColor = OnSurfaceText,
                                unfocusedTextColor = OnSurfaceText
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }

                    // Password Input Field
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Contraseña".tr(),
                            color = OnSurfaceTextVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        TextField(
                            value = password,
                            onValueChange = {
                                password = it
                                showError = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = "Password lock icon",
                                    tint = OnSurfaceTextVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            placeholder = { Text("••••••••", color = OnSurfaceTextVariant.copy(alpha = 0.3f)) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = SurfaceContainerLowest,
                                unfocusedContainerColor = SurfaceContainerLowest,
                                focusedIndicatorColor = PrimaryNeonGreen,
                                unfocusedIndicatorColor = BorderSlate,
                                focusedTextColor = OnSurfaceText,
                                unfocusedTextColor = OnSurfaceText
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }

                    // Error Notification Banner
                    AnimatedVisibility(
                        visible = showError,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFDAD6), shape = RoundedCornerShape(8.dp))
                                .border(BorderStroke(1.dp, StatusCritical.copy(alpha = 0.5f)), shape = RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = "Error warning icon",
                                tint = Color(0xFF690005),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = errorMessage,
                                color = Color(0xFF690005),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Submit Button
                    Button(
                        onClick = {
                            if (username.isEmpty() || password.isEmpty()) {
                                errorMessage = "Por favor, llena todos los campos.".tr()
                                showError = true
                            } else {
                                if (isRegistering) {
                                    viewModel.register(username, password)
                                } else {
                                    viewModel.login(username, password) { success ->
                                        if (!success) {
                                            errorMessage = "Credenciales incorrectas.".tr()
                                            showError = true
                                        }
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeonGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isRegistering) "REGISTRARSE".tr() else "INICIAR SESIÓN".tr(),
                            color = OnPrimaryGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }

                    // Toggle mode text
                    Text(
                        text = if (isRegistering) "¿Ya tienes una cuenta? Iniciar Sesión".tr() else "¿No tienes una cuenta? Regístrate".tr(),
                        color = PrimaryNeonGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isRegistering = !isRegistering
                                showError = false
                            }
                            .padding(top = 8.dp)
                    )
                }
            }

            // Bottom security notice
            Text(
                text = "Conexión encriptada mediante la red local del PC".tr(),
                color = OnSurfaceTextVariant.copy(alpha = 0.5f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
