package com.policyboss.policybosspro.feature.carInsurance

import com.policyboss.policybosspro.core.Resource
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.orderBrandModels
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.toCatalog
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.toDomain
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteResponse
import com.policyboss.policybosspro.feature.carInsurance.data.model.FuelVariantResponseDto
import com.policyboss.policybosspro.feature.carInsurance.data.model.PreferredMakeModelDto
import com.policyboss.policybosspro.feature.carInsurance.data.repository.CarInsuranceRepository
import com.policyboss.policybosspro.feature.carInsurance.data.repository.VehicleInfoSessionProvider
import com.policyboss.policybosspro.feature.carInsurance.model.CarDetailsStep
import com.policyboss.policybosspro.feature.carInsurance.model.CarEntryMode
import com.policyboss.policybosspro.feature.carInsurance.model.FuelVariantCatalog
import com.policyboss.policybosspro.feature.carInsurance.model.FuelVariantState
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarAction
import com.policyboss.policybosspro.feature.carInsurance.model.VehicleModel
import com.policyboss.policybosspro.feature.carInsurance.viewmodel.NewCarViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.builtins.ListSerializer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * 3-step Car Details sheet: preferred + model_list (step 1), beta_GetFuelVariant fuels
 * (step 2) and fuel-filtered variants (step 3). Fixtures are live responses captured from
 * api.policyboss.com (ss_id 150253, Model_ID 138 / MARUTI).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CarDetailsSheetCatalogTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(testDispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun resource(path: String) = javaClass.classLoader!!.getResource(path)!!.readText()

    private val preferredModels: List<VehicleModel> by lazy {
        testJson.decodeFromString(
            ListSerializer(PreferredMakeModelDto.serializer()),
            resource("vehicle/preferred_make_model_ss150253.json")
        ).mapNotNull { it.toDomain() }
    }

    private val catalogueModels: List<VehicleModel> by lazy {
        CarInsuranceRepository(ThrowingCarInsuranceApi(), testAppDataManager(), testJson, VehicleInfoSessionProvider())
            .parseModelList(testJson.parseToJsonElement(resource("vehicle/model_list_product_1.json")))
    }

    private val wagonRResponse: FuelVariantResponseDto by lazy {
        testJson.decodeFromString(FuelVariantResponseDto.serializer(), resource("vehicle/fuel_variant_138_MARUTI.json"))
    }

    private val wagonRCatalog: FuelVariantCatalog by lazy { wagonRResponse.toCatalog("138") }

    private fun liveRepository(onQuote: (CarQuoteRequest) -> Unit = {}) = object : FakeCarInsuranceRepository(
        models = { Resource.Success(catalogueModels) },
        preferred = { Resource.Success(preferredModels) },
        fuelVariants = { modelId, _ ->
            if (modelId == "138") Resource.Success(wagonRCatalog) else Resource.Success(sampleFuelVariantCatalog(modelId))
        }
    ) {
        override suspend fun getCarQuotes(request: CarQuoteRequest): Resource<CarQuoteResponse> {
            onQuote(request)
            return Resource.Error("not under test")
        }
    }

    private fun idle() = testDispatcher.scheduler.advanceUntilIdle()

    // ---------------------------------------------------------------- Parsing

    @Test
    fun `preferred and fuel-variant payloads parse including irregular keys`() {
        assertEquals(
            listOf("MARUTI, WAGON R", "MARUTI, SWIFT", "MARUTI, EECO", "MARUTI, ALTO", "MARUTI, ERTIGA", "HYUNDAI, CRETA"),
            preferredModels.map { it.vehicleName }
        )
        assertEquals("138", preferredModels.first().modelId)

        // VariantList mixes IsActive/Is_Active, Make_ID/undefined, Age_* and Insurer_* objects
        assertEquals(listOf("PETROL", "CNG", "PETROL/CNG", "LPG"), wagonRResponse.fuelList)
        assertEquals(52, wagonRResponse.variantList.size)
        assertEquals(52, wagonRCatalog.variants.size)
        val lxiAbs = wagonRCatalog.variants.first { it.variantId == "7834" }
        assertEquals("1.0 LXI ABS (998CC)", lxiAbs.displayLabel)
        assertEquals("1", lxiAbs.fuelId)
        assertEquals("5", lxiAbs.seatingCapacity)
        assertEquals("456841", lxiAbs.exShowroomPrice)
    }

    @Test
    fun `step 1 order puts preferred first then deduplicated catalogue`() {
        val ordered = orderBrandModels(preferredModels, catalogueModels)

        assertEquals(preferredModels.map { it.modelId }, ordered.take(6).map { it.modelId })
        assertTrue(ordered.take(6).all { it.isPopular })
        assertEquals(ordered.size, ordered.map { it.modelId }.distinct().size) // no duplicate rows
        assertEquals(catalogueModels.size, ordered.size) // preferred are all in model_list
    }

    @Test
    fun `vahan make surfaces that make first, preferred before catalogue`() {
        val ordered = orderBrandModels(preferredModels, catalogueModels, priorityMake = "MARUTI")

        val marutiCount = catalogueModels.count { it.makeName == "MARUTI" }
        assertTrue(ordered.take(marutiCount).all { it.makeName == "MARUTI" })
        assertEquals(listOf("138", "134", "125", "122", "244"), ordered.take(5).map { it.modelId })
        // HYUNDAI CRETA (preferred, other make) comes right after all MARUTI models
        assertEquals("830", ordered[marutiCount].modelId)
    }

    // ---------------------------------------------------------------- 3-step flow

    @Test
    fun `full 3-step selection binds Car Details and quote payload`() {
        var quoteRequest: CarQuoteRequest? = null
        val repo = liveRepository { quoteRequest = it }
        val vm = NewCarViewModel(repo)
        vm.initForMode(CarEntryMode.WITHOUT_CAR_NUMBER)
        idle()

        // Step 1
        vm.onAction(NewCarAction.OpenCarDetailsSheet)
        idle()
        val brands = vm.uiState.value.filteredBrandModels
        assertEquals("MARUTI, WAGON R", brands.first().displayName)
        assertEquals(1, repo.preferredCalls)

        vm.onAction(NewCarAction.OnSheetSearchQueryChange("creta"))
        assertTrue(vm.uiState.value.filteredBrandModels.any { it.modelId == "830" })
        vm.onAction(NewCarAction.OnSheetSearchQueryChange(""))

        // Step 2: selecting MARUTI, WAGON R calls beta_GetFuelVariant/138/1/MARUTI
        vm.onAction(NewCarAction.OnDraftBrandModelSelected(brands.first()))
        assertEquals(CarDetailsStep.FUEL, vm.uiState.value.sheetStep)
        assertEquals(FuelVariantState.Loading, vm.uiState.value.fuelVariantState)
        idle()
        assertEquals(listOf("138" to "MARUTI"), repo.fuelVariantCalls)
        assertEquals(
            listOf("PETROL", "CNG", "PETROL/CNG", "LPG", "EXTERNAL FITTED CNG", "EXTERNAL FITTED LPG"),
            vm.uiState.value.filteredFuelTypes
        )

        // Step 3: PETROL shows PETROL variants plus the dual-fuel PETROL/CNG variant
        vm.onAction(NewCarAction.OnDraftFuelSelected("PETROL"))
        val variants = vm.uiState.value.filteredVariants
        assertEquals(43, variants.size)
        assertTrue(variants.all { it.fuelType == "PETROL" || it.fuelType == "PETROL/CNG" })
        val lxiAbs = variants.first { it.variantId == "7834" }
        assertEquals("1.0 LXI ABS (998CC)", lxiAbs.displayLabel)

        // Back and forth between Fuel and Variant never re-fetches
        vm.onAction(NewCarAction.OnSheetBackClick)
        vm.onAction(NewCarAction.OnDraftFuelSelected("PETROL"))
        idle()
        assertEquals(1, repo.fuelVariantCalls.size)

        // Confirm variant
        vm.onAction(NewCarAction.OnDraftVariantSelected(lxiAbs))
        val state = vm.uiState.value
        assertEquals(false, state.isCarDetailsSheetOpen)
        assertEquals("MARUTI-WAGON R-1.0 LXI ABS-PETROL- (998 CC)", state.formattedCarDetails)
        assertEquals("7834", state.variantId)
        assertEquals("138", state.modelId)
        assertEquals("1", state.fuelId)
        assertEquals("998", state.cubicCapacity)
        assertEquals("5", state.seatingCapacity)
        assertEquals("456841", state.exShowroomPrice)

        // Downstream quote payload uses the new selection
        val filled = state.copy(
            customerName = "Test User",
            selectedRto = com.policyboss.policybosspro.ui.components.dropDown.DropdownOption("579", "Mumbai"),
            registrationDate = "18-Oct-2021",
            policyExpiryDate = "09-Jun-2030",
            selectedInsurer = com.policyboss.policybosspro.ui.components.dropDown.DropdownOption("17", "SBI")
        )
        assertTrue(filled.isFormValid)
    }

    @Test
    fun `quote request carries selected make, model, variant and fuel ids`() = runTest {
        val repo = liveRepository()
        val vm = NewCarViewModel(repo)
        vm.initForMode(CarEntryMode.WITH_CAR_NUMBER, "MH-01-BB-0197")
        idle()

        vm.onAction(NewCarAction.OpenCarDetailsSheet)
        idle()
        vm.onAction(NewCarAction.OnDraftBrandModelSelected(vm.uiState.value.filteredBrandModels.first { it.modelId == "138" }))
        idle()
        vm.onAction(NewCarAction.OnDraftFuelSelected("PETROL"))
        vm.onAction(NewCarAction.OnDraftVariantSelected(wagonRCatalog.variants.first { it.variantId == "7834" }))
        // sampleKiaVehicle (fake lookup) carries the policy/RTO data; vehicle comes from the sheet
        vm.onAction(NewCarAction.OnGetQuotesClick)
        idle()

        // Car-number journey prices through premium_initiate with the re-picked variant
        val input = repo.premiumInitiateInputs.single()
        assertEquals("7834", input.vehicle.variantId)
        assertEquals("PETROL", input.vehicle.fuelType)
        assertEquals("7834", vm.uiState.value.variantId)
        assertEquals("1.0 LXI ABS", vm.uiState.value.variant)
    }

    @Test
    fun `fuel variant failure shows error and retry reloads`() {
        var attempt = 0
        val repo = FakeCarInsuranceRepository(fuelVariants = { modelId, _ ->
            attempt++
            if (attempt == 1) Resource.Error("No internet connection.") else Resource.Success(sampleFuelVariantCatalog(modelId))
        })
        val vm = NewCarViewModel(repo)
        vm.initForMode(CarEntryMode.WITHOUT_CAR_NUMBER)
        idle()
        vm.onAction(NewCarAction.OpenCarDetailsSheet)
        vm.onAction(NewCarAction.OnDraftBrandModelSelected(
            com.policyboss.policybosspro.feature.carInsurance.model.CarBrandModelItem("MARUTI", "MARUTI", "138", "WAGON R")
        ))
        idle()
        assertTrue(vm.uiState.value.fuelVariantState is FuelVariantState.Error)
        assertTrue(vm.uiState.value.filteredFuelTypes.isEmpty())

        vm.onAction(NewCarAction.OnRetryFuelVariants)
        idle()
        assertEquals(FuelVariantState.Success, vm.uiState.value.fuelVariantState)
        assertEquals(5, vm.uiState.value.filteredFuelTypes.size) // PETROL, DIESEL, CNG + 2 external fitted
    }

    @Test
    fun `vahan lookup pre-selects the looked-up variant from the live list`() {
        val vahanWagonR = sampleKiaVehicle("MH01BB0197").copy(
            makeName = "MARUTI", modelName = "WAGON R", modelId = "138",
            variantName = "VXI+", variantId = "6917", fuelType = "PETROL", fuelId = "1", cubicCapacity = "998"
        )
        val repo = object : FakeCarInsuranceRepository(
            vehicleInfo = { Resource.Success(vahanWagonR) },
            models = { Resource.Success(catalogueModels) },
            preferred = { Resource.Success(preferredModels) },
            fuelVariants = { _, _ -> Resource.Success(wagonRCatalog) }
        ) {}
        val vm = NewCarViewModel(repo)
        vm.initForMode(CarEntryMode.WITH_CAR_NUMBER, "MH01BB0197")
        idle()

        // Make priority from vahan: MARUTI first
        assertEquals("MARUTI", vm.uiState.value.brandModels.first().makeName)

        vm.onAction(NewCarAction.OpenCarDetailsSheet)
        idle()
        // Step 1: opens on brand with the looked-up WAGON R highlighted
        var state = vm.uiState.value
        assertEquals(CarDetailsStep.MAKE_MODEL, state.sheetStep)
        assertEquals("138", state.highlightedBrandModel?.modelId)
        assertEquals("MARUTI, WAGON R", state.filteredBrandModels.first().displayName)

        // Step 2: PETROL highlighted
        vm.onAction(NewCarAction.OnDraftBrandModelSelected(state.highlightedBrandModel!!))
        idle()
        assertEquals("PETROL", vm.uiState.value.highlightedFuel)

        // Step 3: VXI+ highlighted in the live list
        vm.onAction(NewCarAction.OnDraftFuelSelected("PETROL"))
        state = vm.uiState.value
        assertEquals("6917", state.highlightedVariant?.variantId)
        assertTrue(state.filteredVariants.any { it.variantId == "6917" && it.displayLabel == "VXI+ (998CC)" })
        assertEquals(listOf("138" to "MARUTI"), repo.fuelVariantCalls.distinct())
    }

    @Test
    fun `external fitted CNG and LPG are added after PETROL fuels and show PETROL variants`() {
        assertEquals(
            listOf("PETROL", "CNG", "PETROL/CNG", "LPG", "EXTERNAL FITTED CNG", "EXTERNAL FITTED LPG"),
            wagonRCatalog.selectableFuels
        )
        assertEquals(wagonRCatalog.variantsFor("PETROL"), wagonRCatalog.variantsFor("EXTERNAL FITTED CNG"))
        assertEquals(wagonRCatalog.variantsFor("PETROL"), wagonRCatalog.variantsFor("external fitted lpg"))

        // No PETROL in FuelList -> nothing added
        val dieselOnly = FuelVariantCatalog("1", listOf("DIESEL"), emptyList())
        assertEquals(listOf("DIESEL"), dieselOnly.selectableFuels)
    }

    @Test
    fun `choosing a variant under EXTERNAL FITTED CNG keeps the external fuel and the variant details`() {
        val vm = NewCarViewModel(liveRepository())
        vm.initForMode(CarEntryMode.WITHOUT_CAR_NUMBER)
        idle()
        vm.onAction(NewCarAction.OpenCarDetailsSheet)
        idle()
        vm.onAction(NewCarAction.OnDraftBrandModelSelected(vm.uiState.value.filteredBrandModels.first { it.modelId == "138" }))
        idle()
        vm.onAction(NewCarAction.OnDraftFuelSelected("EXTERNAL FITTED CNG"))
        val lxi = vm.uiState.value.filteredVariants.first { it.variantId == "7834" }
        vm.onAction(NewCarAction.OnDraftVariantSelected(lxi))

        val state = vm.uiState.value
        assertEquals("MARUTI-WAGON R-1.0 LXI ABS-EXTERNAL FITTED CNG- (998 CC)", state.formattedCarDetails)
        assertEquals("EXTERNAL FITTED CNG", state.fuel)
        assertEquals("7834", state.variantId)
        assertEquals("138", state.modelId)
        assertEquals("1", state.fuelId)
        assertEquals("998", state.cubicCapacity)
    }

    @Test
    fun `dual-fuel PETROL-CNG variant is listed under PETROL, CNG and PETROL-CNG`() {
        val dualFuel = wagonRCatalog.variants.filter { it.fuelType == "PETROL/CNG" }
        assertEquals(1, dualFuel.size)

        val petrol = wagonRCatalog.variantsFor("PETROL")
        val cng = wagonRCatalog.variantsFor("cng")
        val petrolCng = wagonRCatalog.variantsFor("PETROL/CNG")
        val lpg = wagonRCatalog.variantsFor("LPG")

        assertEquals(42 + 1, petrol.size)
        assertEquals(5 + 1, cng.size)
        assertEquals(dualFuel, petrolCng)
        assertTrue(petrol.containsAll(dualFuel) && cng.containsAll(dualFuel))
        assertTrue(lpg.none { it.fuelType == "PETROL/CNG" })
        assertEquals(4, lpg.size)
    }

    // ---------------------------------------------------------------- Repository caching

    @Test
    fun `repository caches fuel variants per model and preferred per ss_id`() = runTest {
        var fuelCalls = 0
        var preferredCalls = 0
        val api = object : ThrowingCarInsuranceApi() {
            override suspend fun getFuelVariants(modelId: String, productId: Int, makeName: String): Response<FuelVariantResponseDto> {
                fuelCalls++
                assertEquals(1, productId)
                return Response.success(wagonRResponse)
            }

            override suspend fun getPreferredMakeModels(ssId: Int, productId: Int): Response<List<PreferredMakeModelDto>> {
                preferredCalls++
                assertEquals(150253, ssId)
                return Response.success(
                    testJson.decodeFromString(
                        ListSerializer(PreferredMakeModelDto.serializer()),
                        resource("vehicle/preferred_make_model_ss150253.json")
                    )
                )
            }
        }
        val appDataManager = testAppDataManager().apply { saveUserSsId("150253") }
        val repo = CarInsuranceRepository(api, appDataManager, testJson, VehicleInfoSessionProvider())

        repeat(3) { repo.getFuelVariants("138", "MARUTI") }
        repeat(3) { repo.getPreferredMakeModels() }

        assertEquals(1, fuelCalls)
        assertEquals(1, preferredCalls)
        val catalog = (repo.getFuelVariants("138", "MARUTI") as Resource.Success).data
        assertEquals(listOf("PETROL", "CNG", "PETROL/CNG", "LPG"), catalog.fuels)
    }
}
