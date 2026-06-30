package com.example.cahu_movie.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import coil3.compose.AsyncImage
import com.example.cahu_movie.back_end.domain.models.Movie

@Composable
fun BannerSlider(
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit
) {

    if (movies.isEmpty()) return

    val pagerState = rememberPagerState(
        pageCount = { movies.size }
    )

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val bannerHeight = screenHeight / 2

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(bannerHeight)
    ) {

        // Slider chiếm toàn bộ chiều rộng, không padding, không hé lộ slide bên cạnh
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->

            BannerCard(
                movie = movies[page],
                onClick = {
                    onMovieClick(movies[page])
                }
            )

        }

        // Lớp phủ chứa nút "Xem ngay" + chấm chỉ báo, cố định, không trượt theo slider
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Button(
                onClick = {
                    onMovieClick(movies[pagerState.currentPage])
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("▶ Xem ngay")
            }

            PageIndicator(
                pageCount = movies.size,
                currentPage = pagerState.currentPage
            )

        }

    }

}

@Composable
private fun PageIndicator(
    pageCount: Int,
    currentPage: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == currentPage) 9.dp else 7.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == currentPage) {
                            Color.White
                        } else {
                            Color.White.copy(alpha = 0.4f)
                        }
                    )
            )
        }
    }
}

@Composable
private fun BannerCard(
    movie: Movie,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                onClick()
            }
    ) {

        AsyncImage(
            model = movie.thumbUrl.ifBlank {
                movie.posterUrl
            },
            contentDescription = movie.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 18.dp, end = 18.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = movie.name,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = movie.currentEpisode.ifBlank {
                    "Đang cập nhật"
                },
                color = Color.LightGray,
                fontSize = 14.sp
            )

        }

    }

}