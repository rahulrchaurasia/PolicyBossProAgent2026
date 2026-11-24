package com.policyboss.policybosspro.feature.carInsurance

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.policyboss.policybosspro.core.Resource
import com.policyboss.policybosspro.core.datastore.AppDataManager
import com.policyboss.policybosspro.feature.carInsurance.data.api.CarInsuranceApi
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteResponse
import com.policyboss.policybosspro.feature.carInsurance.data.model.VehicleInfoRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.VehicleInfoResponseDto
import com.policyboss.policybosspro.feature.carInsurance.data.repository.CarInsuranceRepository
import com.policyboss.policybosspro.feature.carInsurance.data.repository.VehicleInfoSessionProvider
import com.policyboss.policybosspro.feature.carInsurance.model.VehicleInfoDomain
import com.policyboss.policybosspro.feature.carInsurance.model.VehicleModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
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
}

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
    private val models: () -> Resource<List<VehicleModel>> = { Resource.Success(emptyList()) }
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
}
