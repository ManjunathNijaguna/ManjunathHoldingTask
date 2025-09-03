import com.example.manjunathtask.domain.model.Holding
import com.example.manjunathtask.domain.usecase.GetHoldingsUseCase
import com.example.manjunathtask.ui.viewmodel.HoldingsViewModel
import com.example.manjunathtask.ui.viewmodel.UiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    private val getHoldingsUseCase = mockk<GetHoldingsUseCase>()
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
    fun `emits Success state when usecase returns data`() = runBlocking {
        // Arrange
        val holdings = listOf(Holding("AAA", 110.0, 100.0, 10.0, 108))
        coEvery { getHoldingsUseCase() } returns holdings

        val vm = HoldingsViewModel(getHoldingsUseCase)

        // Act
        vm.loadHoldings()
        dispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = vm.uiState.value
        assertTrue(state is UiState.Success)
        val success = state as UiState.Success
        assertEquals("AAA", success.holdings[0].symbol)
    }


    @Test
    fun `emits Error state when usecase throws exception`() = runBlocking {
        // Arrange
        coEvery { getHoldingsUseCase() } throws RuntimeException("Network error")

        val vm = HoldingsViewModel(getHoldingsUseCase)

        // Act
        vm.loadHoldings()
        dispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = vm.uiState.value
        assertTrue(state is UiState.Error)
        val error = state as UiState.Error
        assertEquals("Network error", error.message)
    }

}
