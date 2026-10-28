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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alimentared.data.model.AsignacionResponse
import com.example.alimentared.data.model.SolicitudResponse
import com.example.alimentared.data.session.SessionManager
import com.example.alimentared.ui.theme.*
import com.example.alimentared.ui.viewmodels.SolicitudesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudesScreen(
    viewModel: SolicitudesViewModel = viewModel(),
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val rol = remember { try { SessionManager.getInstance().getRol().uppercase() } catch (e: Exception) { "RECEPTORA" } }
    val esSupervisor = rol == "COORDINADOR" || rol == "ADMINISTRADOR"

    var tabIndex by remember { mutableStateOf(0) }
    var asignacionAConfirmar by remember { mutableStateOf<AsignacionResponse?>(null) }
    var cantidadConfirmar by remember { mutableStateOf("") }
    var observacionesConfirmar by remember { mutableStateOf("") }

    // Diálogo para aprobar/rechazar solicitud como coordinador
    var solicitudAResolver by remember { mutableStateOf<SolicitudResponse?>(null) }
    var motivoResolucion by remember { mutableStateOf("") }
    var esAprobacion by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Solicitudes y Entregas", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
            // Pestañas superiores dinámicas según rol
            TabRow(
                selectedTabIndex = tabIndex,
                containerColor = Color.Transparent,
                contentColor = RedPrimary
            ) {
                Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }) {
                    Text("Asignaciones", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                if (esSupervisor) {
                    Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }) {
                        Text("Pendientes de Aprobación", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
                Tab(selected = tabIndex == if (esSupervisor) 2 else 1, onClick = { tabIndex = if (esSupervisor) 2 else 1 }) {
                    Text("Mis Solicitudes", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Banners de Éxito o Error
            if (uiState.mensajeExito != null) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Text(uiState.mensajeExito!!, color = Color(0xFF14532D), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
                }
            }
            if (uiState.error != null) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Text(uiState.error!!, color = RedDark, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = RedPrimary)
                }
            } else if (tabIndex == 0) {
                // ASIGNACIONES DE RETIRO
                if (uiState.asignaciones.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay asignaciones de retiro pendientes", color = Color.Gray, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(uiState.asignaciones) { asig ->
                            AsignacionCard(
                                asignacion = asig,
                                onConfirmar = {
                                    asignacionAConfirmar = asig
                                    cantidadConfirmar = asig.cantidadAsignada.toString()
                                },
                                onInasistencia = { viewModel.reportarInasistencia(asig.id) }
                            )
                        }
                    }
                }
            } else if (esSupervisor && tabIndex == 1) {
                // SOLICITUDES PENDIENTES DE APROBACIÓN (COORDINADOR / ADMIN)
                val pendientes = uiState.solicitudesReceptora.filter { it.estado == "PENDIENTE" }
                if (pendientes.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay solicitudes pendientes de aprobación", color = Color.Gray, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(pendientes) { sol ->
                            SolicitudPendienteCard(
                                solicitud = sol,
                                onAprobar = {
                                    solicitudAResolver = sol
                                    esAprobacion = true
                                    motivoResolucion = "Aprobado por coordinación"
                                },
                                onRechazar = {
                                    solicitudAResolver = sol
                                    esAprobacion = false
                                    motivoResolucion = "No cumple requisitos de asignación"
                                }
                            )
                        }
                    }
                }
            } else {
                // MIS SOLICITUDES
                if (uiState.solicitudesReceptora.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No tienes solicitudes registradas", color = Color.Gray, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(uiState.solicitudesReceptora) { sol ->
                            SolicitudCard(solicitud = sol)
                        }
                    }
                }
            }
        }
    }

    // Modal Confirmar Entrega
    if (asignacionAConfirmar != null) {
        val asig = asignacionAConfirmar!!
        AlertDialog(
            onDismissRequest = { asignacionAConfirmar = null },
            title = { Text("Confirmar Entrega Física", fontWeight = FontWeight.Bold, color = BlueDark) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Lote: ${asig.loteNombre ?: "Alimento"}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text("Asignado: ${asig.cantidadAsignada} Kg", fontSize = 12.sp, color = Color.Gray)

                    OutlinedTextField(
                        value = cantidadConfirmar,
                        onValueChange = { cantidadConfirmar = it },
                        label = { Text("Cantidad Retirada") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = observacionesConfirmar,
                        onValueChange = { observacionesConfirmar = it },
                        label = { Text("Observaciones (opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cant = cantidadConfirmar.toDoubleOrNull() ?: asig.cantidadAsignada
                        viewModel.confirmarEntrega(asig.id, cant, observacionesConfirmar)
                        asignacionAConfirmar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
                ) {
                    Text("Confirmar Retiro", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { asignacionAConfirmar = null }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    // Modal Resolver Solicitud (Aprobar/Rechazar) para Coordinador
    if (solicitudAResolver != null) {
        val sol = solicitudAResolver!!
        AlertDialog(
            onDismissRequest = { solicitudAResolver = null },
            title = { Text(if (esAprobacion) "Aprobar Solicitud" else "Rechazar Solicitud", fontWeight = FontWeight.Bold, color = BlueDark) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Lote: ${sol.loteNombre}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text("Organización: ${sol.organizacionNombre ?: "Fundación"}", fontSize = 12.sp, color = Color.Gray)
                    Text("Cantidad: ${sol.cantidadSolicitada} Kg", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = motivoResolucion,
                        onValueChange = { motivoResolucion = it },
                        label = { Text("Motivo / Observación") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resolverSolicitud(sol.id, esAprobacion, motivoResolucion)
                        solicitudAResolver = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (esAprobacion) RedPrimary else RedDark)
                ) {
                    Text(if (esAprobacion) "Aprobar" else "Rechazar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { solicitudAResolver = null }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun AsignacionCard(
    asignacion: AsignacionResponse,
    onConfirmar: () -> Unit,
    onInasistencia: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("ASIGNACIÓN #${asignacion.id}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RedPrimary)
                Surface(
                    color = if (asignacion.estado == "ACTIVA") Color(0xFFDCFCE7) else Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        asignacion.estado,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (asignacion.estado == "ACTIVA") Color(0xFF14532D) else Color.DarkGray,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(asignacion.loteNombre ?: "Lote de Alimentos", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BlueDark)
            Text("Receptora: ${asignacion.receptoraNombre ?: "Fundación"}", fontSize = 12.sp, color = Color.Gray)
            Text("Cantidad Asignada: ${asignacion.cantidadAsignada} Kg", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BlueDark)

            if (asignacion.instruccionesRecogida != null) {
                Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("📋 Instrucciones: ${asignacion.instruccionesRecogida}", fontSize = 11.sp, color = Color.DarkGray)
                        Text("📞 Donante: ${asignacion.contactoDonante ?: "En portería"}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BlueDark)
                    }
                }
            }

            if (asignacion.estado == "ACTIVA") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onConfirmar,
                        colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Confirmar Retiro", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onInasistencia,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("No Recogido", fontSize = 11.sp, color = RedDark)
                    }
                }
            }
        }
    }
}

@Composable
fun SolicitudPendienteCard(
    solicitud: SolicitudResponse,
    onAprobar: () -> Unit,
    onRechazar: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("SOLICITUD #${solicitud.id}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RedPrimary)
                Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(6.dp)) {
                    Text("PENDIENTE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }

            Text(solicitud.loteNombre ?: "Lote de Alimentos", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BlueDark)
            Text("Organización: ${solicitud.organizacionNombre ?: "Fundación Receptora"}", fontSize = 12.sp, color = Color.Gray)
            Text("Cantidad Solicitada: ${solicitud.cantidadSolicitada} Kg", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BlueDark)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onAprobar,
                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Aprobar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onRechazar,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Rechazar", fontSize = 11.sp, color = RedDark)
                }
            }
        }
    }
}

@Composable
fun SolicitudCard(solicitud: SolicitudResponse) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("SOLICITUD #${solicitud.id}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RedPrimary)
                Surface(
                    color = when (solicitud.estado) {
                        "APROBADO", "COMPLETADA" -> Color(0xFFDCFCE7)
                        "PENDIENTE" -> Color(0xFFFEF3C7)
                        else -> Color(0xFFFEE2E2)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        solicitud.estado,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (solicitud.estado) {
                            "APROBADO", "COMPLETADA" -> Color(0xFF14532D)
                            "PENDIENTE" -> Color(0xFF92400E)
                            else -> RedDark
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(solicitud.loteNombre ?: "Lote de Alimentos", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BlueDark)
            Text("Cantidad Solicitada: ${solicitud.cantidadSolicitada} Kg", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BlueDark)

            if (solicitud.enEmpate) {
                Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ventana de Empate: en espera de decisión del donante", fontSize = 10.sp, color = Color(0xFF92400E), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
