package com.example.manjunathtask.domain.repository

import com.example.manjunathtask.domain.model.Holding

interface HoldingsRepository {
    suspend fun getHoldings(): List<Holding>
}
