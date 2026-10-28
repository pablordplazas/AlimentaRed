package com.example.alimentared.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alimentared.ui.theme.*

@Composable
fun OnboardingScreen(
    onIrALogin: () -> Unit,
    onIrAPruebas: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueDark)
    ) {
        // LOGOS E ICONOS DE FONDO DECORATIVOS (Sutiles con alpha)
        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = RedPrimary,
                modifier = Modifier
                    .size(180.dp)
                    .offset(x = (-40).dp, y = 60.dp)
                    .alpha(0.08f)
            )
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = RedPrimary,
                modifier = Modifier
                    .size(240.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 60.dp, y = 40.dp)
                    .alpha(0.08f)
            )
            Icon(
                imageVector = Icons.Default.VolunteerActivism,
                contentDescription = null,
                tint = RedPrimary,
                modifier = Modifier
                    .size(170.dp)
                    .align(Alignment.CenterStart)
                    .offset(x = (-50).dp, y = 140.dp)
                    .alpha(0.06f)
            )
            Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = null,
                tint = Color(0xFF16A34A),
                modifier = Modifier
                    .size(140.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = 30.dp)
                    .alpha(0.06f)
            )
        }

        // CONTENIDO PRINCIPAL
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            // BRANDING
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    color = RedPrimary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = RedPrimary,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(56.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "ALIMENTARED",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Plataforma de Rescate & Distribución Responsable",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // TARJETA SOBRE NOSOTROS
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BlueCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = RedPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "¿Quiénes Somos?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Somos una extensión de la plataforma de la Universidad de Cundinamarca, apoyando al ciudadano y familias en condición de vulnerabilidad mediante la recolección, control de calidad y redistribución eficiente de donaciones de alimentos.",
                        fontSize = 13.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFF475569))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Impacto Social Acumulado:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = RedPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("+24,800 kg", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.White)
                            Text("Alimentos Rescatados", fontSize = 10.sp, color = TextMuted)
                        }
                        Column {
                            Text("42", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.White)
                            Text("Fundaciones Beneficiadas", fontSize = 10.sp, color = TextMuted)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BOTÓN ACCESO REAL AL PORTAL
            Button(
                onClick = onIrALogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
            ) {
                Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ingresar al Portal de Acceso", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // BOTÓN MODO DE PRUEBAS RÁPIDAS
            OutlinedButton(
                onClick = onIrAPruebas,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.4f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Science, contentDescription = null, tint = RedPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cuentas y Modo de Pruebas", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
