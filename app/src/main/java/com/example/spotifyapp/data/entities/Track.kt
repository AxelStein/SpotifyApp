package com.example.spotifyapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class Track(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    var title: String = "",

    var artists: String = "",

    var externalUrl: String = "",

    var threadNumber: Int = 0,
)