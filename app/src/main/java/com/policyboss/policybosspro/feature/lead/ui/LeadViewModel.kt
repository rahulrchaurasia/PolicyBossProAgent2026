package com.policyboss.policybosspro.feature.lead.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.network.ConnectivityObserver
import com.policyboss.policybosspro.core.network.NetworkError
import com.policyboss.policybosspro.core.repository.lead.LeadRepository
import com.policyboss.policybosspro.feature.lead.model.LeadAction
import com.policyboss.policybosspro.feature.lead.model.LeadDialogType
import com.policyboss.policybosspro.feature.lead.model.LeadUiEvent
import com.policyboss.policybosspro.feature.lead.model.LeadUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeadViewModel @Inject constructor(
    private val repository: LeadRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeadUiState(isLoading = false, isOnline = true))
    val uiState: StateFlow<LeadUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<LeadUiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private var loadJob: Job? = null
    private var previousConnectivity: Boolean? = null

    init {
        observeNetworkConnectivity()
        loadLeads()
    }

    /**
     * Observes network changes and triggers automatic recovery
     * on offline-to-online transitions.
     */
    private fun observeNetworkConnectivity() {
        viewModelScope.launch {
            connectivityObserver.isConnected.collect { isConnected ->
                val wasOffline = previousConnectivity == false
                previousConnectivity = isConnected

                _uiState.update { it.copy(isOnline = isConnected) }

                // Automatic Recovery on offline-to-online transition
                if (wasOffline && isConnected) {
                    onNetworkRestored()
                }
            }
        }
    }

    private fun onNetworkRestored() {
        val state = _uiState.value
        if (state.isLoading) return

        if (!state.hasCachedData && (state.error != null || !state.isInitialLoadCompleted)) {
            // Case A recovery: Retrying initial load automatically
            loadLeads(showLoading = true)
        } else if (state.hasCachedData) {
            // Case B recovery: Silently refreshing cached leads
            loadLeads(showLoading = false)
            sendEvent(LeadUiEvent.ShowSnackbar("Connection restored. Leads refreshed."))
        }
    }

    fun onAction(action: LeadAction) {
        when (action) {
            is LeadAction.SelectLeadTab -> _uiState.update { it.copy(selectedTab = action.tab) }
            is LeadAction.SelectStatusFilter -> _uiState.update {
                it.copy(selectedStatusFilter = if (it.selectedStatusFilter == action.status) null else action.status)
            }
            is LeadAction.SelectExpiryFilter -> _uiState.update { 
                it.copy(selectedExpiryFilter = if (it.selectedExpiryFilter == action.filter) null else action.filter) 
            }
            is LeadAction.ToggleVehicleFilter -> _uiState.update {
                val filters = it.selectedVehicleFilters.toMutableSet()
                if (filters.contains(action.filter)) filters.remove(action.filter) else filters.add(action.filter)
                it.copy(selectedVehicleFilters = filters)
            }
            is LeadAction.OpenLeadDetails -> _uiState.update { it.copy(selectedLead = action.lead) }
            LeadAction.CloseLeadDetails -> _uiState.update { it.copy(selectedLead = null) }
            
            is LeadAction.OpenMarkAsLost -> _uiState.update { it.copy(selectedLead = action.lead, activeDialog = LeadDialogType.MARK_LOST) }
            LeadAction.CloseMarkAsLost -> _uiState.update { it.copy(activeDialog = null) }
            is LeadAction.SubmitMarkAsLost -> submitMarkAsLost(action.reason, action.otherDescription)
            
            is LeadAction.OpenMarkVehicleSold -> _uiState.update { it.copy(selectedLead = action.lead, activeDialog = LeadDialogType.MARK_SOLD) }
            LeadAction.CloseMarkVehicleSold -> _uiState.update { it.copy(activeDialog = null) }
            LeadAction.SubmitMarkVehicleSold -> submitMarkVehicleSold()
            
            is LeadAction.OpenAddRelationship -> _uiState.update { it.copy(selectedLead = action.lead, activeDialog = LeadDialogType.ADD_RELATIONSHIP) }
            LeadAction.CloseAddRelationship -> _uiState.update { it.copy(activeDialog = null) }
            is LeadAction.SubmitRelationship -> submitRelationship(action.category, action.subCategory)
            
            is LeadAction.OpenEditDetails -> _uiState.update { it.copy(selectedLead = action.lead, activeDialog = LeadDialogType.EDIT_DETAILS) }
            LeadAction.CloseEditDetails -> _uiState.update { it.copy(activeDialog = null) }
            is LeadAction.SubmitEditDetails -> submitEditDetails(action.ncbStatus, action.expiryDate)
            
            is LeadAction.ContinueToQuotes -> {
                if (!connectivityObserver.isCurrentlyConnected()) {
                    sendEvent(LeadUiEvent.ShowSnackbar("Internet connection required to view live quotes."))
                }
            }
            is LeadAction.ShareLead -> { /* Trigger effect to share */ }
            LeadAction.OnRetryClick, LeadAction.LoadLeads -> {
                loadLeads(showLoading = !_uiState.value.hasCachedData, isManualRetry = true)
            }
        }
    }

    /**
     * Loads leads with hybrid offline strategy:
     * - Case A: No internet + No cache -> Full screen No Internet
     * - Case B: No internet + Cache available -> Keep displaying cached leads
     * - Case C: Online -> Load fresh leads and deduplicate requests
     */
    fun loadLeads(showLoading: Boolean = true, isManualRetry: Boolean = false) {
        if (_uiState.value.isLoading && !isManualRetry) return

        val isOnline = connectivityObserver.isCurrentlyConnected()
        _uiState.update { it.copy(isOnline = isOnline) }

        if (!com.policyboss.policybosspro.core.AppConfig.USE_DUMMY_DATA && !isOnline) {
            if (!_uiState.value.hasCachedData) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "No internet connection. Please check your network settings."
                    )
                }
                if (isManualRetry) {
                    sendEvent(LeadUiEvent.ShowSnackbar("No internet connection. Please check your network and try again."))
                }
                return
            } else {
                _uiState.update { it.copy(isLoading = false) }
                if (isManualRetry) {
                    sendEvent(LeadUiEvent.ShowSnackbar("You're currently offline. Displaying saved leads."))
                }
                return
            }
        }

        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = showLoading && !it.hasCachedData,
                    error = null
                )
            }

            try {
                repository.getLeads().collect { result ->
                    result.onSuccess { leads ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                allLeads = leads,
                                isInitialLoadCompleted = true,
                                error = null
                            )
                        }
                    }.onFailure { error ->
                        handleError(error)
                    }
                }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun handleError(e: Throwable) {
        val netError = NetworkError.fromThrowable(e)
        if (!_uiState.value.hasCachedData) {
            _uiState.update { it.copy(isLoading = false, error = netError.errorMessage) }
        } else {
            _uiState.update { it.copy(isLoading = false) }
            sendEvent(LeadUiEvent.ShowSnackbar(netError.errorMessage))
        }
    }

    private fun submitMarkAsLost(reason: String, otherDescription: String?) {
        val lead = _uiState.value.selectedLead ?: return

        // Guard secondary online action
        if (!connectivityObserver.isCurrentlyConnected()) {
            sendEvent(LeadUiEvent.ShowSnackbar("Cannot mark lead while offline. Please connect to internet."))
            return
        }

        _uiState.update { it.copy(isLoading = true, activeDialog = null) }
        viewModelScope.launch {
            repository.markLeadAsLost(lead.id, reason, otherDescription).onSuccess {
                loadLeads(showLoading = false)
            }.onFailure {
                val netError = NetworkError.fromThrowable(it)
                _uiState.update { state -> state.copy(isLoading = false) }
                sendEvent(LeadUiEvent.ShowSnackbar(netError.errorMessage))
            }
        }
    }

    private fun submitMarkVehicleSold() {
        val lead = _uiState.value.selectedLead ?: return

        // Guard secondary online action
        if (!connectivityObserver.isCurrentlyConnected()) {
            sendEvent(LeadUiEvent.ShowSnackbar("Cannot update vehicle status while offline. Please connect to internet."))
            return
        }

        _uiState.update { it.copy(isLoading = true, activeDialog = null) }
        viewModelScope.launch {
            repository.markVehicleAsSold(lead.id).onSuccess {
                loadLeads(showLoading = false)
            }.onFailure {
                val netError = NetworkError.fromThrowable(it)
                _uiState.update { state -> state.copy(isLoading = false) }
                sendEvent(LeadUiEvent.ShowSnackbar(netError.errorMessage))
            }
        }
    }

    private fun submitRelationship(category: String, subCategory: String) {
        val lead = _uiState.value.selectedLead ?: return

        // Guard secondary online action
        if (!connectivityObserver.isCurrentlyConnected()) {
            sendEvent(LeadUiEvent.ShowSnackbar("Cannot update relationship while offline. Please connect to internet."))
            return
        }

        _uiState.update { it.copy(isLoading = true, activeDialog = null) }
        viewModelScope.launch {
            repository.saveRelationship(lead.id, category, subCategory).onSuccess {
                val updatedLead = lead.copy(relationCategory = category, relationSubCategory = subCategory)
                repository.updateLead(updatedLead) // Save to cache
                _uiState.update { it.copy(selectedLead = updatedLead) }
                loadLeads(showLoading = false)
            }.onFailure {
                val netError = NetworkError.fromThrowable(it)
                _uiState.update { state -> state.copy(isLoading = false) }
                sendEvent(LeadUiEvent.ShowSnackbar(netError.errorMessage))
            }
        }
    }
    
    private fun submitEditDetails(ncbStatus: String, expiryDate: String) {
        val lead = _uiState.value.selectedLead ?: return

        // Guard secondary online action
        if (!connectivityObserver.isCurrentlyConnected()) {
            sendEvent(LeadUiEvent.ShowSnackbar("Cannot edit lead details while offline. Please connect to internet."))
            return
        }

        _uiState.update { it.copy(isLoading = true, activeDialog = null) }
        viewModelScope.launch {
            val updatedLead = lead.copy(ncbStatus = ncbStatus, expiryText = "Expiring: $expiryDate")
            repository.updateLead(updatedLead).onSuccess {
                _uiState.update { state -> state.copy(selectedLead = updatedLead) }
                loadLeads(showLoading = false)
            }.onFailure {
                val netError = NetworkError.fromThrowable(it)
                _uiState.update { state -> state.copy(isLoading = false) }
                sendEvent(LeadUiEvent.ShowSnackbar(netError.errorMessage))
            }
        }
    }

    private fun sendEvent(event: LeadUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}
