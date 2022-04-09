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
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

class TrackListViewModel : ViewModel() {
    @Inject
    lateinit var searchTracksUseCase: SearchTracksUseCase

    @Inject
    lateinit var trackDao: TrackDao

    private val trackList = MutableLiveData<List<Track>>()
    val trackListLiveData = trackList as LiveData<List<Track>>

    private val loading = MutableLiveData<Boolean>()
    val loadingLiveData = loading as LiveData<Boolean>

    private val disposables = CompositeDisposable()
    private var searchDisposable: Disposable? = null

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
        searchDisposable?.run {
            if (!isDisposed) {
                dispose()
            }
        }

        searchTracksUseCase.search(query)
            .doOnSubscribe {
                loading.postValue(true)
            }
            .doFinally {
                searchDisposable = null
                loading.postValue(false)
            }
            .subscribe({}, {
                it.printStackTrace()
            }).also {
                searchDisposable = it
                disposables.add(it)
            }
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}