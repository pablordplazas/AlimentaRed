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
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alimentared.ui.theme.*
import com.example.alimentared.ui.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = viewModel(),
    onLoginExitoso: (String) -> Unit,
    onBack: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()

    var modoRegistro by remember { mutableStateOf(false) }
    var mostrarConfigServidor by remember { mutableStateOf(false) }
    val sessionManager = remember { com.example.alimentared.data.session.SessionManager.getInstance() }
    var servidorUrlActual by remember { mutableStateOf(sessionManager.getServerUrl()) }
    var estadoTestServidor by remember { mutableStateOf<String?>(null) }
    var probandoConexion by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Campos Login
    var email by remember { mutableStateOf("receptora@pancompartido.org") }
    var password by remember { mutableStateOf("Pass12345") }

    // Campos Registro
    var regNit by remember { mutableStateOf("") }
    var regNombreOrg by remember { mutableStateOf("") }
    var regCorreoOrg by remember { mutableStateOf("") }
    var regTelefono by remember { mutableStateOf("") }
    var regUbicacion by remember { mutableStateOf("") }
    var regContacto by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regRol by remember { mutableStateOf("RECEPTORA") }

    // Usuarios de prueba rápidos para evaluación
    val accesosRapidos = listOf(
        Triple("Receptora", "receptora@pancompartido.org", "Pass12345"),
        Triple("Donante", "donante@saborcasero.com", "Pass12345"),
        Triple("Coordinador", "coordinador@alimentared.com", "Coord12345"),
        Triple("Admin", "admin@alimentared.com", "Admin12345"),
        Triple("Suspendida (RN08)", "suspendido@abuelitospaz.org", "Pass12345")
    )

    LaunchedEffect(uiState.isSuccess, uiState.user) {
        if (uiState.isSuccess && uiState.user != null) {
            onLoginExitoso(uiState.user!!.rol)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (modoRegistro) "Registro de Organización" else "Portal de Acceso",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarConfigServidor = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Configurar Servidor", tint = Color.White)
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título y selector de modo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (modoRegistro) "Nueva Cuenta" else "Iniciar Sesión",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = BlueDark
                )
                TextButton(onClick = {
                    modoRegistro = !modoRegistro
                    authViewModel.limpiarError()
                }) {
                    Text(
                        if (modoRegistro) "Ya tengo cuenta" else "Registrar Org",
                        color = RedPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Badge Servidor Activo
            Surface(
                onClick = { mostrarConfigServidor = true },
                shape = RoundedCornerShape(8.dp),
                color = BlueDark.copy(alpha = 0.05f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Dns, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Servidor: $servidorUrlActual",
                        fontSize = 11.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.weight(1f)
                    )
                    Text("Cambiar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RedPrimary)
                }
            }

            // Mensaje de Error / Alerta si existe
            if (uiState.error != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = RedPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            uiState.error!!,
                            color = RedDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (!modoRegistro) {
                // SECCIÓN LOGIN
                Text("Cuentas de prueba rápida:", color = Color.Gray, fontSize = 12.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(accesosRapidos) { (label, correo, pass) ->
                        FilterChip(
                            selected = (email == correo),
                            onClick = {
                                email = correo
                                password = pass
                                authViewModel.limpiarError()
                            },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RedPrimary.copy(alpha = 0.15f),
                                selectedLabelColor = RedPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPrimary,
                        focusedLabelColor = RedPrimary
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPrimary,
                        focusedLabelColor = RedPrimary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { authViewModel.login(email, password) },
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
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ingresar a AlimentaRed", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            } else {
                // SECCIÓN REGISTRO (RF02)
                Text("Tipo de Organización:", color = Color.Gray, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilterChip(
                        selected = regRol == "RECEPTORA",
                        onClick = { regRol = "RECEPTORA" },
                        label = { Text("Fundación Receptora") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RedPrimary.copy(alpha = 0.15f), selectedLabelColor = RedPrimary)
                    )
                    FilterChip(
                        selected = regRol == "DONANTE",
                        onClick = { regRol = "DONANTE" },
                        label = { Text("Comercio Donante") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RedPrimary.copy(alpha = 0.15f), selectedLabelColor = RedPrimary)
                    )
                }

                OutlinedTextField(
                    value = regNit,
                    onValueChange = { regNit = it },
                    label = { Text("NIT / Identificación Fiscal") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedPrimary, focusedLabelColor = RedPrimary)
                )

                OutlinedTextField(
                    value = regNombreOrg,
                    onValueChange = { regNombreOrg = it },
                    label = { Text("Nombre de la Organización") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedPrimary, focusedLabelColor = RedPrimary)
                )

                OutlinedTextField(
                    value = regCorreoOrg,
                    onValueChange = { regCorreoOrg = it },
                    label = { Text("Correo Institucional") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedPrimary, focusedLabelColor = RedPrimary)
                )

                OutlinedTextField(
                    value = regContacto,
                    onValueChange = { regContacto = it },
                    label = { Text("Nombre del Representante") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedPrimary, focusedLabelColor = RedPrimary)
                )

                OutlinedTextField(
                    value = regPassword,
                    onValueChange = { regPassword = it },
                    label = { Text("Contraseña (mínimo 6 caracteres)") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedPrimary, focusedLabelColor = RedPrimary)
                )

                Button(
                    onClick = {
                        authViewModel.register(
                            nit = regNit,
                            orgNombre = regNombreOrg,
                            correoOrg = regCorreoOrg,
                            telefono = regTelefono,
                            ubicacion = regUbicacion,
                            nombreContacto = regContacto,
                            contrasena = regPassword,
                            rol = regRol
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
                        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Crear Cuenta de Organización", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }

    if (mostrarConfigServidor) {
        var urlTemp by remember { mutableStateOf(servidorUrlActual) }

        AlertDialog(
            onDismissRequest = {
                mostrarConfigServidor = false
                estadoTestServidor = null
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = RedPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Configurar Servidor Backend", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Selecciona un preset o ingresa la URL de tu API REST:",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    OutlinedTextField(
                        value = urlTemp,
                        onValueChange = { urlTemp = it },
                        label = { Text("URL Base del API") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RedPrimary,
                            focusedLabelColor = RedPrimary
                        )
                    )

                    Text("Presets Rápidos:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BlueDark)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = urlTemp.contains("192.168.1.18"),
                            onClick = { urlTemp = "http://192.168.1.18:8080/api/" },
                            label = { Text("IP Local", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = urlTemp.contains("10.0.2.2"),
                            onClick = { urlTemp = "http://10.0.2.2:8080/api/" },
                            label = { Text("10.0.2.2", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = urlTemp.contains("127.0.0.1"),
                            onClick = { urlTemp = "http://127.0.0.1:8080/api/" },
                            label = { Text("ADB Reverse", fontSize = 10.sp) }
                        )
                    }

                    if (estadoTestServidor != null) {
                        Text(
                            estadoTestServidor!!,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (estadoTestServidor!!.startsWith("✓")) Color(0xFF16A34A) else RedPrimary
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                probandoConexion = true
                                estadoTestServidor = "Probando conexión..."
                                try {
                                    com.example.alimentared.data.remote.RetrofitClient.updateBaseUrl(urlTemp)
                                    val res = com.example.alimentared.data.remote.RetrofitClient.instance.checkHealth()
                                    if (res.isSuccessful) {
                                        estadoTestServidor = "✓ Conexión Exitosa con el Backend (API UP)"
                                    } else {
                                        estadoTestServidor = "✗ Servidor respondió con código ${res.code()}"
                                    }
                                } catch (e: Exception) {
                                    estadoTestServidor = "✗ Error: ${e.localizedMessage ?: "No se pudo conectar"}"
                                } finally {
                                    probandoConexion = false
                                }
                            }
                        },
                        enabled = !probandoConexion,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (probandoConexion) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Probar Conexión", fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        sessionManager.setServerUrl(urlTemp)
                        servidorUrlActual = sessionManager.getServerUrl()
                        com.example.alimentared.data.remote.RetrofitClient.updateBaseUrl(servidorUrlActual)
                        mostrarConfigServidor = false
                        estadoTestServidor = null
                        authViewModel.limpiarError()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
                ) {
                    Text("Guardar y Aplicar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarConfigServidor = false
                    estadoTestServidor = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
