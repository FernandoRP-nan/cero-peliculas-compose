package com.example.cero.data.remote.PopularMovie.response

import com.example.cero.data.remote.PopularMovie.model.MovieDto

data class PopularMoviesResponse(
    val results: List<MovieDto>
)