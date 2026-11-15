package com.example.alimentared.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.alimentared.ui.theme.*
import com.example.alimentared.util.LogisticaLocationUtils
import com.example.alimentared.util.UbicacionPunto
import java.util.Locale

@Composable
fun MapaVisorDialog(
    ubicacionTexto: String?,
    nombreDonacion: String = "Donación AlimentaRed",
    instrucciones: String? = null,
    contacto: String? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val punto = remember(ubicacionTexto) {
        LogisticaLocationUtils.extraerCoordenadas(ubicacionTexto)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(24.dp),
            color = EcoBackground,
            border = BorderStroke(1.dp, EcoBorder)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 1. HEADER DEL VISOR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EcoForestGreen)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Map, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Ubicación Exacta de Recogida",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                nombreDonacion,
                                color = EcoMintLight,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }

                // 2. MAPA INTERACTIVO
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    InteractiveMapView(
                        latitud = punto.latitud,
                        longitud = punto.longitud,
                        esModoSeleccion = false,
                        tituloMarcador = nombreDonacion,
                        subtituloMarcador = punto.direccion,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // 3. TARJETA INFORMATIVA Y ACCIONES EXTERNAS
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, EcoBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    punto.direccion,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = EcoTextPrimary
                                )
                                Text(
                                    "Coordenadas: ${String.format(Locale.US, "%.5f", punto.latitud)}, ${String.format(Locale.US, "%.5f", punto.longitud)}",
                                    fontSize = 10.sp,
                                    color = EcoTextMuted
                                )
                            }
                        }

                        if (!instrucciones.isNullOrBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = EcoOliveGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Indicaciones: $instrucciones",
                                    fontSize = 11.sp,
                                    color = EcoTextMuted
                                )
                            }
                        }

                        if (!contacto.isNullOrBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Contacto operativo: $contacto",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EcoForestGreen
                                )
                            }
                        }

                        HorizontalDivider(color = EcoBorder, modifier = Modifier.padding(vertical = 4.dp))

                        // BOTONES DE ACCIÓN: ABRIR EN GOOGLE MAPS / WAZE Y COPIAR
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    LogisticaLocationUtils.abrirEnMapasExternos(
                                        context = context,
                                        latitud = punto.latitud,
                                        longitud = punto.longitud,
                                        nombreLugar = nombreDonacion
                                    )
                                },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = EcoForestGreen),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            ) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Cómo llegar", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                            }

                            OutlinedButton(
                                onClick = {
                                    clipboardManager.setText(
                                        AnnotatedString("${punto.direccion} (Coordenadas: ${punto.latitud}, ${punto.longitud})")
                                    )
                                    Toast.makeText(context, "Dirección y coordenadas copiadas", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, EcoForestGreen),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copiar", color = EcoForestGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
