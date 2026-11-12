package com.policyboss.policybosspro.feature.profile

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.policyboss.policybosspro.core.datastore.AppDataManager
import com.policyboss.policybosspro.core.repository.profile.ProfileRepository
import com.policyboss.policybosspro.core.repository.profile.ProfileRepositoryImpl
import com.policyboss.policybosspro.core.utils.UserProfileParser
import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.login.data.model.LoginNewResponse_DSAS_Horizon
import com.policyboss.policybosspro.feature.login.data.model.POSP
import com.policyboss.policybosspro.feature.login.data.model.POSP_USER
import com.policyboss.policybosspro.feature.profile.model.PersonalInformation
import com.policyboss.policybosspro.feature.profile.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class ProfileFeatureTest {

    private class FakeProfileRepository : ProfileRepository {
        private val _state = MutableStateFlow(AppDummyData.defaultUserProfile)

        override fun getUserProfile(): Flow<Result<UserProfile>> = _state.asStateFlow().map {
            Result.success(it)
        }

        override suspend fun updatePersonalInfo(personalInfo: PersonalInformation): Result<UserProfile> {
            val updated = _state.value.copy(
                fullName = personalInfo.fullName,
                initials = UserProfileParser.extractInitials(personalInfo.fullName),
                personalInfo = personalInfo
            )
            _state.value = updated
            return Result.success(updated)
        }

        override suspend fun toggleUseSameAsPosp(useSame: Boolean): Result<UserProfile> {
            val updated = _state.value.copy(useSameAsPosp = useSame)
            _state.value = updated
            return Result.success(updated)
        }
    }

    private lateinit var fakeRepository: FakeProfileRepository

    @Before
    fun setUp() {
        fakeRepository = FakeProfileRepository()
    }

    @Test
    fun testInitialProfileLoaded() = runBlocking {
        val result = fakeRepository.getUserProfile().first()
        assertTrue(result.isSuccess)
        val profile = result.getOrNull()
        assertEquals("Dinesh Das", profile?.fullName)
        assertEquals("DD", profile?.initials)
        assertEquals("1449", profile?.accountDetails?.ssId)
        assertFalse(profile?.useSameAsPosp ?: true)
    }

    @Test
    fun testToggleUseSameAsPosp() = runBlocking {
        val initialProfile = fakeRepository.getUserProfile().first().getOrNull()
        assertFalse(initialProfile?.useSameAsPosp ?: true)

        fakeRepository.toggleUseSameAsPosp(true)
        val checkedProfile = fakeRepository.getUserProfile().first().getOrNull()
        assertTrue(checkedProfile?.useSameAsPosp ?: false)

        fakeRepository.toggleUseSameAsPosp(false)
        val uncheckedProfile = fakeRepository.getUserProfile().first().getOrNull()
        assertFalse(uncheckedProfile?.useSameAsPosp ?: true)
    }

    @Test
    fun testSavePersonalInfoUpdatesProfile() = runBlocking {
        val updatedInfo = PersonalInformation(
            fullName = "Rahul Sharma",
            mobile = "9876543210",
            dob = "15 - 08 - 1990",
            email = "rahul.sharma@policyboss.com",
            pan = "ABCDE1234F",
            address = "New Office Tower, Andheri East",
            city = "Mumbai",
            state = "Maharashtra",
            pincode = "400069"
        )

        val saveResult = fakeRepository.updatePersonalInfo(updatedInfo)
        assertTrue(saveResult.isSuccess)

        val updatedProfile = fakeRepository.getUserProfile().first().getOrNull()
        assertEquals("Rahul Sharma", updatedProfile?.fullName)
        assertEquals("RS", updatedProfile?.initials)
        assertEquals("9876543210", updatedProfile?.personalInfo?.mobile)
        assertEquals("ABCDE1234F", updatedProfile?.personalInfo?.pan)
        assertEquals("rahul.sharma@policyboss.com", updatedProfile?.personalInfo?.email)
    }

    @Test
    fun testProfileRepositoryImpl_derivesProfileFromAppDataManagerAndHorizonSession() = runBlocking {
        val testFile = File.createTempFile("test_datastore_profile", ".preferences_pb")
        testFile.deleteOnExit()
        val testDataStore = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.Unconfined + Job()),
            produceFile = { testFile }
        )
        val appDataManager = AppDataManager(testDataStore)
        val realRepo = ProfileRepositoryImpl(appDataManager)

        // Initially unauthenticated -> neutral fallback, never hardcoded Dinesh Das
        val guestProfile = realRepo.getUserProfile().first().getOrNull()
        assertEquals("Guest User", guestProfile?.fullName)
        assertEquals("PB", guestProfile?.initials)

        // After Horizon login with 3-word name -> RC + full personal/account details
        appDataManager.saveLoginHorizonResponse(
            LoginNewResponse_DSAS_Horizon(
                status = "SUCCESS",
                user_type = "POSP",
                Ss_Id = "150252",
                channel = "DIRECT",
                POSP_USER = POSP_USER(
                    Name_On_PAN = "Rahul Rajendra Chaurasia",
                    Mobile_No = "9773113793",
                    Email_Id = "rahul@policyboss.com",
                    Permanant_City = "Mumbai",
                    Permanant_State = "Maharashtra",
                    Permanant_Pincode = "400063"
                ),
                POSP = POSP(
                    Fba_Id = "1001",
                    Ss_Id = "150252",
                    Sm_Posp_Name = "RM Test"
                )
            )
        )

        val loggedInProfile = realRepo.getUserProfile().first().getOrNull()
        assertEquals("Rahul Rajendra Chaurasia", loggedInProfile?.fullName)
        assertEquals("RC", loggedInProfile?.initials)
        assertEquals("150252", loggedInProfile?.accountDetails?.ssId)
        assertEquals("DIRECT", loggedInProfile?.accountDetails?.plan)
        assertEquals("RM Test", loggedInProfile?.accountDetails?.rmName)
        assertEquals("Mumbai", loggedInProfile?.personalInfo?.city)
        assertEquals("400063", loggedInProfile?.personalInfo?.pincode)

        // Updating personal info propagates initials back into AppDataManager
        val edited = loggedInProfile!!.personalInfo.copy(fullName = "Suresh")
        val editResult = realRepo.updatePersonalInfo(edited).getOrNull()
        assertEquals("SU", editResult?.initials)
        assertEquals("SU", appDataManager.getUserInitials())
    }
}
