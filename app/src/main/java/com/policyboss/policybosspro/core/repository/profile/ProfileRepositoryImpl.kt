package com.policyboss.policybosspro.core.repository.profile

import com.policyboss.policybosspro.core.datastore.AppDataManager
import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.profile.model.PersonalInformation
import com.policyboss.policybosspro.feature.profile.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val appDataManager: AppDataManager
) : ProfileRepository {

    private val _userProfileState = MutableStateFlow(AppDummyData.defaultUserProfile)

    override fun getUserProfile(): Flow<Result<UserProfile>> {
        return _userProfileState.asStateFlow().map { profile ->
            Result.success(profile)
        }
    }

    override suspend fun updatePersonalInfo(personalInfo: PersonalInformation): Result<UserProfile> {
        val updatedInitials = extractInitials(personalInfo.fullName)
        val updated = _userProfileState.value.copy(
            fullName = personalInfo.fullName,
            initials = updatedInitials,
            personalInfo = personalInfo
        )
        _userProfileState.value = updated

        // Sync with AppDataManager so HomeTopBar and other screens stay updated
        appDataManager.saveUserSession(
            name = personalInfo.fullName.takeIf { it.isNotBlank() },
            mobile = personalInfo.mobile.takeIf { it.isNotBlank() },
            email = personalInfo.email.takeIf { it.isNotBlank() }
        )

        return Result.success(updated)
    }

    override suspend fun toggleUseSameAsPosp(useSame: Boolean): Result<UserProfile> {
        val updated = _userProfileState.value.copy(useSameAsPosp = useSame)
        _userProfileState.value = updated
        return Result.success(updated)
    }

    private fun extractInitials(fullName: String): String {
        val trimmed = fullName.trim()
        if (trimmed.isEmpty()) return "DD"
        val parts = trimmed.split(" ").filter { it.isNotBlank() }
        return when {
            parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
            parts.size == 1 && parts[0].length >= 2 -> parts[0].take(2).uppercase()
            parts.size == 1 && parts[0].isNotEmpty() -> "${parts[0].first().uppercaseChar()}"
            else -> "DD"
        }
    }
}

