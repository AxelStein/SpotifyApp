package com.example.spotifyapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class Track(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val title: String = "",

    val artists: String = "",

    val externalUrl: String = "",
)