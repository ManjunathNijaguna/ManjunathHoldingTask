package com.example.manjunathtask.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.manjunathtask.domain.PortfolioCalculator
import com.example.manjunathtask.domain.model.Holding
import com.example.manjunathtask.domain.PortfolioSummary
import com.example.manjunathtask.domain.usecase.GetHoldingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UiState {
    object Loading : UiState()
    data class Success(val holdings: List<Holding>, val summary: PortfolioSummary) : UiState()
    data class Error(val message: String) : UiState()
}

class HoldingsViewModel @Inject constructor(
    private val getHoldingsUseCase: GetHoldingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun loadHoldings() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val result = getHoldingsUseCase()
                val summary = PortfolioCalculator.calculate(result)
                _uiState.value = UiState.Success(result, summary)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Something went wrong")
            }
        }
    }
}


