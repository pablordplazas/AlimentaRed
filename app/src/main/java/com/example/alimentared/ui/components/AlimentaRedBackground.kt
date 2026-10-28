package com.example.alimentared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.alimentared.ui.theme.BlueDark
import com.example.alimentared.ui.theme.RedPrimary

enum class BackgroundPatternType {
    GENERAL,
    LOGIN,
    CATALOGO,
    PERFIL
}

@Composable
fun AlimentaRedBackground(
    modifier: Modifier = Modifier,
    patternType: BackgroundPatternType = BackgroundPatternType.GENERAL,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFFBFBFE),
                        Color(0xFFF1F5F9)
                    )
                )
            )
    ) {
        // CAPA DECORATIVA DE FONDOS (Logos, corazones e iconos temáticos sutiles)
        Box(modifier = Modifier.fillMaxSize()) {
            when (patternType) {
                BackgroundPatternType.LOGIN -> {
                    // 1. Corazón institucional grande superior derecho
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = RedPrimary,
                        modifier = Modifier
                            .size(240.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 60.dp, y = (-20).dp)
                            .rotate(-15f)
                            .alpha(0.045f)
                    )
                    // 2. Manos de ayuda / voluntariado centro-izq
                    Icon(
                        imageVector = Icons.Default.VolunteerActivism,
                        contentDescription = null,
                        tint = RedPrimary,
                        modifier = Modifier
                            .size(190.dp)
                            .align(Alignment.CenterStart)
                            .offset(x = (-40).dp, y = 80.dp)
                            .rotate(12f)
                            .alpha(0.04f)
                    )
                    // 3. Corazón secundario inferior derecho
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = RedPrimary,
                        modifier = Modifier
                            .size(170.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = 30.dp, y = 30.dp)
                            .rotate(20f)
                            .alpha(0.04f)
                    )
                    // 4. Hoja ecológica / rescate inferior izq
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = Color(0xFF16A34A),
                        modifier = Modifier
                            .size(130.dp)
                            .align(Alignment.BottomStart)
                            .offset(x = 10.dp, y = (-20).dp)
                            .rotate(-25f)
                            .alpha(0.035f)
                    )
                }

                BackgroundPatternType.CATALOGO -> {
                    // 1. Restaurante / Alimentos superior derecho
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = BlueDark,
                        modifier = Modifier
                            .size(180.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 40.dp, y = 20.dp)
                            .rotate(15f)
                            .alpha(0.035f)
                    )
                    // 2. Corazón de rescate centro izquierdo
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = RedPrimary,
                        modifier = Modifier
                            .size(210.dp)
                            .align(Alignment.CenterStart)
                            .offset(x = (-50).dp, y = (-10).dp)
                            .rotate(-18f)
                            .alpha(0.04f)
                    )
                    // 3. Hoja ecológica inferior derecha
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = Color(0xFF16A34A),
                        modifier = Modifier
                            .size(160.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = 40.dp, y = 30.dp)
                            .rotate(25f)
                            .alpha(0.035f)
                    )
                    // 4. Inventario / Donaciones inferior izquierda
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = BlueDark,
                        modifier = Modifier
                            .size(140.dp)
                            .align(Alignment.BottomStart)
                            .offset(x = (-20).dp, y = 10.dp)
                            .rotate(-10f)
                            .alpha(0.03f)
                    )
                }

                BackgroundPatternType.PERFIL -> {
                    // 1. Manos de solidaridad superior derecha
                    Icon(
                        imageVector = Icons.Default.VolunteerActivism,
                        contentDescription = null,
                        tint = RedPrimary,
                        modifier = Modifier
                            .size(220.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 50.dp, y = 10.dp)
                            .rotate(-10f)
                            .alpha(0.045f)
                    )
                    // 2. Corazón inferior izquierdo
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = RedPrimary,
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.BottomStart)
                            .offset(x = (-40).dp, y = 30.dp)
                            .rotate(18f)
                            .alpha(0.04f)
                    )
                    // 3. Hoja ecológica centro derecha
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = Color(0xFF16A34A),
                        modifier = Modifier
                            .size(150.dp)
                            .align(Alignment.CenterEnd)
                            .offset(x = 30.dp, y = 100.dp)
                            .rotate(-20f)
                            .alpha(0.035f)
                    )
                }

                BackgroundPatternType.GENERAL -> {
                    // 1. Corazón superior derecho
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = RedPrimary,
                        modifier = Modifier
                            .size(210.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 50.dp, y = (-10).dp)
                            .rotate(-12f)
                            .alpha(0.045f)
                    )
                    // 2. Manos solidarias centro izquierdo
                    Icon(
                        imageVector = Icons.Default.VolunteerActivism,
                        contentDescription = null,
                        tint = RedPrimary,
                        modifier = Modifier
                            .size(180.dp)
                            .align(Alignment.CenterStart)
                            .offset(x = (-40).dp, y = 50.dp)
                            .rotate(15f)
                            .alpha(0.04f)
                    )
                    // 3. Hoja ecológica inferior derecha
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = Color(0xFF16A34A),
                        modifier = Modifier
                            .size(140.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = 40.dp, y = 30.dp)
                            .rotate(22f)
                            .alpha(0.035f)
                    )
                    // 4. Cubiertos / Alimentos inferior izquierdo
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = BlueDark,
                        modifier = Modifier
                            .size(130.dp)
                            .align(Alignment.BottomStart)
                            .offset(x = (-20).dp, y = (-20).dp)
                            .rotate(-15f)
                            .alpha(0.03f)
                    )
                }
            }
        }

        // CONTENIDO PRINCIPAL DE LA PANTALLA
        content()
    }
}
