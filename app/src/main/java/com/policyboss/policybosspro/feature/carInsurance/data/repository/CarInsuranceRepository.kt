package com.policyboss.policybosspro.feature.carInsurance.data.repository

import com.policyboss.policybosspro.BuildConfig
import com.policyboss.policybosspro.utils.AppVersionProvider
import com.policyboss.policybosspro.core.AppConfig
import com.policyboss.policybosspro.core.Resource
import com.policyboss.policybosspro.core.datastore.AppDataManager
import com.policyboss.policybosspro.core.network.NetworkConstants
import com.policyboss.policybosspro.core.network.NetworkError
import com.policyboss.policybosspro.feature.carInsurance.data.api.CarInsuranceApi
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.buildPremiumInitiateRequest
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.compactRegistration
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.toPremiumInitiateResult
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumInitiateRequest
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumInitiateInput
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumInitiateResult
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.toPremiumProgress
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.toPremiumQuoteList
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumListResponseDto
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumSearchRequest
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumPollStatus
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumProgress
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumQuotePollEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumQuoteList
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.isVehicleFound
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.toCatalog
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.toDomain
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteResponse
import com.policyboss.policybosspro.feature.carInsurance.data.model.PrevInsurerDto
import com.policyboss.policybosspro.feature.carInsurance.data.model.VehicleInfoRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.VehicleInfoResponseDto
import com.policyboss.policybosspro.feature.carInsurance.data.model.VehicleModelItemDto
import com.policyboss.policybosspro.feature.carInsurance.model.FuelVariantCatalog
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
import com.policyboss.policybosspro.ui.components.dropDown.DropdownOption
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Singleton
open class CarInsuranceRepository @Inject constructor(
    private val carInsuranceApi: CarInsuranceApi,
    private val appDataManager: AppDataManager,
    private val json: Json,
    private val vehicleInfoSessionProvider: VehicleInfoSessionProvider
) {

    /** In-memory model-list cache shared by every car journey for the app session. */
    @Volatile
    private var cachedModels: List<VehicleModel>? = null
    private val modelListMutex = Mutex()

    /** Preferred Make/Models cached per ss_id (agent) for the app session. */
    private val cachedPreferred = java.util.concurrent.ConcurrentHashMap<String, List<VehicleModel>>()
    private val preferredMutex = Mutex()

    /** beta_GetFuelVariant results cached per Model_ID + Make_Name. */
    private val cachedFuelVariants = java.util.concurrent.ConcurrentHashMap<String, FuelVariantCatalog>()
    private val fuelVariantMutex = Mutex()

    /** Previous-insurer master list, cached for the app session. */
    @Volatile
    private var cachedInsurers: List<DropdownOption<String>>? = null
    private val insurerMutex = Mutex()

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
     *
     * Request (full body), parsed response and errors are logged (debug builds only)
     * under Logcat tag [VehicleApiLogger.TAG].
     */
    open suspend fun getVehicleInfo(registrationNumber: String): Resource<VehicleInfoDomain> {
        val compactNumber = registrationNumber.compactRegistration()
        return withContext(Dispatchers.IO) {
            try {
                val rawSsId = appDataManager.getSsId()
                val request = VehicleInfoRequest(
                    registrationNumber = compactNumber,
                    ssId = rawSsId.toIntOrNull() ?: 0,
                    sessionId = vehicleInfoSessionProvider.getSessionId()
                        .ifBlank { NetworkConstants.SESSION_ID }
                )
                logVehicleInfoRequest(request, rawSsId)

                val startedAt = System.currentTimeMillis()
                val response = carInsuranceApi.getVehicleInfo(request)
                val tookMs = System.currentTimeMillis() - startedAt
                val body = response.body()

                when {
                    !response.isSuccessful || body == null -> {
                        val errorBody = runCatching { response.errorBody()?.string() }.getOrNull().orEmpty()
                        VehicleApiLogger.e(
                            "<-- vehicle_info_user FAILED HTTP ${response.code()} ${response.message()} (${tookMs}ms)\n" +
                                "errorBody=${errorBody.ifBlank { "<empty>" }}"
                        )
                        Resource.Error(vehicleInfoHttpErrorMessage(response.code()))
                    }
                    !body.isVehicleFound() -> {
                        VehicleApiLogger.e(
                            "<-- vehicle_info_user NOT FOUND HTTP ${response.code()} (${tookMs}ms) " +
                                "Result=${body.result} status=${body.status} Error_Message=${body.errorMessage} " +
                                "Make_Name=${body.makeName} Model_Name=${body.modelName}"
                        )
                        Resource.Error(vehicleNotFoundMessage(compactNumber))
                    }
                    else -> {
                        val domain = body.toDomain(compactNumber)
                        VehicleApiLogger.d(
                            "<-- vehicle_info_user OK HTTP ${response.code()} (${tookMs}ms)\n" +
                                "parsed response=${json.encodeToString(VehicleInfoResponseDto.serializer(), body.copy(vehicleMatches = null))}\n" +
                                "mapped=$domain"
                        )
                        Resource.Success(domain)
                    }
                }
            } catch (e: CancellationException) {
                VehicleApiLogger.d("vehicle_info_user cancelled for $compactNumber")
                throw e
            } catch (e: Exception) {
                val error = NetworkError.fromThrowable(e)
                VehicleApiLogger.e("<-- vehicle_info_user EXCEPTION for $compactNumber: ${error.errorMessage}", e)
                Resource.Error(error.errorMessage)
            }
        }
    }

    private fun logVehicleInfoRequest(request: VehicleInfoRequest, rawSsId: String) {
        VehicleApiLogger.d(
            "--> POST ${CarInsuranceApi.VEHICLE_INFO_URL}\n" +
                "body=${json.encodeToString(VehicleInfoRequest.serializer(), request)}\n" +
                "ss_id source=AppDataManager.getSsId() raw=\"$rawSsId\"" +
                (if (request.ssId == 0) " (no logged-in ss_id: sending 0)" else "") +
                (if (request.sessionId == NetworkConstants.SESSION_ID) "\nsession_id=constant NetworkConstants.SESSION_ID" else "\nsession_id=from VehicleInfoSessionProvider")
        )
    }

    /** `app_version` for quote APIs, e.g. `policyboss-1.5.6.1`; same format as [AppVersionProvider]. */
    protected open val appVersion: String
        get() = AppVersionProvider.format(BuildConfig.VERSION_NAME)

    /**
     * `quote/premium_initiate` for a vehicle from the vahan lookup. Adds the logged-in ss_id
     * and app version to [input], and returns [Resource.Error] for HTTP/network failures and
     * responses without a CRN / request id — never fabricated data.
     */
    open suspend fun premiumInitiate(input: PremiumInitiateInput): Resource<PremiumInitiateResult> {
        return withContext(Dispatchers.IO) {
            try {
                val request = buildPremiumInitiateRequest(
                    input = input,
                    ssId = appDataManager.getSsId(),
                    appVersion = appVersion,
                    today = LocalDate.now()
                )
                VehicleApiLogger.d(
                    "--> POST ${CarInsuranceApi.PREMIUM_INITIATE_URL}\n" +
                        "body=${json.encodeToString(PremiumInitiateRequest.serializer(), request)}"
                )

                val startedAt = System.currentTimeMillis()
                val response = carInsuranceApi.premiumInitiate(request)
                val tookMs = System.currentTimeMillis() - startedAt
                val body = response.body()

                if (!response.isSuccessful || body == null) {
                    val errorBody = runCatching { response.errorBody()?.string() }.getOrNull().orEmpty()
                    VehicleApiLogger.e(
                        "<-- premium_initiate FAILED HTTP ${response.code()} ${response.message()} (${tookMs}ms)\n" +
                            "errorBody=${errorBody.ifBlank { "<empty>" }}"
                    )
                    return@withContext Resource.Error(
                        "Unable to fetch quotes (${response.code()}). Please try again."
                    )
                }

                VehicleApiLogger.d("<-- premium_initiate HTTP ${response.code()} (${tookMs}ms) summary=${body.summary}")
                val result = body.toPremiumInitiateResult()
                if (result == null) {
                    VehicleApiLogger.e("<-- premium_initiate returned no Request_Unique_Id")
                    Resource.Error("We couldn't start your quotes. Please try again.")
                } else {
                    Resource.Success(result)
                }
            } catch (e: CancellationException) {
                VehicleApiLogger.d("premium_initiate cancelled")
                throw e
            } catch (e: Exception) {
                val error = NetworkError.fromThrowable(e)
                VehicleApiLogger.e("<-- premium_initiate EXCEPTION: ${error.errorMessage}", e)
                Resource.Error(error.errorMessage)
            }
        }
    }

    /**
     * One `quote/premium_summary` call: how many insurers have answered for [searchReferenceNumber]
     * (`Request_Unique_Id` from premium_initiate).
     */
    open suspend fun premiumSummary(searchReferenceNumber: String): Resource<PremiumProgress> =
        quoteSearchCall("premium_summary", CarInsuranceApi.PREMIUM_SUMMARY_URL, searchReferenceNumber,
            call = { carInsuranceApi.premiumSummary(it) },
            map = { body -> body.summary.toPremiumProgress().let { p -> if (p.status.isBlank()) p.copy(status = body.status.orEmpty()) else p } }
        )

    /** One `quote/premium_list_db` call: the insurer plans and premiums for [searchReferenceNumber]. */
    open suspend fun premiumList(searchReferenceNumber: String): Resource<PremiumQuoteList> =
        quoteSearchCall("premium_list_db", CarInsuranceApi.PREMIUM_LIST_URL, searchReferenceNumber,
            call = { carInsuranceApi.premiumList(it) },
            map = { body: PremiumListResponseDto -> body.toPremiumQuoteList(searchReferenceNumber) }
        )

    /**
     * Polls `premium_list_db` until every insurer has answered, emitting each merged snapshot so
     * the results list can grow while insurers respond.
     *
     * - Hit 0 runs immediately. If it fails, a single [PremiumQuotePollEvent.Failed] is emitted.
     * - Then sequential calls (never overlapping, never parallel) while `Summary.Status` is pending:
     *   [fastInterval] apart during the first [fastPhase], when insurers answer quickly, then
     *   [slowInterval] apart (see [premiumPollDelay]), so the count visibly grows (0 → 4 → 8 → 14).
     * - Stops on `complete`, after [maxDuration] of polling ([PremiumPollStatus.TIMED_OUT]), or after
     *   [maxConsecutiveFailures] failed calls in a row ([PremiumPollStatus.STOPPED_ON_ERROR]).
     *   A single failed call is skipped and retried on the next tick; the plans shown are kept.
     * - Collecting in `viewModelScope` makes it cancellable: cancelling stops delays and requests.
     */
    open fun pollPremiumQuotes(
        searchReferenceNumber: String,
        fastInterval: Duration = PREMIUM_POLL_FAST_INTERVAL,
        fastPhase: Duration = PREMIUM_POLL_FAST_PHASE,
        slowInterval: Duration = PREMIUM_POLL_SLOW_INTERVAL,
        maxDuration: Duration = PREMIUM_POLL_MAX_DURATION,
        maxConsecutiveFailures: Int = PREMIUM_POLL_MAX_FAILURES
    ): Flow<PremiumQuotePollEvent> = flow {
        var latest: PremiumQuoteList? = null
        var hits = 0
        var elapsed = Duration.ZERO // time spent waiting between calls
        var failuresInRow = 0

        while (true) {
            when (val result = premiumList(searchReferenceNumber)) {
                is Resource.Success -> {
                    failuresInRow = 0
                    val merged = latest?.mergedWith(result.data) ?: result.data
                    latest = merged
                    val status = when {
                        result.data.progress.isComplete -> PremiumPollStatus.COMPLETE
                        elapsed >= maxDuration -> PremiumPollStatus.TIMED_OUT
                        else -> PremiumPollStatus.POLLING
                    }
                    VehicleApiLogger.d(
                        "premium_list_db hit ${hits + 1} at ${elapsed.inWholeSeconds}s: ${merged.quotes.size} plans, " +
                            "status=${result.data.progress.status}, pending=${result.data.progress.pending} -> $status"
                    )
                    emit(PremiumQuotePollEvent.Update(merged, status))
                    if (status != PremiumPollStatus.POLLING) return@flow
                }
                is Resource.Error -> {
                    val shown = latest
                    if (shown == null) {
                        emit(PremiumQuotePollEvent.Failed(result.message))
                        return@flow
                    }
                    failuresInRow++
                    val stopStatus = when {
                        failuresInRow >= maxConsecutiveFailures -> PremiumPollStatus.STOPPED_ON_ERROR
                        elapsed >= maxDuration -> PremiumPollStatus.TIMED_OUT
                        else -> null
                    }
                    VehicleApiLogger.e("premium_list_db hit ${hits + 1} failed ($failuresInRow in a row): ${result.message}")
                    if (stopStatus != null) {
                        emit(PremiumQuotePollEvent.Update(shown, stopStatus))
                        return@flow
                    }
                }
            }
            hits++
            val wait = premiumPollDelay(elapsed, fastInterval, fastPhase, slowInterval)
            delay(wait)
            elapsed += wait
        }
    }

    /** Shared POST + logging + error handling for the search-reference quote APIs. */
    private suspend fun <Dto : Any, T> quoteSearchCall(
        name: String,
        url: String,
        searchReferenceNumber: String,
        call: suspend (PremiumSearchRequest) -> retrofit2.Response<Dto>,
        map: (Dto) -> T
    ): Resource<T> = withContext(Dispatchers.IO) {
        if (searchReferenceNumber.isBlank()) {
            return@withContext Resource.Error("Quote reference is missing. Please try again.")
        }
        try {
            VehicleApiLogger.d("--> POST $url\nsearch_reference_number=$searchReferenceNumber")
            val startedAt = System.currentTimeMillis()
            val response = call(PremiumSearchRequest(searchReferenceNumber = searchReferenceNumber))
            val tookMs = System.currentTimeMillis() - startedAt
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                val errorBody = runCatching { response.errorBody()?.string() }.getOrNull().orEmpty()
                VehicleApiLogger.e(
                    "<-- $name FAILED HTTP ${response.code()} ${response.message()} (${tookMs}ms)\n" +
                        "errorBody=${errorBody.ifBlank { "<empty>" }}"
                )
                return@withContext Resource.Error("Unable to fetch quotes (${response.code()}). Please try again.")
            }
            VehicleApiLogger.d("<-- $name HTTP ${response.code()} (${tookMs}ms)")
            Resource.Success(map(body))
        } catch (e: CancellationException) {
            VehicleApiLogger.d("$name cancelled")
            throw e
        } catch (e: Exception) {
            val error = NetworkError.fromThrowable(e)
            VehicleApiLogger.e("<-- $name EXCEPTION: ${error.errorMessage}", e)
            Resource.Error(error.errorMessage)
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
        if (!forceRefresh) cachedModels?.let {
            VehicleApiLogger.d("model_list served from memory cache (${it.size} models)")
            return Resource.Success(it)
        }
        return modelListMutex.withLock {
            if (!forceRefresh) cachedModels?.let {
                VehicleApiLogger.d("model_list served from memory cache (${it.size} models)")
                return@withLock Resource.Success(it)
            }
            withContext(Dispatchers.IO) {
                try {
                    VehicleApiLogger.d("--> GET ${CarInsuranceApi.MODEL_LIST_URL}?product_id=${VehicleInfoRequest.CAR_PRODUCT_ID}")
                    val startedAt = System.currentTimeMillis()
                    val response = carInsuranceApi.getModelList()
                    val tookMs = System.currentTimeMillis() - startedAt
                    val body = response.body()
                    if (!response.isSuccessful || body == null) {
                        val errorBody = runCatching { response.errorBody()?.string() }.getOrNull().orEmpty()
                        VehicleApiLogger.e(
                            "<-- model_list FAILED HTTP ${response.code()} ${response.message()} (${tookMs}ms)\n" +
                                "errorBody=${errorBody.ifBlank { "<empty>" }}"
                        )
                        Resource.Error(NetworkError.ServerError(response.code()).errorMessage)
                    } else {
                        val models = parseModelList(body)
                        VehicleApiLogger.d(
                            "<-- model_list OK HTTP ${response.code()} (${tookMs}ms) " +
                                "models=${models.size}, makes=${models.map { it.makeName }.distinct().size}, " +
                                "first=${models.take(3)}"
                        )
                        if (models.isNotEmpty()) cachedModels = models
                        Resource.Success(models)
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    val error = NetworkError.fromThrowable(e)
                    VehicleApiLogger.e("<-- model_list EXCEPTION: ${error.errorMessage}", e)
                    Resource.Error(error.errorMessage)
                }
            }
        }
    }

    /**
     * The logged-in agent's preferred Make/Models (ss_id from AppDataManager.getSsId()).
     * Successful non-empty results are cached per ss_id; errors are not cached.
     */
    open suspend fun getPreferredMakeModels(): Resource<List<VehicleModel>> {
        val rawSsId = appDataManager.getSsId()
        val ssId = rawSsId.toIntOrNull() ?: 0
        cachedPreferred[rawSsId]?.let { return Resource.Success(it) }
        return preferredMutex.withLock {
            cachedPreferred[rawSsId]?.let { return@withLock Resource.Success(it) }
            withContext(Dispatchers.IO) {
                try {
                    VehicleApiLogger.d("--> GET ${CarInsuranceApi.PREFERRED_MAKE_MODEL_URL}?ss_id=$ssId&product_id=${VehicleInfoRequest.CAR_PRODUCT_ID}")
                    val response = carInsuranceApi.getPreferredMakeModels(ssId = ssId)
                    val body = response.body()
                    if (!response.isSuccessful || body == null) {
                        val errorBody = runCatching { response.errorBody()?.string() }.getOrNull().orEmpty()
                        VehicleApiLogger.e("<-- get_preferred FAILED HTTP ${response.code()} errorBody=${errorBody.ifBlank { "<empty>" }}")
                        Resource.Error(NetworkError.ServerError(response.code()).errorMessage)
                    } else {
                        val models = body.mapNotNull { it.toDomain() }.distinctBy { it.modelId }
                        VehicleApiLogger.d("<-- get_preferred OK HTTP ${response.code()} models=$models")
                        if (models.isNotEmpty()) cachedPreferred[rawSsId] = models
                        Resource.Success(models)
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    val error = NetworkError.fromThrowable(e)
                    VehicleApiLogger.e("<-- get_preferred EXCEPTION: ${error.errorMessage}", e)
                    Resource.Error(error.errorMessage)
                }
            }
        }
    }

    /**
     * Fuel types + variants for one model (beta_GetFuelVariant/{modelId}/1/{makeName}).
     * Cached per model, so moving back and forth between the Fuel and Variant steps never
     * re-fetches. Errors and empty results are not cached, so they can be retried.
     */
    open suspend fun getFuelVariants(modelId: String, makeName: String): Resource<FuelVariantCatalog> {
        val key = "${modelId.trim()}|${makeName.trim().uppercase()}"
        cachedFuelVariants[key]?.let { return Resource.Success(it) }
        return fuelVariantMutex.withLock {
            cachedFuelVariants[key]?.let { return@withLock Resource.Success(it) }
            withContext(Dispatchers.IO) {
                try {
                    VehicleApiLogger.d("--> GET vehicles/beta_GetFuelVariant/${modelId.trim()}/${VehicleInfoRequest.CAR_PRODUCT_ID}/${makeName.trim()}")
                    val response = carInsuranceApi.getFuelVariants(modelId = modelId.trim(), makeName = makeName.trim())
                    val body = response.body()
                    if (!response.isSuccessful || body == null) {
                        val errorBody = runCatching { response.errorBody()?.string() }.getOrNull().orEmpty()
                        VehicleApiLogger.e("<-- beta_GetFuelVariant FAILED HTTP ${response.code()} errorBody=${errorBody.ifBlank { "<empty>" }}")
                        Resource.Error(NetworkError.ServerError(response.code()).errorMessage)
                    } else {
                        val catalog = body.toCatalog(modelId.trim())
                        VehicleApiLogger.d(
                            "<-- beta_GetFuelVariant OK HTTP ${response.code()} fuels=${catalog.fuels} " +
                                "variants=${catalog.variants.size} first=${catalog.variants.take(3)}"
                        )
                        if (catalog.fuels.isNotEmpty() && catalog.variants.isNotEmpty()) cachedFuelVariants[key] = catalog
                        Resource.Success(catalog)
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    val error = NetworkError.fromThrowable(e)
                    VehicleApiLogger.e("<-- beta_GetFuelVariant EXCEPTION: ${error.errorMessage}", e)
                    Resource.Error(error.errorMessage)
                }
            }
        }
    }

    /**
     * Previous-insurer master list (insurers/getPrevInsurer/1) as dropdown options
     * (value = insurer_id, label = insurer_name). Non-empty results are cached for the app
     * session; errors are not cached, so a later call retries.
     */
    open suspend fun getPreviousInsurers(): Resource<List<DropdownOption<String>>> {
        cachedInsurers?.let { return Resource.Success(it) }
        return insurerMutex.withLock {
            cachedInsurers?.let { return@withLock Resource.Success(it) }
            withContext(Dispatchers.IO) {
                try {
                    VehicleApiLogger.d("--> GET ${CarInsuranceApi.PREV_INSURER_URL.replace("{productId}", VehicleInfoRequest.CAR_PRODUCT_ID.toString())}")
                    val response = carInsuranceApi.getPreviousInsurers()
                    val body = response.body()
                    if (!response.isSuccessful || body == null) {
                        val errorBody = runCatching { response.errorBody()?.string() }.getOrNull().orEmpty()
                        VehicleApiLogger.e("<-- getPrevInsurer FAILED HTTP ${response.code()} errorBody=${errorBody.ifBlank { "<empty>" }}")
                        Resource.Error(NetworkError.ServerError(response.code()).errorMessage)
                    } else {
                        val insurers = parsePreviousInsurers(body)
                        VehicleApiLogger.d("<-- getPrevInsurer OK HTTP ${response.code()} insurers=${insurers.size} ${insurers.map { "${it.value}:${it.label}" }}")
                        if (insurers.isNotEmpty()) cachedInsurers = insurers
                        Resource.Success(insurers)
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    val error = NetworkError.fromThrowable(e)
                    VehicleApiLogger.e("<-- getPrevInsurer EXCEPTION: ${error.errorMessage}", e)
                    Resource.Error(error.errorMessage)
                }
            }
        }
    }

    /**
     * `{"0":{...},"1":{...}}` (values in numeric key order) or a bare array. Each item is
     * decoded on its own; items without id or name are skipped; ids are de-duplicated.
     */
    internal fun parsePreviousInsurers(element: JsonElement): List<DropdownOption<String>> {
        val items: List<JsonElement> = when (element) {
            is JsonArray -> element
            is JsonObject -> element.entries
                .sortedWith(compareBy({ it.key.toIntOrNull() ?: Int.MAX_VALUE }, { it.key }))
                .map { it.value }
            else -> emptyList()
        }
        return items.mapNotNull { item ->
            val dto = runCatching { json.decodeFromJsonElement(PrevInsurerDto.serializer(), item) }.getOrNull()
            val id = dto?.insurerId?.trim().orEmpty()
            val name = dto?.insurerName?.trim().orEmpty()
            if (id.isBlank() || name.isBlank()) null else DropdownOption(value = id, label = name)
        }.distinctBy { it.value }
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

    private fun vehicleInfoHttpErrorMessage(code: Int): String = when (code) {
        401, 403 -> "Vehicle lookup access was denied. Please try again later."
        else -> NetworkError.ServerError(code).errorMessage
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

/**
 * premium_list_db polling: calls 1 s apart for the first 15 s (insurers answer in ~9 s), then 3 s
 * apart, for up to 60 s, stopping after 3 failures in a row.
 */
internal val PREMIUM_POLL_FAST_INTERVAL: Duration = 1.seconds
internal val PREMIUM_POLL_FAST_PHASE: Duration = 15.seconds
internal val PREMIUM_POLL_SLOW_INTERVAL: Duration = 3.seconds
internal val PREMIUM_POLL_MAX_DURATION: Duration = 60.seconds
internal const val PREMIUM_POLL_MAX_FAILURES = 3

/** Wait before the next premium_list_db call, given the time already spent polling. */
internal fun premiumPollDelay(
    elapsed: Duration,
    fastInterval: Duration = PREMIUM_POLL_FAST_INTERVAL,
    fastPhase: Duration = PREMIUM_POLL_FAST_PHASE,
    slowInterval: Duration = PREMIUM_POLL_SLOW_INTERVAL
): Duration = if (elapsed < fastPhase) fastInterval else slowInterval
