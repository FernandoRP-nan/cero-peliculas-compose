package com.example.cero.feature.movies.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cero.domain.model.Movie
import com.example.cero.domain.usecase.GetPopularMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val getPopularMovies: GetPopularMoviesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<List<Movie>>(emptyList())
    val state: StateFlow<List<Movie>> = _state

    init {
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            try {
                _state.value = getPopularMovies()
                println("soy yo ${ _state.value}")
            } catch (e: Exception) {
                Log.e("MOVIE_ERROR", e.message ?: "Unknown error")
            }
        }
    }
}