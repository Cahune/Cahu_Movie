package com.example.cahu_movie.back_end.data.mapper_impl

import com.example.cahu_movie.back_end.data.remote.models.MovieDto
import com.example.cahu_movie.back_end.domain.models.Movie
import com.example.cahu_movie.common.data.ApiMapper

class MovieApiMapperImpl : ApiMapper<List<Movie>, MovieDto> {

    override fun mapToDomain(apiDto: MovieDto): List<Movie> {
        return apiDto.items.orEmpty().mapNotNull { item ->
            item ?: return@mapNotNull null

            Movie(
                casts = formatEmptyValue(item.casts),
                created = formatEmptyValue(item.created),
                currentEpisode = formatEmptyValue(
                    item.currentEpisode,
                    "Đang cập nhật"
                ),
                description = formatEmptyValue(item.description),
                director = formatEmptyValue(
                    item.director,
                    "Đang cập nhật"
                ),
                language = formatEmptyValue(
                    item.language,
                    "Đang cập nhật"
                ),
                modified = formatEmptyValue(item.modified),
                name = formatEmptyValue(
                    item.name,
                    "Không rõ tên phim"
                ),
                originalName = formatEmptyValue(item.originalName),
                posterUrl = formatEmptyValue(item.posterUrl),
                quality = formatEmptyValue(
                    item.quality,
                    "Đang cập nhật"
                ),
                slug = formatEmptyValue(item.slug),
                thumbUrl = formatEmptyValue(item.thumbUrl),
                time = formatEmptyValue(
                    item.time,
                    "Đang cập nhật"
                ),
                totalEpisodes = item.totalEpisodes ?: 0
            )
        }
    }
    private fun formatEmptyValue(
        value: String?,
        defaultValue: String = ""
    ): String {
        return if (value.isNullOrBlank()) {
            defaultValue
        } else {
            value
        }
    }
}