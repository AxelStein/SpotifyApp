package com.example.spotifyapp.data.dagger

import android.content.Context
import com.example.spotifyapp.data.spotify_api.SpotifyApi
import com.example.spotifyapp.data.spotify_api.TrackListDeserializer
import com.example.spotifyapp.data.spotify_api.TrackListSearchResult
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class AppModule(private val context: Context) {
    @Provides
    fun provideSpotifyApi(): SpotifyApi {
        val gson = GsonBuilder()
            .registerTypeAdapter(TrackListSearchResult::class.java, TrackListDeserializer())
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(SpotifyApi::class.java)
    }
}