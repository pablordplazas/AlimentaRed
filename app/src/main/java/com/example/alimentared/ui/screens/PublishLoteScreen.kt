package com.example.alimentared.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import com.example.alimentared.ui.theme.*
import com.example.alimentared.ui.viewmodels.PublishLoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishLoteScreen(
    viewModel: PublishLoteViewModel = viewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

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

    // Seleccionar primera categoría por defecto
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicar Lote de Alimentos", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlueDark)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Detalles de la Oferta (RF03)", fontWeight = FontWeight.Black, fontSize = 16.sp, color = BlueDark)

            if (uiState.error != null) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)), modifier = Modifier.fillMaxWidth()) {
                    Text(uiState.error!!, color = RedDark, fontSize = 12.sp, modifier = Modifier.padding(10.dp))
                }
            }

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Título del lote (ej. Pan Campesino, Sopa)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedPrimary, focusedLabelColor = RedPrimary)
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción (estado, empacado, refrigeración)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedPrimary, focusedLabelColor = RedPrimary)
            )

            Text("Categoría del Alimento:", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.categorias) { cat ->
                    FilterChip(
                        selected = (categoriaId == cat.id),
                        onClick = { categoriaId = cat.id },
                        label = { Text(cat.nombre, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RedPrimary.copy(alpha = 0.15f),
                            selectedLabelColor = RedPrimary
                        )
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { cantidad = it },
                    label = { Text("Cantidad Total") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedPrimary)
                )
                OutlinedTextField(
                    value = unidadMinima,
                    onValueChange = { unidadMinima = it },
                    label = { Text("Unidad Mínima") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedPrimary)
                )
                OutlinedTextField(
                    value = multiplo,
                    onValueChange = { multiplo = it },
                    label = { Text("Múltiplo") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedPrimary)
                )
            }

            Text("Vigencia / Horas para Caducar:", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(4 to "4h (Rescate Urgente)", 8 to "8h", 12 to "12h", 24 to "24h").forEach { (h, lbl) ->
                    FilterChip(
                        selected = (horasVigencia == h),
                        onClick = { horasVigencia = h },
                        label = { Text(lbl, fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (h == 4) RedPrimary else BlueDark,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Dirección / Ubicación del punto de retiro") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedPrimary)
            )

            OutlinedTextField(
                value = instrucciones,
                onValueChange = { instrucciones = it },
                label = { Text("Instrucciones de Recogida (Enmascaradas hasta asignación)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedPrimary)
            )

            OutlinedTextField(
                value = contacto,
                onValueChange = { contacto = it },
                label = { Text("Persona o Teléfono de Contacto") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedPrimary)
            )

            Spacer(modifier = Modifier.height(10.dp))

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
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Icon(Icons.Default.Publish, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publicar Donación en Catálogo", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
