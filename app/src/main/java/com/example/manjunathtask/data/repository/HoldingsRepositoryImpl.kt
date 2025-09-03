package com.example.manjunathtask.data.repository

import android.content.SharedPreferences
import com.example.manjunathtask.data.api.ApiService
import com.example.manjunathtask.domain.repository.HoldingsRepository
import com.example.manjunathtask.domain.model.Holding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HoldingsRepositoryImpl(
    private val api: ApiService,
    private val prefs: SharedPreferences,
    private val gson: Gson
) : HoldingsRepository {

    companion object {
        private const val CACHE_KEY = "holdings_cache"
    }

    override suspend fun getHoldings(): List<Holding> {
        return try {
            val response = api.getHoldings()
            if (response.isSuccessful) {
                val holdings = response.body()?.data?.userHolding?.map {
                    Holding(
                        symbol = it.symbol,
                        ltp = it.ltp,
                        avgPrice = it.avgPrice,
                        quantity = it.quantity,
                        close = it.close
                    )
                } ?: emptyList()

                // Save to cache
                prefs.edit().putString(CACHE_KEY, gson.toJson(holdings)).apply()
                holdings
            } else {
                loadFromCache()
            }
        } catch (e: Exception) {
            loadFromCache()
        }
    }

    private fun loadFromCache(): List<Holding> {
        val json = prefs.getString(CACHE_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<Holding>>() {}.type
        return gson.fromJson(json, type)
    }
}
