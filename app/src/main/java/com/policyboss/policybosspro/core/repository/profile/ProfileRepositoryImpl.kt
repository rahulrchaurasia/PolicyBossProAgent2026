package com.policyboss.policybosspro.core.repository.profile

import com.policyboss.policybosspro.core.datastore.AppDataManager
import com.policyboss.policybosspro.core.datastore.UserSession
import com.policyboss.policybosspro.core.utils.UserProfileParser
import com.policyboss.policybosspro.feature.profile.model.AccountDetails
import com.policyboss.policybosspro.feature.profile.model.DisplayInformation
import com.policyboss.policybosspro.feature.profile.model.PersonalInformation
import com.policyboss.policybosspro.feature.profile.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val appDataManager: AppDataManager
) : ProfileRepository {

    private val _useSameAsPosp = MutableStateFlow(false)
    private val _personalOverrides = MutableStateFlow<Pair<String, PersonalInformation>?>(null)

    override fun getUserProfile(): Flow<Result<UserProfile>> {
        return combine(
            appDataManager.userSession,
            _useSameAsPosp,
            _personalOverrides
        ) { session, useSameAsPosp, overridesPair ->
            Result.success(
                buildUserProfileSnapshot(
                    session = session,
                    useSameAsPosp = useSameAsPosp,
                    overridesPair = overridesPair
                )
            )
        }
    }

    override suspend fun updatePersonalInfo(personalInfo: PersonalInformation): Result<UserProfile> {
        val currentSession = appDataManager.getUserSession()
        if (currentSession.isLoggedIn) {
            _personalOverrides.value = currentSession.ssId to personalInfo
        }

        val combinedAddress = listOf(
            personalInfo.address,
            personalInfo.city,
            personalInfo.state,
            personalInfo.pincode
        ).map { it.trim() }.filter { it.isNotBlank() }.joinToString(", ")

        // Sync with AppDataManager so HomeTopBar, MenuScreen, and Profile stay updated
        appDataManager.saveUserSession(
            name = personalInfo.fullName.trim().takeIf { it.isNotBlank() },
            mobile = personalInfo.mobile.trim().takeIf { it.isNotBlank() },
            email = personalInfo.email.trim().takeIf { it.isNotBlank() },
            address = combinedAddress.takeIf { it.isNotBlank() }
        )

        val refreshedSession = appDataManager.getUserSession()
        return Result.success(
            buildUserProfileSnapshot(
                session = refreshedSession,
                useSameAsPosp = _useSameAsPosp.value,
                overridesPair = _personalOverrides.value
            )
        )
    }

    override suspend fun toggleUseSameAsPosp(useSame: Boolean): Result<UserProfile> {
        _useSameAsPosp.value = useSame
        return getUserProfile().first()
    }

    private suspend fun buildUserProfileSnapshot(
        session: UserSession,
        useSameAsPosp: Boolean,
        overridesPair: Pair<String, PersonalInformation>?
    ): UserProfile {
        val activeOverrides = if (
            session.isLoggedIn &&
            overridesPair != null &&
            overridesPair.first == session.ssId
        ) {
            overridesPair.second
        } else {
            null
        }

        val horizon = appDataManager.getLoginHorizonResponse()
        val pospUser = horizon?.POSP_USER
        val posp = horizon?.POSP
        val emp = horizon?.EMP

        val resolvedFullName = activeOverrides?.fullName?.trim()?.takeIf { it.isNotBlank() }
            ?: session.name.trim().takeIf { it.isNotBlank() }
            ?: "Guest User"

        val resolvedInitials = activeOverrides?.fullName?.trim()?.takeIf { it.isNotBlank() }?.let {
            UserProfileParser.extractInitials(it)
        } ?: session.initials.ifBlank {
            UserProfileParser.extractInitials(session.name)
        }

        val personalInfo = activeOverrides ?: PersonalInformation(
            fullName = resolvedFullName,
            mobile = session.mobile,
            dob = pospUser?.Birthdate?.trim().orEmpty(),
            email = session.email,
            pan = emp?.Pan?.trim().orEmpty(),
            address = session.address,
            city = pospUser?.Permanant_City?.trim().orEmpty(),
            state = pospUser?.Permanant_State?.trim().orEmpty(),
            pincode = pospUser?.Permanant_Pincode?.trim().orEmpty()
        )

        val accountDetails = AccountDetails(
            ssId = session.ssId.takeIf { it.isNotBlank() && it != "0" } ?: "NA",
            plan = horizon?.channel?.trim()?.takeIf { it.isNotBlank() } ?: "NA",
            rmName = posp?.Sm_Posp_Name?.trim()?.takeIf { it.isNotBlank() } ?: "NA",
            rmMobile = "",
            rmEmail = ""
        )

        val displayInfo = DisplayInformation(
            displayName = resolvedFullName,
            displayMobile = personalInfo.mobile,
            displayEmail = personalInfo.email,
            displayPictureRes = null
        )

        return UserProfile(
            fullName = resolvedFullName,
            initials = resolvedInitials,
            accountDetails = accountDetails,
            displayInfo = displayInfo,
            personalInfo = personalInfo,
            useSameAsPosp = useSameAsPosp
        )
    }
}

