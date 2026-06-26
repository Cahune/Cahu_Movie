package com.example.cahu_movie.di

import com.example.cahu_movie.back_end.data.mapper_impl.MovieApiMapperImpl
import com.example.cahu_movie.back_end.data.remote.api.MovieApiService
import com.example.cahu_movie.back_end.data.remote.models.MovieDto
import com.example.cahu_movie.back_end.data.repository_impl.MovieRepositoryImpl
import com.example.cahu_movie.back_end.domain.models.Movie
import com.example.cahu_movie.back_end.domain.repository.MovieRepository
import com.example.cahu_movie.common.data.ApiMapper
import com.example.cahu_movie.utils.K
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MovieModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setFieldNamingPolicy(
                FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES
            )
            .create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(K.BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(gson)
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideMovieApiService(
        retrofit: Retrofit
    ): MovieApiService {
        return retrofit.create(MovieApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMovieApiMapper():
            ApiMapper<List<Movie>, MovieDto> {
        return MovieApiMapperImpl()
    }

    @Provides
    @Singleton
    fun provideMovieRepository(
        movieApiService: MovieApiService,
        movieApiMapper: ApiMapper<List<Movie>, MovieDto>
    ): MovieRepository {
        return MovieRepositoryImpl(
            movieApiService = movieApiService,
            movieApiMapper = movieApiMapper
        )
    }
}