package com.example.alimentared

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alimentared.ui.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlimentaRedApp()
        }
    }
}

@Composable
fun AlimentaRedApp() {
    val navController = rememberNavController()
    var rolUsuarioActual by remember { mutableStateOf("Donante") }

    NavHost(navController = navController, startDestination = "onboarding") {
        
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
                onBack = { navController.popBackStack() }
            )
        }

        composable("publish") {
            PublishLoteScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
