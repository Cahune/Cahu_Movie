package com.example.cahu_movie.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cahu_movie.back_end.domain.models.Movie
import com.example.cahu_movie.back_end.domain.repository.MovieRepository
import com.example.cahu_movie.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

sealed interface HomeUiState {

    object Loading : HomeUiState

    data class Success(
        val movies: List<Movie>,
        val bannerMovies: List<Movie>
    ) : HomeUiState

    data class Error(
        val message: String
    ) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<HomeUiState>(HomeUiState.Loading)

    val uiState: StateFlow<HomeUiState> =
        _uiState.asStateFlow()

    private var moviesJob: Job? = null

    init {
        loadLatestMovies()
    }

    fun loadLatestMovies(page: Int = 1) {
        moviesJob?.cancel()
        moviesJob = viewModelScope.launch {
            movieRepository.fetchLatestMovies(page).collect { result ->
                when (result) {
                    Resource.Loading -> {
                        _uiState.value = HomeUiState.Loading
                    }

                    is Resource.Success -> {
                        _uiState.value = HomeUiState.Success(
                            movies = result.data,
                            bannerMovies = result.data.take(5)
                        )
                    }

                    is Resource.Error -> {
                        _uiState.value = HomeUiState.Error(
                            message = result.message
                                ?: "Không thể tải danh sách phim"
                        )
                    }
                }
            }
        }
    }

    fun searchMovies(keyword: String) {
        val trimmedKeyword = keyword.trim()

        if (trimmedKeyword.isBlank()) {
            loadLatestMovies()
            return
        }

        moviesJob?.cancel()
        moviesJob = viewModelScope.launch {
            movieRepository.searchMovies(trimmedKeyword).collect { result ->
                when (result) {
                    Resource.Loading -> {
                        _uiState.value = HomeUiState.Loading
                    }

                    is Resource.Success -> {
                        _uiState.value = HomeUiState.Success(
                            movies = result.data,
                            bannerMovies = emptyList()
                        )
                    }

                    is Resource.Error -> {
                        _uiState.value = HomeUiState.Error(
                            message = result.message
                                ?: "Khong the tim kiem phim"
                        )
                    }
                }
            }
        }
    }
}
