package com.example.alimentared.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alimentared.data.session.SessionManager
import com.example.alimentared.ui.components.AlimentaRedBackground
import com.example.alimentared.ui.components.AlimentaRedTopBar
import com.example.alimentared.ui.components.BackgroundPatternType
import com.example.alimentared.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    rolUsuario: String,
    onLoteClick: (Long) -> Unit,
    onCrearLoteClick: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    var pestañaActual by remember { mutableStateOf(0) }
    var mostrarConfigServidor by remember { mutableStateOf(false) }

    val usuarioActual = remember {
        try { SessionManager.getInstance().getUsuario() } catch (e: Exception) { null }
    }

    val rolNormalized = (usuarioActual?.rol ?: rolUsuario).uppercase()
    val esAdmin = rolNormalized == "ADMIN" || rolNormalized == "ADMINISTRADOR"

    // Definir pestañas dinámicas según el rol del usuario
    val tabs = when (rolNormalized) {
        "DONANTE" -> listOf(
            Triple("Inicio", Icons.Default.Home, 0),
            Triple("Mis Lotes", Icons.Default.Storefront, 1),
            Triple("Solicitudes", Icons.AutoMirrored.Filled.Assignment, 2),
            Triple("Calidad", Icons.Default.ReportProblem, 3),
            Triple("Impacto", Icons.Default.Person, 4)
        )
        "RECEPTORA" -> listOf(
            Triple("Inicio", Icons.Default.Home, 0),
            Triple("Catálogo", Icons.Default.VolunteerActivism, 1),
            Triple("Solicitudes", Icons.AutoMirrored.Filled.Assignment, 2),
            Triple("Calidad", Icons.Default.ReportProblem, 3),
            Triple("Perfil", Icons.Default.Person, 4)
        )
        "COORDINADOR" -> listOf(
            Triple("Inicio", Icons.Default.Home, 0),
            Triple("Asignaciones", Icons.AutoMirrored.Filled.Assignment, 1),
            Triple("Apelaciones", Icons.Default.Gavel, 2),
            Triple("Incidencias", Icons.Default.ReportProblem, 3),
            Triple("Perfil", Icons.Default.Person, 4)
        )
        "ADMINISTRADOR" -> listOf(
            Triple("Inicio", Icons.Default.Home, 0),
            Triple("Organizaciones", Icons.Default.Business, 1),
            Triple("Apelaciones", Icons.Default.Gavel, 2),
            Triple("Incidencias", Icons.Default.ReportProblem, 3),
            Triple("Perfil", Icons.Default.Person, 4)
        )
        else -> listOf(
            Triple("Inicio", Icons.Default.Home, 0),
            Triple("Catálogo", Icons.Default.VolunteerActivism, 1),
            Triple("Solicitudes", Icons.AutoMirrored.Filled.Assignment, 2),
            Triple("Calidad", Icons.Default.ReportProblem, 3),
            Triple("Perfil", Icons.Default.Person, 4)
        )
    }

    Scaffold(
        topBar = {
            AlimentaRedTopBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(28.dp),
                            shadowElevation = 1.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = "AlimentaRed",
                                    tint = GreenSilver,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "AlimentaRed • ${usuarioActual?.organizacion?.nombre ?: usuarioActual?.nombre ?: rolUsuario}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                },
                actions = {
                    if (esAdmin) {
                        IconButton(onClick = { mostrarConfigServidor = true }, modifier = Modifier.size(36.dp)) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Configurar Servidor Backend",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    IconButton(onClick = onCerrarSesion, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Salir",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            // NAVBAR ELEGANTE ESTILO ECO ABASTO (Fondo blanco, píldora menta en elemento activo)
            Surface(
                color = EcoSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp),
                border = BorderStroke(1.dp, EcoBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tabs.forEach { (title, icon, index) ->
                        val isSelected = pestañaActual == index
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { pestañaActual = index }
                                .padding(vertical = 4.dp, horizontal = if (isSelected) 8.dp else 4.dp)
                        ) {
                            if (isSelected) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = EcoMintGreen,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = title,
                                            tint = EcoForestGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = title,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EcoForestGreen
                                        )
                                    }
                                }
                            } else {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = title,
                                    tint = EcoTextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = title,
                                    fontSize = 10.sp,
                                    color = EcoTextMuted,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        AlimentaRedBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            patternType = when (pestañaActual) {
                1 -> BackgroundPatternType.CATALOGO
                4 -> BackgroundPatternType.PERFIL
                else -> BackgroundPatternType.GENERAL
            }
        ) {
            when (pestañaActual) {
                0 -> InicioTabScreen()
                1 -> if (esAdmin) {
                    PanelVerificacionOrganizacionesScreen()
                } else {
                    DonacionesTabScreen(
                        rolUsuario = rolNormalized,
                        onLoteClick = onLoteClick,
                        onCrearLoteClick = onCrearLoteClick
                    )
                }
                2 -> when (rolNormalized) {
                    "COORDINADOR", "ADMINISTRADOR" -> PanelApelacionesScreen(onBack = { pestañaActual = 0 })
                    else -> SolicitudesScreen(onVolver = { pestañaActual = 1 })
                }
                3 -> IncidenciasScreen(onBack = { pestañaActual = 0 })
                4 -> PerfilScreen(
                    onCerrarSesion = onCerrarSesion,
                    onIrACatalogo = { pestañaActual = 1 }
                )
            }
        }
    }

    if (mostrarConfigServidor) {
        ConfigurarServidorDialog(
            onDismiss = { mostrarConfigServidor = false }
        )
    }
}
