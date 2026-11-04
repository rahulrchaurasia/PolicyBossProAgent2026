package com.policyboss.policybosspro.core.repository.syncContact

import com.policyboss.policybosspro.feature.syncContact.data.model.SyncContactAgreementItem

interface SyncContactRepository {
    /**
     * Calls GET /sync_contact/get_sync_contact_agreements with query parameters:
     * ss_id, device_code, app_version, fbaid resolved from AppDataManager, DeviceIdProvider, and AppVersionProvider.
     */
    suspend fun getSyncContactAgreements(): Result<List<SyncContactAgreementItem>>

    /**
     * Calls POST /postservicecall/sync_contacts/online_agreement to persist the user's communication
     * preferences and agreement.
     */
    suspend fun saveAgreementCheckbox(isSms: Boolean, isCall: Boolean): Result<Boolean>
}

