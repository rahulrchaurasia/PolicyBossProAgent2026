package com.policyboss.policybosspro.feature.profile.model

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val profile: UserProfile = UserProfile(),
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false
)

