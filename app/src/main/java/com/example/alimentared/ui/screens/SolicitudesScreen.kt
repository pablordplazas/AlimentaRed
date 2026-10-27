package com.example.alimentared.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alimentared.data.model.FakeDataRepository
import com.example.alimentared.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudesScreen(onBack: () -> Unit) {
    val solicitudes = FakeDataRepository.solicitudes

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trazabilidad de Donaciones", color = Color.White, fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
        ) {
            // CAMPAÑA DE EMERGENCIA SOCIAL Y DESASTRES
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RedPrimary),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ALERTA DE EMERGENCIA NACIONAL", fontWeight = FontWeight.Black, color = Color.White, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Campaña Activa: Sismo y Afectación Social en Regiones. ¡Tus donaciones de abarrotes y agua son prioridad hoy!", fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
                }
            }

            Text("Solicitudes y Entregas en Curso (${solicitudes.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BlueDark)
            Spacer(modifier = Modifier.height(8.dp))

            if (solicitudes.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No hay solicitudes activas aún.", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(solicitudes) { sol ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(sol.loteNombre, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BlueDark)
                                    Surface(color = RedPrimary.copy(alpha = 0.1f), shape = RoundedCornerShape(6.dp)) {
                                        Text(sol.estado.name, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RedPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Solicitado por: ${sol.receptora}", fontSize = 12.sp, color = Color.Gray)
                                Text("Cantidad: ${sol.cantidad} ${sol.unidad}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BlueDark)

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Tiempo de llegada estimado: ${sol.tiempoEntregaEstimado}", fontSize = 11.sp, color = RedPrimary, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
