package com.example.alimentared.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.alimentared.ui.theme.*
import com.example.alimentared.ui.viewmodels.LoteDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoteDetailScreen(
    loteId: Long,
    rolUsuario: String,
    viewModel: LoteDetailViewModel = viewModel(),
    onBack: () -> Unit,
    onReportarIncidencia: ((Long) -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(loteId) {
        viewModel.cargarDetalle(loteId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        uiState.lote?.nombre ?: "Detalle del Lote",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    if (onReportarIncidencia != null && uiState.lote != null) {
                        IconButton(onClick = { onReportarIncidencia(loteId) }) {
                            Icon(Icons.Default.ReportProblem, contentDescription = "Reportar", tint = Color(0xFFFCA5A5))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlueDark)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RedPrimary)
            }
            return@Scaffold
        }

        val lote = uiState.lote
        if (lote == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(uiState.errorOperacion ?: "Lote no disponible", color = Color.Gray)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ALERTA DE RESCATE URGENTE SI APLICA
            if (lote.rescateUrgente) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = RedPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "¡Alerta de Rescate Urgente! Caduca en menos de 4 horas. Prioridad máxima de retiro.",
                            color = RedDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // DATOS BÁSICOS DEL LOTE
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        (lote.categoriaNombre ?: "ALIMENTO").uppercase(),
                        color = RedPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Text(lote.nombre, fontSize = 18.sp, fontWeight = FontWeight.Black, color = BlueDark)
                    Text(lote.descripcion, fontSize = 13.sp, color = Color.DarkGray)

                    HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 4.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Stock Disponible", fontSize = 11.sp, color = Color.Gray)
                            Text("${lote.cantidadDisponible} ${lote.unidadMedida}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BlueDark)
                        }
                        Column {
                            Text("Unidad Mínima", fontSize = 11.sp, color = Color.Gray)
                            Text("${lote.unidadMinima} ${lote.unidadMedida}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BlueDark)
                        }
                        Column {
                            Text("Múltiplo de Entrega", fontSize = 11.sp, color = Color.Gray)
                            Text("${lote.multiplo} ${lote.unidadMedida}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BlueDark)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Estado del Alimento: ${lote.estadoAlimento ?: "FRESCO"}", fontSize = 12.sp, color = Color.Gray)
                        Text("Donante: ${lote.donanteNombre ?: "Comercio"}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = BlueDark)
                    }
                }
            }

            // DATOS ENMASCARADOS DE RECOGIDA (RF04 / PRIVACIDAD)
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (lote.instruccionesRecogida != null) Color(0xFFF0FDF4) else Color(0xFFF8FAFC)
                ),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (lote.instruccionesRecogida != null) Color(0xFF86EFAC) else Color(0xFFE2E8F0)
                    )
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (lote.instruccionesRecogida != null) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (lote.instruccionesRecogida != null) Color(0xFF16A34A) else RedPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (lote.instruccionesRecogida != null) "Instrucciones de Retiro Físico (Asignado)" else "Logística de Recogida (Enmascarada)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = BlueDark
                        )
                    }

                    if (lote.instruccionesRecogida != null) {
                        Text("📍 Ubicación: ${lote.ubicacion ?: "Sede Donante"}", fontSize = 12.sp, color = BlueDark)
                        Text("📋 Indicaciones: ${lote.instruccionesRecogida}", fontSize = 12.sp, color = Color.DarkGray)
                        Text("📞 Contacto: ${lote.contacto ?: "No especificado"}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BlueDark)
                    } else {
                        Text(
                            "Por protocolo de seguridad y equidad, la dirección exacta, horario de muelle y contacto solo se revelan a la fundación una vez que la solicitud sea formalmente asignada.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // CALCULADORA DE PORCIONES Y RESERVA (RF05, RN05, RN10)
            if (rolUsuario.equals("RECEPTORA", ignoreCase = true)) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Calculadora de Reserva de Donación", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BlueDark)
                        Text("Calcula las raciones garantizando no dejar remanentes inútiles.", fontSize = 12.sp, color = Color.Gray)

                        OutlinedTextField(
                            value = uiState.cantidadInput,
                            onValueChange = { viewModel.onCantidadCambiada(it) },
                            label = { Text("Cantidad a Solicitar (${lote.unidadMedida})") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (uiState.esValido) Color(0xFF16A34A) else RedPrimary,
                                focusedLabelColor = if (uiState.esValido) Color(0xFF16A34A) else RedPrimary
                            )
                        )

                        // Indicadores calculados
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("🍽️ Raciones estimadas: ${uiState.porcionesEstimadas}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BlueDark)
                            Text("📦 Remanente: ${uiState.remanenteLote} ${lote.unidadMedida}", fontSize = 12.sp, color = if (uiState.remanenteLote < lote.unidadMinima && uiState.remanenteLote > 0) RedPrimary else Color.Gray)
                        }

                        // Mensaje de validación RN05/RN10
                        if (uiState.mensajeValidacion != null) {
                            Text(
                                "⚠️ ${uiState.mensajeValidacion}",
                                color = RedDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else if (uiState.esValido) {
                            Text(
                                "✅ Cumple con la unidad mínima (${lote.unidadMinima}), múltiplos (${lote.multiplo}) y remanente útil.",
                                color = Color(0xFF16A34A),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (uiState.errorOperacion != null) {
                            Text(
                                "❌ ${uiState.errorOperacion}",
                                color = RedPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (uiState.solicitudExitosa) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("¡Solicitud enviada exitosamente! Quedó en espera de asignación.", color = Color(0xFF14532D), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Button(
                            onClick = { viewModel.enviarSolicitud() },
                            enabled = uiState.esValido && !uiState.isSubmitting && !uiState.solicitudExitosa,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
                        ) {
                            if (uiState.isSubmitting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Solicitar Donación", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
