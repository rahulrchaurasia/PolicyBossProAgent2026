package com.policyboss.policybosspro.feature.home.viemodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.AppConfig
import com.policyboss.policybosspro.core.datastore.AppDataManager
import com.policyboss.policybosspro.core.network.ConnectivityObserver
import com.policyboss.policybosspro.core.network.NetworkError
import com.policyboss.policybosspro.core.repository.home.HomeRepository
import com.policyboss.policybosspro.feature.home.model.home.RenewalOption
import com.policyboss.policybosspro.feature.home.model.homeState.HomeAction
import com.policyboss.policybosspro.feature.home.model.homeState.HomeUiEvent
import com.policyboss.policybosspro.feature.home.model.homeState.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appDataManager: AppDataManager,
    private val homeRepository: HomeRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            isLoading = false,
            isShimmerLoading = true,
            isOnline = true
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<HomeUiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private var homeDataJob: Job? = null
    private var previousConnectivity: Boolean? = null
    private var hasFetchedUserConstantsThisSession = false

    init {
        observeUserData()
        observeNetworkConnectivity()
        loadHomeData()
        fetchAndSaveUserConstantsOnce()
    }

    /**
     * Observes real-time network status and coordinates automatic recovery
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
        if (homeDataJob?.isActive == true) return
        val state = _uiState.value
        if (state.isLoading) return

        if (!state.hasCachedData && (state.errorMessage != null || !state.isInitialLoadCompleted)) {
            // Case A recovery: Retrying initial load with shimmer
            loadHomeData(showShimmer = true)
        } else if (state.hasCachedData) {
            // Case B recovery: Silently refresh in background preserving cached view
            loadHomeData(showShimmer = false)
            sendEvent(HomeUiEvent.ShowSnackbar("Connection restored. Home data refreshed."))
        }
    }

    private fun fetchAndSaveUserConstantsOnce() {
        if (hasFetchedUserConstantsThisSession) return
        hasFetchedUserConstantsThisSession = true

        viewModelScope.launch {
            homeRepository.fetchAndSaveUserConstants()
                .onSuccess { response ->
                    Log.d("HomeViewModel", "UserConstants fetched and synced successfully: ${response?.message}")
                }
                .onFailure { error ->
                    Log.w("HomeViewModel", "UserConstants fetch failed: ${error.message}")
                }
        }
    }

    fun setShimmerLoading(loading: Boolean) {
        _uiState.update { it.copy(isShimmerLoading = loading) }
    }

    /**
     * Loads Home data with hybrid offline decision strategy:
     * - Prevents duplicate overlapping requests using job activity tracking.
     * - Preserves active jobs without blind cancellation.
     */
    fun loadHomeData(showShimmer: Boolean = true, isManualRetry: Boolean = false) {
        // Prevent duplicate or overlapping requests using active job check
        if (homeDataJob?.isActive == true) return

        val isOnline = connectivityObserver.isCurrentlyConnected()
        _uiState.update { it.copy(isOnline = isOnline) }

        if (!AppConfig.USE_DUMMY_DATA && !isOnline) {
            val hasCachedData = _uiState.value.hasCachedData
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isShimmerLoading = false,
                    errorMessage = if (!hasCachedData) {
                        "No internet connection. Please check your network settings."
                    } else {
                        null
                    }
                )
            }
            if (isManualRetry) {
                sendEvent(
                    HomeUiEvent.ShowSnackbar(
                        if (hasCachedData) {
                            "You're currently offline. Displaying cached data."
                        } else {
                            "No internet connection. Please check your network and try again."
                        }
                    )
                )
            }
            return
        }

        homeDataJob = viewModelScope.launch {
            val isColdStart = !_uiState.value.hasCachedData

            // 1. Force Shimmer ON immediately
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isShimmerLoading = showShimmer && isColdStart,
                    errorMessage = null
                )
            }

            try {
                // 2. Concurrently load repository items
                supervisorScope {
                    launch {
                        homeRepository.getEarningsSummary().collect { res ->
                            res.onSuccess { data -> _uiState.update { it.copy(earningsSummary = data) } }
                                .onFailure { e -> handleError(e) }
                        }
                    }
                    launch {
                        homeRepository.getQuoteTabs().collect { res ->
                            res.onSuccess { data -> _uiState.update { it.copy(quoteTabs = data) } }
                                .onFailure { e -> handleError(e) }
                        }
                    }
                    launch {
                        homeRepository.getPromoBanners().collect { res ->
                            res.onSuccess { data -> _uiState.update { it.copy(promoBanners = data) } }
                                .onFailure { e -> handleError(e) }
                        }
                    }
                    launch {
                        homeRepository.getHomeMetrics().collect { res ->
                            res.onSuccess { data -> _uiState.update { it.copy(metrics = data) } }
                                .onFailure { e -> handleError(e) }
                        }
                    }
                    launch {
                        homeRepository.getTopLeads().collect { res ->
                            res.onSuccess { data -> _uiState.update { it.copy(leads = data) } }
                                .onFailure { e -> handleError(e) }
                        }
                    }
                    launch {
                        homeRepository.getRenewalLeads().collect { res ->
                            res.onSuccess { data -> _uiState.update { it.copy(renewalLeads = data) } }
                                .onFailure { e -> handleError(e) }
                        }
                    }
                    launch {
                        homeRepository.getQuickLinks().collect { res ->
                            res.onSuccess { data -> _uiState.update { it.copy(quickLinks = data) } }
                                .onFailure { e -> handleError(e) }
                        }
                    }
                    launch {
                        homeRepository.getTrustBullets().collect { res ->
                            res.onSuccess { data -> _uiState.update { it.copy(trustBullets = data) } }
                                .onFailure { e -> handleError(e) }
                        }
                    }
                    launch {
                        homeRepository.getPartnerLogos().collect { res ->
                            res.onSuccess { data -> _uiState.update { it.copy(partnerLogos = data) } }
                                .onFailure { e -> handleError(e) }
                        }
                    }
                }

                // 3. ARTIFICIAL SAFETY DELAY: If using dummy data, guarantee shimmer is visible for 700ms 
                // so the user actually sees the smooth loading skeleton per Figma guidelines!
                if (AppConfig.USE_DUMMY_DATA && showShimmer && isColdStart) {
                    delay(700)
                }

                _uiState.update { it.copy(isInitialLoadCompleted = true) }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isShimmerLoading = false
                    )
                }
            }
        }
    }

    private fun handleError(e: Throwable) {
        val netError = NetworkError.fromThrowable(e)
        if (!_uiState.value.hasCachedData) {
            _uiState.update { it.copy(errorMessage = netError.errorMessage) }
        } else {
            sendEvent(HomeUiEvent.ShowSnackbar(netError.errorMessage))
        }
    }

    private fun observeUserData() {
        viewModelScope.launch {
            appDataManager.userName.collect { name ->
                val initials = extractInitials(name)
                _uiState.update {
                    it.copy(
                        userName = name,
                        userInitials = initials
                    )
                }
            }
        }

        viewModelScope.launch {
            appDataManager.supportNumber.collect { number ->
                _uiState.update {
                    it.copy(supportNumber = number)
                }
            }
        }
    }

    private fun extractInitials(fullName: String): String {
        val trimmed = fullName.trim()
        if (trimmed.isEmpty()) return "DD"
        val parts = trimmed.split(" ").filter { it.isNotBlank() }
        return when {
            parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
            parts.size == 1 && parts[0].length >= 2 -> parts[0].take(2).uppercase()
            parts.size == 1 && parts[0].isNotEmpty() -> "${parts[0].first().uppercaseChar()}"
            else -> "DD"
        }
    }

    private fun sendEvent(event: HomeUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnRetryClick -> {
                loadHomeData(showShimmer = !_uiState.value.hasCachedData, isManualRetry = true)
            }

            is HomeAction.OnRefresh -> {
                if (!connectivityObserver.isCurrentlyConnected()) {
                    sendEvent(HomeUiEvent.ShowSnackbar("You're offline. Displaying cached data."))
                } else {
                    loadHomeData(showShimmer = false, isManualRetry = true)
                }
            }

            is HomeAction.OnRegistrationNumberChange -> {
                val formatted = action.regNo.uppercase().filter { it.isLetterOrDigit() }.take(10)
                _uiState.update { it.copy(registrationNumber = formatted) }
            }

            is HomeAction.OnQuoteTabSelected -> {
                _uiState.update { it.copy(selectedQuoteTab = action.tabTitle) }
            }

            is HomeAction.OnLeadsTabSelected -> {
                _uiState.update { it.copy(selectedLeadsTab = action.tab) }
            }

            is HomeAction.OnGetQuotesClick -> {
                if (action.regNo.isBlank()) {
                    sendEvent(HomeUiEvent.ShowSnackbar("Please enter a registration number"))
                } else if (!connectivityObserver.isCurrentlyConnected()) {
                    sendEvent(HomeUiEvent.ShowSnackbar("Internet connection required to fetch live quotes."))
                } else {
                    sendEvent(HomeUiEvent.ShowSnackbar("Fetching quotes for ${action.regNo} (${action.tab})"))
                }
            }

            is HomeAction.OnNoRegistrationClick -> {
                _uiState.update { it.copy(showNoRegistrationBottomSheet = true) }
            }

            is HomeAction.OnDismissNoRegistrationBottomSheet -> {
                _uiState.update { it.copy(showNoRegistrationBottomSheet = false) }
            }

            is HomeAction.OnOpenSyncIncentiveBottomSheet -> {
                _uiState.update {
                    it.copy(
                        showSyncIncentiveBottomSheet = true,
                        selectedSyncIncentiveLead = action.lead
                    )
                }
            }

            is HomeAction.OnDismissSyncIncentiveBottomSheet -> {
                _uiState.update {
                    it.copy(
                        showSyncIncentiveBottomSheet = false,
                        selectedSyncIncentiveLead = null
                    )
                }
            }

            is HomeAction.OnUnlockSyncRewardClick -> {
                _uiState.update {
                    it.copy(
                        showSyncIncentiveBottomSheet = false,
                        selectedSyncIncentiveLead = null
                    )
                }
            }

            is HomeAction.OnOpenRenewPolicyBottomSheet -> {
                _uiState.update {
                    it.copy(
                        showRenewPolicyBottomSheet = true,
                        selectedRenewalLead = action.lead,
                        selectedRenewalOption = RenewalOption.EXISTING_INSURER
                    )
                }
            }

            is HomeAction.OnSelectRenewalOption -> {
                _uiState.update { it.copy(selectedRenewalOption = action.option) }
            }

            is HomeAction.OnDismissRenewPolicyBottomSheet -> {
                _uiState.update {
                    it.copy(
                        showRenewPolicyBottomSheet = false,
                        selectedRenewalLead = null
                    )
                }
            }

            is HomeAction.OnContinueRenewalClick -> {
                _uiState.update {
                    it.copy(
                        showRenewPolicyBottomSheet = false,
                        selectedRenewalLead = null
                    )
                }
            }

            else -> Unit
        }
    }
}
