package com.example.cahu_movie.utils

object GenreConstants {

    val genreMap: Map<String, String> = linkedMapOf(
        "hanh-dong" to "Hành Động",
        "phieu-luu" to "Phiêu Lưu",
        "hoat-hinh" to "Hoạt Hình",
        "hai" to "Hài",
        "hinh-su" to "Hình Sự",
        "chinh-kich" to "Chính Kịch",
        "kinh-di" to "Kinh Dị",
        "tinh-cam" to "Tình Cảm",
        "co-trang" to "Cổ Trang",
        "vien-tuong" to "Viễn Tưởng"
    )

    fun getGenreNameBySlug(slug: String): String {
        return genreMap[slug] ?: "Không xác định"
    }
}