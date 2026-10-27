package com.example.alimentared.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alimentared.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactoTabScreen() {
    var asunto by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var enviado by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Centro de Contacto & Reportes", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BlueDark)
        Text("¿Tienes alguna incidencia logística o consulta sobre la plataforma? Escríbenos:", fontSize = 12.sp, color = Color.Gray)

        if (enviado) {
            Card(
                colors = CardDefaults.cardColors(containerColor = RedPrimary.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("¡Reporte Enviado con Éxito!", fontWeight = FontWeight.Bold, color = BlueDark)
                    Text("El equipo de soporte de AlimentaRed revisará tu mensaje en breve.", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        OutlinedTextField(
            value = asunto,
            onValueChange = { asunto = it },
            label = { Text("Asunto / Tipo de Incidencia") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedPrimary, focusedLabelColor = RedPrimary)
        )

        OutlinedTextField(
            value = mensaje,
            onValueChange = { mensaje = it },
            label = { Text("Descripción del Mensaje o Reporte") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedPrimary, focusedLabelColor = RedPrimary)
        )

        Button(
            onClick = { enviado = true },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Enviar Reporte a Soporte", fontWeight = FontWeight.Bold)
        }
    }
}
