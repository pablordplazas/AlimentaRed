package com.example.alimentared

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alimentared.data.session.SessionManager
import com.example.alimentared.ui.screens.*
import com.example.alimentared.ui.theme.AlimentaRedTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionManager.initialize(applicationContext)

        setContent {
            AlimentaRedTheme {
                AlimentaRedApp()
            }
        }
    }
}

@Composable
fun AlimentaRedApp() {
    val navController = rememberNavController()
    val sessionManager = remember { SessionManager.getInstance() }
    val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState()
    var rolUsuarioActual by remember { mutableStateOf(sessionManager.getRol()) }

    val startDest = if (isLoggedIn) "main" else "onboarding"

    NavHost(navController = navController, startDestination = startDest) {

        composable("onboarding") {
            OnboardingScreen(
                onIrALogin = { navController.navigate("login") }
            )
        }

        composable("login") {
            LoginScreen(
                onLoginExitoso = { rol ->
                    rolUsuarioActual = rol
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                        popUpTo("onboarding") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("main") {
            MainAppScreen(
                rolUsuario = rolUsuarioActual,
                onLoteClick = { id -> navController.navigate("detail/$id") },
                onCrearLoteClick = { navController.navigate("publish") },
                onCerrarSesion = {
                    sessionManager.clearSession()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }

        composable("detail/{id}") { backStack ->
            val id = backStack.arguments?.getString("id")?.toLongOrNull() ?: 1L
            LoteDetailScreen(
                loteId = id,
                rolUsuario = rolUsuarioActual,
                onBack = { navController.popBackStack() },
                onReportarIncidencia = { loteId -> navController.navigate("incidencias/$loteId") }
            )
        }

        composable("publish") {
            PublishLoteScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("incidencias/{loteId}") { backStack ->
            val loteId = backStack.arguments?.getString("loteId")?.toLongOrNull()
            IncidenciasScreen(
                loteIdInicial = loteId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
