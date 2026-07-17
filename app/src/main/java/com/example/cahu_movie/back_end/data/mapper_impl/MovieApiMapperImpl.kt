package com.example.cahu_movie.back_end.data.mapper_impl

import com.example.cahu_movie.back_end.data.remote.models.MovieDto
import com.example.cahu_movie.back_end.domain.models.Movie
import com.example.cahu_movie.back_end.domain.models.MovieCategory
import com.example.cahu_movie.common.data.ApiMapper
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

class MovieApiMapperImpl : ApiMapper<List<Movie>, MovieDto> {

    override fun mapToDomain(apiDto: MovieDto): List<Movie> {
        return apiDto.items.orEmpty().mapNotNull { item ->
            item ?: return@mapNotNull null

            Movie(
                casts = formatEmptyValue(item.casts),
                categories = mapCategories(
                    item.category
                        ?.takeIf { it.hasCategoryData() }
                        ?: item.categories
                ),
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

    private fun mapCategories(
        categories: JsonElement?
    ): List<MovieCategory> {
        if (categories == null || categories.isJsonNull) {
            return emptyList()
        }

        val mappedCategories = when {
            categories.isJsonArray -> {
                mapCategoryArray(categories.asJsonArray)
            }

            categories.isJsonObject -> {
                mapCategoryObject(categories.asJsonObject)
            }

            else -> emptyList()
        }

        return mappedCategories.distinctBy { category ->
            "${category.name}|${category.slug}"
        }
    }

    private fun JsonElement.hasCategoryData(): Boolean {
        return when {
            isJsonArray -> asJsonArray.size() > 0
            isJsonObject -> asJsonObject.entrySet().isNotEmpty()
            else -> false
        }
    }

    private fun mapCategoryObject(
        categoryObject: JsonObject
    ): List<MovieCategory> {
        val directList = categoryObject.getAsJsonArrayOrNull("list")
        if (directList != null) {
            return mapCategoryArray(directList)
        }

        return categoryObject.entrySet().flatMap { entry ->
            val groupValue = entry.value
            if (groupValue?.isJsonObject == true) {
                val list = groupValue.asJsonObject.getAsJsonArrayOrNull("list")
                if (list != null) {
                    mapCategoryArray(list)
                } else {
                    listOfNotNull(mapCategoryItem(groupValue))
                }
            } else {
                emptyList()
            }
        }
    }

    private fun mapCategoryArray(
        categoryArray: JsonArray
    ): List<MovieCategory> {
        return categoryArray.mapNotNull { category ->
            mapCategoryItem(category)
        }
    }

    private fun mapCategoryItem(
        category: JsonElement?
    ): MovieCategory? {
        if (category == null || !category.isJsonObject) {
            return null
        }

        val categoryObject = category.asJsonObject
        val name = categoryObject.getAsStringOrEmpty("name")
        val slug = categoryObject.getAsStringOrEmpty("slug")
            .ifBlank {
                categoryObject.getAsStringOrEmpty("id")
            }
            .ifBlank {
                name
            }

        return if (name.isBlank() && slug.isBlank()) {
            null
        } else {
            MovieCategory(
                name = name,
                slug = slug
            )
        }
    }

    private fun JsonObject.getAsJsonArrayOrNull(
        memberName: String
    ): JsonArray? {
        val value = get(memberName)

        return if (value != null && value.isJsonArray) {
            value.asJsonArray
        } else {
            null
        }
    }

    private fun JsonObject.getAsStringOrEmpty(
        memberName: String
    ): String {
        val value = get(memberName)

        return if (
            value != null &&
            !value.isJsonNull &&
            value.isJsonPrimitive
        ) {
            value.asString.orEmpty()
        } else {
            ""
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
