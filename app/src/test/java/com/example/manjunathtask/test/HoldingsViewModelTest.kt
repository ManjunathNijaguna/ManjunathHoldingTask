package com.example.manjunathtask.test

import com.example.manjunathtask.data.model.Holding
import com.example.manjunathtask.data.repository.HoldingsRepository
import com.example.manjunathtask.data.repository.Result
import com.example.manjunathtask.ui.HoldingsViewModel
import com.example.manjunathtask.ui.UiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HoldingsViewModelTest {

    private val repo = mockk<HoldingsRepository>()
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `emits Success state when repository returns data`() = runBlocking {
        val list = listOf(Holding("AAA", 10, 110.0, 100.0, 108.0))
        coEvery { repo.getHoldings() } returns Result.Success(list)

        val vm = HoldingsViewModel(repo)

        dispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.first()
        assertTrue(state is UiState.Success)
        assertEquals("AAA", (state as UiState.Success).holdings[0].symbol)
    }

    @Test
    fun `emits Error state when repository fails`() = runBlocking {
        coEvery { repo.getHoldings() } returns Result.Error("Network error")

        val vm = HoldingsViewModel(repo)

        dispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.first()
        assertTrue(state is UiState.Error)
    }
}
