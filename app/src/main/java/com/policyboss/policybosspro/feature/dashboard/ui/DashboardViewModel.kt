package com.policyboss.policybosspro.feature.dashboard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.repository.dashboard.DashboardRepository
import com.policyboss.policybosspro.feature.dashboard.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            // In a real app we might combine multiple flows or do parallel fetches
            repository.getEarnings().collect { result ->
                result.onSuccess { data -> _uiState.update { it.copy(earnings = data) } }
            }
            repository.getTotalPoliciesSold().collect { result ->
                result.onSuccess { data -> _uiState.update { it.copy(totalPoliciesSold = data) } }
            }
            repository.getLeadsByStage().collect { result ->
                result.onSuccess { data -> _uiState.update { it.copy(leadsByStage = data) } }
            }
            repository.getLeadsByProduct().collect { result ->
                result.onSuccess { data -> _uiState.update { it.copy(leadsByProduct = data) } }
            }
            repository.getMissedOpportunities().collect { result ->
                result.onSuccess { data -> _uiState.update { it.copy(missedOpportunities = data) } }
            }
            repository.getEarningsChart().collect { result ->
                result.onSuccess { data -> _uiState.update { it.copy(earningsChart = data) } }
            }
            loadBusinessSnapshot(_uiState.value.selectedBusinessCategory, _uiState.value.selectedBusinessMetric)
            
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun loadBusinessSnapshot(category: String, metric: String) {
        repository.getBusinessSnapshot(category, metric).collect { result ->
            result.onSuccess { data -> 
                _uiState.update { it.copy(businessSnapshot = data) } 
            }
        }
    }

    fun onAction(action: DashboardAction) {
        when (action) {
            is DashboardAction.SelectMainTab -> _uiState.update { it.copy(selectedMainTab = action.tab) }
            DashboardAction.ToggleDateRangeSelector -> _uiState.update { it.copy(showDateRangeSelector = !it.showDateRangeSelector) }
            is DashboardAction.ApplyDateRange -> _uiState.update { 
                it.copy(selectedDateRange = action.range, showDateRangeSelector = false) 
            }
            is DashboardAction.SelectLeadsTab -> _uiState.update { it.copy(selectedLeadsTab = action.tab) }
            is DashboardAction.SelectBusinessCategory -> {
                _uiState.update { it.copy(selectedBusinessCategory = action.category) }
                viewModelScope.launch { loadBusinessSnapshot(action.category, _uiState.value.selectedBusinessMetric) }
            }
            is DashboardAction.SelectBusinessMetric -> {
                _uiState.update { it.copy(selectedBusinessMetric = action.metric) }
                viewModelScope.launch { loadBusinessSnapshot(_uiState.value.selectedBusinessCategory, action.metric) }
            }
            DashboardAction.ViewMissedOpportunities -> { /* Effect logic here */ }
        }
    }
}
