package com.policyboss.policybosspro.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Immutable data model representing the active user session stored in DataStore.
 */
data class UserSession(
    val name: String = "",
    val mobile: String = "",
    val email: String = "",
    val ssId: String = "",
    val isLoggedIn: Boolean = false,
    val authToken: String = ""
) {
    val isSessionValid: Boolean
        get() = isLoggedIn && ssId.isNotBlank()
}

@Singleton
class AppDataManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private const val DEFAULT_GUEST_NAME = "Guest User"
        private const val DEFAULT_SUPPORT_NUMBER = "9224624999"

        private val KEY_AUTH_TOKEN = stringPreferencesKey("key_auth_token")
        private val KEY_USER_MOBILE = stringPreferencesKey("key_user_mobile")
        private val KEY_IS_LOGIN = booleanPreferencesKey("key_is_login")
        private val KEY_USER_NAME = stringPreferencesKey("key_user_name")
        private val KEY_USER_EMAIL = stringPreferencesKey("key_user_email")
        private val KEY_USER_SSID = stringPreferencesKey("key_user_ssid")
        private val KEY_USER_FBAID = stringPreferencesKey("key_user_fbaid")
        private val KEY_USER_TYPE = stringPreferencesKey("key_user_type")
        private val KEY_DEVICE_ID = stringPreferencesKey("key_device_id")
        private val KEY_SUPPORT_NUMBER = stringPreferencesKey("key_support_number")
    }

    /**
     * Safe preferences stream that catches IOExceptions (corrupted file or disk read failure)
     * and emits emptyPreferences() as recommended by Android Jetpack architecture guidelines.
     */
    private val prefFlow: Flow<Preferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

    // =========================================================================
    // 1. REACTIVE GETTERS (Flow)
    // =========================================================================

    val authToken: Flow<String> = prefFlow.map { pref ->
        pref[KEY_AUTH_TOKEN] ?: ""
    }

    val userMobile: Flow<String> = prefFlow.map { pref ->
        pref[KEY_USER_MOBILE] ?: ""
    }

    val userSsId: Flow<String> = prefFlow.map { pref ->
        pref[KEY_USER_SSID] ?: ""
    }

    val userFbaId: Flow<String> = prefFlow.map { pref ->
        pref[KEY_USER_FBAID]?.takeIf { it.isNotBlank() } ?: "0"
    }

    val userType: Flow<String> = prefFlow.map { pref ->
        pref[KEY_USER_TYPE] ?: ""
    }

    val deviceId: Flow<String> = prefFlow.map { pref ->
        pref[KEY_DEVICE_ID] ?: ""
    }

    val isLogin: Flow<Boolean> = prefFlow.map { pref ->
        pref[KEY_IS_LOGIN] ?: false
    }

    val userName: Flow<String> = prefFlow.map { pref ->
        pref[KEY_USER_NAME]?.takeIf { it.isNotBlank() } ?: DEFAULT_GUEST_NAME
    }

    val userEmail: Flow<String> = prefFlow.map { pref ->
        pref[KEY_USER_EMAIL] ?: ""
    }

    val supportNumber: Flow<String> = prefFlow.map { pref ->
        pref[KEY_SUPPORT_NUMBER] ?: DEFAULT_SUPPORT_NUMBER
    }

    val userSession: Flow<UserSession> = prefFlow.map { pref ->
        UserSession(
            name = pref[KEY_USER_NAME] ?: "",
            mobile = pref[KEY_USER_MOBILE] ?: "",
            email = pref[KEY_USER_EMAIL] ?: "",
            ssId = pref[KEY_USER_SSID] ?: "",
            isLoggedIn = pref[KEY_IS_LOGIN] ?: false,
            authToken = pref[KEY_AUTH_TOKEN] ?: ""
        )
    }

    // =========================================================================
    // 2. DIRECT ONE-SHOT GETTERS (suspend functions for ViewModels/Repositories)
    // =========================================================================

    suspend fun getAuthToken(): String = authToken.first()

    suspend fun getUserMobile(): String = userMobile.first()

    suspend fun getUserSsId(): String = userSsId.first()

    suspend fun getSsId(): String = userSsId.first().takeIf { it.isNotBlank() } ?: "0"

    suspend fun getFbaId(): String = userFbaId.first().takeIf { it.isNotBlank() } ?: "0"

    suspend fun getUserType(): String = userType.first()

    suspend fun getDeviceId(): String = deviceId.first()

    suspend fun isUserLoggedIn(): Boolean = isLogin.first()

    suspend fun getUserName(): String = userName.first()

    suspend fun getUserEmail(): String = userEmail.first()

    suspend fun getSupportNumber(): String = supportNumber.first()

    suspend fun getUserSession(): UserSession = userSession.first()

    // =========================================================================
    // 3. ATOMIC BATCH SAVE
    // =========================================================================

    /**
     * Atomically saves user session details in a single DataStore transaction.
     * Prevents multiple consecutive disk I/O operations.
     */
    suspend fun saveUserSession(
        name: String? = null,
        mobile: String? = null,
        email: String? = null,
        ssId: String? = null,
        fbaId: String? = null,
        userType: String? = null,
        isLogin: Boolean? = null,
        authToken: String? = null
    ) {
        dataStore.edit { pref ->
            name?.let { pref[KEY_USER_NAME] = it }
            mobile?.let { pref[KEY_USER_MOBILE] = it }
            email?.let { pref[KEY_USER_EMAIL] = it }
            ssId?.let { pref[KEY_USER_SSID] = it }
            fbaId?.let { pref[KEY_USER_FBAID] = it }
            userType?.let { pref[KEY_USER_TYPE] = it }
            isLogin?.let { pref[KEY_IS_LOGIN] = it }
            authToken?.let { pref[KEY_AUTH_TOKEN] = it }
        }
    }

    // =========================================================================
    // 4. INDIVIDUAL SAVE METHODS
    // =========================================================================

    suspend fun saveAuthToken(token: String) {
        dataStore.edit { pref -> pref[KEY_AUTH_TOKEN] = token }
    }

    suspend fun saveUserMobile(mobile: String) {
        dataStore.edit { pref -> pref[KEY_USER_MOBILE] = mobile }
    }

    suspend fun saveLoginState(isLogin: Boolean) {
        dataStore.edit { pref -> pref[KEY_IS_LOGIN] = isLogin }
    }

    suspend fun saveUserName(name: String) {
        dataStore.edit { pref -> pref[KEY_USER_NAME] = name }
    }

    suspend fun saveUserEmail(email: String) {
        dataStore.edit { pref -> pref[KEY_USER_EMAIL] = email }
    }

    suspend fun saveUserSsId(ssId: String) {
        dataStore.edit { pref -> pref[KEY_USER_SSID] = ssId }
    }

    suspend fun saveUserFbaId(fbaId: String) {
        dataStore.edit { pref -> pref[KEY_USER_FBAID] = fbaId }
    }

    suspend fun saveDeviceId(deviceId: String) {
        dataStore.edit { pref -> pref[KEY_DEVICE_ID] = deviceId }
    }

    suspend fun saveSupportNumber(number: String) {
        dataStore.edit { pref -> pref[KEY_SUPPORT_NUMBER] = number }
    }

    // =========================================================================
    // 5. SESSION MANAGEMENT & CLEAR
    // =========================================================================

    /**
     * Clears user-specific authentication and profile data upon logout,
     * while retaining non-sensitive app configurations (e.g. support number).
     */
    suspend fun clearUserSession() {
        dataStore.edit { pref ->
            pref.remove(KEY_AUTH_TOKEN)
            pref.remove(KEY_USER_MOBILE)
            pref.remove(KEY_IS_LOGIN)
            pref.remove(KEY_USER_NAME)
            pref.remove(KEY_USER_EMAIL)
            pref.remove(KEY_USER_SSID)
            pref.remove(KEY_USER_FBAID)
            pref.remove(KEY_USER_TYPE)
        }
    }

    /**
     * Clears all stored preferences completely.
     */
    suspend fun clearSession() {
        dataStore.edit { pref ->
            pref.clear()
        }
    }
}
