package com.policyboss.policybosspro.core.repository.register

import com.policyboss.policybosspro.feature.register.data.model.CheckPospDataRequest
import com.policyboss.policybosspro.feature.register.data.model.RegistrationApiResponse
import com.policyboss.policybosspro.feature.register.data.model.RegistrationRequest

interface RegisterRepository {

    /**
     * API 1: Calls onboarding/check_posp_data_availability POST API.
     */
    suspend fun checkDataAvailability(request: CheckPospDataRequest): Result<RegistrationApiResponse>

    /**
     * API 2: Calls quote/Postfm/insert-fba-registration POST API.
     */
    suspend fun submitRegistration(request: RegistrationRequest): Result<RegistrationApiResponse>
}

