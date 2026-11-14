package com.example.alimentared.ui.screens

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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alimentared.ui.components.AlimentaRedTopBar
import com.example.alimentared.ui.theme.*
import com.example.alimentared.ui.viewmodels.PublishLoteViewModel

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

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoriaId by remember { mutableStateOf<Long?>(null) }
    var cantidad by remember { mutableStateOf("30") }
    var unidadMinima by remember { mutableStateOf("5") }
    var multiplo by remember { mutableStateOf("5") }
    var estadoAlimento by remember { mutableStateOf("FRESCO") }
    var horasVigencia by remember { mutableStateOf(8) }
    var ubicacion by remember { mutableStateOf("") }
    var instrucciones by remember { mutableStateOf("") }
    var contacto by remember { mutableStateOf("") }

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

            Text("Vigencia de Retiro:", fontSize = 12.sp, color = EcoTextPrimary, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(4, 8, 12, 24, 48).forEach { h ->
                    val sel = (horasVigencia == h)
                    FilterChip(
                        selected = sel,
                        onClick = { horasVigencia = h },
                        shape = RoundedCornerShape(50),
                        label = { Text(if (h == 4) "⚡ $h h (Urgente)" else "$h horas", fontSize = 11.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (h == 4) EcoAmber else EcoMintGreen,
                            selectedLabelColor = if (h == 4) Color.White else EcoForestGreen,
                            containerColor = EcoSurface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = sel,
                            borderColor = if (sel) (if (h == 4) EcoAmber else EcoForestGreen) else EcoBorder
                        )
                    )
                }
            }

            Text("Logística Segura (Enmascarada hasta asignación):", fontSize = 12.sp, color = EcoTextMuted, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Ubicación de recogida") },
                placeholder = { Text("Ej: Calle 123, Local 4") },
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
                        horasVigencia = horasVigencia,
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
}
