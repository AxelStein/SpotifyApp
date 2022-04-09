package com.example.spotifyapp.domain

import com.example.spotifyapp.data.entities.Track
import com.example.spotifyapp.data.room.TrackDao
import com.example.spotifyapp.data.spotify_api.SpotifyApi
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.LinkedBlockingQueue

class SearchTracksUseCase(
    private val spotifyApi: SpotifyApi,
    private val trackDao: TrackDao
    ) {
    fun search(query: String): Flowable<List<Track>> {
        val offsetQueue = LinkedBlockingQueue<Int>()
        for (i in 0..30 step 10) {
            offsetQueue.add(i)
        }

        val result = mutableListOf<Track>()
        return searchSingle(offsetQueue, query, 1).
        zipWith(searchSingle(offsetQueue, query, 2)) { list1, list2 ->
            list1 + list2
        }.repeatUntil {
            offsetQueue.isEmpty()
        }.doOnNext {
            result.addAll(it)
        }.doOnComplete {
            trackDao.deleteAll()
            trackDao.insert(result)
        }
    }

    private fun searchSingle(offsetQueue: LinkedBlockingQueue<Int>, query: String, threadNumber: Int): Single<List<Track>> {
        return Single.fromCallable {
            searchSpotifyApi(query, offsetQueue.take()).onEach {
                it.threadNumber = threadNumber
            }
        }.subscribeOn(Schedulers.single())
    }

    private fun searchSpotifyApi(query: String, offset: Int): List<Track> {
        val response = spotifyApi.search(query, offset).execute()
        if (response.isSuccessful) {
            return response.body()!!.items
        } else {
            throw Exception(response.errorBody()!!.string())
        }
    }
}