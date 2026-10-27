package com.example.alimentared.data.session

import android.content.Context
import android.content.SharedPreferences
import com.example.alimentared.data.model.UsuarioResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager private constructor(context: Context) {

    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )
    private val gson = Gson()

    private val _isLoggedInFlow = MutableStateFlow(!getToken().isNullOrBlank())
    val isLoggedInFlow: StateFlow<Boolean> = _isLoggedInFlow.asStateFlow()

    private val _userFlow = MutableStateFlow(getUsuario())
    val userFlow: StateFlow<UsuarioResponse?> = _userFlow.asStateFlow()

    fun saveAuth(token: String, usuario: UsuarioResponse) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER_JSON, gson.toJson(usuario))
            .putString(KEY_ROLE, usuario.rol)
            .apply()

        _isLoggedInFlow.value = true
        _userFlow.value = usuario
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun getUsuario(): UsuarioResponse? {
        val json = prefs.getString(KEY_USER_JSON, null) ?: return null
        return try {
            gson.fromJson(json, UsuarioResponse::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun getRol(): String {
        return prefs.getString(KEY_ROLE, "DONANTE") ?: "DONANTE"
    }

    fun getServerUrl(): String {
        return prefs.getString(KEY_SERVER_URL, DEFAULT_SERVER_URL) ?: DEFAULT_SERVER_URL
    }

    fun setServerUrl(url: String) {
        var formatted = url.trim()
        if (!formatted.startsWith("http://") && !formatted.startsWith("https://")) {
            formatted = "http://$formatted"
        }
        if (!formatted.endsWith("/")) {
            formatted = "$formatted/"
        }
        if (!formatted.endsWith("api/")) {
            formatted = "${formatted}api/"
        }
        prefs.edit().putString(KEY_SERVER_URL, formatted).apply()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
        _isLoggedInFlow.value = false
        _userFlow.value = null
    }

    companion object {
        private const val PREFS_NAME = "alimentared_session_prefs"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_JSON = "user_json"
        private const val KEY_ROLE = "user_role"
        private const val KEY_SERVER_URL = "server_url"
        const val DEFAULT_SERVER_URL = "http://192.168.1.18:8080/api/"

        @Volatile
        private var INSTANCE: SessionManager? = null

        fun initialize(context: Context): SessionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SessionManager(context.applicationContext).also { INSTANCE = it }
            }
        }

        fun getInstance(): SessionManager {
            return INSTANCE ?: error("SessionManager debe ser inicializado en Application o MainActivity")
        }
    }
}
