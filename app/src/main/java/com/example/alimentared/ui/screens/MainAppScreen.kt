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
    var pestañaActual by remember { mutableStateOf(1) } // Comienza en el catálogo de donaciones

    val usuarioActual = remember {
        try { SessionManager.getInstance().getUsuario() } catch (e: Exception) { null }
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
                NavigationBarItem(
                    selected = pestañaActual == 0,
                    onClick = { pestañaActual = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = RedPrimary, selectedTextColor = RedPrimary, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)
                )
                NavigationBarItem(
                    selected = pestañaActual == 1,
                    onClick = { pestañaActual = 1 },
                    icon = { Icon(Icons.Default.VolunteerActivism, contentDescription = "Catálogo") },
                    label = { Text("Catálogo", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = RedPrimary, selectedTextColor = RedPrimary, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)
                )
                NavigationBarItem(
                    selected = pestañaActual == 2,
                    onClick = { pestañaActual = 2 },
                    icon = { Icon(Icons.Default.Assignment, contentDescription = "Solicitudes") },
                    label = { Text("Solicitudes", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = RedPrimary, selectedTextColor = RedPrimary, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)
                )
                NavigationBarItem(
                    selected = pestañaActual == 3,
                    onClick = { pestañaActual = 3 },
                    icon = { Icon(Icons.Default.ReportProblem, contentDescription = "Incidencias") },
                    label = { Text("Calidad", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = RedPrimary, selectedTextColor = RedPrimary, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)
                )
                NavigationBarItem(
                    selected = pestañaActual == 4,
                    onClick = { pestañaActual = 4 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Impacto", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = RedPrimary, selectedTextColor = RedPrimary, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (pestañaActual) {
                0 -> InicioTabScreen()
                1 -> DonacionesTabScreen(
                    rolUsuario = rolUsuario,
                    onLoteClick = onLoteClick,
                    onCrearLoteClick = onCrearLoteClick
                )
                2 -> SolicitudesScreen(
                    onVolver = { pestañaActual = 1 }
                )
                3 -> IncidenciasScreen(
                    onBack = { pestañaActual = 1 }
                )
                4 -> PerfilScreen(
                    onCerrarSesion = onCerrarSesion
                )
            }
        }
    }
}
