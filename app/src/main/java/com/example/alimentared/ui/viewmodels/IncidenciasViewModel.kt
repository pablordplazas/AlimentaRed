package com.example.alimentared.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alimentared.data.model.IncidenciaRequest
import com.example.alimentared.data.model.IncidenciaResponse
import com.example.alimentared.data.model.ResolverIncidenciaRequest
import com.example.alimentared.data.remote.RetrofitClient
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class IncidenciasUiState(
    val isLoading: Boolean = false,
    val incidencias: List<IncidenciaResponse> = emptyList(),
    val reporteExitoso: Boolean = false,
    val mensaje: String? = null,
    val error: String? = null
)

class IncidenciasViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(IncidenciasUiState())
    val uiState: StateFlow<IncidenciasUiState> = _uiState.asStateFlow()

    init {
        cargarIncidencias()
    }

    fun cargarIncidencias() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val res = RetrofitClient.instance.getIncidencias()
                if (res.isSuccessful && res.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        incidencias = res.body()!!.contenido
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No se pudieron obtener las incidencias (${res.code()})"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Fallo de red: ${e.localizedMessage}"
                )
            }
        }
    }

    fun reportarIncidencia(loteId: Long, detalles: String) {
        if (detalles.length < 10) {
            _uiState.value = _uiState.value.copy(error = "El detalle de la incidencia debe tener al menos 10 caracteres.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val res = RetrofitClient.instance.reportarIncidencia(IncidenciaRequest(loteId, detalles.trim()))
                if (res.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        reporteExitoso = true,
                        mensaje = "Incidencia reportada. El lote fue bloqueado preventivamente (RF08)."
                    )
                    cargarIncidencias()
                } else {
                    val err = parseErrorBody(res.code(), res.errorBody()?.string())
                    _uiState.value = _uiState.value.copy(isLoading = false, error = err)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }

    fun resolverIncidencia(incidenciaId: Long, reactivar: Boolean, resolucion: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val res = RetrofitClient.instance.resolverIncidencia(
                    incidenciaId,
                    ResolverIncidenciaRequest(reactivar, resolucion.trim())
                )
                if (res.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        mensaje = if (reactivar) "Incidencia resuelta: Lote reactivado para donación." else "Incidencia resuelta: Lote cancelado definitivamente."
                    )
                    cargarIncidencias()
                } else {
                    val err = parseErrorBody(res.code(), res.errorBody()?.string())
                    _uiState.value = _uiState.value.copy(isLoading = false, error = err)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }

    private fun parseErrorBody(code: Int, body: String?): String {
        if (!body.isNullOrBlank()) {
            try {
                val json = Gson().fromJson(body, JsonObject::class.java)
                if (json.has("mensaje")) {
                    return json.get("mensaje").asString
                }
            } catch (ignored: Exception) {}
        }
        return "Error en la operación (Código HTTP: $code)"
    }
}
