package com.policyboss.policybosspro.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.datastore.AppDataManager
import com.policyboss.policybosspro.navigation.Dest

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val appDataManager: AppDataManager
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(RootUiState())

    val uiState =
        _uiState.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val isLoggedIn = appDataManager.isUserLoggedIn()
            val ssId = appDataManager.getSsId()
            val isSyncDone = appDataManager.isContactSyncCompleted(ssId)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    startDestination = when {
                        !isLoggedIn -> Dest.CustomSplash
                        !isSyncDone -> Dest.AuthGraph
                        else -> Dest.MainGraph
                    },
                    authStartDestination = if (isLoggedIn && !isSyncDone) {
                        Dest.WelcomeSyncContact
                    } else {
                        Dest.Welcome
                    }
                )
            }
        }
    }
}