package com.policyboss.policybosspro.feature.register.data.api

import com.policyboss.policybosspro.core.network.NetworkConstants
import com.policyboss.policybosspro.feature.register.data.model.CheckPospDataRequest
import com.policyboss.policybosspro.feature.register.data.model.RegistrationApiResponse
import com.policyboss.policybosspro.feature.register.data.model.RegistrationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface RegisterApi {

    /**
     * API 1: Check POSP Data Availability
     * Method: POST
     * Relative URL: onboarding/check_posp_data_availability
     */
    @Headers("token: ${NetworkConstants.DEFAULT_TOKEN}")
    @POST("onboarding/check_posp_data_availability")
    suspend fun checkDataAvailability(
        @Body request: CheckPospDataRequest,
        @Header("token") token: String = NetworkConstants.DEFAULT_TOKEN
    ): Response<RegistrationApiResponse>

    /**
     * API 2: Submit POSP Registration
     * Method: POST
     * Relative URL: quote/Postfm/insert-fba-registration
     */
    @Headers("token: ${NetworkConstants.DEFAULT_TOKEN}")
    @POST("quote/Postfm/insert-fba-registration")
    suspend fun submitRegistration(
        @Body request: RegistrationRequest,
        @Header("token") token: String = NetworkConstants.DEFAULT_TOKEN
    ): Response<RegistrationApiResponse>
}

