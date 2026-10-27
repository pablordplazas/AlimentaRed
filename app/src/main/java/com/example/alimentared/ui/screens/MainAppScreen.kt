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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("ALIMENTARED - $rolUsuario", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
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
                    label = { Text("Inicio", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = RedPrimary, selectedTextColor = RedPrimary, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)
                )
                NavigationBarItem(
                    selected = pestañaActual == 1,
                    onClick = { pestañaActual = 1 },
                    icon = { Icon(Icons.Default.VolunteerActivism, contentDescription = "Donaciones") },
                    label = { Text("Donaciones", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = RedPrimary, selectedTextColor = RedPrimary, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)
                )
                NavigationBarItem(
                    selected = pestañaActual == 2,
                    onClick = { pestañaActual = 2 },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Nosotros") },
                    label = { Text("Nosotros", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = RedPrimary, selectedTextColor = RedPrimary, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)
                )
                NavigationBarItem(
                    selected = pestañaActual == 3,
                    onClick = { pestañaActual = 3 },
                    icon = { Icon(Icons.Default.ContactSupport, contentDescription = "Contacto") },
                    label = { Text("Contacto", fontSize = 11.sp) },
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
                2 -> NosotrosTabScreen()
                3 -> ContactoTabScreen()
            }
        }
    }
}
