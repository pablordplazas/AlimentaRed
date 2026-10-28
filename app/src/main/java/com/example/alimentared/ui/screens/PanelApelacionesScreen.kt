package com.example.alimentared.ui.screens

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
import com.example.alimentared.ui.components.AlimentaRedBackground
import com.example.alimentared.ui.components.BackgroundPatternType
import com.example.alimentared.ui.theme.BackgroundLight
import com.example.alimentared.ui.theme.BlueDark
import com.example.alimentared.ui.theme.RedDark
import com.example.alimentared.ui.theme.RedPrimary
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

    // Filtro
    var soloVigentes by remember { mutableStateOf(true) }

    // Modal de evaluación
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
                    mensajeError = "No se pudieron cargar los strikes (${res.code()})"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        cargarStrikes()
    }

    val strikesFiltrados = if (soloVigentes) strikes.filter { it.vigente } else strikes

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Panel de Apelaciones",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { cargarStrikes() }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Actualizar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlueDark)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        AlimentaRedBackground(
            modifier = Modifier.padding(padding),
            patternType = BackgroundPatternType.PERFIL
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // TARJETA EXPLICATIVA
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Gavel, contentDescription = null, tint = RedPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Gestión y Resolución de Sanciones",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = BlueDark
                        )
                    }
                    Text(
                        "Evalúa las solicitudes de apelación radicadas por las organizaciones Donantes o Receptoras. Al aprobar una apelación, el strike se anula y la cuenta suspendida se reactiva si sus strikes acumulados bajan de 3.",
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        lineHeight = 17.sp
                    )
                }
            }

            // MENSAJES DE ESTADO
            if (mensajeExito != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        mensajeExito!!,
                        color = Color(0xFF14532D),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            if (mensajeError != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        mensajeError!!,
                        color = RedDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // SELECTOR DE FILTRO
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = soloVigentes,
                    onClick = { soloVigentes = true },
                    label = { Text("Pendientes / Vigentes (${strikes.count { it.vigente }})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RedPrimary.copy(alpha = 0.15f),
                        selectedLabelColor = RedPrimary
                    )
                )
                FilterChip(
                    selected = !soloVigentes,
                    onClick = { soloVigentes = false },
                    label = { Text("Todos los Registros (${strikes.size})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RedPrimary.copy(alpha = 0.15f),
                        selectedLabelColor = RedPrimary
                    )
                )
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = RedPrimary)
                }
            } else if (strikesFiltrados.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "No hay sanciones registradas en este filtro.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(24.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                strikesFiltrados.forEach { strike ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
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
                                        fontSize = 14.sp,
                                        color = BlueDark
                                    )
                                    Text(
                                        "ID Strike #${strike.id} • ${strike.fechaRegistro ?: ""}",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (strike.vigente) Color(0xFFFEE2E2) else Color(0xFFDCFCE7)
                                ) {
                                    Text(
                                        if (strike.vigente) "VIGENTE" else "ANULADO",
                                        color = if (strike.vigente) RedPrimary else Color(0xFF15803D),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                "Motivo: ${strike.nombreStrike.removerCodigosRegla()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = RedDark
                            )

                            Text(
                                strike.detalles?.removerCodigosRegla() ?: "Infracción logística registrada.",
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )

                            if (strike.justificacionApelacion != null) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFF8FAFC),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("Resolución previa:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        Text(strike.justificacionApelacion.removerCodigosRegla(), fontSize = 11.sp, color = BlueDark)
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
                                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Evaluar Apelación", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
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
            title = {
                Text(
                    "Evaluar Strike #${s.id}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = BlueDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Organización: ${s.organizacionNombre ?: "#${s.organizacionId}"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray
                    )
                    Text(
                        "Infracción: ${s.nombreStrike}",
                        fontSize = 11.sp,
                        color = RedPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Text("Decisión de Coordinación:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BlueDark)

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
                        placeholder = { Text("Ej: Se valida soporte médico por fuerza mayor...") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RedPrimary,
                            focusedLabelColor = RedPrimary
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
                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                    } else {
                        Text(if (aprobarDecision) "Aprobar" else "Rechazar")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { strikeAEvaluar = null },
                    enabled = !isSubmitting
                ) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }
}
