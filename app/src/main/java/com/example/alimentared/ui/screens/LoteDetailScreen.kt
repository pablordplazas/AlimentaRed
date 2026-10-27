package com.example.alimentared.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alimentared.data.model.FakeDataRepository
import com.example.alimentared.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoteDetailScreen(
    loteId: Long,
    rolUsuario: String,
    onBack: () -> Unit
) {
    val lote = remember(loteId) { FakeDataRepository.obtenerLotePorId(loteId) }
    var cantidadSolicitada by remember { mutableStateOf("") }
    var mensajeExito by remember { mutableStateOf(false) }

    if (lote == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Lote no encontrado")
        }
        return
    }

    // Inicializar con el múltiplo mínimo del lote seleccionado
    LaunchedEffect(lote) {
        cantidadSolicitada = lote.multiploMinimo.toString()
    }

    val cantidadNum = cantidadSolicitada.toDoubleOrNull() ?: 0.0
    val remanente = lote.cantidadDisponible - cantidadNum

    // Validación de Reglas RN05 y RN10
    val esMultiploValido = cantidadNum > 0 && (cantidadNum % lote.multiploMinimo == 0.0)
    val esRemanenteValido = remanente == 0.0 || remanente >= lote.multiploMinimo
    val esValido = esMultiploValido && esRemanenteValido && cantidadNum <= lote.cantidadDisponible

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(lote.nombre, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlueDark)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {
            // TARJETA DE RESUMEN DEL LOTE
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BlueDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(lote.categoria.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RedPrimary)
                    Text(lote.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                    Text("Donante: ${lote.donante}", fontSize = 12.sp, color = TextMuted)
                    Text("Tipo: ${lote.tipoAlimento}", fontSize = 12.sp, color = TextMuted)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFF475569))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Disponible Total", fontSize = 10.sp, color = TextMuted)
                            Text("${lote.cantidadDisponible} ${lote.unidad}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        }
                        Column {
                            Text("Múltiplo Mínimo", fontSize = 10.sp, color = TextMuted)
                            Text("${lote.multiploMinimo} ${lote.unidad}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = RedPrimary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SI MUESTRA ALERTA DE ÉXITO
            if (mensajeExito) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = RedPrimary.copy(alpha = 0.1f)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(RedPrimary)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = RedPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("¡Solicitud Enviada con Éxito!", fontWeight = FontWeight.Bold, color = BlueDark, fontSize = 14.sp)
                            Text("Se envió al Coordinador para aprobación. Tiempo estimado: 2 a 3 días.", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }

            // VISTA SEGÚN EL ROL
            when (rolUsuario) {
                "Receptora" -> {
                    Text("Formulario de Solicitud (Fundación):", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BlueDark)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = cantidadSolicitada,
                        onValueChange = { cantidadSolicitada = it },
                        label = { Text("Cantidad a Solicitar (${lote.unidad})") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RedPrimary, focusedLabelColor = RedPrimary)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Remanente que quedará: $remanente ${lote.unidad}", fontSize = 12.sp, color = if (esRemanenteValido) BlueDark else RedPrimary)

                    if (!esMultiploValido) Text("* La cantidad debe ser múltiplo de ${lote.multiploMinimo}", color = RedPrimary, fontSize = 11.sp)
                    if (!esRemanenteValido) Text("* No se puede dejar un remanente menor al múltiplo mínimo", color = RedPrimary, fontSize = 11.sp)

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            FakeDataRepository.crearSolicitud(lote, cantidadNum, "Fundación Rescate")
                            mensajeExito = true
                        },
                        enabled = esValido && !mensajeExito,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
                    ) {
                        Text("Confirmar Reserva de Donación", fontWeight = FontWeight.Bold)
                    }
                }

                "Donante" -> {
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = RedPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Gestión de Donante", fontWeight = FontWeight.Bold, color = BlueDark)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Este lote fue publicado por tu comercio. Puedes monitorear solicitudes entrantes desde el panel de coordinación.", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }

                "Coordinador", "Administrador" -> {
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Panel de Gobernanza", fontWeight = FontWeight.Bold, color = BlueDark)
                            Text("Como supervisor puedes auditar este lote o resolver empates de asignación.", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
