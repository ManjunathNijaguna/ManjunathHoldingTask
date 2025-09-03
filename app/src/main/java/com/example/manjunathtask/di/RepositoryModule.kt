package com.example.manjunathtask.di

import android.content.SharedPreferences
import com.example.manjunathtask.data.api.ApiService
import com.example.manjunathtask.data.repository.HoldingsRepositoryImpl
import com.example.manjunathtask.domain.repository.HoldingsRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Provides
    @Singleton
    fun provideHoldingsRepository(
        api: ApiService,
        prefs: SharedPreferences,
        gson: Gson
    ): HoldingsRepository {
        return HoldingsRepositoryImpl(api, prefs, gson)
    }
}

