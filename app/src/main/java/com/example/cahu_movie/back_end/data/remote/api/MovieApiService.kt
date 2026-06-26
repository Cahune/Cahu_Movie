package com.example.cahu_movie.back_end.data.remote.api

import com.example.cahu_movie.back_end.data.remote.models.MovieDto
import com.example.cahu_movie.utils.K
import com.example.cahu_movie.back_end.data.remote.models.MovieDetailDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {

    @GET(K.LATEST_MOVIES_ENDPOINT)
    suspend fun fetchLatestMovies(
        @Query(K.PAGE) page: Int = 1
    ): MovieDto

    @GET(K.CATEGORY_MOVIES_ENDPOINT)
    suspend fun fetchMoviesByCategory(
        @Path(K.SLUG) slug: String,
        @Query(K.PAGE) page: Int = 1
    ): MovieDto

    @GET(K.CATEGORY_MOVIES_ENDPOINT)
    suspend fun fetchNowPlayingMovies(
        @Path(K.SLUG) slug: String = K.NOW_PLAYING_CATEGORY,
        @Query(K.PAGE) page: Int = 1
    ): MovieDto

    @GET(K.GENRE_MOVIES_ENDPOINT)
    suspend fun fetchMoviesByGenre(
        @Path(K.SLUG) slug: String,
        @Query(K.PAGE) page: Int = 1
    ): MovieDto

    @GET(K.COUNTRY_MOVIES_ENDPOINT)
    suspend fun fetchMoviesByCountry(
        @Path(K.SLUG) slug: String,
        @Query(K.PAGE) page: Int = 1
    ): MovieDto

    @GET(K.YEAR_MOVIES_ENDPOINT)
    suspend fun fetchMoviesByYear(
        @Path(K.YEAR) year: Int,
        @Query(K.PAGE) page: Int = 1
    ): MovieDto

    @GET(K.SEARCH_MOVIES_ENDPOINT)
    suspend fun searchMovies(
        @Query(K.KEYWORD) keyword: String
    ): MovieDto

    @GET(K.MOVIE_DETAIL_ENDPOINT)
    suspend fun fetchMovieDetail(
        @Path(K.SLUG) slug: String
    ): MovieDetailDto
}