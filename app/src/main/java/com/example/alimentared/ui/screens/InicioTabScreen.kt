package com.example.alimentared.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alimentared.data.noticias.NoticiasManager
import com.example.alimentared.ui.theme.*

@Composable
fun InicioTabScreen() {
    val noticiasManager = remember { NoticiasManager.getInstance() }
    val noticias by noticiasManager.noticiasFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EcoBackground)
            .padding(16.dp)
    ) {
        // BANNER DE ENCABEZADO DE NOTICIAS (ESTILO ECO ABASTO)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = EcoOliveGreen),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF6B824B),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Campaign,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        "Impacto en Vivo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                    Text(
                        "Entérate de cómo Eco Abasto transforma vidas a diario.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Últimas Novedades y Logros",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = EcoTextPrimary
            )
            Surface(
                shape = RoundedCornerShape(50),
                color = EcoMintGreen
            ) {
                Text(
                    "${noticias.size} publicaciones",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoForestGreen,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(noticias) { item ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = EcoSurface),
                    border = BorderStroke(1.dp, if (item.esDestacada) EcoAmber else EcoBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (item.esDestacada) 3.dp else 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (item.esDestacada) {
                                    Surface(
                                        color = EcoAmber,
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                "DESTACADA",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = EcoMintGreen
                                ) {
                                    Text(
                                        item.categoria,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EcoForestGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Text(item.fecha, fontSize = 11.sp, color = EcoTextMuted)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(item.titulo, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EcoTextPrimary)
                        Text("Origen: ${item.entidad}", fontSize = 12.sp, color = EcoTextMuted)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(item.resumen, fontSize = 12.sp, color = EcoTextPrimary.copy(alpha = 0.85f), lineHeight = 17.sp)
                    }
                }
            }
        }
    }
}
