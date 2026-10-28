package com.example.alimentared.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alimentared.data.model.ApelacionStrikeRequest
import com.example.alimentared.data.model.ReporteImpactoResponse
import com.example.alimentared.data.model.StrikeResponse
import com.example.alimentared.data.model.UsuarioResponse
import com.example.alimentared.data.remote.RetrofitClient
import com.example.alimentared.data.session.SessionManager
import com.example.alimentared.util.removerCodigosRegla
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PerfilUiState(
    val isLoading: Boolean = false,
    val usuario: UsuarioResponse? = null,
    val impacto: ReporteImpactoResponse? = null,
    val strikes: List<StrikeResponse> = emptyList(),
    val strikesVigentes: Int = 0,
    val estaBloqueada: Boolean = false,
    val mensajeExito: String? = null,
    val error: String? = null
)

class PerfilImpactoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    private val sessionManager by lazy { SessionManager.getInstance() }

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        val user = sessionManager.getUsuario()
        _uiState.value = _uiState.value.copy(
            usuario = user,
            strikesVigentes = user?.organizacion?.strikesVigentes ?: 0,
            estaBloqueada = user?.organizacion?.bloqueada ?: false
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // 1. Cargar perfil actualizado
                val perfilRes = RetrofitClient.instance.getPerfil()
                if (perfilRes.isSuccessful && perfilRes.body() != null) {
                    val actual = perfilRes.body()!!
                    _uiState.value = _uiState.value.copy(
                        usuario = actual,
                        strikesVigentes = actual.organizacion?.strikesVigentes ?: 0,
                        estaBloqueada = actual.organizacion?.bloqueada ?: false
                    )
                }

                // 2. Cargar KPIs de impacto
                val impactoRes = RetrofitClient.instance.getReporteImpacto()
                if (impactoRes.isSuccessful && impactoRes.body() != null) {
                    _uiState.value = _uiState.value.copy(impacto = impactoRes.body()!!)
                }

                // 3. Cargar strikes de la organización únicamente si es Donante o Receptora
                val rol = _uiState.value.usuario?.rol?.uppercase() ?: ""
                val esDonanteOReceptora = rol.contains("DONANTE") || rol.contains("RECEPTOR")
                val orgId = _uiState.value.usuario?.organizacion?.id

                if (esDonanteOReceptora && orgId != null) {
                    val strikesRes = RetrofitClient.instance.getStrikesPorOrganizacion(orgId)
                    if (strikesRes.isSuccessful && strikesRes.body() != null) {
                        val lista = strikesRes.body()!!
                        _uiState.value = _uiState.value.copy(
                            strikes = lista,
                            strikesVigentes = lista.count { it.vigente }
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        strikes = emptyList(),
                        strikesVigentes = 0
                    )
                }

                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al sincronizar métricas: ${e.localizedMessage}"
                )
            }
        }
    }

    fun apelarStrike(strikeId: Long, justificacion: String) {
        val orgId = _uiState.value.usuario?.organizacion?.id ?: return
        if (justificacion.length < 10) {
            _uiState.value = _uiState.value.copy(error = "La justificación debe tener al menos 10 caracteres.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val res = RetrofitClient.instance.apelarStrike(
                    orgId,
                    ApelacionStrikeRequest(strikeId, justificacion.trim())
                )
                if (res.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        mensajeExito = "¡Apelación radicada exitosamente! Será evaluada por la coordinación."
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
