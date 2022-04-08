package com.example.spotifyapp.data.spotify_api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SpotifyApi {
    @GET("search?type=track&limit=10")
    fun search(
        @Query("q") query: String,
        @Query("offset") offset: Int
    ): Call<TrackListSearchResult>
}