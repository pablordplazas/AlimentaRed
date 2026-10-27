package com.example.alimentared.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alimentared.data.model.StrikeResponse
import com.example.alimentared.data.model.UsuarioResponse
import com.example.alimentared.ui.theme.BackgroundLight
import com.example.alimentared.ui.theme.BlueDark
import com.example.alimentared.ui.theme.RedDark
import com.example.alimentared.ui.theme.RedPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleStrikesScreen(
    usuario: UsuarioResponse?,
    strikes: List<StrikeResponse>,
    strikesVigentes: Int,
    onBack: () -> Unit
) {
    val estaSuspendida = strikesVigentes >= 3 || (usuario?.organizacion?.bloqueada == true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalle de Sanciones (RN08)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
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
            // 1. TARJETA RESUMEN DE LA ORGANIZACIÓN
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                usuario?.organizacion?.nombre ?: usuario?.nombre ?: "Mi Organización",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = BlueDark
                            )
                            if (usuario?.organizacion?.nit != null) {
                                Text(
                                    "NIT: ${usuario.organizacion.nit}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (estaSuspendida) Color(0xFFFEE2E2) else if (strikesVigentes > 0) Color(0xFFFEF3C7) else Color(0xFFDCFCE7)
                        ) {
                            Text(
                                if (estaSuspendida) "SUSPENDIDA" else if (strikesVigentes > 0) "ADVERTENCIA" else "AL DÍA",
                                color = if (estaSuspendida) RedPrimary else if (strikesVigentes > 0) Color(0xFFB45309) else Color(0xFF15803D),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    // Indicador de strikes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Strikes Vigentes Acumulados:",
                            fontSize = 13.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "$strikesVigentes / 3",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = if (estaSuspendida) RedPrimary else if (strikesVigentes > 0) Color(0xFFF59E0B) else Color(0xFF16A34A)
                        )
                    }

                    LinearProgressIndicator(
                        progress = { (strikesVigentes / 3.0f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = if (estaSuspendida) RedPrimary else Color(0xFFF59E0B),
                        trackColor = Color(0xFFE2E8F0)
                    )
                }
            }

            // 2. AVISO DE SUSPENSIÓN (SI TIENE 3 O MÁS STRIKES)
            if (estaSuspendida) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(RedPrimary)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Block, contentDescription = null, tint = RedPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "⛔ CUENTA SUSPENDIDA (RN08)",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = RedDark
                            )
                        }
                        Text(
                            "Tu organización ha acumulado 3 strikes vigentes por incumplimiento de horarios o inasistencia. No puedes publicar ni solicitar alimentos en la plataforma.",
                            fontSize = 12.sp,
                            color = RedDark,
                            lineHeight = 17.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Email, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        "Envía un correo para apelar a:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BlueDark
                                    )
                                    Text(
                                        "coordinador@alimentared.com",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RedPrimary
                                    )
                                    Text(
                                        "Adjunta la justificación y evidencias de tu caso para que la coordinación evalúe la reactivación.",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. LISTADO DETALLADO DE STRIKES O MENSAJE VACÍO
            if (strikes.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF86EFAC))
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            "¡Sin strikes acumulados!",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = Color(0xFF14532D)
                        )
                        Text(
                            "Tu organización no cuenta con ninguna sanción ni strike registrado en AlimentaRed. ¡Felicitaciones por mantener una operación puntual y responsable!",
                            fontSize = 12.sp,
                            color = Color(0xFF166534),
                            lineHeight = 18.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                Text(
                    "Historial de Infracciones Registradas (${strikes.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = BlueDark
                )

                strikes.forEach { strike ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Gavel,
                                        contentDescription = null,
                                        tint = if (strike.vigente) RedPrimary else Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        strike.nombreStrike,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = BlueDark
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (strike.vigente) Color(0xFFFEE2E2) else Color(0xFFDCFCE7)
                                ) {
                                    Text(
                                        if (strike.vigente) "VIGENTE" else "ANULADO",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (strike.vigente) RedPrimary else Color(0xFF15803D),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            if (strike.fechaRegistro != null) {
                                Text(
                                    "Fecha de registro: ${strike.fechaRegistro}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }

                            Text(
                                strike.detalles ?: "Infracción logística o incumplimiento de horario.",
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )

                            HorizontalDivider(color = Color(0xFFF1F5F9))

                            if (strike.vigente) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFFFFBEB), RoundedCornerShape(6.dp))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        tint = Color(0xFFD97706),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Para solicitar apelación (RN12), envía un correo a coordinador@alimentared.com indicando el ID de strike (#${strike.id}).",
                                        fontSize = 10.sp,
                                        color = Color(0xFF92400E),
                                        lineHeight = 14.sp
                                    )
                                }
                            } else {
                                Text(
                                    "✓ Apelación evaluada: ${strike.justificacionApelacion ?: "Sanción anulada por la coordinación"}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF16A34A),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
