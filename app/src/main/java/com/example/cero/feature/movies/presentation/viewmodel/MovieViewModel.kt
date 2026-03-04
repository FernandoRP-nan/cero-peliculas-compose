package com.example.cero.feature.movies.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cero.core.network.NetworkMonitor
import com.example.cero.domain.usecase.GetPopularMoviesUseCase
import com.example.cero.feature.movies.presentation.state.MovieUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val getPopularMovies: GetPopularMoviesUseCase,
   private val networkMonitor: NetworkMonitor

) : ViewModel() {

    // LA COCINA (Privada y Mutable)
    // El cocinero tiene el control total de los ingredientes.
    private val _state = MutableStateFlow<MovieUiState>(MovieUiState.Loading)
    
    // EL MESERO (Público y de Solo Lectura)
    // Es el que sale al comedor. El cliente lo ve, pero no lo controla.
    // El "=" significa que el mesero informa exactamente lo que hace el cocinero.
    val state: StateFlow<MovieUiState> = _state

    init {
        viewModelScope.launch {
            networkMonitor.observe()
                .collectLatest { isConnected ->

                    if (!isConnected) {
                        _state.value = MovieUiState.Error("No internet connection")
                        return@collectLatest
                    }

                    loadMoviesInternal()
                }
        }
    }

    private suspend fun loadMoviesInternal() {

        _state.value = MovieUiState.Loading

        try {
            val movies = getPopularMovies()

            _state.value = if (movies.isEmpty()) {
                MovieUiState.Empty
            } else {
                MovieUiState.Success(movies)
            }

        } catch (e: Exception) {
            _state.value = MovieUiState.Error("Network error")
        }
    }
}