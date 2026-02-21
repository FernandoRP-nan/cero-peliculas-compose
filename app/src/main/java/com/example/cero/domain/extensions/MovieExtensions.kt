package com.example.cero.domain.extensions

import com.example.cero.BuildConfig
import com.example.cero.domain.model.Movie

private const val IMAGE_BASE_URL = BuildConfig.IMAGE_BASE_URL

val Movie.fullPosterUrl: String?
    get() = posterUrl?.let { "$IMAGE_BASE_URL$it" }