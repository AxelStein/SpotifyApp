package com.example.spotifyapp.ui

import android.app.Application
import com.example.spotifyapp.data.dagger.AppComponent
import com.example.spotifyapp.data.dagger.AppModule
import com.example.spotifyapp.data.dagger.DaggerAppComponent

class App : Application() {
    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}