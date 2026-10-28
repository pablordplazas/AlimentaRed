package com.example.alimentared.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.School
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

            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // HEADER
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BlueDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("NUESTRA HISTORIA", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color.White)
                Text("Extensión de la Universidad de Cundinamarca", fontSize = 12.sp, color = TextMuted)
            }
        }

        // HISTORIA DEL PROYECTO
        Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, contentDescription = null, tint = RedPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Origen del Proyecto AlimentaRed", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BlueDark)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "AlimentaRed nació como una iniciativa académica y social en la Facultad de Ingeniería de la Universidad de Cundinamarca. Nació con el objetivo de eliminar la intermediación informal en la donación de alimentos, conectando comercios con fundaciones mediante tecnología transaccional segura e indicadores en tiempo real.",
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    lineHeight = 18.sp
                )
            }
        }

        // MISIÓN Y VISIÓN
        Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.School, contentDescription = null, tint = RedPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Compromiso Institucional", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BlueDark)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Garantizar que ningún alimento apto para el consumo sea desperdiciado, brindando trazabilidad, inocuidad operativa y distribución equitativa para las comunidades más vulnerables.",
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
