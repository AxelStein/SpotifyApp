package com.example.spotifyapp.domain

import com.example.spotifyapp.data.entities.Track
import com.example.spotifyapp.data.room.TrackDao
import com.example.spotifyapp.data.spotify_api.SpotifyApi
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers.io
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
        return createThread(query, offsetQueue, 1)
            .zipWith(createThread(query, offsetQueue, 2)) { l1, l2 ->
                l1+l2
            }.doOnComplete {
                trackDao.deleteAll()
                trackDao.insert(result)
            }
            .doOnNext {
                result.addAll(it)
            }
    }

    private fun createThread(query: String, offsetQueue: LinkedBlockingQueue<Int>, threadNumber: Int): Flowable<List<Track>> {
        return Single.fromCallable {
            searchSpotifyApi(query, offsetQueue.take(), threadNumber)
        }.repeatUntil { offsetQueue.isEmpty() }.subscribeOn(io())
    }

    private fun searchSpotifyApi(query: String, offset: Int, threadNumber: Int): List<Track> {
        val response = spotifyApi.search(query, offset).execute()
        if (response.isSuccessful) {
            return response.body()!!.items.onEach { track ->
                track.threadNumber = threadNumber
            }
        } else {
            throw Exception(response.errorBody()!!.string())
        }
    }
}