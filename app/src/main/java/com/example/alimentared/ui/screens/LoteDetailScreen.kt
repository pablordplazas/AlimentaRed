package com.example.alimentared.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.example.alimentared.data.lotes.LoteImagenesManager
import com.example.alimentared.data.session.SessionManager
import com.example.alimentared.ui.components.AlimentaRedTopBar
import com.example.alimentared.ui.theme.*
import com.example.alimentared.ui.viewmodels.LoteDetailViewModel
import com.example.alimentared.util.VencimientoUtils
import kotlinx.coroutines.launch

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
    var mostrarMapaVisor by remember { mutableStateOf(false) }

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
            val puedeVerUbicacionExacta = (lote.instruccionesRecogida != null) ||
                    rolUsuario.contains("DONANTE", ignoreCase = true) ||
                    rolUsuario.contains("COORD", ignoreCase = true) ||
                    rolUsuario.contains("ADMIN", ignoreCase = true)

            val fotosLote = remember(lote.id) {
                LoteImagenesManager.obtenerFotosParaLote(lote.id, lote.nombre, lote.categoriaNombre)
            }
            val pagerState = rememberPagerState(initialPage = 0, pageCount = { fotosLote.size.coerceAtLeast(1) })
            val coroutineScope = rememberCoroutineScope()
            var fotoSeleccionadaParaZoom by remember { mutableStateOf<String?>(null) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // SECCIÓN DE FOTOGRAFÍAS DEL LOTE (INSPECCIÓN VISUAL PARA RECEPTORES - MÍN 1, MÁX 3 FOTOS)
                Card(
                    colors = CardDefaults.cardColors(containerColor = EcoSurface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, EcoBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                                .background(EcoMintLight)
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                val fotoUrl = fotosLote.getOrNull(page) ?: ""
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable {
                                            if (fotoUrl.isNotBlank()) {
                                                fotoSeleccionadaParaZoom = fotoUrl
                                            }
                                        }
                                ) {
                                    if (fotoUrl.isNotBlank()) {
                                        SubcomposeAsyncImage(
                                            model = fotoUrl,
                                            contentDescription = "Foto ${page + 1} de ${lote.nombre}",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize(),
                                            loading = {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    CircularProgressIndicator(
                                                        color = EcoForestGreen,
                                                        modifier = Modifier.size(36.dp)
                                                    )
                                                }
                                            },
                                            error = {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        Icons.Default.Restaurant,
                                                        contentDescription = null,
                                                        tint = EcoForestGreen,
                                                        modifier = Modifier.size(48.dp)
                                                    )
                                                }
                                            }
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Restaurant,
                                                contentDescription = null,
                                                tint = EcoForestGreen,
                                                modifier = Modifier.size(48.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Badge superior izquierdo: Verificación Sanitaria
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.Black.copy(alpha = 0.65f),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = EcoMintGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Foto del Lote (${fotosLote.size} ${if (fotosLote.size == 1) "foto" else "fotos"})",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Badge superior derecho: Contador de página / foto actual
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.Black.copy(alpha = 0.65f),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "${pagerState.currentPage + 1} / ${fotosLote.size}",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }

                            // Badge inferior derecho: Toca para ampliar
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = EcoForestGreen.copy(alpha = 0.9f),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(12.dp)
                                    .clickable {
                                        val currentFoto = fotosLote.getOrNull(pagerState.currentPage)
                                        if (!currentFoto.isNullOrBlank()) {
                                            fotoSeleccionadaParaZoom = currentFoto
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.ZoomIn,
                                        contentDescription = "Ampliar foto",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Toca para ampliar",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Indicador de puntos (dots) centrado abajo si hay más de 1 foto
                            if (fotosLote.size > 1) {
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    repeat(fotosLote.size) { index ->
                                        val activo = (pagerState.currentPage == index)
                                        Box(
                                            modifier = Modifier
                                                .height(6.dp)
                                                .width(if (activo) 18.dp else 6.dp)
                                                .clip(RoundedCornerShape(50))
                                                .background(if (activo) Color.White else Color.White.copy(alpha = 0.5f))
                                        )
                                    }
                                }
                            }
                        }

                        // Tira de miniaturas interactivas si tiene múltiples fotos (2 o 3 fotos)
                        if (fotosLote.size > 1) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                fotosLote.forEachIndexed { index, url ->
                                    val esActual = (pagerState.currentPage == index)
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(if (esActual) 2.dp else 1.dp, if (esActual) EcoForestGreen else EcoBorder),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(56.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                coroutineScope.launch {
                                                    pagerState.animateScrollToPage(index)
                                                }
                                            }
                                    ) {
                                        SubcomposeAsyncImage(
                                            model = url,
                                            contentDescription = "Miniatura ${index + 1}",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }

                        // Mensaje de garantía para el receptor
                        Surface(
                            color = EcoMintGreen.copy(alpha = 0.35f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = EcoForestGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Fotos reales del donante: certifican empaque, estado del alimento y fecha de caducidad.",
                                    fontSize = 11.sp,
                                    color = EcoTextPrimary,
                                    lineHeight = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
                // ALERTA DE PRIORIDAD AUTOMÁTICA O RESCATE URGENTE
                val infoVencimiento = remember(lote.fechaCaducidad, lote.rescateUrgente) {
                    VencimientoUtils.calcularInfoVencimiento(lote.fechaCaducidad, lote.rescateUrgente)
                }

                if (infoVencimiento.esPrioridadAlta || lote.rescateUrgente) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = EcoAmber.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, EcoAmber),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = EcoAmber, modifier = Modifier.size(36.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "¡Alerta de Prioridad Automática!",
                                    color = EcoAmberDark,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    infoVencimiento.alertaMensaje ?: "Este lote vence en ${infoVencimiento.tiempoRestanteTexto}. Prioridad máxima de retiro para evitar desperdicio.",
                                    color = EcoTextPrimary,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            }
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
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
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

                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = infoVencimiento.badgeColor
                                ) {
                                    Text(
                                        infoVencimiento.badgeTexto,
                                        color = infoVencimiento.badgeTextColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
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

                        // DETALLES ESPECÍFICOS DE VENCIMIENTO Y CADUCIDAD
                        HorizontalDivider(color = EcoBorder, modifier = Modifier.padding(vertical = 4.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Fecha de Caducidad", fontSize = 11.sp, color = EcoTextMuted)
                                Text(
                                    infoVencimiento.fechaFormateada,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (infoVencimiento.esPrioridadAlta) EcoAmberDark else EcoTextPrimary
                                )
                            }
                            Column {
                                Text("Vigencia Restante", fontSize = 11.sp, color = EcoTextMuted)
                                Text(
                                    infoVencimiento.tiempoRestanteTexto,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (infoVencimiento.esPrioridadAlta) EcoAmberDark else EcoForestGreen
                                )
                            }
                            Column {
                                Text("Nivel de Prioridad", fontSize = 11.sp, color = EcoTextMuted)
                                Text(
                                    if (infoVencimiento.esPrioridadAlta) "⚡ ALTA" else "NORMAL",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (infoVencimiento.esPrioridadAlta) EcoAmberDark else EcoForestGreen
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EcoBackground,
                                border = BorderStroke(1.dp, EcoBorder)
                            ) {
                                Text(
                                    "Estado: ${lote.estadoAlimento ?: "FRESCO"}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = EcoTextMuted,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EcoMintLight
                            ) {
                                Text(
                                    "Donante: ${if (puedeVerUbicacionExacta) (lote.donanteNombre ?: "Comercio Donante") else "Comercio Aliado"}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EcoForestGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
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

                // 3. LOGÍSTICA DE RECOGIDA Y UBICACIÓN CON MAPA INTERACTIVO (SOLO VISIBLE SI ES DONANTE, ADMIN/COORD O TRAS ASIGNACIÓN FORMAL)
                if (puedeVerUbicacionExacta) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = EcoMintLight),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, EcoMintGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.LockOpen,
                                    contentDescription = null,
                                    tint = EcoForestGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Ubicación y Logística de Recogida",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = EcoTextPrimary
                                )
                            }

                            // Mini-mapa interactivo para ver la ubicación exacta
                            com.example.alimentared.ui.components.UbicacionLogisticaCard(
                                ubicacionTexto = lote.ubicacion,
                                esEditable = false,
                                titulo = "Punto de Recogida: ${lote.nombre}",
                                subtitulo = lote.ubicacion ?: "Sede Donante",
                                onAbrirVisor = { mostrarMapaVisor = true }
                            )

                            if (!lote.instruccionesRecogida.isNullOrBlank()) {
                                Text("📋 Indicaciones: ${lote.instruccionesRecogida}", fontSize = 12.sp, color = EcoTextMuted)
                            }
                            if (!lote.contacto.isNullOrBlank()) {
                                Text("📞 Contacto operativo: ${lote.contacto}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EcoForestGreen)
                            }
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

            if (fotoSeleccionadaParaZoom != null) {
                Dialog(
                    onDismissRequest = { fotoSeleccionadaParaZoom = null },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.94f))
                            .clickable { fotoSeleccionadaParaZoom = null }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Barra superior del visor con título y botón de cerrar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = lote.nombre,
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    val numFoto = (fotosLote.indexOf(fotoSeleccionadaParaZoom) + 1).coerceAtLeast(1)
                                    Text(
                                        text = "Foto $numFoto de ${fotosLote.size} • Inspección de Inocuidad",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 12.sp
                                    )
                                }

                                IconButton(
                                    onClick = { fotoSeleccionadaParaZoom = null },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Cerrar visor",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            // Imagen en pantalla completa con zoom/ajuste
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                SubcomposeAsyncImage(
                                    model = fotoSeleccionadaParaZoom,
                                    contentDescription = "Foto ampliada de ${lote.nombre}",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize(),
                                    loading = {
                                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(color = Color.White)
                                        }
                                    }
                                )
                            }

                            // Barra inferior informativa
                            Surface(
                                color = Color.Black.copy(alpha = 0.8f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Inspecciona el lote a detalle. Verifica empaque, rotulado, estado del alimento y fecha de vencimiento.",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    val puedeVerMapaVisor = uiState.lote?.let { lote ->
        (lote.instruccionesRecogida != null) ||
                rolUsuario.contains("DONANTE", ignoreCase = true) ||
                rolUsuario.contains("COORD", ignoreCase = true) ||
                rolUsuario.contains("ADMIN", ignoreCase = true)
    } ?: false

    if (mostrarMapaVisor && uiState.lote != null && puedeVerMapaVisor) {
        val lote = uiState.lote!!
        com.example.alimentared.ui.components.MapaVisorDialog(
            ubicacionTexto = lote.ubicacion,
            nombreDonacion = lote.nombre,
            instrucciones = lote.instruccionesRecogida,
            contacto = lote.contacto,
            onDismiss = { mostrarMapaVisor = false }
        )
    }
}
