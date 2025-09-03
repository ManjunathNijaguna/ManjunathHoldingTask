package com.example.manjunathtask.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.manjunathtask.domain.repository.HoldingsRepository
import com.example.manjunathtask.domain.usecase.GetHoldingsUseCase
import com.example.manjunathtask.ui.viewmodel.HoldingsViewModel
import dagger.Module
import dagger.Provides

@Module
class ViewModelModule {

    @Provides
    fun provideViewModelFactory(repo: GetHoldingsUseCase): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(HoldingsViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return HoldingsViewModel(repo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
            }
        }
    }
}
