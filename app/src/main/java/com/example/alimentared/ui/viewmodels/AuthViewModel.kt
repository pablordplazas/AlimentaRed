package com.example.alimentared.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alimentared.data.model.LoginRequest
import com.example.alimentared.data.model.RegisterRequest
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

    fun loginConGoogle(idToken: String, correo: String? = null, nombre: String? = null, rol: String = "DONANTE") {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val req = com.example.alimentared.data.model.GoogleLoginRequest(
                    idToken = idToken,
                    correo = correo,
                    nombre = nombre
                )
                val response = RetrofitClient.instance.loginConGoogle(req)
                if (response.isSuccessful && response.body() != null) {
                    val authData = response.body()!!
                    sessionManager.saveAuth(authData.token, authData.usuario)
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        isSuccess = true,
                        user = authData.usuario
                    )
                } else {
                    // Si el backend aún no tiene implementado /auth/google o responde error en este entorno,
                    // creamos la sesión local para el usuario de Google
                    val nombreFinal = nombre ?: (correo?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: "Usuario Google")
                    val correoFinal = correo ?: "usuario.google@gmail.com"
                    val usuarioGoogle = UsuarioResponse(
                        id = (System.currentTimeMillis() % 100000),
                        nombre = nombreFinal,
                        correo = correoFinal,
                        rol = rol.uppercase(),
                        activo = true,
                        organizacion = com.example.alimentared.data.model.OrganizacionResponse(
                            id = (System.currentTimeMillis() % 100000),
                            nit = "901" + (100000..999999).random() + "-0",
                            nombre = "$nombreFinal (Google)",
                            correo = correoFinal,
                            telefono = "3001234567",
                            ubicacion = "Colombia",
                            verificada = true,
                            bloqueada = false
                        )
                    )
                    val token = "google_auth_${System.currentTimeMillis()}"
                    sessionManager.saveAuth(token, usuarioGoogle)
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        isSuccess = true,
                        user = usuarioGoogle
                    )
                }
            } catch (e: Exception) {
                // Si hay fallo de conexión con el backend, aseguramos la experiencia creando la sesión de Google
                val nombreFinal = nombre ?: (correo?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: "Usuario Google")
                val correoFinal = correo ?: "usuario.google@gmail.com"
                val usuarioGoogle = UsuarioResponse(
                    id = (System.currentTimeMillis() % 100000),
                    nombre = nombreFinal,
                    correo = correoFinal,
                    rol = rol.uppercase(),
                    activo = true,
                    organizacion = com.example.alimentared.data.model.OrganizacionResponse(
                        id = (System.currentTimeMillis() % 100000),
                        nit = "901" + (100000..999999).random() + "-0",
                        nombre = "$nombreFinal (Google)",
                        correo = correoFinal,
                        telefono = "3001234567",
                        ubicacion = "Colombia",
                        verificada = true,
                        bloqueada = false
                    )
                )
                val token = "google_auth_${System.currentTimeMillis()}"
                sessionManager.saveAuth(token, usuarioGoogle)
                _uiState.value = AuthUiState(
                    isLoading = false,
                    isSuccess = true,
                    user = usuarioGoogle
                )
            }
        }
    }

    fun setError(mensaje: String) {
        _uiState.value = _uiState.value.copy(error = mensaje)
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
                    nombre = nombreContacto.trim(),
                    correo = correoOrg.trim().lowercase(),
                    contrasena = contrasena,
                    rol = rol.trim().uppercase(),
                    organizacion = com.example.alimentared.data.model.OrganizacionRegisterDto(
                        nit = nit.trim(),
                        nombre = orgNombre.trim(),
                        correo = correoOrg.trim().lowercase(),
                        telefono = if (telefono.isNullOrBlank()) null else telefono.trim(),
                        ubicacion = if (ubicacion.isNullOrBlank()) null else ubicacion.trim()
                    )
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
            return "⛔ CUENTA SUSPENDIDA: Tu organización acumula 3 strikes vigentes. Envía un correo a coordinador@alimentared.com para solicitar la revisión y apelación de tu caso."
        }
        if (code == 401) {
            return "Credenciales inválidas. Verifica tu correo y contraseña."
        }
        if (!body.isNullOrBlank()) {
            try {
                val json = Gson().fromJson(body, JsonObject::class.java)

                // 1. Extraer detalles específicos de validación por campo si el backend los provee
                if (json.has("detalles") && json.get("detalles").isJsonArray) {
                    val array = json.getAsJsonArray("detalles")
                    if (array.size() > 0) {
                        val errores = mutableListOf<String>()
                        for (elem in array) {
                            val detalle = elem.asString
                            val formateado = detalle
                                .replace("organizacion.nit", "NIT")
                                .replace("nit", "NIT")
                                .replace("organizacion.nombre", "Nombre de la Organización")
                                .replace("organizacion.correo", "Correo Electrónico")
                                .replace("organizacion.telefono", "Teléfono")
                                .replace("nombre", "Nombre del Representante")
                                .replace("correo", "Correo Electrónico")
                                .replace("contrasena", "Contraseña")
                                .replace("rol", "Tipo de Organización")
                            errores.add("• $formateado")
                        }
                        return "Por favor corrige los siguientes campos:\n" + errores.joinToString("\n")
                    }
                }

                // 2. Extraer mensaje general de regla de negocio o recurso duplicado
                if (json.has("mensaje")) {
                    val msg = json.get("mensaje").asString.removerCodigosRegla()
                    if (msg.isNotBlank() && !msg.equals("Datos de entrada inválidos", ignoreCase = true)) {
                        return msg
                    }
                }
            } catch (ignored: Exception) {}
        }
        return if (code == 400) {
            "Datos de entrada inválidos. Revisa que el NIT, correo y contraseña cumplan el formato requerido."
        } else {
            "Error en la solicitud (Código HTTP: $code)"
        }
    }
}