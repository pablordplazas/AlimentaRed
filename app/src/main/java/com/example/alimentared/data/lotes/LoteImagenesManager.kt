package com.example.alimentared.data.lotes

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Gestor centralizado de fotografías de lotes de alimentos en AlimentaRed.
 * Permite almacenar, asociar y recuperar entre 1 y 3 imágenes por lote,
 * garantizando la visibilidad e inspección sanitaria para los receptores.
 */
object LoteImagenesManager {

    private const val PREFS_NAME = "alimentared_lote_images_prefs"
    private const val KEY_MAPA_FOTOS = "mapa_lote_fotos_json"

    private var prefs: SharedPreferences? = null
    private val gson = Gson()

    // Memoria en caché de mapeo loteId -> Lista de rutas de imágenes
    private val mapaLoteFotos = mutableMapOf<Long, List<String>>()

    fun initialize(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            cargarDesdePrefs()
        }
    }

    private fun cargarDesdePrefs() {
        val json = prefs?.getString(KEY_MAPA_FOTOS, null) ?: return
        try {
            val type = object : TypeToken<Map<Long, List<String>>>() {}.type
            val guardado: Map<Long, List<String>>? = gson.fromJson(json, type)
            if (guardado != null) {
                mapaLoteFotos.clear()
                mapaLoteFotos.putAll(guardado)
            }
        } catch (ignored: Exception) {}
    }

    private fun guardarEnPrefs() {
        prefs?.edit()?.putString(KEY_MAPA_FOTOS, gson.toJson(mapaLoteFotos))?.apply()
    }

    /**
     * Guarda una imagen seleccionada desde Uri en el almacenamiento interno privado de la app,
     * optimizando su resolución y peso (máximo 1280px de ancho/alto y 85% de calidad JPEG).
     */
    fun guardarImagenDesdeUri(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmapOriginal = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmapOriginal == null) return null
            guardarBitmapOptimizado(context, bitmapOriginal)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Guarda un bitmap proveniente de la cámara en el almacenamiento interno.
     */
    fun guardarImagenDesdeBitmap(context: Context, bitmap: Bitmap): String? {
        return try {
            guardarBitmapOptimizado(context, bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun guardarBitmapOptimizado(context: Context, bitmapOriginal: Bitmap): String {
        val directorio = File(context.filesDir, "lotes_fotos").apply {
            if (!exists()) mkdirs()
        }

        // Redimensionar si supera 1280px
        val maxDim = 1280
        val ancho = bitmapOriginal.width
        val alto = bitmapOriginal.height
        val bitmapFinal = if (ancho > maxDim || alto > maxDim) {
            val ratio = ancho.toFloat() / alto.toFloat()
            val nuevoAncho: Int
            val nuevoAlto: Int
            if (ratio > 1) {
                nuevoAncho = maxDim
                nuevoAlto = (maxDim / ratio).toInt()
            } else {
                nuevoAlto = maxDim
                nuevoAncho = (maxDim * ratio).toInt()
            }
            Bitmap.createScaledBitmap(bitmapOriginal, nuevoAncho, nuevoAlto, true)
        } else {
            bitmapOriginal
        }

        val archivo = File(directorio, "foto_lote_${System.currentTimeMillis()}_${(1000..9999).random()}.jpg")
        FileOutputStream(archivo).use { out ->
            bitmapFinal.compress(Bitmap.CompressFormat.JPEG, 85, out)
            out.flush()
        }

        if (bitmapFinal != bitmapOriginal) {
            bitmapFinal.recycle()
        }
        bitmapOriginal.recycle()

        return archivo.absolutePath
    }

    /**
     * Asocia una lista de 1 a 3 rutas de imágenes a un lote específico.
     */
    fun asociarFotosALote(loteId: Long, fotos: List<String>) {
        if (fotos.isEmpty()) return
        val listaLimitada = fotos.take(3)
        mapaLoteFotos[loteId] = listaLimitada
        guardarEnPrefs()
    }

    /**
     * Obtiene las fotos de un lote (entre 1 y 3). Si el donante subió fotos, retorna sus fotos reales.
     * Si no tiene fotos registradas (como lotes iniciales o de prueba), provee fotografías
     * realistas de alta calidad contextuales a su ID, nombre o categoría.
     */
    fun obtenerFotosParaLote(
        loteId: Long,
        nombreAlimento: String? = null,
        categoria: String? = null
    ): List<String> {
        val guardadas = mapaLoteFotos[loteId]
        if (!guardadas.isNullOrEmpty()) {
            return guardadas
        }

        // Mapeo específico por ID de lote para garantizar consistencia fotográfica
        when (loteId) {
            12L -> return listOf(
                "https://images.unsplash.com/photo-1547592166-23ac45744acd?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1541832676-9b763b0239ab?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=800&q=80"
            )
            13L -> return listOf(
                "https://images.unsplash.com/photo-1619566636858-adf3ef46400b?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?auto=format&fit=crop&w=800&q=80"
            )
            14L -> return listOf(
                "https://images.unsplash.com/photo-1518843875459-f738682238a6?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1598170845058-32b9d6a5c731?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&w=800&q=80"
            )
            15L -> return listOf(
                "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1549931319-a545dcf3bc73?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1586444248902-2f64eddc13df?auto=format&fit=crop&w=800&q=80"
            )
            16L -> return listOf(
                "https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1528750997573-59b89d56f4f7?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1563636619-e9143da7973b?auto=format&fit=crop&w=800&q=80"
            )
            17L -> return listOf(
                "https://images.unsplash.com/photo-1584269600464-37b1b58a9fe7?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1534483509719-3feaee7c30da?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?auto=format&fit=crop&w=800&q=80"
            )
        }

        // Catálogo temático curado de respaldo para lotes sin fotos propias
        return generarFotosCuradasParaLote(nombreAlimento ?: "", categoria ?: "")
    }

    /**
     * Retorna la foto principal (primera foto) de un lote.
     */
    fun obtenerFotoPrincipal(
        loteId: Long,
        nombreAlimento: String? = null,
        categoria: String? = null
    ): String {
        return obtenerFotosParaLote(loteId, nombreAlimento, categoria).firstOrNull() ?: ""
    }

    /**
     * Genera entre 1 y 3 imágenes fotográficas de alta calidad para alimentos de prueba o iniciales.
     */
    private fun generarFotosCuradasParaLote(nombre: String, categoria: String): List<String> {
        val n = nombre.lowercase()
        val c = categoria.lowercase()

        return when {
            // Pan y panadería
            n.contains("pan") || c.contains("pan") -> listOf(
                "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1549931319-a545dcf3bc73?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1586444248902-2f64eddc13df?auto=format&fit=crop&w=800&q=80"
            )

            // Frutas, manzanas, plátanos
            n.contains("manzana") || n.contains("fruta") || n.contains("plátano") || n.contains("platano") || c.contains("fruta") -> listOf(
                "https://images.unsplash.com/photo-1619566636858-adf3ef46400b?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?auto=format&fit=crop&w=800&q=80"
            )

            // Verduras, hortalizas, papas
            n.contains("verdura") || n.contains("papa") || n.contains("zanahoria") || c.contains("verdura") -> listOf(
                "https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1518843875459-f738682238a6?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1598170845058-32b9d6a5c731?auto=format&fit=crop&w=800&q=80"
            )

            // Atún, conservas, enlatados, abarrotes
            n.contains("atun") || n.contains("atún") || n.contains("conserva") || n.contains("grano") || c.contains("grano") || c.contains("abarrote") -> listOf(
                "https://images.unsplash.com/photo-1584269600464-37b1b58a9fe7?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1534483509719-3feaee7c30da?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?auto=format&fit=crop&w=800&q=80"
            )

            // Sopas, comidas preparadas
            n.contains("sopa") || n.contains("comida") || c.contains("comida") || c.contains("preparada") -> listOf(
                "https://images.unsplash.com/photo-1547592166-23ac45744acd?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1541832676-9b763b0239ab?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=800&q=80"
            )

            // Lácteos, leche, yogur
            n.contains("leche") || n.contains("lacteo") || n.contains("lácteo") || n.contains("yogur") || c.contains("lacteo") || c.contains("lácteo") -> listOf(
                "https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1528750997573-59b89d56f4f7?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1563636619-e9143da7973b?auto=format&fit=crop&w=800&q=80"
            )

            // Proteínas, pollo, carne, huevos
            n.contains("carne") || n.contains("pollo") || n.contains("huevo") || c.contains("proteina") || c.contains("proteína") -> listOf(
                "https://images.unsplash.com/photo-1587593810167-a84920ea0781?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1516684732162-798a0062be99?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1582722872445-44dc5f7e3c8f?auto=format&fit=crop&w=800&q=80"
            )

            // Genérico alimentos saludables
            else -> listOf(
                "https://images.unsplash.com/photo-1488459716781-31db52582fe9?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1498837167922-ddd27525d352?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1490818387583-1baba5e638af?auto=format&fit=crop&w=800&q=80"
            )
        }
    }
}
