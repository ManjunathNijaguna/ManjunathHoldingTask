package com.example.manjunathtask.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.manjunathtask.data.model.Holding
import com.example.manjunathtask.data.repository.HoldingsRepository
import com.example.manjunathtask.domain.PortfolioCalculator
import com.example.manjunathtask.domain.PortfolioSummary
import com.example.manjunathtask.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Loading : UiState()
    data class Success(val holdings: List<Holding>, val summary: PortfolioSummary) : UiState()
    data class Error(val message: String) : UiState()
}

class HoldingsViewModel(
    private val repo: HoldingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            when (val r = repo.getHoldings()) {
                is Result.Success -> {
                    val holdings = r.data
                    val summary = PortfolioCalculator.calculate(holdings)
                    _uiState.value = UiState.Success(holdings, summary)
                }
                is Result.Error -> {
                    _uiState.value = UiState.Error(r.message)
                }
            }
        }
    }
}

