package com.example.alimentared.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alimentared.data.model.LoteResponse
import com.example.alimentared.data.session.SessionManager
import com.example.alimentared.ui.theme.*
import com.example.alimentared.ui.viewmodels.FiltroEstadoMisLotes
import com.example.alimentared.ui.viewmodels.LotesViewModel
import com.example.alimentared.ui.viewmodels.ModoVistaLotes

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

    // Si el usuario es donante, iniciar en la vista de Mis Publicaciones para ver sus borradores y lotes
    LaunchedEffect(rolUsuario) {
        if (esDonante) {
            lotesViewModel.setModoVista(ModoVistaLotes.MIS_PUBLICACIONES)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // ENCABEZADO CON BOTÓN DESPLEGABLE PARA DONANTE
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    if (esDonante) {
                        if (uiState.modoVista == ModoVistaLotes.MIS_PUBLICACIONES) "Mis Publicaciones" else "Catálogo General"
                    } else "Donaciones Disponibles",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = BlueDark
                )
                Text(
                    if (esDonante) {
                        if (uiState.modoVista == ModoVistaLotes.MIS_PUBLICACIONES) {
                            "Borradores y lotes registrados por tu organización"
                        } else {
                            "Lotes disponibles en la red AlimentaRed"
                        }
                    } else {
                        "Inventario sincronizado en tiempo real"
                    },
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // BOTÓN DESPLEGABLE DE PUBLICACIONES PARA DONANTE
            if (esDonante) {
                var menuExpanded by remember { mutableStateOf(false) }

                Box {
                    Button(
                        onClick = { menuExpanded = true },
                        colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            if (uiState.modoVista == ModoVistaLotes.MIS_PUBLICACIONES) "Mis Lotes" else "Catálogo",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Opciones", modifier = Modifier.size(18.dp))
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        // 1. Crear Publicación
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(Icons.Default.AddCircle, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(20.dp))
                            },
                            text = {
                                Column {
                                    Text("Crear Publicación", fontWeight = FontWeight.Bold, color = BlueDark, fontSize = 13.sp)
                                    Text("Donar nuevo lote (o guardar borrador)", fontSize = 10.sp, color = Color.Gray)
                                }
                            },
                            onClick = {
                                menuExpanded = false
                                onCrearLoteClick()
                            }
                        )
                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        // 2. Ver Mis Publicaciones (Borradores y Publicadas)
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Inventory,
                                    contentDescription = null,
                                    tint = if (uiState.modoVista == ModoVistaLotes.MIS_PUBLICACIONES) RedPrimary else BlueDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            text = {
                                Column {
                                    Text(
                                        "Ver Mis Publicaciones",
                                        fontWeight = FontWeight.Bold,
                                        color = if (uiState.modoVista == ModoVistaLotes.MIS_PUBLICACIONES) RedPrimary else BlueDark,
                                        fontSize = 13.sp
                                    )
                                    Text("Borradores y lotes de mi organización", fontSize = 10.sp, color = Color.Gray)
                                }
                            },
                            onClick = {
                                menuExpanded = false
                                lotesViewModel.setModoVista(ModoVistaLotes.MIS_PUBLICACIONES)
                            }
                        )

                        // 3. Ver Catálogo General
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = if (uiState.modoVista == ModoVistaLotes.CATALOGO_GENERAL) RedPrimary else BlueDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            text = {
                                Column {
                                    Text(
                                        "Ver Catálogo General",
                                        fontWeight = FontWeight.Bold,
                                        color = if (uiState.modoVista == ModoVistaLotes.CATALOGO_GENERAL) RedPrimary else BlueDark,
                                        fontSize = 13.sp
                                    )
                                    Text("Explorar lotes activos en la red", fontSize = 10.sp, color = Color.Gray)
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

        // AVISO DE VERIFICACIÓN PENDIENTE
        val usuarioSesion = remember { SessionManager.getInstance().getUsuario() }
        val estaVerificada = usuarioSesion?.organizacion?.verificada ?: false

        if (esDonante && !estaVerificada) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Cuenta en proceso de verificación: Puedes registrar lotes como BORRADOR. El administrador debe verificar tu organización para habilitar la publicación en el catálogo.",
                        fontSize = 11.sp,
                        color = Color(0xFF92400E),
                        lineHeight = 15.sp
                    )
                }
            }
        } else if (rolUsuario.equals("RECEPTORA", ignoreCase = true) && !estaVerificada) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Cuenta en proceso de verificación: Puedes explorar el catálogo; una vez aprobada tu organización por el Administrador, podrás solicitar donaciones.",
                        fontSize = 11.sp,
                        color = Color(0xFF92400E),
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // BARRA DE BÚSQUEDA
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { lotesViewModel.actualizarBusqueda(it) },
            placeholder = { Text("Buscar alimento (ej. pan, verduras, sopa)...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            trailingIcon = {
                if (uiState.searchQuery.isNotBlank()) {
                    IconButton(onClick = {
                        lotesViewModel.actualizarBusqueda("")
                        lotesViewModel.ejecutarBusqueda()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.Gray)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = RedPrimary,
                unfocusedBorderColor = Color(0xFFCBD5E1),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // CHIPS DE FILTRO DEPENDIENDO DEL MODO
        if (esDonante && uiState.modoVista == ModoVistaLotes.MIS_PUBLICACIONES) {
            // Filtros de estado de mis publicaciones: Todas / Borradores / Publicadas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FiltroEstadoMisLotes.values().forEach { filtro ->
                    val seleccionado = uiState.filtroEstadoMisLotes == filtro
                    FilterChip(
                        selected = seleccionado,
                        onClick = { lotesViewModel.setFiltroEstadoMisLotes(filtro) },
                        label = {
                            Text(
                                filtro.label,
                                fontSize = 11.sp,
                                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = if (seleccionado) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (filtro == FiltroEstadoMisLotes.BORRADORES) Color(0xFFFEF3C7) else RedPrimary.copy(alpha = 0.15f),
                            selectedLabelColor = if (filtro == FiltroEstadoMisLotes.BORRADORES) Color(0xFF92400E) else RedDark,
                            containerColor = Color.White
                        )
                    )
                }
            }
        } else {
            // Catálogo general: Filtro de Categorías y Rescate Urgente (RF04)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chip de Rescate Urgente (RF04)
                FilterChip(
                    selected = uiState.soloUrgentes,
                    onClick = { lotesViewModel.toggleUrgentes() },
                    label = { Text("⚡ Rescate Urgente (≤ 4h)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RedPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = uiState.soloUrgentes,
                        borderColor = if (uiState.soloUrgentes) RedPrimary else RedPrimary.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Categorías dinámicas
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(uiState.categorias) { cat ->
                        val sel = (uiState.categoriaSeleccionada == cat.id)
                        FilterChip(
                            selected = sel,
                            onClick = { lotesViewModel.seleccionarCategoria(cat.id) },
                            label = { Text(cat.nombre, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BlueDark,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // CONTENIDO / LISTA DE LOTES
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RedPrimary)
            }
        } else if (uiState.lotes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
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
                        color = BlueDark,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        if (esDonante && uiState.modoVista == ModoVistaLotes.MIS_PUBLICACIONES) {
                            "Crea un nuevo lote para guardarlo como borrador o publicarlo en el catálogo oficial."
                        } else {
                            "Intenta ajustar tus filtros o búsqueda."
                        },
                        color = Color.Gray,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )

                    if (esDonante) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onCrearLoteClick,
                            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Crear Publicación", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(uiState.lotes) { lote ->
                    LoteCardItem(
                        lote = lote,
                        onClick = { onLoteClick(lote.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun LoteCardItem(
    lote: LoteResponse,
    onClick: () -> Unit
) {
    val esBorrador = lote.estado.equals("BORRADOR", ignoreCase = true)
    val esDisponible = lote.estado.equals("DISPONIBLE", ignoreCase = true)

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    (lote.categoriaNombre ?: "ALIMENTO").uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = RedPrimary
                )

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Badge de Borrador o Publicada
                    if (esBorrador) {
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, Color(0xFFFDE68A))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF92400E), modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    "BORRADOR",
                                    color = Color(0xFF92400E),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else if (esDisponible) {
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, Color(0xFF86EFAC))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF14532D), modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    "PUBLICADA",
                                    color = Color(0xFF14532D),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                lote.estado,
                                color = BlueDark,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Badge de rescate urgente si vence en menos de 4 horas (RF04)
                    if (lote.rescateUrgente) {
                        Surface(
                            color = Color(0xFFFEE2E2),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "¡Rescate Urgente!",
                                    color = RedDark,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(lote.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BlueDark)
            Text(lote.descripcion, maxLines = 2, fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Donado por: ${lote.donanteNombre ?: "Comercio Donante"}", fontSize = 11.sp, color = TextMuted)
                    Text(
                        "Stock: ${lote.cantidadDisponible} ${lote.unidadMedida ?: "KG"}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BlueDark
                    )
                }

                Surface(
                    color = if (esBorrador) Color(0xFFFEF3C7) else RedPrimary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        if (esBorrador) "Revisar / Publicar" else "Ver Detalle",
                        color = if (esBorrador) Color(0xFF92400E) else RedPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}
