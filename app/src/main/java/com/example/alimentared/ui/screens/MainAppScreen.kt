package com.example.alimentared.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.alimentared.data.session.SessionManager
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

    val usuarioActual = remember {
        try { SessionManager.getInstance().getUsuario() } catch (e: Exception) { null }
    }

    val rolNormalized = rolUsuario.uppercase()

    // Definir pestañas dinámicas según el rol del usuario
    val tabs = when (rolNormalized) {
        "DONANTE" -> listOf(
            Triple("Inicio", Icons.Default.Home, 0),
            Triple("Mis Lotes", Icons.Default.Store, 1),
            Triple("Solicitudes", Icons.Default.Assignment, 2),
            Triple("Calidad", Icons.Default.ReportProblem, 3),
            Triple("Impacto", Icons.Default.Person, 4)
        )
        "RECEPTORA" -> listOf(
            Triple("Inicio", Icons.Default.Home, 0),
            Triple("Catálogo", Icons.Default.VolunteerActivism, 1),
            Triple("Solicitudes", Icons.Default.Assignment, 2),
            Triple("Calidad", Icons.Default.ReportProblem, 3),
            Triple("Impacto", Icons.Default.Person, 4)
        )
        "COORDINADOR" -> listOf(
            Triple("Inicio", Icons.Default.Home, 0),
            Triple("Asignaciones", Icons.Default.Assignment, 1),
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
            Triple("Solicitudes", Icons.Default.Assignment, 2),
            Triple("Calidad", Icons.Default.ReportProblem, 3),
            Triple("Perfil", Icons.Default.Person, 4)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "ALIMENTARED • ${usuarioActual?.organizacion?.nombre ?: usuarioActual?.nombre ?: rolUsuario}",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = Color.White,
                        maxLines = 1
                    )
                },
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Salir", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlueDark)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = BlueDark, contentColor = Color.White) {
                tabs.forEach { (title, icon, index) ->
                    NavigationBarItem(
                        selected = pestañaActual == index,
                        onClick = { pestañaActual = index },
                        icon = { Icon(icon, contentDescription = title) },
                        label = { Text(title, fontSize = 9.sp, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RedPrimary,
                            selectedTextColor = RedPrimary,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (pestañaActual) {
                0 -> InicioTabScreen()
                1 -> DonacionesTabScreen(
                    rolUsuario = rolNormalized,
                    onLoteClick = onLoteClick,
                    onCrearLoteClick = onCrearLoteClick
                )
                2 -> when (rolNormalized) {
                    "COORDINADOR", "ADMINISTRADOR" -> PanelApelacionesScreen(onBack = { pestañaActual = 0 })
                    else -> SolicitudesScreen(onVolver = { pestañaActual = 1 })
                }
                3 -> IncidenciasScreen(onBack = { pestañaActual = 0 })
                4 -> PerfilScreen(onCerrarSesion = onCerrarSesion)
            }
        }
    }
}
