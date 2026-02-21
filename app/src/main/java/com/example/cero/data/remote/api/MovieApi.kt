package com.example.cero.data.remote.api

import com.example.cero.data.remote.PopularMovie.response.PopularMoviesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi  {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1
    ): PopularMoviesResponse
}