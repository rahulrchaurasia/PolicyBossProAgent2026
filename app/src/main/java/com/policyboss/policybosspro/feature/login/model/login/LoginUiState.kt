package com.policyboss.policybosspro.feature.login.model.login

data class LoginUiState(
    val loginId: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null
) {
    // Backward compatibility aliases for existing references
    val mobileNumber: String get() = loginId
    val isMobileError: Boolean get() = isError
    val mobileErrorMessage: String? get() = errorMessage
}
