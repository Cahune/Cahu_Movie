package com.example.cahu_movie.common.data

import com.example.cahu_movie.back_end.data.remote.models.MovieDto

interface ApiMapper<Domain, Entity> {
    fun mapToDomain(apiDto: Entity):Domain
}