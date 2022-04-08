package com.example.spotifyapp.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.spotifyapp.data.Track
import io.reactivex.Single

@Dao
interface TrackDao {
    @Insert
    fun save(list: List<Track>)

    @Query("select * from tracks")
    fun getAll(): Single<List<Track>>

    @Query("delete from tracks")
    fun deleteAll()
}