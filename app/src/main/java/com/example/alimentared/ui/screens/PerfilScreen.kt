package com.example.alimentared.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import com.example.alimentared.data.model.StrikeResponse
import com.example.alimentared.ui.theme.*
import com.example.alimentared.ui.viewmodels.PerfilImpactoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    viewModel: PerfilImpactoViewModel = viewModel(),
    onCerrarSesion: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var strikeAApelar by remember { mutableStateOf<StrikeResponse?>(null) }
    var justificacionApelacion by remember { mutableStateOf("") }

    val user = uiState.usuario
    val rol = user?.rol?.uppercase() ?: ""
    val esDonanteOReceptora = rol.contains("DONANTE") || rol.contains("RECEPTOR")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil y Métricas de Impacto", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión", tint = Color.White)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // MENSAJES DE ESTADO
            if (uiState.mensajeExito != null) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)), modifier = Modifier.fillMaxWidth()) {
                    Text(uiState.mensajeExito!!, color = Color(0xFF14532D), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
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
                                Text("Verificada", color = Color(0xFF14532D), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }

                    Text(user?.organizacion?.nombre ?: user?.nombre ?: "AlimentaRed", fontWeight = FontWeight.Black, fontSize = 18.sp, color = BlueDark)
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
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Gavel, contentDescription = null, tint = RedPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Strikes Acumulados (RN08)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BlueDark)
                            }
                            Text("$strikesVigentes / 3", fontWeight = FontWeight.Black, fontSize = 16.sp, color = if (strikesVigentes >= 3) RedPrimary else BlueDark)
                        }

                        LinearProgressIndicator(
                            progress = { (strikesVigentes / 3.0f).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = if (strikesVigentes >= 3) RedPrimary else Color(0xFFF59E0B),
                            trackColor = Color(0xFFE2E8F0),
                        )

                        if (strikesVigentes >= 3) {
                            Text(
                                "⛔ CUENTA SUSPENDIDA: Al acumular 3 strikes por inasistencia o impuntualidad, tu organización no puede solicitar ni publicar alimentos. Radica una apelación para revisión (RN08/RN12).",
                                color = RedDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                "Al acumular 3 strikes vigentes, la cuenta se suspende automáticamente.",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        // Lista de strikes con opción de apelar
                        if (uiState.strikes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Detalle de Sanciones:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BlueDark)
                            uiState.strikes.forEach { strike ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("• ${strike.nombreStrike}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = BlueDark)
                                        Text(strike.detalles ?: "Infracción logística", fontSize = 10.sp, color = Color.Gray)
                                    }
                                    if (strike.vigente) {
                                        TextButton(onClick = { strikeAApelar = strike }) {
                                            Text("Apelar (RN12)", fontSize = 11.sp, color = RedPrimary, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Text("Anulado", fontSize = 10.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
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
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = RedPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Panel de Control Operativo ($rol)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BlueDark)
                        }
                        Text(
                            "Tu perfil cuenta con privilegios administrativos y de auditoría. Los strikes y sanciones (RN08 / RN12) aplican exclusivamente a organizaciones Donantes y Receptoras por cumplimiento de franjas horarias e inocuidad.",
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // 3. KPIS DE IMPACTO SOCIAL Y AMBIENTAL (RF09)
            val imp = uiState.impacto
            Text("Impacto Global de AlimentaRed (RF09)", fontWeight = FontWeight.Black, fontSize = 16.sp, color = BlueDark)

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

    // Modal de Apelación de Strike (RN12) - Exclusivo para Donante o Receptora
    if (esDonanteOReceptora && strikeAApelar != null) {
        val s = strikeAApelar!!
        AlertDialog(
            onDismissRequest = { strikeAApelar = null },
            title = { Text("Apelar Strike", fontWeight = FontWeight.Bold, color = BlueDark) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Motivo: ${s.nombreStrike}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text("Ingresa la justificación de fuerza mayor que causó la infracción:", fontSize = 12.sp, color = Color.Gray)

                    OutlinedTextField(
                        value = justificacionApelacion,
                        onValueChange = { justificacionApelacion = it },
                        label = { Text("Justificación detallada") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.apelarStrike(s.id, justificacionApelacion)
                        justificacionApelacion = ""
                        strikeAApelar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
                ) {
                    Text("Enviar Apelación", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { strikeAApelar = null }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
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
