package com.example.alimentared.data.remote

import com.example.alimentared.data.model.LoteDto
import com.example.alimentared.data.model.SolicitudRequest
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {
    @GET("lotes")
    suspend fun getLotes(): Response<List<LoteDto>>

    @POST("lotes")
    suspend fun crearLote(@Body lote: LoteDto): Response<LoteDto>

    @POST("solicitudes")
    suspend fun crearSolicitud(@Body solicitud: SolicitudRequest): Response<Unit>
}

object RetrofitClient {
    // Para pruebas locales en emulador usa 10.0.2.2 o la IP del backend
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
