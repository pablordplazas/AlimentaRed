package com.example.alimentared.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alimentared.data.model.AsignacionResponse
import com.example.alimentared.data.model.ConfirmarEntregaRequest
import com.example.alimentared.data.model.ResolverSolicitudRequest
import com.example.alimentared.data.model.SolicitudResponse
import com.example.alimentared.data.remote.RetrofitClient
import com.example.alimentared.data.session.SessionManager
import com.example.alimentared.util.removerCodigosRegla
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SolicitudesUiState(
    val isLoading: Boolean = false,
    val solicitudesReceptora: List<SolicitudResponse> = emptyList(),
    val asignaciones: List<AsignacionResponse> = emptyList(),
    val conteoActivas: Long = 0,
    val mensajeExito: String? = null,
    val error: String? = null
)

class SolicitudesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SolicitudesUiState())
    val uiState: StateFlow<SolicitudesUiState> = _uiState.asStateFlow()

    private val rolUsuario: String
        get() = try { SessionManager.getInstance().getRol() } catch (e: Exception) { "RECEPTORA" }

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, mensajeExito = null)
            try {
                if (rolUsuario.equals("RECEPTORA", ignoreCase = true)) {
                    val solRes = RetrofitClient.instance.getMisSolicitudes()
                    val conteoRes = RetrofitClient.instance.getConteoActivas()
                    val asigRes = RetrofitClient.instance.getAsignaciones()

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        solicitudesReceptora = solRes.body()?.contenido ?: emptyList(),
                        conteoActivas = conteoRes.body()?.get("activas") ?: 0L,
                        asignaciones = asigRes.body()?.contenido ?: emptyList()
                    )
                } else {
                    // Donante o Coordinador
                    val asigRes = RetrofitClient.instance.getAsignaciones()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        asignaciones = asigRes.body()?.contenido ?: emptyList()
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al sincronizar solicitudes: ${e.localizedMessage}"
                )
            }
        }
    }

    fun resolverSolicitud(solicitudId: Long, aprobada: Boolean, motivo: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val res = RetrofitClient.instance.resolverSolicitud(
                    solicitudId,
                    ResolverSolicitudRequest(aprobada, motivo)
                )
                if (res.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        mensajeExito = if (aprobada) "¡Solicitud aprobada y asignación generada!" else "Solicitud rechazada."
                    )
                    cargarDatos()
                } else {
                    val err = parseErrorBody(res.code(), res.errorBody()?.string())
                    _uiState.value = _uiState.value.copy(isLoading = false, error = err)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }

    fun confirmarEntrega(asignacionId: Long, cantidadRetirada: Double, observaciones: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val res = RetrofitClient.instance.confirmarEntrega(
                    asignacionId,
                    ConfirmarEntregaRequest(cantidadRetirada, observaciones)
                )
                if (res.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        mensajeExito = "¡Entrega confirmada exitosamente!"
                    )
                    cargarDatos()
                } else {
                    val err = parseErrorBody(res.code(), res.errorBody()?.string())
                    _uiState.value = _uiState.value.copy(isLoading = false, error = err)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }

    fun reportarInasistencia(asignacionId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val res = RetrofitClient.instance.reportarNoRecogida(asignacionId)
                if (res.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        mensajeExito = "Asignación cancelada por inasistencia. Se aplicó 1 strike y se reabasteció el inventario."
                    )
                    cargarDatos()
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
                    return json.get("mensaje").asString.removerCodigosRegla()
                }
            } catch (ignored: Exception) {}
        }
        return "Error en la operación (Código HTTP: $code)"
    }
}
