package com.policyboss.policybosspro.feature.carInsurance

import com.policyboss.policybosspro.core.Resource
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.VehicleDateFormatter
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.groupByMake
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.isVehicleFound
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.toBrandModelItems
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.toDomain
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.toNewCarUiState
import com.policyboss.policybosspro.feature.carInsurance.data.model.VehicleInfoRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.VehicleInfoResponseDto
import com.policyboss.policybosspro.feature.carInsurance.data.repository.CarInsuranceRepository
import com.policyboss.policybosspro.feature.carInsurance.data.repository.VehicleInfoSessionProvider
import com.policyboss.policybosspro.feature.carInsurance.model.CarEntryMode
import com.policyboss.policybosspro.feature.carInsurance.model.ModelListState
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarAction
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiEvent
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiState
import com.policyboss.policybosspro.feature.carInsurance.model.VehicleLookupState
import com.policyboss.policybosspro.feature.carInsurance.model.VehicleModel
import com.policyboss.policybosspro.feature.carInsurance.viewmodel.NewCarViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * Vehicle info (vehicle_info_user) + model list (vehicles/model_list) integration.
 *
 * NOTE: [MH01BB0197_RESPONSE] is assembled from the field values given in the integration
 * spec; replace it with a captured production response when one is available.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class VehicleInfoIntegrationTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun parse(json: String): VehicleInfoResponseDto =
        testJson.decodeFromString(VehicleInfoResponseDto.serializer(), json)

    private fun sampleDomain() = parse(MH01BB0197_RESPONSE).toDomain("MH-01-BB-0197")

    // ---------------------------------------------------------------- DTO parsing

    @Test
    fun `vehicle info DTO parses MH01BB0197 sample including numeric ids and Vehicle_Matches`() {
        val dto = parse(MH01BB0197_RESPONSE)

        assertTrue(dto.isVehicleFound())
        assertEquals("MH01BB0197", dto.registrationNumber)
        assertEquals("MARUTI", dto.makeName)
        assertEquals("WAGON R", dto.modelName)
        assertEquals("38", dto.modelId)          // numeric in JSON
        assertEquals("998", dto.cubicCapacity)   // numeric in JSON
        assertEquals("579", dto.rtoCode)
        assertNull(dto.pypPolicyNumber)          // "NA" -> null
        assertNotNull(dto.vehicleMatches)
        assertEquals("MARUTI SUZUKI INDIA LTD", dto.fastlaneResponse?.makerDescription)
        assertNull(dto.fastlaneResponse?.rcNumber) // explicit null
    }

    @Test
    fun `nullable or non-object FastlaneResponse_Obj never fails parsing`() {
        val asString = parse("""{"Make_Name":"MARUTI","Model_Name":"SWIFT","FastlaneResponse_Obj":""}""")
        assertNull(asString.fastlaneResponse)

        val asNull = parse("""{"Make_Name":"MARUTI","Model_Name":"SWIFT","FastlaneResponse_Obj":null}""")
        assertNull(asNull.fastlaneResponse)

        val partial = parse("""{"FastlaneResponse_Obj":{"owner_name":null,"cubic_capacity":1197,"extra":{"a":1}}}""")
        assertEquals("1197", partial.fastlaneResponse?.cubicCapacity)
        assertNull(partial.fastlaneResponse?.ownerName)
    }

    @Test
    fun `failed or empty vehicle info responses are not treated as found`() {
        assertFalse(parse("""{"Result":"Fail","status":"Fail","Error_Message":"Vehicle not found"}""").isVehicleFound())
        assertFalse(parse("""{"status":"Error","Make_Name":"MARUTI","Model_Name":"SWIFT"}""").isVehicleFound())
        assertFalse(parse("""{"status":"Success","Make_Name":"MARUTI"}""").isVehicleFound())
        assertFalse(parse("""{}""").isVehicleFound())
    }

    // ---------------------------------------------------------------- DTO -> domain -> UI state

    @Test
    fun `mapping fills vahan banner, customer, vehicle, rto and dates`() {
        val state = sampleDomain().toNewCarUiState(NewCarUiState())

        assertEquals(CarEntryMode.WITH_CAR_NUMBER, state.entryMode)
        assertTrue(state.showVahanBanner)
        assertEquals("MARUTI SUZUKI INDIA LTD, MARUTI WAGON R VXI BSIV, PETROL (998 CC)", state.vahanData)
        assertEquals("VIJAYKUMAR PAWAR", state.customerName)
        assertEquals("MARUTI-WAGON R-VXI+-PETROL- (998 CC)", state.carDetailsSummary)
        assertEquals("MARUTI-WAGON R-VXI+-PETROL- (998 CC)", state.formattedCarDetails)
        assertEquals("38", state.selectedModel?.value)
        assertEquals("WAGON R", state.selectedModel?.label)
        assertEquals("1001", state.selectedVariant?.value)
        assertEquals("VXI+", state.selectedVariant?.label)
        assertEquals("2", state.selectedFuel?.value)
        assertEquals("579", state.selectedRto?.value)
        assertEquals("MH01BB0197-Mumbai-Tardeo", state.selectedRto?.label)
        assertTrue(state.rtoOptions.contains(state.selectedRto))
        assertEquals("18-Oct-2011", state.registrationDate)
        assertEquals("Sep", state.manufacturingMonth)
        assertEquals("2011", state.manufacturingYear)
    }

    @Test
    fun `existing policy selects Yes, expiry and matching insurer option`() {
        val state = sampleDomain().toNewCarUiState(NewCarUiState())

        assertTrue(state.hasExistingPolicy)
        assertEquals("09-Jun-2030", state.policyExpiryDate)
        // "Bajaj Allianz General Insurance Co. Ltd." matches the existing option by name
        assertEquals("BAJAJ_ALLIANZ", state.selectedInsurer?.value)
        assertTrue(state.isFormValid)
    }

    @Test
    fun `unknown insurer builds option from response id and name`() {
        val dto = parse(MH01BB0197_RESPONSE.replace("Bajaj Allianz General Insurance Co. Ltd.", "Zuno General Insurance"))
        val state = dto.toDomain("MH01BB0197").toNewCarUiState(NewCarUiState())

        assertEquals("9", state.selectedInsurer?.value)
        assertEquals("Zuno General Insurance", state.selectedInsurer?.label)
        assertTrue(state.insurerOptions.first() == state.selectedInsurer)
    }

    @Test
    fun `no insurer selects No and clears expiry and insurer`() {
        val dto = parse(
            """{"Make_Name":"MARUTI","Model_Name":"SWIFT","Owner_Name":"A B","RTO_Code":"1","RTO_Name":"X",
               "Registration_Date":"01/01/2020","Manufacture_Month":"Jan","Manufacture_Year":"2020",
               "Pyp_Insurer_Name":"","Pyp_Policy_Expiry_Date":"09/06/2030",
               "FastlaneResponse_Obj":{"insurance_company":null,"insurance_upto":"2030-06-09"}}"""
        )
        val state = dto.toDomain("MH01AA1111").toNewCarUiState(NewCarUiState())

        assertFalse(state.hasExistingPolicy)
        assertEquals("", state.policyExpiryDate)
        assertNull(state.selectedInsurer)
    }

    @Test
    fun `fastlane fallbacks are used for dates, owner and insurer`() {
        val dto = parse(
            """{"Make_Name":"MARUTI","Model_Name":"WAGON R","RTO_Code":"579","RTO_Name":"Mumbai-Tardeo",
               "Vahan_Make":"VM","Vahan_Model":"VMODEL","Vahan_Fuel":"CNG","Vahan_Cubic_Capacity":"998.0",
               "FastlaneResponse_Obj":{"owner_name":"FAST OWNER","registration_date":"2011-10-18",
               "manufacturing_date":"9/2011","insurance_company":"HDFC ERGO General Insurance",
               "insurance_upto":"2030-06-09"}}"""
        )
        val domain = dto.toDomain("MH01BB0197")

        assertEquals("FAST OWNER", domain.ownerName)
        assertEquals("VM, VMODEL, CNG (998 CC)", domain.vahanSummary)
        assertEquals("18-Oct-2011", domain.registrationDate)
        assertEquals("Sep", domain.manufacturingMonth)
        assertEquals("2011", domain.manufacturingYear)
        assertEquals("HDFC ERGO General Insurance", domain.previousInsurerName)
        assertEquals("09-Jun-2030", domain.policyExpiryDate)
    }

    @Test
    fun `missing locked fields keep the form editable as WITHOUT_CAR_NUMBER`() {
        val dto = parse("""{"Make_Name":"MARUTI","Model_Name":"SWIFT","Owner_Name":"A B"}""")
        val state = dto.toDomain("MH01AA1111").toNewCarUiState(NewCarUiState())

        assertEquals(CarEntryMode.WITHOUT_CAR_NUMBER, state.entryMode)
        assertEquals("A B", state.customerName)
        assertEquals("MARUTI", state.make)
    }

    @Test
    fun `date formatter handles api formats and rejects invalid values`() {
        assertEquals("18-Oct-2011", VehicleDateFormatter.toDisplayDate("18/10/2011"))
        assertEquals("18-Oct-2011", VehicleDateFormatter.toDisplayDate("2011-10-18"))
        assertEquals("", VehicleDateFormatter.toDisplayDate("31/02/2011"))
        assertEquals("", VehicleDateFormatter.toDisplayDate("not a date"))
        assertEquals("", VehicleDateFormatter.toDisplayDate(null))
    }

    private fun resource(path: String): String =
        javaClass.classLoader!!.getResource(path)!!.readText()

    @Test
    fun `real MH01BB0197 response parses and maps to the expected form`() {
        // Live response from api.policyboss.com quote/vehicle_info_user (personal fields redacted)
        val dto = parse(resource("vehicle/vehicle_info_MH01BB0197.json"))
        assertTrue(dto.isVehicleFound())

        val state = dto.toDomain("MH01BB0197").toNewCarUiState(NewCarUiState())

        assertEquals(CarEntryMode.WITH_CAR_NUMBER, state.entryMode)
        assertEquals("MARUTI SUZUKI INDIA LTD, MARUTI WAGON R VXI BSIV, PETROL (998 CC)", state.vahanData)
        assertEquals("VIJAYKUMAR PAWAR", state.customerName)
        assertEquals("MARUTI-WAGON R-VXI+-PETROL- (998 CC)", state.formattedCarDetails)
        assertEquals("138", state.modelId)      // matches Model_ID in vehicles/model_list
        assertEquals("6917", state.variantId)
        assertEquals("1", state.fuelId)
        assertEquals("579", state.selectedRto?.value)
        assertEquals("MH01BB0197-Mumbai-Tardeo", state.selectedRto?.label)
        assertEquals("18-Oct-2011", state.registrationDate)
        assertEquals("Sep", state.manufacturingMonth)
        assertEquals("2011", state.manufacturingYear)
        assertTrue(state.hasExistingPolicy)
        assertEquals("09-Jun-2030", state.policyExpiryDate)
        assertEquals("17", state.selectedInsurer?.value)
        assertEquals("SBI GENERAL INS CO LTD", state.selectedInsurer?.label)
        assertTrue(state.isFormValid)
    }

    @Test
    fun `real not-found response (FASTLANE_DATA_NA_API) is treated as vehicle not found`() {
        val dto = parse(resource("vehicle/vehicle_info_not_found.json"))
        assertEquals("FASTLANE_DATA_NA_API", dto.errorMessage)
        assertFalse(dto.isVehicleFound())
    }

    @Test
    fun `request body sent to vehicle_info_user matches the agreed contract exactly`() = runTest {
        val api = CountingModelListApi(Response.success(testJson.parseToJsonElement("[]")))
        val appDataManager = testAppDataManager().apply { saveUserSsId("150253") }
        val repo = CarInsuranceRepository(api, appDataManager, testJson, VehicleInfoSessionProvider())

        repo.getVehicleInfo("MH-01-BB-0197")

        val sent = testJson.encodeToJsonElement(VehicleInfoRequest.serializer(), api.lastVehicleRequest!!)
        val expected = testJson.parseToJsonElement(
            """
            {
              "secret_key": "SECRET-HZ07QRWY-JIBT-XRMQ-ZP95-J0RWP3DYRACW",
              "client_key": "CLIENT-CNTP6NYE-CU9N-DUZW-CSPI-SH1IS4DOVHB9",
              "RegistrationNumber": "MH01BB0197",
              "product_id": 1,
              "ss_id": 150253,
              "source": "PB-BETA",
              "session_id": "PolYkcquP-c2GlAg1xQXWq5fsD8KxF2V",
              "g-recaptcha-response": ""
            }
            """.trimIndent()
        )
        assertEquals(expected, sent)
    }

    @Test
    fun `request sends constant keys and session id unless the provider overrides it`() = runTest {
        val api = CountingModelListApi(Response.success(testJson.parseToJsonElement("[]")))
        val sessionProvider = VehicleInfoSessionProvider()
        val repo = CarInsuranceRepository(api, testAppDataManager(), testJson, sessionProvider)

        repo.getVehicleInfo("MH01BB0197")
        assertEquals(VehicleInfoRequest.SECRET_KEY, api.lastVehicleRequest?.secretKey)
        assertEquals(VehicleInfoRequest.CLIENT_KEY, api.lastVehicleRequest?.clientKey)
        assertEquals(VehicleInfoRequest.SESSION_ID, api.lastVehicleRequest?.sessionId)

        sessionProvider.updateSessionId("RUNTIME-SESSION")
        repo.getVehicleInfo("MH01BB0197")
        assertEquals("RUNTIME-SESSION", api.lastVehicleRequest?.sessionId)
    }

    // ---------------------------------------------------------------- Model list

    @Test
    fun `model list parses bare and wrapped arrays and skips bad records`() {
        val repo = CarInsuranceRepository(ThrowingCarInsuranceApi(), testAppDataManager(), testJson, VehicleInfoSessionProvider())

        val bare = repo.parseModelList(testJson.parseToJsonElement(MODEL_LIST_RESPONSE))
        assertEquals(4, bare.size)

        val wrapped = repo.parseModelList(testJson.parseToJsonElement("""{"Status":"ok","data":$MODEL_LIST_RESPONSE}"""))
        assertEquals(bare, wrapped)
    }

    @Test
    fun `real model_list response for product 1 parses, dedupes and groups`() {
        // Captured from GET vehicles/model_list?product_id=1 (593 records, 587 unique Model_IDs, 62 makes)
        val raw = javaClass.classLoader!!.getResource("vehicle/model_list_product_1.json")!!.readText()
        val repo = CarInsuranceRepository(ThrowingCarInsuranceApi(), testAppDataManager(), testJson, VehicleInfoSessionProvider())

        val models = repo.parseModelList(testJson.parseToJsonElement(raw))

        assertEquals(587, models.size)
        assertEquals(62, models.groupByMake().size)
        val wagonR = models.first { it.makeName == "MARUTI" && it.modelName == "WAGON R" }
        assertEquals("138", wagonR.modelId)
        assertEquals("MARUTI, WAGON R", wagonR.vehicleName)
    }

    @Test
    fun `models are grouped by unique make and flattened for the sheet`() {
        val repo = CarInsuranceRepository(ThrowingCarInsuranceApi(), testAppDataManager(), testJson, VehicleInfoSessionProvider())
        val models = repo.parseModelList(testJson.parseToJsonElement(MODEL_LIST_RESPONSE))

        val grouped = models.groupByMake()
        assertEquals(listOf("HYUNDAI", "MARUTI"), grouped.keys.toList())
        assertEquals(listOf("SWIFT", "WAGON R"), grouped.getValue("MARUTI").map { it.modelName })

        val items = models.toBrandModelItems()
        assertEquals(listOf("101", "102", "1", "38"), items.map { it.modelId })
        assertEquals("MARUTI WAGON R", items.first { it.modelId == "38" }.vehicleName)
    }

    @Test
    fun `repository caches model list and shares concurrent requests`() = runTest {
        val api = CountingModelListApi(Response.success(testJson.parseToJsonElement(MODEL_LIST_RESPONSE)))
        val repo = CarInsuranceRepository(api, testAppDataManager(), testJson, VehicleInfoSessionProvider())

        val first = async { repo.getModelList() }
        val second = async { repo.getModelList() }
        assertTrue(first.await() is Resource.Success)
        assertTrue(second.await() is Resource.Success)
        assertTrue(repo.getModelList() is Resource.Success)

        assertEquals(1, api.modelListCalls)
    }

    @Test
    fun `repository does not cache model list errors`() = runTest {
        val api = CountingModelListApi(Response.error(500, "".toResponseBody()))
        val repo = CarInsuranceRepository(api, testAppDataManager(), testJson, VehicleInfoSessionProvider())

        assertTrue(repo.getModelList() is Resource.Error)
        assertTrue(repo.getModelList() is Resource.Error)
        assertEquals(2, api.modelListCalls)
    }

    @Test
    fun `repository sends compact registration, product 1 and PB-BETA source`() = runTest {
        val api = CountingModelListApi(Response.success(testJson.parseToJsonElement("[]")))
        val repo = CarInsuranceRepository(api, testAppDataManager(), testJson, VehicleInfoSessionProvider())

        val result = repo.getVehicleInfo("MH-01-BB-0197")

        assertTrue(result is Resource.Success)
        val request = api.lastVehicleRequest!!
        assertEquals("MH01BB0197", request.registrationNumber)
        assertEquals(1, request.productId)
        assertEquals("PB-BETA", request.source)
        assertEquals(0, request.ssId) // no logged-in session in this test
        val encoded = testJson.encodeToJsonElement(VehicleInfoRequest.serializer(), request)
        assertEquals("MH01BB0197", (encoded as kotlinx.serialization.json.JsonObject)["RegistrationNumber"]?.jsonPrimitive?.content)
        assertTrue(encoded.containsKey("g-recaptcha-response"))
    }

    @Test
    fun `repository maps vehicle not found to error`() = runTest {
        val api = CountingModelListApi(
            Response.success(testJson.parseToJsonElement("[]")),
            vehicleResponse = """{"Result":"Fail","status":"Fail","Error_Message":"No record"}"""
        )
        val repo = CarInsuranceRepository(api, testAppDataManager(), testJson, VehicleInfoSessionProvider())

        val result = repo.getVehicleInfo("MH01BB0197")
        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).message.contains("MH01BB0197"))
    }

    // ---------------------------------------------------------------- ViewModel flows

    @Test
    fun `WITH_CAR_NUMBER shows loader, runs both calls and fills the form`() {
        val repo = FakeCarInsuranceRepository(
            vehicleInfo = { Resource.Success(sampleDomain()) },
            models = { Resource.Success(listOf(VehicleModel("38", "MARUTI", "WAGON R", "MARUTI WAGON R"))) }
        )
        val vm = NewCarViewModel(repo)

        vm.initForMode(CarEntryMode.WITH_CAR_NUMBER, "MH-01-BB-0197")
        assertEquals(VehicleLookupState.Loading, vm.uiState.value.vehicleLookupState)
        assertTrue(vm.uiState.value.isVehicleLookupInProgress)

        testDispatcher.scheduler.advanceUntilIdle()
        val state = vm.uiState.value
        assertEquals(VehicleLookupState.Idle, state.vehicleLookupState)
        assertEquals(CarEntryMode.WITH_CAR_NUMBER, state.entryMode)
        assertEquals("VIJAYKUMAR PAWAR", state.customerName)
        assertEquals(ModelListState.Success, state.modelListState)
        assertEquals(1, repo.vehicleInfoCalls)
        assertEquals(1, repo.modelListCalls)

        // Field locks preserved
        vm.onAction(NewCarAction.OnCustomerNameChange("Someone Else"))
        assertEquals("VIJAYKUMAR PAWAR", vm.uiState.value.customerName)

        // Opening the sheet preselects the looked-up vehicle without another model-list call
        vm.onAction(NewCarAction.OpenCarDetailsSheet)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("38", vm.uiState.value.draftBrandModel?.modelId)
        assertEquals("VXI+", vm.uiState.value.draftVariant?.variantName)
        assertTrue(vm.uiState.value.filteredVariants.any { it.variantName == "VXI+" })
        assertEquals(1, repo.modelListCalls)
    }

    @Test
    fun `vehicle not found shows loader error and continues manually`() {
        val repo = FakeCarInsuranceRepository(vehicleInfo = { Resource.Error("We couldn't find vehicle details") })
        val vm = NewCarViewModel(repo)

        vm.initForMode(CarEntryMode.WITH_CAR_NUMBER, "MH01BB0197")
        testDispatcher.scheduler.advanceUntilIdle()

        val error = vm.uiState.value.vehicleLookupState as? VehicleLookupState.Error
        assertEquals("We couldn't find vehicle details", error?.message)
        assertFalse(vm.uiState.value.showVahanBanner)
        assertEquals("", vm.uiState.value.customerName)

        vm.onAction(NewCarAction.OnContinueWithoutVehicleInfo)
        testDispatcher.scheduler.advanceUntilIdle()
        val state = vm.uiState.value
        assertEquals(VehicleLookupState.Idle, state.vehicleLookupState)
        assertEquals(CarEntryMode.WITHOUT_CAR_NUMBER, state.entryMode)
        assertEquals("", state.registrationNumber)

        // Re-running the route's init (e.g. after rotation) must not restart the lookup
        vm.initForMode(CarEntryMode.WITH_CAR_NUMBER, "MH01BB0197")
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(CarEntryMode.WITHOUT_CAR_NUMBER, vm.uiState.value.entryMode)
        assertEquals(1, repo.vehicleInfoCalls)
    }

    @Test
    fun `retry re-runs the lookup after a failure`() {
        var attempt = 0
        val repo = FakeCarInsuranceRepository(vehicleInfo = {
            attempt++
            if (attempt == 1) Resource.Error("Timeout") else Resource.Success(sampleKiaVehicle(it))
        })
        val vm = NewCarViewModel(repo)

        vm.initForMode(CarEntryMode.WITH_CAR_NUMBER, "RJ14-1234")
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.uiState.value.vehicleLookupState is VehicleLookupState.Error)

        vm.onAction(NewCarAction.OnRetryVehicleLookup)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(VehicleLookupState.Idle, vm.uiState.value.vehicleLookupState)
        assertEquals("PRIYANKA VYAS", vm.uiState.value.customerName)
        assertEquals(2, repo.vehicleInfoCalls)
    }

    @Test
    fun `cancelling the lookup navigates back and ignores late results`() = runTest {
        val pending = CompletableDeferred<Resource<com.policyboss.policybosspro.feature.carInsurance.model.VehicleInfoDomain>>()
        val repo = object : FakeCarInsuranceRepository() {
            override suspend fun getVehicleInfo(registrationNumber: String) = pending.await()
        }
        val vm = NewCarViewModel(repo)
        val events = mutableListOf<NewCarUiEvent>()
        val job = launch { vm.uiEvent.collect { events.add(it) } }

        vm.initForMode(CarEntryMode.WITH_CAR_NUMBER, "RJ14-1234")
        advanceUntilIdle()
        vm.onAction(NewCarAction.OnCancelVehicleLookup)
        advanceUntilIdle()

        pending.complete(Resource.Success(sampleKiaVehicle()))
        advanceUntilIdle()

        assertEquals(VehicleLookupState.Idle, vm.uiState.value.vehicleLookupState)
        assertEquals("", vm.uiState.value.customerName)
        assertTrue(events.contains(NewCarUiEvent.NavigateBack))
        job.cancel()
    }

    @Test
    fun `WITHOUT_CAR_NUMBER loads model list once and reuses it when the sheet reopens`() {
        val repo = FakeCarInsuranceRepository(
            models = { Resource.Success(listOf(VehicleModel("38", "MARUTI", "WAGON R", "MARUTI WAGON R"))) }
        )
        val vm = NewCarViewModel(repo)

        vm.initForMode(CarEntryMode.WITHOUT_CAR_NUMBER)
        testDispatcher.scheduler.advanceUntilIdle()
        repeat(3) {
            vm.onAction(NewCarAction.OpenCarDetailsSheet)
            vm.onAction(NewCarAction.DismissCarDetailsSheet)
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, repo.vehicleInfoCalls)
        assertEquals(1, repo.modelListCalls)
        assertEquals(listOf("MARUTI, WAGON R"), vm.uiState.value.filteredBrandModels.map { it.displayName })
    }

    @Test
    fun `model list failure exposes error and retry recovers`() {
        var attempt = 0
        val repo = FakeCarInsuranceRepository(models = {
            attempt++
            if (attempt == 1) Resource.Error("No internet connection.")
            else Resource.Success(listOf(VehicleModel("1", "MARUTI", "SWIFT", "MARUTI SWIFT")))
        })
        val vm = NewCarViewModel(repo)

        vm.initForMode(CarEntryMode.NEW_CAR)
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.uiState.value.modelListState is ModelListState.Error)
        assertTrue(vm.uiState.value.brandModels.isEmpty())

        vm.onAction(NewCarAction.OnRetryModelList)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(ModelListState.Success, vm.uiState.value.modelListState)
        assertEquals(1, vm.uiState.value.brandModels.size)
    }

    // ---------------------------------------------------------------- Fakes & fixtures

    private class CountingModelListApi(
        private val modelListResponse: Response<JsonElement>,
        private val vehicleResponse: String = MH01BB0197_RESPONSE
    ) : ThrowingCarInsuranceApi() {
        var modelListCalls = 0
        var lastVehicleRequest: VehicleInfoRequest? = null

        override suspend fun getModelList(productId: Int): Response<JsonElement> {
            assertEquals(1, productId)
            modelListCalls++
            return modelListResponse
        }

        override suspend fun getVehicleInfo(request: VehicleInfoRequest): Response<VehicleInfoResponseDto> {
            lastVehicleRequest = request
            return Response.success(testJson.decodeFromString(VehicleInfoResponseDto.serializer(), vehicleResponse))
        }
    }

    companion object {
        val MH01BB0197_RESPONSE = """
            {
              "Result": "Success",
              "status": "Success",
              "Error_Message": "",
              "Registration_Number": "MH01BB0197",
              "Make_Name": "MARUTI",
              "Model_Name": "WAGON R",
              "Fuel_Type": "PETROL",
              "Variant_Name": "VXI+",
              "Model_ID": 38,
              "Variant_Id": 1001,
              "Fuel_ID": 2,
              "Cubic_Capacity": 998,
              "Seating_Capacity": 5,
              "Manufacture_Year": "2011",
              "Manufacture_Month": "Sep",
              "Registration_Date": "18/10/2011",
              "RTO_Code": 579,
              "RTO_Name": "Mumbai-Tardeo",
              "Owner_Name": "VIJAYKUMAR PAWAR",
              "Pyp_Policy_Expiry_Date": "09/06/2030",
              "Pyp_Insurer_Name": "Bajaj Allianz General Insurance Co. Ltd.",
              "Pyp_Insurer_Id": 9,
              "Pyp_Policy_Number": "NA",
              "Vahan_Make": "MARUTI SUZUKI INDIA LTD",
              "Vahan_Model": "MARUTI WAGON R VXI BSIV",
              "Vahan_Variant": "VXI BSIV",
              "Vahan_Fuel": "PETROL",
              "Vahan_Cubic_Capacity": "998",
              "Vehicle_Detail_Id": 123456,
              "Unexpected_Field": {"nested": [1, 2, 3]},
              "FastlaneResponse_Obj": {
                "maker_description": "MARUTI SUZUKI INDIA LTD",
                "maker_model": "MARUTI WAGON R VXI BSIV",
                "fuel_type": "PETROL",
                "cubic_capacity": "998",
                "owner_name": "VIJAYKUMAR PAWAR",
                "registration_date": "2011-10-18",
                "manufacturing_date": "9/2011",
                "insurance_company": "BAJAJ ALLIANZ",
                "insurance_upto": "2030-06-09",
                "rc_number": null,
                "unknown_block": {"a": true}
              },
              "Vehicle_Matches": [
                {"Insurer_Id": 1, "Model": {"Id": 9, "Odd": [null, "x"]}, "Rates": null},
                {"Insurer_Id": "2", "Variant": "VXI", "Extra": [[1], {"b": 2}]},
                "unexpected-string",
                null
              ]
            }
        """.trimIndent()

        val MODEL_LIST_RESPONSE = """
            [
              {"Model_ID": 38, "Make_Name": "MARUTI", "Model_Name": "WAGON R", "vehicle_name": "MARUTI WAGON R"},
              {"Model_ID": "1", "Make_Name": "MARUTI", "Model_Name": "SWIFT", "vehicle_name": "MARUTI SWIFT"},
              {"Model_ID": 101, "Make_Name": "HYUNDAI", "Model_Name": "CRETA", "vehicle_name": "HYUNDAI CRETA"},
              {"Model_ID": 102, "Make_Name": "HYUNDAI", "Model_Name": "I20", "vehicle_name": null},
              {"Model_ID": 38, "Make_Name": "MARUTI", "Model_Name": "WAGON R", "vehicle_name": "DUPLICATE"},
              {"Model_ID": null, "Make_Name": "BROKEN", "Model_Name": "NO ID"},
              {"Model_ID": "abc", "Make_Name": "BAD", "Model_Name": "ID"},
              "not-an-object"
            ]
        """.trimIndent()
    }
}
