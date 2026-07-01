package com.example.cahu_movie.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cahu_movie.ui.components.MovieCard
import com.example.cahu_movie.back_end.domain.models.Movie
import androidx.compose.foundation.layout.fillMaxWidth
@Composable
fun MovieGrid(
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
    // Nội dung chiếm trọn 1 hàng phía trên grid (vd: BannerSlider).
    // Nội dung này nằm CHUNG trong danh sách cuộn nên sẽ trôi theo khi scroll,
    // không bị "dính cứng" ở trên đầu màn hình.
    header: (@Composable () -> Unit)? = null
) {
    if (movies.isEmpty() && header == null) {
        Box(
            modifier = Modifier.fillMaxWidth(),
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
        modifier = modifier,
        contentPadding = PaddingValues(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (header != null) {
            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                header()
            }
        }

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