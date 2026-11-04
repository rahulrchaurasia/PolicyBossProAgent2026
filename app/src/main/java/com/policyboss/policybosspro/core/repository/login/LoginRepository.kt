package com.policyboss.policybosspro.core.repository.login

import com.policyboss.policybosspro.feature.login.data.model.OtpUserMsg
import com.policyboss.policybosspro.feature.login.data.model.OtpVerifyResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface LoginRepository {

    /**
     * API 1: Calls Horizon otp_login POST API.
     */
    suspend fun initiateLogin(loginId: String): Result<OtpUserMsg>

    /**
     * API 2: Calls Horizon verifyOTP_New/{otp}/{loginInput} GET API.
     */
    suspend fun otpVerifyHorizon(
        otp: String,
        loginInput: String
    ): Flow<Response<OtpVerifyResponse>>

    /**
     * Completes login details flow using SS ID.
     */
    suspend fun getLoginDetailHorizon(ssId: String): Result<Boolean>

    /**
     * Safely clears authenticated user session and DataStore records.
     */
    suspend fun logout(): Result<Unit>
}
