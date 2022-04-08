package com.example.spotifyapp.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.spotifyapp.data.entities.Track

@Database(
    entities = [
        Track::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
}