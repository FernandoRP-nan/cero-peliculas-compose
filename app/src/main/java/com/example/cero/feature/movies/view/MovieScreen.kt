package com.example.cero.feature.movies.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cero.feature.movies.viewmodel.MovieViewModel
import com.example.cero.feature.movies.components.MovieItem

@Preview
@Composable
fun MovieScreen(
    viewModel: MovieViewModel = hiltViewModel()
) {
    val movies by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn {
            items(movies) { movie ->
                MovieItem(movie = movie)
            }
        }

    }
}
