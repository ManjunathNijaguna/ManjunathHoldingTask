package com.example.manjunathtask.di

import com.example.manjunathtask.ui.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, RepositoryModule::class, ViewModelModule::class,
    UseCaseModule::class, StorageModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
}

