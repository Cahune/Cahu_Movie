package com.example.cahu_movie.back_end.domain.models

data class MovieCategory(
    val name: String,
    val slug: String
)

data class Movie(
    val casts: String,
    val categories: List<MovieCategory>,
    val created: String,
    val currentEpisode: String,
    val description: String,
    val director: String,
    val language: String,
    val modified: String,
    val name: String,
    val originalName: String,
    val posterUrl: String,
    val quality: String,
    val slug: String,
    val thumbUrl: String,
    val time: String,
    val totalEpisodes: Int
)
