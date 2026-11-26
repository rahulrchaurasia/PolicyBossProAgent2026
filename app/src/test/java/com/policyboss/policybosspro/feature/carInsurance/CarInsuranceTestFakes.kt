package com.policyboss.policybosspro.feature.carInsurance

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.policyboss.policybosspro.core.Resource
import com.policyboss.policybosspro.core.datastore.AppDataManager
import com.policyboss.policybosspro.feature.carInsurance.data.api.CarInsuranceApi
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteResponse
import com.policyboss.policybosspro.feature.carInsurance.data.model.FuelVariantResponseDto
import com.policyboss.policybosspro.feature.carInsurance.data.model.PreferredMakeModelDto
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumInitiateRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.VehicleInfoRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.VehicleInfoResponseDto
import com.policyboss.policybosspro.feature.carInsurance.data.repository.CarInsuranceRepository
import com.policyboss.policybosspro.feature.carInsurance.data.repository.VehicleInfoSessionProvider
import com.policyboss.policybosspro.feature.carInsurance.model.CarVariantItem
import com.policyboss.policybosspro.feature.carInsurance.model.FuelVariantCatalog
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumInitiateInput
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumInitiateResult
import com.policyboss.policybosspro.feature.carInsurance.model.VehicleInfoDomain
import com.policyboss.policybosspro.feature.carInsurance.model.VehicleModel
import com.policyboss.policybosspro.ui.components.dropDown.DropdownOption
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumInitiateResponseDto
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumListResponseDto
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumSearchRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumSummaryResponseDto
import com.policyboss.policybosspro.feature.carInsurance.model.DeclinedInsurer
import com.policyboss.policybosspro.feature.carInsurance.model.MotorQuote
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumProgress
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumQuoteList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import retrofit2.Response

/** Same configuration as NetworkModule.provideJson(). */
val testJson: Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = true
}

/** In-memory DataStore so AppDataManager can be constructed without Android. */
class InMemoryPreferencesDataStore(initial: Preferences = emptyPreferences()) : DataStore<Preferences> {
    private val state = MutableStateFlow(initial)
    override val data: Flow<Preferences> = state
    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        val updated = transform(state.value)
        state.value = updated
        return updated
    }
}

fun testAppDataManager(): AppDataManager = AppDataManager(InMemoryPreferencesDataStore())

/** API whose calls fail loudly; fakes override repository methods instead. */
open class ThrowingCarInsuranceApi : CarInsuranceApi {
    override suspend fun getCarQuotes(request: CarQuoteRequest): Response<CarQuoteResponse> =
        error("Fake repository overrides getCarQuotes")

    override suspend fun getVehicleInfo(request: VehicleInfoRequest): Response<VehicleInfoResponseDto> =
        error("Fake repository overrides getVehicleInfo")

    override suspend fun getModelList(productId: Int): Response<JsonElement> =
        error("Fake repository overrides getModelList")

    override suspend fun getPreferredMakeModels(ssId: Int, productId: Int): Response<List<PreferredMakeModelDto>> =
        error("Fake repository overrides getPreferredMakeModels")

    override suspend fun getFuelVariants(modelId: String, productId: Int, makeName: String): Response<FuelVariantResponseDto> =
        error("Fake repository overrides getFuelVariants")

    override suspend fun getPreviousInsurers(productId: Int): Response<JsonElement> =
        error("Fake repository overrides getPreviousInsurers")

    override suspend fun premiumInitiate(request: PremiumInitiateRequest): Response<PremiumInitiateResponseDto> =
        error("Fake repository overrides premiumInitiate")

    override suspend fun premiumSummary(request: PremiumSearchRequest): Response<PremiumSummaryResponseDto> =
        error("Fake repository overrides premiumSummary")

    override suspend fun premiumList(request: PremiumSearchRequest): Response<PremiumListResponseDto> =
        error("Fake repository overrides premiumList")
}

fun samplePremiumInitiateResult(crn: String = "11380001") = PremiumInitiateResult(
    crn = crn,
    requestUniqueId = "SRN-$crn"
)

/** premium_list_db result with two insurer plans and one declined insurer. */
fun samplePremiumQuoteList(searchReference: String = "SRN-11380001") = PremiumQuoteList(
    searchReferenceNumber = searchReference,
    crn = searchReference.removePrefix("SRN-"),
    progress = PremiumProgress(status = PremiumProgress.STATUS_COMPLETE, total = 3, pending = 0, success = 2, fail = 1),
    quotes = listOf(
        MotorQuote(id = "ARN-IFFCO", insurerName = "IFFCO Tokio", finalPremium = 2471, totalEarnings = 0, buyNowUrl = "https://example.test/iffco"),
        MotorQuote(id = "ARN-BAJAJ", insurerName = "Bajaj Allianz", finalPremium = 4031, totalEarnings = 0, buyNowUrl = "https://example.test/bajaj")
    ),
    declinedInsurers = listOf(DeclinedInsurer(4, "Future Generali", null, "LM009", "Vehicle Underwriting Restriction")),
    minIdv = 0,
    maxIdv = 0
)

/** A slice of the getPrevInsurer master list (ids as the API returns them). */
val sampleInsurers: List<DropdownOption<String>> = listOf(
    DropdownOption("45", "Acko"),
    DropdownOption("1", "Bajaj Allianz"),
    DropdownOption("13", "Oriental Insurance"),
    DropdownOption("17", "SBI General")
)

/** Generic fuel/variant catalogue for any model (PETROL, DIESEL, CNG variants). */
fun sampleFuelVariantCatalog(modelId: String): FuelVariantCatalog = FuelVariantCatalog(
    modelId = modelId,
    fuels = listOf("PETROL", "DIESEL", "CNG"),
    variants = listOf(
        CarVariantItem("${modelId}_BASE", "Base MT", "PETROL", modelId = modelId, fuelId = "1", cubicCapacity = "1198"),
        CarVariantItem("${modelId}_TOP", "Top ZX AT", "PETROL", modelId = modelId, fuelId = "1", cubicCapacity = "1498"),
        CarVariantItem("${modelId}_DIESEL", "Diesel SX MT", "DIESEL", modelId = modelId, fuelId = "2", cubicCapacity = "1493"),
        CarVariantItem("${modelId}_CNG", "CNG SX", "CNG", modelId = modelId, fuelId = "3", cubicCapacity = "1198")
    )
)

/** The vehicle the former hardcoded WITH_CAR_NUMBER form used, now served by the fake lookup. */
fun sampleKiaVehicle(registrationNumber: String = "RJ14-1234") = VehicleInfoDomain(
    registrationNumber = registrationNumber,
    vahanSummary = "KIA INDIA PRIVATE LIMITED, SELTOS D1.5 6AT GTX PLUS, DIESEL (1493 CC)",
    ownerName = "PRIYANKA VYAS",
    makeName = "KIA",
    modelName = "SELTOS",
    variantName = "Plus 1.5 GTX AT (1493CC)",
    fuelType = "DIESEL",
    modelId = "SELTOS",
    variantId = "SELTOS_PLUS_1.5_GTX_AT",
    fuelId = "DIESEL",
    cubicCapacity = "1493",
    rtoCode = "RJ-14",
    rtoName = "Jaipur",
    registrationDate = "01-Feb-2021",
    manufacturingMonth = "Jan",
    manufacturingYear = "2021",
    previousInsurerId = "ORIENTAL",
    previousInsurerName = "ORIENTAL INSURANCE CO. LTD.",
    policyExpiryDate = "29-Jan-2027",
    vehicleDetailId = "1"
)

/**
 * Repository fake: vehicle lookup and model list resolve immediately; quote calls are
 * overridden per test.
 */
open class FakeCarInsuranceRepository(
    private val vehicleInfo: (String) -> Resource<VehicleInfoDomain> = { Resource.Success(sampleKiaVehicle(it)) },
    private val models: () -> Resource<List<VehicleModel>> = { Resource.Success(emptyList()) },
    private val preferred: () -> Resource<List<VehicleModel>> = { Resource.Success(emptyList()) },
    private val fuelVariants: (String, String) -> Resource<FuelVariantCatalog> = { modelId, _ ->
        Resource.Success(sampleFuelVariantCatalog(modelId))
    },
    private val insurers: () -> Resource<List<DropdownOption<String>>> = { Resource.Success(sampleInsurers) },
    private val premium: suspend (PremiumInitiateInput) -> Resource<PremiumInitiateResult> = {
        Resource.Success(samplePremiumInitiateResult())
    },
    private val premiumQuotes: suspend (String) -> Resource<PremiumQuoteList> = { srn ->
        Resource.Success(samplePremiumQuoteList(srn))
    }
) : CarInsuranceRepository(ThrowingCarInsuranceApi(), testAppDataManager(), testJson, VehicleInfoSessionProvider()) {
    var vehicleInfoCalls = 0
        private set
    var modelListCalls = 0
        private set

    override suspend fun getCarQuotes(request: CarQuoteRequest): Resource<CarQuoteResponse> =
        Resource.Success(CarQuoteResponse())

    override suspend fun getVehicleInfo(registrationNumber: String): Resource<VehicleInfoDomain> {
        vehicleInfoCalls++
        return vehicleInfo(registrationNumber)
    }

    override suspend fun getModelList(forceRefresh: Boolean): Resource<List<VehicleModel>> {
        modelListCalls++
        return models()
    }

    var preferredCalls = 0
        private set
    val fuelVariantCalls = mutableListOf<Pair<String, String>>()

    override suspend fun getPreferredMakeModels(): Resource<List<VehicleModel>> {
        preferredCalls++
        return preferred()
    }

    var insurerCalls = 0
        private set

    override suspend fun getPreviousInsurers(): Resource<List<DropdownOption<String>>> {
        insurerCalls++
        return insurers()
    }

    val premiumInitiateInputs = mutableListOf<PremiumInitiateInput>()

    override suspend fun premiumInitiate(input: PremiumInitiateInput): Resource<PremiumInitiateResult> {
        premiumInitiateInputs += input
        return premium(input)
    }

    /** Search references passed to premium_summary / premium_list_db. */
    val premiumQuoteSearches = mutableListOf<String>()

    override suspend fun awaitPremiumQuotes(
        searchReferenceNumber: String,
        pollInterval: kotlin.time.Duration,
        maxPolls: Int,
        onProgress: suspend (PremiumProgress) -> Unit
    ): Resource<PremiumQuoteList> {
        premiumQuoteSearches += searchReferenceNumber
        onProgress(PremiumProgress(status = "pending", total = 3, pending = 1))
        return premiumQuotes(searchReferenceNumber)
    }

    override suspend fun getFuelVariants(modelId: String, makeName: String): Resource<FuelVariantCatalog> {
        fuelVariantCalls += modelId to makeName
        return fuelVariants(modelId, makeName)
    }
}
