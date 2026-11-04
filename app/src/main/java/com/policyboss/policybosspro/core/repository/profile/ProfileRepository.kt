package com.policyboss.policybosspro.core.repository.profile

import com.policyboss.policybosspro.feature.profile.model.PersonalInformation
import com.policyboss.policybosspro.feature.profile.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getUserProfile(): Flow<Result<UserProfile>>
    suspend fun updatePersonalInfo(personalInfo: PersonalInformation): Result<UserProfile>
    suspend fun toggleUseSameAsPosp(useSame: Boolean): Result<UserProfile>
}

