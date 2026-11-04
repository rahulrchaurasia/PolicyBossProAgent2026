package com.policyboss.policybosspro.core.repository.syncContact

import com.policyboss.policybosspro.core.datastore.AppDataManager
import com.policyboss.policybosspro.feature.syncContact.data.api.SyncContactApi
import com.policyboss.policybosspro.feature.syncContact.data.model.SaveCheckboxRequestEntity
import com.policyboss.policybosspro.feature.syncContact.data.model.SyncContactAgreementItem
import com.policyboss.policybosspro.utils.AppVersionProvider
import com.policyboss.policybosspro.utils.DeviceIdProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncContactRepositoryImpl @Inject constructor(
    private val syncContactApi: SyncContactApi,
    private val appDataManager: AppDataManager,
    private val deviceIdProvider: DeviceIdProvider,
    private val appVersionProvider: AppVersionProvider
) : SyncContactRepository {

    override suspend fun getSyncContactAgreements(): Result<List<SyncContactAgreementItem>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val ssId = appDataManager.getSsId().ifBlank { "0" }
                val fbaId = appDataManager.getFbaId().ifBlank { "0" }
                val deviceCode = deviceIdProvider.getDeviceId()
                val appVersion = appVersionProvider.getAppVersion()

                val response = syncContactApi.getSyncContactAgreements(
                    ssId = ssId,
                    deviceCode = deviceCode,
                    appVersion = appVersion,
                    fbaId = fbaId
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    val items = body?.msg ?: emptyList()
                    items
                } else {
                    throw IllegalStateException("API error: ${response.code()} ${response.message()}")
                }
            }
        }

    override suspend fun saveAgreementCheckbox(
        isSms: Boolean,
        isCall: Boolean
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            val ssId = appDataManager.getSsId().toIntOrNull() ?: 0
            val fbaId = appDataManager.getFbaId().toIntOrNull() ?: 0
            val deviceCode = deviceIdProvider.getDeviceId()
            val appVersion = appVersionProvider.getAppVersion()

            val request = SaveCheckboxRequestEntity(
                fba_id = fbaId,
                is_sms = if (isSms) "yes" else "no",
                is_call = if (isCall) "yes" else "no",
                online_agreement = "online_agreement",
                ss_id = ssId,
                app_version = appVersion,
                device_code = deviceCode
            )

            val response = syncContactApi.saveCheckboxDetails(request)
            if (response.isSuccessful) {
                true
            } else {
                throw IllegalStateException("Save error: ${response.code()} ${response.message()}")
            }
        }
    }
}

