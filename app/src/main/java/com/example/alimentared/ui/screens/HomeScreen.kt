package com.example.alimentared.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
fun HomeScreen(
    rolUsuario: String,
    onLoteClick: (Long) -> Unit,
    onCrearLoteClick: () -> Unit,
    onVerSolicitudesClick: () -> Unit,
    onNosotrosClick: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    var categoriaSeleccionada by remember { mutableStateOf("Todas") }
    val categorias = listOf("Todas", "Verduras", "Lácteos", "Panadería", "Abarrotes", "Carnes / Proteínas")

    val lotes = FakeDataRepository.lotes

    val lotesFiltrados = remember(categoriaSeleccionada, lotes.size) {
        if (categoriaSeleccionada == "Todas") {
            lotes
        } else {
            lotes.filter { it.categoria.equals(categoriaSeleccionada, ignoreCase = true) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("ALIMENTARED", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color.White)
                        Text("Perfil: $rolUsuario", fontSize = 11.sp, color = RedPrimary, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = onVerSolicitudesClick) {
                        Icon(Icons.Default.LocalShipping, contentDescription = "Trazabilidad", tint = Color.White)
                    }
                    IconButton(onClick = onNosotrosClick) {
                        Icon(Icons.Default.Info, contentDescription = "Nosotros", tint = Color.White)
                    }
                    IconButton(onClick = onCerrarSesion) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Salir", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlueDark)
            )
        },
        floatingActionButton = {
            if (rolUsuario == "Donante" || rolUsuario == "Administrador") {
                ExtendedFloatingActionButton(
                    onClick = onCrearLoteClick,
                    containerColor = RedPrimary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Publicar Lote", fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BlueDark),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when(rolUsuario) {
                            "Donante" -> Icons.Default.Store
                            "Receptora" -> Icons.Default.VolunteerActivism
                            "Coordinador" -> Icons.Default.Shield
                            else -> Icons.Default.Person
                        },
                        contentDescription = null,
                        tint = RedPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = when(rolUsuario) {
                            "Donante" -> "Panel de Comercio: Publica tus excedentes y confirma salidas."
                            "Receptora" -> "Panel de Fundación: Explora donaciones disponibles y solicita reservas."
                            "Coordinador" -> "Panel de Supervisión: Control de incidencias y apelaciones."
                            else -> "Panel de Administración General del Sistema."
                        },
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }

            Text("Categorías de Alimentos:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BlueDark)
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(categorias) { cat ->
                    val seleccionada = (categoriaSeleccionada == cat)
                    FilterChip(
                        selected = seleccionada,
                        onClick = { categoriaSeleccionada = cat },
                        label = { Text(cat, fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RedPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Text("Catálogo de Donaciones (${lotesFiltrados.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BlueDark)
            Spacer(modifier = Modifier.height(8.dp))

            if (lotesFiltrados.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No hay alimentos disponibles en esta categoría.", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(lotesFiltrados) { lote ->
                        Card(
                            onClick = { onLoteClick(lote.id) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(lote.categoria.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RedPrimary)
                                    if (lote.esUrgente) {
                                        Surface(color = RedPrimary.copy(alpha = 0.1f), shape = RoundedCornerShape(6.dp)) {
                                            Text("RESCATE URGENTE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = RedPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(lote.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BlueDark)
                                Text("Donado por: ${lote.donante}", fontSize = 12.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Disponible: ${lote.cantidadDisponible} ${lote.unidad}", fontWeight = FontWeight.ExtraBold, color = BlueDark)
                                    Text("Expira en ${lote.horasParaExpirar}h", fontSize = 12.sp, color = RedPrimary, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
