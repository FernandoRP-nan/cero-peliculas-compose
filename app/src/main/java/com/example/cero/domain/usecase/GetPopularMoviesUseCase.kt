package com.example.cero.domain.usecase

import com.example.cero.domain.model.Movie
import com.example.cero.domain.respository.MovieRepository
import javax.inject.Inject

class GetPopularMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(): List<Movie> {
        return repository.getPopularMovies()
    }
}