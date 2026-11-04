package com.policyboss.policybosspro.feature.login.data.api

import com.policyboss.policybosspro.core.network.NetworkConstants
import com.policyboss.policybosspro.feature.login.data.model.DevicetokenResponse
import com.policyboss.policybosspro.feature.login.data.model.LoginNewResponse_DSAS_Horizon
import com.policyboss.policybosspro.feature.login.data.model.OtpLoginRequest
import com.policyboss.policybosspro.feature.login.data.model.OtpLoginResponse
import com.policyboss.policybosspro.feature.login.data.model.OtpVerifyResponse
import com.policyboss.policybosspro.feature.login.data.model.TriggerOtpResponse
import com.policyboss.policybosspro.feature.login.data.model.UserConstantResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface LoginApi {

    companion object {
        const val DEFAULT_TOKEN = NetworkConstants.DEFAULT_TOKEN
    }

    /**
     * API 1: Login and Initiate OTP
     * Method: POST
     * Relative URL: postservicecall/otp_login (resolved against Retrofit baseUrl)
     * Header: token: 1234567890
     */
    @Headers("token: ${NetworkConstants.DEFAULT_TOKEN}")
    @POST("postservicecall/otp_login")
    suspend fun initiateLogin(
        @Body request: OtpLoginRequest,
        @Header("token") token: String = NetworkConstants.DEFAULT_TOKEN
    ): Response<OtpLoginResponse>

    /**
     * API 2: Verify OTP Entered by User
     * Method: GET
     * Relative URL: verifyOTP_New/{otp}/{loginInput} (resolved against Retrofit baseUrl)
     */
    @GET("verifyOTP_New/{otp}/{loginInput}")
    suspend fun otpVerifyHorizon(
        @Path("otp") otp: String,
        @Path("loginInput", encoded = false) loginInput: String
    ): Response<OtpVerifyResponse>

    /**
     * API 3: Get Horizon Login Details (DSAS)
     * Method: GET
     * Relative URL: posps/dsas/view/{userId}
     */
    @GET("posps/dsas/view/{userId}")
    suspend fun getLoginDsasHorizonDetails(
        @Path("userId") userId: String
    ): Response<LoginNewResponse_DSAS_Horizon>

    /**
     * API 4: Get User Constant configurations for POSP/User
     * Method: POST
     * Relative URL: Postfm/user-constant-pb
     */
    @Headers("token: ${NetworkConstants.DEFAULT_TOKEN}")
    @POST("Postfm/user-constant-pb")
    suspend fun getUserConstant(
        @Body body: Map<String, String>
    ): Response<UserConstantResponse>

    /**
     * API 5: Register Device FCM Notification Token on Horizon
     * Method: POST
     * Relative URL: Postfm/notification-auth-token
     */
    @POST("Postfm/notification-auth-token")
    suspend fun insertNotificationToken(
        @Body body: Map<String, String>
    ): Response<DevicetokenResponse>

    /**
     * Optional trigger API if needed for resend
     */
    @GET("verifyOTP_New/2339/{userData}")
    suspend fun triggerOtpMessage(
        @Path("userData") userData: String
    ): Response<TriggerOtpResponse>
}
