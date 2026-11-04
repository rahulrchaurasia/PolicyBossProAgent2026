package com.policyboss.policybosspro.feature.profile.model

sealed interface ProfileAction {
    data class ToggleUseSameAsPosp(val isSame: Boolean) : ProfileAction
    data class SavePersonalInfo(val personalInfo: PersonalInformation) : ProfileAction
    data object Refresh : ProfileAction
}

