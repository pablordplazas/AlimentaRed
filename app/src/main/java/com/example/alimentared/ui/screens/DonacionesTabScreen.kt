package com.example.alimentared.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alimentared.data.model.LoteResponse
import com.example.alimentared.ui.theme.*
import com.example.alimentared.ui.viewmodels.LotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonacionesTabScreen(
    rolUsuario: String,
    lotesViewModel: LotesViewModel = viewModel(),
    onLoteClick: (Long) -> Unit,
    onCrearLoteClick: () -> Unit
) {
    val uiState by lotesViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Encabezado con Botón de Donar si es Donante
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    if (rolUsuario.equals("DONANTE", ignoreCase = true)) "Catálogo y Mis Ofertas" else "Donaciones Disponibles",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = BlueDark
                )
                Text("Inventario sincronizado en tiempo real", fontSize = 11.sp, color = Color.Gray)
            }

            if (rolUsuario.equals("DONANTE", ignoreCase = true)) {
                Button(
                    onClick = onCrearLoteClick,
                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Publicar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Barra de Búsqueda
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

        Spacer(modifier = Modifier.height(10.dp))

        // Filtro de Categorías y Badge de Rescate Urgente
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

        Spacer(modifier = Modifier.height(12.dp))

        // Contenido / Lista de Lotes
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RedPrimary)
            }
        } else if (uiState.lotes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Inventory2, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No hay lotes disponibles en esta categoría", color = Color.Gray, fontSize = 13.sp)
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
                    color = RedPrimary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Ver Detalle",
                        color = RedPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}
