package com.example.alimentared.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alimentared.ui.components.AlimentaRedTopBar
import com.example.alimentared.ui.theme.*
import com.example.alimentared.ui.viewmodels.PublishLoteViewModel
import com.example.alimentared.util.VencimientoUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishLoteScreen(
    viewModel: PublishLoteViewModel = viewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val sessionManager = remember { com.example.alimentared.data.session.SessionManager.getInstance() }
    val usuarioActual = remember { sessionManager.getUsuario() }
    val orgVerificada = usuarioActual?.organizacion?.verificada ?: false

    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoriaId by remember { mutableStateOf<Long?>(null) }
    var cantidad by remember { mutableStateOf("30") }
    var unidadMinima by remember { mutableStateOf("5") }
    var multiplo by remember { mutableStateOf("5") }
    var estadoAlimento by remember { mutableStateOf("FRESCO") }
    // Fecha de caducidad por defecto a +7 días
    var fechaCaducidadTexto by remember {
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 7) }
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        mutableStateOf(sdf.format(cal.time))
    }
    var errorFechaCaducidad by remember { mutableStateOf<String?>(null) }
    var ubicacion by remember {
        val orgUbicacion = usuarioActual?.organizacion?.ubicacion
        mutableStateOf<String>(if (!orgUbicacion.isNullOrBlank()) orgUbicacion else "Plaza de Paloquemao, Calle 19 # 25-04, Bogotá [4.61450, -74.08860]")
    }
    var mostrarMapaSelector by remember { mutableStateOf(false) }
    var instrucciones by remember { mutableStateOf("") }
    var contacto by remember { mutableStateOf("") }

    fun abrirCalendario() {
        val calendar = Calendar.getInstance()
        val parsed = VencimientoUtils.parsearFecha(fechaCaducidadTexto)
        if (parsed != null) {
            calendar.time = parsed
        }
        val dialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                fechaCaducidadTexto = sdf.format(cal.time)
                errorFechaCaducidad = null
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.minDate = System.currentTimeMillis()
        dialog.show()
    }

    LaunchedEffect(uiState.categorias) {
        if (categoriaId == null && uiState.categorias.isNotEmpty()) {
            categoriaId = uiState.categorias.first().id
        }
    }

    LaunchedEffect(uiState.publicadoExitoso) {
        if (uiState.publicadoExitoso) {
            onBack()
        }
    }

    val ecoFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = EcoTextPrimary,
        unfocusedTextColor = EcoTextPrimary,
        focusedContainerColor = EcoSurface,
        unfocusedContainerColor = EcoSurface,
        focusedBorderColor = EcoForestGreen,
        unfocusedBorderColor = EcoBorder,
        focusedLabelColor = EcoForestGreen,
        unfocusedLabelColor = EcoTextMuted,
        cursorColor = EcoForestGreen
    )

    Scaffold(
        topBar = {
            AlimentaRedTopBar(
                title = { Text("Formulario de Lote", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ENCABEZADO ESTILO ECO ABASTO
            Column {
                Text(
                    "Formulario de lote",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = EcoTextPrimary
                )
                Text(
                    "Completa la información del ecoabasto para donación",
                    fontSize = 13.sp,
                    color = EcoTextMuted
                )
            }

            if (!orgVerificada) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Modo Borrador: Tu organización está en proceso de verificación. Este lote se guardará como BORRADOR hasta que tu cuenta sea aprobada.",
                            color = Color(0xFF92400E),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            if (uiState.error != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EcoRedLight),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(uiState.error!!, color = EcoRed, fontSize = 12.sp, modifier = Modifier.padding(12.dp))
                }
            }

            // CAMPOS DE ENTRADA CON BORDES REDONDEADOS PÍLDORA (RADIUS 16.DP)
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del alimento") },
                placeholder = { Text("Ej: Arroz integral, Manzanas, Pan") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ecoFieldColors
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción del estado y conservación") },
                placeholder = { Text("Ej: Envasado al vacío, recién horneado") },
                minLines = 2,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ecoFieldColors
            )

            Text("Categoría:", fontSize = 12.sp, color = EcoTextPrimary, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.categorias) { cat ->
                    val sel = (categoriaId == cat.id)
                    FilterChip(
                        selected = sel,
                        onClick = { categoriaId = cat.id },
                        shape = RoundedCornerShape(50),
                        label = { Text(cat.nombre, fontSize = 11.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal) },
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

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { cantidad = it },
                    label = { Text("Cantidad (Kg/L)") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f),
                    colors = ecoFieldColors
                )
                OutlinedTextField(
                    value = unidadMinima,
                    onValueChange = { unidadMinima = it },
                    label = { Text("Mínimo") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f),
                    colors = ecoFieldColors
                )
                OutlinedTextField(
                    value = multiplo,
                    onValueChange = { multiplo = it },
                    label = { Text("Múltiplo") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f),
                    colors = ecoFieldColors
                )
            }

            Text("Estado y Conservación:", fontSize = 12.sp, color = EcoTextPrimary, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("FRESCO", "REFRIGERADO", "CONGELADO", "NO_PERECEDERO").forEach { estado ->
                    val sel = (estadoAlimento == estado)
                    FilterChip(
                        selected = sel,
                        onClick = { estadoAlimento = estado },
                        shape = RoundedCornerShape(50),
                        label = { Text(estado, fontSize = 10.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal) },
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

            // SECCIÓN FECHA DE CADUCIDAD / VENCIMIENTO
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val infoVencimiento = remember(fechaCaducidadTexto) {
                    VencimientoUtils.calcularInfoVencimiento(fechaCaducidadTexto)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Fecha de Vencimiento / Caducidad:",
                        fontSize = 12.sp,
                        color = EcoTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    if (infoVencimiento.esPrioridadAlta && errorFechaCaducidad == null) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = EcoAmber
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    "Prioridad Alta Detectada",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Text(
                    "Ingresa o selecciona la fecha que figura en el empaque (ej. latas de atún, conservas, lácteos).",
                    fontSize = 11.sp,
                    color = EcoTextMuted
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Campo de texto para escribir directamente la fecha o abrir calendario
                OutlinedTextField(
                    value = fechaCaducidadTexto,
                    onValueChange = {
                        fechaCaducidadTexto = it
                        errorFechaCaducidad = VencimientoUtils.validarFechaCaducidad(it)
                    },
                    label = { Text("Fecha de Caducidad (AAAA-MM-DD)") },
                    placeholder = { Text("Ej: 2026-12-31 o 31/12/2026") },
                    singleLine = true,
                    isError = errorFechaCaducidad != null,
                    trailingIcon = {
                        IconButton(onClick = { abrirCalendario() }) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = "Abrir Calendario",
                                tint = EcoForestGreen
                            )
                        }
                    },
                    supportingText = {
                        if (errorFechaCaducidad != null) {
                            Text(errorFechaCaducidad!!, color = EcoRed, fontSize = 11.sp)
                        } else if (infoVencimiento.fechaDate != null) {
                            Text(
                                "Vencimiento: ${infoVencimiento.fechaLargaFormateada} (${infoVencimiento.tiempoRestanteTexto})",
                                color = if (infoVencimiento.esPrioridadAlta) EcoAmberDark else EcoForestGreen,
                                fontSize = 11.sp,
                                fontWeight = if (infoVencimiento.esPrioridadAlta) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ecoFieldColors
                )

                // Botón elegante para abrir calendario
                OutlinedButton(
                    onClick = { abrirCalendario() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, EcoForestGreen),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                ) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = EcoForestGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Elegir Fecha en Calendario",
                        color = EcoForestGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Atajos rápidos según tipo de alimento
                Text("Atajos rápidos sugeridos:", fontSize = 11.sp, color = EcoTextMuted, fontWeight = FontWeight.SemiBold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val atajos = listOf(
                        Pair("⚡ 1 día (Urgente)", 1),
                        Pair("3 días (Perecedero)", 3),
                        Pair("1 semana", 7),
                        Pair("1 mes", 30),
                        Pair("6 meses (Atún/Conservas)", 180),
                        Pair("1 año (No perecedero)", 365)
                    )
                    items(atajos) { (label, dias) ->
                        val sugerida = VencimientoUtils.calcularFechaSugerida(dias)
                        val sel = (fechaCaducidadTexto == sugerida)
                        FilterChip(
                            selected = sel,
                            onClick = {
                                fechaCaducidadTexto = sugerida
                                errorFechaCaducidad = null
                            },
                            shape = RoundedCornerShape(50),
                            label = {
                                Text(
                                    label,
                                    fontSize = 11.sp,
                                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (dias <= 3) EcoAmber else EcoMintGreen,
                                selectedLabelColor = if (dias <= 3) Color.White else EcoForestGreen,
                                containerColor = EcoSurface
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = sel,
                                borderColor = if (sel) (if (dias <= 3) EcoAmber else EcoForestGreen) else EcoBorder
                            )
                        )
                    }
                }

                // Banner de detección automática de prioridad
                if (infoVencimiento.esPrioridadAlta && errorFechaCaducidad == null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = EcoAmber.copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, EcoAmber),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = EcoAmberDark, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "⚡ Detección automática: Este lote vencerá en los próximos días (${infoVencimiento.tiempoRestanteTexto}). La app activará automáticamente la ALERTA DE PRIORIDAD en el catálogo para asegurar su consumo rápido.",
                                color = EcoAmberDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            Text("Logística Segura (Enmascarada hasta asignación):", fontSize = 12.sp, color = EcoTextMuted, fontWeight = FontWeight.Bold)

            // VISTA PREVIA Y ACCESO A MAPA INTERACTIVO
            com.example.alimentared.ui.components.UbicacionLogisticaCard(
                ubicacionTexto = ubicacion,
                esEditable = true,
                titulo = "Punto Logístico de Entrega / Recogida",
                subtitulo = "Toca para fijar la ubicación en el mapa interactivo",
                onAbrirSelector = { mostrarMapaSelector = true }
            )

            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Ubicación de recogida (Dirección y Coordenadas)") },
                placeholder = { Text("Ej: Calle 19 # 25-04 [4.6145, -74.0886]") },
                leadingIcon = {
                    Icon(Icons.Default.Place, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    IconButton(onClick = { mostrarMapaSelector = true }) {
                        Icon(Icons.Default.Map, contentDescription = "Abrir mapa", tint = EcoForestGreen)
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ecoFieldColors
            )

            OutlinedTextField(
                value = instrucciones,
                onValueChange = { instrucciones = it },
                label = { Text("Observaciones / Instrucciones de retiro") },
                placeholder = { Text("Ej: Portón 2, horario 8am a 12m") },
                minLines = 2,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ecoFieldColors
            )

            OutlinedTextField(
                value = contacto,
                onValueChange = { contacto = it },
                label = { Text("Teléfono de contacto operativo") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ecoFieldColors
            )

            Spacer(modifier = Modifier.height(6.dp))

            // BOTÓN DE ACCIÓN PÍLDORA (ESTILO ECO ABASTO)
            Button(
                onClick = {
                    val errFecha = VencimientoUtils.validarFechaCaducidad(fechaCaducidadTexto)
                    if (errFecha != null) {
                        errorFechaCaducidad = errFecha
                        return@Button
                    }
                    val cid = categoriaId ?: 1L
                    val cant = cantidad.toDoubleOrNull() ?: 0.0
                    val umin = unidadMinima.toDoubleOrNull() ?: 0.0
                    val mult = multiplo.toDoubleOrNull() ?: 0.0
                    viewModel.publicarLote(
                        nombre = nombre,
                        descripcion = descripcion,
                        categoriaId = cid,
                        cantidad = cant,
                        unidadMinima = umin,
                        multiplo = mult,
                        estadoAlimento = estadoAlimento,
                        fechaCaducidad = fechaCaducidadTexto,
                        ubicacion = ubicacion,
                        instrucciones = instrucciones,
                        contacto = contacto
                    )
                },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (orgVerificada) EcoForestGreen else EcoSageGreen,
                    contentColor = if (orgVerificada) Color.White else EcoTextPrimary
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Icon(Icons.Default.Publish, contentDescription = null, tint = if (orgVerificada) Color.White else EcoTextPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (orgVerificada) "Publicar lote Ahora" else "Guardar Lote como Borrador",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (orgVerificada) Color.White else EcoTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (mostrarMapaSelector) {
        com.example.alimentared.ui.components.MapaSelectorDialog(
            ubicacionInicial = ubicacion,
            onDismiss = { mostrarMapaSelector = false },
            onUbicacionConfirmada = { puntoSeleccionado ->
                ubicacion = com.example.alimentared.util.LogisticaLocationUtils.formatearUbicacionParaGuardar(
                    puntoSeleccionado.direccion,
                    puntoSeleccionado.latitud,
                    puntoSeleccionado.longitud
                )
                mostrarMapaSelector = false
            }
        )
    }
}
