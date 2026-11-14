package com.example.alimentared.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
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
import com.example.alimentared.ui.components.AlimentaRedTopBar
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
            AlimentaRedTopBar(
                title = {
                    Text(
                        "Gestión de Calidad e Inocuidad",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        },
        containerColor = EcoBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Reportes de Inocuidad", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = EcoTextPrimary)
                Button(
                    onClick = { mostrarFormulario = !mostrarFormulario },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (mostrarFormulario) EcoSageGreen else EcoForestGreen,
                        contentColor = if (mostrarFormulario) EcoTextPrimary else Color.White
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.height(38.dp)
                ) {
                    Text(if (mostrarFormulario) "Ver Lista" else "+ Reportar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (uiState.mensaje != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EcoMintGreen),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EcoForestGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(uiState.mensaje!!, color = EcoForestGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            if (uiState.error != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EcoRedLight),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = EcoRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(uiState.error!!, color = EcoRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (mostrarFormulario) {
                // FORMULARIO DE REPORTE ESTILO ECO ABASTO
                Card(
                    colors = CardDefaults.cardColors(containerColor = EcoSurface),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, EcoBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Reportar Problema en Lote", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EcoTextPrimary)
                        Text("El reporte bloqueará preventivamente el lote para evitar distribución de producto no inocuo.", fontSize = 12.sp, color = EcoTextMuted)

                        OutlinedTextField(
                            value = nuevoLoteId,
                            onValueChange = { nuevoLoteId = it },
                            label = { Text("ID del Lote") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = nuevosDetalles,
                            onValueChange = { nuevosDetalles = it },
                            label = { Text("Detalle de la anomalía (cadena de frío, empaque roto, etc.)") },
                            minLines = 3,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                val id = nuevoLoteId.toLongOrNull() ?: 1L
                                viewModel.reportarIncidencia(id, nuevosDetalles)
                                nuevosDetalles = ""
                            },
                            enabled = !uiState.isLoading && nuevosDetalles.length >= 10,
                            colors = ButtonDefaults.buttonColors(containerColor = EcoForestGreen),
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text("Enviar Reporte de Inocuidad", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            } else {
                // LISTADO DE INCIDENCIAS
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = EcoForestGreen)
                    }
                } else if (uiState.incidencias.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay incidencias activas reportadas", color = EcoTextMuted, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = EcoSurface),
        border = BorderStroke(1.dp, EcoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("INCIDENCIA #${incidencia.id} - LOTE ${incidencia.loteId}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = EcoOliveGreen)
                Surface(
                    color = if (incidencia.resuelta) EcoMintGreen else EcoRedLight,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        if (incidencia.resuelta) "RESUELTA" else "PENDIENTE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (incidencia.resuelta) EcoForestGreen else EcoRed,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(incidencia.loteNombre ?: "Alimento Reportado", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EcoTextPrimary)
            Text("Detalles: ${incidencia.detalles}", fontSize = 12.sp, color = EcoTextMuted)

            if (incidencia.resolucion != null) {
                Text("Resolución: ${incidencia.resolucion}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = EcoForestGreen)
            }

            if (!incidencia.resuelta) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { onResolver(true, "Lote inspeccionado y reactivado apto para consumo") },
                        colors = ButtonDefaults.buttonColors(containerColor = EcoForestGreen),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1.2f).height(42.dp)
                    ) {
                        Text("Reactivar Lote", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Button(
                        onClick = { onResolver(false, "Lote descartado definitivamente por inocuidad") },
                        colors = ButtonDefaults.buttonColors(containerColor = EcoSageGreen, contentColor = EcoTextPrimary),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f).height(42.dp)
                    ) {
                        Text("Descartar Lote", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
