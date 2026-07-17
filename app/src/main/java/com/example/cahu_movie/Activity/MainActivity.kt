package com.example.cahu_movie.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.cahu_movie.ui.home.HomeScreen
import com.example.cahu_movie.ui.home.HomeViewModel
import com.example.cahu_movie.ui.theme.Cahu_MovieTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.Modifier

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            Cahu_MovieTheme {

                val uiState by homeViewModel.uiState.collectAsState()

                HomeScreen(
                    uiState = uiState,
                    onRetry = homeViewModel::loadLatestMovies,
                    onSearch = homeViewModel::searchMovies,
                    onClearSearch = homeViewModel::loadLatestMovies,
                    onFiltersSelected = homeViewModel::applyMovieFilters,
                    onMovieClick = { movie ->

                        if (movie.slug.isBlank()) {
                            Toast.makeText(
                                this,
                                "Phim này không có slug",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            startActivity(
                                MovieDetailActivity.newIntent(
                                    context = this,
                                    slug = movie.slug
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}
