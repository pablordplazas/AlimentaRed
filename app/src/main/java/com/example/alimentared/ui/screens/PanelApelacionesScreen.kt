package com.example.alimentared.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alimentared.data.model.ApelacionStrikeRequest
import com.example.alimentared.data.model.StrikeResponse
import com.example.alimentared.data.remote.RetrofitClient
import com.example.alimentared.ui.components.AlimentaRedTopBar
import com.example.alimentared.ui.theme.*
import com.example.alimentared.util.removerCodigosRegla
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanelApelacionesScreen(
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var strikes by remember { mutableStateOf<List<StrikeResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }

    var soloVigentes by remember { mutableStateOf(true) }

    var strikeAEvaluar by remember { mutableStateOf<StrikeResponse?>(null) }
    var aprobarDecision by remember { mutableStateOf(true) }
    var justificacionEvaluacion by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    fun cargarStrikes() {
        coroutineScope.launch {
            isLoading = true
            mensajeError = null
            try {
                val res = RetrofitClient.instance.getTodosLosStrikes()
                if (res.isSuccessful && res.body() != null) {
                    strikes = res.body()!!
                } else {
                    mensajeError = "No se pudieron obtener las sanciones (${res.code()})"
                }
            } catch (e: Exception) {
                mensajeError = "Error al conectar: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        cargarStrikes()
    }

    val strikesFiltrados = remember(strikes, soloVigentes) {
        if (soloVigentes) strikes.filter { it.vigente } else strikes
    }

    Scaffold(
        topBar = {
            AlimentaRedTopBar(
                title = {
                    Text(
                        "Panel de Apelaciones y Sanciones",
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
                },
                actions = {
                    IconButton(
                        onClick = { cargarStrikes() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Actualizar",
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // TARJETA EXPLICATIVA ESTILO ECO ABASTO
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = EcoSurface),
                border = BorderStroke(1.dp, EcoBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Gavel, contentDescription = null, tint = EcoForestGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Gestión y Resolución de Sanciones",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = EcoTextPrimary
                        )
                    }
                    Text(
                        "Evalúa las solicitudes de apelación radicadas por las organizaciones Donantes o Receptoras. Al aprobar una apelación, el strike se anula y la cuenta suspendida se reactiva si sus strikes acumulados bajan de 3.",
                        fontSize = 12.sp,
                        color = EcoTextMuted,
                        lineHeight = 17.sp
                    )
                }
            }

            // MENSAJES DE ESTADO
            if (mensajeExito != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EcoMintGreen),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        mensajeExito!!,
                        color = EcoForestGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            if (mensajeError != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EcoRedLight),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        mensajeError!!,
                        color = EcoRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // SELECTOR DE FILTRO PÍLDORA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = soloVigentes,
                    onClick = { soloVigentes = true },
                    shape = RoundedCornerShape(50),
                    label = { Text("Pendientes / Vigentes (${strikes.count { it.vigente }})", fontSize = 11.sp, fontWeight = if (soloVigentes) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EcoMintGreen,
                        selectedLabelColor = EcoForestGreen,
                        containerColor = EcoSurface
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = soloVigentes,
                        borderColor = if (soloVigentes) EcoForestGreen else EcoBorder
                    )
                )
                FilterChip(
                    selected = !soloVigentes,
                    onClick = { soloVigentes = false },
                    shape = RoundedCornerShape(50),
                    label = { Text("Todos los Registros (${strikes.size})", fontSize = 11.sp, fontWeight = if (!soloVigentes) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EcoMintGreen,
                        selectedLabelColor = EcoForestGreen,
                        containerColor = EcoSurface
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = !soloVigentes,
                        borderColor = if (!soloVigentes) EcoForestGreen else EcoBorder
                    )
                )
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = EcoForestGreen)
                }
            } else if (strikesFiltrados.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = EcoSurface),
                    border = BorderStroke(1.dp, EcoBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "No hay sanciones registradas en este filtro.",
                        fontSize = 13.sp,
                        color = EcoTextMuted,
                        modifier = Modifier.padding(24.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                strikesFiltrados.forEach { strike ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = EcoSurface),
                        border = BorderStroke(1.dp, EcoBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        strike.organizacionNombre ?: "Organización #${strike.organizacionId}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = EcoTextPrimary
                                    )
                                    Text(
                                        "ID Strike #${strike.id} • ${strike.fechaRegistro ?: ""}",
                                        fontSize = 11.sp,
                                        color = EcoTextMuted
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = if (strike.vigente) EcoRedLight else EcoMintGreen
                                ) {
                                    Text(
                                        if (strike.vigente) "VIGENTE" else "ANULADO",
                                        color = if (strike.vigente) EcoRed else EcoForestGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Text(
                                "Motivo: ${strike.nombreStrike.removerCodigosRegla()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = EcoOliveGreen
                            )

                            Text(
                                strike.detalles?.removerCodigosRegla() ?: "Infracción logística registrada.",
                                fontSize = 12.sp,
                                color = EcoTextMuted
                            )

                            if (strike.justificacionApelacion != null) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = EcoBackground,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Resolución previa:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EcoTextMuted)
                                        Text(strike.justificacionApelacion.removerCodigosRegla(), fontSize = 11.sp, color = EcoTextPrimary)
                                    }
                                }
                            }

                            if (strike.vigente) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = {
                                        strikeAEvaluar = strike
                                        aprobarDecision = true
                                        justificacionEvaluacion = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EcoForestGreen),
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.fillMaxWidth().height(44.dp)
                                ) {
                                    Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Evaluar Apelación", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // DIÁLOGO DE EVALUACIÓN DE APELACIÓN
    if (strikeAEvaluar != null) {
        val s = strikeAEvaluar!!
        AlertDialog(
            onDismissRequest = { if (!isSubmitting) strikeAEvaluar = null },
            shape = RoundedCornerShape(24.dp),
            containerColor = EcoSurface,
            title = {
                Text(
                    "Evaluar Strike #${s.id}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = EcoTextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Organización: ${s.organizacionNombre ?: "#${s.organizacionId}"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EcoTextMuted
                    )
                    Text(
                        "Infracción: ${s.nombreStrike}",
                        fontSize = 11.sp,
                        color = EcoRed,
                        fontWeight = FontWeight.Bold
                    )

                    Text("Decisión de Coordinación:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EcoTextPrimary)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = aprobarDecision,
                            onClick = { aprobarDecision = true }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Aprobar apelación (Anular strike)", fontSize = 12.sp)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = !aprobarDecision,
                            onClick = { aprobarDecision = false }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rechazar apelación (Mantener vigente)", fontSize = 12.sp)
                    }

                    OutlinedTextField(
                        value = justificacionEvaluacion,
                        onValueChange = { justificacionEvaluacion = it },
                        label = { Text("Justificación (mín. 10 caracteres)") },
                        placeholder = { Text("Ej: Se valida soporte médico...") },
                        minLines = 3,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EcoForestGreen,
                            focusedLabelColor = EcoForestGreen
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isSubmitting = true
                            mensajeError = null
                            mensajeExito = null
                            try {
                                val res = RetrofitClient.instance.apelarStrike(
                                    id = s.organizacionId,
                                    request = ApelacionStrikeRequest(
                                        strikeId = s.id,
                                        justificacion = justificacionEvaluacion.trim(),
                                        aprobada = aprobarDecision
                                    )
                                )
                                if (res.isSuccessful) {
                                    mensajeExito = if (aprobarDecision) {
                                        "Apelación APROBADA. Strike #${s.id} anulado exitosamente."
                                    } else {
                                        "Apelación RECHAZADA. Strike #${s.id} se mantiene vigente."
                                    }
                                    strikeAEvaluar = null
                                    cargarStrikes()
                                } else {
                                    mensajeError = "Error al resolver apelación: código ${res.code()}"
                                }
                            } catch (e: Exception) {
                                mensajeError = "Error al conectar: ${e.localizedMessage}"
                            } finally {
                                isSubmitting = false
                            }
                        }
                    },
                    enabled = justificacionEvaluacion.trim().length >= 10 && !isSubmitting,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = EcoForestGreen)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                    } else {
                        Text(if (aprobarDecision) "Aprobar" else "Rechazar", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { strikeAEvaluar = null },
                    enabled = !isSubmitting
                ) {
                    Text("Cancelar", color = EcoTextMuted)
                }
            }
        )
    }
}
