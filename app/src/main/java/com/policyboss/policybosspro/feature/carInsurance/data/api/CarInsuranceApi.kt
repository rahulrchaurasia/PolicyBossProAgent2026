package com.policyboss.policybosspro.feature.carInsurance.data.api

import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CarInsuranceApi {

    @POST("car-insurance/quote")
    suspend fun getCarQuotes(
        @Body request: CarQuoteRequest
    ): Response<CarQuoteResponse>
}
