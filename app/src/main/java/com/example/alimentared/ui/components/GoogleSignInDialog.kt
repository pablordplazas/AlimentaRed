package com.example.alimentared.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.alimentared.R
import com.example.alimentared.ui.theme.*

@Composable
fun GoogleSignInDialog(
    onDismiss: () -> Unit,
    onCuentaSeleccionada: (idToken: String, correo: String, nombre: String, rol: String) -> Unit,
    esModoDemo: Boolean = false,
    correoInicial: String? = null,
    onElegirCuentaDispositivo: (() -> Unit)? = null
) {
    var mostrarFormularioPersonalizado by remember(esModoDemo) { mutableStateOf(!esModoDemo) }
    var correoPersonalizado by remember(correoInicial) { mutableStateOf(correoInicial ?: "") }
    var nombrePersonalizado by remember(correoInicial) {
        val derivado = if (!correoInicial.isNullOrBlank()) {
            correoInicial.substringBefore("@")
                .replace(".", " ")
                .replace("_", " ")
                .split(" ")
                .filter { it.isNotBlank() }
                .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
        } else ""
        mutableStateOf(derivado)
    }
    var rolPersonalizado by remember { mutableStateOf("DONANTE") }
    var errorValidacion by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ENCABEZADO GOOGLE
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Google Logo",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (esModoDemo) "Acceder con Google" else "Acceder con Google",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF202124)
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = EcoTextMuted, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (esModoDemo) {
                        "Selecciona una cuenta para continuar en AlimentaRed"
                    } else {
                        "Inicia sesión con tu cuenta de Google en AlimentaRed"
                    },
                    fontSize = 13.sp,
                    color = EcoTextMuted,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                if (esModoDemo && !mostrarFormularioPersonalizado) {
                    // MODO DEMO: CUENTAS PRECONFIGURADAS
                    // CUENTA 1: DONANTE GOOGLE
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF8F9FA),
                        border = BorderStroke(1.dp, Color(0xFFE8EAED)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onCuentaSeleccionada(
                                    "google_token_donante_${System.currentTimeMillis()}",
                                    "donante.alimentared@gmail.com",
                                    "Donante AlimentaRed",
                                    "DONANTE"
                                )
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = EcoForestGreen,
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("D", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Donante AlimentaRed",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF202124)
                                )
                                Text(
                                    text = "donante.alimentared@gmail.com",
                                    fontSize = 12.sp,
                                    color = Color(0xFF5F6368)
                                )
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = EcoMintLight,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = "Rol: Donador de Alimentos",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = EcoForestGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // CUENTA 2: RECEPTORA GOOGLE
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF8F9FA),
                        border = BorderStroke(1.dp, Color(0xFFE8EAED)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onCuentaSeleccionada(
                                    "google_token_receptora_${System.currentTimeMillis()}",
                                    "receptora.pan@gmail.com",
                                    "Fundación Pan Compartido",
                                    "RECEPTORA"
                                )
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = EcoOliveGreen,
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("R", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Fundación Pan Compartido",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF202124)
                                )
                                Text(
                                    text = "receptora.pan@gmail.com",
                                    fontSize = 12.sp,
                                    color = Color(0xFF5F6368)
                                )
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = EcoMintLight,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = "Rol: Organización Receptora",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = EcoOliveGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // OPCIÓN: INGRESAR OTRA CUENTA GOOGLE
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, Color(0xFFE8EAED)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { mostrarFormularioPersonalizado = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFF1F3F4),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF5F6368))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Usar otra cuenta de Google",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = Color(0xFF1A73E8)
                            )
                        }
                    }
                } else {
                    // CUENTA REAL DE GOOGLE / FORMULARIO
                    if (!esModoDemo && onElegirCuentaDispositivo != null) {
                        OutlinedButton(
                            onClick = onElegirCuentaDispositivo,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, Color(0xFFDADCE0)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFF8F9FA))
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = EcoForestGreen
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Elegir cuenta del dispositivo",
                                fontSize = 13.sp,
                                color = EcoTextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = nombrePersonalizado,
                        onValueChange = { nombrePersonalizado = it; errorValidacion = null },
                        placeholder = { Text("Nombre completo o de la Organización", fontSize = 13.sp) },
                        label = { Text("Nombre", fontSize = 12.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = correoPersonalizado,
                        onValueChange = { correoPersonalizado = it; errorValidacion = null },
                        placeholder = { Text("ejemplo@gmail.com", fontSize = 13.sp) },
                        label = { Text("Correo Gmail", fontSize = 12.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        "Participar en AlimentaRed como:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EcoTextMuted,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = rolPersonalizado == "DONANTE",
                            onClick = { rolPersonalizado = "DONANTE" },
                            label = { Text("Donante", fontSize = 11.sp, fontWeight = if (rolPersonalizado == "DONANTE") FontWeight.Bold else FontWeight.Normal) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EcoMintLight,
                                selectedLabelColor = EcoForestGreen
                            )
                        )
                        FilterChip(
                            selected = rolPersonalizado == "RECEPTORA",
                            onClick = { rolPersonalizado = "RECEPTORA" },
                            label = { Text("Receptora", fontSize = 11.sp, fontWeight = if (rolPersonalizado == "RECEPTORA") FontWeight.Bold else FontWeight.Normal) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EcoMintLight,
                                selectedLabelColor = EcoForestGreen
                            )
                        )
                    }

                    if (errorValidacion != null) {
                        Text(
                            text = errorValidacion!!,
                            color = EcoRed,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (esModoDemo) {
                                    mostrarFormularioPersonalizado = false
                                } else {
                                    onDismiss()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(if (esModoDemo) "Atrás" else "Cancelar", fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                val c = correoPersonalizado.trim()
                                val n = nombrePersonalizado.trim()
                                if (c.isBlank() || !c.contains("@")) {
                                    errorValidacion = "Ingresa un correo electrónico válido"
                                    return@Button
                                }
                                val nom = if (n.isNotBlank()) n else c.substringBefore("@")
                                val tokenPrefix = if (esModoDemo) "google_token_demo" else "google_token_real"
                                onCuentaSeleccionada(
                                    "${tokenPrefix}_${System.currentTimeMillis()}",
                                    c,
                                    nom,
                                    rolPersonalizado
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = EcoOliveGreen)
                        ) {
                            Text("Continuar", fontSize = 13.sp, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Para continuar, Google compartirá tu nombre, correo electrónico y foto de perfil con AlimentaRed. Consulta nuestra Política de Privacidad.",
                    fontSize = 10.sp,
                    color = Color(0xFF70757A),
                    lineHeight = 13.sp
                )
            }
        }
    }
}
