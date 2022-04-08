package com.example.spotifyapp.data.dagger

import com.example.spotifyapp.ui.track_list.TrackListViewModel
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    fun inject(vm: TrackListViewModel)
}