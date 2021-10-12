package com.example.finalproject

import android.app.Application
import com.example.finalproject.di.networkModule
import com.example.finalproject.di.repositoryModule
import com.example.finalproject.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Application)
            modules(networkModule, repositoryModule, viewModelModule)
        }
    }
}