package com.example.alimentared.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alimentared.data.model.IncidenciaResponse
import com.example.alimentared.ui.theme.*
import com.example.alimentared.ui.viewmodels.IncidenciasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidenciasScreen(
    loteIdInicial: Long? = null,
    viewModel: IncidenciasViewModel = viewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var nuevoLoteId by remember { mutableStateOf(loteIdInicial?.toString() ?: "1") }
    var nuevosDetalles by remember { mutableStateOf("") }
    var mostrarFormulario by remember { mutableStateOf(loteIdInicial != null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Calidad e Inocuidad", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Reportes de Inocuidad (RF08)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BlueDark)
                Button(
                    onClick = { mostrarFormulario = !mostrarFormulario },
                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (mostrarFormulario) "Ver Lista" else "Reportar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (uiState.mensaje != null) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)), modifier = Modifier.fillMaxWidth()) {
                    Text(uiState.mensaje!!, color = Color(0xFF14532D), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
                }
            }
            if (uiState.error != null) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)), modifier = Modifier.fillMaxWidth()) {
                    Text(uiState.error!!, color = RedDark, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
                }
            }

            if (mostrarFormulario) {
                // FORMULARIO DE REPORTE
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Reportar Problema en Lote", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BlueDark)
                        Text("El reporte bloqueará preventivamente el lote para evitar distribución de producto no inocuo (RF08).", fontSize = 11.sp, color = Color.Gray)

                        OutlinedTextField(
                            value = nuevoLoteId,
                            onValueChange = { nuevoLoteId = it },
                            label = { Text("ID del Lote") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = nuevosDetalles,
                            onValueChange = { nuevosDetalles = it },
                            label = { Text("Detalle de la anomalía (cadena de frío, empaque roto, etc.)") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                val id = nuevoLoteId.toLongOrNull() ?: 1L
                                viewModel.reportarIncidencia(id, nuevosDetalles)
                                nuevosDetalles = ""
                            },
                            enabled = !uiState.isLoading && nuevosDetalles.length >= 10,
                            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Enviar Reporte de Inocuidad", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // LISTADO DE INCIDENCIAS
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = RedPrimary)
                    }
                } else if (uiState.incidencias.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay incidencias activas reportadas", color = Color.Gray, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(uiState.incidencias) { inc ->
                            IncidenciaCard(
                                incidencia = inc,
                                onResolver = { reactivar, res ->
                                    viewModel.resolverIncidencia(inc.id, reactivar, res)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IncidenciaCard(
    incidencia: IncidenciaResponse,
    onResolver: (Boolean, String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("INCIDENCIA #${incidencia.id} - LOTE ${incidencia.loteId}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = RedPrimary)
                Surface(
                    color = if (incidencia.resuelta) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        if (incidencia.resuelta) "RESUELTA" else "PENDIENTE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (incidencia.resuelta) Color(0xFF14532D) else RedDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(incidencia.loteNombre ?: "Alimento Reportado", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BlueDark)
            Text("Detalles: ${incidencia.detalles}", fontSize = 12.sp, color = Color.DarkGray)

            if (incidencia.resolucion != null) {
                Text("Resolución: ${incidencia.resolucion}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF16A34A))
            }

            if (!incidencia.resuelta) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onResolver(true, "Lote inspeccionado y reactivado apto para consumo") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reactivar Lote", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { onResolver(false, "Lote descartado definitivamente por inocuidad") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Descartar Lote", fontSize = 10.sp, color = RedDark)
                    }
                }
            }
        }
    }
}
