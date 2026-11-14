package com.example.alimentared.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.alimentared.ui.components.AlimentaRedTopBar
import com.example.alimentared.ui.theme.*
import com.example.alimentared.util.removerCodigosRegla

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
            AlimentaRedTopBar(
                title = {
                    Text(
                        "Detalle de Sanciones",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. TARJETA RESUMEN DE LA ORGANIZACIÓN ESTILO ECO ABASTO
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = EcoSurface),
                border = BorderStroke(1.dp, EcoBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
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
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                color = EcoTextPrimary
                            )
                            if (usuario?.organizacion?.nit != null) {
                                Text(
                                    "NIT: ${usuario.organizacion.nit}",
                                    fontSize = 12.sp,
                                    color = EcoTextMuted
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (estaSuspendida) EcoRedLight else if (strikesVigentes > 0) Color(0xFFFEF3C7) else EcoMintGreen
                        ) {
                            Text(
                                if (estaSuspendida) "SUSPENDIDA" else if (strikesVigentes > 0) "ADVERTENCIA" else "AL DÍA",
                                color = if (estaSuspendida) EcoRed else if (strikesVigentes > 0) Color(0xFFB45309) else EcoForestGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = EcoBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Strikes Vigentes Acumulados:",
                            fontSize = 13.sp,
                            color = EcoTextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "$strikesVigentes / 3",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = if (estaSuspendida) EcoRed else if (strikesVigentes > 0) EcoAmber else EcoForestGreen
                        )
                    }

                    LinearProgressIndicator(
                        progress = { (strikesVigentes / 3.0f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = if (estaSuspendida) EcoRed else EcoAmber,
                        trackColor = EcoBorder
                    )
                }
            }

            // 2. AVISO DE SUSPENSIÓN (SI TIENE 3 O MÁS STRIKES)
            if (estaSuspendida) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = EcoRedLight),
                    border = BorderStroke(1.dp, EcoRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Block, contentDescription = null, tint = EcoRed)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "⛔ CUENTA SUSPENDIDA",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = EcoRed
                            )
                        }
                        Text(
                            "Tu organización ha acumulado 3 strikes vigentes por incumplimiento de horarios o inasistencia. No puedes publicar ni solicitar alimentos en la plataforma.",
                            fontSize = 12.sp,
                            color = EcoRed,
                            lineHeight = 17.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Email, contentDescription = null, tint = EcoOliveGreen, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        "Envía un correo para apelar a:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = EcoTextPrimary
                                    )
                                    Text(
                                        "coordinador@alimentared.com",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EcoForestGreen
                                    )
                                    Text(
                                        "Adjunta la justificación y evidencias de tu caso para que la coordinación evalúe la reactivación.",
                                        fontSize = 10.sp,
                                        color = EcoTextMuted
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
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = EcoMintLight),
                    border = BorderStroke(1.dp, EcoMintGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(26.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EcoMintGreen,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EcoForestGreen,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Text(
                            "¡Sin strikes acumulados!",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = EcoForestGreen
                        )
                        Text(
                            "Tu organización no cuenta con ninguna sanción ni strike registrado en Eco Abasto. ¡Felicitaciones por mantener una operación puntual y responsable!",
                            fontSize = 12.sp,
                            color = EcoTextPrimary,
                            lineHeight = 18.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                Text(
                    "Historial de Infracciones Registradas (${strikes.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = EcoTextPrimary
                )

                strikes.forEach { strike ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = EcoSurface),
                        border = BorderStroke(1.dp, EcoBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Gavel,
                                        contentDescription = null,
                                        tint = if (strike.vigente) EcoRed else EcoTextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        strike.nombreStrike.removerCodigosRegla(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = EcoTextPrimary
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = if (strike.vigente) EcoRedLight else EcoMintGreen
                                ) {
                                    Text(
                                        if (strike.vigente) "VIGENTE" else "ANULADO",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (strike.vigente) EcoRed else EcoForestGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            if (strike.fechaRegistro != null) {
                                Text(
                                    "Fecha de registro: ${strike.fechaRegistro}",
                                    fontSize = 11.sp,
                                    color = EcoTextMuted
                                )
                            }

                            Text(
                                strike.detalles?.removerCodigosRegla() ?: "Infracción logística o incumplimiento de horario.",
                                fontSize = 12.sp,
                                color = EcoTextPrimary.copy(alpha = 0.85f)
                            )

                            HorizontalDivider(color = EcoBorder)

                            if (strike.vigente) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFFFFBEB), RoundedCornerShape(10.dp))
                                        .padding(10.dp),
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
                                        "Para solicitar apelación, envía un correo a coordinador@alimentared.com indicando el ID de strike (#${strike.id}).",
                                        fontSize = 10.sp,
                                        color = Color(0xFF92400E),
                                        lineHeight = 14.sp
                                    )
                                }
                            } else {
                                Text(
                                    "✓ Apelación evaluada: ${strike.justificacionApelacion?.removerCodigosRegla() ?: "Sanción anulada por la coordinación"}",
                                    fontSize = 11.sp,
                                    color = EcoForestGreen,
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
