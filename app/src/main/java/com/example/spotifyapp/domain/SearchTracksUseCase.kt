package com.example.spotifyapp.domain

import android.util.Log
import com.example.spotifyapp.data.entities.Track
import com.example.spotifyapp.data.room.TrackDao
import com.example.spotifyapp.data.spotify_api.SpotifyApi
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.LinkedBlockingQueue

class SearchTracksUseCase(
    private val spotifyApi: SpotifyApi,
    private val trackDao: TrackDao
    ) {
    private val offsetQueue = LinkedBlockingQueue<Int>()

    fun search(query: String): Single<List<Track>> {
        offsetQueue.clear()
        for (i in 0..30 step 10) {
            offsetQueue.add(i)
        }
        return searchImpl(query, 1).zipWith(searchImpl(query, 2)) { type1, type2 ->
            type1 + type2
        }.doAfterSuccess {
            trackDao.deleteAll()
            trackDao.insert(it)
        }
    }

    private fun searchImpl(query: String, threadNumber: Int): Single<List<Track>> {
        return Single.fromCallable {
            val result = mutableListOf<Track>()
            while (offsetQueue.isNotEmpty()) {
                queryFromApi(query, offsetQueue.take()).onEach {
                    it.threadNumber = threadNumber
                }.also {
                    result.addAll(it)
                }
            }
            result.toList()
        }.subscribeOn(Schedulers.single())
    }

    private fun queryFromApi(query: String, offset: Int): List<Track> {
        Log.e("TAG", "queryFromApi $query offset=$offset")
        val response = spotifyApi.search(query, offset).execute()
        if (response.isSuccessful) {
            return response.body()!!.items
        } else {
            throw Exception(response.errorBody()!!.string())
        }
    }
}