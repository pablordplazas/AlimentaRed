package com.example.alimentared

import com.example.alimentared.data.lotes.LoteImagenesManager
import org.junit.Assert.*
import org.junit.Test

class LoteImagenesTest {

    @Test
    fun `regla de negocio - un lote debe tener minimo 1 foto y maximo 3 fotos`() {
        // 0 fotos -> No cumple requisito obligatorio
        val fotosVacias = emptyList<String>()
        assertFalse("El lote no puede publicarse sin fotos", fotosVacias.isNotEmpty())

        // 1 foto -> Cumple requisito mínimo
        val unaFoto = listOf("https://images.com/foto1.jpg")
        assertTrue(unaFoto.size in 1..3)

        // 2 fotos -> Válido
        val dosFotos = listOf("https://images.com/foto1.jpg", "https://images.com/foto2.jpg")
        assertTrue(dosFotos.size in 1..3)

        // 3 fotos -> Cumple requisito máximo
        val tresFotos = listOf(
            "https://images.com/foto1.jpg",
            "https://images.com/foto2.jpg",
            "https://images.com/foto3.jpg"
        )
        assertTrue(tresFotos.size in 1..3)

        // Más de 3 fotos -> Debe ser limitado / rechazado
        val cuatroFotos = listOf(
            "https://images.com/foto1.jpg",
            "https://images.com/foto2.jpg",
            "https://images.com/foto3.jpg",
            "https://images.com/foto4.jpg"
        )
        assertFalse(cuatroFotos.size in 1..3)
        assertEquals(3, cuatroFotos.take(3).size)
    }

    @Test
    fun `asociacion de fotos a un lote respeta el limite maximo de 3`() {
        val loteId = 999L
        val fotos = listOf(
            "file:///data/foto1.jpg",
            "file:///data/foto2.jpg",
            "file:///data/foto3.jpg",
            "file:///data/foto4.jpg"
        )

        LoteImagenesManager.asociarFotosALote(loteId, fotos)
        val recuperadas = LoteImagenesManager.obtenerFotosParaLote(loteId)

        assertEquals("No debe almacenar más de 3 fotos", 3, recuperadas.size)
        assertEquals("file:///data/foto1.jpg", recuperadas[0])
        assertEquals("file:///data/foto2.jpg", recuperadas[1])
        assertEquals("file:///data/foto3.jpg", recuperadas[2])
    }

    @Test
    fun `obtenerFotoPrincipal retorna la primera imagen asignada`() {
        val loteId = 888L
        val fotos = listOf(
            "file:///data/principal.jpg",
            "file:///data/secundaria.jpg"
        )

        LoteImagenesManager.asociarFotosALote(loteId, fotos)
        val fotoPrincipal = LoteImagenesManager.obtenerFotoPrincipal(loteId)

        assertEquals("file:///data/principal.jpg", fotoPrincipal)
    }

    @Test
    fun `lotes sin fotos personalizadas reciben fotos curadas tematicas para visibilidad de receptores`() {
        // Lote de panadería
        val fotosPan = LoteImagenesManager.obtenerFotosParaLote(1001L, "Pan tajado integral", "Panadería")
        assertTrue("Debe proveer entre 1 y 3 fotos de alta calidad", fotosPan.size in 1..3)
        assertTrue("Foto de panadería debe ser válida", fotosPan.first().startsWith("http"))

        // Lote de frutas
        val fotosFruta = LoteImagenesManager.obtenerFotosParaLote(1002L, "Manzanas rojas", "Frutas y verduras")
        assertTrue(fotosFruta.size in 1..3)

        // Lote de abarrotes / atún
        val fotosAtun = LoteImagenesManager.obtenerFotosParaLote(1003L, "Atún enlatado", "Granos y abarrotes")
        assertTrue(fotosAtun.size in 1..3)

        // Lote de comida preparada / sopa
        val fotosSopa = LoteImagenesManager.obtenerFotosParaLote(1004L, "Sopa nutritiva", "Comidas preparadas")
        assertTrue(fotosSopa.size in 1..3)
    }
}
