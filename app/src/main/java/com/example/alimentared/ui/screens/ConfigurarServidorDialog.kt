package com.example.alimentared.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alimentared.data.remote.RetrofitClient
import com.example.alimentared.data.session.SessionManager
import com.example.alimentared.ui.theme.BlueDark
import com.example.alimentared.ui.theme.RedPrimary
import kotlinx.coroutines.launch

@Composable
fun ConfigurarServidorDialog(
    onDismiss: () -> Unit,
    onServidorGuardado: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance() }
    var urlTemp by remember { mutableStateOf(sessionManager.getServerUrl()) }
    var estadoTestServidor by remember { mutableStateOf<String?>(null) }
    var probandoConexion by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Configuración Servidor",
                    tint = RedPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Configurar Servidor Backend",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlueDark
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BlueDark.copy(alpha = 0.05f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Dns, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Servidor actual: ${sessionManager.getServerUrl()}",
                            fontSize = 11.sp,
                            color = Color.DarkGray
                        )
                    }
                }

                Text(
                    text = "Selecciona un preset o ingresa la URL de tu API REST:",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                OutlinedTextField(
                    value = urlTemp,
                    onValueChange = { urlTemp = it },
                    label = { Text("URL Base del API") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontSize = 13.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = RedPrimary,
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedLabelColor = RedPrimary,
                        unfocusedLabelColor = Color(0xFF475569),
                        cursorColor = RedPrimary
                    )
                )

                Text(
                    text = "Presets Rápidos:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlueDark
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = urlTemp.contains("onrender.com"),
                        onClick = { urlTemp = "https://pr-alimentared.onrender.com/api/" },
                        label = { Text("Render Cloud", fontSize = 10.sp) }
                    )
                    FilterChip(
                        selected = urlTemp.contains("192.168.1.18"),
                        onClick = { urlTemp = "http://192.168.1.18:8080/api/" },
                        label = { Text("IP Local", fontSize = 10.sp) }
                    )
                    FilterChip(
                        selected = urlTemp.contains("10.0.2.2"),
                        onClick = { urlTemp = "http://10.0.2.2:8080/api/" },
                        label = { Text("10.0.2.2", fontSize = 10.sp) }
                    )
                }

                if (estadoTestServidor != null) {
                    Text(
                        text = estadoTestServidor!!,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (estadoTestServidor!!.startsWith("✓")) Color(0xFF16A34A) else RedPrimary
                    )
                }

                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            probandoConexion = true
                            estadoTestServidor = "Probando conexión..."
                            try {
                                RetrofitClient.updateBaseUrl(urlTemp)
                                val res = RetrofitClient.instance.checkHealth()
                                if (res.isSuccessful) {
                                    estadoTestServidor = "✓ Conexión Exitosa con el Backend (API UP)"
                                } else {
                                    estadoTestServidor = "✗ Servidor respondió con código ${res.code()}"
                                }
                            } catch (e: Exception) {
                                estadoTestServidor = "✗ Error: ${e.localizedMessage ?: "No se pudo conectar"}"
                            } finally {
                                probandoConexion = false
                            }
                        }
                    },
                    enabled = !probandoConexion,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (probandoConexion) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Probar Conexión", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    sessionManager.setServerUrl(urlTemp)
                    val urlFinal = sessionManager.getServerUrl()
                    RetrofitClient.updateBaseUrl(urlFinal)
                    Toast.makeText(context, "Servidor configurado: $urlFinal", Toast.LENGTH_SHORT).show()
                    onServidorGuardado?.invoke(urlFinal)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
            ) {
                Text("Guardar y Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
