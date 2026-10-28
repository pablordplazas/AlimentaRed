package com.example.alimentared.data.noticias

import android.content.Context
import android.content.SharedPreferences
import com.example.alimentared.data.model.AsignacionResponse
import com.example.alimentared.data.model.LoteResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class NoticiaImpacto(
    val id: Long,
    val titulo: String,
    val entidad: String,
    val fecha: String,
    val resumen: String,
    val categoria: String,
    val esDestacada: Boolean = false,
    val referenciaId: Long? = null,
    val tipoReferencia: String? = null // "LOTE" o "ENTREGA"
)

class NoticiasManager private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    private val defaultNoticias = listOf(
        NoticiaImpacto(
            id = 1L,
            titulo = "¡Éxito en Jornada de Rescate!",
            entidad = "Panadería Central ➔ Comedor Esperanza",
            fecha = "Hace 2 horas",
            resumen = "Se entregaron 120 kg de pan integral a la fundación Comedor Esperanza en Chía.",
            categoria = "RESCATE DESTACADO",
            esDestacada = true,
            referenciaId = 101L,
            tipoReferencia = "ENTREGA"
        ),
        NoticiaImpacto(
            id = 2L,
            titulo = "Campaña de Emergencia Activa",
            entidad = "Red Comunitaria",
            fecha = "Hoy",
            resumen = "Prioridad en la recolección de granos no perecederos y agua embotellada.",
            categoria = "ALERTA SOCIAL",
            esDestacada = false
        ),
        NoticiaImpacto(
            id = 3L,
            titulo = "Supermercado Éxito se suma a la Red",
            entidad = "Grupo Éxito",
            fecha = "Ayer",
            resumen = "Nuevos puntos de recolección de lácteos y abarrotes habilitados en la sabana.",
            categoria = "NUEVO DONANTE",
            esDestacada = false
        )
    )

    private val _noticiasFlow = MutableStateFlow<List<NoticiaImpacto>>(cargarNoticias())
    val noticiasFlow: StateFlow<List<NoticiaImpacto>> = _noticiasFlow.asStateFlow()

    private fun cargarNoticias(): List<NoticiaImpacto> {
        val json = prefs.getString(KEY_NOTICIAS, null) ?: return defaultNoticias
        return try {
            val type = object : TypeToken<List<NoticiaImpacto>>() {}.type
            val list: List<NoticiaImpacto>? = gson.fromJson(json, type)
            if (!list.isNullOrEmpty()) list else defaultNoticias
        } catch (e: Exception) {
            defaultNoticias
        }
    }

    private fun guardarNoticias(lista: List<NoticiaImpacto>) {
        _noticiasFlow.value = lista
        prefs.edit().putString(KEY_NOTICIAS, gson.toJson(lista)).apply()
    }

    fun esLoteDestacado(loteId: Long): Boolean {
        return _noticiasFlow.value.any { it.tipoReferencia == "LOTE" && it.referenciaId == loteId }
    }

    fun esEntregaDestacada(asignacionId: Long): Boolean {
        return _noticiasFlow.value.any { it.tipoReferencia == "ENTREGA" && it.referenciaId == asignacionId }
    }

    fun toggleDestacarLote(lote: LoteResponse): Boolean {
        val actual = _noticiasFlow.value.toMutableList()
        val index = actual.indexOfFirst { it.tipoReferencia == "LOTE" && it.referenciaId == lote.id }
        return if (index >= 0) {
            actual.removeAt(index)
            guardarNoticias(actual)
            false // Removido
        } else {
            val nueva = NoticiaImpacto(
                id = System.currentTimeMillis(),
                titulo = "¡Donación Disponible: ${lote.nombre}!",
                entidad = lote.donanteNombre ?: "Comercio Donante",
                fecha = "Hoy",
                resumen = "Se encuentran disponibles ${lote.cantidadDisponible} ${lote.unidadMedida ?: "KG"} de ${lote.nombre} en ${lote.ubicacion ?: "punto de entrega"}. ¡Solicítalo antes de su vencimiento!",
                categoria = "DONACIÓN DESTACADA",
                esDestacada = true,
                referenciaId = lote.id,
                tipoReferencia = "LOTE"
            )
            actual.add(0, nueva)
            guardarNoticias(actual)
            true // Agregado
        }
    }

    fun toggleDestacarEntrega(asignacion: AsignacionResponse): Boolean {
        val actual = _noticiasFlow.value.toMutableList()
        val index = actual.indexOfFirst { it.tipoReferencia == "ENTREGA" && it.referenciaId == asignacion.id }
        return if (index >= 0) {
            actual.removeAt(index)
            guardarNoticias(actual)
            false // Removido
        } else {
            val cant = if (asignacion.cantidadRetirada > 0) asignacion.cantidadRetirada else asignacion.cantidadAsignada
            val nueva = NoticiaImpacto(
                id = System.currentTimeMillis(),
                titulo = "¡Éxito en Jornada de Rescate!",
                entidad = "${asignacion.donanteNombre ?: "Donante"} ➔ ${asignacion.receptoraNombre ?: "Fundación"}",
                fecha = "Hoy",
                resumen = "Se completó la entrega de $cant Kg de ${asignacion.loteNombre ?: "alimentos"} a la fundación ${asignacion.receptoraNombre ?: "receptora"}.",
                categoria = "RESCATE DESTACADO",
                esDestacada = true,
                referenciaId = asignacion.id,
                tipoReferencia = "ENTREGA"
            )
            actual.add(0, nueva)
            guardarNoticias(actual)
            true // Agregado
        }
    }

    fun eliminarNoticia(id: Long) {
        val actual = _noticiasFlow.value.toMutableList()
        actual.removeAll { it.id == id }
        guardarNoticias(actual)
    }

    companion object {
        private const val PREFS_NAME = "alimentared_noticias_prefs"
        private const val KEY_NOTICIAS = "key_noticias_json"

        @Volatile
        private var instance: NoticiasManager? = null

        fun initialize(context: Context): NoticiasManager {
            return instance ?: synchronized(this) {
                instance ?: NoticiasManager(context).also { instance = it }
            }
        }

        fun getInstance(): NoticiasManager {
            return instance ?: throw IllegalStateException("NoticiasManager no está inicializado. Llama a initialize() primero.")
        }
    }
}
