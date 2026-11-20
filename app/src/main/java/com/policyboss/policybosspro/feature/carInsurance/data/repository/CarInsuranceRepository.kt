package com.policyboss.policybosspro.feature.carInsurance.data.repository

import com.policyboss.policybosspro.core.AppConfig
import com.policyboss.policybosspro.core.Resource
import com.policyboss.policybosspro.core.network.NetworkError
import com.policyboss.policybosspro.feature.carInsurance.data.api.CarInsuranceApi
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
open class CarInsuranceRepository @Inject constructor(
    private val carInsuranceApi: CarInsuranceApi
) {

    open suspend fun getCarQuotes(request: CarQuoteRequest): Resource<CarQuoteResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = carInsuranceApi.getCarQuotes(request)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!)
                } else if (AppConfig.USE_DUMMY_DATA) {
                    // Fallback to staging/dev mock quote generation when USE_DUMMY_DATA is enabled
                    delay(1200.milliseconds)
                    Resource.Success(buildDevFallbackResponse(request, "Quotes generated successfully"))
                } else {
                    val serverMessage = response.message().ifBlank {
                        "Unable to fetch quotes (${response.code()}). Please try again."
                    }
                    Resource.Error(serverMessage)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (AppConfig.USE_DUMMY_DATA) {
                    delay(1000.milliseconds)
                    Resource.Success(
                        buildDevFallbackResponse(request, "Quotes generated successfully (Local Dev Mode)")
                    )
                } else {
                    Resource.Error(NetworkError.fromThrowable(e).errorMessage)
                }
            }
        }
    }

    private fun buildDevFallbackResponse(
        request: CarQuoteRequest,
        message: String
    ): CarQuoteResponse = CarQuoteResponse(
        status = "SUCCESS",
        message = message,
        quoteId = "CAR-QUOTE-${System.currentTimeMillis() % 100000}",
        quoteUrl = "https://www.policyboss.com/car-insurance?ss_id=${request.ssId}&fba_id=${request.fbaId}&product_id=1",
        quotesCount = 8,
        minPremium = 6403,
        maxEarnings = 1403,
        cpaDescription = "CPA: Don't have Personal Accident cover"
    )
}
