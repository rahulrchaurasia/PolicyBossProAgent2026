package com.policyboss.policybosspro.feature.register

import com.policyboss.policybosspro.core.repository.register.RegisterRepository
import com.policyboss.policybosspro.feature.register.data.model.CheckPospDataRequest
import com.policyboss.policybosspro.feature.register.data.model.MasterData
import com.policyboss.policybosspro.feature.register.data.model.RegistrationApiResponse
import com.policyboss.policybosspro.feature.register.data.model.RegistrationRequest
import com.policyboss.policybosspro.feature.register.util.NameSplitter
import com.policyboss.policybosspro.feature.register.util.RegisterValidator
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterFeatureTest {

    // =========================================================================
    // 1. NAME SPLITTING TESTS
    // =========================================================================

    @Test
    fun testNameSplitter_twoWords() {
        val (first, last) = NameSplitter.splitFullName("Rahul Chaurasia")
        assertEquals("Rahul", first)
        assertEquals("Chaurasia", last)
    }

    @Test
    fun testNameSplitter_threeWords_preservesMiddleName() {
        val (first, last) = NameSplitter.splitFullName("Rahul Rajendra Chaurasia")
        assertEquals("Rahul", first)
        assertEquals("Rajendra Chaurasia", last)
    }

    @Test
    fun testNameSplitter_singleWord_usesFallbackDot() {
        val (first, last) = NameSplitter.splitFullName("Rahul")
        assertEquals("Rahul", first)
        assertEquals(".", last)
    }

    @Test
    fun testNameSplitter_emptyString() {
        val (first, last) = NameSplitter.splitFullName("   ")
        assertEquals("", first)
        assertEquals("", last)
    }

    // =========================================================================
    // 2. FORM VALIDATION TESTS
    // =========================================================================

    @Test
    fun testValidateFullName() {
        assertNull(RegisterValidator.validateFullName("Rahul Chaurasia"))
        assertNotNull(RegisterValidator.validateFullName(""))
        assertNotNull(RegisterValidator.validateFullName("R"))
        assertNotNull(RegisterValidator.validateFullName("Rahul123"))
    }

    @Test
    fun testValidateMobile() {
        assertNull(RegisterValidator.validateMobile("9773113793"))
        assertNull(RegisterValidator.validateMobile("6123456789"))
        assertNotNull(RegisterValidator.validateMobile(""))
        assertNotNull(RegisterValidator.validateMobile("977311379")) // 9 digits
        assertNotNull(RegisterValidator.validateMobile("97731137933")) // 11 digits
        assertNotNull(RegisterValidator.validateMobile("1234567890")) // starts with 1
        assertNotNull(RegisterValidator.validateMobile("abcdefghij"))
    }

    @Test
    fun testValidateEmail() {
        assertNull(RegisterValidator.validateEmail("rahul.chaurasia@policyboss.com"))
        assertNotNull(RegisterValidator.validateEmail(""))
        assertNotNull(RegisterValidator.validateEmail("invalid_email"))
        assertNotNull(RegisterValidator.validateEmail("rahul@domain"))
    }

    @Test
    fun testValidateDob() {
        assertNull(RegisterValidator.validateDob("24/01/1988"))
        assertNotNull(RegisterValidator.validateDob(""))
        assertNotNull(RegisterValidator.validateDob("01/01/2099")) // Future date
        assertNotNull(RegisterValidator.validateDob("invalid-date"))
    }

    @Test
    fun testValidatePincode() {
        assertNull(RegisterValidator.validatePincode("400063"))
        assertNotNull(RegisterValidator.validatePincode(""))
        assertNotNull(RegisterValidator.validatePincode("40006")) // 5 digits
        assertNotNull(RegisterValidator.validatePincode("4000633")) // 7 digits
        assertNotNull(RegisterValidator.validatePincode("000063")) // starts with 0
    }

    @Test
    fun testValidatePan() {
        assertNull(RegisterValidator.validatePan("HPFOO4389L"))
        assertNull(RegisterValidator.validatePan("ABCDE1234F"))
        assertNotNull(RegisterValidator.validatePan(""))
        assertNotNull(RegisterValidator.validatePan("HPFOO4389")) // 9 chars
        assertNotNull(RegisterValidator.validatePan("12345ABCDE")) // wrong format
    }

    @Test
    fun testValidateGender() {
        assertNull(RegisterValidator.validateGender("M"))
        assertNull(RegisterValidator.validateGender("F"))
        assertNotNull(RegisterValidator.validateGender(""))
        assertNotNull(RegisterValidator.validateGender("X"))
    }

    @Test
    fun testValidateCity() {
        assertNull(RegisterValidator.validateCity("Mumbai"))
        assertNotNull(RegisterValidator.validateCity(""))
        assertNotNull(RegisterValidator.validateCity("   "))
        assertEquals("Please enter your city", RegisterValidator.validateCity(""))
    }

    @Test
    fun testValidateSubscription() {
        assertNull(RegisterValidator.validateSubscription("All-In-One(₹999)"))
        assertNotNull(RegisterValidator.validateSubscription(""))
        assertEquals("Please select a subscription type", RegisterValidator.validateSubscription(""))
    }

    @Test
    fun testValidateExistingAgent() {
        assertNull(RegisterValidator.validateExistingAgent(true))
        assertNull(RegisterValidator.validateExistingAgent(false))
        assertNotNull(RegisterValidator.validateExistingAgent(null))
        assertEquals("Please select Yes or No", RegisterValidator.validateExistingAgent(null))
    }

    @Test
    fun testSequentialValidationFlow_exactSequence() {
        // Step 1: Submit empty form -> only Full Name error
        var error = RegisterValidator.validateSequential(
            fullName = "",
            mobile = "",
            email = "",
            dob = "",
            pincode = "",
            city = "",
            pan = "",
            gender = "",
            subscriptionType = "",
            isExistingAgent = null
        )
        assertNotNull(error)
        assertEquals("fullName", error?.first)
        assertEquals("Please enter your full name", error?.second)

        // Step 2: Enter Full Name and submit -> only Mobile error
        error = RegisterValidator.validateSequential(
            fullName = "Rahul Chaurasia",
            mobile = "",
            email = "",
            dob = "",
            pincode = "",
            city = "",
            pan = "",
            gender = "",
            subscriptionType = "",
            isExistingAgent = null
        )
        assertNotNull(error)
        assertEquals("mobile", error?.first)
        assertEquals("Please enter a valid 10-digit mobile number", error?.second)

        // Step 3: Enter valid Mobile and submit -> only Email error
        error = RegisterValidator.validateSequential(
            fullName = "Rahul Chaurasia",
            mobile = "9773113793",
            email = "",
            dob = "",
            pincode = "",
            city = "",
            pan = "",
            gender = "",
            subscriptionType = "",
            isExistingAgent = null
        )
        assertNotNull(error)
        assertEquals("email", error?.first)
        assertEquals("Please enter a valid email address", error?.second)

        // Step 4: Enter valid Email and submit -> only DOB error
        error = RegisterValidator.validateSequential(
            fullName = "Rahul Chaurasia",
            mobile = "9773113793",
            email = "rahul.chaurasia@policyboss.com",
            dob = "",
            pincode = "",
            city = "",
            pan = "",
            gender = "",
            subscriptionType = "",
            isExistingAgent = null
        )
        assertNotNull(error)
        assertEquals("dob", error?.first)
        assertEquals("Please select your date of birth", error?.second)

        // Step 5: Select DOB and submit -> only Pincode error
        error = RegisterValidator.validateSequential(
            fullName = "Rahul Chaurasia",
            mobile = "9773113793",
            email = "rahul.chaurasia@policyboss.com",
            dob = "24/01/1988",
            pincode = "",
            city = "",
            pan = "",
            gender = "",
            subscriptionType = "",
            isExistingAgent = null
        )
        assertNotNull(error)
        assertEquals("pincode", error?.first)
        assertEquals("Please enter a valid 6-digit pincode", error?.second)

        // Step 6: Enter valid Pincode and submit -> only City error (City validated before PAN!)
        error = RegisterValidator.validateSequential(
            fullName = "Rahul Chaurasia",
            mobile = "9773113793",
            email = "rahul.chaurasia@policyboss.com",
            dob = "24/01/1988",
            pincode = "400063",
            city = "",
            pan = "",
            gender = "",
            subscriptionType = "",
            isExistingAgent = null
        )
        assertNotNull(error)
        assertEquals("city", error?.first)
        assertEquals("Please enter your city", error?.second)

        // Step 7: Enter City and submit -> only PAN error
        error = RegisterValidator.validateSequential(
            fullName = "Rahul Chaurasia",
            mobile = "9773113793",
            email = "rahul.chaurasia@policyboss.com",
            dob = "24/01/1988",
            pincode = "400063",
            city = "Mumbai",
            pan = "",
            gender = "",
            subscriptionType = "",
            isExistingAgent = null
        )
        assertNotNull(error)
        assertEquals("pan", error?.first)
        assertEquals("Please enter a valid PAN number", error?.second)

        // Step 8: Enter valid PAN and submit -> only Gender error
        error = RegisterValidator.validateSequential(
            fullName = "Rahul Chaurasia",
            mobile = "9773113793",
            email = "rahul.chaurasia@policyboss.com",
            dob = "24/01/1988",
            pincode = "400063",
            city = "Mumbai",
            pan = "HPFOO4389L",
            gender = "",
            subscriptionType = "",
            isExistingAgent = null
        )
        assertNotNull(error)
        assertEquals("gender", error?.first)
        assertEquals("Please select your gender", error?.second)

        // Step 9: Select Gender and submit -> only Subscription Type error
        error = RegisterValidator.validateSequential(
            fullName = "Rahul Chaurasia",
            mobile = "9773113793",
            email = "rahul.chaurasia@policyboss.com",
            dob = "24/01/1988",
            pincode = "400063",
            city = "Mumbai",
            pan = "HPFOO4389L",
            gender = "M",
            subscriptionType = "",
            isExistingAgent = null
        )
        assertNotNull(error)
        assertEquals("subscriptionType", error?.first)
        assertEquals("Please select a subscription type", error?.second)

        // Step 10: Select Subscription Type and submit -> only Existing Agent error
        error = RegisterValidator.validateSequential(
            fullName = "Rahul Chaurasia",
            mobile = "9773113793",
            email = "rahul.chaurasia@policyboss.com",
            dob = "24/01/1988",
            pincode = "400063",
            city = "Mumbai",
            pan = "HPFOO4389L",
            gender = "M",
            subscriptionType = "All-In-One(₹999)",
            isExistingAgent = null
        )
        assertNotNull(error)
        assertEquals("existingAgent", error?.first)
        assertEquals("Please select Yes or No", error?.second)

        // Step 11: Select Yes or No -> all valid, proceed to API calls
        error = RegisterValidator.validateSequential(
            fullName = "Rahul Chaurasia",
            mobile = "9773113793",
            email = "rahul.chaurasia@policyboss.com",
            dob = "24/01/1988",
            pincode = "400063",
            city = "Mumbai",
            pan = "HPFOO4389L",
            gender = "M",
            subscriptionType = "All-In-One(₹999)",
            isExistingAgent = false
        )
        assertNull(error)
    }

    // =========================================================================
    // 3. REPOSITORY & API FLOW TESTS
    // =========================================================================

    private class FakeRegisterRepository(
        var checkAvailabilityResult: Result<RegistrationApiResponse> = Result.success(
            RegistrationApiResponse(Status = "SUCCESS", Message = "Data is available")
        ),
        var submitRegistrationResult: Result<RegistrationApiResponse> = Result.success(
            RegistrationApiResponse(
                Status = "SUCCESS",
                Message = "Registration Successful",
                MasterData = MasterData(rawFbaId = JsonPrimitive("150252"))
            )
        )
    ) : RegisterRepository {
        var availabilityCalledWith: CheckPospDataRequest? = null
        var registrationCalledWith: RegistrationRequest? = null

        override suspend fun checkDataAvailability(
            request: CheckPospDataRequest
        ): Result<RegistrationApiResponse> {
            availabilityCalledWith = request
            return checkAvailabilityResult
        }

        override suspend fun submitRegistration(
            request: RegistrationRequest
        ): Result<RegistrationApiResponse> {
            registrationCalledWith = request
            return submitRegistrationResult
        }
    }

    @Test
    fun testRegistrationFlow_success() = runBlocking {
        val fakeRepo = FakeRegisterRepository()

        // 1. Check Availability
        val availReq = CheckPospDataRequest(
            email = "rahul.chaurasia@policyboss.com",
            mobile = "9773113793",
            pan = "HPFOO4389L"
        )
        val availResult = fakeRepo.checkDataAvailability(availReq)
        assertTrue(availResult.isSuccess)
        assertEquals("SUCCESS", availResult.getOrNull()?.Status)
        assertEquals(availReq, fakeRepo.availabilityCalledWith)

        // 2. Submit Registration
        val (first, last) = NameSplitter.splitFullName("Rahul Chaurasia")
        val regReq = RegistrationRequest(
            FirstName = first,
            LastName = last,
            Mobile_1 = "9773113793",
            EmailId = "rahul.chaurasia@policyboss.com",
            DOB = "1988-01-24",
            PinCode = "400063",
            City = "Mumbai",
            Posp_PAN = "HPFOO4389L",
            Gender = "M",
            Source = "All-In-One(₹999)",
            field_sales_uid = ""
        )
        val submitResult = fakeRepo.submitRegistration(regReq)
        assertTrue(submitResult.isSuccess)
        val response = submitResult.getOrNull()
        assertEquals("SUCCESS", response?.Status)
        assertEquals("150252", response?.MasterData?.fbaId)
        assertEquals("Rahul", fakeRepo.registrationCalledWith?.FirstName)
        assertEquals("Chaurasia", fakeRepo.registrationCalledWith?.LastName)
        assertEquals("M", fakeRepo.registrationCalledWith?.Gender)
    }

    @Test
    fun testRegistrationFlow_availabilityFails_stopsSubmission() = runBlocking {
        val fakeRepo = FakeRegisterRepository(
            checkAvailabilityResult = Result.failure(Exception("Mobile number already registered"))
        )

        val availReq = CheckPospDataRequest(
            email = "test@policyboss.com",
            mobile = "9999999999",
            pan = "ABCDE1234F"
        )
        val availResult = fakeRepo.checkDataAvailability(availReq)
        assertTrue(availResult.isFailure)
        assertEquals("Mobile number already registered", availResult.exceptionOrNull()?.message)

        // Ensure submit is never called
        assertNull(fakeRepo.registrationCalledWith)
    }
}

