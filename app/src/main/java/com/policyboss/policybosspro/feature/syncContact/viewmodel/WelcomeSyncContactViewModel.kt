package com.policyboss.policybosspro.feature.syncContact.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.datastore.AppDataManager
import com.policyboss.policybosspro.core.repository.syncContact.SyncContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WelcomeSyncContactUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSmsChecked: Boolean = false,
    val isCallChecked: Boolean = false,
    val isAgreeChecked: Boolean = false,
    val hasExistingAgreements: Boolean = false,
    val isPermissionsNeeded: Boolean = false,
    val errorMessage: String? = null
)

sealed interface WelcomeSyncContactEvent {
    data object RequestPermissions : WelcomeSyncContactEvent
    data object NavigateToMain : WelcomeSyncContactEvent
    data class ShowMessage(val message: String) : WelcomeSyncContactEvent
}

@HiltViewModel
class WelcomeSyncContactViewModel @Inject constructor(
    private val syncContactRepository: SyncContactRepository,
    private val appDataManager: AppDataManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(WelcomeSyncContactUiState())
    val uiState: StateFlow<WelcomeSyncContactUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<WelcomeSyncContactEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadAgreements()
    }

    /**
     * Calls GET /sync_contact/get_sync_contact_agreements on initial launch
     * to pre-populate existing communication preferences for slide 4.
     * Does NOT auto-advance the welcome slides, trigger automatic sync, or navigate.
     */
    fun loadAgreements() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null) }

            val result = syncContactRepository.getSyncContactAgreements()
            result.onSuccess { items ->
                val lastItem = items.lastOrNull()
                val isSms = lastItem?.isSms.equals("yes", ignoreCase = true)
                val isCall = lastItem?.isCall.equals("yes", ignoreCase = true)
                val hasAgreements = items.isNotEmpty()

                _uiState.update {
                    it.copy(
                        isSmsChecked = isSms,
                        isCallChecked = isCall,
                        hasExistingAgreements = hasAgreements,
                        isPermissionsNeeded = hasAgreements
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    fun onCheckboxToggled(isSms: Boolean, isCall: Boolean, isAgree: Boolean) {
        _uiState.update {
            it.copy(
                isSmsChecked = isSms,
                isCallChecked = isCall,
                isAgreeChecked = isAgree
            )
        }
    }

    /**
     * Persists agreement checkboxes via POST /postservicecall/sync_contacts/online_agreement
     * and emits NavigateToMain ONLY when saving succeeds.
     * Guaranteed to never navigate on failure and safely resets loading state in all cases.
     */
    fun saveAgreements(
        isSms: Boolean,
        isCall: Boolean
    ) {
        if (_uiState.value.isLoading || _uiState.value.isSaving) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isSaving = true, errorMessage = null) }
            try {
                val saveResult = syncContactRepository.saveAgreementCheckbox(isSms = isSms, isCall = isCall)
                saveResult.onSuccess { success ->
                    if (success) {
                        _uiEvent.send(WelcomeSyncContactEvent.NavigateToMain)
                    } else {
                        val errorMsg = "Failed to save agreement. Please try again."
                        _uiState.update { it.copy(errorMessage = errorMsg) }
                        _uiEvent.send(WelcomeSyncContactEvent.ShowMessage(errorMsg))
                    }
                }.onFailure { error ->
                    val errorMsg = error.message ?: "Failed to save agreement. Please check your connection."
                    _uiState.update { it.copy(errorMessage = errorMsg) }
                    _uiEvent.send(WelcomeSyncContactEvent.ShowMessage(errorMsg))
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "An unexpected error occurred."
                _uiState.update { it.copy(errorMessage = errorMsg) }
                _uiEvent.send(WelcomeSyncContactEvent.ShowMessage(errorMsg))
            } finally {
                _uiState.update { it.copy(isLoading = false, isSaving = false) }
            }
        }
    }

    @Deprecated("Use saveAgreements(isSms, isCall) and observe NavigateToMain instead")
    fun saveAgreementsAndProceed(
        isSms: Boolean,
        isCall: Boolean,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isSaving = true) }
            try {
                val saveResult = syncContactRepository.saveAgreementCheckbox(isSms = isSms, isCall = isCall)
                saveResult.onSuccess { success ->
                    if (success) {
                        onComplete()
                    } else {
                        _uiEvent.send(WelcomeSyncContactEvent.ShowMessage("Failed to save agreement"))
                    }
                }.onFailure { error ->
                    _uiEvent.send(WelcomeSyncContactEvent.ShowMessage(error.message ?: "Failed to save agreement"))
                }
            } catch (e: Exception) {
                _uiEvent.send(WelcomeSyncContactEvent.ShowMessage(e.message ?: "Failed to save agreement"))
            } finally {
                _uiState.update { it.copy(isLoading = false, isSaving = false) }
            }
        }
    }
}
