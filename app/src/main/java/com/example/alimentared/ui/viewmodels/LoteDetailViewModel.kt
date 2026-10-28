package com.example.alimentared.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alimentared.data.model.LoteResponse
import com.example.alimentared.data.model.SolicitudRequest
import com.example.alimentared.data.remote.RetrofitClient
import com.example.alimentared.util.removerCodigosRegla
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoteDetailUiState(
    val isLoading: Boolean = false,
    val lote: LoteResponse? = null,
    val cantidadInput: String = "",
    val cantidadCalculada: Double = 0.0,
    val porcionesEstimadas: Int = 0,
    val remanenteLote: Double = 0.0,
    val esValido: Boolean = false,
    val mensajeValidacion: String? = null,
    val isSubmitting: Boolean = false,
    val solicitudExitosa: Boolean = false,
    val errorOperacion: String? = null
)

class LoteDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoteDetailUiState())
    val uiState: StateFlow<LoteDetailUiState> = _uiState.asStateFlow()

    fun cargarDetalle(loteId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorOperacion = null)
            try {
                val res = RetrofitClient.instance.getLotePorId(loteId)
                if (res.isSuccessful && res.body() != null) {
                    val lote = res.body()!!
                    val inicial = lote.unidadMinima.toString()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        lote = lote,
                        cantidadInput = inicial
                    )
                    validarCantidad(inicial, lote)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorOperacion = "No se pudo cargar el detalle del lote (${res.code()})"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorOperacion = "Error de conexión: ${e.localizedMessage}"
                )
            }
        }
    }

    fun onCantidadCambiada(input: String) {
        val lote = _uiState.value.lote ?: return
        _uiState.value = _uiState.value.copy(cantidadInput = input)
        validarCantidad(input, lote)
    }

    private fun validarCantidad(input: String, lote: LoteResponse) {
        val cant = input.toDoubleOrNull()
        if (cant == null || cant <= 0.0) {
            _uiState.value = _uiState.value.copy(
                esValido = false,
                mensajeValidacion = "Ingrese una cantidad válida mayor a cero.",
                remanenteLote = lote.cantidadDisponible
            )
            return
        }

        val remanente = lote.cantidadDisponible - cant
        val porciones = (cant / lote.multiplo).toInt().coerceAtLeast(1)

        // RN02: No superar disponible
        if (cant > lote.cantidadDisponible) {
            _uiState.value = _uiState.value.copy(
                esValido = false,
                mensajeValidacion = "La cantidad solicitada supera el stock disponible (${lote.cantidadDisponible} ${lote.unidadMedida}).",
                cantidadCalculada = cant,
                remanenteLote = remanente,
                porcionesEstimadas = porciones
            )
            return
        }

        // RN05: Mínimo unidad mínima y múltiplo
        if (cant < lote.unidadMinima) {
            _uiState.value = _uiState.value.copy(
                esValido = false,
                mensajeValidacion = "La cantidad mínima a solicitar es de ${lote.unidadMinima} ${lote.unidadMedida}.",
                cantidadCalculada = cant,
                remanenteLote = remanente,
                porcionesEstimadas = porciones
            )
            return
        }

        val delta = cant - lote.unidadMinima
        val esMultiplo = (delta % lote.multiplo == 0.0) || Math.abs(delta % lote.multiplo) < 0.001
        if (!esMultiplo) {
            _uiState.value = _uiState.value.copy(
                esValido = false,
                mensajeValidacion = "Debe solicitar la unidad mínima más múltiplos de ${lote.multiplo} ${lote.unidadMedida}.",
                cantidadCalculada = cant,
                remanenteLote = remanente,
                porcionesEstimadas = porciones
            )
            return
        }

        // RN10: Remanente útil (o se lleva todo o deja al menos la unidad mínima)
        val esRemanenteValido = (remanente == 0.0 || Math.abs(remanente) < 0.001) || remanente >= lote.unidadMinima
        if (!esRemanenteValido) {
            _uiState.value = _uiState.value.copy(
                esValido = false,
                mensajeValidacion = "El remanente que quedaría ($remanente ${lote.unidadMedida}) es menor a la unidad mínima (${lote.unidadMinima}). Ajuste su pedido o tome el lote completo.",
                cantidadCalculada = cant,
                remanenteLote = remanente,
                porcionesEstimadas = porciones
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            esValido = true,
            mensajeValidacion = null,
            cantidadCalculada = cant,
            remanenteLote = remanente,
            porcionesEstimadas = porciones
        )
    }

    fun enviarSolicitud() {
        val lote = _uiState.value.lote ?: return
        val cant = _uiState.value.cantidadCalculada
        if (!_uiState.value.esValido) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, errorOperacion = null)
            try {
                val res = RetrofitClient.instance.crearSolicitud(SolicitudRequest(lote.id, cant))
                if (res.isSuccessful && res.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        solicitudExitosa = true
                    )
                } else {
                    val errorMsg = parseErrorBody(res.code(), res.errorBody()?.string())
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        errorOperacion = errorMsg
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorOperacion = "Error al conectar: ${e.localizedMessage}"
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
        return "No fue posible procesar la solicitud (Código HTTP: $code)"
    }
}
