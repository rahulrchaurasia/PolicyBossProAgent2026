package com.policyboss.policybosspro.feature.login.model.login

import com.policyboss.policybosspro.feature.login.model.verifyAccount.VerifyOtpSource

sealed interface LoginEvent {

    data class NavigateToVerifyOtp(
        val fullName: String = "",
        val mobileNumber: String = "",
        val loginInput: String = "",
        val ssId: String = "",
        val emailId: String = "",
        val source: VerifyOtpSource = VerifyOtpSource.LOGIN
    ) : LoginEvent

    data class ShowSnackbar(
        val message: String
    ) : LoginEvent
}
