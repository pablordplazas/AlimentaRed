package com.example.alimentared.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.alimentared.ui.theme.*
import com.example.alimentared.ui.viewmodels.PerfilImpactoViewModel

enum class PerfilSubVista {
    PERFIL,
    DETALLE_STRIKES,
    PANEL_APELACIONES,
    PANEL_VERIFICACION
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    viewModel: PerfilImpactoViewModel = viewModel(),
    onCerrarSesion: () -> Unit = {}
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
        PerfilSubVista.PANEL_VERIFICACION -> {
            PanelVerificacionOrganizacionesScreen(
                onBack = {
                    subVista = PerfilSubVista.PERFIL
                    viewModel.cargarDatos()
                }
            )
        }
        PerfilSubVista.PERFIL -> {
            // Contenedor principal sin barra de navegación duplicada
            Surface(
                color = Color.Transparent,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Encabezado de sección
                    Text(
                        "Perfil y Métricas de Impacto",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = BlueDark
                    )

                    // MENSAJES DE ESTADO
                    if (uiState.mensajeExito != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                uiState.mensajeExito!!,
                                color = Color(0xFF14532D),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                                        // 1. TARJETA DE ORGANIZACIÓN Y USUARIO
                    // ESTADO DE VERIFICACIÓN INSTITUCIONAL (RF02)
                    if (esDonanteOReceptora) {
                        val estaVerificada = user?.organizacion?.verificada == true
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (estaVerificada) Color(0xFFF0FDF4) else Color(0xFFFFFBEB)
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(
                                    if (estaVerificada) Color(0xFF86EFAC) else Color(0xFFFDE68A)
                                )
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        if (estaVerificada) Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                                        contentDescription = null,
                                        tint = if (estaVerificada) Color(0xFF16A34A) else Color(0xFFD97706),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        if (estaVerificada) "Organización Verificada y Habilitada" else "Organización Pendiente de Aprobación",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (estaVerificada) Color(0xFF14532D) else Color(0xFF92400E)
                                    )
                                }
                                Text(
                                    if (estaVerificada) {
                                        "Tu perfil ha sido verificado y aprobado por el Administrador. Estás plenamente habilitado para operar en AlimentaRed."
                                    } else if (rol.contains("DONANTE")) {
                                        "Tu cuenta está en proceso de revisión por el Administrador. Mientras tanto, puedes crear y guardar lotes como BORRADOR. Una vez aprobada tu organización, podrás publicarlos en el catálogo oficial."
                                    } else {
                                        "Tu fundación está en proceso de revisión por el Administrador. Puedes explorar el catálogo de donaciones en tiempo real; una vez aprobada tu cuenta, podrás solicitar alimentos."
                                    },
                                    fontSize = 11.sp,
                                    color = if (estaVerificada) Color(0xFF166534) else Color(0xFF78350F),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    // 1. TARJETA DE ORGANIZACIÓN Y USUARIO
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(user?.rol ?: "ROL", color = RedPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                if (user?.organizacion?.verificada == true) {
                                    Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(6.dp)) {
                                        Text(
                                            "Verificada",
                                            color = Color(0xFF14532D),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                user?.organizacion?.nombre ?: user?.nombre ?: "AlimentaRed",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = BlueDark
                            )
                            Text("Representante: ${user?.nombre ?: "Usuario"}", fontSize = 12.sp, color = Color.Gray)
                            Text("Correo: ${user?.correo ?: ""}", fontSize = 12.sp, color = Color.Gray)
                            if (user?.organizacion?.nit != null) {
                                Text("NIT: ${user.organizacion.nit}", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }

                    // 2. VISUALIZADOR DE STRIKES Y SANCIONES (RN08 / RN12) - EXCLUSIVO PARA DONANTE Y RECEPTORA
                    if (esDonanteOReceptora) {
                        val strikesVigentes = uiState.strikesVigentes
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (strikesVigentes >= 3) Color(0xFFFEE2E2) else Color.White
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(
                                    if (strikesVigentes >= 3) RedPrimary else Color(0xFFE2E8F0)
                                )
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Gavel, contentDescription = null, tint = RedPrimary)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Strikes Acumulados", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BlueDark)
                                    }
                                    Text(
                                        "$strikesVigentes / 3",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = if (strikesVigentes >= 3) RedPrimary else BlueDark
                                    )
                                }

                                LinearProgressIndicator(
                                    progress = { (strikesVigentes / 3.0f).coerceIn(0f, 1f) },
                                    modifier = Modifier.fillMaxWidth().height(8.dp),
                                    color = if (strikesVigentes >= 3) RedPrimary else Color(0xFFF59E0B),
                                    trackColor = Color(0xFFE2E8F0),
                                )

                                if (strikesVigentes >= 3) {
                                    Text(
                                        "⛔ CUENTA SUSPENDIDA: Al acumular 3 strikes vigentes, tu organización no puede solicitar ni publicar alimentos. Envía un correo a coordinador@alimentared.com para solicitar la revisión y apelación de tu caso.",
                                        color = RedDark,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 16.sp
                                    )
                                } else {
                                    Text(
                                        "Al acumular 3 strikes vigentes, la cuenta se suspende automáticamente.",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                OutlinedButton(
                                    onClick = { subVista = PerfilSubVista.DETALLE_STRIKES },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, if (strikesVigentes >= 3) RedPrimary else BlueDark.copy(alpha = 0.25f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (strikesVigentes >= 3) RedPrimary else BlueDark
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Ver detalle e historial de mis strikes (${uiState.strikes.size})",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (strikesVigentes >= 3) RedPrimary else BlueDark
                                    )
                                }
                            }
                        }
                    } else {
                        // PANEL INFORMATIVO PARA COORDINADOR Y ADMINISTRADOR (NO RECIBEN STRIKES)
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = RedPrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Panel de Control Operativo ($rol)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BlueDark)
                                }
                                Text(
                                    "Tu perfil cuenta con privilegios administrativos y de auditoría. Los strikes y sanciones aplican exclusivamente a organizaciones Donantes y Receptoras por cumplimiento de franjas horarias e inocuidad.",
                                    fontSize = 12.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 17.sp
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                                                Button(
                                    onClick = { subVista = PerfilSubVista.PANEL_APELACIONES },
                                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("⚖️ Abrir Panel de Gestión de Apelaciones", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                if (rol.contains("ADMIN")) {
                                    Button(
                                        onClick = { subVista = PerfilSubVista.PANEL_VERIFICACION },
                                        colors = ButtonDefaults.buttonColors(containerColor = BlueDark),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("🏢 Aprobación y Verificación de Organizaciones", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // 3. KPIS DE IMPACTO SOCIAL Y AMBIENTAL
                    val imp = uiState.impacto
                    Text("Impacto Global de AlimentaRed", fontWeight = FontWeight.Black, fontSize = 16.sp, color = BlueDark)

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
                }
            }
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(icono, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(22.dp))
            Text(titulo, fontSize = 11.sp, color = Color.Gray)
            Text(valor, fontSize = 16.sp, fontWeight = FontWeight.Black, color = BlueDark)
        }
    }
}

