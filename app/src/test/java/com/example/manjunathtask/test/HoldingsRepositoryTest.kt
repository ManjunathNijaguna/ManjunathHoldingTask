package com.example.manjunathtask.test

import android.content.Context
import android.content.SharedPreferences
import com.example.manjunathtask.data.api.ApiService
import com.example.manjunathtask.data.api.HoldingDTO
import com.example.manjunathtask.data.api.HoldingsData
import com.example.manjunathtask.data.api.HoldingsResponse
import com.example.manjunathtask.data.repository.HoldingsRepository
import com.example.manjunathtask.data.repository.Result
import com.google.gson.Gson
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class HoldingsRepositoryTest {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var api: ApiService
    private lateinit var repo: HoldingsRepository

    @Before
    fun setup() {
        context = mockk()
        prefs = mockk()
        editor = mockk()
        api = mockk()
        coEvery { context.getSharedPreferences("app_cache", Context.MODE_PRIVATE) } returns prefs
        coEvery { prefs.edit() } returns editor
        coEvery { editor.putString(any(), any()) } returns editor
        coEvery { editor.apply() } just Runs
        repo = HoldingsRepository(api, prefs, Gson())
    }

    @Test
    fun `getHoldings API`() = runBlocking {

        val dto = HoldingDTO("AAA", 10, 110.0, 100.0, 108.0)
        val response = HoldingsResponse(HoldingsData(listOf(dto)))
        coEvery { api.getHoldings() } returns Response.success(response)

        val result = repo.getHoldings()

        assertTrue(result is Result.Success)
        val holdings = (result as Result.Success).data
        assertEquals(1, holdings.size)
        assertEquals("AAA", holdings[0].symbol)
    }
}
