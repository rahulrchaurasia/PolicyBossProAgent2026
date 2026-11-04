package com.policyboss.policybosspro.feature.login.model.verifyAccount

import com.policyboss.policybosspro.feature.login.component.verifyAccount.OTPFieldState


data class VerifyOtpUiState(

    val otp: String = "",

    val fieldState: OTPFieldState =
        OTPFieldState.DEFAULT,

    val errorMessage: String? = null,

    val resendMessage: String? = null,

    val resendSeconds: Int = 120,

    val isVerifyingOtp: Boolean = false,

    val isResendingOtp: Boolean = false,

    val isOtpVerified: Boolean = false
) {
    constructor(
        isResendingOtp: Boolean,
        otp: String,
        fieldState: OTPFieldState,
        resendMessage: String,
        resendSeconds: Int
    ) : this()
}