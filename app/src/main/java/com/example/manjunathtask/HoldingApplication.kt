package com.example.manjunathtask

import android.app.Application
import com.example.manjunathtask.di.AppComponent
import com.example.manjunathtask.di.DaggerAppComponent
import com.example.manjunathtask.di.NetworkModule
import com.example.manjunathtask.di.RepositoryModule
import com.example.manjunathtask.di.StorageModule
import com.example.manjunathtask.di.UseCaseModule
import com.example.manjunathtask.di.ViewModelModule

class HoldingApplication : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .networkModule(NetworkModule())
            .repositoryModule(RepositoryModule())
            .useCaseModule(UseCaseModule())
            .storageModule(StorageModule(this))
            .viewModelModule(ViewModelModule())
            .build()
    }
}
