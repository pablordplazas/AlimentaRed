package com.example.alimentared

import com.example.alimentared.data.usuarios.UsuariosInternosManager
import com.example.alimentared.ui.viewmodels.CrearUsuarioInternoUiState
import org.junit.Assert.*
import org.junit.Test

class CrearUsuarioInternoTest {

    @Test
    fun `validaciones de nombre funcionan correctamente`() {
        val estadoVacio = CrearUsuarioInternoUiState(nombre = "")
        assertFalse(estadoVacio.esNombreValido)

        val estadoEspacios = CrearUsuarioInternoUiState(nombre = "   ")
        assertFalse(estadoEspacios.esNombreValido)

        val estadoValido = CrearUsuarioInternoUiState(nombre = "Carlos Alberto Mendoza")
        assertTrue(estadoValido.esNombreValido)

        val estadoDemasiadoLargo = CrearUsuarioInternoUiState(nombre = "A".repeat(121))
        assertFalse(estadoDemasiadoLargo.esNombreValido)
    }

    @Test
    fun `validaciones de correo institucional funcionan correctamente`() {
        val correoInvalido1 = CrearUsuarioInternoUiState(correo = "carlos")
        assertFalse(correoInvalido1.esCorreoValido)

        val correoInvalido2 = CrearUsuarioInternoUiState(correo = "carlos@")
        assertFalse(correoInvalido2.esCorreoValido)

        val correoInvalido3 = CrearUsuarioInternoUiState(correo = "carlos@alimentared")
        assertFalse(correoInvalido3.esCorreoValido)

        val correoValido = CrearUsuarioInternoUiState(correo = "carlos.mendoza@alimentared.com")
        assertTrue(correoValido.esCorreoValido)
    }

    @Test
    fun `validaciones de contrasena alfanumerica y longitud funcionan`() {
        // Longitud menor a 8
        val passCorta = CrearUsuarioInternoUiState(contrasena = "Pass1")
        assertFalse(passCorta.esContrasenaLongitudValida)

        // Sin numeros
        val passSinNumeros = CrearUsuarioInternoUiState(contrasena = "ContrasenaSolamente")
        assertTrue(passSinNumeros.esContrasenaLongitudValida)
        assertFalse(passSinNumeros.esContrasenaComplejidadValida)

        // Sin letras
        val passSinLetras = CrearUsuarioInternoUiState(contrasena = "1234567890")
        assertTrue(passSinLetras.esContrasenaLongitudValida)
        assertFalse(passSinLetras.esContrasenaComplejidadValida)

        // Valida con letras y numeros >= 8 caracteres
        val passValida = CrearUsuarioInternoUiState(contrasena = "Admin2026Secure")
        assertTrue(passValida.esContrasenaLongitudValida)
        assertTrue(passValida.esContrasenaComplejidadValida)
    }

    @Test
    fun `validacion de confirmacion de contrasena y formulario completo`() {
        val formularioIncompleto = CrearUsuarioInternoUiState(
            nombre = "Laura Morales",
            correo = "laura.morales@alimentared.com",
            contrasena = "Coord12345",
            confirmarContrasena = "OtraCosa12345",
            rol = "COORDINADOR"
        )
        assertFalse(formularioIncompleto.coincidenContrasenas)
        assertFalse(formularioIncompleto.esFormularioValido)

        val formularioValidoCoordinador = formularioIncompleto.copy(
            confirmarContrasena = "Coord12345"
        )
        assertTrue(formularioValidoCoordinador.coincidenContrasenas)
        assertTrue(formularioValidoCoordinador.esFormularioValido)

        val formularioValidoAdmin = formularioValidoCoordinador.copy(
            rol = "ADMINISTRADOR"
        )
        assertTrue(formularioValidoAdmin.esFormularioValido)

        val rolInvalido = formularioValidoAdmin.copy(
            rol = "DONANTE" // No permitido como usuario interno
        )
        assertFalse(rolInvalido.esFormularioValido)
    }

    @Test
    fun `usuarios predefinidos existen y se pueden consultar en UsuariosInternosManager`() {
        val todos = UsuariosInternosManager.obtenerTodos()
        assertTrue("Debe contener al menos los 2 usuarios predefinidos", todos.size >= 2)

        val admin = UsuariosInternosManager.buscarPorCorreo("admin@alimentared.com")
        assertNotNull(admin)
        assertEquals("ADMINISTRADOR", admin?.rol)
        assertTrue(admin?.esPredefinido == true)

        val coord = UsuariosInternosManager.buscarPorCorreo("coordinador@alimentared.com")
        assertNotNull(coord)
        assertEquals("COORDINADOR", coord?.rol)
        assertTrue(coord?.esPredefinido == true)
    }

    @Test
    fun `registrar nuevo usuario interno en manager actualiza lista y genera response correcto`() {
        val nuevo = UsuariosInternosManager.registrarUsuario(
            nombre = "Dra. Patricia Gómez",
            correo = "patricia.gomez@alimentared.com",
            contrasena = "PatriAdmin123",
            rol = "ADMINISTRADOR"
        )

        assertEquals("patricia.gomez@alimentared.com", nuevo.correo)
        assertEquals("ADMINISTRADOR", nuevo.rol)
        assertFalse(nuevo.esPredefinido)

        val encontrado = UsuariosInternosManager.buscarPorCorreo("patricia.gomez@alimentared.com")
        assertNotNull(encontrado)
        assertEquals("Dra. Patricia Gómez", encontrado?.nombre)

        val resp = UsuariosInternosManager.toUsuarioResponse(nuevo)
        assertEquals(nuevo.nombre, resp.nombre)
        assertEquals(nuevo.correo, resp.correo)
        assertEquals("ADMINISTRADOR", resp.rol)
        assertTrue(resp.activo)
        assertNotNull(resp.organizacion)
    }
}
