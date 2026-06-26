package com.example.cahu_movie.back_end.domain.models

data class WatchHistory(

    val slug: String,

    val movieName: String,

    val episodeSlug: String,

    val episodeName: String,

    val posterUrl: String,

    val currentPosition: Long,

    val duration: Long,

    val lastWatchTime: Long
)