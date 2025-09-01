package com.example.manjunathtask.ui

import android.app.Application
import com.example.manjunathtask.data.repository.HoldingsRepository
import com.example.manjunathtask.ui.viewmodel.HoldingsViewModel
import com.example.manjunathtask.ui.viewmodel.UiState
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [34],
    application = Application::class,
    packageName = "com.example.manjunathtask"
)
class MainActivityTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repo: HoldingsRepository
    private lateinit var viewModel: HoldingsViewModel
    private lateinit var activity: MainActivity

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        repo = mockk(relaxed = true)
        viewModel = spyk(HoldingsViewModel(repo))

        activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()

        // inject fake ViewModel
        val field = MainActivity::class.java.getDeclaredField("viewModel")
        field.isAccessible = true
        field.set(activity, viewModel)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when UiState Loading then swipeRefresh is refreshing`() = runTest {
        val stateFlow = MutableStateFlow<UiState>(UiState.Loading)
        every { viewModel.uiState } returns stateFlow

        activity.observe()
        dispatcher.scheduler.advanceUntilIdle()

        assert(activity.binding.swipeContainer.isRefreshing)
    }

    @Test
    fun `when UiState Error then show tvNoHolding`() = runTest {
        val stateFlow = MutableStateFlow<UiState>(UiState.Error("Network error"))
        every { viewModel.uiState } returns stateFlow

        activity.observe()
        dispatcher.scheduler.advanceUntilIdle()

        assert(activity.binding.tvNoHolding.visibility == android.view.View.VISIBLE)
        assert(activity.binding.summaryCard.visibility == android.view.View.GONE)
    }

    @Test
    fun `when search is triggered then toolbar hides`() {
        activity.runOnUiThread {
            activity.toggleSearch(true)
        }
        assert(activity.binding.toolbar.visibility == android.view.View.INVISIBLE)
        assert(activity.binding.searchBar.visibility == android.view.View.VISIBLE)
    }

    @Test
    fun `when clear search clicked then search is reset`() {
        activity.runOnUiThread {
            activity.binding.etSearch.setText("ABC")
            activity.binding.ivClearSearch.performClick()
        }
        assert(activity.binding.etSearch.text.isEmpty())
    }
}
