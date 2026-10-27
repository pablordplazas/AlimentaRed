package com.example.alimentared.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alimentared.data.model.LoginRequest
import com.example.alimentared.data.model.RegisterRequest
import com.example.alimentared.data.model.UsuarioResponse
import com.example.alimentared.data.remote.RetrofitClient
import com.example.alimentared.data.session.SessionManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val user: UsuarioResponse? = null
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val sessionManager by lazy { SessionManager.getInstance() }

    init {
        val currentUser = sessionManager.getUsuario()
        if (currentUser != null && sessionManager.getToken() != null) {
            _uiState.value = AuthUiState(isSuccess = true, user = currentUser)
        }
    }

    fun login(correo: String, contrasena: String) {
        if (correo.isBlank() || contrasena.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Por favor ingrese correo y contraseña")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = RetrofitClient.instance.login(LoginRequest(correo.trim(), contrasena))
                if (response.isSuccessful && response.body() != null) {
                    val authData = response.body()!!
                    sessionManager.saveAuth(authData.token, authData.usuario)
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        isSuccess = true,
                        user = authData.usuario
                    )
                } else {
                    val errorMsg = parseErrorMessage(response.code(), response.errorBody()?.string())
                    _uiState.value = AuthUiState(isLoading = false, error = errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    isLoading = false,
                    error = "Error de conexión con el servidor (${e.localizedMessage ?: "Revise si el backend está activo"})."
                )
            }
        }
    }

    fun register(
        nit: String,
        orgNombre: String,
        correoOrg: String,
        telefono: String?,
        ubicacion: String?,
        nombreContacto: String,
        contrasena: String,
        rol: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val req = RegisterRequest(
                    nit = nit.trim(),
                    organizacionNombre = orgNombre.trim(),
                    correoOrganizacion = correoOrg.trim(),
                    telefono = telefono?.trim(),
                    ubicacion = ubicacion?.trim(),
                    nombreContacto = nombreContacto.trim(),
                    contrasena = contrasena,
                    rol = rol
                )
                val response = RetrofitClient.instance.register(req)
                if (response.isSuccessful && response.body() != null) {
                    val authData = response.body()!!
                    sessionManager.saveAuth(authData.token, authData.usuario)
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        isSuccess = true,
                        user = authData.usuario
                    )
                } else {
                    val errorMsg = parseErrorMessage(response.code(), response.errorBody()?.string())
                    _uiState.value = AuthUiState(isLoading = false, error = errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    isLoading = false,
                    error = "Error al conectar: ${e.localizedMessage}"
                )
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
        _uiState.value = AuthUiState()
    }

    fun limpiarError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun parseErrorMessage(code: Int, body: String?): String {
        if (code == 403) {
            return "Cuenta suspendida: tu organización acumula 3 strikes vigentes (RN08). Consulta el panel de apelaciones."
        }
        if (code == 401) {
            return "Credenciales inválidas. Verifica tu correo y contraseña."
        }
        if (!body.isNullOrBlank()) {
            try {
                val json = Gson().fromJson(body, JsonObject::class.java)
                if (json.has("mensaje")) {
                    return json.get("mensaje").asString
                }
            } catch (ignored: Exception) {}
        }
        return "Error en la solicitud (Código HTTP: $code)"
    }
}
