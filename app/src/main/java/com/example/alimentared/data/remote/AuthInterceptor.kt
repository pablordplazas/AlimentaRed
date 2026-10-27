package com.example.alimentared.data.remote

import com.example.alimentared.data.session.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()

        builder.addHeader("Accept", "application/json")

        // Obtener token JWT si existe en sesión
        try {
            val token = SessionManager.getInstance().getToken()
            if (!token.isNullOrBlank()) {
                builder.addHeader("Authorization", "Bearer $token")
            }
        } catch (ignored: Exception) {
            // Si SessionManager aún no se inicializó, continuar sin token (ej. login público)
        }

        return chain.proceed(builder.build())
    }
}
