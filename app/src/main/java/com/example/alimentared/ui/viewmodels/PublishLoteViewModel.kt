package com.example.alimentared.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alimentared.data.model.LoteRequest
import com.example.alimentared.data.model.TipoAlimentoResponse
import com.example.alimentared.data.remote.RetrofitClient
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
        horasVigencia: Int,
        ubicacion: String?,
        instrucciones: String?,
        contacto: String?
    ) {
        if (nombre.isBlank() || descripcion.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "El nombre y la descripción son obligatorios.")
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

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // Calcular fecha de caducidad
                val expiraMillis = System.currentTimeMillis() + (horasVigencia.toLong() * 3600 * 1000)
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val fechaCaducidadIso = sdf.format(Date(expiraMillis))

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
                    contacto = contacto?.trim()
                )

                val res = RetrofitClient.instance.crearLote(request)
                if (res.isSuccessful && res.body() != null) {
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
