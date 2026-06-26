package com.example.cahu_movie.back_end.domain.repository

import com.example.cahu_movie.back_end.domain.models.Movie
import com.example.cahu_movie.utils.Resource
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    fun fetchLatestMovies(
        page: Int = 1
    ): Flow<Resource<List<Movie>>>

    fun fetchMoviesByCategory(
        slug: String,
        page: Int = 1
    ): Flow<Resource<List<Movie>>>

    fun fetchMoviesByGenre(
        slug: String,
        page: Int = 1
    ): Flow<Resource<List<Movie>>>

    fun fetchMoviesByCountry(
        slug: String,
        page: Int = 1
    ): Flow<Resource<List<Movie>>>

    fun fetchMoviesByYear(
        year: Int,
        page: Int = 1
    ): Flow<Resource<List<Movie>>>

    fun searchMovies(
        keyword: String
    ): Flow<Resource<List<Movie>>>
}