package com.policyboss.policybosspro.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.policyboss.policybosspro.core.utils.UserProfileParser
import com.policyboss.policybosspro.feature.login.data.model.LoginNewResponse_DSAS_Horizon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Immutable data model representing the active user session stored in DataStore.
 */
data class UserSession(
    val name: String = "",
    val initials: String = UserProfileParser.DEFAULT_INITIALS,
    val mobile: String = "",
    val email: String = "",
    val ssId: String = "",
    val fbaId: String = "0",
    val erpId: String = "0",
    val userType: String = "",
    val designation: String = "",
    val address: String = "",
    val isLoggedIn: Boolean = false,
    val authToken: String = ""
) {
    val isSessionValid: Boolean
        get() = isLoggedIn && ssId.isNotBlank() && ssId != "0"
}

@Singleton
class AppDataManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private const val DEFAULT_GUEST_NAME = "Guest User"
        private const val DEFAULT_SUPPORT_NUMBER = "9224624999"

        private val horizonJson = Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        }

        private val KEY_AUTH_TOKEN = stringPreferencesKey("key_auth_token")
        private val KEY_USER_MOBILE = stringPreferencesKey("key_user_mobile")
        private val KEY_IS_LOGIN = booleanPreferencesKey("key_is_login")
        private val KEY_USER_NAME = stringPreferencesKey("key_user_name")
        private val KEY_USER_INITIALS = stringPreferencesKey("key_user_initials")
        private val KEY_USER_EMAIL = stringPreferencesKey("key_user_email")
        private val KEY_USER_SSID = stringPreferencesKey("key_user_ssid")
        private val KEY_USER_FBAID = stringPreferencesKey("key_user_fbaid")
        private val KEY_USER_ERPID = stringPreferencesKey("key_user_erpid")
        private val KEY_USER_TYPE = stringPreferencesKey("key_user_type")
        private val KEY_USER_DESIGNATION = stringPreferencesKey("key_user_designation")
        private val KEY_USER_ADDRESS = stringPreferencesKey("key_user_address")
        private val KEY_DEVICE_ID = stringPreferencesKey("key_device_id")
        private val KEY_SUPPORT_NUMBER = stringPreferencesKey("key_support_number")
        private val KEY_RAW_HORIZON_JSON = stringPreferencesKey("key_raw_horizon_json")
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

    val userErpId: Flow<String> = prefFlow.map { pref ->
        pref[KEY_USER_ERPID]?.takeIf { it.isNotBlank() } ?: "0"
    }

    val userType: Flow<String> = prefFlow.map { pref ->
        pref[KEY_USER_TYPE] ?: ""
    }

    val userDesignation: Flow<String> = prefFlow.map { pref ->
        pref[KEY_USER_DESIGNATION] ?: ""
    }

    val userAddress: Flow<String> = prefFlow.map { pref ->
        pref[KEY_USER_ADDRESS] ?: ""
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

    val userInitials: Flow<String> = prefFlow.map { pref ->
        pref[KEY_USER_INITIALS]?.takeIf { it.isNotBlank() }
            ?: UserProfileParser.extractInitials(pref[KEY_USER_NAME].orEmpty())
    }

    val userEmail: Flow<String> = prefFlow.map { pref ->
        pref[KEY_USER_EMAIL] ?: ""
    }

    val supportNumber: Flow<String> = prefFlow.map { pref ->
        pref[KEY_SUPPORT_NUMBER] ?: DEFAULT_SUPPORT_NUMBER
    }

    val userSession: Flow<UserSession> = prefFlow.map { pref ->
        val storedName = pref[KEY_USER_NAME].orEmpty()
        val resolvedInitials = pref[KEY_USER_INITIALS]?.takeIf { it.isNotBlank() }
            ?: UserProfileParser.extractInitials(storedName)

        UserSession(
            name = storedName,
            initials = resolvedInitials,
            mobile = pref[KEY_USER_MOBILE].orEmpty(),
            email = pref[KEY_USER_EMAIL].orEmpty(),
            ssId = pref[KEY_USER_SSID].orEmpty(),
            fbaId = pref[KEY_USER_FBAID]?.takeIf { it.isNotBlank() } ?: "0",
            erpId = pref[KEY_USER_ERPID]?.takeIf { it.isNotBlank() } ?: "0",
            userType = pref[KEY_USER_TYPE].orEmpty(),
            designation = pref[KEY_USER_DESIGNATION].orEmpty(),
            address = pref[KEY_USER_ADDRESS].orEmpty(),
            isLoggedIn = pref[KEY_IS_LOGIN] ?: false,
            authToken = pref[KEY_AUTH_TOKEN].orEmpty()
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

    suspend fun getErpId(): String = userErpId.first().takeIf { it.isNotBlank() } ?: "0"

    suspend fun getUserType(): String = userType.first()

    suspend fun getUserDesignation(): String = userDesignation.first()

    suspend fun getUserAddress(): String = userAddress.first()

    suspend fun getDeviceId(): String = deviceId.first()

    suspend fun isUserLoggedIn(): Boolean = isLogin.first()

    suspend fun getUserName(): String = userName.first()

    suspend fun getUserInitials(): String = userInitials.first()

    suspend fun getUserEmail(): String = userEmail.first()

    suspend fun getSupportNumber(): String = supportNumber.first()

    suspend fun getUserSession(): UserSession = userSession.first()

    suspend fun getLoginHorizonResponse(): LoginNewResponse_DSAS_Horizon? {
        val raw = prefFlow.first()[KEY_RAW_HORIZON_JSON]?.takeIf { it.isNotBlank() } ?: return null
        return runCatching {
            horizonJson.decodeFromString<LoginNewResponse_DSAS_Horizon>(raw)
        }.getOrNull()
    }

    // =========================================================================
    // 3. SAVE & PARSE HORIZON RESPONSE / ATOMIC BATCH SAVE
    // =========================================================================

    /**
     * Parses and persists the full [LoginNewResponse_DSAS_Horizon] model atomically.
     * Computes first + last initials via [UserProfileParser.extractInitials] so 3-word names
     * (e.g. "Rahul Rajendra Chaurasia") resolve to "RC".
     */
    suspend fun saveLoginHorizonResponse(
        response: LoginNewResponse_DSAS_Horizon?,
        fallbackSsId: String = ""
    ) {
        if (response == null) return

        val rawJson = runCatching { horizonJson.encodeToString(response) }.getOrDefault("")
        val name = UserProfileParser.extractName(response)
        val initials = UserProfileParser.extractInitials(name)
        val mobile = UserProfileParser.extractMobile(response)
        val email = UserProfileParser.extractEmail(response)
        val parsedSsId = UserProfileParser.extractSsId(response)
        val ssId = if (parsedSsId == "0" && fallbackSsId.isNotBlank()) fallbackSsId.trim() else parsedSsId
        val fbaId = UserProfileParser.extractFbaId(response)
        val erpId = UserProfileParser.extractErpId(response)
        val designation = UserProfileParser.extractDesignation(response)
        val address = UserProfileParser.extractAddress(response)
        val userType = response.user_type.orEmpty().trim()

        dataStore.edit { pref ->
            if (rawJson.isNotBlank()) {
                pref[KEY_RAW_HORIZON_JSON] = rawJson
            }
            pref[KEY_USER_NAME] = name
            pref[KEY_USER_INITIALS] = initials
            pref[KEY_USER_MOBILE] = mobile
            pref[KEY_USER_EMAIL] = email
            pref[KEY_USER_SSID] = ssId
            pref[KEY_USER_FBAID] = fbaId
            pref[KEY_USER_ERPID] = erpId
            pref[KEY_USER_TYPE] = userType
            pref[KEY_USER_DESIGNATION] = designation
            pref[KEY_USER_ADDRESS] = address
            pref[KEY_IS_LOGIN] = true
        }
    }

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
        authToken: String? = null,
        initials: String? = null,
        erpId: String? = null,
        designation: String? = null,
        address: String? = null
    ) {
        dataStore.edit { pref ->
            name?.let {
                pref[KEY_USER_NAME] = it
                pref[KEY_USER_INITIALS] = initials ?: UserProfileParser.extractInitials(it)
            } ?: initials?.let { pref[KEY_USER_INITIALS] = it }
            mobile?.let { pref[KEY_USER_MOBILE] = it }
            email?.let { pref[KEY_USER_EMAIL] = it }
            ssId?.let { pref[KEY_USER_SSID] = it }
            fbaId?.let { pref[KEY_USER_FBAID] = it }
            userType?.let { pref[KEY_USER_TYPE] = it }
            isLogin?.let { pref[KEY_IS_LOGIN] = it }
            authToken?.let { pref[KEY_AUTH_TOKEN] = it }
            erpId?.let { pref[KEY_USER_ERPID] = it }
            designation?.let { pref[KEY_USER_DESIGNATION] = it }
            address?.let { pref[KEY_USER_ADDRESS] = it }
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
        dataStore.edit { pref ->
            pref[KEY_USER_NAME] = name
            pref[KEY_USER_INITIALS] = UserProfileParser.extractInitials(name)
        }
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
    // CONTACT SYNC ONBOARDING STATUS
    // =========================================================================

    /**
     * Checks if contact synchronization onboarding has been successfully completed
     * for the specific user account identified by [ssId].
     */
    suspend fun isContactSyncCompleted(ssId: String): Boolean {
        if (ssId.isBlank() || ssId == "0") return false
        val key = booleanPreferencesKey("key_is_contact_sync_completed_$ssId")
        return prefFlow.first()[key] ?: false
    }

    /**
     * Persists the contact synchronization completion status for the account identified by [ssId].
     */
    suspend fun setContactSyncCompleted(ssId: String, completed: Boolean) {
        if (ssId.isNotBlank() && ssId != "0") {
            val key = booleanPreferencesKey("key_is_contact_sync_completed_$ssId")
            dataStore.edit { pref ->
                pref[key] = completed
            }
        }
    }

    // =========================================================================
    // 5. SESSION MANAGEMENT & CLEAR
    // =========================================================================

    /**
     * Clears user-specific authentication and profile data upon logout,
     * including contact synchronization status, so a fresh login must
     * complete the mandatory synchronization journey again.
     */
    suspend fun clearUserSession() {
        dataStore.edit { pref ->
            pref.remove(KEY_RAW_HORIZON_JSON)
            pref.remove(KEY_AUTH_TOKEN)
            pref.remove(KEY_USER_MOBILE)
            pref.remove(KEY_IS_LOGIN)
            pref.remove(KEY_USER_NAME)
            pref.remove(KEY_USER_INITIALS)
            pref.remove(KEY_USER_EMAIL)
            pref.remove(KEY_USER_SSID)
            pref.remove(KEY_USER_FBAID)
            pref.remove(KEY_USER_ERPID)
            pref.remove(KEY_USER_TYPE)
            pref.remove(KEY_USER_DESIGNATION)
            pref.remove(KEY_USER_ADDRESS)

            val syncKeys = pref.asMap().keys.filter { it.name.startsWith("key_is_contact_sync_completed_") }
            syncKeys.forEach { key ->
                pref.remove(key)
            }
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
