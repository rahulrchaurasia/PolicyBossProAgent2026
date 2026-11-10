package com.policyboss.policybosspro.core.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.policyboss.policybosspro.core.network.NetworkConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class AppDataManagerTest {

    private lateinit var appDataManager: AppDataManager

    @Before
    fun setup() {
        val testFile = File.createTempFile("test_datastore", ".preferences_pb")
        testFile.deleteOnExit()

        val testDataStore = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.Unconfined + Job()),
            produceFile = { testFile }
        )

        appDataManager = AppDataManager(testDataStore)
    }

    @Test
    fun testNetworkConstants_baseUrlIsSingleSourceOfTruth() {
        assertEquals("https://horizon.policyboss.com:5443/", NetworkConstants.BASE_URL)
        assertEquals("1234567890", NetworkConstants.DEFAULT_TOKEN)
        assertTrue(NetworkConstants.BASE_URL.endsWith("/"))
    }

    @Test
    fun testDefaultValues_whenEmpty() = runBlocking {
        assertEquals("Guest User", appDataManager.getUserName())
        assertEquals("", appDataManager.getUserMobile())
        assertEquals("", appDataManager.getUserEmail())
        assertEquals("", appDataManager.getUserSsId())
        assertEquals("", appDataManager.getAuthToken())
        assertEquals("9224624999", appDataManager.getSupportNumber())
        assertFalse(appDataManager.isUserLoggedIn())

        val session = appDataManager.getUserSession()
        assertEquals("", session.name)
        assertEquals("", session.mobile)
        assertEquals("", session.ssId)
        assertFalse(session.isLoggedIn)
        assertFalse(session.isSessionValid)
    }

    @Test
    fun testSaveAndGetUserSession_atomicBatch() = runBlocking {
        appDataManager.saveUserSession(
            name = "Rahul Chaurasia",
            mobile = "9773113793",
            email = "rahul.chaurasia@policyboss.com",
            ssId = "150252",
            isLogin = true,
            authToken = "token_abc123"
        )

        assertEquals("Rahul Chaurasia", appDataManager.getUserName())
        assertEquals("9773113793", appDataManager.getUserMobile())
        assertEquals("rahul.chaurasia@policyboss.com", appDataManager.getUserEmail())
        assertEquals("150252", appDataManager.getUserSsId())
        assertEquals("token_abc123", appDataManager.getAuthToken())
        assertTrue(appDataManager.isUserLoggedIn())

        val session = appDataManager.getUserSession()
        assertEquals("Rahul Chaurasia", session.name)
        assertEquals("9773113793", session.mobile)
        assertEquals("rahul.chaurasia@policyboss.com", session.email)
        assertEquals("150252", session.ssId)
        assertEquals("token_abc123", session.authToken)
        assertTrue(session.isLoggedIn)
        assertTrue(session.isSessionValid)
    }

    @Test
    fun testIndividualSaveMethods() = runBlocking {
        appDataManager.saveUserName("Dinesh Das")
        assertEquals("Dinesh Das", appDataManager.getUserName())

        appDataManager.saveUserMobile("9910034425")
        assertEquals("9910034425", appDataManager.getUserMobile())

        appDataManager.saveUserEmail("dineshdas@policyboss.com")
        assertEquals("dineshdas@policyboss.com", appDataManager.getUserEmail())

        appDataManager.saveUserSsId("1449")
        assertEquals("1449", appDataManager.getUserSsId())

        appDataManager.saveLoginState(true)
        assertTrue(appDataManager.isUserLoggedIn())
    }

    @Test
    fun testClearUserSession_clearsAuthPreservingSupportNumber() = runBlocking {
        appDataManager.saveUserSession(
            name = "Test User",
            mobile = "9999999999",
            email = "test@policyboss.com",
            ssId = "9999",
            isLogin = true
        )
        appDataManager.saveSupportNumber("1800123456")

        appDataManager.clearUserSession()

        assertEquals("Guest User", appDataManager.getUserName())
        assertEquals("", appDataManager.getUserMobile())
        assertEquals("", appDataManager.getUserEmail())
        assertEquals("", appDataManager.getUserSsId())
        assertFalse(appDataManager.isUserLoggedIn())

        // Support number preserved
        assertEquals("1800123456", appDataManager.getSupportNumber())
    }

    @Test
    fun testContactSyncCompleted_accountSpecific() = runBlocking {
        // Blank or zero returns false
        assertFalse(appDataManager.isContactSyncCompleted(""))
        assertFalse(appDataManager.isContactSyncCompleted("0"))

        // Account A default is false
        assertFalse(appDataManager.isContactSyncCompleted("150252"))

        // Set Account A to true
        appDataManager.setContactSyncCompleted("150252", true)
        assertTrue(appDataManager.isContactSyncCompleted("150252"))

        // Account B remains false (isolated per ssId)
        assertFalse(appDataManager.isContactSyncCompleted("999999"))
    }

    @Test
    fun testClearUserSession_clearsContactSyncCompleted() = runBlocking {
        // Mark contact sync complete for Account A and Account B
        appDataManager.setContactSyncCompleted("150252", true)
        appDataManager.setContactSyncCompleted("999999", true)
        assertTrue(appDataManager.isContactSyncCompleted("150252"))
        assertTrue(appDataManager.isContactSyncCompleted("999999"))

        // When user logs out and session is cleared
        appDataManager.clearUserSession()

        // Sync completion flags must be completely reset so fresh login triggers mandatory flow
        assertFalse(appDataManager.isContactSyncCompleted("150252"))
        assertFalse(appDataManager.isContactSyncCompleted("999999"))
    }
}

