package com.example.alimentared.data.usuarios

import android.content.Context
import android.content.SharedPreferences
import com.example.alimentared.data.model.OrganizacionResponse
import com.example.alimentared.data.model.UsuarioResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UsuarioInternoItem(
    val id: Long,
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val rol: String, // "ADMINISTRADOR" o "COORDINADOR"
    val esPredefinido: Boolean = false,
    val fechaCreacion: String = ""
)

object UsuariosInternosManager {

    private const val PREFS_NAME = "alimentared_usuarios_internos_prefs"
    private const val KEY_USUARIOS_INTERNOS = "usuarios_internos_creados"

    private var prefs: SharedPreferences? = null
    private val gson = Gson()

    private val usuariosPredefinidos = listOf(
        UsuarioInternoItem(
            id = 1L,
            nombre = "Administrador AlimentaRed",
            correo = "admin@alimentared.com",
            contrasena = "Admin12345",
            rol = "ADMINISTRADOR",
            esPredefinido = true,
            fechaCreacion = "Perfil Inicial del Sistema"
        ),
        UsuarioInternoItem(
            id = 2L,
            nombre = "Coordinador Red Comunitaria",
            correo = "coordinador@alimentared.com",
            contrasena = "Coord12345",
            rol = "COORDINADOR",
            esPredefinido = true,
            fechaCreacion = "Perfil Inicial del Sistema"
        )
    )

    private val usuariosCreados = mutableListOf<UsuarioInternoItem>()

    fun initialize(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            cargarDesdePrefs()
        }
    }

    private fun cargarDesdePrefs() {
        val json = prefs?.getString(KEY_USUARIOS_INTERNOS, null) ?: return
        try {
            val type = object : TypeToken<List<UsuarioInternoItem>>() {}.type
            val guardados: List<UsuarioInternoItem>? = gson.fromJson(json, type)
            if (guardados != null) {
                usuariosCreados.clear()
                usuariosCreados.addAll(guardados)
            }
        } catch (e: Exception) {
            usuariosCreados.clear()
        }
    }

    private fun guardarEnPrefs() {
        val json = gson.toJson(usuariosCreados)
        prefs?.edit()?.putString(KEY_USUARIOS_INTERNOS, json)?.apply()
    }

    @Synchronized
    fun obtenerTodos(): List<UsuarioInternoItem> {
        val todos = mutableListOf<UsuarioInternoItem>()
        todos.addAll(usuariosCreados)
        usuariosPredefinidos.forEach { predef ->
            if (todos.none { it.correo.equals(predef.correo, ignoreCase = true) }) {
                todos.add(predef)
            }
        }
        return todos
    }

    @Synchronized
    fun obtenerUsuariosCreados(): List<UsuarioInternoItem> {
        return usuariosCreados.toList()
    }

    @Synchronized
    fun registrarUsuario(
        nombre: String,
        correo: String,
        contrasena: String,
        rol: String
    ): UsuarioInternoItem {
        val correoNorm = correo.trim().lowercase(Locale.ROOT)
        val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val nuevo = UsuarioInternoItem(
            id = (System.currentTimeMillis() % 100000) + 100L,
            nombre = nombre.trim(),
            correo = correoNorm,
            contrasena = contrasena,
            rol = rol.uppercase(Locale.ROOT),
            esPredefinido = false,
            fechaCreacion = fechaActual
        )

        val existenteIndex = usuariosCreados.indexOfFirst { it.correo.equals(correoNorm, ignoreCase = true) }
        if (existenteIndex >= 0) {
            usuariosCreados[existenteIndex] = nuevo
        } else {
            usuariosCreados.add(0, nuevo)
        }
        guardarEnPrefs()
        return nuevo
    }

    fun buscarPorCorreo(correo: String): UsuarioInternoItem? {
        val correoNorm = correo.trim().lowercase(Locale.ROOT)
        return obtenerTodos().find { it.correo.equals(correoNorm, ignoreCase = true) }
    }

    fun existeCorreo(correo: String): Boolean {
        val correoNorm = correo.trim().lowercase(Locale.ROOT)
        return obtenerTodos().any { it.correo.equals(correoNorm, ignoreCase = true) }
    }

    @Synchronized
    fun actualizarContrasena(correo: String, nuevaContrasena: String): Boolean {
        val correoNorm = correo.trim().lowercase(Locale.ROOT)
        val existenteIndex = usuariosCreados.indexOfFirst { it.correo.equals(correoNorm, ignoreCase = true) }
        if (existenteIndex >= 0) {
            val actual = usuariosCreados[existenteIndex]
            usuariosCreados[existenteIndex] = actual.copy(contrasena = nuevaContrasena)
            guardarEnPrefs()
            return true
        }
        val predef = usuariosPredefinidos.find { it.correo.equals(correoNorm, ignoreCase = true) }
        if (predef != null) {
            val override = predef.copy(contrasena = nuevaContrasena)
            usuariosCreados.add(0, override)
            guardarEnPrefs()
            return true
        }
        return false
    }

    fun toUsuarioResponse(item: UsuarioInternoItem): UsuarioResponse {
        return UsuarioResponse(
            id = item.id,
            nombre = item.nombre,
            correo = item.correo,
            rol = item.rol,
            activo = true,
            fechaRegistro = item.fechaCreacion,
            organizacion = OrganizacionResponse(
                id = item.id,
                nit = "900000000-1",
                nombre = if (item.rol.contains("ADMIN")) "AlimentaRed Central (Admin)" else "AlimentaRed Operaciones",
                correo = item.correo,
                telefono = "3000000000",
                ubicacion = "Bogotá, Colombia",
                verificada = true,
                bloqueada = false
            )
        )
    }
}
