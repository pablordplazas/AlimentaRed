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
import com.example.alimentared.ui.theme.EcoForestGreen
import com.example.alimentared.ui.theme.EcoOliveGreen
import com.example.alimentared.ui.theme.EcoMintGreen

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
                        Color(0xFFF9FAFB),
                        Color(0xFFF2F7F3)
                    )
                )
            )
    ) {
        // CAPA DECORATIVA DE FONDOS ECOLÓGICOS (Eco Abasto: Trigo, Hoja, Rescate sutiles)
        Box(modifier = Modifier.fillMaxSize()) {
            when (patternType) {
                BackgroundPatternType.LOGIN -> {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = EcoOliveGreen,
                        modifier = Modifier
                            .size(240.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 60.dp, y = (-20).dp)
                            .rotate(-15f)
                            .alpha(0.04f)
                    )
                    Icon(
                        imageVector = Icons.Default.VolunteerActivism,
                        contentDescription = null,
                        tint = EcoForestGreen,
                        modifier = Modifier
                            .size(190.dp)
                            .align(Alignment.CenterStart)
                            .offset(x = (-40).dp, y = 80.dp)
                            .rotate(12f)
                            .alpha(0.035f)
                    )
                    Icon(
                        imageVector = Icons.Default.Grass,
                        contentDescription = null,
                        tint = EcoOliveGreen,
                        modifier = Modifier
                            .size(170.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = 30.dp, y = 30.dp)
                            .rotate(20f)
                            .alpha(0.035f)
                    )
                }

                BackgroundPatternType.CATALOGO -> {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = EcoForestGreen,
                        modifier = Modifier
                            .size(180.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 40.dp, y = 20.dp)
                            .rotate(15f)
                            .alpha(0.035f)
                    )
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = EcoOliveGreen,
                        modifier = Modifier
                            .size(210.dp)
                            .align(Alignment.CenterStart)
                            .offset(x = (-50).dp, y = (-10).dp)
                            .rotate(-18f)
                            .alpha(0.035f)
                    )
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = EcoForestGreen,
                        modifier = Modifier
                            .size(140.dp)
                            .align(Alignment.BottomStart)
                            .offset(x = (-20).dp, y = 10.dp)
                            .rotate(-10f)
                            .alpha(0.03f)
                    )
                }

                BackgroundPatternType.PERFIL -> {
                    Icon(
                        imageVector = Icons.Default.VolunteerActivism,
                        contentDescription = null,
                        tint = EcoForestGreen,
                        modifier = Modifier
                            .size(220.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 50.dp, y = 10.dp)
                            .rotate(-10f)
                            .alpha(0.04f)
                    )
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = EcoOliveGreen,
                        modifier = Modifier
                            .size(170.dp)
                            .align(Alignment.BottomStart)
                            .offset(x = (-40).dp, y = 30.dp)
                            .rotate(18f)
                            .alpha(0.035f)
                    )
                }

                BackgroundPatternType.GENERAL -> {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = EcoForestGreen,
                        modifier = Modifier
                            .size(210.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 50.dp, y = (-10).dp)
                            .rotate(-12f)
                            .alpha(0.035f)
                    )
                    Icon(
                        imageVector = Icons.Default.VolunteerActivism,
                        contentDescription = null,
                        tint = EcoOliveGreen,
                        modifier = Modifier
                            .size(180.dp)
                            .align(Alignment.CenterStart)
                            .offset(x = (-40).dp, y = 50.dp)
                            .rotate(15f)
                            .alpha(0.035f)
                    )
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = EcoForestGreen,
                        modifier = Modifier
                            .size(140.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = 40.dp, y = 30.dp)
                            .rotate(22f)
                            .alpha(0.03f)
                    )
                }
            }
        }

        // CONTENIDO PRINCIPAL DE LA PANTALLA
        content()
    }
}
