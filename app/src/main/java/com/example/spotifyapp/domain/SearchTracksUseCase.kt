package com.example.spotifyapp.domain

import android.util.Log
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
    private val offsetQueue = LinkedBlockingQueue<Int>()

    fun search(query: String): Flowable<List<Track>> {
        offsetQueue.clear()
        for (i in 0..30 step 10) {
            offsetQueue.add(i)
        }

        val result = mutableListOf<Track>()
        return searchSingle(query, 1).zipWith(searchSingle(query, 2)) { list1, list2 ->
            list1 + list2
        }.repeatUntil {
            offsetQueue.isEmpty()
        }.doOnNext {
            result.addAll(it)
        }.doOnComplete {
            Log.e("TAG", "complete $result")
            trackDao.deleteAll()
            trackDao.insert(result)
        }
    }

    private fun searchSingle(query: String, threadNumber: Int): Single<List<Track>> {
        return Single.fromCallable {
            Thread.sleep(1000)
            queryFromApi(query, offsetQueue.take(), threadNumber).onEach {
                it.threadNumber = threadNumber
            }
        }.subscribeOn(Schedulers.single())
    }

    private fun queryFromApi(query: String, offset: Int, threadNumber: Int): List<Track> {
        Log.e("TAG", "queryFromApi $query offset=$offset threadNumber=$threadNumber")
        val response = spotifyApi.search(query, offset).execute()
        if (response.isSuccessful) {
            return response.body()!!.items
        } else {
            throw Exception(response.errorBody()!!.string())
        }
    }
}