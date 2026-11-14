package com.example.alimentared.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.alimentared.data.model.AsignacionResponse
import com.example.alimentared.data.noticias.NoticiasManager
import com.example.alimentared.data.remote.RetrofitClient
import com.example.alimentared.ui.components.AlimentaRedTopBar
import com.example.alimentared.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestacarEntregasScreen(
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val noticiasManager = remember { NoticiasManager.getInstance() }
    val noticiasState by noticiasManager.noticiasFlow.collectAsState()

    var asignacionesCompletadas by remember { mutableStateOf<List<AsignacionResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var mensajeFeedback by remember { mutableStateOf<String?>(null) }

    val entregasEjemplo = remember {
        listOf(
            AsignacionResponse(
                id = 101L,
                solicitudId = 201L,
                loteId = 301L,
                loteNombre = "Pan Integral Artesanal",
                donanteNombre = "Panadería Central",
                receptoraNombre = "Fundación Comedor Esperanza",
                cantidadAsignada = 120.0,
                cantidadRetirada = 120.0,
                estado = "COMPLETADA",
                fechaCierre = "Hoy a las 10:30 AM",
                instruccionesRecogida = "Retiro completado en punto de acopio.",
                contactoDonante = "3101234567"
            ),
            AsignacionResponse(
                id = 102L,
                solicitudId = 202L,
                loteId = 302L,
                loteNombre = "Manzanas y Verduras Frescas",
                donanteNombre = "Frutas La Huerta",
                receptoraNombre = "Hogar Santa María",
                cantidadAsignada = 85.0,
                cantidadRetirada = 85.0,
                estado = "COMPLETADA",
                fechaCierre = "Ayer a las 4:15 PM",
                instruccionesRecogida = "Entrega en muelle 2.",
                contactoDonante = "3159876543"
            ),
            AsignacionResponse(
                id = 103L,
                solicitudId = 203L,
                loteId = 303L,
                loteNombre = "Yogurt y Lácteos Varios",
                donanteNombre = "Lácteos del Valle",
                receptoraNombre = "Asociación Niños de Paz",
                cantidadAsignada = 60.0,
                cantidadRetirada = 60.0,
                estado = "COMPLETADA",
                fechaCierre = "Hace 2 días",
                instruccionesRecogida = "Cadena de frío verificada.",
                contactoDonante = "3201122334"
            )
        )
    }

    fun cargarEntregas() {
        coroutineScope.launch {
            isLoading = true
            try {
                val res = RetrofitClient.instance.getAsignaciones()
                if (res.isSuccessful && res.body() != null) {
                    val realesCompletadas = res.body()!!.contenido.filter { it.estado == "COMPLETADA" }
                    val combinadas = (realesCompletadas + entregasEjemplo).distinctBy { it.id }
                    asignacionesCompletadas = combinadas
                } else {
                    asignacionesCompletadas = entregasEjemplo
                }
            } catch (e: Exception) {
                asignacionesCompletadas = entregasEjemplo
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        cargarEntregas()
    }

    val listaFiltrada = remember(asignacionesCompletadas, searchQuery) {
        if (searchQuery.isBlank()) {
            asignacionesCompletadas
        } else {
            val q = searchQuery.trim().lowercase()
            asignacionesCompletadas.filter {
                (it.loteNombre?.lowercase()?.contains(q) == true) ||
                (it.donanteNombre?.lowercase()?.contains(q) == true) ||
                (it.receptoraNombre?.lowercase()?.contains(q) == true)
            }
        }
    }

    Scaffold(
        topBar = {
            AlimentaRedTopBar(
                title = {
                    Column {
                        Text("⭐ Destacar Entregas de Comida", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                        Text("Publicar rescates exitosos en Noticias de Inicio", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                },
                actions = {
                    IconButton(onClick = { cargarEntregas() }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = Color.White, modifier = Modifier.size(20.dp))
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Banner Informativo Estilo Eco Abasto
            Card(
                colors = CardDefaults.cardColors(containerColor = EcoAmber.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, EcoAmber),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = EcoAmber, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            "Difusión de Impacto Institucional",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = EcoAmberDark
                        )
                        Text(
                            "Selecciona una entrega completada para destacarla con la estrella ⭐. Se publicará inmediatamente en la sección de Noticias del Inicio para todos los usuarios.",
                            fontSize = 11.sp,
                            color = EcoTextPrimary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            if (mensajeFeedback != null) {
                Surface(
                    color = EcoMintGreen,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(mensajeFeedback!!, color = EcoForestGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Buscador Píldora
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar entrega por donante, fundación o alimento...", fontSize = 12.sp, color = EcoTextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EcoForestGreen) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = EcoTextMuted)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EcoForestGreen,
                    unfocusedBorderColor = EcoBorder,
                    focusedContainerColor = EcoSurface,
                    unfocusedContainerColor = EcoSurface
                )
            )

            // Lista de entregas completadas
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = EcoForestGreen)
                }
            } else if (listaFiltrada.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.TaskAlt, contentDescription = null, tint = EcoTextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No se encontraron entregas completadas", color = EcoTextMuted, fontSize = 13.sp)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(listaFiltrada) { entrega ->
                        val esDestacada = noticiasManager.esEntregaDestacada(entrega.id)

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = EcoSurface),
                            border = BorderStroke(1.dp, if (esDestacada) EcoAmber else EcoBorder),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "ASIGNACIÓN #${entrega.id}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EcoOliveGreen
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        if (esDestacada) {
                                            Surface(
                                                color = EcoAmber,
                                                shape = RoundedCornerShape(50)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text("EN INICIO", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }

                                        Surface(
                                            color = EcoMintGreen,
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Text(
                                                "COMPLETADA",
                                                color = EcoForestGreen,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(entrega.loteNombre ?: "Lote de Alimentos", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EcoTextPrimary)
                                Text("Donante: ${entrega.donanteNombre ?: "Comercio"}", fontSize = 12.sp, color = EcoTextMuted)
                                Text("Receptora: ${entrega.receptoraNombre ?: "Fundación"}", fontSize = 12.sp, color = EcoTextMuted)

                                val cantFinal = if (entrega.cantidadRetirada > 0) entrega.cantidadRetirada else entrega.cantidadAsignada
                                Text("Cantidad Entregada: $cantFinal Kg", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EcoForestGreen)

                                if (entrega.fechaCierre != null) {
                                    Text("Fecha: ${entrega.fechaCierre}", fontSize = 11.sp, color = EcoTextMuted)
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = EcoBorder)
                                Spacer(modifier = Modifier.height(10.dp))

                                // Botón píldora con la estrella para destacar
                                Button(
                                    onClick = {
                                        val agregado = noticiasManager.toggleDestacarEntrega(entrega)
                                        mensajeFeedback = if (agregado) {
                                            "¡Entrega #${entrega.id} destacada exitosamente en la pantalla de inicio!"
                                        } else {
                                            "La entrega #${entrega.id} fue retirada de las noticias de inicio."
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (esDestacada) EcoAmber else EcoForestGreen
                                    ),
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.fillMaxWidth().height(44.dp)
                                ) {
                                    Icon(
                                        imageVector = if (esDestacada) Icons.Default.Star else Icons.Default.StarOutline,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        if (esDestacada) "⭐ Destacada en Noticias de Inicio" else "⭐ Destacar en Noticias de Inicio",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
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
