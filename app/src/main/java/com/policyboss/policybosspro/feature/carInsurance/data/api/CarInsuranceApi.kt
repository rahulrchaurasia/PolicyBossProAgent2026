package com.policyboss.policybosspro.feature.carInsurance.data.api

import com.policyboss.policybosspro.core.network.NetworkConstants
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteResponse
import com.policyboss.policybosspro.feature.carInsurance.data.model.FuelVariantResponseDto
import com.policyboss.policybosspro.feature.carInsurance.data.model.PreferredMakeModelDto
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumInitiateRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumInitiateResponseDto
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumListResponseDto
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumSearchRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumSummaryResponseDto
import com.policyboss.policybosspro.feature.carInsurance.data.model.VehicleInfoRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.VehicleInfoResponseDto
import kotlinx.serialization.json.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CarInsuranceApi {

    @POST("car-insurance/quote")
    suspend fun getCarQuotes(
        @Body request: CarQuoteRequest
    ): Response<CarQuoteResponse>

    /**
     * Vahan lookup by registration number.
     * POST https://api.policyboss.com:5443/quote/vehicle_info_user
     */
    @POST(VEHICLE_INFO_URL)
    suspend fun getVehicleInfo(
        @Body request: VehicleInfoRequest
    ): Response<VehicleInfoResponseDto>

    /**
     * Live Make/Model catalogue.
     * GET https://api.policyboss.com:5443/vehicles/model_list?product_id=1
     *
     * Returned as raw JSON so both a bare array and an object-wrapped array are supported;
     * the repository decodes the records.
     */
    @GET(MODEL_LIST_URL)
    suspend fun getModelList(
        @Query("product_id") productId: Int = VehicleInfoRequest.CAR_PRODUCT_ID
    ): Response<JsonElement>

    /**
     * Agent's preferred Make/Models, shown at the top of the bottom sheet.
     * GET https://api.policyboss.com:5443/vehicles/get_preferred/make_model?ss_id=&product_id=1
     */
    @GET(PREFERRED_MAKE_MODEL_URL)
    suspend fun getPreferredMakeModels(
        @Query("ss_id") ssId: Int,
        @Query("product_id") productId: Int = VehicleInfoRequest.CAR_PRODUCT_ID
    ): Response<List<PreferredMakeModelDto>>

    /**
     * Fuel types and variants of one model.
     * GET https://api.policyboss.com:5443/vehicles/beta_GetFuelVariant/{modelId}/{productId}/{makeName}
     */
    @GET(FUEL_VARIANT_URL)
    suspend fun getFuelVariants(
        @Path("modelId") modelId: String,
        @Path("productId") productId: Int = VehicleInfoRequest.CAR_PRODUCT_ID,
        @Path("makeName") makeName: String
    ): Response<FuelVariantResponseDto>

    /**
     * Previous-insurer master list (car = product 1).
     * GET https://api.policyboss.com:5443/insurers/getPrevInsurer/1
     * Returned raw: the payload is an index-keyed object, parsed by the repository.
     */
    @GET(PREV_INSURER_URL)
    suspend fun getPreviousInsurers(
        @Path("productId") productId: Int = VehicleInfoRequest.CAR_PRODUCT_ID
    ): Response<JsonElement>

    /**
     * Starts premium calculation for a looked-up vehicle.
     * POST https://api.policyboss.com:5443/quote/premium_initiate
     */
    @POST(PREMIUM_INITIATE_URL)
    suspend fun premiumInitiate(
        @Body request: PremiumInitiateRequest
    ): Response<PremiumInitiateResponseDto>

    /**
     * Progress of the insurer calls started by premium_initiate; polled until complete.
     * POST https://api.policyboss.com:5443/quote/premium_summary
     */
    @POST(PREMIUM_SUMMARY_URL)
    suspend fun premiumSummary(
        @Body request: PremiumSearchRequest
    ): Response<PremiumSummaryResponseDto>

    /**
     * Insurer quotes (plans and premiums) for a search.
     * POST https://api.policyboss.com:5443/quote/premium_list_db
     */
    @POST(PREMIUM_LIST_URL)
    suspend fun premiumList(
        @Body request: PremiumSearchRequest
    ): Response<PremiumListResponseDto>

    companion object {
        const val PREMIUM_SUMMARY_URL = NetworkConstants.QUOTE_API_BASE_URL + "quote/premium_summary"
        const val PREMIUM_LIST_URL = NetworkConstants.QUOTE_API_BASE_URL + "quote/premium_list_db"
        const val PREMIUM_INITIATE_URL = NetworkConstants.QUOTE_API_BASE_URL + "quote/premium_initiate"
        const val PREV_INSURER_URL = NetworkConstants.QUOTE_API_BASE_URL + "insurers/getPrevInsurer/{productId}"
        const val PREFERRED_MAKE_MODEL_URL = NetworkConstants.QUOTE_API_BASE_URL + "vehicles/get_preferred/make_model"
        const val FUEL_VARIANT_URL = NetworkConstants.QUOTE_API_BASE_URL + "vehicles/beta_GetFuelVariant/{modelId}/{productId}/{makeName}"
        const val VEHICLE_INFO_URL = NetworkConstants.QUOTE_API_BASE_URL + "quote/vehicle_info_user"
        const val MODEL_LIST_URL = NetworkConstants.QUOTE_API_BASE_URL + "vehicles/model_list"
    }
}
