package com.policyboss.policybosspro.feature.register.model

data class RegisterUiState(
    val fullName: String = "",
    val mobile: String = "",
    val email: String = "",
    val dob: String = "",
    val pincode: String = "",
    val city: String = "",
    val pan: String = "",
    val gender: String = "",
    val subscriptionType: String = "", // "All-In-One(₹999)"
    val isExistingAgent: Boolean? = null, // false = No, true = Yes
    val isDatePickerVisible: Boolean = false,
    val isLoading: Boolean = false,
    val loadingMessage: String? = null,
    val generalError: String? = null,
    val fieldErrors: Map<String, String> = emptyMap(),
    val registeredFbaId: String? = null,
    val successMessage: String? = null,
    val isSuccess: Boolean = false,
    val isSuccessDialogVisible: Boolean = false,
    val validationErrorTrigger: Long = 0L
) {
    val isFormValid: Boolean
        get() = fieldErrors.isEmpty() &&
                fullName.isNotBlank() &&
                mobile.isNotBlank() &&
                email.isNotBlank() &&
                dob.isNotBlank() &&
                pincode.isNotBlank() &&
                city.isNotBlank() &&
                pan.isNotBlank() &&
                gender.isNotBlank() &&
                subscriptionType.isNotBlank() &&
                isExistingAgent != null
}

