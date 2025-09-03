package com.example.manjunathtask.data.repository

import android.content.SharedPreferences
import com.example.manjunathtask.data.api.ApiService
import com.example.manjunathtask.data.api.HoldingDTO
import com.example.manjunathtask.data.api.HoldingsData
import com.example.manjunathtask.data.api.HoldingsResponse
import com.example.manjunathtask.domain.model.Holding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class HoldingsRepositoryImplTest {

    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var api: ApiService
    private lateinit var repo: HoldingsRepositoryImpl
    private val gson = Gson()

    @Before
    fun setup() {
        prefs = mockk()
        editor = mockk()
        api = mockk()
        every { prefs.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.apply() } just Runs
        repo = HoldingsRepositoryImpl(api, prefs, gson)
    }

    @Test
    fun `getHoldings returns API data and saves to cache`() = runBlocking {
        // Arrange
        val dto = HoldingDTO("AAA", 10, 110.0, 100.0, 108.0)
        val response = HoldingsResponse(HoldingsData(listOf(dto)))
        coEvery { api.getHoldings() } returns Response.success(response)

        // Act
        val result = repo.getHoldings()

        // Assert
        assertEquals(1, result.size)
        assertEquals("AAA", result[0].symbol)
        assertEquals(10, result[0].quantity)
    }

    @Test
    fun `getHoldings loads from cache when API fails`() = runBlocking {
        // Arrange
        val cached = listOf(Holding("CACHED", 50.0, 40.0, 5.0, 45))
        val json = gson.toJson(cached)
        every { prefs.getString("holdings_cache", null) } returns json
        coEvery { api.getHoldings() } throws RuntimeException("network error")

        // Act
        val result = repo.getHoldings()

        // Assert
        assertEquals(1, result.size)
        assertEquals("CACHED", result[0].symbol)
        assertEquals(45, result[0].quantity)
    }

    @Test
    fun `getHoldings returns empty list when API fails and no cache`() = runBlocking {
        // Arrange
        every { prefs.getString("holdings_cache", null) } returns null
        coEvery { api.getHoldings() } throws RuntimeException("network error")

        // Act
        val result = repo.getHoldings()

        // Assert
        assertTrue(result.isEmpty())
    }
}
