package com.example.manjunathtask.domain.usecase

import com.example.manjunathtask.domain.repository.HoldingsRepository
import com.example.manjunathtask.domain.model.Holding

class GetHoldingsUseCase(
    private val repository: HoldingsRepository
) {
    suspend operator fun invoke(): List<Holding> = repository.getHoldings()
}
