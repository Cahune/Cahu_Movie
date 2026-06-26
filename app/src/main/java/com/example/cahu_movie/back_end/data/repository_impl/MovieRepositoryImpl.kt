package com.example.cahu_movie.back_end.data.repository_impl

import com.example.cahu_movie.back_end.data.remote.api.MovieApiService
import com.example.cahu_movie.back_end.data.remote.models.MovieDto
import com.example.cahu_movie.back_end.domain.models.Movie
import com.example.cahu_movie.back_end.domain.repository.MovieRepository
import com.example.cahu_movie.common.data.ApiMapper
import com.example.cahu_movie.utils.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MovieRepositoryImpl(
    private val movieApiService: MovieApiService,
    private val movieApiMapper: ApiMapper<List<Movie>, MovieDto>
) : MovieRepository {

    override fun fetchLatestMovies(
        page: Int
    ): Flow<Resource<List<Movie>>> {
        return executeRequest(
            errorMessage = "Không thể tải danh sách phim mới"
        ) {
            movieApiService.fetchLatestMovies(page)
        }
    }

    override fun fetchMoviesByCategory(
        slug: String,
        page: Int
    ): Flow<Resource<List<Movie>>> {
        return executeRequest(
            errorMessage = "Không thể tải phim theo danh mục"
        ) {
            movieApiService.fetchMoviesByCategory(
                slug = slug,
                page = page
            )
        }
    }

    override fun fetchMoviesByGenre(
        slug: String,
        page: Int
    ): Flow<Resource<List<Movie>>> {
        return executeRequest(
            errorMessage = "Không thể tải phim theo thể loại"
        ) {
            movieApiService.fetchMoviesByGenre(
                slug = slug,
                page = page
            )
        }
    }

    override fun fetchMoviesByCountry(
        slug: String,
        page: Int
    ): Flow<Resource<List<Movie>>> {
        return executeRequest(
            errorMessage = "Không thể tải phim theo quốc gia"
        ) {
            movieApiService.fetchMoviesByCountry(
                slug = slug,
                page = page
            )
        }
    }

    override fun fetchMoviesByYear(
        year: Int,
        page: Int
    ): Flow<Resource<List<Movie>>> {
        return executeRequest(
            errorMessage = "Không thể tải phim theo năm phát hành"
        ) {
            movieApiService.fetchMoviesByYear(
                year = year,
                page = page
            )
        }
    }

    override fun searchMovies(
        keyword: String
    ): Flow<Resource<List<Movie>>> {
        return executeRequest(
            errorMessage = "Không thể tìm kiếm phim"
        ) {
            movieApiService.searchMovies(keyword)
        }
    }

    private fun executeRequest(
        errorMessage: String,
        request: suspend () -> MovieDto
    ): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading)

            try {
                val movieDto = request()
                val movies = movieApiMapper.mapToDomain(movieDto)

                emit(Resource.Success(movies))
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                emit(
                    Resource.Error(
                        throwable = exception,
                        message = exception.message ?: errorMessage
                    )
                )
            }
        }
    }
}