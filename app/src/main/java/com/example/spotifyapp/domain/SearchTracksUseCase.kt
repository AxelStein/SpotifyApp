package com.example.spotifyapp.domain

import com.example.spotifyapp.data.entities.Track
import com.example.spotifyapp.data.room.TrackDao
import com.example.spotifyapp.data.spotify_api.SpotifyApi
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.schedulers.Schedulers.single

class SearchTracksUseCase(
    private val spotifyApi: SpotifyApi,
    private val trackDao: TrackDao
    ) {
    fun search(query: String, offset: Int): Single<List<Track>> {
        return queryImpl(query, offset).subscribeOn(io()).doOnSuccess {
            trackDao.deleteAll()
            trackDao.insert(it)
        }
    }

    private fun queryImpl(query: String, offset: Int): Single<List<Track>> {
        return Single.fromCallable {
            val response = spotifyApi.search(query, offset).execute()
            if (response.isSuccessful) {
                return@fromCallable response.body()!!.items
            } else {
                throw Exception()
            }
        }.subscribeOn(single())
    }
}