package com.policyboss.policybosspro.feature.sales.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.policyboss.policybosspro.core.repository.sales.SalesRepository
import com.policyboss.policybosspro.feature.sales.model.PolicyDetailsUiState
import com.policyboss.policybosspro.navigation.Dest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PolicyDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val salesRepository: SalesRepository
) : ViewModel() {

    private val policyId: String = try {
        savedStateHandle.toRoute<Dest.PolicyDetails>().policyId
    } catch (e: Exception) {
        savedStateHandle.get<String>("policyId").orEmpty()
    }

    private val _uiState = MutableStateFlow(PolicyDetailsUiState(isLoading = true))
    val uiState: StateFlow<PolicyDetailsUiState> = _uiState.asStateFlow()

    init {
        loadPolicyDetails()
    }

    fun loadPolicyDetails() {
        if (policyId.isBlank()) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Invalid Policy ID") }
            return
        }
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            salesRepository.getPolicyById(policyId).collect { result ->
                result.fold(
                    onSuccess = { policy ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                policy = policy,
                                errorMessage = null
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Policy not found"
                            )
                        }
                    }
                )
            }
        }
    }
}
