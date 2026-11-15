package com.example.alimentared.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import com.example.alimentared.ui.theme.*
import com.example.alimentared.ui.viewmodels.PerfilImpactoViewModel

enum class PerfilSubVista {
    PERFIL,
    DETALLE_STRIKES,
    PANEL_APELACIONES,
    PANEL_VERIFICACION,
    DESTACAR_ENTREGAS,
    CREAR_USUARIO_INTERNO
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    viewModel: PerfilImpactoViewModel = viewModel(),
    onCerrarSesion: () -> Unit = {},
    onIrACatalogo: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    var subVista by remember { mutableStateOf(PerfilSubVista.PERFIL) }

    val user = uiState.usuario
    val rol = user?.rol?.uppercase() ?: ""
    val esDonanteOReceptora = rol.contains("DONANTE") || rol.contains("RECEPTOR")

    when (subVista) {
        PerfilSubVista.DETALLE_STRIKES -> {
            DetalleStrikesScreen(
                usuario = user,
                strikes = uiState.strikes,
                strikesVigentes = uiState.strikesVigentes,
                onBack = { subVista = PerfilSubVista.PERFIL }
            )
        }
        PerfilSubVista.PANEL_APELACIONES -> {
            PanelApelacionesScreen(
                onBack = {
                    subVista = PerfilSubVista.PERFIL
                    viewModel.cargarDatos()
                }
            )
        }
        PerfilSubVista.DESTACAR_ENTREGAS -> {
            DestacarEntregasScreen(
                onBack = {
                    subVista = PerfilSubVista.PERFIL
                    viewModel.cargarDatos()
                }
            )
        }
        PerfilSubVista.PANEL_VERIFICACION -> {
            PanelVerificacionOrganizacionesScreen(
                onBack = {
                    subVista = PerfilSubVista.PERFIL
                    viewModel.cargarDatos()
                }
            )
        }
        PerfilSubVista.CREAR_USUARIO_INTERNO -> {
            CrearUsuarioInternoScreen(
                onBack = {
                    subVista = PerfilSubVista.PERFIL
                    viewModel.cargarDatos()
                }
            )
        }
        PerfilSubVista.PERFIL -> {
            Surface(
                color = EcoBackground,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. HEADER AVATAR ESTILO ECO ABASTO (DONADOR / PERFIL DEL DONADOR)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar circular verde bosque con iniciales
                        val iniciales = remember(user) {
                            val partes = (user?.nombre ?: "Usuario").trim().split(" ")
                            if (partes.size >= 2) {
                                "${partes[0].take(1)}${partes[1].take(1)}".uppercase()
                            } else {
                                partes.firstOrNull()?.take(2)?.uppercase() ?: "US"
                            }
                        }
                        Surface(
                            shape = CircleShape,
                            color = EcoForestGreen,
                            modifier = Modifier.size(68.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    iniciales,
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                user?.nombre ?: "Usuario Eco Abasto",
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = EcoTextPrimary
                            )
                            Text(
                                "${user?.rol ?: "Usuario"} • ${user?.organizacion?.nombre ?: "AlimentaRed"}",
                                fontSize = 12.sp,
                                color = EcoTextMuted
                            )
                        }
                    }

                    // BANNER INFORMACIÓN CARGADA (PÍLDORA MENTA ECO ABASTO)
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EcoMintGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Información cargada correctamente",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = EcoForestGreen
                            )
                        }
                    }

                    // MENSAJES DE ESTADO
                    if (uiState.mensajeExito != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = EcoMintGreen),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                uiState.mensajeExito!!,
                                color = EcoForestGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // ESTADO DE VERIFICACIÓN INSTITUCIONAL (RF02)
                    if (esDonanteOReceptora) {
                        val estaVerificada = user?.organizacion?.verificada == true
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (estaVerificada) EcoMintLight else Color(0xFFFFFBEB)
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (estaVerificada) EcoMintGreen else Color(0xFFFDE68A)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        if (estaVerificada) Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                                        contentDescription = null,
                                        tint = if (estaVerificada) EcoForestGreen else Color(0xFFD97706),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        if (estaVerificada) "Organización Verificada y Habilitada" else "Organización Pendiente de Aprobación",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (estaVerificada) EcoForestGreen else Color(0xFF92400E)
                                    )
                                }
                                Text(
                                    if (estaVerificada) {
                                        "Tu perfil ha sido verificado por el Administrador. Estás habilitado para operar plenamente."
                                    } else if (rol.contains("DONANTE")) {
                                        "Tu cuenta está en revisión. Mientras tanto, puedes crear y guardar lotes como BORRADOR."
                                    } else {
                                        "Tu fundación está en revisión. Puedes explorar el catálogo de donaciones en tiempo real."
                                    },
                                    fontSize = 11.sp,
                                    color = if (estaVerificada) EcoForestGreen.copy(alpha = 0.9f) else Color(0xFF78350F),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    // CAMPOS DE PERFIL ESTILO ECO ABASTO (TARJETAS BLANCAS CON CHEVRON >)
                    PerfilFieldCard(label = "Nombre", valor = user?.nombre ?: "Usuario")
                    PerfilFieldCard(label = "Email", valor = user?.correo ?: "Sin correo")
                    PerfilFieldCard(label = "Organización", valor = user?.organizacion?.nombre ?: "AlimentaRed")
                    if (user?.organizacion?.nit != null) {
                        PerfilFieldCard(label = "NIT", valor = user.organizacion.nit)
                    }

                    // 2. VISUALIZADOR DE STRIKES Y SANCIONES (RN08 / RN12)
                    if (esDonanteOReceptora) {
                        val strikesVigentes = uiState.strikesVigentes
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (strikesVigentes >= 3) EcoRedLight else EcoSurface
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (strikesVigentes >= 3) EcoRed else EcoBorder
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Gavel, contentDescription = null, tint = if (strikesVigentes >= 3) EcoRed else EcoOliveGreen)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Strikes Acumulados", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EcoTextPrimary)
                                    }
                                    Text(
                                        "$strikesVigentes / 3",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = if (strikesVigentes >= 3) EcoRed else EcoForestGreen
                                    )
                                }

                                LinearProgressIndicator(
                                    progress = { (strikesVigentes / 3.0f).coerceIn(0f, 1f) },
                                    modifier = Modifier.fillMaxWidth().height(8.dp),
                                    color = if (strikesVigentes >= 3) EcoRed else EcoAmber,
                                    trackColor = EcoBorder,
                                )

                                if (strikesVigentes >= 3) {
                                    Text(
                                        "⛔ CUENTA SUSPENDIDA: Al acumular 3 strikes vigentes, no puedes solicitar ni publicar alimentos. Contacta a coordinación para apelar.",
                                        color = EcoRed,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 16.sp
                                    )
                                } else {
                                    Text(
                                        "Al acumular 3 strikes vigentes, la cuenta se suspende automáticamente.",
                                        fontSize = 11.sp,
                                        color = EcoTextMuted
                                    )
                                }

                                Button(
                                    onClick = { subVista = PerfilSubVista.DETALLE_STRIKES },
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EcoSageGreen,
                                        contentColor = EcoTextPrimary
                                    ),
                                    modifier = Modifier.fillMaxWidth().height(42.dp)
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp), tint = EcoTextPrimary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Ver detalle de mis strikes (${uiState.strikes.size})",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    } else {
                        // PANEL INFORMATIVO PARA COORDINADOR Y ADMINISTRADOR
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = EcoSurface),
                            border = BorderStroke(1.dp, EcoBorder),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = EcoForestGreen)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Panel de Control Operativo ($rol)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EcoTextPrimary)
                                }
                                Text(
                                    "Tu perfil cuenta con privilegios administrativos y de auditoría.",
                                    fontSize = 12.sp,
                                    color = EcoTextMuted,
                                    lineHeight = 17.sp
                                )

                                Button(
                                    onClick = { subVista = PerfilSubVista.PANEL_APELACIONES },
                                    colors = ButtonDefaults.buttonColors(containerColor = EcoOliveGreen),
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.fillMaxWidth().height(44.dp)
                                ) {
                                    Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("⚖️ Gestión de Apelaciones", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }

                                var menuDestacarExpanded by remember { mutableStateOf(false) }

                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Button(
                                        onClick = { menuDestacarExpanded = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = EcoAmber),
                                        shape = RoundedCornerShape(50),
                                        modifier = Modifier.fillMaxWidth().height(44.dp)
                                    ) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("⭐ Destacar Noticias en Inicio", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    }

                                    DropdownMenu(
                                        expanded = menuDestacarExpanded,
                                        onDismissRequest = { menuDestacarExpanded = false },
                                        modifier = Modifier.background(Color.White)
                                    ) {
                                        DropdownMenuItem(
                                            leadingIcon = {
                                                Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = EcoAmber, modifier = Modifier.size(20.dp))
                                            },
                                            text = {
                                                Column {
                                                    Text("Destacar Donaciones Disponibles", fontWeight = FontWeight.Bold, color = EcoTextPrimary, fontSize = 13.sp)
                                                    Text("Ir al catálogo para seleccionar un lote activo", fontSize = 10.sp, color = EcoTextMuted)
                                                }
                                            },
                                            onClick = {
                                                menuDestacarExpanded = false
                                                onIrACatalogo?.invoke()
                                            }
                                        )

                                        HorizontalDivider(color = EcoBorder)

                                        DropdownMenuItem(
                                            leadingIcon = {
                                                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(20.dp))
                                            },
                                            text = {
                                                Column {
                                                    Text("Destacar Entregas de Comida", fontWeight = FontWeight.Bold, color = EcoTextPrimary, fontSize = 13.sp)
                                                    Text("Ver entregas completadas para destacarlas", fontSize = 10.sp, color = EcoTextMuted)
                                                }
                                            },
                                            onClick = {
                                                menuDestacarExpanded = false
                                                subVista = PerfilSubVista.DESTACAR_ENTREGAS
                                            }
                                        )
                                    }
                                }

                                if (rol.contains("ADMIN")) {
                                    Button(
                                        onClick = { subVista = PerfilSubVista.PANEL_VERIFICACION },
                                        colors = ButtonDefaults.buttonColors(containerColor = EcoForestGreen),
                                        shape = RoundedCornerShape(50),
                                        modifier = Modifier.fillMaxWidth().height(44.dp)
                                    ) {
                                        Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("🏢 Verificación de Organizaciones", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }

                                    Button(
                                        onClick = { subVista = PerfilSubVista.CREAR_USUARIO_INTERNO },
                                        colors = ButtonDefaults.buttonColors(containerColor = EcoForestGreen),
                                        shape = RoundedCornerShape(50),
                                        modifier = Modifier.fillMaxWidth().height(44.dp)
                                    ) {
                                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("👥 Crear Administrador o Coordinador", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }

                        // TARJETA DESTACADA DE GESTIÓN DE PERSONAL PARA ADMINISTRADOR
                        if (rol.contains("ADMIN")) {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = EcoMintLight),
                                border = BorderStroke(1.dp, EcoMintGreen),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { subVista = PerfilSubVista.CREAR_USUARIO_INTERNO }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = EcoForestGreen,
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.GroupAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "Alta de Personal Interno",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = EcoForestGreen
                                        )
                                        Text(
                                            "Crea nuevos perfiles y asigna rol de Administrador o Coordinador.",
                                            fontSize = 11.sp,
                                            color = EcoTextMuted,
                                            lineHeight = 15.sp
                                        )
                                    }
                                    Icon(
                                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = EcoForestGreen
                                    )
                                }
                            }
                        }
                    }

                    // 3. KPIS DE IMPACTO SOCIAL Y AMBIENTAL
                    val imp = uiState.impacto
                    Text("Impacto Global de Eco Abasto", fontWeight = FontWeight.Black, fontSize = 16.sp, color = EcoTextPrimary)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricCard(
                            titulo = "Alimento Recuperado",
                            valor = "${imp?.cantidadTotalRecuperada ?: 0.0} Kg",
                            icono = Icons.Default.Eco,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            titulo = "Entregas Exitosas",
                            valor = "${imp?.totalEntregas ?: 0}",
                            icono = Icons.Default.VolunteerActivism,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricCard(
                            titulo = "Tasa de Rescate",
                            valor = "${imp?.porcentajeRecuperacion ?: 0.0} %",
                            icono = Icons.Default.TrendingUp,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            titulo = "Lotes Ofrecidos",
                            valor = "${imp?.totalLotesPublicados ?: 0}",
                            icono = Icons.Default.Inventory,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // BOTÓN CERRAR SESIÓN (PÍLDORA SALVIA ECO ABASTO #BEC69D)
                    Button(
                        onClick = onCerrarSesion,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EcoSageGreen,
                            contentColor = EcoTextPrimary
                        )
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = EcoTextPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cerrar Sesión", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EcoTextPrimary)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun PerfilFieldCard(
    label: String,
    valor: String,
    onClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EcoSurface),
        border = BorderStroke(1.dp, EcoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(label, fontSize = 11.sp, color = EcoTextMuted, fontWeight = FontWeight.Medium)
                Text(valor, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EcoTextPrimary)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = EcoTextMuted, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun MetricCard(
    titulo: String,
    valor: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = EcoSurface),
        border = BorderStroke(1.dp, EcoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Surface(
                shape = CircleShape,
                color = EcoMintLight,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icono, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(20.dp))
                }
            }
            Text(titulo, fontSize = 11.sp, color = EcoTextMuted)
            Text(valor, fontSize = 16.sp, fontWeight = FontWeight.Black, color = EcoTextPrimary)
        }
    }
}
