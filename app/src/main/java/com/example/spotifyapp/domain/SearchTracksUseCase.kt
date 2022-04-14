package com.example.spotifyapp.domain

import com.example.spotifyapp.data.entities.Track
import com.example.spotifyapp.data.room.TrackDao
import com.example.spotifyapp.data.spotify_api.SpotifyApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors

class SearchTracksUseCase(
    private val spotifyApi: SpotifyApi,
    private val trackDao: TrackDao
    ) {
    private var threadPool = Executors.newFixedThreadPool(2)

    fun search(query: String): Observable<List<Track>> {
        val offsetQueue = mutableListOf<Int>()
        for (i in 0..30 step 10) {
            offsetQueue.add(i)
        }

        val result = mutableListOf<Track>()
        return Observable.fromIterable(offsetQueue)
            .flatMap({ fetchTracks(query, it) }, 2)
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

    private fun fetchTracks(
        query: String,
        offset: Int
    ) = Observable.fromCallable {
        val threadNumber = Thread.currentThread().id.toInt()
        val response = spotifyApi.search(query, offset).execute()
        if (response.isSuccessful) {
            response.body()!!.items.onEach { track ->
                track.threadNumber = threadNumber
            }
        } else {
            throw Exception(response.errorBody()!!.string())
        }
    }.subscribeOn(Schedulers.from(threadPool))

    fun clear() {
        threadPool.shutdown()
    }
}