package com.policyboss.policybosspro.feature.syncContact.data.api

import com.policyboss.policybosspro.feature.syncContact.data.model.SaveCheckboxRequestEntity
import com.policyboss.policybosspro.feature.syncContact.data.model.SaveCheckboxResponseEntity
import com.policyboss.policybosspro.feature.syncContact.data.model.SyncContactAgreementResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SyncContactApi {

    @GET("sync_contact/get_sync_contact_agreements")
    suspend fun getSyncContactAgreements(
        @Query("ss_id") ssId: String,
        @Query("device_code") deviceCode: String,
        @Query("app_version") appVersion: String,
        @Query("fbaid") fbaId: String
    ): Response<SyncContactAgreementResponse>

    @POST("postservicecall/sync_contacts/online_agreement")
    suspend fun saveCheckboxDetails(
        @Body request: SaveCheckboxRequestEntity
    ): Response<SaveCheckboxResponseEntity>
}

