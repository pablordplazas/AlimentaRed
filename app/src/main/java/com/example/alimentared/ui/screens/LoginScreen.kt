package com.example.alimentared.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alimentared.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginExitoso: (String) -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("usuario@alimentared.org") }
    var password by remember { mutableStateOf("123456") }
    var rolSeleccionado by remember { mutableStateOf("Donante") }

    val roles = listOf(
        "Donante" to "Publicar y gestionar lotes de alimentos",
        "Receptora" to "Solicitar donaciones para fundaciones",
        "Coordinador" to "Supervisar entregas y resolver incidencias",
        "Administrador" to "Gestión general del sistema"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Portal de Acceso", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlueDark)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Iniciar Sesión", fontSize = 22.sp, fontWeight = FontWeight.Black, color = BlueDark)
            Text("Selecciona tu rol operativo en AlimentaRed:", color = Color.Gray, fontSize = 13.sp)

            // SELECTOR DE ROL ESTILIZADO CON BORDES ROJOS
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                roles.forEach { (rol, desc) ->
                    val seleccionado = (rolSeleccionado == rol)
                    Card(
                        onClick = { rolSeleccionado = rol },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (seleccionado) RedPrimary.copy(alpha = 0.08f) else Color.White
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(
                                if (seleccionado) RedPrimary else Color(0xFFE2E8F0)
                            )
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = seleccionado,
                                onClick = { rolSeleccionado = rol },
                                colors = RadioButtonDefaults.colors(selectedColor = RedPrimary)
                            )
                            Column(modifier = Modifier.padding(start = 8.dp)) {
                                Text(rol, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BlueDark)
                                Text(desc, fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RedPrimary,
                    focusedLabelColor = RedPrimary
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RedPrimary,
                    focusedLabelColor = RedPrimary
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onLoginExitoso(rolSeleccionado) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ingresar como $rolSeleccionado", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
