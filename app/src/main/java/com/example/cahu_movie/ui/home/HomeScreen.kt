package com.example.cahu_movie.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cahu_movie.ui.components.ErrorContent
import com.example.cahu_movie.ui.components.LoadingContent
import com.example.cahu_movie.ui.components.MovieGrid
import com.example.cahu_movie.ui.components.TopBar
import com.example.cahu_movie.ui.components.BannerSlider
import com.example.cahu_movie.back_end.domain.models.Movie
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.rememberDrawerState
import com.example.cahu_movie.ui.components.AppSidebar
import com.example.cahu_movie.ui.components.MovieFilterDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(

    uiState: HomeUiState,
    onRetry: () -> Unit,
    onSearch: (String) -> Unit,
    onClearSearch: () -> Unit,
    onFiltersSelected: (List<MovieFilterOption>) -> Unit,
    onMovieClick: (Movie) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedSidebarItem by remember { mutableStateOf("Trang chu") }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedFilters by remember {
        mutableStateOf(emptyList<MovieFilterOption>())
    }

    LaunchedEffect(
        isSearchActive,
        searchQuery
    ) {
        if (!isSearchActive) {
            return@LaunchedEffect
        }

        delay(400)

        if (searchQuery.isBlank()) {
            onClearSearch()
        } else {
            onSearch(searchQuery)
        }
    }

    if (showFilterDialog) {
        MovieFilterDialog(
            selectedFilters = selectedFilters,
            onApplyFilters = { filters ->
                selectedFilters = filters
                showFilterDialog = false
                onFiltersSelected(filters)
            },
            onDismiss = {
                showFilterDialog = false
            }
        )
    }

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
                        LoadingContent(
                            modifier = Modifier
                                .weight(1f)
                                .imePadding()
                        )
                    }

                    is HomeUiState.Error -> {
                        ErrorContent(
                            message = uiState.message,
                            onRetry = onRetry,
                            modifier = Modifier
                                .weight(1f)
                                .imePadding()
                        )
                    }

                    is HomeUiState.Success -> {

                        MovieGrid(
                            movies = uiState.movies,
                            onMovieClick = onMovieClick,
                            modifier = Modifier
                                .weight(1f)
                                .imePadding(),
                            header = if (!isSearchActive) {
                                {
                                    Column {
                                        if (uiState.bannerMovies.isNotEmpty()) {
                                            BannerSlider(
                                                movies = uiState.bannerMovies,
                                                onMovieClick = onMovieClick
                                            )
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                showFilterDialog = true
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults
                                                .outlinedButtonColors(
                                                    contentColor = Color.White
                                                ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(
                                                    top = 12.dp
                                                )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.FilterList,
                                                contentDescription = "Lọc phim"
                                            )
                                            Text(
                                                text = selectedFilters.filterTitle(),
                                                modifier = Modifier.padding(
                                                    start = 8.dp
                                                )
                                            )
                                        }
                                    }
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

private fun List<MovieFilterOption>.filterTitle(): String {
    return when (size) {
        0 -> "Lọc phim"
        1 -> first().title
        else -> "${first().title} +${size - 1}"
    }
}
