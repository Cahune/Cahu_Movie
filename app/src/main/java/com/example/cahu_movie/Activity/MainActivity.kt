package com.example.cahu_movie.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.cahu_movie.back_end.domain.models.Movie
import com.example.cahu_movie.ui.home.HomeUiState
import com.example.cahu_movie.ui.home.HomeViewModel
import com.example.cahu_movie.ui.theme.Cahu_MovieTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            Cahu_MovieTheme {
                val uiState by homeViewModel.uiState.collectAsState()

                MovieHomeScreen(
                    uiState = uiState,
                    onRetry = homeViewModel::loadLatestMovies,
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

@Composable
private fun MovieHomeScreen(
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
            HomeHeader()

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

@Composable
private fun HomeHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF11111A))
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 48.dp,
                bottom = 16.dp
            )
    ) {
        Text(
            text = "Cahu Movie",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Phim mới cập nhật",
            color = Color(0xFFAAAAAF),
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFFFF4D8D)
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )

            Button(
                onClick = onRetry,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Thử lại")
            }
        }
    }
}

@Composable
private fun MovieGrid(
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit
) {
    if (movies.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Chưa có phim",
                color = Color.White
            )
        }

        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = movies,
            key = { movie -> movie.slug }
        ) { movie ->
            MovieCard(
                movie = movie,
                onClick = {
                    onMovieClick(movie)
                }
            )
        }
    }
}

@Composable
private fun MovieCard(
    movie: Movie,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF191922)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            AsyncImage(
                model = movie.posterUrl.ifBlank {
                    movie.thumbUrl
                },
                contentDescription = movie.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .clip(
                        RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = movie.name.ifBlank {
                        "Chưa rõ tên phim"
                    },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = movie.currentEpisode.ifBlank {
                            "Đang cập nhật"
                        },
                        color = Color(0xFFFF6B9E),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = movie.quality,
                        color = Color(0xFF6EE7B7),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(34.dp)
                    )
                }

                Text(
                    text = movie.time,
                    color = Color(0xFF9A9AA2),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
        }
    }
}