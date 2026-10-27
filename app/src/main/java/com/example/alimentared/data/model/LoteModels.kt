package com.example.alimentared.data.model

import androidx.compose.runtime.mutableStateListOf

data class LoteDto(
    val id: Long,
    val nombre: String,
    val donante: String,
    val categoria: String,
    val tipoAlimento: String,
    val cantidadDisponible: Double,
    val unidad: String,
    val multiploMinimo: Double,
    val horasParaExpirar: Int,
    val esUrgente: Boolean,
    val descripcion: String = "Alimento fresco en excelentes condiciones de inocuidad."
)

data class SolicitudRequest(
    val loteId: Long,
    val cantidadSolicitada: Double,
    val organizacionId: Long
)

enum class EstadoSolicitud { PENDIENTE, APROBADA_EN_CAMINO, RECHAZADA, ENTREGADA }

data class SolicitudItem(
    val id: Long,
    val loteNombre: String,
    val donante: String,
    val receptora: String,
    val cantidad: Double,
    val unidad: String,
    var estado: EstadoSolicitud,
    val tiempoEntregaEstimado: String = "2 a 3 días hábiles"
)

// Repositorio Simulado en Memoria
object FakeDataRepository {
    val lotes = mutableStateListOf(
        LoteDto(1, "Bolsas de Pan Integral x 500g", "Panadería Central", "Panadería", "Panificados", 15.0, "Bolsas", 5.0, 3, true, "Lote de pan fresco horneado en la mañana."),
        LoteDto(2, "Cajas de Leche Entera 1L", "Supermercado Éxito", "Lácteos", "Líquidos", 40.0, "Unidades", 10.0, 12, false, "Leche pasteurizada en empaque TetraPak."),
        LoteDto(3, "Kilos de Tomate Chonto", "Fruver La Sabana", "Verduras", "Perecederos", 25.0, "Kg", 5.0, 2, true, "Tomate rojo maduro apto para consumo inmediato."),
        LoteDto(4, "Arroz Florhuila 1kg", "Distribuidora Mayorista", "Abarrotes", "Granos", 50.0, "Bolsas", 10.0, 48, false, "Arroz de primera calidad en sacos de 1kg.")
    )

    val solicitudes = mutableStateListOf<SolicitudItem>()

    fun obtenerLotePorId(id: Long): LoteDto? = lotes.find { it.id == id }

    fun agregarLote(nuevo: LoteDto) {
        lotes.add(0, nuevo)
    }

    fun crearSolicitud(lote: LoteDto, cantidad: Double, receptoraNombre: String) {
        solicitudes.add(
            SolicitudItem(
                id = System.currentTimeMillis(),
                loteNombre = lote.nombre,
                donante = lote.donante,
                receptora = receptoraNombre,
                cantidad = cantidad,
                unidad = lote.unidad,
                estado = EstadoSolicitud.PENDIENTE
            )
        )
    }
}
