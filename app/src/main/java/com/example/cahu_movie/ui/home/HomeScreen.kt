package com.example.cahu_movie.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.cahu_movie.ui.components.ErrorContent
import com.example.cahu_movie.ui.components.LoadingContent
import com.example.cahu_movie.ui.components.MovieGrid
import com.example.cahu_movie.ui.components.TopBar
import com.example.cahu_movie.back_end.domain.models.Movie

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onRetry: () -> Unit,
    onMovieClick: (Movie) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0B0B12)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopBar()

            when (uiState) {
                HomeUiState.Loading -> {
                    LoadingContent()
                }

                is HomeUiState.Error -> {
                    ErrorContent(
                        message = uiState.message,
                        onRetry = onRetry
                    )
                }

                is HomeUiState.Success -> {
                    MovieGrid(
                        movies = uiState.movies,
                        onMovieClick = onMovieClick
                    )
                }
            }
        }
    }
}