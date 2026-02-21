package com.example.cero.data.repository


import com.example.cero.data.mapper.toDomain
import com.example.cero.data.remote.api.MovieApi
import com.example.cero.domain.model.Movie
import com.example.cero.domain.respository.MovieRepository
import javax.inject.Inject


class MovieRepositoryImpl @Inject constructor(
    private val api: MovieApi
) : MovieRepository {

    override suspend fun getPopularMovies(): List<Movie> {
        return api.getPopularMovies().results.map { it.toDomain() }
    }
}