package com.example.cero.feature.coffe.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.cero.domain.model.Movie
import com.example.cero.feature.movies.presentation.components.MovieItem

@Composable
fun CoffContent(movies: List<Movie>) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(movies) { movie ->
                MovieItem(movie = movie)
            }
        }
    }
}