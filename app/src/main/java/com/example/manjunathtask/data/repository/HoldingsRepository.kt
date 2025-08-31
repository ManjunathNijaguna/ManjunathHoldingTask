package com.example.manjunathtask.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.manjunathtask.data.api.ApiService
import com.example.manjunathtask.data.api.HoldingDTO
import com.example.manjunathtask.data.api.HoldingsResponse
import com.example.manjunathtask.data.api.toDomain
import com.example.manjunathtask.data.model.Holding
import com.google.gson.Gson

sealed class Result<out T> {
    data class Success<out R>(val data: R) : Result<R>()
    data class Error(val message: String) : Result<Nothing>()
}

class HoldingsRepository(
    private val api: ApiService,
    private val prefs: SharedPreferences,
    private val gson: Gson
) {
    companion object {
        private const val KEY_CACHE = "holdings_cache"
    }

    suspend fun getHoldings(): Result<List<Holding>> {
        return try {
            val response = api.getHoldings()
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!

                // ✅ Save JSON for offline
                prefs.edit().putString(KEY_CACHE, gson.toJson(body)).apply()

                Result.Success(body.data.userHolding.map { it.toDomain() })
            } else {
                loadFromCacheOrError("Network error: ${response.code()}")
            }
        } catch (e: Exception) {
            loadFromCacheOrError(e.localizedMessage ?: "Unknown error")
        }
    }

    private fun loadFromCacheOrError(errorMsg: String): Result<List<Holding>> {
        val cachedJson = prefs.getString(KEY_CACHE, null)
        return if (cachedJson != null) {
            val cached = gson.fromJson(cachedJson, HoldingsResponse::class.java)
            Result.Success(cached.data.userHolding.map { it.toDomain() })
        } else {
            Result.Error(errorMsg)
        }
    }
}

