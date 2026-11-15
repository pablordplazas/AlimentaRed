package com.example.alimentared

import com.example.alimentared.util.LogisticaLocationUtils
import org.junit.Assert.*
import org.junit.Test

class LogisticaLocationUtilsTest {

    @Test
    fun `extraerCoordenadas desde formato de corchetes funciona correctamente`() {
        val texto = "Av. Carrera 80 # 2-51, Corabastos [4.62840, -74.15370]"
        val punto = LogisticaLocationUtils.extraerCoordenadas(texto)

        assertEquals(4.62840, punto.latitud, 0.0001)
        assertEquals(-74.15370, punto.longitud, 0.0001)
        assertEquals("Av. Carrera 80 # 2-51, Corabastos", punto.direccion)
    }

    @Test
    fun `extraerCoordenadas desde formato de parentesis funciona correctamente`() {
        val texto = "Plaza de Paloquemao (Lat: 4.61450, Lng: -74.08860)"
        val punto = LogisticaLocationUtils.extraerCoordenadas(texto)

        assertEquals(4.61450, punto.latitud, 0.0001)
        assertEquals(-74.08860, punto.longitud, 0.0001)
        assertEquals("Plaza de Paloquemao", punto.direccion)
    }

    @Test
    fun `extraerCoordenadas reconoce puntos logisticos predefinidos por nombre`() {
        val textoCorabastos = "Entregar en Corabastos Bogotá"
        val puntoCorabastos = LogisticaLocationUtils.extraerCoordenadas(textoCorabastos)

        assertEquals(4.6284, puntoCorabastos.latitud, 0.001)
        assertEquals(-74.1537, puntoCorabastos.longitud, 0.001)
    }

    @Test
    fun `extraerCoordenadas usa valores por defecto cuando el texto esta vacio o es nulo`() {
        val puntoNulo = LogisticaLocationUtils.extraerCoordenadas(null)
        assertEquals(LogisticaLocationUtils.DEFAULT_LAT, puntoNulo.latitud, 0.0001)
        assertEquals(LogisticaLocationUtils.DEFAULT_LNG, puntoNulo.longitud, 0.0001)

        val puntoVacio = LogisticaLocationUtils.extraerCoordenadas("   ")
        assertEquals(LogisticaLocationUtils.DEFAULT_LAT, puntoVacio.latitud, 0.0001)
        assertEquals(LogisticaLocationUtils.DEFAULT_LNG, puntoVacio.longitud, 0.0001)
    }

    @Test
    fun `extraerDireccionLimpia remueve bloques de coordenadas`() {
        val conCorchetes = "Calle 19 # 25-04, Paloquemao [4.61450, -74.08860]"
        assertEquals("Calle 19 # 25-04, Paloquemao", LogisticaLocationUtils.extraerDireccionLimpia(conCorchetes))

        val conParentesis = "Carrera 7 # 12-40 (Lat: 4.59810, Lng: -74.07580)"
        assertEquals("Carrera 7 # 12-40", LogisticaLocationUtils.extraerDireccionLimpia(conParentesis))
    }

    @Test
    fun `formatearUbicacionParaGuardar genera formato estandar valido`() {
        val formateado = LogisticaLocationUtils.formatearUbicacionParaGuardar(
            direccion = "Calle 74B # 69Q-35, Las Ferias",
            latitud = 4.69120,
            longitud = -74.08980
        )

        assertTrue(formateado.startsWith("Calle 74B # 69Q-35, Las Ferias"))
        assertTrue(formateado.contains("[4.69120, -74.08980]"))

        // Verificar que el formato resultante se puede volver a parsear exactamente
        val parseado = LogisticaLocationUtils.extraerCoordenadas(formateado)
        assertEquals(4.69120, parseado.latitud, 0.0001)
        assertEquals(-74.08980, parseado.longitud, 0.0001)
        assertEquals("Calle 74B # 69Q-35, Las Ferias", parseado.direccion)
    }
}
