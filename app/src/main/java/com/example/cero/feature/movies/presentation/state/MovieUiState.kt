package com.example.cero.feature.movies.presentation.state
import com.example.cero.domain.model.Movie


sealed class MovieUiState {
    object Loading : MovieUiState()
    data class Success(val movies: List<Movie>) : MovieUiState()
    data class Error(val message: String) : MovieUiState()
    object Empty : MovieUiState()
}