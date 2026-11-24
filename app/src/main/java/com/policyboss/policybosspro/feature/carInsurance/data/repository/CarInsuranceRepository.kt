package com.policyboss.policybosspro.feature.carInsurance.data.repository

import com.policyboss.policybosspro.BuildConfig
import com.policyboss.policybosspro.core.AppConfig
import com.policyboss.policybosspro.core.Resource
import com.policyboss.policybosspro.core.datastore.AppDataManager
import com.policyboss.policybosspro.core.network.NetworkError
import com.policyboss.policybosspro.feature.carInsurance.data.api.CarInsuranceApi
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.compactRegistration
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.isVehicleFound
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.toDomain
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteResponse
import com.policyboss.policybosspro.feature.carInsurance.data.model.VehicleInfoRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.VehicleModelItemDto
import com.policyboss.policybosspro.feature.carInsurance.model.VehicleInfoDomain
import com.policyboss.policybosspro.feature.carInsurance.model.VehicleModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
open class CarInsuranceRepository @Inject constructor(
    private val carInsuranceApi: CarInsuranceApi,
    private val appDataManager: AppDataManager,
    private val json: Json
) {

    /** In-memory model-list cache shared by every car journey for the app session. */
    @Volatile
    private var cachedModels: List<VehicleModel>? = null
    private val modelListMutex = Mutex()

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

    /**
     * Vahan lookup for [registrationNumber]. Returns [Resource.Error] for HTTP/network
     * failures and for responses without a usable vehicle — never fabricated data.
     */
    open suspend fun getVehicleInfo(registrationNumber: String): Resource<VehicleInfoDomain> {
        val compactNumber = registrationNumber.compactRegistration()
        return withContext(Dispatchers.IO) {
            try {
                val request = VehicleInfoRequest(
                    secretKey = BuildConfig.VEHICLE_INFO_SECRET_KEY,
                    clientKey = BuildConfig.VEHICLE_INFO_CLIENT_KEY,
                    registrationNumber = compactNumber,
                    ssId = appDataManager.getSsId().toIntOrNull() ?: 0
                )
                val response = carInsuranceApi.getVehicleInfo(request)
                val body = response.body()
                when {
                    !response.isSuccessful || body == null -> Resource.Error(
                        NetworkError.ServerError(response.code()).errorMessage
                    )
                    !body.isVehicleFound() -> Resource.Error(vehicleNotFoundMessage(compactNumber))
                    else -> Resource.Success(body.toDomain(compactNumber))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Resource.Error(NetworkError.fromThrowable(e).errorMessage)
            }
        }
    }

    /**
     * Live Make/Model catalogue for cars (product_id = 1).
     *
     * Successful results are cached in memory, and concurrent callers share one request
     * through [modelListMutex], so reopening the bottom sheet never re-fetches. Errors are
     * not cached, so a later call retries.
     */
    open suspend fun getModelList(forceRefresh: Boolean = false): Resource<List<VehicleModel>> {
        if (!forceRefresh) cachedModels?.let { return Resource.Success(it) }
        return modelListMutex.withLock {
            if (!forceRefresh) cachedModels?.let { return@withLock Resource.Success(it) }
            withContext(Dispatchers.IO) {
                try {
                    val response = carInsuranceApi.getModelList()
                    val body = response.body()
                    if (!response.isSuccessful || body == null) {
                        Resource.Error(NetworkError.ServerError(response.code()).errorMessage)
                    } else {
                        val models = parseModelList(body)
                        if (models.isNotEmpty()) cachedModels = models
                        Resource.Success(models)
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Resource.Error(NetworkError.fromThrowable(e).errorMessage)
                }
            }
        }
    }

    /**
     * Accepts a bare array or an object wrapping the array (first array-valued property).
     * Each record is decoded on its own so one malformed item is skipped, not fatal.
     */
    internal fun parseModelList(element: JsonElement): List<VehicleModel> {
        val records: JsonArray = when (element) {
            is JsonArray -> element
            is JsonObject -> element.values.firstOrNull { it is JsonArray } as? JsonArray ?: JsonArray(emptyList())
            else -> JsonArray(emptyList())
        }
        return records.mapNotNull { item ->
            runCatching { json.decodeFromJsonElement(VehicleModelItemDto.serializer(), item) }
                .getOrNull()
                ?.toDomain()
        }.distinctBy { it.modelId }
    }

    private fun vehicleNotFoundMessage(registrationNumber: String): String =
        "We couldn't find vehicle details for $registrationNumber. Please enter the car details manually."

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
