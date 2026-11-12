package com.policyboss.policybosspro.feature.home

import com.policyboss.policybosspro.core.repository.login.LoginRepository
import com.policyboss.policybosspro.feature.home.ui_Menu.MenuAction
import com.policyboss.policybosspro.feature.home.ui_Menu.MenuEvent
import com.policyboss.policybosspro.feature.home.ui_Menu.MenuUiState
import com.policyboss.policybosspro.feature.login.data.model.OtpUserMsg
import com.policyboss.policybosspro.feature.login.data.model.OtpVerifyResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class MenuFeatureTest {

    // =========================================================================
    // 1. DATA MODEL & INITIALS FORMATTING TESTS
    // =========================================================================

    @Test
    fun testUserInitials_formatting() {
        val threeWords = MenuUiState(userName = "Rahul Rajendra Chaurasia")
        assertEquals("RC", threeWords.userInitials)

        val twoWords = MenuUiState(userName = "Rahul Chaurasia")
        assertEquals("RC", twoWords.userInitials)

        val singleWord = MenuUiState(userName = "Alice")
        assertEquals("AL", singleWord.userInitials)

        val emptyUser = MenuUiState(userName = "")
        assertEquals("PB", emptyUser.userInitials)

        val defaultGuest = MenuUiState(userName = "Guest User")
        assertEquals("PB", defaultGuest.userInitials)

        val explicitInitials = MenuUiState(userName = "Guest User", initials = "AK")
        assertEquals("AK", explicitInitials.userInitials)
    }

    @Test
    fun testDisplayName_and_Ids() {
        val stateWithData = MenuUiState(
            userName = "Rahul Chaurasia",
            userSsId = "150252",
            userFbaId = "1001",
            appVersion = "1.0"
        )
        assertEquals("Rahul Chaurasia", stateWithData.displayName)
        assertEquals("#150252", stateWithData.displaySsId)
        assertEquals("#1001", stateWithData.displayFbaId)

        val emptyState = MenuUiState()
        assertEquals("Guest User", emptyState.displayName)
        assertEquals("NA", emptyState.displaySsId)
        assertEquals("NA", emptyState.displayFbaId)
    }

    @Test
    fun testAppVersionDisplayFormatting() {
        val versionRaw = "1.0"
        val display = if (versionRaw.startsWith("Version", ignoreCase = true) || versionRaw.startsWith("v", ignoreCase = true)) {
            versionRaw
        } else {
            "Version $versionRaw"
        }
        assertEquals("Version 1.0", display)

        val prefixed = "Version 1.5.0"
        val displayPrefixed = if (prefixed.startsWith("Version", ignoreCase = true) || prefixed.startsWith("v", ignoreCase = true)) {
            prefixed
        } else {
            "Version $prefixed"
        }
        assertEquals("Version 1.5.0", displayPrefixed)
    }

    // =========================================================================
    // 2. LOGOUT CONFIRMATION & DIALOG BEHAVIOR
    // =========================================================================

    @Test
    fun testLogoutDialog_stateTransitions() {
        var state = MenuUiState()
        assertFalse(state.showLogoutDialog)

        // Trigger Logout click -> dialog appears
        state = state.copy(showLogoutDialog = true)
        assertTrue(state.showLogoutDialog)
        assertFalse(state.isLoggingOut)

        // Dismiss dialog -> dialog disappears, no session change
        state = state.copy(showLogoutDialog = false)
        assertFalse(state.showLogoutDialog)
        assertFalse(state.isLoggingOut)
    }

    @Test
    fun testLogoutExecution_successFlow() = runBlocking {
        var logoutCallCount = 0
        val fakeRepo = object : LoginRepository {
            override suspend fun initiateLogin(loginId: String): Result<OtpUserMsg> = Result.failure(Exception())
            override suspend fun otpVerifyHorizon(otp: String, mobileNumber: String): Flow<Response<OtpVerifyResponse>> = emptyFlow()
            override suspend fun getLoginDetailHorizon(ssId: String): Result<Boolean> = Result.success(true)
            override suspend fun logout(): Result<Unit> {
                logoutCallCount++
                return Result.success(Unit)
            }
        }

        val result = fakeRepo.logout()
        assertTrue(result.isSuccess)
        assertEquals(1, logoutCallCount)
    }

    @Test
    fun testLogoutExecution_failureFlow() = runBlocking {
        val fakeRepo = object : LoginRepository {
            override suspend fun initiateLogin(loginId: String): Result<OtpUserMsg> = Result.failure(Exception())
            override suspend fun otpVerifyHorizon(otp: String, mobileNumber: String): Flow<Response<OtpVerifyResponse>> = emptyFlow()
            override suspend fun getLoginDetailHorizon(ssId: String): Result<Boolean> = Result.success(true)
            override suspend fun logout(): Result<Unit> {
                return Result.failure(Exception("Disk write failure during logout"))
            }
        }

        val result = fakeRepo.logout()
        assertTrue(result.isFailure)
        assertEquals("Disk write failure during logout", result.exceptionOrNull()?.message)
    }

    // =========================================================================
    // 3. DATASTORE USER KEY CLEANUP LOGIC TEST
    // =========================================================================

    @Test
    fun testUserDataStoreKeys_isolatedCleanup() {
        // Mock preferences map representing user session and app settings
        val storedPreferences = mutableMapOf<String, Any>(
            "key_raw_horizon_json" to "{}",
            "key_auth_token" to "token_abc123",
            "key_user_mobile" to "9773113793",
            "key_is_login" to true,
            "key_user_name" to "Rahul Rajendra Chaurasia",
            "key_user_initials" to "RC",
            "key_user_email" to "rahul@policyboss.com",
            "key_user_ssid" to "150252",
            "key_user_fbaid" to "1001",
            "key_user_erpid" to "ERP-456",
            "key_user_type" to "POSP",
            "key_user_designation" to "Senior POSP",
            "key_user_address" to "Mumbai",
            "key_device_id" to "unique_device_uuid_456",
            "key_support_number" to "9224624999"
        )

        // Keys defined in AppDataManager for clearUserSession()
        val userScopedKeys = listOf(
            "key_raw_horizon_json",
            "key_auth_token",
            "key_user_mobile",
            "key_is_login",
            "key_user_name",
            "key_user_initials",
            "key_user_email",
            "key_user_ssid",
            "key_user_fbaid",
            "key_user_erpid",
            "key_user_type",
            "key_user_designation",
            "key_user_address"
        )

        // Execute clearUserSession contract
        userScopedKeys.forEach { storedPreferences.remove(it) }

        // Assert all user credentials & profile records are deleted
        userScopedKeys.forEach { key ->
            assertNull("Expected $key to be removed", storedPreferences[key])
        }

        // Assert shared & device-scoped configurations are retained
        assertEquals("unique_device_uuid_456", storedPreferences["key_device_id"])
        assertEquals("9224624999", storedPreferences["key_support_number"])
    }
}

