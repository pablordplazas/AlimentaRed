package com.example.alimentared.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.alimentared.ui.theme.*
import com.example.alimentared.util.LogisticaLocationUtils
import com.example.alimentared.util.UbicacionPunto
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaSelectorDialog(
    ubicacionInicial: String?,
    onDismiss: () -> Unit,
    onUbicacionConfirmada: (UbicacionPunto) -> Unit
) {
    val context = LocalContext.current
    val puntoInicial = remember(ubicacionInicial) {
        LogisticaLocationUtils.extraerCoordenadas(ubicacionInicial)
    }

    var latitudActual by remember { mutableDoubleStateOf(puntoInicial.latitud) }
    var longitudActual by remember { mutableDoubleStateOf(puntoInicial.longitud) }
    var direccionTexto by remember { mutableStateOf(puntoInicial.direccion) }
    var nombreLugarTexto by remember { mutableStateOf(puntoInicial.nombreLugar ?: "Punto de Entrega") }

    // Launcher para permisos de ubicación
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            obtenerUbicacionDispositivo(context) { lat, lng ->
                latitudActual = lat
                longitudActual = lng
                direccionTexto = "Mi Ubicación Actual (GPS)"
                nombreLugarTexto = "Punto GPS"
                Toast.makeText(context, "Ubicación GPS obtenida", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    fun solicitarUbicacionGps() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacionDispositivo(context) { lat, lng ->
                latitudActual = lat
                longitudActual = lng
                direccionTexto = "Mi Ubicación Actual (GPS)"
                nombreLugarTexto = "Punto GPS"
                Toast.makeText(context, "Ubicación GPS obtenida", Toast.LENGTH_SHORT).show()
            }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
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
                // 1. TOP BAR DEL DIÁLOGO DE MAPA
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
                                Icon(Icons.Default.PinDrop, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Fijar Ubicación en el Mapa",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                "Toca el mapa o arrastra el pin verde",
                                color = EcoMintLight,
                                fontSize = 11.sp
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

                // 2. CHIPS DE PUNTOS LOGÍSTICOS ESTRATÉGICOS
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        "Puntos logísticos rápidos:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoTextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(LogisticaLocationUtils.PUNTOS_PREDEFINIDOS) { punto ->
                            val isSelected = (Math.abs(punto.latitud - latitudActual) < 0.0001 && Math.abs(punto.longitud - longitudActual) < 0.0001)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    latitudActual = punto.latitud
                                    longitudActual = punto.longitud
                                    direccionTexto = punto.direccion
                                    nombreLugarTexto = punto.nombreLugar ?: "Punto Logístico"
                                },
                                shape = RoundedCornerShape(50),
                                label = {
                                    Text(
                                        punto.nombreLugar ?: punto.direccion,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EcoMintGreen,
                                    selectedLabelColor = EcoForestGreen,
                                    containerColor = EcoBackground
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) EcoForestGreen else EcoBorder
                                )
                            )
                        }
                    }
                }

                // 3. MAPA INTERACTIVO CON BOTÓN FLOTANTE GPS
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    InteractiveMapView(
                        latitud = latitudActual,
                        longitud = longitudActual,
                        esModoSeleccion = true,
                        tituloMarcador = nombreLugarTexto,
                        subtituloMarcador = direccionTexto,
                        onCoordenadasCambiadas = { nuevaLat, nuevaLng ->
                            latitudActual = nuevaLat
                            longitudActual = nuevaLng
                            // Si el usuario movió manualmente el pin, actualizamos etiqueta
                            if (!direccionTexto.contains("GPS") && !direccionTexto.contains("Fijado")) {
                                direccionTexto = "Ubicación fijada en el mapa"
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Botón flotante para GPS del dispositivo
                    FloatingActionButton(
                        onClick = { solicitarUbicacionGps() },
                        containerColor = Color.White,
                        contentColor = EcoForestGreen,
                        shape = CircleShape,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .size(46.dp)
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = "Mi ubicación", modifier = Modifier.size(22.dp))
                    }
                }

                // 4. TARJETA INFERIOR CON DETALLES EDITABLES Y CONFIRMACIÓN
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Detalles del Punto de Entrega",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = EcoForestGreen
                            )
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = EcoMintLight
                            ) {
                                Text(
                                    "Lat: ${String.format(Locale.US, "%.5f", latitudActual)}, Lng: ${String.format(Locale.US, "%.5f", longitudActual)}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoForestGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = direccionTexto,
                            onValueChange = { direccionTexto = it },
                            label = { Text("Dirección o referencia exacta", fontSize = 12.sp) },
                            placeholder = { Text("Ej: Muelle 4, Calle 19 # 25-04", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Place, contentDescription = null, tint = EcoForestGreen, modifier = Modifier.size(18.dp))
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = EcoBackground,
                                unfocusedContainerColor = EcoBackground,
                                focusedBorderColor = EcoForestGreen,
                                unfocusedBorderColor = EcoBorder
                            )
                        )

                        // BOTÓN DE CONFIRMACIÓN
                        Button(
                            onClick = {
                                val punto = UbicacionPunto(
                                    latitud = latitudActual,
                                    longitud = longitudActual,
                                    direccion = direccionTexto.trim(),
                                    nombreLugar = nombreLugarTexto
                                )
                                onUbicacionConfirmada(punto)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = EcoForestGreen)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Confirmar Este Punto en el Mapa", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

private fun obtenerUbicacionDispositivo(context: Context, onUbicacionObtenida: (Double, Double) -> Unit) {
    try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (locationManager != null) {
            val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)
            for (p in providers) {
                if (locationManager.isProviderEnabled(p)) {
                    @Suppress("MissingPermission")
                    val loc = locationManager.getLastKnownLocation(p)
                    if (loc != null) {
                        onUbicacionObtenida(loc.latitude, loc.longitude)
                        return
                    }
                }
            }
        }
    } catch (ignored: Exception) {}
    // Fallback a coordenadas por defecto de Bogotá
    onUbicacionObtenida(LogisticaLocationUtils.DEFAULT_LAT, LogisticaLocationUtils.DEFAULT_LNG)
}
