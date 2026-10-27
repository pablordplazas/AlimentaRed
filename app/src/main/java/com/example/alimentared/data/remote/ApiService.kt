package com.example.alimentared.data.remote

import com.example.alimentared.data.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ApiService {

    // --- AUTENTICACIÓN Y USUARIOS (RF01, RF02) ---
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("usuarios/me")
    suspend fun getPerfil(): Response<UsuarioResponse>

    @GET("health")
    suspend fun checkHealth(): Response<Map<String, Any>>

    // --- CATEGORÍAS ---
    @GET("categorias")
    suspend fun getCategorias(): Response<List<TipoAlimentoResponse>>

    // --- LOTES DE ALIMENTOS (RF03, RF04) ---
    @GET("lotes")
    suspend fun getLotes(
        @Query("categoriaId") categoriaId: Long? = null,
        @Query("rescateUrgente") rescateUrgente: Boolean? = null,
        @Query("query") query: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<PageResponse<LoteResponse>>

    @GET("lotes/{id}")
    suspend fun getLotePorId(@Path("id") id: Long): Response<LoteResponse>

    @GET("lotes/mis-lotes")
    suspend fun getMisLotes(
        @Query("estado") estado: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<PageResponse<LoteResponse>>

    @POST("lotes")
    suspend fun crearLote(@Body request: LoteRequest): Response<LoteResponse>

    @DELETE("lotes/{id}")
    suspend fun cancelarLote(@Path("id") id: Long): Response<Unit>

    @GET("lotes/{id}/historial")
    suspend fun getHistorialLote(@Path("id") id: Long): Response<List<HistorialEstadoResponse>>

    // --- SOLICITUDES (RF05, RF06, RN07, RN09) ---
    @POST("solicitudes")
    suspend fun crearSolicitud(@Body request: SolicitudRequest): Response<SolicitudResponse>

    @GET("solicitudes/mis-solicitudes")
    suspend fun getMisSolicitudes(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<PageResponse<SolicitudResponse>>

    @GET("solicitudes/activas/conteo")
    suspend fun getConteoActivas(): Response<Map<String, Long>>

    @GET("solicitudes/lote/{loteId}")
    suspend fun getSolicitudesPorLote(
        @Path("loteId") loteId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<PageResponse<SolicitudResponse>>

    @GET("solicitudes/{id}")
    suspend fun getSolicitudPorId(@Path("id") id: Long): Response<SolicitudResponse>

    @PATCH("solicitudes/{id}/resolver")
    suspend fun resolverSolicitud(
        @Path("id") id: Long,
        @Body request: ResolverSolicitudRequest
    ): Response<SolicitudResponse>

    @PATCH("solicitudes/{id}/cancelar")
    suspend fun cancelarSolicitud(@Path("id") id: Long): Response<SolicitudResponse>

    // --- ASIGNACIONES Y ENTREGAS (RF07, RN08, RN11, RN12) ---
    @GET("entregas/asignaciones")
    suspend fun getAsignaciones(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<PageResponse<AsignacionResponse>>

    @GET("entregas/asignaciones/{id}")
    suspend fun getAsignacionPorId(@Path("id") id: Long): Response<AsignacionResponse>

    @PATCH("entregas/{asignacionId}/confirmar")
    suspend fun confirmarEntrega(
        @Path("asignacionId") asignacionId: Long,
        @Body request: ConfirmarEntregaRequest
    ): Response<EntregaResponse>

    @PATCH("entregas/{asignacionId}/no-recogida")
    suspend fun reportarNoRecogida(
        @Path("asignacionId") asignacionId: Long,
        @Body request: Map<String, String> = mapOf("motivo" to "Inasistencia de retiro físico")
    ): Response<AsignacionResponse>

    @GET("entregas/asignaciones/{asignacionId}/retiros")
    suspend fun getRetirosPorAsignacion(@Path("asignacionId") asignacionId: Long): Response<List<EntregaResponse>>

    // --- INCIDENCIAS (RF08) ---
    @POST("incidencias")
    suspend fun reportarIncidencia(@Body request: IncidenciaRequest): Response<IncidenciaResponse>

    @GET("incidencias")
    suspend fun getIncidencias(
        @Query("resuelta") resuelta: Boolean? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<PageResponse<IncidenciaResponse>>

    @GET("incidencias/lote/{loteId}")
    suspend fun getIncidenciasPorLote(@Path("loteId") loteId: Long): Response<List<IncidenciaResponse>>

    @PATCH("incidencias/{id}/resolver")
    suspend fun resolverIncidencia(
        @Path("id") id: Long,
        @Body request: ResolverIncidenciaRequest
    ): Response<IncidenciaResponse>

    // --- ORGANIZACIONES Y STRIKES (RF02, RN08, RN12) ---
    @GET("organizaciones/{id}")
    suspend fun getOrganizacionPorId(@Path("id") id: Long): Response<OrganizacionResponse>

    @GET("organizaciones/{id}/strikes")
    suspend fun getStrikesPorOrganizacion(@Path("id") id: Long): Response<List<StrikeResponse>>

    @PATCH("organizaciones/{id}/strikes/apelar")
    suspend fun apelarStrike(
        @Path("id") id: Long,
        @Body request: ApelacionStrikeRequest
    ): Response<StrikeResponse>

    // --- REPORTES (RF09) ---
    @GET("reportes/impacto")
    suspend fun getReporteImpacto(
        @Query("desde") desde: String? = null,
        @Query("hasta") hasta: String? = null,
        @Query("categoriaId") categoriaId: Long? = null
    ): Response<ReporteImpactoResponse>

    @GET("reportes/historial")
    suspend fun getHistorialEntregas(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<PageResponse<EntregaResponse>>
}

object RetrofitClient {
    // Por defecto usa la IP local del host (192.168.1.18:8080) que funciona garantizada en emulador y físico
    var baseUrl: String = "http://192.168.1.18:8080/api/"
        private set

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Volatile
    private var _instance: ApiService? = null

    val instance: ApiService
        get() {
            return _instance ?: synchronized(this) {
                _instance ?: buildApiService(baseUrl).also { _instance = it }
            }
        }

    fun updateBaseUrl(newUrl: String) {
        var formatted = newUrl.trim()
        if (!formatted.startsWith("http://") && !formatted.startsWith("https://")) {
            formatted = "http://$formatted"
        }
        if (!formatted.endsWith("/")) {
            formatted = "$formatted/"
        }
        if (!formatted.endsWith("api/")) {
            formatted = "${formatted}api/"
        }
        baseUrl = formatted
        synchronized(this) {
            _instance = buildApiService(formatted)
        }
    }

    private fun buildApiService(url: String): ApiService {
        return Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
