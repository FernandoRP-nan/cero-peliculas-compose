package com.example.cero.feature.movies.presentation.view


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cero.feature.movies.presentation.components.MovieItem
import com.example.cero.feature.movies.presentation.state.MovieUiState
import com.example.cero.feature.movies.presentation.viewmodel.MovieViewModel

@Composable
fun MovieScreen(
    viewModel: MovieViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Manejamos los diferentes estados de la UI
        when (val currentState = state) {
            is MovieUiState.Loading -> {
                // Muestra un indicador de carga (opcional)
                // CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is MovieUiState.Success -> {
                LazyColumn {
                    // Ahora sí, accedemos a currentState.movies
                    items(currentState.movies) { movie ->
                        MovieItem(movie = movie)
                    }
                }
            }
            is MovieUiState.Error -> {
                // Muestra el mensaje de error: Text(text = currentState.message)
            }
            is MovieUiState.Empty -> {
                // Muestra un mensaje de "No hay películas"
            }
        }
    }
}

