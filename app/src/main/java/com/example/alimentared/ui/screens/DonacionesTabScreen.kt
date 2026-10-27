package com.example.alimentared.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun DonacionesTabScreen(
    rolUsuario: String,
    onLoteClick: (Long) -> Unit,
    onCrearLoteClick: () -> Unit
) {
    var subTabSeleccionada by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
    ) {
        // VISTA SEGÚN ROL
        when (rolUsuario) {
            "Donante" -> {
                // DONANTE: TUS DONACIONES + DONAR DE NUEVO
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Gestión de Donante", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BlueDark)
                    Button(
                        onClick = onCrearLoteClick,
                        colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Dona de Nuevo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                TabRow(selectedTabIndex = subTabSeleccionada, containerColor = Color.Transparent, contentColor = RedPrimary) {
                    Tab(selected = subTabSeleccionada == 0, onClick = { subTabSeleccionada = 0 }) {
                        Text("Tus Donaciones", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Tab(selected = subTabSeleccionada == 1, onClick = { subTabSeleccionada = 1 }) {
                        Text("Historial Entregado", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(FakeDataRepository.lotes) { lote ->
                        Card(
                            onClick = { onLoteClick(lote.id) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(lote.categoria.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RedPrimary)
                                Text(lote.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BlueDark)
                                Text("Cantidad en Stock: ${lote.cantidadDisponible} ${lote.unidad}", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            "Receptora" -> {
                // RECEPTORA (FUNDACIÓN): RESERVAR DONACIONES + SOLICITUDES EN CAMINO
                Text("Donaciones Disponibles", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BlueDark)
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(FakeDataRepository.lotes) { lote ->
                        Card(
                            onClick = { onLoteClick(lote.id) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(lote.categoria.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RedPrimary)
                                Text(lote.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BlueDark)
                                Text("Donante: ${lote.donante}", fontSize = 12.sp, color = Color.Gray)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Disponible: ${lote.cantidadDisponible} ${lote.unidad}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BlueDark)
                                    Text("Solicitar Reserva", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RedPrimary)
                                }
                            }
                        }
                    }
                }
            }

            "Coordinador" -> {
                // COORDINADOR: APROBACIONES, SOLUCIÓN DE EMPATES Y STRIKES
                Text("Panel de Coordinación y Gobernanza", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BlueDark)
                Text("Supervisa la equidad en asignaciones y apelación de sanciones.", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))

                Card(colors = CardDefaults.cardColors(containerColor = BlueDark), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = RedPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ventana de Empate Activa (1 min)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        }
                        Text("2 fundaciones solicitaron 'Pan Integral'. Requiere tu asignación manual.", fontSize = 11.sp, color = TextMuted)
                    }
                }
            }

            "Administrador" -> {
                // ADMINISTRADOR: PARAMETRIZACIÓN GENERAL
                Text("Administración del Sistema AlimentaRed", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BlueDark)
                Spacer(modifier = Modifier.height(12.dp))

                Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Gestión de Usuarios y Entidades", fontWeight = FontWeight.Bold, color = BlueDark)
                        Text("Aprobación de organizaciones donantes y receptoras registradas.", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}
