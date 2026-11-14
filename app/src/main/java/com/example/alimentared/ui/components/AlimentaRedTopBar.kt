package com.example.alimentared.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.alimentared.ui.theme.EcoOliveGreen

/**
 * Calcula el relleno superior seguro para todas las pantallas de la aplicación.
 * Garantiza un margen mínimo de 36.dp para que el texto de la barra superior
 * quede ubicado más abajo y nunca choque con la cámara frontal (punch hole, notch)
 * ni con la barra de estado en ningún modelo de celular.
 */
@Composable
fun getAlimentaRedTopPadding(): Dp {
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val cutoutTop = WindowInsets.displayCutout.asPaddingValues().calculateTopPadding()
    val safeDrawingTop = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding()
    return maxOf(statusBarTop, cutoutTop, safeDrawingTop, 36.dp)
}

/**
 * Barra superior unificada de AlimentaRed.
 * Cubre el área superior del celular con el color institucional (EcoOliveGreen) y
 * posiciona el título, navegación y acciones a una altura cómoda y segura.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlimentaRedTopBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    containerColor: Color = EcoOliveGreen
) {
    val topPadding = getAlimentaRedTopPadding()

    Surface(
        color = containerColor,
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = topPadding)
        ) {
            TopAppBar(
                windowInsets = WindowInsets(0.dp),
                modifier = Modifier.height(52.dp),
                title = title,
                navigationIcon = navigationIcon,
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    }
}
