package com.example.cahu_movie.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import coil3.compose.AsyncImage
import com.example.cahu_movie.back_end.data.remote.models.EpisodeItem
import com.example.cahu_movie.back_end.data.remote.models.EpisodeServer
import com.example.cahu_movie.back_end.data.remote.models.MovieDetailDto
import com.example.cahu_movie.back_end.data.remote.models.MovieDetailItem
import com.example.cahu_movie.ui.detail.MovieDetailUiState
import com.example.cahu_movie.ui.detail.MovieDetailViewModel
import com.example.cahu_movie.ui.theme.Cahu_MovieTheme
import dagger.hilt.android.AndroidEntryPoint

private val DetailBackground = Color(0xFF0D0B14)
private val DetailSurface = Color(0xFF17131F)
private val DetailPink = Color(0xFFFF4081)
private val DetailTextSecondary = Color(0xFFB8B1C2)

@AndroidEntryPoint
class MovieDetailActivity : BaseActivity() {

    private val viewModel: MovieDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val movieSlug = intent
            .getStringExtra(EXTRA_MOVIE_SLUG)
            .orEmpty()
            .trim()

        if (movieSlug.isBlank()) {
            Toast.makeText(
                this,
                "Không tìm thấy thông tin phim",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        viewModel.loadMovie(movieSlug)

        setContent {
            Cahu_MovieTheme {
                val uiState by viewModel.uiState.collectAsState()

                MovieDetailScreen(
                    uiState = uiState,
                    onBackClick = {
                        finish()
                    },
                    onRetry = {
                        viewModel.retry()
                    },
                    onEpisodeClick = { episode ->
                        openEpisode(episode)
                    }
                )
            }
        }
    }

    private fun openEpisode(
        episode: EpisodeItem
    ) {
        val embedUrl = episode.embed
            ?.trim()
            .orEmpty()

        if (embedUrl.isBlank()) {
            Toast.makeText(
                this,
                "Tập này chưa có đường dẫn phát",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        startActivity(
            PlayerActivity.newIntent(
                context = this,
                embedUrl = embedUrl,
                episodeName = episodeDisplayName(episode)
            )
        )
    }

    companion object {

        private const val EXTRA_MOVIE_SLUG =
            "extra_movie_slug"

        fun newIntent(
            context: Context,
            slug: String
        ): Intent {
            return Intent(
                context,
                MovieDetailActivity::class.java
            ).apply {
                putExtra(
                    EXTRA_MOVIE_SLUG,
                    slug
                )
            }
        }
    }
}

@Composable
private fun MovieDetailScreen(
    uiState: MovieDetailUiState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onEpisodeClick: (EpisodeItem) -> Unit
) {
    when (uiState) {
        MovieDetailUiState.Loading -> {
            DetailLoadingContent()
        }

        is MovieDetailUiState.Error -> {
            DetailErrorContent(
                message = uiState.message,
                onBackClick = onBackClick,
                onRetry = onRetry
            )
        }

        is MovieDetailUiState.Success -> {
            MovieDetailContent(
                response = uiState.response,
                onBackClick = onBackClick,
                onEpisodeClick = onEpisodeClick
            )
        }
    }
}

@Composable
private fun DetailLoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DetailBackground),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = DetailPink
        )
    }
}

@Composable
private fun DetailErrorContent(
    message: String,
    onBackClick: () -> Unit,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DetailBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = message.ifBlank {
                    "Không thể tải thông tin phim"
                },
                color = Color.White,
                fontSize = 16.sp
            )

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DetailPink
                )
            ) {
                Text("Thử lại")
            }

            TextButton(
                onClick = onBackClick
            ) {
                Text(
                    text = "Quay lại",
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun MovieDetailContent(
    response: MovieDetailDto,
    onBackClick: () -> Unit,
    onEpisodeClick: (EpisodeItem) -> Unit
) {
    val movie = response.movie

    if (movie == null) {
        DetailErrorContent(
            message = "Không tìm thấy dữ liệu phim",
            onBackClick = onBackClick,
            onRetry = {}
        )

        return
    }

    val episodeServers = movie.episodes
        .orEmpty()
        .filter { server ->
            server.items.orEmpty().isNotEmpty()
        }
        .sortedBy { server ->
            serverPriority(
                server.serverName.orEmpty()
            )
        }

    var showEpisodes by rememberSaveable(
        movie.slug
    ) {
        mutableStateOf(false)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DetailBackground)
    ) {
        item {
            MoviePosterHeader(
                movie = movie,
                onBackClick = onBackClick
            )
        }

        item {
            Button(
                onClick = {
                    showEpisodes = !showEpisodes
                },
                enabled = episodeServers.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DetailPink,
                    disabledContainerColor =
                        DetailPink.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 10.dp,
                        vertical = 12.dp
                    )
            ) {
                Text(
                    text = when {
                        episodeServers.isEmpty() ->
                            "Chưa có tập phim"

                        showEpisodes ->
                            "Ẩn danh sách tập"

                        else ->
                            "Xem ngay"
                    },
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (showEpisodes) {
            itemsIndexed(
                items = episodeServers
            ) { serverIndex, server ->
                EpisodeServerSection(
                    server = server,
                    serverIndex = serverIndex,
                    onEpisodeClick =
                        onEpisodeClick
                )
            }
        }

        item {
            MovieInformation(
                movie = movie
            )
        }

        item {
            Spacer(
                modifier = Modifier.height(32.dp)
            )
        }
    }
}

@Composable
private fun MoviePosterHeader(
    movie: MovieDetailItem,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Color.Black)
    ) {
        AsyncImage(
            model = movie.posterUrl
                ?: movie.thumbUrl,
            contentDescription = movie.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        TextButton(
            onClick = onBackClick,
            modifier = Modifier
                .statusBarsPadding()
                .align(Alignment.TopStart)
                .padding(4.dp)
                .background(
                    color = Color.Black.copy(
                        alpha = 0.55f
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Text(
                text = "← Quay lại",
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    Color.Black.copy(
                        alpha = 0.65f
                    )
                )
                .padding(12.dp)
        ) {
            Text(
                text = movie.name
                    ?.takeIf { it.isNotBlank() }
                    ?: "Không rõ tên phim",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow =
                    TextOverflow.Ellipsis
            )

            movie.originalName
                ?.takeIf { it.isNotBlank() }
                ?.let { originalName ->
                    Text(
                        text = originalName,
                        color = DetailTextSecondary,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow =
                            TextOverflow.Ellipsis
                    )
                }
        }
    }
}

@Composable
private fun EpisodeServerSection(
    server: EpisodeServer,
    serverIndex: Int,
    onEpisodeClick: (EpisodeItem) -> Unit
) {
    val episodes = server.items.orEmpty()

    if (episodes.isEmpty()) {
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 10.dp,
                vertical = 8.dp
            )
    ) {
        Text(
            text = server.serverName
                ?.takeIf { it.isNotBlank() }
                ?: "Máy chủ ${serverIndex + 1}",
            color = serverTitleColor(
                server.serverName.orEmpty()
            ),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                bottom = 10.dp
            )
        )

        episodes
            .chunked(3)
            .forEach { episodeRow ->
                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(7.dp)
                ) {
                    episodeRow.forEach { episode ->
                        OutlinedButton(
                            onClick = {
                                onEpisodeClick(episode)
                            },
                            shape = RoundedCornerShape(
                                8.dp
                            ),
                            colors =
                                ButtonDefaults
                                    .outlinedButtonColors(
                                        contentColor =
                                            Color.White
                                    ),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                        ) {
                            Text(
                                text =
                                    episodeButtonName(
                                        episode
                                    ),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow =
                                    TextOverflow.Ellipsis
                            )
                        }
                    }

                    repeat(
                        3 - episodeRow.size
                    ) {
                        Spacer(
                            modifier =
                                Modifier.weight(1f)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(7.dp)
                )
            }
    }
}

@Composable
private fun MovieInformation(
    movie: MovieDetailItem
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 10.dp,
                vertical = 14.dp
            )
    ) {
        Text(
            text = movie.name
                ?.takeIf { it.isNotBlank() }
                ?: "Thông tin phim",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        movie.originalName
            ?.takeIf { it.isNotBlank() }
            ?.let { originalName ->
                Text(
                    text = originalName,
                    color = DetailTextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(
                        top = 3.dp
                    )
                )
            }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        val summary = listOfNotNull(
            movie.quality
                ?.takeIf { it.isNotBlank() },
            movie.language
                ?.takeIf { it.isNotBlank() },
            movie.currentEpisode
                ?.takeIf { it.isNotBlank() },
            movie.time
                ?.takeIf { it.isNotBlank() }
        ).joinToString(" • ")

        if (summary.isNotBlank()) {
            Text(
                text = summary,
                color = DetailPink,
                fontSize = 12.sp
            )
        }

        movie.director
            ?.takeIf { it.isNotBlank() }
            ?.let { director ->
                InfoLine(
                    label = "Đạo diễn",
                    value = director
                )
            }

        movie.casts
            ?.takeIf { it.isNotBlank() }
            ?.let { casts ->
                InfoLine(
                    label = "Diễn viên",
                    value = casts
                )
            }

        val plainDescription =
            htmlToPlainText(
                movie.description
            )

        if (plainDescription.isNotBlank()) {
            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = "Nội dung",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = plainDescription,
                color = DetailTextSecondary,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                modifier = Modifier.padding(
                    top = 6.dp
                )
            )
        }
    }
}

@Composable
private fun InfoLine(
    label: String,
    value: String
) {
    Text(
        text = "$label: $value",
        color = DetailTextSecondary,
        fontSize = 13.sp,
        lineHeight = 19.sp,
        modifier = Modifier.padding(
            top = 8.dp
        )
    )
}

private fun serverPriority(
    serverName: String
): Int {
    val normalizedName =
        serverName.lowercase()

    return when {
        "vietsub" in normalizedName ||
                "phụ đề" in normalizedName -> 0

        "lồng tiếng" in normalizedName ||
                "thuyết minh" in normalizedName -> 1

        else -> 2
    }
}

private fun serverTitleColor(
    serverName: String
): Color {
    val normalizedName =
        serverName.lowercase()

    return when {
        "lồng tiếng" in normalizedName ||
                "thuyết minh" in normalizedName ->
            Color(0xFFFFC107)

        else ->
            DetailPink
    }
}

private fun episodeButtonName(
    episode: EpisodeItem
): String {
    val episodeName = episode.name
        ?.trim()
        .orEmpty()

    return if (episodeName.isBlank()) {
        "Xem tập"
    } else {
        "Tập $episodeName"
    }
}

private fun episodeDisplayName(
    episode: EpisodeItem
): String {
    val episodeName = episode.name
        ?.trim()
        .orEmpty()

    return if (episodeName.isBlank()) {
        "Đang phát phim"
    } else {
        "Tập $episodeName"
    }
}

private fun htmlToPlainText(
    html: String?
): String {
    if (html.isNullOrBlank()) {
        return ""
    }

    return HtmlCompat.fromHtml(
        html,
        HtmlCompat.FROM_HTML_MODE_LEGACY
    )
        .toString()
        .trim()
}
