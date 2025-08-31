package com.example.manjunathtask.test

import com.example.manjunathtask.data.api.ApiService
import com.example.manjunathtask.data.api.HoldingDTO
import com.example.manjunathtask.data.api.HoldingsData
import com.example.manjunathtask.data.api.HoldingsResponse
import com.example.manjunathtask.data.repository.HoldingsRepository
import com.example.manjunathtask.data.repository.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response

class HoldingsRepositoryTest {

    private val api = mockk<ApiService>()
    private val repo = HoldingsRepository(api)

    @Test
    fun `getHoldings returns Success from API`() = runBlocking {
        val dto = HoldingDTO("AAA", 10, 110.0, 100.0, 108.0)
        val response = HoldingsResponse(HoldingsData(listOf(dto)))
        coEvery { api.getHoldings() } returns Response.success(response)

        val result = repo.getHoldings()

        assertTrue(result is Result.Success)
        val holdings = (result as Result.Success).data
        assertEquals(1, holdings.size)
        assertEquals("AAA", holdings[0].symbol)
    }

    @Test
    fun `getHoldings returns cached data on API failure`() = runBlocking {
        val dto = HoldingDTO("AAA", 10, 110.0, 100.0, 108.0)
        val response = HoldingsResponse(HoldingsData(listOf(dto)))
        coEvery { api.getHoldings() } returns Response.success(response)

        // first call - populate cache
        repo.getHoldings()

        // simulate failure
        coEvery { api.getHoldings() } throws RuntimeException("Network down")

        val result = repo.getHoldings()

        assertTrue(result is Result.Success)
        assertEquals("AAA", (result as Result.Success).data[0].symbol)
    }
}
