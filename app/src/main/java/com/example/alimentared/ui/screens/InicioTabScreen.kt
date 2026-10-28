package com.example.alimentared.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
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
            .padding(16.dp)
    ) {
        // BANNER DE ENCABEZADO DE NOTICIAS
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BlueDark),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Campaign, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Impacto en Vivo", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                    Text("Entérate de cómo AlimentaRed transforma vidas diariamente.", fontSize = 11.sp, color = TextMuted)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Últimas Novedades y Logros", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BlueDark)
            Text("${noticias.size} publicaciones", fontSize = 11.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(noticias) { item ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = if (item.esDestacada) BorderStroke(1.2.dp, Color(0xFFF59E0B)) else null,
                    elevation = CardDefaults.cardElevation(defaultElevation = if (item.esDestacada) 3.dp else 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (item.esDestacada) {
                                    Surface(
                                        color = Color(0xFFFEF3C7),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(11.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                "DESTACADA",
                                                color = Color(0xFF92400E),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Text(
                                    item.categoria,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.esDestacada) Color(0xFFD97706) else RedPrimary
                                )
                            }

                            Text(item.fecha, fontSize = 10.sp, color = Color.Gray)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(item.titulo, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BlueDark)
                        Text("Origen: ${item.entidad}", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(item.resumen, fontSize = 12.sp, color = Color.DarkGray, lineHeight = 16.sp)
                    }
                }
            }
        }
    }
}
