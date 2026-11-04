package com.policyboss.policybosspro.feature.syncContact.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.feature.syncContact.model.SyncContactsAction
import com.policyboss.policybosspro.feature.syncContact.model.SyncContactsEvent
import com.policyboss.policybosspro.feature.syncContact.model.SyncContactsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncContactsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SyncContactsUiState())
    val uiState: StateFlow<SyncContactsUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<SyncContactsEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onAction(action: SyncContactsAction) {
        when (action) {
            is SyncContactsAction.OnSyncContactsClick -> {
                viewModelScope.launch {
                    _uiEvent.send(SyncContactsEvent.RequestContactPermission)
                }
            }
        }
    }

    fun onPermissionGranted() {
        viewModelScope.launch {
            _uiEvent.send(SyncContactsEvent.NavigateNext)
        }
    }
}

