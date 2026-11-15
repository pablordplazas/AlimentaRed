package com.example.alimentared.util

import androidx.compose.ui.graphics.Color
import com.example.alimentared.ui.theme.EcoAmber
import com.example.alimentared.ui.theme.EcoAmberDark
import com.example.alimentared.ui.theme.EcoForestGreen
import com.example.alimentared.ui.theme.EcoMintGreen
import com.example.alimentared.ui.theme.EcoMintLight
import com.example.alimentared.ui.theme.EcoRed
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class NivelPrioridadVencimiento {
    VENCIDO,
    CRITICA, // Menos de 24 horas (Rescate Urgente)
    ALTA,    // Entre 24 y 72 horas (Próximo a vencer / 1 a 3 días)
    MEDIA,   // Entre 4 y 7 días
    NORMAL   // Más de 7 días (enlatados, atún, no perecederos)
}

data class InfoVencimiento(
    val fechaDate: Date?,
    val nivel: NivelPrioridadVencimiento,
    val esPrioridadAlta: Boolean,
    val estaVencido: Boolean,
    val horasRestantes: Long,
    val diasRestantes: Long,
    val fechaFormateada: String,
    val fechaLargaFormateada: String,
    val tiempoRestanteTexto: String,
    val badgeTexto: String,
    val badgeColor: Color,
    val badgeTextColor: Color,
    val alertaMensaje: String?
)

object VencimientoUtils {

    private val formatosLectura = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm",
        "yyyy-MM-dd",
        "dd/MM/yyyy",
        "dd-MM-yyyy",
        "yyyy/MM/dd"
    )

    fun parsearFecha(fechaStr: String?): Date? {
        if (fechaStr.isNullOrBlank()) return null
        val limpia = fechaStr.trim()
        for (patron in formatosLectura) {
            try {
                val sdf = SimpleDateFormat(patron, Locale.getDefault())
                sdf.isLenient = false
                val date = sdf.parse(limpia)
                if (date != null) {
                    // Si el formato no contenía hora (solo fecha), fijar al fin del día (23:59:59)
                    if (!patron.contains("HH")) {
                        val cal = Calendar.getInstance().apply {
                            time = date
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                            set(Calendar.SECOND, 59)
                            set(Calendar.MILLISECOND, 999)
                        }
                        return cal.time
                    }
                    return date
                }
            } catch (ignored: Exception) {}
        }
        return null
    }

    fun formatearParaEnvioBackend(fechaStr: String?): String? {
        val date = parsearFecha(fechaStr) ?: return null
        val sdfIso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return sdfIso.format(date)
    }

    fun validarFechaCaducidad(fechaStr: String?): String? {
        if (fechaStr.isNullOrBlank()) {
            return "La fecha de caducidad es obligatoria."
        }
        val date = parsearFecha(fechaStr)
            ?: return "Formato inválido. Usa AAAA-MM-DD o DD/MM/AAAA."

        val ahora = System.currentTimeMillis()
        if (date.time <= ahora) {
            return "La fecha de caducidad debe ser posterior a la fecha y hora actual."
        }
        return null
    }

    fun calcularFechaSugerida(diasDesdeHoy: Int): String {
        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, diasDesdeHoy)
        }
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(cal.time)
    }

    fun esPrioridadAlta(fechaStr: String?, rescateUrgenteBackend: Boolean = false): Boolean {
        if (rescateUrgenteBackend) return true
        val info = calcularInfoVencimiento(fechaStr, rescateUrgenteBackend)
        return info.esPrioridadAlta
    }

    fun calcularInfoVencimiento(fechaStr: String?, rescateUrgenteBackend: Boolean = false): InfoVencimiento {
        val date = parsearFecha(fechaStr)

        val sdfCorta = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val sdfLarga = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-ES"))

        if (date == null) {
            return InfoVencimiento(
                fechaDate = null,
                nivel = if (rescateUrgenteBackend) NivelPrioridadVencimiento.CRITICA else NivelPrioridadVencimiento.NORMAL,
                esPrioridadAlta = rescateUrgenteBackend,
                estaVencido = false,
                horasRestantes = 0,
                diasRestantes = 0,
                fechaFormateada = fechaStr ?: "Sin fecha",
                fechaLargaFormateada = fechaStr ?: "Sin fecha",
                tiempoRestanteTexto = if (rescateUrgenteBackend) "⚡ Rescate Urgente" else "Vigencia no especificada",
                badgeTexto = if (rescateUrgenteBackend) "⚡ Rescate Urgente" else "Alimento",
                badgeColor = if (rescateUrgenteBackend) EcoAmber else EcoMintLight,
                badgeTextColor = if (rescateUrgenteBackend) Color.White else EcoForestGreen,
                alertaMensaje = if (rescateUrgenteBackend) "Alerta de Rescate Urgente activa." else null
            )
        }

        val ahora = System.currentTimeMillis()
        val diffMillis = date.time - ahora
        val fechaFormateada = sdfCorta.format(date)
        val fechaLarga = sdfLarga.format(date)

        if (diffMillis <= 0) {
            return InfoVencimiento(
                fechaDate = date,
                nivel = NivelPrioridadVencimiento.VENCIDO,
                esPrioridadAlta = false,
                estaVencido = true,
                horasRestantes = 0,
                diasRestantes = 0,
                fechaFormateada = fechaFormateada,
                fechaLargaFormateada = fechaLarga,
                tiempoRestanteTexto = "Caducado el $fechaFormateada",
                badgeTexto = "🚫 Caducado",
                badgeColor = EcoRed,
                badgeTextColor = Color.White,
                alertaMensaje = "Este lote ha superado su fecha de caducidad ($fechaFormateada)."
            )
        }

        val horas = diffMillis / (1000 * 3600)
        val dias = diffMillis / (1000 * 3600 * 24)

        return when {
            rescateUrgenteBackend || horas < 24 -> {
                val textoHoras = if (horas <= 1) "menos de 1 hora" else "$horas horas"
                InfoVencimiento(
                    fechaDate = date,
                    nivel = NivelPrioridadVencimiento.CRITICA,
                    esPrioridadAlta = true,
                    estaVencido = false,
                    horasRestantes = horas,
                    diasRestantes = dias,
                    fechaFormateada = fechaFormateada,
                    fechaLargaFormateada = fechaLarga,
                    tiempoRestanteTexto = "Caduca hoy (en $textoHoras)",
                    badgeTexto = "⚡ Vence hoy ($textoHoras)",
                    badgeColor = EcoAmber,
                    badgeTextColor = Color.White,
                    alertaMensaje = "¡Alerta de Prioridad Máxima! Caduca en $textoHoras. Requiere rescate urgente para evitar desperdicio."
                )
            }
            dias <= 3 -> {
                val diasTexto = if (dias == 1L) "1 día" else "$dias días"
                InfoVencimiento(
                    fechaDate = date,
                    nivel = NivelPrioridadVencimiento.ALTA,
                    esPrioridadAlta = true,
                    estaVencido = false,
                    horasRestantes = horas,
                    diasRestantes = dias,
                    fechaFormateada = fechaFormateada,
                    fechaLargaFormateada = fechaLarga,
                    tiempoRestanteTexto = "Vence en $diasTexto ($fechaFormateada)",
                    badgeTexto = "⚡ Prioridad: Vence en $diasTexto",
                    badgeColor = EcoAmber,
                    badgeTextColor = Color.White,
                    alertaMensaje = "¡Alerta de Prioridad Alta! Caduca en $diasTexto ($fechaFormateada). Se recomienda solicitarlo pronto."
                )
            }
            dias <= 7 -> {
                InfoVencimiento(
                    fechaDate = date,
                    nivel = NivelPrioridadVencimiento.MEDIA,
                    esPrioridadAlta = false,
                    estaVencido = false,
                    horasRestantes = horas,
                    diasRestantes = dias,
                    fechaFormateada = fechaFormateada,
                    fechaLargaFormateada = fechaLarga,
                    tiempoRestanteTexto = "Vence en $dias días ($fechaFormateada)",
                    badgeTexto = "🕒 Vence en $dias días",
                    badgeColor = Color(0xFFFEF3C7),
                    badgeTextColor = Color(0xFF92400E),
                    alertaMensaje = null
                )
            }
            else -> {
                val textoTiempo = when {
                    dias < 30 -> "en $dias días"
                    dias < 365 -> "en ${dias / 30} meses"
                    else -> "en más de 1 año"
                }
                InfoVencimiento(
                    fechaDate = date,
                    nivel = NivelPrioridadVencimiento.NORMAL,
                    esPrioridadAlta = false,
                    estaVencido = false,
                    horasRestantes = horas,
                    diasRestantes = dias,
                    fechaFormateada = fechaFormateada,
                    fechaLargaFormateada = fechaLarga,
                    tiempoRestanteTexto = "Vence $textoTiempo ($fechaFormateada)",
                    badgeTexto = "📅 Vence: $fechaFormateada",
                    badgeColor = EcoMintLight,
                    badgeTextColor = EcoForestGreen,
                    alertaMensaje = null
                )
            }
        }
    }
}
