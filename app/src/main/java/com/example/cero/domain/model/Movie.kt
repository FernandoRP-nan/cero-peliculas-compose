package com.example.cero.domain.model


data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?
)