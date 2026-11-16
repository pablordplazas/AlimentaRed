package com.example.alimentared.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import android.accounts.AccountManager
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alimentared.ui.components.AlimentaRedTopBar
import com.example.alimentared.ui.components.GoogleSignInDialog
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
    var mostrarDialogoGoogle by remember { mutableStateOf(false) }
    var correoGoogleSeleccionado by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val accountPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val accountName = result.data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            if (!accountName.isNullOrBlank()) {
                correoGoogleSeleccionado = accountName
            }
        }
        mostrarDialogoGoogle = true
    }

    fun iniciarGoogleReal() {
        try {
            val intent = AccountManager.newChooseAccountIntent(
                null,
                null,
                arrayOf("com.google"),
                null,
                null,
                null,
                null
            )
            accountPickerLauncher.launch(intent)
        } catch (e: Exception) {
            mostrarDialogoGoogle = true
        }
    }

    // Campos Login
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

        val nitLimpio = regNit.trim()
        if (nitLimpio.isBlank()) {
            errorNit = "El NIT es obligatorio"
            if (primerError == null) primerError = "El campo NIT es obligatorio"
            esValido = false
        } else if (!Regex("^[0-9]{6,15}(-[0-9])?$").matches(nitLimpio)) {
            errorNit = "Debe tener entre 6 y 15 dígitos numéricos (ej. 900123456-7)"
            if (primerError == null) primerError = "NIT inválido: debe tener entre 6 y 15 dígitos numéricos"
            esValido = false
        }

        val nombreOrgLimpio = regNombreOrg.trim()
        if (nombreOrgLimpio.isBlank()) {
            errorNombreOrg = "El nombre de la organización es obligatorio"
            if (primerError == null) primerError = "El nombre de la organización es obligatorio"
            esValido = false
        } else if (nombreOrgLimpio.length < 3) {
            errorNombreOrg = "El nombre es muy corto (mínimo 3 caracteres)"
            if (primerError == null) primerError = "El nombre de la organización es muy corto"
            esValido = false
        }

        val correoLimpio = regCorreoOrg.trim()
        if (correoLimpio.isBlank()) {
            errorCorreoOrg = "El correo electrónico es obligatorio"
            if (primerError == null) primerError = "El correo electrónico es obligatorio"
            esValido = false
        } else if (!Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(correoLimpio)) {
            errorCorreoOrg = "Formato de correo no válido"
            if (primerError == null) primerError = "Ingresa un correo electrónico válido"
            esValido = false
        }

        val contactoLimpio = regContacto.trim()
        if (contactoLimpio.isBlank()) {
            errorContacto = "El nombre del representante es obligatorio"
            if (primerError == null) primerError = "El nombre del representante es obligatorio"
            esValido = false
        } else if (contactoLimpio.length < 3) {
            errorContacto = "El nombre es muy corto (mínimo 3 caracteres)"
            if (primerError == null) primerError = "El nombre del representante es muy corto"
            esValido = false
        }

        if (regPassword.isBlank()) {
            errorPassword = "La contraseña es obligatoria"
            if (primerError == null) primerError = "La contraseña es obligatoria"
            esValido = false
        } else if (regPassword.length < 8) {
            errorPassword = "La contraseña es muy corta (mínimo 8 caracteres)"
            if (primerError == null) primerError = "La contraseña debe tener al menos 8 caracteres"
            esValido = false
        } else if (!regPassword.any { it.isLetter() } || !regPassword.any { it.isDigit() }) {
            errorPassword = "Debe contener tanto letras como números"
            if (primerError == null) primerError = "La contraseña debe contener letras y números"
            esValido = false
        }

        if (!esValido && primerError != null) {
            authViewModel.setError(primerError)
        }

        return esValido
    }

    val usuariosCreados = remember { com.example.alimentared.data.usuarios.UsuariosInternosManager.obtenerUsuariosCreados() }
    val accesosRapidos = remember(usuariosCreados) {
        val lista = mutableListOf(
            Triple("Donante: Sabor Casero", "donante@saborcasero.com", "Pass12345"),
            Triple("Donante: Panificadora Central", "donante@panificadoracentral.com", "Pass12345"),
            Triple("Receptora: Pan Compartido", "receptora@pancompartido.org", "Pass12345"),
            Triple("Receptora: Comedor Esperanza", "receptora@comedoresperanza.org", "Pass12345"),
            Triple("Coordinador", "coordinador@alimentared.com", "Coord12345"),
            Triple("Admin", "admin@alimentared.com", "Admin12345")
        )
        usuariosCreados.forEach { u ->
            val prefijo = if (u.rol.contains("ADMIN")) "Admin" else "Coord"
            val primerNombre = u.nombre.trim().split(" ").firstOrNull() ?: u.nombre
            lista.add(Triple("$prefijo: $primerNombre", u.correo, u.contrasena))
        }
        lista.add(Triple("Panadería Sol (Suspendida)", "suspendido@panaderiasol.com", "Pass12345"))
        lista.add(Triple("Abuelitos de la Paz (3 Strikes)", "suspendido@abuelitospaz.org", "Pass12345"))
        lista
    }

    LaunchedEffect(uiState.isSuccess, uiState.user) {
        if (uiState.isSuccess && uiState.user != null) {
            onLoginExitoso(uiState.user!!.rol)
        }
    }

    val ecoTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = EcoTextPrimary,
        unfocusedTextColor = EcoTextPrimary,
        errorTextColor = EcoTextPrimary,
        focusedContainerColor = EcoSurface,
        unfocusedContainerColor = EcoSurface,
        errorContainerColor = EcoSurface,
        focusedBorderColor = EcoForestGreen,
        unfocusedBorderColor = EcoBorder,
        errorBorderColor = EcoRed,
        focusedLabelColor = EcoForestGreen,
        unfocusedLabelColor = EcoTextMuted,
        errorLabelColor = EcoRed,
        focusedPlaceholderColor = EcoTextMuted,
        unfocusedPlaceholderColor = EcoTextMuted,
        cursorColor = EcoForestGreen,
        errorCursorColor = EcoRed
    )

    Scaffold(
        topBar = {
            AlimentaRedTopBar(
                title = {
                    Text(
                        if (modoRegistro) "Crear Cuenta" else if (esModoPruebas) "Pruebas Rápidas" else "Iniciar Sesión",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                },
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ARCO SUPERIOR ALIMENTARED
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        color = EcoOliveGreen,
                        shape = RoundedCornerShape(bottomStart = 130.dp, bottomEnd = 130.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Ícono circular blanco con corazón verde silver (AlimentaRed)
                    Surface(
                        modifier = Modifier.size(72.dp),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "AlimentaRed Logo",
                                tint = GreenSilver,
                                modifier = Modifier.size(42.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(52.dp)
                            .background(Color.White.copy(alpha = 0.6f))
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Alimenta",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Red",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // CUERPO DEL FORMULARIO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // TÍTULO Y SUBTÍTULO
                Column {
                    Text(
                        text = if (modoRegistro) "Crea tu cuenta" else "¡Bienvenido de nuevo!",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = EcoOliveGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (modoRegistro) "Únete a Eco Abasto • AlimentaRed" else "Inicia sesión a tu cuenta",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF75865E)
                    )
                }

                // MENSAJE DE ÉXITO O ERROR
                if (uiState.isSuccess) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = EcoMintGreen),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (modoRegistro) "Cuenta creada con éxito" else "Sesión iniciada",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = EcoTextPrimary
                                )
                                Text(
                                    text = if (modoRegistro) "Tu organización ha sido registrada." else "Tu acceso se ha confirmado correctamente.",
                                    fontSize = 12.sp,
                                    color = EcoForestGreen
                                )
                            }
                        }
                    }
                }

                if (uiState.error != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = EcoRedLight),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = EcoRed)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                uiState.error!!,
                                color = EcoRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // SELECTOR MODO PRUEBAS RÁPIDAS
                if (!modoRegistro && esModoPruebas) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = EcoMintLight,
                        border = BorderStroke(1.dp, EcoMintGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Science, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Cuentas rápidas de demostración:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoForestGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(accesosRapidos) { (label, correo, pass) ->
                                    val isSelected = (email == correo)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            email = correo
                                            password = pass
                                            authViewModel.limpiarError()
                                        },
                                        shape = RoundedCornerShape(50),
                                        label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = EcoMintGreen,
                                            selectedLabelColor = EcoForestGreen,
                                            containerColor = Color.White
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = isSelected,
                                            borderColor = if (isSelected) EcoForestGreen else EcoBorder
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                if (!modoRegistro) {
                    // FORMULARIO DE INICIO DE SESIÓN
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; authViewModel.limpiarError() },
                        placeholder = { Text("Correo o Usuario", fontSize = 14.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ecoTextFieldColors
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; authViewModel.limpiarError() },
                        placeholder = { Text("Contraseña", fontSize = 14.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = if (showLoginPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showLoginPassword = !showLoginPassword }) {
                                Icon(
                                    if (showLoginPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (showLoginPassword) "Ocultar" else "Mostrar",
                                    tint = EcoForestGreen
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ecoTextFieldColors
                    )

                    // ENLACE OLVIDÓ CONTRASEÑA
                    Text(
                        text = "Olvidó su contraseña",
                        fontSize = 11.sp,
                        color = EcoTextMuted,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // BOTÓN INICIAR SESIÓN (PÍLDORA VERDE OLIVA / BOSQUE)
                    Button(
                        onClick = { authViewModel.login(email, password) },
                        enabled = !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = EcoOliveGreen)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                        } else {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Iniciar Sesión",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }
                    }

                    // SEPARADOR ELEGANTE "O continúa con"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = EcoBorder)
                        Text(
                            text = "  O continúa con  ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = EcoTextMuted
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = EcoBorder)
                    }

                    // BOTÓN INICIAR SESIÓN CON GOOGLE
                    OutlinedButton(
                        onClick = {
                            if (esModoPruebas) {
                                mostrarDialogoGoogle = true
                            } else {
                                iniciarGoogleReal()
                            }
                        },
                        enabled = !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.2.dp, Color(0xFFDADCE0)),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = com.example.alimentared.R.drawable.ic_google),
                                contentDescription = "Google",
                                modifier = Modifier.size(22.dp),
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Continuar con Google",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF3C4043)
                            )
                        }
                    }

                    // ENLACE CREAR CUENTA
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "¿No tienes cuenta? Crea una",
                            fontSize = 13.sp,
                            color = EcoTextMuted,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable {
                                modoRegistro = true
                                limpiarErroresLocales()
                                authViewModel.limpiarError()
                            }
                        )
                    }

                } else {
                    // FORMULARIO DE REGISTRO
                    Text(
                        "¿Cómo quieres participar?",
                        fontSize = 13.sp,
                        color = EcoTextMuted,
                        fontWeight = FontWeight.Medium
                    )

                    // SELECTOR DE ROL ESTILO ECO ABASTO (2 PÍLDORAS)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Donador
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (regRol == "DONANTE") Color(0xFFF0F4E8) else EcoSurface,
                            border = BorderStroke(1.dp, if (regRol == "DONANTE") EcoOliveGreen else EcoBorder),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clickable {
                                    regRol = "DONANTE"
                                    authViewModel.limpiarError()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (regRol == "DONANTE") Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (regRol == "DONANTE") EcoOliveGreen else EcoTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Donador",
                                    fontSize = 13.sp,
                                    fontWeight = if (regRol == "DONANTE") FontWeight.Bold else FontWeight.Normal,
                                    color = if (regRol == "DONANTE") EcoOliveGreen else EcoTextMuted
                                )
                            }
                        }

                        // Organización Receptora
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (regRol == "RECEPTORA") Color(0xFFF0F4E8) else EcoSurface,
                            border = BorderStroke(1.dp, if (regRol == "RECEPTORA") EcoOliveGreen else EcoBorder),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(46.dp)
                                .clickable {
                                    regRol = "RECEPTORA"
                                    authViewModel.limpiarError()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (regRol == "RECEPTORA") Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (regRol == "RECEPTORA") EcoOliveGreen else EcoTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Receptora",
                                    fontSize = 13.sp,
                                    fontWeight = if (regRol == "RECEPTORA") FontWeight.Bold else FontWeight.Normal,
                                    color = if (regRol == "RECEPTORA") EcoOliveGreen else EcoTextMuted
                                )
                            }
                        }
                    }

                    // 1. Nombre completo / representante
                    OutlinedTextField(
                        value = regContacto,
                        onValueChange = {
                            regContacto = it
                            errorContacto = null
                            authViewModel.limpiarError()
                        },
                        placeholder = { Text("Nombre completo del representante", fontSize = 13.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        isError = errorContacto != null,
                        supportingText = if (errorContacto != null) {
                            { Text(errorContacto!!, color = EcoRed, fontSize = 11.sp) }
                        } else null,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ecoTextFieldColors
                    )

                    // 2. Nombre de la Organización
                    OutlinedTextField(
                        value = regNombreOrg,
                        onValueChange = {
                            regNombreOrg = it
                            errorNombreOrg = null
                            authViewModel.limpiarError()
                        },
                        placeholder = { Text("Nombre de la organización", fontSize = 13.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        isError = errorNombreOrg != null,
                        supportingText = if (errorNombreOrg != null) {
                            { Text(errorNombreOrg!!, color = EcoRed, fontSize = 11.sp) }
                        } else null,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ecoTextFieldColors
                    )

                    // 3. NIT / Identificación Fiscal
                    OutlinedTextField(
                        value = regNit,
                        onValueChange = {
                            regNit = it
                            errorNit = null
                            authViewModel.limpiarError()
                        },
                        placeholder = { Text("NIT / Identificación Fiscal (ej: 900123456-7)", fontSize = 13.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        isError = errorNit != null,
                        supportingText = if (errorNit != null) {
                            { Text(errorNit!!, color = EcoRed, fontSize = 11.sp) }
                        } else null,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ecoTextFieldColors
                    )

                    // 4. Correo electrónico
                    OutlinedTextField(
                        value = regCorreoOrg,
                        onValueChange = {
                            regCorreoOrg = it
                            errorCorreoOrg = null
                            authViewModel.limpiarError()
                        },
                        placeholder = { Text("Correo electrónico", fontSize = 13.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        isError = errorCorreoOrg != null,
                        supportingText = if (errorCorreoOrg != null) {
                            { Text(errorCorreoOrg!!, color = EcoRed, fontSize = 11.sp) }
                        } else null,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ecoTextFieldColors
                    )

                    // 5. Dirección
                    OutlinedTextField(
                        value = regUbicacion,
                        onValueChange = { regUbicacion = it },
                        placeholder = { Text("Dirección", fontSize = 13.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ecoTextFieldColors
                    )

                    // 6. Contraseña
                    OutlinedTextField(
                        value = regPassword,
                        onValueChange = {
                            regPassword = it
                            errorPassword = null
                            authViewModel.limpiarError()
                        },
                        placeholder = { Text("Contraseña", fontSize = 13.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = if (showRegisterPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showRegisterPassword = !showRegisterPassword }) {
                                Icon(
                                    if (showRegisterPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (showRegisterPassword) "Ocultar" else "Mostrar",
                                    tint = EcoForestGreen
                                )
                            }
                        },
                        isError = errorPassword != null,
                        supportingText = {
                            Text(
                                errorPassword ?: "Usa al menos 8 caracteres.",
                                color = if (errorPassword != null) EcoRed else EcoTextMuted,
                                fontSize = 11.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ecoTextFieldColors
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // BOTÓN CREAR CUENTA (PÍLDORA)
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
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = EcoOliveGreen)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                        } else {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Crear cuenta", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                        }
                    }

                    // ENLACE VOLVER A LOGIN
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "¿Ya tienes cuenta? Inicia sesión",
                            fontSize = 13.sp,
                            color = EcoTextMuted,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable {
                                modoRegistro = false
                                limpiarErroresLocales()
                                authViewModel.limpiarError()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (mostrarDialogoGoogle) {
        GoogleSignInDialog(
            esModoDemo = esModoPruebas,
            correoInicial = correoGoogleSeleccionado,
            onDismiss = {
                mostrarDialogoGoogle = false
                correoGoogleSeleccionado = null
            },
            onElegirCuentaDispositivo = if (!esModoPruebas) {
                { iniciarGoogleReal() }
            } else null,
            onCuentaSeleccionada = { idToken, correo, nombre, rol ->
                mostrarDialogoGoogle = false
                correoGoogleSeleccionado = null
                authViewModel.loginConGoogle(
                    idToken = idToken,
                    correo = correo,
                    nombre = nombre,
                    rol = rol
                )
            }
        )
    }
}
