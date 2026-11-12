package com.policyboss.policybosspro.core.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.policyboss.policybosspro.core.network.NetworkConstants
import com.policyboss.policybosspro.feature.login.data.model.EMP
import com.policyboss.policybosspro.feature.login.data.model.HR
import com.policyboss.policybosspro.feature.login.data.model.LoginNewResponse_DSAS_Horizon
import com.policyboss.policybosspro.feature.login.data.model.POSP
import com.policyboss.policybosspro.feature.login.data.model.POSP_USER
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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
        assertEquals("PB", session.initials)
        assertEquals("", session.mobile)
        assertEquals("", session.ssId)
        assertEquals("0", session.fbaId)
        assertEquals("0", session.erpId)
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

    @Test
    fun testSaveLoginHorizonResponse_persistsFullProfileAndThreeWordInitials() = runBlocking {
        val horizonResponse = LoginNewResponse_DSAS_Horizon(
            status = "SUCCESS",
            user_type = "POSP",
            Ss_Id = "150252",
            POSP_USER = POSP_USER(
                Name_On_PAN = "Rahul Rajendra Chaurasia",
                Mobile_No = "9773113793",
                Email_Id = "rahul.chaurasia@policyboss.com",
                Erp_Id = "ERP-456",
                Permanant_Add1 = "12B Poonam Nagar",
                Permanant_City = "Mumbai",
                Permanant_State = "Maharashtra",
                Permanant_Pincode = "400063"
            ),
            POSP = POSP(
                Fba_Id = "1001",
                Ss_Id = "150252"
            ),
            HR = HR(Designation = "Senior POSP Advisor")
        )

        appDataManager.saveLoginHorizonResponse(horizonResponse)

        assertTrue(appDataManager.isUserLoggedIn())
        assertEquals("Rahul Rajendra Chaurasia", appDataManager.getUserName())
        assertEquals("RC", appDataManager.getUserInitials())
        assertEquals("9773113793", appDataManager.getUserMobile())
        assertEquals("rahul.chaurasia@policyboss.com", appDataManager.getUserEmail())
        assertEquals("150252", appDataManager.getUserSsId())
        assertEquals("1001", appDataManager.getFbaId())
        assertEquals("ERP-456", appDataManager.getErpId())
        assertEquals("POSP", appDataManager.getUserType())
        assertEquals("Senior POSP Advisor", appDataManager.getUserDesignation())
        assertEquals(
            "12B Poonam Nagar, Mumbai, Maharashtra, 400063",
            appDataManager.getUserAddress()
        )

        val session = appDataManager.getUserSession()
        assertEquals("Rahul Rajendra Chaurasia", session.name)
        assertEquals("RC", session.initials)
        assertEquals("150252", session.ssId)
        assertEquals("1001", session.fbaId)
        assertEquals("ERP-456", session.erpId)
        assertEquals("POSP", session.userType)
        assertEquals("Senior POSP Advisor", session.designation)
        assertTrue(session.isSessionValid)

        val storedRaw = appDataManager.getLoginHorizonResponse()
        assertNotNull(storedRaw)
        assertEquals("POSP", storedRaw?.user_type)
        assertEquals("Rahul Rajendra Chaurasia", storedRaw?.POSP_USER?.Name_On_PAN)
    }

    @Test
    fun testSaveLoginHorizonResponse_empUserType_andClearUserSessionRemovesAllProfileKeys() = runBlocking {
        val empResponse = LoginNewResponse_DSAS_Horizon(
            status = "SUCCESS",
            user_type = "EMP",
            EMP = EMP(
                Emp_Id = "2002",
                FBA_ID = "3003",
                Emp_Name = "Suresh",
                Mobile_Number = "9876543210",
                Email_Id = "suresh@policyboss.com"
            ),
            HR = HR(Designation = "Regional Manager")
        )

        appDataManager.saveSupportNumber("1800999888")
        appDataManager.saveLoginHorizonResponse(empResponse)

        assertEquals("Suresh", appDataManager.getUserName())
        assertEquals("SU", appDataManager.getUserInitials())
        assertEquals("2002", appDataManager.getUserSsId())
        assertEquals("3003", appDataManager.getFbaId())
        assertEquals("Regional Manager", appDataManager.getUserDesignation())

        appDataManager.clearUserSession()

        assertFalse(appDataManager.isUserLoggedIn())
        assertEquals("Guest User", appDataManager.getUserName())
        assertEquals("PB", appDataManager.getUserInitials())
        assertEquals("", appDataManager.getUserSsId())
        assertEquals("0", appDataManager.getFbaId())
        assertEquals("0", appDataManager.getErpId())
        assertEquals("", appDataManager.getUserType())
        assertEquals("", appDataManager.getUserDesignation())
        assertEquals("", appDataManager.getUserAddress())
        assertNull(appDataManager.getLoginHorizonResponse())
        assertEquals("1800999888", appDataManager.getSupportNumber())
    }
}

