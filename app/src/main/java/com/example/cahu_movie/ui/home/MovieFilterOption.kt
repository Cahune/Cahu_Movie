package com.example.cahu_movie.ui.home

enum class MovieFilterType {
    LATEST,
    CATEGORY,
    GENRE,
    COUNTRY,
    YEAR
}

data class MovieFilterOption(
    val title: String,
    val type: MovieFilterType,
    val value: String = ""
)

val movieFilterGroups: List<Pair<String, List<MovieFilterOption>>> =
    listOf(
        "Nhanh" to listOf(
            MovieFilterOption(
                title = "Tất cả phim mới",
                type = MovieFilterType.LATEST
            )
        ),
        "Danh mục" to listOf(
            MovieFilterOption("Phim bộ", MovieFilterType.CATEGORY, "phim-bo"),
            MovieFilterOption("Phim lẻ", MovieFilterType.CATEGORY, "phim-le"),
            MovieFilterOption("TV Shows", MovieFilterType.CATEGORY, "tv-shows"),
            MovieFilterOption("Hoạt hình", MovieFilterType.CATEGORY, "hoat-hinh")
        ),
        "Thể loại" to listOf(
            MovieFilterOption("Hành động", MovieFilterType.GENRE, "hanh-dong"),
            MovieFilterOption("Tình cảm", MovieFilterType.GENRE, "tinh-cam"),
            MovieFilterOption("Hài hước", MovieFilterType.GENRE, "hai-huoc"),
            MovieFilterOption("Kinh dị", MovieFilterType.GENRE, "kinh-di")
        ),
        "Quốc gia" to listOf(
            MovieFilterOption("Hàn Quốc", MovieFilterType.COUNTRY, "han-quoc"),
            MovieFilterOption("Trung Quốc", MovieFilterType.COUNTRY, "trung-quoc"),
            MovieFilterOption("Âu Mỹ", MovieFilterType.COUNTRY, "au-my"),
            MovieFilterOption("Việt Nam", MovieFilterType.COUNTRY, "viet-nam")
        ),
        "Năm" to listOf(
            MovieFilterOption("2026", MovieFilterType.YEAR, "2026"),
            MovieFilterOption("2025", MovieFilterType.YEAR, "2025"),
            MovieFilterOption("2024", MovieFilterType.YEAR, "2024"),
            MovieFilterOption("2023", MovieFilterType.YEAR, "2023")
        )
    )
