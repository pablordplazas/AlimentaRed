package com.example.alimentared.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alimentared.ui.theme.*
import com.example.alimentared.util.LogisticaLocationUtils
import java.util.Locale

@Composable
fun UbicacionLogisticaCard(
    ubicacionTexto: String?,
    esEditable: Boolean = false,
    titulo: String = "Punto Logístico de Recogida",
    subtitulo: String = "Ubicación fijada en el mapa",
    onAbrirSelector: (() -> Unit)? = null,
    onAbrirVisor: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val punto = remember(ubicacionTexto) {
        LogisticaLocationUtils.extraerCoordenadas(ubicacionTexto)
    }

    val tieneUbicacion = !ubicacionTexto.isNullOrBlank()

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = EcoSurface),
        border = BorderStroke(1.dp, if (tieneUbicacion) EcoForestGreen.copy(alpha = 0.5f) else EcoBorder),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header del Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = EcoMintLight,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.PinDrop, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            titulo,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = EcoTextPrimary
                        )
                        Text(
                            if (tieneUbicacion) punto.direccion else "Sin punto fijado en el mapa",
                            fontSize = 11.sp,
                            color = if (tieneUbicacion) EcoTextPrimary else EcoTextMuted,
                            maxLines = 1
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (tieneUbicacion) EcoMintGreen else EcoBackground
                ) {
                    Text(
                        if (tieneUbicacion) "En Mapa 📍" else "Pendiente",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (tieneUbicacion) EcoForestGreen else EcoTextMuted,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Vista previa de mapa interactivo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, EcoBorder, RoundedCornerShape(14.dp))
            ) {
                InteractiveMapView(
                    latitud = punto.latitud,
                    longitud = punto.longitud,
                    esModoSeleccion = false,
                    tituloMarcador = titulo,
                    subtituloMarcador = punto.direccion,
                    modifier = Modifier.fillMaxSize()
                )

                // Capa transparente para capturar clics en la vista previa del mini-mapa
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            if (esEditable) onAbrirSelector?.invoke() else onAbrirVisor?.invoke()
                        }
                )

                // Botón semi-transparente para ampliar o editar
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.Black.copy(alpha = 0.55f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (esEditable) Icons.Default.EditLocation else Icons.Default.Fullscreen,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            if (esEditable) "Cambiar en el mapa" else "Ver en pantalla completa",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Coordenadas y acciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Coordenadas: ${String.format(Locale.US, "%.4f", punto.latitud)}, ${String.format(Locale.US, "%.4f", punto.longitud)}",
                    fontSize = 10.sp,
                    color = EcoTextMuted,
                    fontWeight = FontWeight.Medium
                )

                if (esEditable) {
                    TextButton(
                        onClick = { onAbrirSelector?.invoke() },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Abrir Mapa", color = EcoForestGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    TextButton(
                        onClick = {
                            LogisticaLocationUtils.abrirEnMapasExternos(
                                context = context,
                                latitud = punto.latitud,
                                longitud = punto.longitud,
                                nombreLugar = titulo
                            )
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cómo llegar", color = EcoForestGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
