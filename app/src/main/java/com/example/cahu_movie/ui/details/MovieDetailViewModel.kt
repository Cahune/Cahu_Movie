package com.example.cahu_movie.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cahu_movie.back_end.data.remote.api.MovieApiService
import com.example.cahu_movie.back_end.data.remote.models.MovieDetailDto
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface MovieDetailUiState {

    data object Loading : MovieDetailUiState

    data class Success(
        val response: MovieDetailDto
    ) : MovieDetailUiState

    data class Error(
        val message: String
    ) : MovieDetailUiState
}

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val movieApiService: MovieApiService
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<MovieDetailUiState>(
            MovieDetailUiState.Loading
        )

    val uiState: StateFlow<MovieDetailUiState> =
        _uiState.asStateFlow()

    private var currentSlug: String = ""

    fun loadMovie(slug: String) {
        if (slug.isBlank()) {
            _uiState.value = MovieDetailUiState.Error(
                "Slug của phim không hợp lệ"
            )
            return
        }

        currentSlug = slug

        viewModelScope.launch {
            _uiState.value = MovieDetailUiState.Loading

            try {
                val response =
                    movieApiService.fetchMovieDetail(slug)

                if (response.movie == null) {
                    _uiState.value = MovieDetailUiState.Error(
                        "API không trả về thông tin phim"
                    )
                } else {
                    _uiState.value =
                        MovieDetailUiState.Success(response)
                }
            } catch (exception: Exception) {
                _uiState.value = MovieDetailUiState.Error(
                    exception.message
                        ?: "Không thể tải thông tin phim"
                )
            }
        }
    }

    fun retry() {
        if (currentSlug.isNotBlank()) {
            loadMovie(currentSlug)
        }
    }
}