package com.example.cahu_movie.utils

object K {

    const val BASE_URL = "https://phim.nguonc.com/"

    // Endpoint lấy danh sách phim
    const val LATEST_MOVIES_ENDPOINT =
        "api/films/phim-moi-cap-nhat"

    const val CATEGORY_MOVIES_ENDPOINT =
        "api/films/danh-sach/{slug}"

    const val GENRE_MOVIES_ENDPOINT =
        "api/films/the-loai/{slug}"

    const val COUNTRY_MOVIES_ENDPOINT =
        "api/films/quoc-gia/{slug}"

    const val YEAR_MOVIES_ENDPOINT =
        "api/films/nam-phat-hanh/{year}"

    const val SEARCH_MOVIES_ENDPOINT =
        "api/films/search"

    // Endpoint chi tiết phim và danh sách tập
    const val MOVIE_DETAIL_ENDPOINT =
        "api/film/{slug}"

    // Tên tham số Retrofit
    const val PAGE = "page"
    const val SLUG = "slug"
    const val YEAR = "year"
    const val KEYWORD = "keyword"

    // Một số slug danh mục thường dùng
    const val NOW_PLAYING_CATEGORY = "phim-dang-chieu"
}