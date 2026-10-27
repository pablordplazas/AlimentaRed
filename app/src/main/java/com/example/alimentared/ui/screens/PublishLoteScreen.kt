package com.example.alimentared.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alimentared.data.model.FakeDataRepository
import com.example.alimentared.data.model.LoteDto
import com.example.alimentared.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishLoteScreen(onBack: () -> Unit) {
    var nombreAlimento by remember { mutableStateOf("") }
    var tipoComida by remember { mutableStateOf("") }
    var cantidadTotal by remember { mutableStateOf("") }
    var unidadMedida by remember { mutableStateOf("Kg") }
    var multiploMinimo by remember { mutableStateOf("5") }
    var horasExpiracion by remember { mutableStateOf("24") }
    var categoriaExpandida by remember { mutableStateOf(false) }
    var categoriaSeleccionada by remember { mutableStateOf("Verduras") }

    val categoriasList = listOf("Verduras", "Lácteos", "Panadería", "Abarrotes", "Carnes / Proteínas")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicar Donación", color = Color.White, fontWeight = FontWeight.Bold) },
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Detalles del Lote de Alimentos", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BlueDark)

            // NOMBRE DEL ALIMENTO
            OutlinedTextField(
                value = nombreAlimento,
                onValueChange = { nombreAlimento = it },
                label = { Text("Nombre del Alimento") },
                placeholder = { Text("Ej. Tomate Chonto Maduro") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedPrimary, focusedLabelColor = RedPrimary)
            )

            // CATEGORÍA (DROPDOWN)
            ExposedDropdownMenuBox(
                expanded = categoriaExpandida,
                onExpandedChange = { categoriaExpandida = !categoriaExpandida }
            ) {
                OutlinedTextField(
                    value = categoriaSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría de Alimento") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpandida) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedPrimary, focusedLabelColor = RedPrimary)
                )
                ExposedDropdownMenu(
                    expanded = categoriaExpandida,
                    onDismissRequest = { categoriaExpandida = false }
                ) {
                    categoriasList.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                categoriaSeleccionada = cat
                                categoriaExpandida = false
                            }
                        )
                    }
                }
            }

            // TIPO DE COMIDA DEGLOSADO
            OutlinedTextField(
                value = tipoComida,
                onValueChange = { tipoComida = it },
                label = { Text("Tipo de Comida / Especificación") },
                placeholder = { Text("Ej. Perecedero / Enlatado / Fresco") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedPrimary, focusedLabelColor = RedPrimary)
            )

            // CANTIDAD Y UNIDAD SEPARADAS (FILA)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = cantidadTotal,
                    onValueChange = { cantidadTotal = it },
                    label = { Text("Cantidad Total") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedPrimary, focusedLabelColor = RedPrimary)
                )
                OutlinedTextField(
                    value = unidadMedida,
                    onValueChange = { unidadMedida = it },
                    label = { Text("Unidad (Ej. Kg / Cajas)") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedPrimary, focusedLabelColor = RedPrimary)
                )
            }

            // MÚLTIPLO MÍNIMO Y HORAS EXPIRACIÓN
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = multiploMinimo,
                    onValueChange = { multiploMinimo = it },
                    label = { Text("Múltiplo Mínimo") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedPrimary, focusedLabelColor = RedPrimary)
                )
                OutlinedTextField(
                    value = horasExpiracion,
                    onValueChange = { horasExpiracion = it },
                    label = { Text("Horas p/ Expirar") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedPrimary, focusedLabelColor = RedPrimary)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (nombreAlimento.isNotBlank() && cantidadTotal.isNotBlank()) {
                        FakeDataRepository.agregarLote(
                            LoteDto(
                                id = System.currentTimeMillis(),
                                nombre = nombreAlimento,
                                donante = "Mi Comercio Donante",
                                categoria = categoriaSeleccionada,
                                tipoAlimento = tipoComida.ifBlank { "General" },
                                cantidadDisponible = cantidadTotal.toDoubleOrNull() ?: 10.0,
                                unidad = unidadMedida,
                                multiploMinimo = multiploMinimo.toDoubleOrNull() ?: 1.0,
                                horasParaExpirar = horasExpiracion.toIntOrNull() ?: 24,
                                esUrgente = (horasExpiracion.toIntOrNull() ?: 24) <= 4
                            )
                        )
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Publicar Donación en la Red", fontWeight = FontWeight.Bold)
            }
        }
    }
}
