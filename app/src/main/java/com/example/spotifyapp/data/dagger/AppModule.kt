package com.example.spotifyapp.data.dagger

import android.content.Context
import androidx.room.Room
import com.example.spotifyapp.data.room.AppDatabase
import com.example.spotifyapp.data.room.TrackDao
import com.example.spotifyapp.data.spotify_api.SpotifyApi
import com.example.spotifyapp.data.spotify_api.TrackListDeserializer
import com.example.spotifyapp.data.spotify_api.TrackListSearchResult
import com.example.spotifyapp.domain.SearchTracksUseCase
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import io.reactivex.internal.functions.Functions
import io.reactivex.plugins.RxJavaPlugins
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {
    @Provides
    @Singleton
    fun provideDb(): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            context.packageName
        ).build()
    }

    @Provides
    @Singleton
    fun provideTrackDao(db: AppDatabase): TrackDao {
        return db.trackDao()
    }

    @Provides
    @Singleton
    fun provideSpotifyApi(): SpotifyApi {
        RxJavaPlugins.setErrorHandler(Functions.emptyConsumer())

        val client = OkHttpClient.Builder().addInterceptor(Interceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $spotifyToken")
                .build()
            chain.proceed(newRequest)
        }).build()

        val gson = GsonBuilder()
            .registerTypeAdapter(TrackListSearchResult::class.java, TrackListDeserializer())
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
        return retrofit.create(SpotifyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSearchTrackUseCase(api: SpotifyApi, dao: TrackDao): SearchTracksUseCase {
        return SearchTracksUseCase(api, dao)
    }
}