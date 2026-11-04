package com.policyboss.policybosspro.feature.sales.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.repository.sales.SalesRepository
import com.policyboss.policybosspro.feature.sales.model.SalePolicy
import com.policyboss.policybosspro.feature.sales.model.SalesAction
import com.policyboss.policybosspro.feature.sales.model.SalesFilterState
import com.policyboss.policybosspro.feature.sales.model.SalesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SalesViewModel @Inject constructor(
    private val salesRepository: SalesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SalesUiState())
    val uiState: StateFlow<SalesUiState> = _uiState.asStateFlow()

    init {
        loadSales()
        loadFilterOptions()
    }

    private fun loadFilterOptions() {
        viewModelScope.launch {
            salesRepository.getFilterProducts().collect { products ->
                _uiState.update { state ->
                    state.copy(
                        filterState = state.filterState.copy(availableProducts = products),
                        appliedFilterState = state.appliedFilterState.copy(availableProducts = products)
                    )
                }
            }
        }
        viewModelScope.launch {
            salesRepository.getFilterSubUsers().collect { subUsers ->
                _uiState.update { state ->
                    state.copy(
                        filterState = state.filterState.copy(availableSubUsers = subUsers),
                        appliedFilterState = state.appliedFilterState.copy(availableSubUsers = subUsers)
                    )
                }
            }
        }
        viewModelScope.launch {
            salesRepository.getFilterSubPosps().collect { subPosps ->
                _uiState.update { state ->
                    state.copy(
                        filterState = state.filterState.copy(availableSubPosps = subPosps),
                        appliedFilterState = state.appliedFilterState.copy(availableSubPosps = subPosps)
                    )
                }
            }
        }
    }

    fun onAction(action: SalesAction) {
        when (action) {
            SalesAction.ToggleDateRangeSelector -> {
                _uiState.update { it.copy(showDateRangeSelector = !it.showDateRangeSelector) }
            }
            is SalesAction.ApplyDateRange -> {
                _uiState.update {
                    it.copy(
                        dateRange = action.range,
                        showDateRangeSelector = false
                    )
                }
            }
            SalesAction.OpenFilter -> {
                // Initialize temporary filterState from appliedFilterState and reset search queries
                _uiState.update { state ->
                    state.copy(
                        filterState = state.appliedFilterState.copy(
                            subUserSearchQuery = "",
                            subPospSearchQuery = ""
                        )
                    )
                }
            }
            SalesAction.CloseFilter -> {
                // Discard temporary selections and restore appliedFilterState
                _uiState.update { state ->
                    state.copy(filterState = state.appliedFilterState)
                }
            }
            is SalesAction.SelectFilterCategory -> {
                _uiState.update { state ->
                    state.copy(
                        filterState = state.filterState.copy(selectedCategory = action.category)
                    )
                }
            }
            is SalesAction.ToggleProductFilter -> {
                _uiState.update { state ->
                    val current = state.filterState.selectedProducts
                    val updated = if (current.contains(action.product)) {
                        current - action.product
                    } else {
                        current + action.product
                    }
                    state.copy(filterState = state.filterState.copy(selectedProducts = updated))
                }
            }
            is SalesAction.ToggleSubUserFilter -> {
                _uiState.update { state ->
                    val current = state.filterState.selectedSubUsers
                    val updated = if (current.contains(action.subUser)) {
                        current - action.subUser
                    } else {
                        current + action.subUser
                    }
                    state.copy(filterState = state.filterState.copy(selectedSubUsers = updated))
                }
            }
            is SalesAction.ToggleSubPospFilter -> {
                _uiState.update { state ->
                    val current = state.filterState.selectedSubPosps
                    val updated = if (current.contains(action.subPosp)) {
                        current - action.subPosp
                    } else {
                        current + action.subPosp
                    }
                    state.copy(filterState = state.filterState.copy(selectedSubPosps = updated))
                }
            }
            is SalesAction.UpdateSubUserSearch -> {
                _uiState.update { state ->
                    state.copy(
                        filterState = state.filterState.copy(subUserSearchQuery = action.query)
                    )
                }
            }
            is SalesAction.UpdateSubPospSearch -> {
                _uiState.update { state ->
                    state.copy(
                        filterState = state.filterState.copy(subPospSearchQuery = action.query)
                    )
                }
            }
            SalesAction.ClearFilterSelection -> {
                _uiState.update { state ->
                    state.copy(
                        filterState = state.filterState.copy(
                            selectedProducts = emptySet(),
                            selectedSubUsers = emptySet(),
                            selectedSubPosps = emptySet(),
                            subUserSearchQuery = "",
                            subPospSearchQuery = ""
                        )
                    )
                }
            }
            SalesAction.ApplyFilters -> {
                _uiState.update { state ->
                    val newApplied = state.filterState
                    val filtered = applyCurrentFilters(state.allPolicies, newApplied)
                    state.copy(
                        appliedFilterState = newApplied,
                        policies = filtered,
                        resultsCount = filtered.size
                    )
                }
            }
        }
    }

    private fun applyCurrentFilters(
        all: List<SalePolicy>,
        filter: SalesFilterState
    ): List<SalePolicy> {
        return all.filter { policy ->
            val matchesProduct = if (filter.selectedProducts.isEmpty()) {
                true
            } else {
                filter.selectedProducts.any { selected ->
                    policy.insuranceCategory.contains(selected, ignoreCase = true) ||
                            (selected.equals("Two Wheeler", ignoreCase = true) && policy.insuranceCategory.contains("TW", ignoreCase = true))
                }
            }

            val matchesSubUser = if (filter.selectedSubUsers.isEmpty()) {
                true
            } else {
                filter.selectedSubUsers.contains(policy.subUserName)
            }

            val matchesSubPosp = if (filter.selectedSubPosps.isEmpty()) {
                true
            } else {
                filter.selectedSubPosps.contains(policy.agentName)
            }

            matchesProduct && matchesSubUser && matchesSubPosp
        }
    }

    fun loadSales() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            salesRepository.getSales().collect { result ->
                result.fold(
                    onSuccess = { policies ->
                        _uiState.update {
                            val filtered = applyCurrentFilters(policies, it.appliedFilterState)
                            it.copy(
                                isLoading = false,
                                allPolicies = policies,
                                policies = filtered,
                                resultsCount = filtered.size,
                                errorMessage = null
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message
                            )
                        }
                    }
                )
            }
        }
    }
}
