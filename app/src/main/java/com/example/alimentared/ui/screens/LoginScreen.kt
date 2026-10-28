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
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alimentared.ui.components.AlimentaRedBackground
import com.example.alimentared.ui.components.BackgroundPatternType
import com.example.alimentared.ui.theme.*
import com.example.alimentared.ui.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = viewModel(),
    esModoPruebas: Boolean = false,
    onLoginExitoso: (String) -> Unit,
    onBack: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()

    var modoRegistro by remember { mutableStateOf(false) }

    // Campos Login (en modo normal inician vacíos; en pruebas pueden iniciar con receptora)
    var email by remember { mutableStateOf(if (esModoPruebas) "receptora@pancompartido.org" else "") }
    var password by remember { mutableStateOf(if (esModoPruebas) "Pass12345" else "") }
    var showLoginPassword by remember { mutableStateOf(false) }

    // Campos Registro
    var regNit by remember { mutableStateOf("") }
    var regNombreOrg by remember { mutableStateOf("") }
    var regCorreoOrg by remember { mutableStateOf("") }
    var regTelefono by remember { mutableStateOf("") }
    var regUbicacion by remember { mutableStateOf("") }
    var regContacto by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regRol by remember { mutableStateOf("RECEPTORA") }

    // Estados de validación por campo
    var errorNit by remember { mutableStateOf<String?>(null) }
    var errorNombreOrg by remember { mutableStateOf<String?>(null) }
    var errorCorreoOrg by remember { mutableStateOf<String?>(null) }
    var errorContacto by remember { mutableStateOf<String?>(null) }
    var errorPassword by remember { mutableStateOf<String?>(null) }
    var showRegisterPassword by remember { mutableStateOf(false) }

    fun limpiarErroresLocales() {
        errorNit = null
        errorNombreOrg = null
        errorCorreoOrg = null
        errorContacto = null
        errorPassword = null
    }

    fun validarCamposRegistro(): Boolean {
        limpiarErroresLocales()
        var esValido = true
        var primerError: String? = null

        // 1. NIT: numérico de 6 a 15 dígitos (opcional dígito de verificación con guion)
        val nitLimpio = regNit.trim()
        if (nitLimpio.isBlank()) {
            errorNit = "El NIT es obligatorio"
            if (primerError == null) primerError = "El campo NIT es obligatorio"
            esValido = false
        } else if (!Regex("^[0-9]{6,15}(-[0-9])?$").matches(nitLimpio)) {
            errorNit = "Debe tener entre 6 y 15 dígitos numéricos (ej. 900123456-7)"
            if (primerError == null) primerError = "NIT inválido: debe tener entre 6 y 15 dígitos numéricos (ej. 900123456-7)"
            esValido = false
        }

        // 2. Nombre de la Organización: mínimo 3 caracteres
        val nombreOrgLimpio = regNombreOrg.trim()
        if (nombreOrgLimpio.isBlank()) {
            errorNombreOrg = "El nombre de la organización es obligatorio"
            if (primerError == null) primerError = "El nombre de la organización es obligatorio"
            esValido = false
        } else if (nombreOrgLimpio.length < 3) {
            errorNombreOrg = "El nombre es muy corto (mínimo 3 caracteres)"
            if (primerError == null) primerError = "El nombre de la organización es muy corto (mínimo 3 caracteres)"
            esValido = false
        }

        // 3. Correo Electrónico
        val correoLimpio = regCorreoOrg.trim()
        if (correoLimpio.isBlank()) {
            errorCorreoOrg = "El correo electrónico es obligatorio"
            if (primerError == null) primerError = "El correo electrónico es obligatorio"
            esValido = false
        } else if (!Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(correoLimpio)) {
            errorCorreoOrg = "Formato de correo no válido (ej. contacto@organizacion.com)"
            if (primerError == null) primerError = "Ingresa un correo electrónico válido (ej. contacto@organizacion.com)"
            esValido = false
        }

        // 4. Nombre del Representante: mínimo 3 caracteres
        val contactoLimpio = regContacto.trim()
        if (contactoLimpio.isBlank()) {
            errorContacto = "El nombre del representante es obligatorio"
            if (primerError == null) primerError = "El nombre del representante es obligatorio"
            esValido = false
        } else if (contactoLimpio.length < 3) {
            errorContacto = "El nombre es muy corto (mínimo 3 caracteres)"
            if (primerError == null) primerError = "El nombre del representante es muy corto (mínimo 3 caracteres)"
            esValido = false
        }

        // 5. Contraseña: mínimo 8 caracteres, letras y números
        if (regPassword.isBlank()) {
            errorPassword = "La contraseña es obligatoria"
            if (primerError == null) primerError = "La contraseña es obligatoria"
            esValido = false
        } else if (regPassword.length < 8) {
            errorPassword = "La contraseña es muy corta (mínimo 8 caracteres)"
            if (primerError == null) primerError = "La contraseña es muy corta (debe tener al menos 8 caracteres)"
            esValido = false
        } else if (!regPassword.any { it.isLetter() } || !regPassword.any { it.isDigit() }) {
            errorPassword = "Debe contener tanto letras como números (ej. Pass12345)"
            if (primerError == null) primerError = "La contraseña debe contener letras y números (ej. Pass12345)"
            esValido = false
        }

        if (!esValido && primerError != null) {
            authViewModel.setError(primerError)
        }

        return esValido
    }

    // Cuentas de prueba rápidas corregidas
    val accesosRapidos = listOf(
        Triple("Receptora", "receptora@pancompartido.org", "Pass12345"),
        Triple("Donante", "donante@saborcasero.com", "Pass12345"),
        Triple("Coordinador", "coordinador@alimentared.com", "Coord12345"),
        Triple("Admin", "admin@alimentared.com", "Admin12345"),
        Triple("Panadería Sol (Suspendida)", "suspendido@panaderiasol.com", "Pass12345"),
        Triple("Abuelitos de la Paz (2 Strikes)", "suspendido@abuelitospaz.org", "Pass12345")
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
                        if (modoRegistro) {
                            "Registro de Organización"
                        } else if (esModoPruebas) {
                            "Acceso de Pruebas (Demo)"
                        } else {
                            "Portal de Acceso"
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlueDark)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        AlimentaRedBackground(
            modifier = Modifier.padding(padding),
            patternType = BackgroundPatternType.LOGIN
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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
                    if (modoRegistro) "Nueva Cuenta" else if (esModoPruebas) "Pruebas Rápidas" else "Iniciar Sesión",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = BlueDark
                )
                TextButton(onClick = {
                    modoRegistro = !modoRegistro
                    limpiarErroresLocales()
                    authViewModel.limpiarError()
                }) {
                    Text(
                        if (modoRegistro) "Ya tengo cuenta" else "Registrar Org",
                        color = RedPrimary,
                        fontWeight = FontWeight.Bold
                    )
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
                // SECCIÓN DE PRUEBAS: Sólo visible si esModoPruebas es true
                if (esModoPruebas) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = BlueDark.copy(alpha = 0.05f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Science, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Selecciona una cuenta rápida para simular los roles y estados del sistema:",
                                fontSize = 11.sp,
                                color = Color.DarkGray
                            )
                        }
                    }

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

                    Spacer(modifier = Modifier.height(2.dp))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; authViewModel.limpiarError() },
                    label = { Text("Correo Electrónico") },
                    placeholder = { Text("ejemplo@organizacion.com") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPrimary,
                        focusedLabelColor = RedPrimary
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; authViewModel.limpiarError() },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = if (showLoginPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showLoginPassword = !showLoginPassword }) {
                            Icon(
                                if (showLoginPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showLoginPassword) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = Color.Gray
                            )
                        }
                    },
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
                        Text(
                            if (esModoPruebas) "Probar Ingreso con esta Cuenta" else "Ingresar a AlimentaRed",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else {
                // SECCIÓN REGISTRO (RF02)
                Text("Tipo de Organización:", color = Color.Gray, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilterChip(
                        selected = regRol == "RECEPTORA",
                        onClick = { regRol = "RECEPTORA"; authViewModel.limpiarError() },
                        label = { Text("Fundación Receptora") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RedPrimary.copy(alpha = 0.15f), selectedLabelColor = RedPrimary)
                    )
                    FilterChip(
                        selected = regRol == "DONANTE",
                        onClick = { regRol = "DONANTE"; authViewModel.limpiarError() },
                        label = { Text("Comercio Donante") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RedPrimary.copy(alpha = 0.15f), selectedLabelColor = RedPrimary)
                    )
                }

                // 1. NIT
                OutlinedTextField(
                    value = regNit,
                    onValueChange = {
                        regNit = it
                        errorNit = null
                        authViewModel.limpiarError()
                    },
                    label = { Text("NIT / Identificación Fiscal") },
                    placeholder = { Text("Ej: 900123456-7") },
                    singleLine = true,
                    isError = errorNit != null,
                    supportingText = if (errorNit != null) {
                        { Text(errorNit!!, color = RedPrimary, fontSize = 11.sp) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPrimary,
                        focusedLabelColor = RedPrimary,
                        errorBorderColor = RedPrimary,
                        errorLabelColor = RedPrimary
                    )
                )

                // 2. Nombre de la Organización
                OutlinedTextField(
                    value = regNombreOrg,
                    onValueChange = {
                        regNombreOrg = it
                        errorNombreOrg = null
                        authViewModel.limpiarError()
                    },
                    label = { Text("Nombre de la Organización") },
                    placeholder = { Text("Ej: Nikopolleria") },
                    singleLine = true,
                    isError = errorNombreOrg != null,
                    supportingText = if (errorNombreOrg != null) {
                        { Text(errorNombreOrg!!, color = RedPrimary, fontSize = 11.sp) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPrimary,
                        focusedLabelColor = RedPrimary,
                        errorBorderColor = RedPrimary,
                        errorLabelColor = RedPrimary
                    )
                )

                // 3. Correo Electrónico
                OutlinedTextField(
                    value = regCorreoOrg,
                    onValueChange = {
                        regCorreoOrg = it
                        errorCorreoOrg = null
                        authViewModel.limpiarError()
                    },
                    label = { Text("Correo Electrónico") },
                    placeholder = { Text("ejemplo@organizacion.com") },
                    singleLine = true,
                    isError = errorCorreoOrg != null,
                    supportingText = if (errorCorreoOrg != null) {
                        { Text(errorCorreoOrg!!, color = RedPrimary, fontSize = 11.sp) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPrimary,
                        focusedLabelColor = RedPrimary,
                        errorBorderColor = RedPrimary,
                        errorLabelColor = RedPrimary
                    )
                )

                // 4. Nombre del Representante
                OutlinedTextField(
                    value = regContacto,
                    onValueChange = {
                        regContacto = it
                        errorContacto = null
                        authViewModel.limpiarError()
                    },
                    label = { Text("Nombre del Representante") },
                    placeholder = { Text("Ej: Nicolas Rodriguez") },
                    singleLine = true,
                    isError = errorContacto != null,
                    supportingText = if (errorContacto != null) {
                        { Text(errorContacto!!, color = RedPrimary, fontSize = 11.sp) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPrimary,
                        focusedLabelColor = RedPrimary,
                        errorBorderColor = RedPrimary,
                        errorLabelColor = RedPrimary
                    )
                )

                // 5. Contraseña
                OutlinedTextField(
                    value = regPassword,
                    onValueChange = {
                        regPassword = it
                        errorPassword = null
                        authViewModel.limpiarError()
                    },
                    label = { Text("Contraseña (mínimo 8 caracteres)") },
                    placeholder = { Text("Mínimo 8 letras y números") },
                    singleLine = true,
                    visualTransformation = if (showRegisterPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showRegisterPassword = !showRegisterPassword }) {
                            Icon(
                                if (showRegisterPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showRegisterPassword) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = Color.Gray
                            )
                        }
                    },
                    isError = errorPassword != null,
                    supportingText = if (errorPassword != null) {
                        { Text(errorPassword!!, color = RedPrimary, fontSize = 11.sp) }
                    } else {
                        { Text("Debe tener al menos 8 caracteres, combinando letras y números.", color = Color.Gray, fontSize = 10.sp) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPrimary,
                        focusedLabelColor = RedPrimary,
                        errorBorderColor = RedPrimary,
                        errorLabelColor = RedPrimary
                    )
                )

                Button(
                    onClick = {
                        if (validarCamposRegistro()) {
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
                        }
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
}
}