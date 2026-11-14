package com.example.alimentared.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.alimentared.data.model.OrganizacionResponse
import com.example.alimentared.data.remote.RetrofitClient
import com.example.alimentared.ui.components.AlimentaRedTopBar
import com.example.alimentared.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanelVerificacionOrganizacionesScreen(
    onBack: (() -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()

    var organizaciones by remember { mutableStateOf<List<OrganizacionResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }

    var filtroVerificacion by remember { mutableStateOf(0) }
    var searchNombre by remember { mutableStateOf("") }
    var orgIdVerificando by remember { mutableStateOf<Long?>(null) }

    fun cargarOrganizaciones() {
        coroutineScope.launch {
            isLoading = true
            mensajeError = null
            try {
                val filtroParam = when (filtroVerificacion) {
                    0 -> false
                    1 -> true
                    else -> null
                }
                val res = RetrofitClient.instance.getOrganizaciones(verificada = filtroParam, page = 0, size = 100)
                if (res.isSuccessful && res.body() != null) {
                    organizaciones = res.body()!!.contenido
                } else {
                    mensajeError = "No se pudieron cargar las organizaciones (${res.code()})"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(filtroVerificacion) {
        cargarOrganizaciones()
    }

    fun verificarOrganizacion(org: OrganizacionResponse) {
        coroutineScope.launch {
            orgIdVerificando = org.id
            mensajeError = null
            mensajeExito = null
            try {
                val res = RetrofitClient.instance.verificarOrganizacion(org.id)
                if (res.isSuccessful) {
                    mensajeExito = "¡Organización \"${org.nombre}\" aprobada y habilitada exitosamente!"
                    cargarOrganizaciones()
                } else {
                    mensajeError = "No se pudo verificar la organización (${res.code()})"
                }
            } catch (e: Exception) {
                mensajeError = "Error al verificar: ${e.localizedMessage}"
            } finally {
                orgIdVerificando = null
            }
        }
    }

    val listaFiltrada = if (searchNombre.isBlank()) {
        organizaciones
    } else {
        organizaciones.filter {
            it.nombre.contains(searchNombre.trim(), ignoreCase = true) ||
                    it.nit.contains(searchNombre.trim(), ignoreCase = true) ||
                    it.correo.contains(searchNombre.trim(), ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            AlimentaRedTopBar(
                title = {
                    Column {
                        Text(
                            "Verificación de Organizaciones",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            "Aprobación de Donantes y Receptores",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp
                        )
                    }
                },
                navigationIcon = if (onBack != null) {
                    {
                        IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                } else {
                    {}
                },
                actions = {
                    IconButton(onClick = { cargarOrganizaciones() }, modifier = Modifier.size(36.dp)) {
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
            // Banner Explicativo Estilo Eco Abasto
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = EcoSurface),
                border = BorderStroke(1.dp, EcoBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = EcoMintLight,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(24.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Control y Autorización de Perfiles", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EcoTextPrimary)
                        Text(
                            "Como Administrador, aprueba perfiles para habilitar publicaciones de donantes y solicitudes de fundaciones.",
                            fontSize = 11.sp,
                            color = EcoTextMuted
                        )
                    }
                }
            }

            // Filtro por Estado (Pendientes / Verificadas / Todas) PÍLDORAS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filtroVerificacion == 0,
                    onClick = { filtroVerificacion = 0 },
                    shape = RoundedCornerShape(50),
                    label = { Text("Pendientes", fontSize = 11.sp, fontWeight = if (filtroVerificacion == 0) FontWeight.Bold else FontWeight.Normal) },
                    leadingIcon = { Icon(Icons.Default.HourglassTop, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EcoMintGreen,
                        selectedLabelColor = EcoForestGreen,
                        containerColor = EcoSurface
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = filtroVerificacion == 0,
                        borderColor = if (filtroVerificacion == 0) EcoForestGreen else EcoBorder
                    )
                )
                FilterChip(
                    selected = filtroVerificacion == 1,
                    onClick = { filtroVerificacion = 1 },
                    shape = RoundedCornerShape(50),
                    label = { Text("Verificadas", fontSize = 11.sp, fontWeight = if (filtroVerificacion == 1) FontWeight.Bold else FontWeight.Normal) },
                    leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EcoMintGreen,
                        selectedLabelColor = EcoForestGreen,
                        containerColor = EcoSurface
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = filtroVerificacion == 1,
                        borderColor = if (filtroVerificacion == 1) EcoForestGreen else EcoBorder
                    )
                )
                FilterChip(
                    selected = filtroVerificacion == 2,
                    onClick = { filtroVerificacion = 2 },
                    shape = RoundedCornerShape(50),
                    label = { Text("Todas", fontSize = 11.sp, fontWeight = if (filtroVerificacion == 2) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EcoMintGreen,
                        selectedLabelColor = EcoForestGreen,
                        containerColor = EcoSurface
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = filtroVerificacion == 2,
                        borderColor = if (filtroVerificacion == 2) EcoForestGreen else EcoBorder
                    )
                )
            }

            // Buscador Píldora
            OutlinedTextField(
                value = searchNombre,
                onValueChange = { searchNombre = it },
                placeholder = { Text("Buscar por nombre, NIT o correo...", fontSize = 12.sp, color = EcoTextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EcoForestGreen) },
                trailingIcon = if (searchNombre.isNotBlank()) {
                    { IconButton(onClick = { searchNombre = "" }) { Icon(Icons.Default.Close, contentDescription = null, tint = EcoTextMuted) } }
                } else null,
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

            // Mensajes de Éxito / Error
            if (mensajeExito != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EcoMintGreen),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EcoForestGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(mensajeExito!!, color = EcoForestGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (mensajeError != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EcoRedLight),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = EcoRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(mensajeError!!, color = EcoRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Lista de Organizaciones
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = EcoForestGreen)
                }
            } else if (listaFiltrada.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = EcoTextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            if (filtroVerificacion == 0) "No hay organizaciones pendientes de verificación." else "No se encontraron organizaciones.",
                            color = EcoTextMuted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listaFiltrada) { org ->
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = EcoSurface),
                            border = BorderStroke(1.dp, EcoBorder),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        org.nombre,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = EcoTextPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (org.verificada) {
                                        Surface(
                                            color = EcoMintGreen,
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Text(
                                                "✓ Verificada",
                                                color = EcoForestGreen,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    } else {
                                        Surface(
                                            color = Color(0xFFFEF3C7),
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Text(
                                                "⏳ Pendiente",
                                                color = Color(0xFF92400E),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }

                                HorizontalDivider(color = EcoBorder)

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("NIT: ${org.nit}", fontSize = 12.sp, color = EcoTextPrimary)
                                    if (org.telefono != null) {
                                        Text("Tel: ${org.telefono}", fontSize = 12.sp, color = EcoTextMuted)
                                    }
                                }

                                Text("Correo: ${org.correo}", fontSize = 12.sp, color = EcoTextMuted)

                                if (org.ubicacion != null) {
                                    Text("Ubicación: ${org.ubicacion}", fontSize = 12.sp, color = EcoTextMuted)
                                }

                                if (org.bloqueada) {
                                    Surface(color = EcoRedLight, shape = RoundedCornerShape(50)) {
                                        Text(
                                            "Cuenta Suspendida (${org.strikesVigentes} strikes)",
                                            color = EcoRed,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                // Botón de Verificación si no está verificada
                                if (!org.verificada) {
                                    val isVerificando = orgIdVerificando == org.id
                                    Button(
                                        onClick = { verificarOrganizacion(org) },
                                        enabled = !isVerificando,
                                        colors = ButtonDefaults.buttonColors(containerColor = EcoForestGreen),
                                        shape = RoundedCornerShape(50),
                                        modifier = Modifier.fillMaxWidth().height(44.dp)
                                    ) {
                                        if (isVerificando) {
                                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                                        } else {
                                            Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Aprobar y Habilitar Organización", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Habilitada para operar en Eco Abasto", color = EcoForestGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
