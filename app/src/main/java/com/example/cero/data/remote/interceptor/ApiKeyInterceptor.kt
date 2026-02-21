package com.example.cero.data.remote.interceptor
import com.example.cero.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ApiKeyInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        val newRequest = request.newBuilder()
            .addHeader(
                "Authorization",
                "Bearer ${BuildConfig.TMDB_API_KEY}"
            )
            .addHeader("accept", "application/json")
            .build()

        return chain.proceed(newRequest)
    }
}