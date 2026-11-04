package com.policyboss.policybosspro.feature.register.model

sealed interface RegisterEvent {
    data class RegistrationSuccess(val fbaId: String, val message: String) : RegisterEvent
    data class ShowToast(val message: String) : RegisterEvent
    data object NavigateToLogin : RegisterEvent
    data class FocusField(val fieldName: String) : RegisterEvent
}

