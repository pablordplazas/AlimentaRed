package com.example.alimentared.data.model

import com.google.gson.annotations.SerializedName

// --- PAGINACIÓN ---
data class PageResponse<T>(
    val contenido: List<T> = emptyList(),
    val pagina: Int = 0,
    val tamano: Int = 20,
    val totalElementos: Long = 0,
    val totalPaginas: Int = 0,
    val ultima: Boolean = true
)

// --- AUTENTICACIÓN Y USUARIOS (RF01, RF02) ---
data class LoginRequest(
    val correo: String,
    val contrasena: String
)

data class RegisterRequest(
    val nit: String,
    val organizacionNombre: String,
    val correoOrganizacion: String,
    val telefono: String? = null,
    val ubicacion: String? = null,
    val nombreContacto: String,
    val contrasena: String,
    val rol: String // "DONANTE" o "RECEPTORA"
)

data class AuthResponse(
    val token: String,
    val tipo: String = "Bearer",
    val expiraEnSegundos: Long,
    val usuario: UsuarioResponse
)

data class UsuarioResponse(
    val id: Long,
    val nombre: String,
    val correo: String,
    val rol: String,
    val activo: Boolean,
    val fechaRegistro: String? = null,
    val organizacion: OrganizacionResponse? = null
)

data class OrganizacionResponse(
    val id: Long,
    val nit: String,
    val nombre: String,
    val ubicacion: String? = null,
    val correo: String,
    val telefono: String? = null,
    val verificada: Boolean = false,
    val bloqueada: Boolean = false,
    val strikesVigentes: Int = 0,
    val fechaRegistro: String? = null
)

// --- CATEGORÍAS ---
data class TipoAlimentoResponse(
    val id: Long,
    val nombre: String,
    val descripcion: String? = null,
    val unidadMedida: String
)

// --- LOTES (RF03, RF04) ---
data class LoteResponse(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val categoriaId: Long,
    val categoriaNombre: String? = null,
    val unidadMedida: String? = null,
    val donanteId: Long,
    val donanteNombre: String? = null,
    val cantidadInicial: Double,
    val cantidadDisponible: Double,
    val unidadMinima: Double,
    val multiplo: Double,
    val estado: String,
    val estadoAlimento: String? = null,
    val ubicacion: String? = null,
    val instruccionesRecogida: String? = null,
    val contacto: String? = null,
    val fechaRegistro: String? = null,
    val fechaCaducidad: String,
    val rescateUrgente: Boolean = false
)

data class LoteRequest(
    val nombre: String,
    val descripcion: String,
    val categoriaId: Long,
    val cantidad: Double,
    val unidadMinima: Double,
    val multiplo: Double,
    val estadoAlimento: String = "FRESCO",
    val fechaCaducidad: String,
    val ubicacion: String? = null,
    val instruccionesRecogida: String? = null,
    val contacto: String? = null
)

// --- SOLICITUDES (RF05, RF06) ---
data class SolicitudRequest(
    val loteId: Long,
    val cantidadSolicitada: Double
)

data class SolicitudResponse(
    val id: Long,
    val loteId: Long,
    val loteNombre: String? = null,
    val organizacionId: Long,
    val organizacionNombre: String? = null,
    val cantidadSolicitada: Double,
    val estado: String, // PENDIENTE, APROBADO, CANCELADO, RECHAZADA, EXPIRADA, COMPLETADA
    val fechaCreacion: String? = null,
    val fechaResolucion: String? = null,
    val enEmpate: Boolean = false,
    val motivoResolucion: String? = null,
    val cantidadComprometida: Double? = null
)

data class ResolverSolicitudRequest(
    val aprobada: Boolean,
    val motivo: String? = null
)

// --- ASIGNACIONES Y ENTREGAS (RF07) ---
data class AsignacionResponse(
    val id: Long,
    val solicitudId: Long,
    val loteId: Long? = null,
    val loteNombre: String? = null,
    val donanteNombre: String? = null,
    val receptoraNombre: String? = null,
    val cantidadAsignada: Double,
    val cantidadRetirada: Double = 0.0,
    val estado: String, // ACTIVA, RETIRO_PARCIAL, COMPLETADA, NO_RECOGIDA, CANCELADA
    val autoAsignada: Boolean = false,
    val fechaAsignacion: String? = null,
    val fechaLimiteRetiro: String? = null,
    val fechaCierre: String? = null,
    val instruccionesRecogida: String? = null,
    val contactoDonante: String? = null
)

data class ConfirmarEntregaRequest(
    val cantidadRetirada: Double,
    val observaciones: String? = null
)

data class EntregaResponse(
    val id: Long,
    val solicitudId: Long,
    val asignacionId: Long,
    val loteNombre: String? = null,
    val donanteNombre: String? = null,
    val receptoraNombre: String? = null,
    val cantidadRetirada: Double,
    val estado: String,
    val fechaEntrega: String? = null,
    val observaciones: String? = null,
    val responsable: String? = null,
    val fueraDeFranja: Boolean = false
)

// --- INCIDENCIAS (RF08) ---
data class IncidenciaRequest(
    val loteId: Long,
    val detalles: String
)

data class IncidenciaResponse(
    val id: Long,
    val loteId: Long,
    val loteNombre: String? = null,
    val detalles: String,
    val estadoLoteAnterior: String? = null,
    val reportadoPor: String? = null,
    val fechaReporte: String? = null,
    val resuelta: Boolean = false,
    val resolucion: String? = null,
    val resueltoPor: String? = null,
    val fechaResolucion: String? = null
)

data class ResolverIncidenciaRequest(
    val reactivarLote: Boolean,
    val resolucion: String
)

// --- STRIKES Y ORGANIZACIONES (RF02, RN08, RN12) ---
data class StrikeResponse(
    val id: Long,
    val organizacionId: Long,
    val organizacionNombre: String? = null,
    val asignacionId: Long? = null,
    val nombreStrike: String,
    val detalles: String? = null,
    val fechaRegistro: String? = null,
    val vigente: Boolean = true,
    val justificacionApelacion: String? = null,
    val fechaApelacion: String? = null,
    val evaluador: String? = null
)

data class ApelacionStrikeRequest(
    val strikeId: Long,
    val justificacion: String,
    val aprobada: Boolean = true
)

// --- REPORTES Y MÉTRICAS (RF09, RN04) ---
data class ReporteImpactoResponse(
    val desde: String? = null,
    val hasta: String? = null,
    val totalLotesPublicados: Long = 0,
    val cantidadTotalPublicada: Double = 0.0,
    val cantidadTotalAsignada: Double = 0.0,
    val cantidadTotalRecuperada: Double = 0.0,
    val porcentajeRecuperacion: Double = 0.0,
    val totalEntregas: Long = 0,
    val entregasFueraDeFranja: Long = 0,
    val strikesRegistrados: Long = 0,
    val organizacionesBloqueadas: Long = 0,
    val porCategoria: List<ImpactoCategoria>? = null,
    val organizacionesConStrikes: List<OrganizacionStrikes>? = null
)

data class ImpactoCategoria(
    val categoriaId: Long,
    val categoria: String,
    val unidadMedida: String,
    val lotes: Long,
    val cantidadPublicada: Double,
    val cantidadRecuperada: Double
)

data class OrganizacionStrikes(
    val organizacionId: Long,
    val nombre: String,
    val strikesVigentes: Int,
    val bloqueada: Boolean
)

data class HistorialEstadoResponse(
    val id: Long,
    val entidad: String,
    val entidadId: Long,
    val estadoAnterior: String? = null,
    val estadoNuevo: String,
    val responsable: String? = null,
    val fecha: String,
    val justificacion: String? = null
)

data class CambioContrasenaRequest(
    val contrasenaActual: String,
    val contrasenaNueva: String
)

data class UsuarioInternoRequest(
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val rol: String
)

data class CancelacionRequest(
    val motivo: String? = null
)
