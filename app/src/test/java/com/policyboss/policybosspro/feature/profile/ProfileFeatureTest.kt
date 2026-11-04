package com.policyboss.policybosspro.feature.profile

import com.policyboss.policybosspro.core.repository.profile.ProfileRepository
import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.profile.model.PersonalInformation
import com.policyboss.policybosspro.feature.profile.model.UserProfile
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

class ProfileFeatureTest {

    private class FakeProfileRepository : ProfileRepository {
        private val _state = MutableStateFlow(AppDummyData.defaultUserProfile)

        override fun getUserProfile(): Flow<Result<UserProfile>> = _state.asStateFlow().map {
            Result.success(it)
        }

        override suspend fun updatePersonalInfo(personalInfo: PersonalInformation): Result<UserProfile> {
            val updated = _state.value.copy(
                fullName = personalInfo.fullName,
                initials = if (personalInfo.fullName.contains(" ")) {
                    val parts = personalInfo.fullName.split(" ")
                    "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
                } else "DD",
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
}
