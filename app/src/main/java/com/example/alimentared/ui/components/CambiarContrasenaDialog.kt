package com.example.alimentared.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.alimentared.ui.theme.*

@Composable
fun CambiarContrasenaDialog(
    nombreUsuario: String = "Usuario",
    rolUsuario: String = "Donante",
    onDismiss: () -> Unit,
    onCambiarConfirmado: (contrasenaActual: String, contrasenaNueva: String, onExito: () -> Unit, onError: (String) -> Unit) -> Unit
) {
    var contrasenaActual by remember { mutableStateOf("") }
    var contrasenaNueva by remember { mutableStateOf("") }
    var confirmarNueva by remember { mutableStateOf("") }

    var showActualPassword by remember { mutableStateOf(false) }
    var showNuevaPassword by remember { mutableStateOf(false) }
    var showConfirmarPassword by remember { mutableStateOf(false) }

    var isSubmitting by remember { mutableStateOf(false) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    var exitoMensaje by remember { mutableStateOf<String?>(null) }

    val actualValida = contrasenaActual.isNotBlank()
    val longitudValida = contrasenaNueva.length in 8..72
    val alfanumericaValida = contrasenaNueva.any { it.isLetter() } && contrasenaNueva.any { it.isDigit() }
    val coinciden = contrasenaNueva.isNotBlank() && contrasenaNueva == confirmarNueva
    val distintaALaActual = contrasenaNueva.isNotBlank() && contrasenaActual != contrasenaNueva

    val puedeEnviar = actualValida && longitudValida && alfanumericaValida && coinciden && distintaALaActual && !isSubmitting

    val ecoTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = EcoTextPrimary,
        unfocusedTextColor = EcoTextPrimary,
        errorTextColor = EcoTextPrimary,
        focusedContainerColor = EcoSurface,
        unfocusedContainerColor = EcoSurface,
        errorContainerColor = EcoSurface,
        focusedBorderColor = EcoForestGreen,
        unfocusedBorderColor = EcoBorder,
        errorBorderColor = EcoRed,
        focusedLabelColor = EcoForestGreen,
        unfocusedLabelColor = EcoTextMuted,
        errorLabelColor = EcoRed,
        focusedPlaceholderColor = EcoTextMuted,
        unfocusedPlaceholderColor = EcoTextMuted,
        cursorColor = EcoForestGreen,
        errorCursorColor = EcoRed
    )

    Dialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(24.dp),
            color = EcoBackground,
            border = BorderStroke(1.dp, EcoBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. HEADER DEL DIÁLOGO
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EcoForestGreen)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.VpnKey,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Cambiar Contraseña",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                "Cuenta: $nombreUsuario ($rolUsuario)",
                                color = EcoMintLight,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        enabled = !isSubmitting,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }

                // 2. CONTENIDO DEL FORMULARIO
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Por seguridad de tu cuenta, ingresa tu contraseña actual antes de definir tu nueva clave.",
                        fontSize = 12.sp,
                        color = EcoTextMuted,
                        lineHeight = 16.sp
                    )

                    if (errorMensaje != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EcoRedLight,
                            border = BorderStroke(1.dp, EcoRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = EcoRed, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(errorMensaje!!, color = EcoRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    if (exitoMensaje != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EcoMintGreen,
                            border = BorderStroke(1.dp, EcoForestGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(exitoMensaje!!, color = EcoForestGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // CONTRASEÑA ACTUAL
                    OutlinedTextField(
                        value = contrasenaActual,
                        onValueChange = { contrasenaActual = it; errorMensaje = null },
                        label = { Text("Contraseña Actual") },
                        placeholder = { Text("Ingresa tu clave actual") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Password, contentDescription = null, tint = EcoOliveGreen, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = { showActualPassword = !showActualPassword }) {
                                Icon(
                                    if (showActualPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = EcoForestGreen
                                )
                            }
                        },
                        visualTransformation = if (showActualPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ecoTextFieldColors
                    )

                    // NUEVA CONTRASEÑA
                    OutlinedTextField(
                        value = contrasenaNueva,
                        onValueChange = { contrasenaNueva = it; errorMensaje = null },
                        label = { Text("Nueva Contraseña") },
                        placeholder = { Text("Mínimo 8 caracteres, letras y números") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = { showNuevaPassword = !showNuevaPassword }) {
                                Icon(
                                    if (showNuevaPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = EcoForestGreen
                                )
                            }
                        },
                        visualTransformation = if (showNuevaPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ecoTextFieldColors
                    )

                    // CONFIRMAR NUEVA CONTRASEÑA
                    OutlinedTextField(
                        value = confirmarNueva,
                        onValueChange = { confirmarNueva = it; errorMensaje = null },
                        label = { Text("Confirmar Nueva Contraseña") },
                        placeholder = { Text("Repite exactamente la nueva contraseña") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = { showConfirmarPassword = !showConfirmarPassword }) {
                                Icon(
                                    if (showConfirmarPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = EcoForestGreen
                                )
                            }
                        },
                        visualTransformation = if (showConfirmarPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ecoTextFieldColors
                    )

                    // CHECKLIST DE SEGURIDAD
                    Card(
                        colors = CardDefaults.cardColors(containerColor = EcoSurface),
                        border = BorderStroke(1.dp, EcoBorder),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Requisitos de seguridad:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EcoTextPrimary)
                            ChecklistRow(cumplido = actualValida, texto = "Contraseña actual ingresada")
                            ChecklistRow(cumplido = longitudValida, texto = "Mínimo 8 caracteres (máx. 72)")
                            ChecklistRow(cumplido = alfanumericaValida, texto = "Combinación de letras y números")
                            ChecklistRow(cumplido = coinciden, texto = "Las contraseñas coinciden exactamente")
                            ChecklistRow(cumplido = distintaALaActual, texto = "Diferente a la contraseña actual")
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // BOTONES DE ACCIÓN
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            enabled = !isSubmitting,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            border = BorderStroke(1.dp, EcoBorder)
                        ) {
                            Text("Cancelar", color = EcoTextMuted, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                isSubmitting = true
                                errorMensaje = null
                                exitoMensaje = null
                                onCambiarConfirmado(
                                    contrasenaActual,
                                    contrasenaNueva,
                                    {
                                        isSubmitting = false
                                        exitoMensaje = "¡Contraseña actualizada exitosamente!"
                                    },
                                    { error ->
                                        isSubmitting = false
                                        errorMensaje = error
                                    }
                                )
                            },
                            enabled = puedeEnviar,
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = EcoForestGreen),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(46.dp)
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
