package com.example.cahu_movie.back_end.data.remote.models

import com.google.gson.annotations.SerializedName

data class MovieDetailDto(
    val status: String? = null,
    val movie: MovieDetailItem? = null
)

data class MovieDetailItem(
    val id: String? = null,
    val name: String? = null,
    val slug: String? = null,

    @SerializedName("original_name")
    val originalName: String? = null,

    @SerializedName("thumb_url")
    val thumbUrl: String? = null,

    @SerializedName("poster_url")
    val posterUrl: String? = null,

    val description: String? = null,

    @SerializedName("total_episodes")
    val totalEpisodes: Int? = null,

    @SerializedName("current_episode")
    val currentEpisode: String? = null,

    val time: String? = null,
    val quality: String? = null,
    val language: String? = null,
    val director: String? = null,
    val casts: String? = null,

    val category: List<CategoryDto>? = null,
    val categories: List<CategoryDto>? = null,

    val episodes: List<EpisodeServer>? = null
)

data class EpisodeServer(
    @SerializedName("server_name")
    val serverName: String? = null,

    val items: List<EpisodeItem>? = null
)

data class EpisodeItem(
    val name: String? = null,
    val slug: String? = null,
    val embed: String? = null
)
