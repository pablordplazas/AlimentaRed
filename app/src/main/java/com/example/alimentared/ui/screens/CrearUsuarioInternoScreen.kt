package com.example.alimentared.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alimentared.data.usuarios.UsuarioInternoItem
import com.example.alimentared.ui.components.AlimentaRedTopBar
import com.example.alimentared.ui.theme.*
import com.example.alimentared.ui.viewmodels.CrearUsuarioInternoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearUsuarioInternoScreen(
    onBack: () -> Unit,
    viewModel: CrearUsuarioInternoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var pestañaSeleccionada by remember { mutableIntStateOf(0) } // 0: Formulario, 1: Equipo Interno

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
                    Column {
                        Text(
                            "Crear Perfil Interno",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            "Alta de Administrador o Coordinador",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
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
        ) {
            // SELECTOR DE PESTAÑAS (FORMULARIO / VER EQUIPO)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChip(
                    selected = pestañaSeleccionada == 0,
                    onClick = { pestañaSeleccionada = 0 },
                    shape = RoundedCornerShape(50),
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Nuevo Perfil", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EcoMintGreen,
                        selectedLabelColor = EcoForestGreen,
                        containerColor = EcoBackground
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = pestañaSeleccionada == 0,
                        borderColor = if (pestañaSeleccionada == 0) EcoForestGreen else EcoBorder
                    ),
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = pestañaSeleccionada == 1,
                    onClick = {
                        pestañaSeleccionada = 1
                        viewModel.cargarUsuariosInternos()
                    },
                    shape = RoundedCornerShape(50),
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Equipo (${uiState.usuariosInternos.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EcoMintGreen,
                        selectedLabelColor = EcoForestGreen,
                        containerColor = EcoBackground
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = pestañaSeleccionada == 1,
                        borderColor = if (pestañaSeleccionada == 1) EcoForestGreen else EcoBorder
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider(color = EcoBorder)

            if (pestañaSeleccionada == 0) {
                // FORMULARIO DE ALTA DE USUARIOS INTERNOS
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. BANNER INFORMATIVO
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EcoMintLight),
                        border = BorderStroke(1.dp, EcoMintGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = EcoForestGreen,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Facultad Exclusiva de Administrador",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = EcoForestGreen
                                )
                                Text(
                                    "Crea nuevos perfiles institucionales eligiendo si otorgas rol de Administrador o Coordinador.",
                                    fontSize = 11.sp,
                                    color = EcoTextMuted,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    // 2. SELECTOR DE ROL: ADMINISTRADOR O COORDINADOR
                    Text(
                        "1. Selecciona el Rol Institucional *",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = EcoTextPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val esAdminSeleccionado = uiState.rol == "ADMINISTRADOR"
                        val esCoordSeleccionado = uiState.rol == "COORDINADOR"

                        // Tarjeta Administrador
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (esAdminSeleccionado) Color.White else EcoSurface.copy(alpha = 0.6f)
                            ),
                            border = BorderStroke(
                                if (esAdminSeleccionado) 2.dp else 1.dp,
                                if (esAdminSeleccionado) EcoForestGreen else EcoBorder
                            ),
                            elevation = CardDefaults.cardElevation(if (esAdminSeleccionado) 4.dp else 1.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.onRolChange("ADMINISTRADOR") }
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (esAdminSeleccionado) EcoForestGreen else EcoSageGreen.copy(alpha = 0.4f),
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.AdminPanelSettings,
                                                contentDescription = null,
                                                tint = if (esAdminSeleccionado) Color.White else EcoForestGreen,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    if (esAdminSeleccionado) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = "Seleccionado",
                                            tint = EcoForestGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Text(
                                    "Administrador",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (esAdminSeleccionado) EcoForestGreen else EcoTextPrimary
                                )

                                Text(
                                    "Control total, verificación de fundaciones y donantes, y creación de perfiles.",
                                    fontSize = 10.sp,
                                    color = EcoTextMuted,
                                    lineHeight = 14.sp
                                )

                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = if (esAdminSeleccionado) EcoMintLight else EcoBackground,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        "Acceso Total",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EcoForestGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        // Tarjeta Coordinador
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (esCoordSeleccionado) Color.White else EcoSurface.copy(alpha = 0.6f)
                            ),
                            border = BorderStroke(
                                if (esCoordSeleccionado) 2.dp else 1.dp,
                                if (esCoordSeleccionado) EcoForestGreen else EcoBorder
                            ),
                            elevation = CardDefaults.cardElevation(if (esCoordSeleccionado) 4.dp else 1.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.onRolChange("COORDINADOR") }
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (esCoordSeleccionado) EcoOliveGreen else EcoSageGreen.copy(alpha = 0.4f),
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.SupervisorAccount,
                                                contentDescription = null,
                                                tint = if (esCoordSeleccionado) Color.White else EcoOliveGreen,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    if (esCoordSeleccionado) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = "Seleccionado",
                                            tint = EcoForestGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Text(
                                    "Coordinador",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (esCoordSeleccionado) EcoForestGreen else EcoTextPrimary
                                )

                                Text(
                                    "Supervisión de entregas, atención de apelaciones y difusión en noticias.",
                                    fontSize = 10.sp,
                                    color = EcoTextMuted,
                                    lineHeight = 14.sp
                                )

                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = if (esCoordSeleccionado) EcoMintLight else EcoBackground,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        "Operativo",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EcoOliveGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 3. CAMPOS DEL FORMULARIO
                    Text(
                        "2. Datos del Perfil *",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = EcoTextPrimary
                    )

                    // Nombre Completo
                    OutlinedTextField(
                        value = uiState.nombre,
                        onValueChange = { viewModel.onNombreChange(it) },
                        label = { Text("Nombre Completo *", fontSize = 13.sp) },
                        placeholder = { Text("Ej: Carlos Alberto Mendoza", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(20.dp))
                        },
                        trailingIcon = {
                            if (uiState.esNombreValido) {
                                Icon(Icons.Default.Check, contentDescription = "Válido", tint = EcoForestGreen)
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ecoTextFieldColors
                    )

                    // Correo Institucional
                    OutlinedTextField(
                        value = uiState.correo,
                        onValueChange = { viewModel.onCorreoChange(it) },
                        label = { Text("Correo Institucional / Electrónico *", fontSize = 13.sp) },
                        placeholder = { Text("Ej: c.mendoza@alimentared.com", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(20.dp))
                        },
                        trailingIcon = {
                            if (uiState.esCorreoValido) {
                                Icon(Icons.Default.Check, contentDescription = "Válido", tint = EcoForestGreen)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ecoTextFieldColors
                    )

                    // Contraseña Inicial
                    OutlinedTextField(
                        value = uiState.contrasena,
                        onValueChange = { viewModel.onContrasenaChange(it) },
                        label = { Text("Contraseña Inicial *", fontSize = 13.sp) },
                        placeholder = { Text("Mínimo 8 caracteres (letras y números)", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(20.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Mostrar contraseña",
                                    tint = EcoTextMuted
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ecoTextFieldColors
                    )

                    // Confirmar Contraseña
                    OutlinedTextField(
                        value = uiState.confirmarContrasena,
                        onValueChange = { viewModel.onConfirmarContrasenaChange(it) },
                        label = { Text("Confirmar Contraseña *", fontSize = 13.sp) },
                        placeholder = { Text("Repite la contraseña", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.LockReset, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(20.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                Icon(
                                    if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Mostrar confirmación",
                                    tint = EcoTextMuted
                                )
                            }
                        },
                        visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ecoTextFieldColors
                    )

                    // 4. CHECKLIST DE VALIDACIÓN EN TIEMPO REAL
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = EcoSurface),
                        border = BorderStroke(1.dp, EcoBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("Requisitos del nuevo perfil:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = EcoTextPrimary)
                            ValidationItem(valido = uiState.esNombreValido, texto = "Nombre completo válido (máx. 120 caracteres)")
                            ValidationItem(valido = uiState.esCorreoValido, texto = "Formato de correo electrónico válido (@)")
                            ValidationItem(valido = uiState.esContrasenaLongitudValida, texto = "Contraseña con mínimo 8 caracteres (máx. 72)")
                            ValidationItem(valido = uiState.esContrasenaComplejidadValida, texto = "Contraseña con combinación de letras y números")
                            ValidationItem(valido = uiState.coincidenContrasenas, texto = "Las contraseñas coinciden exactamente")
                        }
                    }

                    // MENSAJE DE ERROR
                    if (uiState.error != null) {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = EcoRedLight),
                            border = BorderStroke(1.dp, EcoRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = EcoRed, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    uiState.error!!,
                                    color = EcoRed,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // 5. BOTÓN DE CREACIÓN
                    val rolTexto = if (uiState.rol == "ADMINISTRADOR") "Administrador" else "Coordinador"
                    Button(
                        onClick = { viewModel.crearUsuarioInterno() },
                        enabled = uiState.esFormularioValido && !uiState.isLoading,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EcoForestGreen,
                            disabledContainerColor = EcoSageGreen.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Registrando perfil...", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        } else {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Crear Perfil de $rolTexto",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            } else {
                // LISTADO DE EQUIPO INTERNO
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = EcoMintLight),
                            border = BorderStroke(1.dp, EcoMintGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = EcoForestGreen,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Groups, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "Personal Interno Autorizado",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = EcoForestGreen
                                    )
                                    Text(
                                        "Incluye los perfiles iniciales predefinidos y los nuevos usuarios creados por los administradores.",
                                        fontSize = 11.sp,
                                        color = EcoTextMuted,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }

                    items(uiState.usuariosInternos) { item ->
                        UsuarioInternoCard(
                            item = item,
                            onCopiarCredenciales = {
                                clipboardManager.setText(
                                    AnnotatedString("Correo: ${item.correo}\nContraseña: ${item.contrasena}\nRol: ${item.rol}")
                                )
                                Toast.makeText(context, "Credenciales copiadas al portapapeles", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }
    }

    // DIÁLOGO DE ÉXITO AL CREAR PERFIL
    if (uiState.usuarioCreado != null) {
        val creado = uiState.usuarioCreado!!
        val contrasenaUsada = uiState.contrasenaUsuarioCreado ?: ""
        val rolTitulo = if (creado.rol.contains("ADMIN")) "Administrador" else "Coordinador"

        AlertDialog(
            onDismissRequest = { viewModel.descartarUsuarioCreado() },
            icon = {
                Surface(
                    shape = CircleShape,
                    color = EcoMintLight,
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(34.dp))
                    }
                }
            },
            title = {
                Text(
                    "¡Perfil Creado con Éxito!",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = EcoTextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Se ha registrado el nuevo usuario institucional en AlimentaRed con rol de $rolTitulo.",
                        fontSize = 13.sp,
                        color = EcoTextMuted,
                        lineHeight = 17.sp
                    )

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = EcoSurface),
                        border = BorderStroke(1.dp, EcoBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Resumen de Credenciales:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = EcoForestGreen)
                            Text("• Nombre: ${creado.nombre}", fontSize = 12.sp, color = EcoTextPrimary, fontWeight = FontWeight.Medium)
                            Text("• Correo: ${creado.correo}", fontSize = 12.sp, color = EcoTextPrimary, fontWeight = FontWeight.Medium)
                            Text("• Rol: $rolTitulo", fontSize = 12.sp, color = EcoForestGreen, fontWeight = FontWeight.Bold)
                            if (contrasenaUsada.isNotBlank()) {
                                Text("• Contraseña: $contrasenaUsada", fontSize = 12.sp, color = EcoTextPrimary)
                            }
                        }
                    }

                    Button(
                        onClick = {
                            clipboardManager.setText(
                                AnnotatedString("AlimentaRed Credenciales:\nNombre: ${creado.nombre}\nCorreo: ${creado.correo}\nContraseña: $contrasenaUsada\nRol: $rolTitulo")
                            )
                            Toast.makeText(context, "Credenciales copiadas al portapapeles", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EcoMintLight, contentColor = EcoForestGreen),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.fillMaxWidth().height(38.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copiar Credenciales", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.descartarUsuarioCreado()
                        viewModel.limpiarFormulario()
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = EcoForestGreen)
                ) {
                    Text("Crear Otro", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.descartarUsuarioCreado()
                        onBack()
                    }
                ) {
                    Text("Volver al Perfil", color = EcoForestGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun ValidationItem(valido: Boolean, texto: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 1.dp)
    ) {
        Icon(
            if (valido) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (valido) EcoForestGreen else EcoTextMuted.copy(alpha = 0.5f),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            texto,
            fontSize = 11.sp,
            color = if (valido) EcoForestGreen else EcoTextMuted,
            fontWeight = if (valido) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun UsuarioInternoCard(
    item: UsuarioInternoItem,
    onCopiarCredenciales: () -> Unit
) {
    val esAdmin = item.rol.contains("ADMIN")
    val badgeColor = if (esAdmin) EcoForestGreen else EcoOliveGreen
    val badgeBg = if (esAdmin) EcoMintLight else EcoSageGreen.copy(alpha = 0.3f)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EcoSurface),
        border = BorderStroke(1.dp, EcoBorder),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            val iniciales = remember(item.nombre) {
                val partes = item.nombre.trim().split(" ")
                if (partes.size >= 2) {
                    "${partes[0].take(1)}${partes[1].take(1)}".uppercase()
                } else {
                    partes.firstOrNull()?.take(2)?.uppercase() ?: "US"
                }
            }

            Surface(
                shape = CircleShape,
                color = badgeColor,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        iniciales,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        item.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = EcoTextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (item.esPredefinido) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = EcoSageGreen.copy(alpha = 0.4f),
                            modifier = Modifier.padding(horizontal = 2.dp)
                        ) {
                            Text(
                                "Predefinido",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoTextPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    item.correo,
                    fontSize = 12.sp,
                    color = EcoTextMuted
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = badgeBg
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                if (esAdmin) Icons.Default.AdminPanelSettings else Icons.Default.SupervisorAccount,
                                contentDescription = null,
                                tint = badgeColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (esAdmin) "Administrador" else "Coordinador",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor
                            )
                        }
                    }

                    if (item.fechaCreacion.isNotBlank()) {
                        Text(
                            item.fechaCreacion,
                            fontSize = 10.sp,
                            color = EcoTextMuted
                        )
                    }
                }
            }

            IconButton(
                onClick = onCopiarCredenciales,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Default.ContentCopy,
                    contentDescription = "Copiar credenciales",
                    tint = EcoForestGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
