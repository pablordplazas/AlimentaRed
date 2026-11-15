package com.example.alimentared.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.util.Locale

data class UbicacionPunto(
    val latitud: Double,
    val longitud: Double,
    val direccion: String,
    val nombreLugar: String? = null
)

object LogisticaLocationUtils {

    // Coordenadas base por defecto (Bogotá - Plaza de Paloquemao / Centro)
    const val DEFAULT_LAT = 4.6145
    const val DEFAULT_LNG = -74.0886
    const val DEFAULT_DIRECCION = "Plaza de Paloquemao, Calle 19 # 25-04, Bogotá"

    // Puntos logísticos estratégicos predefinidos de alta frecuencia en donaciones
    val PUNTOS_PREDEFINIDOS = listOf(
        UbicacionPunto(
            latitud = 4.6284,
            longitud = -74.1537,
            direccion = "Av. Carrera 80 # 2-51, Kennedy",
            nombreLugar = "Central Mayorista Corabastos"
        ),
        UbicacionPunto(
            latitud = 4.6145,
            longitud = -74.0886,
            direccion = "Calle 19 # 25-04, Los Mártires",
            nombreLugar = "Plaza de Mercado Paloquemao"
        ),
        UbicacionPunto(
            latitud = 4.6750,
            longitud = -74.1350,
            direccion = "Av. Calle 26 # 68-15, Fontibón",
            nombreLugar = "Zona Industrial y Logística Calle 26"
        ),
        UbicacionPunto(
            latitud = 4.5981,
            longitud = -74.0758,
            direccion = "Carrera 7 # 12-40, La Candelaria",
            nombreLugar = "Centro Histórico / Carrera 7ma"
        ),
        UbicacionPunto(
            latitud = 4.6912,
            longitud = -74.0898,
            direccion = "Calle 74B # 69Q-35, Engativá",
            nombreLugar = "Plaza de Mercado Las Ferias"
        ),
        UbicacionPunto(
            latitud = 4.6975,
            longitud = -74.0305,
            direccion = "Carrera 6 # 119-24, Usaquén",
            nombreLugar = "Zona Norte Usaquén"
        )
    )

    private val REGEX_COORDENADAS = Regex("""(?:\[|\()\s*(?:Lat:\s*)?(-?\d+\.\d+)\s*,\s*(?:Lng:\s*)?(-?\d+\.\d+)\s*(?:\]|\))""", RegexOption.IGNORE_CASE)

    /**
     * Extrae las coordenadas y la dirección limpia de una cadena de ubicación.
     * Soporta formatos como:
     * - "Calle 123 # 45-67 [4.6284, -74.1537]"
     * - "Carrera 7 # 12-40 (Lat: 4.5981, Lng: -74.0758)"
     * - "Plaza Paloquemao" (aplica fallback a punto conocido o coordenadas por defecto)
     */
    fun extraerCoordenadas(textoUbicacion: String?): UbicacionPunto {
        if (textoUbicacion.isNullOrBlank()) {
            return UbicacionPunto(
                latitud = DEFAULT_LAT,
                longitud = DEFAULT_LNG,
                direccion = DEFAULT_DIRECCION,
                nombreLugar = "Punto de Entrega"
            )
        }

        val match = REGEX_COORDENADAS.find(textoUbicacion)
        if (match != null && match.groupValues.size >= 3) {
            val lat = match.groupValues[1].toDoubleOrNull() ?: DEFAULT_LAT
            val lng = match.groupValues[2].toDoubleOrNull() ?: DEFAULT_LNG
            val dirLimpia = extraerDireccionLimpia(textoUbicacion)
            return UbicacionPunto(
                latitud = lat,
                longitud = lng,
                direccion = if (dirLimpia.isNotBlank()) dirLimpia else DEFAULT_DIRECCION,
                nombreLugar = null
            )
        }

        // Buscar coincidencia con puntos predefinidos por similitud de texto
        val textoNorm = textoUbicacion.trim().lowercase(Locale.ROOT)
        val conocido = PUNTOS_PREDEFINIDOS.find { punto ->
            val palabrasLugar = (punto.nombreLugar ?: "").lowercase(Locale.ROOT).split(" ").filter { it.length > 4 }
            palabrasLugar.any { textoNorm.contains(it) } ||
            punto.direccion.lowercase(Locale.ROOT).contains(textoNorm) ||
            textoNorm.contains(punto.direccion.lowercase(Locale.ROOT))
        }

        if (conocido != null) {
            return conocido.copy(direccion = textoUbicacion.trim())
        }

        return UbicacionPunto(
            latitud = DEFAULT_LAT,
            longitud = DEFAULT_LNG,
            direccion = textoUbicacion.trim(),
            nombreLugar = "Ubicación Registrada"
        )
    }

    /**
     * Devuelve la dirección sin el bloque de coordenadas [lat, lng].
     */
    fun extraerDireccionLimpia(textoUbicacion: String?): String {
        if (textoUbicacion.isNullOrBlank()) return ""
        return textoUbicacion.replace(REGEX_COORDENADAS, "").trim().trimEnd(',', '-', ' ')
    }

    /**
     * Formatea la dirección junto con las coordenadas exactas para guardarse en el backend.
     */
    fun formatearUbicacionParaGuardar(direccion: String, latitud: Double, longitud: Double): String {
        val dirLimpia = extraerDireccionLimpia(direccion)
        val latStr = String.format(Locale.US, "%.5f", latitud)
        val lngStr = String.format(Locale.US, "%.5f", longitud)
        return if (dirLimpia.isNotBlank()) {
            "$dirLimpia [$latStr, $lngStr]"
        } else {
            "Punto de Entrega [$latStr, $lngStr]"
        }
    }

    /**
     * Abre la ubicación en aplicaciones de navegación externas (Google Maps, Waze, etc.)
     */
    fun abrirEnMapasExternos(
        context: Context,
        latitud: Double,
        longitud: Double,
        nombreLugar: String = "Punto de Entrega AlimentaRed"
    ) {
        val encodedLabel = Uri.encode(nombreLugar)
        val uri = Uri.parse("geo:$latitud,$longitud?q=$latitud,$longitud($encodedLabel)")
        val intent = Intent(Intent.ACTION_VIEW, uri)

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback a navegador web con Google Maps
            try {
                val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitud,$longitud")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                context.startActivity(webIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "No se pudo abrir la aplicación de mapas", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
