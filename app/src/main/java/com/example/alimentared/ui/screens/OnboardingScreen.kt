package com.example.alimentared.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alimentared.ui.components.getAlimentaRedTopPadding
import com.example.alimentared.ui.theme.*

@Composable
fun OnboardingScreen(
    onIrALogin: () -> Unit,
    onIrAPruebas: () -> Unit
) {
    val topPadding = getAlimentaRedTopPadding()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EcoBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ARCO SUPERIOR ESTILO ECO ABASTO
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp + topPadding)
                    .background(
                        color = EcoOliveGreen,
                        shape = RoundedCornerShape(bottomStart = 140.dp, bottomEnd = 140.dp)
                    )
                    .padding(top = topPadding),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Ícono circular blanco con corazón verde silver (AlimentaRed)
                    Surface(
                        modifier = Modifier.size(76.dp),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "AlimentaRed Logo",
                                tint = GreenSilver,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Divisor vertical
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(56.dp)
                            .background(Color.White.copy(alpha = 0.6f))
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Título AlimentaRed
                    Column {
                        Text(
                            text = "Alimenta",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 26.sp
                        )
                        Text(
                            text = "Red",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 26.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CONTENIDO CENTRAL
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "¡Alimentando Comunidades!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = EcoTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Plataforma de Rescate y Redistribución Sostenible",
                    fontSize = 13.sp,
                    color = EcoTextMuted,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(20.dp))

                // TARJETA SOBRE NOSOTROS (Estilo limpio Eco Abasto)
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = EcoSurface),
                    border = BorderStroke(1.dp, EcoBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = EcoMintLight,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.VolunteerActivism,
                                        contentDescription = null,
                                        tint = EcoForestGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "¿Quiénes Somos?",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = EcoTextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Somos una extensión comunitaria de la Universidad de Cundinamarca, articulando donantes y organizaciones sociales para rescatar alimentos aptos y reducir el desperdicio.",
                            fontSize = 13.sp,
                            color = EcoTextMuted,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = EcoBorder)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Impacto Social Acumulado:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = EcoForestGreen
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("+24,800 kg", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = EcoTextPrimary)
                                Text("Alimentos Rescatados", fontSize = 11.sp, color = EcoTextMuted)
                            }
                            Column {
                                Text("42", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = EcoTextPrimary)
                                Text("Fundaciones Beneficiadas", fontSize = 11.sp, color = EcoTextMuted)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // BOTÓN PRINCIPAL ESTILO PÍLDORA ECO ABASTO
                Button(
                    onClick = onIrALogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = EcoForestGreen)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ingresar al Portal de Acceso", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // BOTÓN SECUNDARIO MODO DE PRUEBAS
                Button(
                    onClick = onIrAPruebas,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EcoSageGreen,
                        contentColor = EcoTextPrimary
                    )
                ) {
                    Icon(Icons.Default.Science, contentDescription = null, tint = EcoForestGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cuentas y Modo de Pruebas", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EcoTextPrimary)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
