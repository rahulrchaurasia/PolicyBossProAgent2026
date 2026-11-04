package com.policyboss.policybosspro.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.policyboss.policybosspro.core.datastore.AppDataManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceIdProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appDataManager: AppDataManager
) {
    /**
     * Retrieves the persistent unique device ID.
     * Uses the cached ID from DataStore if present, otherwise reads ANDROID_ID
     * (with UUID fallback) and securely stores it in AppDataManager.
     */
    @SuppressLint("HardwareIds")
    suspend fun getDeviceId(): String {
        val cachedId = appDataManager.getDeviceId()
        if (cachedId.isNotBlank()) {
            return cachedId
        }

        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        val finalId = if (!androidId.isNullOrBlank()) {
            androidId
        } else {
            UUID.randomUUID().toString()
        }

        appDataManager.saveDeviceId(finalId)
        return finalId
    }
}

