package com.example.cero.data.mapper

import com.example.cero.data.remote.PopularMovie.model.MovieDto
import com.example.cero.domain.model.Movie


fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        posterUrl = poster_path
    )
}