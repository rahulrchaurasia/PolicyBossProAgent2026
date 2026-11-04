package com.policyboss.policybosspro.feature.syncContact.model

import com.policyboss.policybosspro.feature.dummyData.AppDummyData

enum class SyncPhase {
    CONTACT_FETCHING,
    CONTACT_UPLOADING,
    CONTACT_COMPLETED,
    CALL_LOG_FETCHING,
    CALL_LOG_UPLOADING,
    CALL_LOG_COMPLETED
}

data class SyncProcessingState(
    val isSyncing: Boolean = true,
    val isSuccess: Boolean = false,
    val currentPhase: SyncPhase = SyncPhase.CONTACT_FETCHING,
    val progress: Float = 0f,
    val processedRecords: Int = 0,
    val totalRecords: Int = 0,
    val contactsFound: Int = 0,
    val statusMessage: String = "Fetching Contacts...",
    val avgEarnings: String = AppDummyData.SyncContacts.AVG_EARNINGS,
    val earningPotential: String = AppDummyData.SyncContacts.EARNING_POTENTIAL,
    val bossCoins: String = AppDummyData.SyncContacts.BOSS_COINS,
    val errorMessage: String? = null
)

sealed interface SyncProcessingAction {
    data object StartSync : SyncProcessingAction
    data object CompleteProfileClicked : SyncProcessingAction
    data object RetrySync : SyncProcessingAction
}

sealed interface SyncProcessingEvent {
    data object NavigateToHome : SyncProcessingEvent
    data class ShowError(val message: String) : SyncProcessingEvent
}
