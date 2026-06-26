package com.example.cahu_movie.back_end.domain.repository

import com.example.cahu_movie.back_end.domain.models.WatchHistory

interface HistoryRepository {

    suspend fun saveHistory(
        history: WatchHistory
    )

    suspend fun getHistory(): List<WatchHistory>

    suspend fun deleteHistory(
        slug: String
    )

    suspend fun clearHistory()
}