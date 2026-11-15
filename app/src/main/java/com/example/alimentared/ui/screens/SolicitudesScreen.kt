package com.example.alimentared.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.alimentared.ui.components.AlimentaRedTopBar
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

    var solicitudAResolver by remember { mutableStateOf<SolicitudResponse?>(null) }
    var motivoResolucion by remember { mutableStateOf("") }
    var esAprobacion by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            AlimentaRedTopBar(
                title = {
                    Text(
                        "Panel de Solicitudes y Entregas",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White, modifier = Modifier.size(20.dp))
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
                .padding(16.dp)
        ) {
            // PESTAÑAS PÍLDORA ESTILO ECO ABASTO
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tab 0: Asignaciones
                val sel0 = (tabIndex == 0)
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (sel0) EcoMintGreen else EcoSurface,
                    border = BorderStroke(1.dp, if (sel0) EcoForestGreen else EcoBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { tabIndex = 0 }
                ) {
                    Text(
                        "Asignaciones",
                        modifier = Modifier.padding(vertical = 10.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontWeight = if (sel0) FontWeight.Bold else FontWeight.Medium,
                        color = if (sel0) EcoForestGreen else EcoTextMuted,
                        fontSize = 11.sp
                    )
                }

                if (esSupervisor) {
                    val sel1 = (tabIndex == 1)
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (sel1) EcoMintGreen else EcoSurface,
                        border = BorderStroke(1.dp, if (sel1) EcoForestGreen else EcoBorder),
                        modifier = Modifier
                            .weight(1.2f)
                            .clickable { tabIndex = 1 }
                    ) {
                        Text(
                            "Pendientes",
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontWeight = if (sel1) FontWeight.Bold else FontWeight.Medium,
                            color = if (sel1) EcoForestGreen else EcoTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                val mySolIndex = if (esSupervisor) 2 else 1
                val selMy = (tabIndex == mySolIndex)
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (selMy) EcoMintGreen else EcoSurface,
                    border = BorderStroke(1.dp, if (selMy) EcoForestGreen else EcoBorder),
                    modifier = Modifier
                        .weight(1.1f)
                        .clickable { tabIndex = mySolIndex }
                ) {
                    Text(
                        "Mis Solicitudes",
                        modifier = Modifier.padding(vertical = 10.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontWeight = if (selMy) FontWeight.Bold else FontWeight.Medium,
                        color = if (selMy) EcoForestGreen else EcoTextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            // Banners de Éxito o Error
            if (uiState.mensajeExito != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EcoMintGreen),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EcoForestGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(uiState.mensajeExito!!, color = EcoForestGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            if (uiState.error != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EcoRedLight),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = EcoRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(uiState.error!!, color = EcoRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = EcoForestGreen)
                }
            } else if (tabIndex == 0) {
                // ASIGNACIONES DE RETIRO
                if (uiState.asignaciones.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay asignaciones de retiro pendientes", color = EcoTextMuted, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                // SOLICITUDES PENDIENTES DE APROBACIÓN
                val pendientes = uiState.solicitudesReceptora.filter { it.estado == "PENDIENTE" }
                if (pendientes.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay solicitudes pendientes de aprobación", color = EcoTextMuted, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                        Text("No tienes solicitudes registradas", color = EcoTextMuted, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
            shape = RoundedCornerShape(24.dp),
            containerColor = EcoSurface,
            title = { Text("Confirmar Entrega Física", fontWeight = FontWeight.Bold, color = EcoTextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Lote: ${asig.loteNombre ?: "Alimento"}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = EcoTextPrimary)
                    Text("Asignado: ${asig.cantidadAsignada} Kg", fontSize = 12.sp, color = EcoTextMuted)

                    OutlinedTextField(
                        value = cantidadConfirmar,
                        onValueChange = { cantidadConfirmar = it },
                        label = { Text("Cantidad Retirada") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = observacionesConfirmar,
                        onValueChange = { observacionesConfirmar = it },
                        label = { Text("Observaciones (opcional)") },
                        shape = RoundedCornerShape(14.dp),
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
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = EcoForestGreen)
                ) {
                    Text("Confirmar Retiro", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { asignacionAConfirmar = null }) {
                    Text("Cancelar", color = EcoTextMuted)
                }
            }
        )
    }

    // Modal Resolver Solicitud para Coordinador
    if (solicitudAResolver != null) {
        val sol = solicitudAResolver!!
        AlertDialog(
            onDismissRequest = { solicitudAResolver = null },
            shape = RoundedCornerShape(24.dp),
            containerColor = EcoSurface,
            title = { Text(if (esAprobacion) "Aprobar Solicitud" else "Rechazar Solicitud", fontWeight = FontWeight.Bold, color = EcoTextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Lote: ${sol.loteNombre}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = EcoTextPrimary)
                    Text("Organización: ${sol.organizacionNombre ?: "Fundación"}", fontSize = 12.sp, color = EcoTextMuted)
                    Text("Cantidad: ${sol.cantidadSolicitada} Kg", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EcoTextPrimary)

                    OutlinedTextField(
                        value = motivoResolucion,
                        onValueChange = { motivoResolucion = it },
                        label = { Text("Motivo / Observación") },
                        shape = RoundedCornerShape(14.dp),
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
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = if (esAprobacion) EcoForestGreen else EcoRed)
                ) {
                    Text(if (esAprobacion) "Aprobar" else "Rechazar", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { solicitudAResolver = null }) {
                    Text("Cancelar", color = EcoTextMuted)
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = EcoSurface),
        border = BorderStroke(1.dp, EcoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("ASIGNACIÓN #${asignacion.id}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EcoOliveGreen)
                Surface(
                    color = if (asignacion.estado == "ACTIVA") EcoMintGreen else Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        asignacion.estado,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (asignacion.estado == "ACTIVA") EcoForestGreen else EcoTextMuted,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(asignacion.loteNombre ?: "Lote de Alimentos", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EcoTextPrimary)
            Text("Receptora: ${asignacion.receptoraNombre ?: "Fundación"}", fontSize = 12.sp, color = EcoTextMuted)
            Text("Cantidad Asignada: ${asignacion.cantidadAsignada} Kg", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EcoForestGreen)

            if (asignacion.instruccionesRecogida != null) {
                var mostrarMapaAsignacion by remember { mutableStateOf(false) }

                Surface(color = EcoMintLight, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("📋 Instrucciones: ${asignacion.instruccionesRecogida}", fontSize = 11.sp, color = EcoTextPrimary)
                        Text("📞 Donante: ${asignacion.contactoDonante ?: "En portería"}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EcoForestGreen)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(
                                onClick = { mostrarMapaAsignacion = true },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Map, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ver en Mapa 📍", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EcoForestGreen)
                            }
                        }
                    }
                }

                if (mostrarMapaAsignacion) {
                    com.example.alimentared.ui.components.MapaVisorDialog(
                        ubicacionTexto = asignacion.instruccionesRecogida,
                        nombreDonacion = asignacion.loteNombre ?: "Punto de Entrega",
                        instrucciones = asignacion.instruccionesRecogida,
                        contacto = asignacion.contactoDonante,
                        onDismiss = { mostrarMapaAsignacion = false }
                    )
                }
            }

            if (asignacion.estado == "ACTIVA") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onConfirmar,
                        colors = ButtonDefaults.buttonColors(containerColor = EcoForestGreen),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1.2f).height(42.dp)
                    ) {
                        Text("Confirmar Retiro", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Button(
                        onClick = onInasistencia,
                        colors = ButtonDefaults.buttonColors(containerColor = EcoSageGreen, contentColor = EcoTextPrimary),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f).height(42.dp)
                    ) {
                        Text("No Recogido", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = EcoSurface),
        border = BorderStroke(1.dp, EcoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("SOLICITUD #${solicitud.id}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EcoOliveGreen)
                Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(50)) {
                    Text("PENDIENTE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E), modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
            }

            Text(solicitud.loteNombre ?: "Lote de Alimentos", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EcoTextPrimary)
            Text("Organización: ${solicitud.organizacionNombre ?: "Fundación Receptora"}", fontSize = 12.sp, color = EcoTextMuted)
            Text("Cantidad Solicitada: ${solicitud.cantidadSolicitada} Kg", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EcoForestGreen)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onAprobar,
                    colors = ButtonDefaults.buttonColors(containerColor = EcoForestGreen),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f).height(42.dp)
                ) {
                    Text("Aprobar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Button(
                    onClick = onRechazar,
                    colors = ButtonDefaults.buttonColors(containerColor = EcoSageGreen, contentColor = EcoTextPrimary),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f).height(42.dp)
                ) {
                    Text("Rechazar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SolicitudCard(solicitud: SolicitudResponse) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = EcoSurface),
        border = BorderStroke(1.dp, EcoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("SOLICITUD #${solicitud.id}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EcoOliveGreen)
                Surface(
                    color = when (solicitud.estado) {
                        "APROBADO", "COMPLETADA" -> EcoMintGreen
                        "PENDIENTE" -> Color(0xFFFEF3C7)
                        else -> EcoRedLight
                    },
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        solicitud.estado,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (solicitud.estado) {
                            "APROBADO", "COMPLETADA" -> EcoForestGreen
                            "PENDIENTE" -> Color(0xFF92400E)
                            else -> EcoRed
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(solicitud.loteNombre ?: "Lote de Alimentos", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EcoTextPrimary)
            Text("Cantidad Solicitada: ${solicitud.cantidadSolicitada} Kg", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EcoForestGreen)

            // LÍNEA DE TIEMPO DE ESTADOS ESTILO ECO ABASTO
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val estados = listOf("Solicitado", "Aprobado", "En retiro", "Retirado")
                val pasoActual = when (solicitud.estado) {
                    "COMPLETADA" -> 4
                    "ASIGNADA", "EN_RETIRO" -> 3
                    "APROBADO" -> 2
                    else -> 1
                }

                estados.forEachIndexed { i, label ->
                    val completado = i < pasoActual
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = if (completado) EcoForestGreen else EcoBorder,
                            modifier = Modifier.size(22.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (completado) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(label, fontSize = 9.sp, color = if (completado) EcoTextPrimary else EcoTextMuted, fontWeight = if (completado) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }

            if (solicitud.enEmpate) {
                Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ventana de Empate: en espera de decisión del donante", fontSize = 10.sp, color = Color(0xFF92400E), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
