package com.example.cahu_movie.utils

sealed class Resource<out T> {

    data class Success<out T>(
        val data: T
    ) : Resource<T>()

    data class Error(
        val throwable: Throwable,
        val message: String? = throwable.message
    ) : Resource<Nothing>()

    object Loading : Resource<Nothing>()
}