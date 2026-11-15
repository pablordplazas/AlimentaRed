package com.example.alimentared

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alimentared.data.session.SessionManager
import com.example.alimentared.ui.screens.*
import com.example.alimentared.ui.theme.AlimentaRedTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ocultar barra de estado (elimina hora, señal, internet y batería en todas las pantallas)
        ocultarBarraDeEstado()

        val session = SessionManager.initialize(applicationContext)
        com.example.alimentared.data.noticias.NoticiasManager.initialize(applicationContext)
        com.example.alimentared.data.usuarios.UsuariosInternosManager.initialize(applicationContext)
        com.example.alimentared.data.remote.RetrofitClient.updateBaseUrl(session.getServerUrl())

        setContent {
            AlimentaRedTheme {
                AlimentaRedApp()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            ocultarBarraDeEstado()
        }
    }

    private fun ocultarBarraDeEstado() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.statusBars())
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
                onIrALogin = { navController.navigate("login") },
                onIrAPruebas = { navController.navigate("login_pruebas") }
            )
        }

        composable("login") {
            LoginScreen(
                esModoPruebas = false,
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

        composable("login_pruebas") {
            LoginScreen(
                esModoPruebas = true,
                onLoginExitoso = { rol ->
                    rolUsuarioActual = rol
                    navController.navigate("main") {
                        popUpTo("login_pruebas") { inclusive = true }
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
                    navController.navigate("onboarding") {
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
