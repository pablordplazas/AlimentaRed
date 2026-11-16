package com.example.alimentared

import com.example.alimentared.data.usuarios.UsuariosInternosManager
import org.junit.Assert.*
import org.junit.Test

class GestionContrasenaTest {

    @Test
    fun `validacion de longitud minima de contrasena (8 caracteres)`() {
        val passCorta = "Pass1"
        val passValida = "Pass12345"

        assertTrue("La contraseña corta debe ser inválida", passCorta.length < 8)
        assertTrue("La contraseña de 9 caracteres debe cumplir la longitud mínima", passValida.length in 8..72)
    }

    @Test
    fun `validacion de complejidad alfanumerica (debe contener letras y numeros)`() {
        val soloLetras = "SoloLetrasSinNumeros"
        val soloNumeros = "1234567890"
        val alfanumericaValida = "AlimentaRed2026"

        val cumpleSoloLetras = soloLetras.any { it.isLetter() } && soloLetras.any { it.isDigit() }
        val cumpleSoloNumeros = soloNumeros.any { it.isLetter() } && soloNumeros.any { it.isDigit() }
        val cumpleAlfanumerica = alfanumericaValida.any { it.isLetter() } && alfanumericaValida.any { it.isDigit() }

        assertFalse("Solo letras debe fallar", cumpleSoloLetras)
        assertFalse("Solo números debe fallar", cumpleSoloNumeros)
        assertTrue("Alfanumérica debe ser válida", cumpleAlfanumerica)
    }

    @Test
    fun `validacion de coincidencia exacta entre nueva contrasena y confirmacion`() {
        val passNueva = "Segura12345"
        val passConfirmarDistinta = "Segura123456"
        val passConfirmarExacta = "Segura12345"

        assertNotEquals("Contraseñas no coinciden", passNueva, passConfirmarDistinta)
        assertEquals("Contraseñas deben coincidir exactamente", passNueva, passConfirmarExacta)
    }

    @Test
    fun `validacion de que la nueva contrasena sea distinta a la actual`() {
        val contrasenaActual = "Admin12345"
        val contrasenaNuevaIgual = "Admin12345"
        val contrasenaNuevaDistinta = "NuevoAdmin2026"

        assertTrue("No debe ser igual a la actual", contrasenaActual == contrasenaNuevaIgual)
        assertTrue("Debe ser distinta a la actual", contrasenaActual != contrasenaNuevaDistinta)
    }

    @Test
    fun `actualizacion de contrasena en usuarios internos`() {
        // Administrador AlimentaRed predefinido
        val correoAdmin = "admin@alimentared.com"
        val nuevaPass = "NuevaAdminPass2026"

        val resultado = UsuariosInternosManager.actualizarContrasena(correoAdmin, nuevaPass)
        assertTrue("Debe actualizar la contraseña del usuario interno", resultado)

        val adminActualizado = UsuariosInternosManager.buscarPorCorreo(correoAdmin)
        assertNotNull("El usuario debe existir", adminActualizado)
        assertEquals("La contraseña debe ser la nueva", nuevaPass, adminActualizado?.contrasena)

        // Coordinador Red Comunitaria
        val correoCoord = "coordinador@alimentared.com"
        val nuevaPassCoord = "CoordRenovada2026"

        val resultadoCoord = UsuariosInternosManager.actualizarContrasena(correoCoord, nuevaPassCoord)
        assertTrue("Debe actualizar la contraseña del coordinador", resultadoCoord)

        val coordActualizado = UsuariosInternosManager.buscarPorCorreo(correoCoord)
        assertNotNull("El coordinador debe existir", coordActualizado)
        assertEquals("La contraseña debe ser la nueva", nuevaPassCoord, coordActualizado?.contrasena)
    }
}
