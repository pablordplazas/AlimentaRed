package com.example.alimentared.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alimentared.data.model.LoteRequest
import com.example.alimentared.data.model.TipoAlimentoResponse
import com.example.alimentared.data.remote.RetrofitClient
import com.example.alimentared.util.VencimientoUtils
import com.example.alimentared.util.removerCodigosRegla
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PublishUiState(
    val isLoading: Boolean = false,
    val categorias: List<TipoAlimentoResponse> = emptyList(),
    val publicadoExitoso: Boolean = false,
    val error: String? = null
)

class PublishLoteViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PublishUiState())
    val uiState: StateFlow<PublishUiState> = _uiState.asStateFlow()

    init {
        cargarCategorias()
    }

    fun cargarCategorias() {
        viewModelScope.launch {
            try {
                val res = RetrofitClient.instance.getCategorias()
                if (res.isSuccessful && res.body() != null) {
                    _uiState.value = _uiState.value.copy(categorias = res.body()!!)
                }
            } catch (ignored: Exception) {}
        }
    }

    fun publicarLote(
        nombre: String,
        descripcion: String,
        categoriaId: Long,
        cantidad: Double,
        unidadMinima: Double,
        multiplo: Double,
        estadoAlimento: String,
        fechaCaducidad: String,
        ubicacion: String?,
        instrucciones: String?,
        contacto: String?,
        fotos: List<String> = emptyList()
    ) {
        if (nombre.isBlank() || descripcion.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "El nombre y la descripción son obligatorios.")
            return
        }
        if (fotos.isEmpty()) {
            _uiState.value = _uiState.value.copy(error = "Debes subir al menos 1 fotografía del lote (mínimo 1, máximo 3) para garantizar la visibilidad de la donación.")
            return
        }
        if (fotos.size > 3) {
            _uiState.value = _uiState.value.copy(error = "Solo se permite subir hasta un máximo de 3 fotografías por lote.")
            return
        }
        if (cantidad <= 0 || unidadMinima <= 0 || multiplo <= 0) {
            _uiState.value = _uiState.value.copy(error = "Las cantidades deben ser valores mayores a cero.")
            return
        }
        if (unidadMinima > cantidad) {
            _uiState.value = _uiState.value.copy(error = "La porción mínima no puede exceder el stock total.")
            return
        }

        val errorFecha = VencimientoUtils.validarFechaCaducidad(fechaCaducidad)
        if (errorFecha != null) {
            _uiState.value = _uiState.value.copy(error = errorFecha)
            return
        }

        val fechaCaducidadIso = VencimientoUtils.formatearParaEnvioBackend(fechaCaducidad)
            ?: run {
                _uiState.value = _uiState.value.copy(error = "Formato de fecha de vencimiento no válido.")
                return
            }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val request = LoteRequest(
                    nombre = nombre.trim(),
                    descripcion = descripcion.trim(),
                    categoriaId = categoriaId,
                    cantidad = cantidad,
                    unidadMinima = unidadMinima,
                    multiplo = multiplo,
                    estadoAlimento = estadoAlimento,
                    fechaCaducidad = fechaCaducidadIso,
                    ubicacion = ubicacion?.trim(),
                    instruccionesRecogida = instrucciones?.trim(),
                    contacto = contacto?.trim(),
                    fotos = fotos
                )

                val res = RetrofitClient.instance.crearLote(request)
                if (res.isSuccessful && res.body() != null) {
                    val loteCreado = res.body()!!
                    com.example.alimentared.data.lotes.LoteImagenesManager.asociarFotosALote(loteCreado.id, fotos)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        publicadoExitoso = true
                    )
                } else {
                    val msg = parseErrorBody(res.code(), res.errorBody()?.string())
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = msg
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Fallo de conexión: ${e.localizedMessage}"
                )
            }
        }
    }

    private fun parseErrorBody(code: Int, body: String?): String {
        if (!body.isNullOrBlank()) {
            try {
                val json = Gson().fromJson(body, JsonObject::class.java)
                if (json.has("mensaje")) {
                    return json.get("mensaje").asString.removerCodigosRegla()
                }
            } catch (ignored: Exception) {}
        }
        return "Error al publicar lote (Código HTTP: $code)"
    }
}
