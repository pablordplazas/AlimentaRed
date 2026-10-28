package com.example.alimentared.ui.screens

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
import com.example.alimentared.data.model.OrganizacionResponse
import com.example.alimentared.data.remote.RetrofitClient
import com.example.alimentared.ui.components.AlimentaRedBackground
import com.example.alimentared.ui.components.BackgroundPatternType
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

    // Filtro: 0 = Pendientes de Aprobación, 1 = Verificadas, 2 = Todas
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
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Verificación de Organizaciones",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            "Aprobación de Donantes y Receptores",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { cargarOrganizaciones() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlueDark)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        AlimentaRedBackground(
            modifier = Modifier.padding(padding),
            patternType = BackgroundPatternType.PERFIL
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Banner Explicativo
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Control y Autorización de Perfiles", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BlueDark)
                            Text(
                                "Como Administrador, aprueba perfiles para que los comercios puedan publicar donaciones activas y las fundaciones puedan solicitar alimentos.",
                                fontSize = 11.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }

                // Filtro por Estado (Pendientes / Verificadas / Todas)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = filtroVerificacion == 0,
                        onClick = { filtroVerificacion = 0 },
                        label = { Text("Pendientes", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.HourglassTop, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RedPrimary.copy(alpha = 0.15f),
                            selectedLabelColor = RedPrimary
                        )
                    )
                    FilterChip(
                        selected = filtroVerificacion == 1,
                        onClick = { filtroVerificacion = 1 },
                        label = { Text("Verificadas", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFDCFCE7),
                            selectedLabelColor = Color(0xFF14532D)
                        )
                    )
                    FilterChip(
                        selected = filtroVerificacion == 2,
                        onClick = { filtroVerificacion = 2 },
                        label = { Text("Todas", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BlueDark.copy(alpha = 0.15f),
                            selectedLabelColor = BlueDark
                        )
                    )
                }

                // Buscador
                OutlinedTextField(
                    value = searchNombre,
                    onValueChange = { searchNombre = it },
                    placeholder = { Text("Buscar por nombre, NIT o correo...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    trailingIcon = if (searchNombre.isNotBlank()) {
                        { IconButton(onClick = { searchNombre = "" }) { Icon(Icons.Default.Close, contentDescription = null, tint = Color.Gray) } }
                    } else null,
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                // Mensajes de Éxito / Error
                if (mensajeExito != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(mensajeExito!!, color = Color(0xFF14532D), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (mensajeError != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = RedPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(mensajeError!!, color = RedDark, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Lista de Organizaciones
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = RedPrimary)
                    }
                } else if (listaFiltrada.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Business, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                if (filtroVerificacion == 0) "No hay organizaciones pendientes de verificación." else "No se encontraron organizaciones.",
                                color = Color.Gray,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(listaFiltrada) { org ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
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
                                            fontSize = 15.sp,
                                            color = BlueDark,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (org.verificada) {
                                            Surface(
                                                color = Color(0xFFDCFCE7),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    "✓ Verificada",
                                                    color = Color(0xFF14532D),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
                                        } else {
                                            Surface(
                                                color = Color(0xFFFEF3C7),
                                                shape = RoundedCornerShape(6.dp)
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

                                    HorizontalDivider(color = Color(0xFFF1F5F9))

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("NIT: ${org.nit}", fontSize = 12.sp, color = Color.DarkGray)
                                        if (org.telefono != null) {
                                            Text("Tel: ${org.telefono}", fontSize = 12.sp, color = Color.Gray)
                                        }
                                    }

                                    Text("Correo: ${org.correo}", fontSize = 12.sp, color = Color.Gray)

                                    if (org.ubicacion != null) {
                                        Text("Ubicación: ${org.ubicacion}", fontSize = 12.sp, color = Color.Gray)
                                    }

                                    if (org.bloqueada) {
                                        Surface(color = Color(0xFFFEE2E2), shape = RoundedCornerShape(6.dp)) {
                                            Text(
                                                "Cuenta Suspendida (${org.strikesVigentes} strikes)",
                                                color = RedDark,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    // Botón de Verificación si no está verificada
                                    if (!org.verificada) {
                                        val isVerificando = orgIdVerificando == org.id
                                        Button(
                                            onClick = { verificarOrganizacion(org) },
                                            enabled = !isVerificando,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            if (isVerificando) {
                                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                                            } else {
                                                Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Aprobar y Habilitar Organización", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    } else {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Habilitada para operar en AlimentaRed", color = Color(0xFF16A34A), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
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
}