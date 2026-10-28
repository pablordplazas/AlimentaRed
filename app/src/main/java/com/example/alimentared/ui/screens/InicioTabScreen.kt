package com.example.alimentared.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alimentared.ui.theme.*

data class NoticiaImpacto(
    val id: Long,
    val titulo: String,
    val entidad: String,
    val fecha: String,
    val resumen: String,
    val categoria: String
)

@Composable
fun InicioTabScreen() {
    val noticias = listOf(
        NoticiaImpacto(1, "¡Éxito en Jornada de Rescate!", "Panadería Central", "Hace 2 horas", "Se entregaron 120 kg de pan integral a la fundación Comedor Esperanza en Chía.", "RESCATE DESTACADO"),
        NoticiaImpacto(2, "Campaña de Emergencia Activa", "Red Comunitaria", "Hoy", "Prioridad en la recolección de granos no perecederos y agua embotellada.", "ALERTA SOCIAL"),
        NoticiaImpacto(3, "Supermercado Éxito se suma a la Red", "Grupo Éxito", "Ayer", "Nuevos puntos de recolección de lácteos y abarrotes habilitados en la sabana.", "NUEVO DONANTE")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // BANNER DE ENCABEZADO DE NOTICIAS
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BlueDark),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Campaign, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Impacto en Vivo", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                    Text("Entérate de cómo AlimentaRed transforma vidas diariamente.", fontSize = 11.sp, color = TextMuted)
                }
            }
        }

        Text("Últimas Novedades y Logros", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BlueDark)
        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(noticias) { item ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(item.categoria, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RedPrimary)
                            Text(item.fecha, fontSize = 10.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(item.titulo, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BlueDark)
                        Text("Origen: ${item.entidad}", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(item.resumen, fontSize = 12.sp, color = Color.DarkGray)
                    }
                }
            }
        }
    }
}
