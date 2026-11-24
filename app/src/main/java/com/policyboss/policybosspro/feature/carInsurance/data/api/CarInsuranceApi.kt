package com.policyboss.policybosspro.feature.carInsurance.data.api

import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteResponse
import com.policyboss.policybosspro.feature.carInsurance.data.model.VehicleInfoRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.VehicleInfoResponseDto
import kotlinx.serialization.json.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CarInsuranceApi {

    @POST("car-insurance/quote")
    suspend fun getCarQuotes(
        @Body request: CarQuoteRequest
    ): Response<CarQuoteResponse>

    /**
     * Vahan lookup by registration number.
     * Resolves to https://horizon.policyboss.com:5443/quote/vehicle_info_user
     */
    @POST("quote/vehicle_info_user")
    suspend fun getVehicleInfo(
        @Body request: VehicleInfoRequest
    ): Response<VehicleInfoResponseDto>

    /**
     * Live Make/Model catalogue.
     * Resolves to https://horizon.policyboss.com:5443/vehicles/model_list?product_id=1
     *
     * Returned as raw JSON so both a bare array and an object-wrapped array are supported;
     * the repository decodes the records.
     */
    @GET("vehicles/model_list")
    suspend fun getModelList(
        @Query("product_id") productId: Int = VehicleInfoRequest.CAR_PRODUCT_ID
    ): Response<JsonElement>
}
