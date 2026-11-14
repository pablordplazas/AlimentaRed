package com.example.alimentared.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alimentared.ui.theme.*

@Composable
fun NosotrosTabScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EcoBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // HEADER ECO ABASTO
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = EcoOliveGreen),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF6B824B),
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Grass, contentDescription = null, tint = Color(0xFFF3C043), modifier = Modifier.size(32.dp))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("NUESTRA HISTORIA", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color.White)
                Text("Extensión de la Universidad de Cundinamarca", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
            }
        }

        // HISTORIA DEL PROYECTO
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = EcoSurface),
            border = BorderStroke(1.dp, EcoBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, contentDescription = null, tint = EcoForestGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Origen del Proyecto Eco Abasto", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EcoTextPrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Eco Abasto (AlimentaRed) nació como una iniciativa académica y social en la Universidad de Cundinamarca con el objetivo de eliminar la intermediación informal en la donación de alimentos, conectando comercios con fundaciones mediante tecnología segura e indicadores en tiempo real.",
                    fontSize = 13.sp,
                    color = EcoTextMuted,
                    lineHeight = 18.sp
                )
            }
        }

        // MISIÓN Y VISIÓN
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = EcoSurface),
            border = BorderStroke(1.dp, EcoBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = EcoForestGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Compromiso Institucional", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EcoTextPrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Garantizar que ningún alimento apto para el consumo sea desperdiciado, brindando trazabilidad, inocuidad operativa y distribución equitativa para las comunidades más vulnerables.",
                    fontSize = 13.sp,
                    color = EcoTextMuted,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
