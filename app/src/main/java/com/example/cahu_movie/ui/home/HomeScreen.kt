package com.example.cahu_movie.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.cahu_movie.ui.components.ErrorContent
import com.example.cahu_movie.ui.components.LoadingContent
import com.example.cahu_movie.ui.components.MovieGrid
import com.example.cahu_movie.ui.components.TopBar
import com.example.cahu_movie.ui.components.BannerSlider
import com.example.cahu_movie.back_end.domain.models.Movie
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.rememberDrawerState
import com.example.cahu_movie.ui.components.AppSidebar
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(

    uiState: HomeUiState,
    onRetry: () -> Unit,
    onSearch: (String) -> Unit,
    onClearSearch: () -> Unit,
    onMovieClick: (Movie) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedSidebarItem by remember { mutableStateOf("Trang chu") }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppSidebar(
                selectedItem = selectedSidebarItem,
                onItemClick = { item ->
                    selectedSidebarItem = item.title
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            color = Color(0xFF0B0B12)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TopBar(
                    isSearchActive = isSearchActive,
                    searchQuery = searchQuery,
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                    onSearchClick = {
                        isSearchActive = true
                    },
                    onSearchQueryChange = { query ->
                        searchQuery = query
                    },
                    onSearchSubmit = {
                        onSearch(searchQuery)
                    },
                    onCloseSearch = {
                        searchQuery = ""
                        isSearchActive = false
                        onClearSearch()
                    }
                )
//            if (uiState is HomeUiState.Success) {
//                BannerSlider(
//                    movies = uiState.bannerMovies,
//                    onMovieClick = onMovieClick
//                )
//            }
//            when (uiState) {
//                HomeUiState.Loading -> {
//                    LoadingContent()
//                }
//
//                is HomeUiState.Error -> {
//                    ErrorContent(
//                        message = uiState.message,
//                        onRetry = onRetry
//                    )
//                }
//
//                is HomeUiState.Success -> {
//                    MovieGrid(
//                        movies = uiState.movies,
//                        onMovieClick = onMovieClick
//                    )
//                }
//            }
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
                            onMovieClick = onMovieClick,
                            header = if (!isSearchActive && uiState.bannerMovies.isNotEmpty()) {
                                {
                                    BannerSlider(
                                        movies = uiState.bannerMovies,
                                        onMovieClick = onMovieClick
                                    )
                                }
                            } else {
                                null
                            }
                        )

                    }
                }
            }
        }
    }
}
