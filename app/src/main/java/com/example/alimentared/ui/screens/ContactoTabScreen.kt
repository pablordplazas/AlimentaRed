package com.example.alimentared.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
            .background(EcoBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Centro de Contacto & Reportes", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = EcoTextPrimary)
        Text("¿Tienes alguna incidencia logística o consulta sobre la plataforma? Escríbenos:", fontSize = 12.sp, color = EcoTextMuted)

        if (enviado) {
            Card(
                colors = CardDefaults.cardColors(containerColor = EcoMintGreen),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EcoForestGreen)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("¡Reporte Enviado con Éxito!", fontWeight = FontWeight.Bold, color = EcoForestGreen, fontSize = 14.sp)
                        Text("El equipo de soporte de Eco Abasto revisará tu mensaje en breve.", fontSize = 12.sp, color = EcoForestGreen)
                    }
                }
            }
        }

        OutlinedTextField(
            value = asunto,
            onValueChange = { asunto = it },
            label = { Text("Asunto / Tipo de Incidencia") },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EcoForestGreen,
                unfocusedBorderColor = EcoBorder,
                focusedContainerColor = EcoSurface,
                unfocusedContainerColor = EcoSurface
            )
        )

        OutlinedTextField(
            value = mensaje,
            onValueChange = { mensaje = it },
            label = { Text("Descripción del Mensaje o Reporte") },
            minLines = 4,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().height(130.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EcoForestGreen,
                unfocusedBorderColor = EcoBorder,
                focusedContainerColor = EcoSurface,
                unfocusedContainerColor = EcoSurface
            )
        )

        Button(
            onClick = { enviado = true },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = EcoForestGreen)
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Enviar Reporte a Soporte", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
        }
    }
}
