package com.policyboss.policybosspro.feature.login

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.policyboss.policybosspro.core.datastore.AppDataManager
import com.policyboss.policybosspro.core.repository.login.LoginRepository
import com.policyboss.policybosspro.feature.login.data.model.OtpUserMsg
import com.policyboss.policybosspro.feature.login.data.model.OtpVerifyResponse
import com.policyboss.policybosspro.feature.login.util.LoginIdentifierType
import com.policyboss.policybosspro.feature.login.util.LoginIdentifierValidator
import com.policyboss.policybosspro.feature.login.util.ValidationResult
import com.policyboss.policybosspro.feature.login.viewmodel.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.JsonPrimitive
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class LoginFeatureTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createLoginViewModel(fakeRepo: LoginRepository): LoginViewModel {
        val testFile = File.createTempFile("test_datastore_login", ".preferences_pb")
        testFile.deleteOnExit()
        val testDataStore = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(testDispatcher + Job()),
            produceFile = { testFile }
        )
        val appDataManager = AppDataManager(testDataStore)
        return LoginViewModel(
            loginRepository = fakeRepo,
            appDataManager = appDataManager
        )
    }

    // =========================================================================
    // 1. IDENTIFIER VALIDATION TESTS (All 4 types)
    // =========================================================================

    @Test
    fun testMobileNumberValidation_success() {
        val result = LoginIdentifierValidator.validate("9773113793")
        assertTrue(result is ValidationResult.Success)
        val success = result as ValidationResult.Success
        assertEquals(LoginIdentifierType.MOBILE, success.type)
        assertEquals("9773113793", success.sanitizedId)
    }

    @Test
    fun testEmailValidation_success() {
        val result = LoginIdentifierValidator.validate("rahul.chaurasia@policyboss.com")
        assertTrue(result is ValidationResult.Success)
        val success = result as ValidationResult.Success
        assertEquals(LoginIdentifierType.EMAIL, success.type)
        assertEquals("rahul.chaurasia@policyboss.com", success.sanitizedId)
    }

    @Test
    fun testEmailValidation_invalidFormat() {
        val result = LoginIdentifierValidator.validate("rahul@invalid")
        assertTrue(result is ValidationResult.Error)
        val error = result as ValidationResult.Error
        assertEquals("Please enter a valid email address.", error.message)
    }

    @Test
    fun testPospNumberValidation_success() {
        val result = LoginIdentifierValidator.validate("150252")
        assertTrue(result is ValidationResult.Success)
        val success = result as ValidationResult.Success
        assertEquals(LoginIdentifierType.POSP_NUMBER, success.type)
        assertEquals("150252", success.sanitizedId)
    }

    @Test
    fun testErpCodeValidation_success() {
        val result = LoginIdentifierValidator.validate("ERP-99100")
        assertTrue(result is ValidationResult.Success)
        val success = result as ValidationResult.Success
        assertEquals(LoginIdentifierType.ERP_CODE, success.type)
        assertEquals("ERP-99100", success.sanitizedId)
    }

    @Test
    fun testEmptyInputValidation_error() {
        val resultEmpty = LoginIdentifierValidator.validate("")
        assertTrue(resultEmpty is ValidationResult.Error)

        val resultSpaces = LoginIdentifierValidator.validate("    ")
        assertTrue(resultSpaces is ValidationResult.Error)
    }

    // =========================================================================
    // 2. REPOSITORY & HORIZON OTP FLOW TESTS
    // =========================================================================

    private class FakeLoginRepository(
        var initiateLoginResult: Result<OtpUserMsg> = Result.success(
            OtpUserMsg(
                name = "RAHUL R CHAURASIA",
                mobileNo = "9773113793",
                ssId = JsonPrimitive(150252),
                otpStatus = "SUCCESS",
                emailId = "rahul.chaurasia@policyboss.com"
            )
        ),
        var otpVerifyResponse: Response<OtpVerifyResponse> = Response.success(
            OtpVerifyResponse(msg = "SUCCESS")
        ),
        var loginDetailResult: Result<Boolean> = Result.success(true)
    ) : LoginRepository {
        var initiateLoginCalledWith: String? = null
        var initiateLoginCallsCount: Int = 0
        var otpVerifyCalledWith: Pair<String, String>? = null
        var getLoginDetailCalledWith: String? = null

        override suspend fun initiateLogin(loginId: String): Result<OtpUserMsg> {
            initiateLoginCallsCount++
            initiateLoginCalledWith = loginId
            return initiateLoginResult
        }

        override suspend fun otpVerifyHorizon(
            otp: String,
            loginInput: String
        ): Flow<Response<OtpVerifyResponse>> {
            otpVerifyCalledWith = Pair(otp, loginInput)
            return flowOf(otpVerifyResponse)
        }

        override suspend fun getLoginDetailHorizon(ssId: String): Result<Boolean> {
            getLoginDetailCalledWith = ssId
            return loginDetailResult
        }

        override suspend fun logout(): Result<Unit> {
            return Result.success(Unit)
        }
    }

    @Test
    fun testInitiateLoginSuccess_returnsUserDetails() = runBlocking {
        val fakeRepo = FakeLoginRepository()

        val initiateResult = fakeRepo.initiateLogin("9773113793")
        assertTrue(initiateResult.isSuccess)

        val user = initiateResult.getOrNull()
        assertEquals("RAHUL R CHAURASIA", user?.name)
        assertEquals("9773113793", user?.mobileNo)
        assertEquals("SUCCESS", user?.otpStatus)
        assertEquals("150252", user?.ssId?.toString()?.trim()?.removeSurrounding("\""))
        assertEquals("rahul.chaurasia@policyboss.com", user?.emailId)
        assertEquals("9773113793", fakeRepo.initiateLoginCalledWith)
    }

    @Test
    fun testInitiateLoginFailure_handledCorrectly() = runBlocking {
        val fakeRepo = FakeLoginRepository(
            initiateLoginResult = Result.failure(Exception("User not registered."))
        )

        val initiateResult = fakeRepo.initiateLogin("invalid_user")
        assertFalse(initiateResult.isSuccess)
        assertEquals("User not registered.", initiateResult.exceptionOrNull()?.message)
    }

    @Test
    fun testOtpVerifyHorizon_successFlow() = runBlocking {
        val fakeRepo = FakeLoginRepository()

        val flow = fakeRepo.otpVerifyHorizon(
            otp = "8902",
            loginInput = "rahul.chaurasia@policyboss.com"
        )
        val response = flow.first()

        assertTrue(response.isSuccessful)
        assertEquals("SUCCESS", response.body()?.msg)
        assertTrue(response.body()?.isSuccess == true)
        assertEquals(Pair("8902", "rahul.chaurasia@policyboss.com"), fakeRepo.otpVerifyCalledWith)
    }

    @Test
    fun testOtpVerifyHorizon_invalidCodeFailure() = runBlocking {
        val fakeRepo = FakeLoginRepository(
            otpVerifyResponse = Response.success(
                OtpVerifyResponse(msg = "FAILED")
            )
        )

        val flow = fakeRepo.otpVerifyHorizon(
            otp = "0000",
            loginInput = "9773113793"
        )
        val response = flow.first()

        assertTrue(response.isSuccessful)
        assertEquals("FAILED", response.body()?.msg)
        assertFalse(response.body()?.isSuccess == true)
    }

    @Test
    fun testGetLoginDetailHorizon_success() = runBlocking {
        val fakeRepo = FakeLoginRepository()

        val result = fakeRepo.getLoginDetailHorizon("150252")
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
        assertEquals("150252", fakeRepo.getLoginDetailCalledWith)
    }

    @Test
    fun testLoginNewResponse_DSAS_Horizon_pospExtraction() {
        val posp = com.policyboss.policybosspro.feature.login.data.model.POSP(
            Ss_Id = "150252",
            Fba_Id = "1001",
            First_Name = "Rahul",
            Last_Name = "Chaurasia",
            Mobile_No = "9773113793",
            Email_Id = "rahul@policyboss.com",
            _id = "1"
        )

        val response = com.policyboss.policybosspro.feature.login.data.model.LoginNewResponse_DSAS_Horizon(
            status = "SUCCESS",
            user_type = "POSP",
            POSP = posp
        )

        assertTrue(response.isSuccessful)
        assertEquals("150252", response.extractedSsId)
        assertEquals("1001", response.extractedFbaId)
        assertEquals("Rahul Chaurasia", response.extractedName)
        assertEquals("9773113793", response.extractedMobile)
        assertEquals("rahul@policyboss.com", response.extractedEmail)
    }

    @Test
    fun testLoginNewResponse_DSAS_Horizon_empExtraction() {
        val emp = com.policyboss.policybosspro.feature.login.data.model.EMP(
            Emp_Id = "2002",
            FBA_ID = "3003",
            Emp_Name = "Dinesh Das",
            Mobile_Number = "9876543210",
            Email_Id = "dinesh@policyboss.com"
        )

        val response = com.policyboss.policybosspro.feature.login.data.model.LoginNewResponse_DSAS_Horizon(
            status = "SUCCESS",
            user_type = "EMP",
            EMP = emp
        )

        assertTrue(response.isSuccessful)
        assertEquals("2002", response.extractedSsId)
        assertEquals("3003", response.extractedFbaId)
        assertEquals("Dinesh Das", response.extractedName)
        assertEquals("9876543210", response.extractedMobile)
        assertEquals("dinesh@policyboss.com", response.extractedEmail)
    }

    @Test
    fun testLoginNewResponse_lenientDeserialization_withDirtyValues() {
        val json = kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        // Dirty backend response: POSP is "NA", HR is "NA", SUB_USER is [], and Permanant_Pincode is an Int (400063)
        val rawJson = """
            {
                "status": "SUCCESS",
                "user_type": "EMP",
                "Ss_Id": 150252,
                "POSP": "NA",
                "HR": "NA",
                "SUB_USER": [],
                "POSP_USER": {
                    "Name_On_PAN": "RAHUL CHAURASIA",
                    "User_Id": "1001",
                    "Mobile_No": "9773113793",
                    "Permanant_Pincode": 400063
                },
                "EMP": {
                    "Emp_Id": 2002,
                    "FBA_ID": 3003,
                    "Emp_Name": "Rahul Emp",
                    "Mobile_Number": "9773113793",
                    "Email_Id": "rahul@policyboss.com"
                }
            }
        """.trimIndent()

        val parsed = json.decodeFromString<com.policyboss.policybosspro.feature.login.data.model.LoginNewResponse_DSAS_Horizon>(rawJson)

        assertTrue(parsed.isSuccessful)
        assertEquals("EMP", parsed.user_type)
        assertEquals("150252", parsed.Ss_Id)
        // Check that "NA" was parsed to null without throwing an exception!
        org.junit.Assert.assertNull(parsed.POSP)
        org.junit.Assert.assertNull(parsed.HR)
        org.junit.Assert.assertNull(parsed.SUB_USER)
        // Check that POSP_USER was parsed with Int pincode parsed to String
        org.junit.Assert.assertNotNull(parsed.POSP_USER)
        assertEquals("400063", parsed.extractedPincode)
        assertEquals("RAHUL CHAURASIA", parsed.extractedName)
        assertEquals("150252", parsed.extractedSsId)
        assertEquals("3003", parsed.extractedFbaId)
    }

    @Test
    fun testLoginNewResponse_singleObjectWrappedInArray() {
        val json = kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        // Backend PHP sometimes encodes a single record inside an array [ { ... } ]
        val rawJson = """
            {
                "status": "SUCCESS",
                "user_type": "POSP",
                "POSP": [
                    {
                        "First_Name": "Rahul",
                        "Last_Name": "Chaurasia",
                        "Fba_Id": "1001",
                        "Ss_Id": "150252",
                        "Is_Active": "1"
                    }
                ],
                "HR": [
                    {
                        "Designation": "Senior POSP Leader"
                    }
                ]
            }
        """.trimIndent()

        val parsed = json.decodeFromString<com.policyboss.policybosspro.feature.login.data.model.LoginNewResponse_DSAS_Horizon>(rawJson)

        assertTrue(parsed.isSuccessful)
        org.junit.Assert.assertNotNull(parsed.POSP)
        assertEquals("Rahul Chaurasia", parsed.extractedName)
        assertEquals("1001", parsed.extractedFbaId)
        assertEquals(true, parsed.POSP?.Is_Active)
        assertEquals("Senior POSP Leader", parsed.extractedDesignation)
    }

    @Test
    fun testLoginNewResponse_userTypeBusinessRules_FOS_and_MISP() {
        val json = kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        // FOS user type
        val fosJson = """
            {
                "status": "SUCCESS",
                "user_type": "FOS",
                "POSP": {
                    "First_Name": "Ajay",
                    "Last_Name": "Kumar",
                    "Fba_Id": "5005",
                    "Ss_Id": "999"
                }
            }
        """.trimIndent()
        val fosParsed = json.decodeFromString<com.policyboss.policybosspro.feature.login.data.model.LoginNewResponse_DSAS_Horizon>(fosJson)
        assertEquals("Ajay Kumar", fosParsed.extractedName)
        assertEquals("5005", fosParsed.extractedFbaId)
        assertEquals("999", fosParsed.extractedSsId)

        // MISP user type
        val mispJson = """
            {
                "status": "SUCCESS",
                "user_type": "MISP",
                "EMP": {
                    "Emp_Name": "MISP Agent",
                    "FBA_ID": "6006",
                    "Emp_Id": "888"
                }
            }
        """.trimIndent()
        val mispParsed = json.decodeFromString<com.policyboss.policybosspro.feature.login.data.model.LoginNewResponse_DSAS_Horizon>(mispJson)
        assertEquals("MISP Agent", mispParsed.extractedName)
        assertEquals("6006", mispParsed.extractedFbaId)
        assertEquals("888", mispParsed.extractedSsId)
    }

    // =========================================================================
    // 4. LOGIN INPUT HANDLING & EXPLICIT SEND OTP ACTION TESTS (7 SCENARIOS)
    // =========================================================================

    @Test
    fun testScenario1_typingMobileNumber_doesNotTriggerApiCall() {
        val fakeRepo = FakeLoginRepository()
        val viewModel = createLoginViewModel(fakeRepo)

        viewModel.onLoginIdChange("9876543210")

        assertEquals("9876543210", viewModel.uiState.value.loginId)
        assertFalse(viewModel.uiState.value.isError)
        assertNull(viewModel.uiState.value.errorMessage)
        assertEquals(0, fakeRepo.initiateLoginCallsCount)
    }

    @Test
    fun testScenario2_typingEmailId_doesNotTriggerApiCall() {
        val fakeRepo = FakeLoginRepository()
        val viewModel = createLoginViewModel(fakeRepo)

        viewModel.onLoginIdChange("rahul.chaurasia@policyboss.com")

        assertEquals("rahul.chaurasia@policyboss.com", viewModel.uiState.value.loginId)
        assertFalse(viewModel.uiState.value.isError)
        assertNull(viewModel.uiState.value.errorMessage)
        assertEquals(0, fakeRepo.initiateLoginCallsCount)
    }

    @Test
    fun testScenario3_typingPospOrErp_doesNotTriggerApiCall() {
        val fakeRepo = FakeLoginRepository()
        val viewModel = createLoginViewModel(fakeRepo)

        viewModel.onLoginIdChange("150252")
        assertEquals("150252", viewModel.uiState.value.loginId)
        assertEquals(0, fakeRepo.initiateLoginCallsCount)

        viewModel.onLoginIdChange("ERP-99100")
        assertEquals("ERP-99100", viewModel.uiState.value.loginId)
        assertFalse(viewModel.uiState.value.isError)
        assertNull(viewModel.uiState.value.errorMessage)
        assertEquals(0, fakeRepo.initiateLoginCallsCount)
    }

    @Test
    fun testScenario4_clickingSendOtp_triggersValidationAndFlowExactlyOnce() = runTest {
        val fakeRepo = FakeLoginRepository()
        val viewModel = createLoginViewModel(fakeRepo)

        viewModel.onLoginIdChange("9773113793")
        assertEquals(0, fakeRepo.initiateLoginCallsCount)

        // User explicitly clicks Send OTP
        viewModel.onSendOtpClick()
        testScheduler.advanceUntilIdle()

        assertEquals(1, fakeRepo.initiateLoginCallsCount)
        assertEquals("9773113793", fakeRepo.initiateLoginCalledWith)
        assertFalse(viewModel.uiState.value.isError)
    }

    @Test
    fun testScenario5_pressingKeyboardDone_triggersSameSendOtpFlow() = runTest {
        val fakeRepo = FakeLoginRepository()
        val viewModel = createLoginViewModel(fakeRepo)

        viewModel.onLoginIdChange("rahul.chaurasia@policyboss.com")
        assertEquals(0, fakeRepo.initiateLoginCallsCount)

        // Simulating the exact action invoked by keyboard onDone
        viewModel.onSendOtpClick()
        testScheduler.advanceUntilIdle()

        assertEquals(1, fakeRepo.initiateLoginCallsCount)
        assertEquals("rahul.chaurasia@policyboss.com", fakeRepo.initiateLoginCalledWith)
        assertFalse(viewModel.uiState.value.isError)
    }

    @Test
    fun testScenario6_invalidInput_showsErrorOnlyAfterSubmission_notWhileTyping() = runTest {
        val fakeRepo = FakeLoginRepository()
        val viewModel = createLoginViewModel(fakeRepo)

        // User enters invalid input (e.g. invalid format)
        viewModel.onLoginIdChange("invalid_email@")

        // Crucial requirement: No error or validation while typing!
        assertFalse(viewModel.uiState.value.isError)
        assertNull(viewModel.uiState.value.errorMessage)
        assertEquals(0, fakeRepo.initiateLoginCallsCount)

        // User explicitly submits
        viewModel.onSendOtpClick()
        testScheduler.advanceUntilIdle()

        // Validation error is displayed only now, and no API call is made
        assertTrue(viewModel.uiState.value.isError)
        assertEquals("Please enter a valid email address.", viewModel.uiState.value.errorMessage)
        assertEquals(0, fakeRepo.initiateLoginCallsCount)

        // When user resumes typing to correct it, error is cleared without triggering validation
        viewModel.onLoginIdChange("rahul@policyboss.com")
        assertFalse(viewModel.uiState.value.isError)
        assertNull(viewModel.uiState.value.errorMessage)
        assertEquals(0, fakeRepo.initiateLoginCallsCount)
    }

    @Test
    fun testScenario7_repeatedSendOtpClicks_whileLoading_doesNotTriggerDuplicateRequests() = runTest {
        val fakeRepo = object : LoginRepository {
            var callsCount = 0
            override suspend fun initiateLogin(loginId: String): Result<OtpUserMsg> {
                callsCount++
                delay(500) // Simulate in-flight network call
                return Result.success(
                    OtpUserMsg(name = "Rahul", mobileNo = "9773113793", otpStatus = "SUCCESS")
                )
            }
            override suspend fun otpVerifyHorizon(otp: String, loginInput: String) = flowOf(Response.success(OtpVerifyResponse()))
            override suspend fun getLoginDetailHorizon(ssId: String) = Result.success(true)
            override suspend fun logout() = Result.success(Unit)
        }

        val viewModel = createLoginViewModel(fakeRepo)
        viewModel.onLoginIdChange("9773113793")

        // First click starts API call
        viewModel.onSendOtpClick()
        testScheduler.runCurrent() // Runs up to delay

        assertTrue(viewModel.uiState.value.isLoading)
        assertEquals(1, fakeRepo.callsCount)

        // Rapid duplicate clicks while loading is in progress
        viewModel.onSendOtpClick()
        viewModel.onSendOtpClick()
        viewModel.onSendOtpClick()
        testScheduler.runCurrent()

        // Count must strictly remain 1!
        assertEquals(1, fakeRepo.callsCount)

        // Complete the in-flight request
        testScheduler.advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(1, fakeRepo.callsCount)
    }
}
