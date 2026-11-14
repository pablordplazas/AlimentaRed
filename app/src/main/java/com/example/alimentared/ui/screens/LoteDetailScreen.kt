package com.example.alimentared.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.alimentared.data.session.SessionManager
import com.example.alimentared.ui.components.AlimentaRedTopBar
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
    val sessionManager = remember { SessionManager.getInstance() }
    val usuarioActual = remember { sessionManager.getUsuario() }
    val orgVerificada = usuarioActual?.organizacion?.verificada ?: false

    var mensajePublicacionBorrador by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(loteId) {
        viewModel.cargarDetalle(loteId)
    }

    Scaffold(
        topBar = {
            AlimentaRedTopBar(
                title = {
                    Text(
                        uiState.lote?.nombre ?: "Detalle del Lote",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                },
                actions = {
                    if (onReportarIncidencia != null && uiState.lote != null) {
                        IconButton(onClick = { onReportarIncidencia(loteId) }, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.ReportProblem, contentDescription = "Reportar", tint = Color(0xFFFCA5A5), modifier = Modifier.size(20.dp))
                        }
                    }
                }
            )
        },
        containerColor = EcoBackground
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = EcoForestGreen)
            }
        } else if (uiState.lote == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(uiState.errorOperacion ?: "Lote no encontrado", color = EcoTextMuted)
            }
        } else {
            val lote = uiState.lote!!
            val esBorrador = lote.estado.equals("BORRADOR", ignoreCase = true)

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
                        colors = CardDefaults.cardColors(containerColor = EcoAmber.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, EcoAmber),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = EcoAmber, modifier = Modifier.size(32.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "¡Alerta de Rescate Urgente! Caduca en menos de 4 horas. Prioridad máxima de retiro.",
                                color = EcoAmberDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // DATOS BÁSICOS DEL LOTE
                Card(
                    colors = CardDefaults.cardColors(containerColor = EcoSurface),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, EcoBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = EcoMintGreen
                            ) {
                                Text(
                                    (lote.categoriaNombre ?: "ALIMENTO").uppercase(),
                                    color = EcoForestGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            if (esBorrador) {
                                Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(50)) {
                                    Text(
                                        "BORRADOR",
                                        color = Color(0xFF92400E),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            } else {
                                Surface(color = EcoMintLight, shape = RoundedCornerShape(50)) {
                                    Text(
                                        lote.estado,
                                        color = EcoForestGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Text(lote.nombre, fontSize = 20.sp, fontWeight = FontWeight.Black, color = EcoTextPrimary)
                        Text(lote.descripcion, fontSize = 13.sp, color = EcoTextMuted, lineHeight = 18.sp)

                        HorizontalDivider(color = EcoBorder, modifier = Modifier.padding(vertical = 4.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Stock Disponible", fontSize = 11.sp, color = EcoTextMuted)
                                Text("${lote.cantidadDisponible} ${lote.unidadMedida}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EcoTextPrimary)
                            }
                            Column {
                                Text("Unidad Mínima", fontSize = 11.sp, color = EcoTextMuted)
                                Text("${lote.unidadMinima} ${lote.unidadMedida}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EcoTextPrimary)
                            }
                            Column {
                                Text("Múltiplo de Entrega", fontSize = 11.sp, color = EcoTextMuted)
                                Text("${lote.multiplo} ${lote.unidadMedida}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EcoTextPrimary)
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Estado del Alimento: ${lote.estadoAlimento ?: "FRESCO"}", fontSize = 12.sp, color = EcoTextMuted)
                            Text("Donante: ${lote.donanteNombre ?: "Comercio"}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EcoForestGreen)
                        }
                    }
                }

                // GESTIÓN DE BORRADOR PARA DONANTE
                if (rolUsuario.equals("DONANTE", ignoreCase = true) && esBorrador) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = EcoSurface),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, EcoBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.HourglassTop, contentDescription = null, tint = EcoForestGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Control de Publicación (Borrador)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EcoTextPrimary)
                            }

                            if (orgVerificada) {
                                Text(
                                    "Tu organización ya está verificada por el Administrador. Puedes publicar este lote para que aparezca visible inmediatamente en el catálogo oficial de donaciones.",
                                    fontSize = 12.sp,
                                    color = EcoTextMuted,
                                    lineHeight = 17.sp
                                )

                                if (mensajePublicacionBorrador != null) {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = EcoMintGreen),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EcoForestGreen)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(mensajePublicacionBorrador!!, color = EcoForestGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Button(
                                    onClick = {
                                        viewModel.publicarBorrador(lote.id) {
                                            mensajePublicacionBorrador = "¡Lote publicado exitosamente en el catálogo oficial!"
                                        }
                                    },
                                    enabled = !uiState.isSubmitting,
                                    colors = ButtonDefaults.buttonColors(containerColor = EcoForestGreen),
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.fillMaxWidth().height(50.dp)
                                ) {
                                    if (uiState.isSubmitting) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                    } else {
                                        Icon(Icons.Default.Publish, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Publicar Lote Ahora", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                    }
                                }
                            } else {
                                Surface(
                                    color = Color(0xFFFFFBEB),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Tu perfil de donante está pendiente de verificación por el Administrador. Podrás publicar este borrador tan pronto como tu organización sea aprobada.",
                                            fontSize = 11.sp,
                                            color = Color(0xFF92400E),
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // DATOS ENMASCARADOS DE RECOGIDA (RF04 / PRIVACIDAD)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (lote.instruccionesRecogida != null) EcoMintLight else EcoSurface
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, if (lote.instruccionesRecogida != null) EcoMintGreen else EcoBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (lote.instruccionesRecogida != null) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (lote.instruccionesRecogida != null) EcoForestGreen else EcoOliveGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (lote.instruccionesRecogida != null) "Instrucciones de Retiro Físico (Asignado)" else "Logística de Recogida (Enmascarada)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = EcoTextPrimary
                            )
                        }

                        if (lote.instruccionesRecogida != null) {
                            Text("📍 Ubicación: ${lote.ubicacion ?: "Sede Donante"}", fontSize = 12.sp, color = EcoTextPrimary)
                            Text("📋 Indicaciones: ${lote.instruccionesRecogida}", fontSize = 12.sp, color = EcoTextMuted)
                            Text("📞 Contacto: ${lote.contacto ?: "No especificado"}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EcoForestGreen)
                        } else {
                            Text(
                                "Por protocolo de seguridad y equidad, la dirección exacta, horario de muelle y contacto solo se revelan a la fundación una vez que la solicitud sea formalmente asignada.",
                                fontSize = 11.sp,
                                color = EcoTextMuted,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // DIFUSIÓN EN NOTICIAS DE INICIO (PARA COORDINADOR Y ADMIN)
                val esCoordinadorOAdmin = rolUsuario.contains("COORD", ignoreCase = true) || rolUsuario.contains("ADMIN", ignoreCase = true)
                if (esCoordinadorOAdmin && !esBorrador) {
                    val noticiasManager = remember { com.example.alimentared.data.noticias.NoticiasManager.getInstance() }
                    var esDestacado by remember { mutableStateOf(noticiasManager.esLoteDestacado(lote.id)) }
                    var feedbackMsg by remember { mutableStateOf<String?>(null) }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = if (esDestacado) Color(0xFFFFFBEB) else EcoSurface),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, if (esDestacado) EcoAmber else EcoBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = EcoAmber, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Destacar Donación en Noticias de Inicio", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EcoTextPrimary)
                            }
                            Text(
                                if (esDestacado) {
                                    "Esta donación está actualmente publicada como noticia destacada en la pantalla de inicio para todos los usuarios."
                                } else {
                                    "Como coordinador puedes destacar esta donación activa para darle máxima visibilidad en la sección inicial de noticias."
                                },
                                fontSize = 12.sp,
                                color = EcoTextMuted,
                                lineHeight = 16.sp
                            )

                            if (feedbackMsg != null) {
                                Surface(
                                    color = if (esDestacado) EcoMintGreen else EcoBackground,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(feedbackMsg!!, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (esDestacado) EcoForestGreen else EcoTextMuted, modifier = Modifier.padding(8.dp))
                                }
                            }

                            Button(
                                onClick = {
                                    val agregado = noticiasManager.toggleDestacarLote(lote)
                                    esDestacado = agregado
                                    feedbackMsg = if (agregado) {
                                        "¡Donación destacada exitosamente en la pantalla de inicio!"
                                    } else {
                                        "La donación fue retirada de las noticias de inicio."
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (esDestacado) EcoAmber else EcoOliveGreen
                                ),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Icon(
                                    imageVector = if (esDestacado) Icons.Default.Star else Icons.Default.StarOutline,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (esDestacado) "⭐ Destacada en Noticias (Quitar)" else "⭐ Destacar en Noticias de Inicio",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // CALCULADORA DE PORCIONES Y RESERVA (RF05, RN05, RN10) - PARA RECEPTORAS
                if (rolUsuario.equals("RECEPTORA", ignoreCase = true)) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = EcoSurface),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, EcoBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Calculadora de Reserva de Donación", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EcoTextPrimary)
                            Text("Calcula las raciones garantizando no dejar remanentes inútiles.", fontSize = 12.sp, color = EcoTextMuted)

                            if (!orgVerificada) {
                                Surface(
                                    color = Color(0xFFFFFBEB),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Tu fundación aún no ha sido aprobada por el Administrador. Podrás solicitar raciones una vez que tu cuenta sea verificada.",
                                            color = Color(0xFF92400E),
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = uiState.cantidadInput,
                                onValueChange = { viewModel.onCantidadCambiada(it) },
                                label = { Text("Cantidad a Solicitar (${lote.unidadMedida})") },
                                singleLine = true,
                                enabled = orgVerificada,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = if (uiState.esValido) EcoForestGreen else EcoRed,
                                    focusedLabelColor = if (uiState.esValido) EcoForestGreen else EcoRed,
                                    focusedContainerColor = EcoSurface,
                                    unfocusedContainerColor = EcoSurface
                                )
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("🍽️ Raciones estimadas: ${uiState.porcionesEstimadas}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EcoTextPrimary)
                                Text("📦 Remanente: ${uiState.remanenteLote} ${lote.unidadMedida}", fontSize = 12.sp, color = if (uiState.remanenteLote < lote.unidadMinima && uiState.remanenteLote > 0) EcoRed else EcoTextMuted)
                            }

                            if (uiState.mensajeValidacion != null) {
                                Text(
                                    "⚠️ ${uiState.mensajeValidacion}",
                                    color = EcoRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else if (uiState.esValido) {
                                Text(
                                    "✅ Cumple con la unidad mínima (${lote.unidadMinima}), múltiplos (${lote.multiplo}) y remanente útil.",
                                    color = EcoForestGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            if (uiState.errorOperacion != null) {
                                Text(
                                    "❌ ${uiState.errorOperacion}",
                                    color = EcoRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (uiState.solicitudExitosa) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = EcoMintGreen),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EcoForestGreen)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("¡Solicitud enviada exitosamente! Quedó en espera de asignación.", color = EcoForestGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // BOTÓN PÍLDORA SOLICITAR DONACIÓN ESTILO ECO ABASTO
                            Button(
                                onClick = { viewModel.enviarSolicitud() },
                                enabled = orgVerificada && uiState.esValido && !uiState.isSubmitting && !uiState.solicitudExitosa,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = EcoForestGreen)
                            ) {
                                if (uiState.isSubmitting) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                } else {
                                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        if (orgVerificada) "Solicitar Donación" else "Pendiente de Aprobación",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
