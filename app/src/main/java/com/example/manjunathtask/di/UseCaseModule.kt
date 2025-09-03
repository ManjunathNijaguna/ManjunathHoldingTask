package com.example.manjunathtask.di

import com.example.manjunathtask.domain.repository.HoldingsRepository
import com.example.manjunathtask.domain.usecase.GetHoldingsUseCase
import dagger.Module
import dagger.Provides

@Module
class UseCaseModule {
    @Provides
    fun provideGetHoldingsUseCase(repo: HoldingsRepository): GetHoldingsUseCase {
        return GetHoldingsUseCase(repo)
    }
}
