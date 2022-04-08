package com.example.spotifyapp.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.spotifyapp.data.entities.Track
import io.reactivex.Flowable

@Dao
interface TrackDao {
    @Insert
    fun insert(list: List<Track>)

    @Query("select * from tracks")
    fun getAll(): Flowable<List<Track>>

    @Query("delete from tracks")
    fun deleteAll()
}