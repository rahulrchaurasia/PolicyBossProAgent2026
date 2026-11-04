package com.policyboss.policybosspro.feature.syncContact.model

import com.policyboss.policybosspro.feature.dummyData.AppDummyData

data class SyncContactsUiState(
    val avgEarnings: String = AppDummyData.SyncContacts.AVG_EARNINGS,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed interface SyncContactsAction {
    data object OnSyncContactsClick : SyncContactsAction
}

sealed interface SyncContactsEvent {
    data object RequestContactPermission : SyncContactsEvent
    data object NavigateNext : SyncContactsEvent
    data class ShowError(val message: String) : SyncContactsEvent
}

