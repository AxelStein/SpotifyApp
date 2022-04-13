package com.example.spotifyapp.domain

import com.example.spotifyapp.data.entities.Track
import com.example.spotifyapp.data.room.TrackDao
import com.example.spotifyapp.data.spotify_api.SpotifyApi
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

class SearchTracksUseCase(
    private val spotifyApi: SpotifyApi,
    private val trackDao: TrackDao
    ) {
    private var threadPool = Executors.newFixedThreadPool(2)

    fun search(query: String): Flowable<List<Track>> {
        val offsetQueue = LinkedBlockingQueue<Int>()
        for (i in 0..30 step 10) {
            offsetQueue.add(i)
        }
        val result = mutableListOf<Track>()
        val t1 = createThread(query, offsetQueue)
        val t2 = createThread(query, offsetQueue)
        return Flowable.merge(t1, t2)
            .doOnComplete {
                if (result.isNotEmpty()) {
                    trackDao.deleteAll()
                    trackDao.insert(result)
                }
            }
            .doOnNext {
                result.addAll(it)
            }
    }

    private fun createThread(
        query: String,
        offsetQueue: LinkedBlockingQueue<Int>
    ): Flowable<List<Track>> {
        return Flowable.fromCallable {
            searchSpotifyApi(query, offsetQueue.take())
        }
            .timeout(3, TimeUnit.SECONDS, Flowable.empty())
            .repeatUntil { offsetQueue.isEmpty() }
            .subscribeOn(Schedulers.from(threadPool))
    }

    private fun searchSpotifyApi(
        query: String,
        offset: Int
    ): List<Track> {
        val threadNumber = Thread.currentThread().id.toInt()
        val response = spotifyApi.search(query, offset).execute()
        if (response.isSuccessful) {
            return response.body()!!.items.onEach { track ->
                track.threadNumber = threadNumber
            }
        } else {
            throw Exception(response.errorBody()!!.string())
        }
    }

    fun clear() {
        threadPool.shutdown()
    }
}