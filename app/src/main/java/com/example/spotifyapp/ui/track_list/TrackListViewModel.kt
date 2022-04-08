package com.example.spotifyapp.ui.track_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spotifyapp.data.entities.Track
import com.example.spotifyapp.data.room.TrackDao
import com.example.spotifyapp.domain.SearchTracksUseCase
import com.example.spotifyapp.ui.App
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

class TrackListViewModel : ViewModel() {
    @Inject
    lateinit var searchTracksUseCase: SearchTracksUseCase

    @Inject
    lateinit var trackDao: TrackDao

    private val trackList = MutableLiveData<List<Track>>()
    val trackListLiveData = trackList as LiveData<List<Track>>

    private val disposables = CompositeDisposable()

    init {
        App.appComponent.inject(this)

        trackDao.getAll()
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe({
                trackList.value = it
            }, {
                it.printStackTrace()
            }).also {
                disposables.add(it)
            }
    }

    fun search(query: String) {
        searchTracksUseCase.search(query, 0).subscribe()
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}