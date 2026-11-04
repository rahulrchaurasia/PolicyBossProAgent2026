package com.policyboss.policybosspro.feature.home.ui_Menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.datastore.AppDataManager
import com.policyboss.policybosspro.core.repository.login.LoginRepository
import com.policyboss.policybosspro.utils.AppVersionProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val appDataManager: AppDataManager,
    private val loginRepository: LoginRepository,
    private val appVersionProvider: AppVersionProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MenuUiState(appVersion = appVersionProvider.getAppVersion())
    )
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<MenuEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    init {
        observeUserData()
    }

    private fun observeUserData() {
        viewModelScope.launch {
            combine(
                appDataManager.userName,
                appDataManager.userSsId,
                appDataManager.userFbaId
            ) { name, ssId, fbaId ->
                Triple(name, ssId, fbaId)
            }.collect { (name, ssId, fbaId) ->
                _uiState.update { current ->
                    current.copy(
                        userName = name,
                        userSsId = ssId,
                        userFbaId = fbaId
                    )
                }
            }
        }
    }

    fun onAction(action: MenuAction) {
        when (action) {
            is MenuAction.OnLogoutClick -> {
                if (_uiState.value.isLoggingOut) return
                _uiState.update { it.copy(showLogoutDialog = true) }
            }
            is MenuAction.OnDismissLogoutDialog -> {
                if (_uiState.value.isLoggingOut) return
                _uiState.update { it.copy(showLogoutDialog = false) }
            }
            is MenuAction.OnConfirmLogout -> {
                executeLogout()
            }
        }
    }

    private fun executeLogout() {
        if (_uiState.value.isLoggingOut) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoggingOut = true,
                    logoutError = null
                )
            }

            val result = loginRepository.logout()
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoggingOut = false,
                            showLogoutDialog = false
                        )
                    }
                    _event.send(MenuEvent.NavigateToLogin)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoggingOut = false,
                            showLogoutDialog = false,
                            logoutError = error.localizedMessage ?: "Failed to log out"
                        )
                    }
                    _event.send(
                        MenuEvent.ShowError(
                            error.localizedMessage ?: "Failed to log out. Please try again."
                        )
                    )
                }
            )
        }
    }
}

