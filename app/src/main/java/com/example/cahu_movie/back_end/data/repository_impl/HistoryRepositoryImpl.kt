package com.example.cahu_movie.back_end.data.repository_impl

import android.content.Context
import com.example.cahu_movie.back_end.domain.models.WatchHistory
import com.example.cahu_movie.back_end.domain.repository.HistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryRepositoryImpl(
    private val context: Context
) : HistoryRepository {

    companion object {

        private const val FILE_NAME = "history.json"

    }

    private val gson = Gson()

    override suspend fun saveHistory(
        history: WatchHistory
    ) {

        val list = getHistory().toMutableList()

        list.removeAll {

            it.slug == history.slug &&
                    it.episodeSlug == history.episodeSlug

        }

        list.add(0, history)

        writeFile(list)
    }

    override suspend fun getHistory(): List<WatchHistory> {

        return try {

            val json = context
                .openFileInput(FILE_NAME)
                .bufferedReader()
                .use {
                    it.readText()
                }

            val type = object :
                TypeToken<List<WatchHistory>>() {}.type

            gson.fromJson(json, type) ?: emptyList()

        } catch (e: Exception) {

            emptyList()

        }

    }

    override suspend fun deleteHistory(
        slug: String
    ) {

        val list = getHistory()
            .filter {
                it.slug != slug
            }

        writeFile(list)

    }

    override suspend fun clearHistory() {

        writeFile(emptyList())

    }

    private fun writeFile(
        list: List<WatchHistory>
    ) {

        val json = gson.toJson(list)

        context
            .openFileOutput(
                FILE_NAME,
                Context.MODE_PRIVATE
            )
            .bufferedWriter()
            .use {

                it.write(json)

            }

    }

}