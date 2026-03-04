package com.example.cero.data.di

import android.content.Context
import com.example.cero.core.network.NetworkMonitor
import com.example.cero.core.network.NetworkMonitorImpl
import com.example.cero.data.remote.api.MovieApi
import com.example.cero.data.remote.interceptor.ApiKeyInterceptor
import com.example.cero.security.NativeKeys
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import javax.inject.Singleton
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient {

        val certificatePinner = CertificatePinner.Builder()
            .add(
                "api.themoviedb.org",
                "sha256/f78NVAesYtdZ9OGSbK7VtGQkSIVykh3DnduuLIJHMu4="
            )
            .add(
                "api.themoviedb.org",
                "sha256/G9LNNAql897egYsabashkzUCTEJkWBzgoEtk8X/678c="
            )
            .build()

        return OkHttpClient.Builder()
            .certificatePinner(certificatePinner)
            .addInterceptor(ApiKeyInterceptor())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(NativeKeys.getBaseUrl())
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideMovieApi(retrofit: Retrofit): MovieApi =
        retrofit.create(MovieApi::class.java)

    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context
    ): NetworkMonitor {
        return NetworkMonitorImpl(context)
    }
}