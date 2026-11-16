package com.example.alimentared.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.example.alimentared.data.lotes.LoteImagenesManager
import com.example.alimentared.data.model.LoteResponse
import com.example.alimentared.data.session.SessionManager
import com.example.alimentared.ui.theme.*
import com.example.alimentared.ui.viewmodels.FiltroEstadoMisLotes
import com.example.alimentared.ui.viewmodels.LotesViewModel
import com.example.alimentared.ui.viewmodels.ModoVistaLotes
import com.example.alimentared.util.VencimientoUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonacionesTabScreen(
    rolUsuario: String,
    lotesViewModel: LotesViewModel = viewModel(),
    onLoteClick: (Long) -> Unit,
    onCrearLoteClick: () -> Unit
) {
    val uiState by lotesViewModel.uiState.collectAsState()
    val esDonante = rolUsuario.equals("DONANTE", ignoreCase = true)
    val usuarioSesion = remember { SessionManager.getInstance().getUsuario() }
    val estaVerificada = usuarioSesion?.organizacion?.verificada ?: false

    var menuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(rolUsuario) {
        if (esDonante) {
            lotesViewModel.setModoVista(ModoVistaLotes.MIS_PUBLICACIONES)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EcoBackground)
    ) {
        // ENCABEZADO CON ARCO SUAVE VERDE MENTA (ESTILO ECO ABASTO)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = EcoMintGreen,
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "¡Hola, ${usuarioSesion?.nombre?.split(" ")?.firstOrNull() ?: "Usuario"}!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = EcoTextPrimary
                    )
                    Text(
                        text = if (esDonante) {
                            if (uiState.modoVista == ModoVistaLotes.MIS_PUBLICACIONES) "Tus Publicaciones" else "Catálogo General"
                        } else "Catálogo de Donaciones",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EcoOliveGreen
                    )
                }

                if (esDonante) {
                    Box {
                        Surface(
                            shape = CircleShape,
                            color = EcoForestGreen,
                            modifier = Modifier
                                .size(44.dp)
                                .clickable { menuExpanded = true }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Add, contentDescription = "Opciones", tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(Icons.Default.AddCircle, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(20.dp))
                                },
                                text = {
                                    Column {
                                        Text("Crear Publicación", fontWeight = FontWeight.Bold, color = EcoTextPrimary, fontSize = 13.sp)
                                        Text("Donar nuevo lote (o guardar borrador)", fontSize = 10.sp, color = EcoTextMuted)
                                    }
                                },
                                onClick = {
                                    menuExpanded = false
                                    onCrearLoteClick()
                                }
                            )
                            HorizontalDivider(color = EcoBorder)

                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Inventory,
                                        contentDescription = null,
                                        tint = if (uiState.modoVista == ModoVistaLotes.MIS_PUBLICACIONES) EcoForestGreen else EcoTextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                text = {
                                    Column {
                                        Text(
                                            "Ver Mis Publicaciones",
                                            fontWeight = FontWeight.Bold,
                                            color = if (uiState.modoVista == ModoVistaLotes.MIS_PUBLICACIONES) EcoForestGreen else EcoTextPrimary,
                                            fontSize = 13.sp
                                        )
                                        Text("Borradores y lotes de mi organización", fontSize = 10.sp, color = EcoTextMuted)
                                    }
                                },
                                onClick = {
                                    menuExpanded = false
                                    lotesViewModel.setModoVista(ModoVistaLotes.MIS_PUBLICACIONES)
                                }
                            )

                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Storefront,
                                        contentDescription = null,
                                        tint = if (uiState.modoVista == ModoVistaLotes.CATALOGO_GENERAL) EcoForestGreen else EcoTextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                text = {
                                    Column {
                                        Text(
                                            "Ver Catálogo General",
                                            fontWeight = FontWeight.Bold,
                                            color = if (uiState.modoVista == ModoVistaLotes.CATALOGO_GENERAL) EcoForestGreen else EcoTextPrimary,
                                            fontSize = 13.sp
                                        )
                                        Text("Explorar lotes activos en la red", fontSize = 10.sp, color = EcoTextMuted)
                                    }
                                },
                                onClick = {
                                    menuExpanded = false
                                    lotesViewModel.setModoVista(ModoVistaLotes.CATALOGO_GENERAL)
                                }
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // AVISO DE VERIFICACIÓN PENDIENTE
            if (esDonante && !estaVerificada) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Cuenta en proceso de verificación: Puedes registrar lotes como BORRADOR. El administrador habilitará tu publicación una vez verificada.",
                            fontSize = 11.sp,
                            color = Color(0xFF92400E),
                            lineHeight = 15.sp
                        )
                    }
                }
            } else if (rolUsuario.equals("RECEPTORA", ignoreCase = true) && !estaVerificada) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Cuenta en verificación: Puedes explorar el catálogo; una vez aprobada tu fundación por el Administrador, podrás solicitar donaciones.",
                            fontSize = 11.sp,
                            color = Color(0xFF92400E),
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // BARRA DE BÚSQUEDA PÍLDORA (ESTILO ECO ABASTO)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { lotesViewModel.actualizarBusqueda(it) },
                    placeholder = { Text("Ej. Manzanas, pan, verduras...", fontSize = 13.sp, color = EcoTextMuted) },
                    trailingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = EcoForestGreen)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EcoForestGreen,
                        unfocusedBorderColor = EcoBorder,
                        focusedContainerColor = EcoSurface,
                        unfocusedContainerColor = EcoSurface,
                        cursorColor = EcoForestGreen
                    )
                )

                if (esDonante) {
                    Surface(
                        shape = CircleShape,
                        color = EcoForestGreen,
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { onCrearLoteClick() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Add, contentDescription = "Crear Lote", tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // CHIPS DE FILTRO PÍLDORA ESTILO ECO ABASTO
            if (esDonante && uiState.modoVista == ModoVistaLotes.MIS_PUBLICACIONES) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FiltroEstadoMisLotes.values().forEach { filtro ->
                        val seleccionado = uiState.filtroEstadoMisLotes == filtro
                        FilterChip(
                            selected = seleccionado,
                            onClick = { lotesViewModel.setFiltroEstadoMisLotes(filtro) },
                            shape = RoundedCornerShape(50),
                            label = {
                                Text(
                                    filtro.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EcoMintGreen,
                                selectedLabelColor = EcoForestGreen,
                                containerColor = EcoSurface
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = seleccionado,
                                borderColor = if (seleccionado) EcoForestGreen else EcoBorder
                            )
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = uiState.soloUrgentes,
                        onClick = { lotesViewModel.toggleUrgentes() },
                        shape = RoundedCornerShape(50),
                        label = { Text("⚡ Prioridad / Por Vencer", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EcoAmber,
                            selectedLabelColor = Color.White,
                            containerColor = EcoSurface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = uiState.soloUrgentes,
                            borderColor = if (uiState.soloUrgentes) EcoAmber else EcoBorder
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(uiState.categorias) { cat ->
                            val sel = (uiState.categoriaSeleccionada == cat.id)
                            FilterChip(
                                selected = sel,
                                onClick = { lotesViewModel.seleccionarCategoria(cat.id) },
                                shape = RoundedCornerShape(50),
                                label = { Text(cat.nombre, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EcoMintGreen,
                                    selectedLabelColor = EcoForestGreen,
                                    containerColor = EcoSurface
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = sel,
                                    borderColor = if (sel) EcoForestGreen else EcoBorder
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ALERTA DE PRIORIDAD AUTOMÁTICA SI HAY LOTES PRÓXIMOS A VENCER
            val lotesPrioritarios = remember(uiState.lotes) {
                uiState.lotes.filter { VencimientoUtils.esPrioridadAlta(it.fechaCaducidad, it.rescateUrgente) }
            }

            if (lotesPrioritarios.isNotEmpty() && !uiState.isLoading) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EcoAmber.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, EcoAmber),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(shape = CircleShape, color = EcoAmber, modifier = Modifier.size(34.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "¡Alerta de Prioridad Automática!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = EcoAmberDark
                            )
                            Text(
                                "Se detectaron ${lotesPrioritarios.size} ${if (lotesPrioritarios.size == 1) "lote próximo a caducar" else "lotes próximos a caducar"}. ¡Prioriza su solicitud para evitar el desperdicio!",
                                fontSize = 11.sp,
                                color = EcoTextPrimary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // LISTA DE LOTES / TARJETAS
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = EcoForestGreen)
                }
            } else if (uiState.lotes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EcoMintLight,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Inventory2,
                                    contentDescription = null,
                                    tint = EcoForestGreen,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            if (esDonante && uiState.modoVista == ModoVistaLotes.MIS_PUBLICACIONES) {
                                when (uiState.filtroEstadoMisLotes) {
                                    FiltroEstadoMisLotes.BORRADORES -> "No tienes lotes guardados como borrador"
                                    FiltroEstadoMisLotes.PUBLICADAS -> "No tienes lotes publicados en el catálogo"
                                    else -> "Aún no has registrado ninguna publicación"
                                }
                            } else {
                                "No hay lotes disponibles en esta categoría"
                            },
                            fontWeight = FontWeight.Bold,
                            color = EcoTextPrimary,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            if (esDonante && uiState.modoVista == ModoVistaLotes.MIS_PUBLICACIONES) {
                                "Crea un nuevo lote para guardarlo como borrador o publicarlo en el catálogo oficial."
                            } else {
                                "Intenta ajustar tus filtros o búsqueda."
                            },
                            color = EcoTextMuted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )

                        if (esDonante) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onCrearLoteClick,
                                colors = ButtonDefaults.buttonColors(containerColor = EcoForestGreen),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.height(46.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Publicar lote Ahora", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(uiState.lotes) { lote ->
                        LoteCardItem(
                            lote = lote,
                            esDonante = esDonante,
                            onClick = { onLoteClick(lote.id) },
                            onEditarClick = { onLoteClick(lote.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoteCardItem(
    lote: LoteResponse,
    esDonante: Boolean = false,
    onClick: () -> Unit,
    onEditarClick: () -> Unit = onClick
) {
    val esBorrador = lote.estado.equals("BORRADOR", ignoreCase = true)
    val esDisponible = lote.estado.equals("DISPONIBLE", ignoreCase = true)

    val fotosLote = remember(lote.id) {
        LoteImagenesManager.obtenerFotosParaLote(lote.id, lote.nombre, lote.categoriaNombre)
    }
    val fotoPrincipal = fotosLote.firstOrNull() ?: ""

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = EcoSurface),
        border = BorderStroke(1.dp, EcoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Fotografía real del lote con indicador de cantidad de fotos
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(EcoMintLight)
                ) {
                    if (fotoPrincipal.isNotBlank()) {
                        SubcomposeAsyncImage(
                            model = fotoPrincipal,
                            contentDescription = lote.nombre,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            loading = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = EcoForestGreen,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            },
                            error = {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    val icon = when {
                                        lote.nombre.contains("pan", ignoreCase = true) -> Icons.Default.BakeryDining
                                        lote.nombre.contains("manzana", ignoreCase = true) || lote.nombre.contains("fruta", ignoreCase = true) -> Icons.Default.Eco
                                        lote.nombre.contains("verdura", ignoreCase = true) || lote.nombre.contains("papa", ignoreCase = true) -> Icons.Default.Grass
                                        else -> Icons.Default.Restaurant
                                    }
                                    Icon(icon, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(32.dp))
                                }
                            }
                        )

                        // Badge con número de fotos si tiene más de 1 foto
                        if (fotosLote.size > 1) {
                            Surface(
                                shape = RoundedCornerShape(topStart = 8.dp),
                                color = Color.Black.copy(alpha = 0.65f),
                                modifier = Modifier.align(Alignment.BottomEnd)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.PhotoLibrary,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${fotosLote.size}",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    } else {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            val icon = when {
                                lote.nombre.contains("pan", ignoreCase = true) -> Icons.Default.BakeryDining
                                lote.nombre.contains("manzana", ignoreCase = true) || lote.nombre.contains("fruta", ignoreCase = true) -> Icons.Default.Eco
                                lote.nombre.contains("verdura", ignoreCase = true) || lote.nombre.contains("papa", ignoreCase = true) -> Icons.Default.Grass
                                else -> Icons.Default.Restaurant
                            }
                            Icon(icon, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(32.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        lote.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = EcoTextPrimary
                    )
                    Text(
                        "Stock: ${lote.cantidadDisponible} ${lote.unidadMedida ?: "Kg"}",
                        fontSize = 12.sp,
                        color = EcoTextMuted,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // BADGES PÍLDORA (Categoría en menta, Vencimiento dinámico en ámbar/color)
                    val infoVencimiento = remember(lote.fechaCaducidad, lote.rescateUrgente) {
                        VencimientoUtils.calcularInfoVencimiento(lote.fechaCaducidad, lote.rescateUrgente)
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = EcoMintGreen
                        ) {
                            Text(
                                (lote.categoriaNombre ?: "Alimento"),
                                color = EcoForestGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        // BADGE DE FOTOS DISPONIBLES
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFFEFF6FF)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.PhotoCamera,
                                    contentDescription = null,
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    if (fotosLote.size == 1) "1 foto" else "${fotosLote.size} fotos",
                                    color = Color(0xFF1D4ED8),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // BADGE DE PRIORIDAD / VENCIMIENTO AUTOMÁTICO
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = infoVencimiento.badgeColor
                        ) {
                            Text(
                                infoVencimiento.badgeTexto,
                                color = infoVencimiento.badgeTextColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        if (esBorrador) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    "Borrador",
                                    color = Color(0xFF92400E),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // BOTONES DE ACCIÓN EN TARJETA (DISPOSICIÓN ECO ABASTO: PÍLDORAS LADO A LADO)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Botón Secundario (Verde Salvia terroso #BEC69D)
                Button(
                    onClick = onEditarClick,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EcoSageGreen,
                        contentColor = EcoTextPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        if (esBorrador) "Editar Lote" else "Detalles",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = EcoTextPrimary
                    )
                }

                // Botón Primario (Verde Bosque intenso #02542D)
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EcoForestGreen,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(42.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        if (esDonante) "Ver publicación" else "Solicitar Donación",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
