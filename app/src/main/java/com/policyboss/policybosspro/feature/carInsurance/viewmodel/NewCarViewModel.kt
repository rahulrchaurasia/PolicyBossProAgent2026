package com.policyboss.policybosspro.feature.carInsurance.viewmodel

import kotlinx.coroutines.CompletableDeferred
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumQuoteList
import com.policyboss.policybosspro.feature.carInsurance.model.QuotePopup
import com.policyboss.policybosspro.feature.carInsurance.data.repository.PremiumQuoteBundle
import com.policyboss.policybosspro.feature.carInsurance.data.repository.PremiumQuoteBundleEvent
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumPollStatus
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.AppConfig
import com.policyboss.policybosspro.core.Resource
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteRequest
import com.policyboss.policybosspro.feature.carInsurance.data.repository.CarInsuranceRepository
import com.policyboss.policybosspro.feature.carInsurance.model.CarBrandModelItem
import com.policyboss.policybosspro.feature.carInsurance.model.CarDetailsStep
import com.policyboss.policybosspro.feature.carInsurance.model.CarEntryMode
import com.policyboss.policybosspro.feature.carInsurance.model.CarInsuranceData
import com.policyboss.policybosspro.feature.carInsurance.model.CarVariantItem
import com.policyboss.policybosspro.feature.carInsurance.model.CpaCoverageOption
import com.policyboss.policybosspro.feature.carInsurance.model.ModelListState
import com.policyboss.policybosspro.feature.carInsurance.model.VehicleLookupState
import com.policyboss.policybosspro.feature.carInsurance.model.VehicleModel
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.buildCarDetailsSummary
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.orderBrandModels
import com.policyboss.policybosspro.feature.carInsurance.model.FuelVariantState
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.toNewCarUiState
import com.policyboss.policybosspro.feature.carInsurance.model.DatePickerType
import com.policyboss.policybosspro.feature.carInsurance.model.NavigationDirection
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarAction
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiEvent
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiState
import com.policyboss.policybosspro.feature.carInsurance.model.OwnershipType
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumInitiateResult
import com.policyboss.policybosspro.feature.carInsurance.model.VehicleInfoDomain
import com.policyboss.policybosspro.feature.carInsurance.model.toPremiumInitiateInput
import com.policyboss.policybosspro.feature.carInsurance.model.QuoteFetchState
import com.policyboss.policybosspro.feature.carInsurance.model.QuoteLoadingUiEvent
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.MotorQuoteFilterData
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.QuoteFilterState
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.ZeroDepClaimLimit
import com.policyboss.policybosspro.ui.components.dropDown.DropdownOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewCarViewModel @Inject constructor(
    private val repository: CarInsuranceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewCarUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<NewCarUiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _quoteLoadingEvent = Channel<QuoteLoadingUiEvent>(Channel.BUFFERED)
    val quoteLoadingEvent = _quoteLoadingEvent.receiveAsFlow()

    private var initializedMode: CarEntryMode? = null
    private var initializedCarNumber: String? = null
    private var quoteJob: Job? = null
    /** premium_list_db polling; keeps running on the results screen after the loader closes. */
    private var premiumPollJob: Job? = null

    /** Last successful vahan lookup; drives `premium_initiate`. Cleared whenever the form resets. */
    private var vehicleInfo: VehicleInfoDomain? = null
    private var vehicleLookupJob: Job? = null
    private var modelListJob: Job? = null
    private var fuelVariantJob: Job? = null

    /** Raw catalogues kept so the sheet order can be rebuilt for the current Make. */
    private var catalogueModels: List<VehicleModel> = emptyList()
    private var preferredModels: List<VehicleModel> = emptyList()

    fun initForMode(
        entryMode: CarEntryMode,
        carNumber: String? = null
    ) {
        if (initializedMode == entryMode && initializedCarNumber == carNumber) return
        initializedMode = entryMode
        initializedCarNumber = carNumber

        if (entryMode == CarEntryMode.WITH_CAR_NUMBER && !carNumber.isNullOrBlank()) {
            applyEntryMode(CarEntryMode.WITH_CAR_NUMBER, carNumber)
            startVehicleLookup(carNumber)
        } else {
            val manualMode = if (entryMode == CarEntryMode.WITH_CAR_NUMBER) CarEntryMode.WITHOUT_CAR_NUMBER else entryMode
            applyEntryMode(manualMode, carNumber = null)
            loadModelList()
            loadInsurers()
        }
    }

    fun init(
        entryMode: CarEntryMode = CarEntryMode.WITHOUT_CAR_NUMBER,
        carNumber: String? = null
    ) {
        initForMode(entryMode = entryMode, carNumber = carNumber)
    }

    /**
     * Resets the form for [entryMode]. WITH_CAR_NUMBER starts blank and is filled by the
     * vahan lookup; nothing is pre-filled with placeholder vehicle data.
     */
    private fun applyEntryMode(entryMode: CarEntryMode, carNumber: String?) {
        cancelVehicleLookupJob()
        vehicleInfo = null
        _uiState.update { current ->
            val keepCustomerName = current.entryMode != CarEntryMode.WITH_CAR_NUMBER
            val base = current.copy(
                entryMode = entryMode,
                registrationNumber = carNumber.orEmpty(),
                vahanData = "",
                vahanDetails = "",
                showVahanBanner = false,
                carDetailsSummary = "",
                make = "",
                makeId = "",
                model = "",
                modelId = "",
                fuel = "",
                fuelId = "",
                variant = "",
                variantId = "",
                cubicCapacity = "",
                seatingCapacity = "",
                exShowroomPrice = "",
                isCarDetailsSheetOpen = false,
                sheetStep = CarDetailsStep.MAKE_MODEL,
                draftBrandModel = null,
                draftFuel = null,
                draftVariant = null,
                sheetSearchQuery = "",
                policyType = com.policyboss.policybosspro.feature.carInsurance.model.PolicyType.COMPREHENSIVE,
                customerName = if (keepCustomerName) current.customerName else "",
                customerNameError = null,
                selectedRto = null,
                rtoOptions = CarInsuranceData.rtoOptions,
                ownershipType = OwnershipType.INDIVIDUAL,
                cpaCoverageOption = CpaCoverageOption.ZEXTRA_SWITCH,
                isZextraBundleEnabled = true,
                cpaNoticeText = "Special Zextra Bundle (CPA + RSA) included",
                draftQuoteFilter = QuoteFilterState(),
                appliedQuoteFilter = QuoteFilterState(),
                selectedModel = null,
                selectedVariant = null,
                selectedFuel = null,
                selectedInsurer = null,
                selectedTpInsurer = null,
                policyExpiryDate = "",
                hasClaim = false,
                selectedNcb = DropdownOption("0", "0%"),
                consentGiven = true,
                vehicleLookupState = VehicleLookupState.Idle
            )
            when (entryMode) {
                CarEntryMode.WITH_CAR_NUMBER -> base.copy(
                    headerTitle = "Continue with Car Number",
                    policyTypeOptions = CarInsuranceData.existingCarPolicyTypes,
                    selectedPolicyType = "Comprehensive",
                    registrationDate = "",
                    manufacturingMonth = "",
                    manufacturingYear = "",
                    manufacturingDate = "",
                    hasExistingPolicy = false
                )
                CarEntryMode.WITHOUT_CAR_NUMBER -> base.copy(
                    headerTitle = "Continue without car number",
                    registrationNumber = "",
                    policyTypeOptions = CarInsuranceData.existingCarPolicyTypes,
                    selectedPolicyType = "Comprehensive",
                    registrationDate = "",
                    manufacturingMonth = "Jan",
                    manufacturingYear = "2021",
                    manufacturingDate = "",
                    hasExistingPolicy = true
                )
                CarEntryMode.NEW_CAR -> {
                    val today = java.time.LocalDate.now()
                    val regFormatted = today.format(java.time.format.DateTimeFormatter.ofPattern("dd-MMM-yyyy", java.util.Locale.ENGLISH))
                    val mfgMonth = today.format(java.time.format.DateTimeFormatter.ofPattern("MMM", java.util.Locale.ENGLISH))
                    val mfgYear = today.year.toString()
                    base.copy(
                        headerTitle = "New Car",
                        registrationNumber = "",
                        policyTypeOptions = listOf("TP (3 Yr)", "OD (1 Yr) + TP (3 Yr)"),
                        selectedPolicyType = "OD (1 Yr) + TP (3 Yr)",
                        registrationDate = regFormatted,
                        manufacturingMonth = mfgMonth,
                        manufacturingYear = mfgYear,
                        manufacturingDate = "01/$mfgMonth/$mfgYear",
                        hasExistingPolicy = false
                    )
                }
            }
        }
    }

    // =====================================================================
    // Registration lookup (vehicle_info_user + model_list) & live catalogue
    // =====================================================================

    /**
     * Runs the vahan lookup and the model-list fetch concurrently while the quote loader is
     * shown. Success fills the form; failure keeps the loader in its error state so the user
     * can retry or continue manually — invalid data is never shown as a success.
     */
    private fun startVehicleLookup(carNumber: String) {
        cancelVehicleLookupJob()
        _uiState.update {
            it.copy(
                vehicleLookupState = VehicleLookupState.Loading,
                modelListState = if (it.modelListState is ModelListState.Success) it.modelListState else ModelListState.Loading
            )
        }

        vehicleLookupJob = viewModelScope.launch {
            val (infoResult, catalogueResult, insurersResult) = coroutineScope {
                val info = async { repository.getVehicleInfo(carNumber) }
                val catalogue = async { fetchMakeModelCatalogue() }
                val insurers = async { repository.getPreviousInsurers() }
                Triple(info.await(), catalogue.await(), insurers.await())
            }

            // Insurer master list is optional for the lookup: a failure never fails it
            applyInsurersResult(insurersResult)

            val vahanMake = (infoResult as? Resource.Success)?.data?.makeName
            applyMakeModelCatalogue(catalogueResult, priorityMake = vahanMake)

            when (infoResult) {
                is Resource.Success -> {
                    val vehicle = infoResult.data
                    vehicleInfo = vehicle
                    _uiState.update { current ->
                        vehicle.toNewCarUiState(current).copy(vehicleLookupState = VehicleLookupState.Idle)
                    }
                    // Warm the fuel/variant cache so the sheet opens on the right variant
                    loadFuelVariants(vehicle.modelId, vehicle.makeName)
                    if (!vehicle.hasAllLockedFields) {
                        sendFormEvent(
                            NewCarUiEvent.ShowSnackbar(
                                "Some vehicle details weren't found. Please review and complete the form."
                            )
                        )
                    }
                }

                is Resource.Error -> {
                    _uiState.update { it.copy(vehicleLookupState = VehicleLookupState.Error(infoResult.message)) }
                }
            }
        }
    }

    private fun retryVehicleLookup() {
        val carNumber = _uiState.value.registrationNumber.ifBlank { initializedCarNumber.orEmpty() }
        if (carNumber.isBlank()) {
            continueWithoutVehicleInfo()
            return
        }
        applyEntryMode(CarEntryMode.WITH_CAR_NUMBER, carNumber)
        startVehicleLookup(carNumber)
    }

    /** Vehicle not found / lookup failed: fall back to the manual WITHOUT_CAR_NUMBER form. */
    private fun continueWithoutVehicleInfo() {
        applyEntryMode(CarEntryMode.WITHOUT_CAR_NUMBER, carNumber = null)
        loadModelList()
        loadInsurers()
    }

    private fun cancelVehicleLookup() {
        cancelVehicleLookupJob()
        quoteJob?.cancel()
        quoteJob = null
        cancelPremiumPolling()
        initializedMode = null
        initializedCarNumber = null
        _uiState.update {
            it.copy(
                vehicleLookupState = VehicleLookupState.Idle,
                quoteState = QuoteFetchState.Idle,
                errorMessage = null
            )
        }
        sendFormEvent(NewCarUiEvent.NavigateBack)
    }

    private fun cancelVehicleLookupJob() {
        vehicleLookupJob?.cancel()
        vehicleLookupJob = null
    }

    /**
     * Loads step 1 of the sheet: preferred Make/Models (get_preferred) + the full model_list,
     * concurrently. Both are cached in the repository, so reopening the sheet is free; a
     * failed load can be retried.
     */
    private fun loadModelList() {
        if (modelListJob?.isActive == true || vehicleLookupJob?.isActive == true) return
        val state = _uiState.value
        if (state.modelListState is ModelListState.Success && state.brandModels.isNotEmpty()) {
            // Re-order for the current Make (e.g. after a vahan lookup or a new selection)
            publishBrandModels(priorityMake = state.make)
            return
        }

        _uiState.update { it.copy(modelListState = ModelListState.Loading) }
        modelListJob = viewModelScope.launch {
            applyMakeModelCatalogue(fetchMakeModelCatalogue(), priorityMake = _uiState.value.make)
        }
    }

    private suspend fun fetchMakeModelCatalogue(): Pair<Resource<List<VehicleModel>>, Resource<List<VehicleModel>>> =
        coroutineScope {
            val models = async { repository.getModelList() }
            val preferred = async { repository.getPreferredMakeModels() }
            models.await() to preferred.await()
        }

    /** model_list is required; get_preferred is optional (its failure just hides the top list). */
    private fun applyMakeModelCatalogue(
        result: Pair<Resource<List<VehicleModel>>, Resource<List<VehicleModel>>>,
        priorityMake: String?
    ) {
        val (modelsResult, preferredResult) = result
        if (modelsResult is Resource.Success) catalogueModels = modelsResult.data
        if (preferredResult is Resource.Success) preferredModels = preferredResult.data

        if (catalogueModels.isEmpty() && preferredModels.isEmpty() && modelsResult is Resource.Error) {
            _uiState.update { it.copy(modelListState = ModelListState.Error(modelsResult.message)) }
            return
        }
        publishBrandModels(priorityMake)
    }

    private fun publishBrandModels(priorityMake: String?) {
        val ordered = orderBrandModels(preferredModels, catalogueModels, priorityMake)
        _uiState.update { it.copy(brandModels = ordered, modelListState = ModelListState.Success) }
    }

    /**
     * Step 2/3 data for one model (beta_GetFuelVariant). Skips the call when that model's
     * catalogue is already in state; the repository caches it across sheet openings.
     */
    private fun loadFuelVariants(modelId: String, makeName: String, force: Boolean = false) {
        if (modelId.isBlank() || makeName.isBlank()) return
        val state = _uiState.value
        val loadedForModel = state.fuelVariantCatalog?.modelId?.equals(modelId, ignoreCase = true) == true
        if (!force && loadedForModel && state.fuelVariantState is FuelVariantState.Success) return
        if (!force && fuelVariantJob?.isActive == true && state.fuelVariantState is FuelVariantState.Loading &&
            loadingFuelVariantModelId == modelId
        ) return

        fuelVariantJob?.cancel()
        loadingFuelVariantModelId = modelId
        _uiState.update { it.copy(fuelVariantState = FuelVariantState.Loading) }
        fuelVariantJob = viewModelScope.launch {
            when (val result = repository.getFuelVariants(modelId, makeName)) {
                is Resource.Success -> _uiState.update {
                    it.copy(fuelVariantCatalog = result.data, fuelVariantState = FuelVariantState.Success)
                }
                is Resource.Error -> _uiState.update {
                    it.copy(fuelVariantState = FuelVariantState.Error(result.message))
                }
            }
        }
    }

    private var loadingFuelVariantModelId: String? = null

    private var insurerJob: Job? = null

    /**
     * Previous/TP insurer master list (insurers/getPrevInsurer/1). Skipped when already
     * loaded or in flight; the repository caches it, so retries are cheap.
     */
    private fun loadInsurers() {
        if (insurerJob?.isActive == true || _uiState.value.insurerOptions.isNotEmpty()) return
        insurerJob = viewModelScope.launch {
            applyInsurersResult(repository.getPreviousInsurers())
        }
    }

    private fun applyInsurersResult(result: Resource<List<DropdownOption<String>>>) {
        if (result is Resource.Success && result.data.isNotEmpty()) {
            _uiState.update { it.copy(insurerOptions = result.data, tpInsurerOptions = result.data) }
        }
    }

    fun onAction(action: NewCarAction) {
        when (action) {
            is NewCarAction.UpdateEntryMode -> {
                initForMode(entryMode = action.mode)
            }

            is NewCarAction.OnRetryVehicleLookup -> retryVehicleLookup()

            is NewCarAction.OnCancelVehicleLookup -> cancelVehicleLookup()

            is NewCarAction.OnContinueWithoutVehicleInfo -> continueWithoutVehicleInfo()

            is NewCarAction.OnRetryModelList -> loadModelList()

            is NewCarAction.OnInsurerSheetOpened -> loadInsurers()

            // Results-screen variant sheet: always the committed model, never a form-sheet draft
            is NewCarAction.OnRetryQuoteVariants -> {
                val state = _uiState.value
                loadFuelVariants(modelId = state.modelId, makeName = state.make, force = true)
            }

            is NewCarAction.OnRetryFuelVariants -> {
                val state = _uiState.value
                val brandModel = state.draftBrandModel
                loadFuelVariants(
                    modelId = brandModel?.modelId ?: state.modelId,
                    makeName = brandModel?.makeName ?: state.make,
                    force = true
                )
            }

            is NewCarAction.UpdateManufacturingMonth -> {
                if (_uiState.value.isContinueWithCarNumber) return
                _uiState.update {
                    it.copy(
                        manufacturingMonth = action.month,
                        manufacturingDate = "01/${action.month}/${it.manufacturingYear}"
                    )
                }
            }

            is NewCarAction.UpdateManufacturingYear -> {
                if (_uiState.value.isContinueWithCarNumber) return
                _uiState.update {
                    it.copy(
                        manufacturingYear = action.year,
                        manufacturingDate = "01/${it.manufacturingMonth}/${action.year}"
                    )
                }
            }

            is NewCarAction.UpdateModel -> {
                val matched = CarInsuranceData.carModels.firstOrNull {
                    it.label.equals(action.model, ignoreCase = true) || it.value.equals(action.model, ignoreCase = true)
                } ?: DropdownOption(action.model, action.model)
                val variants = CarInsuranceData.variantsByModel[matched.value] ?: CarInsuranceData.defaultVariants
                _uiState.update {
                    val summary = "${matched.label} - ${variants.firstOrNull()?.label ?: ""} - ${it.selectedFuel?.label ?: ""}"
                    it.copy(
                        selectedModel = matched,
                        variantOptions = variants,
                        selectedVariant = variants.firstOrNull(),
                        carDetailsSummary = summary
                    )
                }
            }

            is NewCarAction.UpdateVariant -> {
                val matched = _uiState.value.variantOptions.firstOrNull {
                    it.label.equals(action.variant, ignoreCase = true) || it.value.equals(action.variant, ignoreCase = true)
                } ?: DropdownOption(action.variant, action.variant)
                _uiState.update {
                    val summary = "${it.selectedModel?.label ?: ""} - ${matched.label} - ${it.selectedFuel?.label ?: ""}"
                    it.copy(selectedVariant = matched, carDetailsSummary = summary)
                }
            }

            is NewCarAction.UpdateFuel -> {
                val matched = CarInsuranceData.fuelOptions.firstOrNull {
                    it.label.equals(action.fuel, ignoreCase = true) || it.value.equals(action.fuel, ignoreCase = true)
                } ?: DropdownOption(action.fuel, action.fuel)
                _uiState.update {
                    val summary = "${it.selectedModel?.label ?: ""} - ${it.selectedVariant?.label ?: ""} - ${matched.label}"
                    it.copy(selectedFuel = matched, carDetailsSummary = summary)
                }
            }

            is NewCarAction.OnCustomerNameChange -> {
                if (_uiState.value.isContinueWithCarNumber) return
                _uiState.update {
                    it.copy(
                        customerName = action.name,
                        customerNameError = if (action.name.isBlank()) "Customer name is required" else null
                    )
                }
            }

            is NewCarAction.OnRtoSelected -> {
                if (_uiState.value.isContinueWithCarNumber) return
                _uiState.update { it.copy(selectedRto = action.option) }
            }

            is NewCarAction.OnOwnershipSelected -> {
                val defaultCpa = if (action.type == OwnershipType.COMPANY) {
                    CpaCoverageOption.NO_CPA
                } else {
                    CpaCoverageOption.ZEXTRA_SWITCH
                }
                val zextraEnabled = (defaultCpa == CpaCoverageOption.ZEXTRA_SWITCH)
                val notice = when (defaultCpa) {
                    CpaCoverageOption.ZEXTRA_SWITCH -> "Special Zextra Bundle (CPA + RSA) included"
                    CpaCoverageOption.INSURER_CPA -> "Insurer CPA included"
                    CpaCoverageOption.NO_CPA -> "CPA: Don't have Personal Accident cover"
                }
                _uiState.update { current ->
                    val sanitizedDraft = if (action.type == OwnershipType.COMPANY) {
                        current.draftQuoteFilter.copy(
                            selectedLiabilityCoverIds = current.draftQuoteFilter.selectedLiabilityCoverIds - "owner_driver_pa"
                        )
                    } else {
                        current.draftQuoteFilter
                    }
                    val sanitizedApplied = if (action.type == OwnershipType.COMPANY) {
                        current.appliedQuoteFilter.copy(
                            selectedLiabilityCoverIds = current.appliedQuoteFilter.selectedLiabilityCoverIds - "owner_driver_pa"
                        )
                    } else {
                        current.appliedQuoteFilter
                    }
                    current.copy(
                        ownershipType = action.type,
                        cpaCoverageOption = defaultCpa,
                        isZextraBundleEnabled = zextraEnabled,
                        cpaNoticeText = notice,
                        draftQuoteFilter = sanitizedDraft,
                        appliedQuoteFilter = sanitizedApplied
                    )
                }
            }

            is NewCarAction.OnPolicyTypeSelected -> {
                val resolved = com.policyboss.policybosspro.feature.carInsurance.model.PolicyType.fromString(action.policyType)
                _uiState.update {
                    it.copy(
                        policyType = resolved,
                        selectedPolicyType = action.policyType
                    )
                }
            }

            is NewCarAction.SelectPolicyType -> {
                _uiState.update {
                    it.copy(
                        policyType = action.policyType,
                        selectedPolicyType = action.policyType.displayName
                    )
                }
            }

            is NewCarAction.OnExistingPolicyToggle -> {
                _uiState.update { it.copy(hasExistingPolicy = action.hasExisting) }
            }

            is NewCarAction.OnInsurerSelected -> {
                _uiState.update { it.copy(selectedInsurer = action.option) }
            }

            is NewCarAction.OnTpInsurerSelected -> {
                _uiState.update { it.copy(selectedTpInsurer = action.option) }
            }

            // Unified Car Details Selection Actions
            is NewCarAction.OpenCarDetailsSheet -> {
                loadModelList()
                val state = _uiState.value
                // Warm step 2/3 data for the current car; the sheet still starts at step 1
                if (state.model.isNotBlank()) loadFuelVariants(state.modelId, state.make)
                _uiState.update {
                    it.copy(
                        isCarDetailsSheetOpen = true,
                        sheetStep = CarDetailsStep.MAKE_MODEL,
                        sheetNavigationDirection = NavigationDirection.FORWARD,
                        draftBrandModel = null,
                        draftFuel = null,
                        draftVariant = null,
                        sheetSearchQuery = ""
                    )
                }
            }

            is NewCarAction.DismissCarDetailsSheet -> {
                _uiState.update {
                    it.copy(
                        isCarDetailsSheetOpen = false,
                        draftBrandModel = null,
                        draftFuel = null,
                        draftVariant = null,
                        sheetSearchQuery = ""
                    )
                }
            }

            is NewCarAction.OnSheetStepClick -> {
                val state = _uiState.value
                if (action.step == state.sheetStep) return
                if (action.step != CarDetailsStep.MAKE_MODEL) {
                    val brandModel = state.draftBrandModel
                    loadFuelVariants(brandModel?.modelId ?: state.modelId, brandModel?.makeName ?: state.make)
                }
                val canNavigate = when (action.step) {
                    CarDetailsStep.MAKE_MODEL -> true
                    // Strict Brand -> Fuel -> Variant: only choices made in this sheet session unlock steps
                    CarDetailsStep.FUEL -> state.draftBrandModel != null
                    CarDetailsStep.VARIANT -> state.draftBrandModel != null && !state.draftFuel.isNullOrBlank()
                }
                if (canNavigate) {
                    val direction = if (action.step.ordinal > state.sheetStep.ordinal) {
                        NavigationDirection.FORWARD
                    } else {
                        NavigationDirection.BACKWARD
                    }
                    _uiState.update {
                        it.copy(
                            sheetStep = action.step,
                            sheetNavigationDirection = direction,
                            sheetSearchQuery = ""
                        )
                    }
                }
            }

            is NewCarAction.OnSheetBackClick -> {
                val state = _uiState.value
                when (state.sheetStep) {
                    CarDetailsStep.VARIANT -> {
                        _uiState.update {
                            it.copy(
                                sheetStep = CarDetailsStep.FUEL,
                                sheetNavigationDirection = NavigationDirection.BACKWARD,
                                sheetSearchQuery = ""
                            )
                        }
                    }
                    CarDetailsStep.FUEL -> {
                        _uiState.update {
                            it.copy(
                                sheetStep = CarDetailsStep.MAKE_MODEL,
                                sheetNavigationDirection = NavigationDirection.BACKWARD,
                                sheetSearchQuery = ""
                            )
                        }
                    }
                    CarDetailsStep.MAKE_MODEL -> {
                        _uiState.update {
                            it.copy(
                                isCarDetailsSheetOpen = false,
                                draftBrandModel = null,
                                draftFuel = null,
                                draftVariant = null,
                                sheetSearchQuery = ""
                            )
                        }
                    }
                }
            }

            is NewCarAction.OnDraftBrandModelSelected -> {
                loadFuelVariants(action.item.modelId, action.item.makeName)
                _uiState.update {
                    it.copy(
                        draftBrandModel = action.item,
                        draftFuel = null,
                        draftVariant = null,
                        sheetStep = CarDetailsStep.FUEL,
                        sheetNavigationDirection = NavigationDirection.FORWARD,
                        sheetSearchQuery = ""
                    )
                }
            }

            is NewCarAction.OnDraftFuelSelected -> {
                _uiState.update {
                    it.copy(
                        draftFuel = action.fuel,
                        draftVariant = null,
                        sheetStep = CarDetailsStep.VARIANT,
                        sheetNavigationDirection = NavigationDirection.FORWARD,
                        sheetSearchQuery = ""
                    )
                }
            }

            is NewCarAction.OnDraftVariantSelected -> {
                val state = _uiState.value
                val brandModel = state.draftBrandModel ?: state.brandModels.firstOrNull {
                    it.makeId.equals(state.makeId, ignoreCase = true) && it.modelId.equals(state.modelId, ignoreCase = true)
                } ?: CarBrandModelItem(
                    makeId = state.makeId.ifBlank { state.make },
                    makeName = state.make,
                    modelId = state.modelId.ifBlank { state.model },
                    modelName = state.model
                )
                val chosenFuel = state.draftFuel ?: action.item.fuelType.ifBlank { state.fuel.ifBlank { "PETROL" } }
                val makeName = brandModel.makeName
                val makeId = brandModel.makeId
                val modelName = brandModel.modelName
                val modelId = action.item.modelId.ifBlank { brandModel.modelId }
                val variantName = action.item.variantName
                val variantId = action.item.variantId          // Vehicle_ID
                val fuelId = action.item.fuelId.ifBlank { chosenFuel }
                val cubicCapacity = action.item.cubicCapacity
                val summary = buildCarDetailsSummary(makeName, modelName, variantName, chosenFuel, cubicCapacity)

                _uiState.update {
                    it.copy(
                        make = makeName,
                        makeId = makeId,
                        model = modelName,
                        modelId = modelId,
                        fuel = chosenFuel,
                        fuelId = fuelId,
                        variant = variantName,
                        variantId = variantId,
                        cubicCapacity = cubicCapacity,
                        seatingCapacity = action.item.seatingCapacity,
                        exShowroomPrice = action.item.exShowroomPrice,
                        selectedModel = DropdownOption(modelId, modelName),
                        selectedVariant = DropdownOption(variantId, action.item.displayLabel),
                        selectedFuel = DropdownOption(fuelId, chosenFuel),
                        carDetailsSummary = summary,
                        isCarDetailsSheetOpen = false,
                        sheetSearchQuery = "",
                        draftBrandModel = null,
                        draftFuel = null,
                        draftVariant = null
                    )
                }
            }

            is NewCarAction.OnSheetSearchQueryChange -> {
                _uiState.update { it.copy(sheetSearchQuery = action.query) }
            }

            is NewCarAction.OnModelSelected -> {
                val availableVariants = CarInsuranceData.variantsByModel[action.option.value]
                    ?: CarInsuranceData.defaultVariants
                _uiState.update {
                    val summary = "${action.option.label} - ${availableVariants.firstOrNull()?.label ?: ""} - ${it.selectedFuel?.label ?: ""}"
                    it.copy(
                        selectedModel = action.option,
                        variantOptions = availableVariants,
                        selectedVariant = availableVariants.firstOrNull(),
                        carDetailsSummary = summary
                    )
                }
            }

            is NewCarAction.OnVariantSelected -> {
                onQuoteVariantSelected(action.option)
            }

            is NewCarAction.OnFuelSelected -> {
                _uiState.update {
                    val summary = "${it.selectedModel?.label ?: ""} - ${it.selectedVariant?.label ?: ""} - ${action.option.label}"
                    it.copy(selectedFuel = action.option, carDetailsSummary = summary)
                }
            }

            is NewCarAction.OnOpenDatePicker -> {
                if (action.type == DatePickerType.REGISTRATION && _uiState.value.isContinueWithCarNumber) return
                _uiState.update { it.copy(activeDatePicker = action.type) }
            }

            is NewCarAction.OnDateSelected -> {
                val activeType = _uiState.value.activeDatePicker
                _uiState.update { state ->
                    when (activeType) {
                        DatePickerType.REGISTRATION -> {
                            if (state.isContinueWithCarNumber) {
                                state.copy(activeDatePicker = null)
                            } else {
                                state.copy(
                                    registrationDate = action.dateString,
                                    activeDatePicker = null
                                )
                            }
                        }
                        DatePickerType.MANUFACTURING -> state.copy(
                            manufacturingDate = action.dateString,
                            activeDatePicker = null
                        )
                        DatePickerType.POLICY_EXPIRY -> state.copy(
                            policyExpiryDate = action.dateString,
                            activeDatePicker = null
                        )
                        null -> state.copy(activeDatePicker = null)
                    }
                }
            }

            is NewCarAction.OnDismissDatePicker -> {
                _uiState.update { it.copy(activeDatePicker = null) }
            }

            is NewCarAction.OnClaimToggle -> {
                _uiState.update {
                    if (action.hasClaim) {
                        it.copy(hasClaim = true, selectedNcb = DropdownOption("0", "0%"))
                    } else {
                        it.copy(hasClaim = false)
                    }
                }
            }

            is NewCarAction.OnNcbSelected -> {
                _uiState.update { it.copy(selectedNcb = action.option) }
            }

            is NewCarAction.OnConsentChange -> {
                _uiState.update { it.copy(consentGiven = action.isConsentGiven) }
            }

            is NewCarAction.OnNoticeForConsentClick -> {
                sendFormEvent(NewCarUiEvent.OpenUrl("https://www.policyboss.com/consent"))
            }

            is NewCarAction.OnBackClick -> {
                sendFormEvent(NewCarUiEvent.NavigateBack)
            }

            is NewCarAction.OnGetQuotesClick -> {
                handleGetQuotes(isRetry = false)
            }

            is NewCarAction.OnRetryGetQuotes -> {
                handleGetQuotes(isRetry = true)
            }

            is NewCarAction.OnCancelQuoteLoading -> {
                cancelQuoteLoading()
            }

            is NewCarAction.OnConfirmQuotePopup -> onConfirmQuotePopup()

            is NewCarAction.OnDismissQuotePopup -> dismissQuotePopup()

            is NewCarAction.OnLeaveQuoteResults -> {
                dismissQuotePopup()
                // An in-place re-quote may still be waiting on premium_initiate
                if (isRefreshingQuotesInPlace) {
                    quoteJob?.cancel()
                    quoteJob = null
                    isRefreshingQuotesInPlace = false
                    // Never leave Loading behind: it would block the next Get Quotes
                    _uiState.update {
                        if (it.quoteState is QuoteFetchState.Loading) it.copy(quoteState = QuoteFetchState.Idle) else it
                    }
                }
                cancelPremiumPolling()
            }

            is NewCarAction.OnQuoteNavigationHandled -> {
                _uiState.update {
                    if (it.quoteState is QuoteFetchState.Success) {
                        it.copy(quoteState = QuoteFetchState.Idle)
                    } else {
                        it
                    }
                }
            }

            // ==========================================
            // Motor Quote Results Action Handling
            // ==========================================
            is NewCarAction.ToggleZextraBundle -> {
                _uiState.update { it.copy(isZextraBundleEnabled = !it.isZextraBundleEnabled) }
            }

            is NewCarAction.ToggleAddOnsExpanded -> {
                _uiState.update { current ->
                    val updated = current.expandedAddOnQuoteIds.toMutableSet()
                    if (updated.contains(action.quoteId)) {
                        updated.remove(action.quoteId)
                    } else {
                        updated.add(action.quoteId)
                    }
                    current.copy(expandedAddOnQuoteIds = updated)
                }
            }

            is NewCarAction.ToggleAddOnSelected -> {
                _uiState.update { current ->
                    val updatedQuotes = current.quotes.map { quote ->
                        if (quote.id == action.quoteId) {
                            val updatedAddOns = quote.addOns.map { addOn ->
                                if (addOn.id == action.addOnId) {
                                    addOn.copy(isSelected = !addOn.isSelected)
                                } else {
                                    addOn
                                }
                            }
                            quote.copy(addOns = updatedAddOns)
                        } else {
                            quote
                        }
                    }
                    current.copy(quotes = updatedQuotes)
                }
            }

            is NewCarAction.ToggleCompareQuote -> {
                _uiState.update { current ->
                    val updated = current.selectedQuotesForComparison.toMutableSet()
                    if (updated.contains(action.quoteId)) {
                        updated.remove(action.quoteId)
                    } else {
                        if (updated.size >= 3) {
                            sendFormEvent(NewCarUiEvent.ShowSnackbar("You can compare up to 3 quotes"))
                            return@update current
                        }
                        updated.add(action.quoteId)
                    }
                    current.copy(selectedQuotesForComparison = updated)
                }
            }

            is NewCarAction.OnSortOptionSelected -> {
                _uiState.update { it.copy(selectedSortOption = action.option, showSortSheet = false) }
            }

            is NewCarAction.OnShowSortSheet -> {
                _uiState.update { it.copy(showSortSheet = action.show) }
            }

            is NewCarAction.OnShowFilterSheet -> {
                _uiState.update { it.copy(showFilterSheet = action.show) }
            }

            is NewCarAction.OnShowFeedbackDialog -> {
                _uiState.update { it.copy(showFeedbackDialog = action.show) }
            }

            is NewCarAction.OnFeedbackRatingChange -> {
                _uiState.update { it.copy(feedbackRating = action.rating) }
            }

            is NewCarAction.OnFeedbackTextChange -> {
                _uiState.update { it.copy(feedbackText = action.text) }
            }

            is NewCarAction.OnSubmitFeedback -> {
                _uiState.update { it.copy(showFeedbackDialog = false, feedbackText = "") }
                sendFormEvent(NewCarUiEvent.ShowSnackbar("Thank you for your feedback!"))
            }

            is NewCarAction.OnCpaEditClick -> {
                sendFormEvent(NewCarUiEvent.ShowSnackbar("Personal Accident Cover settings opened"))
            }

            is NewCarAction.OnBuyNowClick -> {
                val targetUrl = action.quote.buyNowUrl
                    ?: _uiState.value.quotes.firstOrNull()?.buyNowUrl
                    ?: "https://www.policyboss.com/buy"
                sendFormEvent(NewCarUiEvent.OpenUrl(targetUrl))
            }

            is NewCarAction.OnShareQuoteClick -> {
                val quote = action.quote
                val premium = quote.effectivePremium(_uiState.value.isZextraBundleEnabled)
                val shareText = "Motor Insurance Quote for ${_uiState.value.customerName}: ${quote.insurerName} at ₹$premium/yr. Check details at ${quote.buyNowUrl ?: "https://www.policyboss.com"}"
                sendFormEvent(NewCarUiEvent.ShareText(shareText))
            }

            is NewCarAction.OnShareAllQuotesClick -> {
                val state = _uiState.value
                val summary = "Compare Motor Quotes for ${state.customerName} - Min Premium: ₹${state.minPremium}, Total: ${state.totalQuotesCount} quotes available."
                sendFormEvent(NewCarUiEvent.ShareText(summary))
            }

            is NewCarAction.OnPolicyDetailsClick -> {
                _uiState.update { it.copy(selectedPolicyDetailQuote = action.quote) }
                sendFormEvent(NewCarUiEvent.NavigateToPolicyDetail)
            }

            is NewCarAction.OnEditVehicleClick -> {
                sendFormEvent(NewCarUiEvent.NavigateBackToForm)
            }

            is NewCarAction.OpenFilterScreen -> {
                _uiState.update {
                    it.copy(
                        selectedFilterCategory = it.coerceFilterCategory(action.category),
                        draftQuoteFilter = it.appliedQuoteFilter
                    )
                }
            }

            is NewCarAction.OnSelectFilterCategory -> {
                _uiState.update { it.copy(selectedFilterCategory = it.coerceFilterCategory(action.category)) }
            }

            is NewCarAction.OnUpdateDraftIdv -> {
                _uiState.update {
                    it.copy(
                        draftQuoteFilter = it.draftQuoteFilter.copy(
                            selectedIdv = action.idv,
                            isIdvExplicitlyChanged = (action.idv != it.draftQuoteFilter.defaultIdv)
                        )
                    )
                }
            }

            is NewCarAction.OnToggleDraftAddOn -> {
                _uiState.update { current ->
                    val updated = current.draftQuoteFilter.selectedAddOnIds.toMutableSet()
                    val adding = !updated.contains(action.addOnId)
                    if (adding) {
                        updated.add(action.addOnId)
                    } else {
                        updated.remove(action.addOnId)
                    }
                    val updatedClaimLimit = if (action.addOnId == "zero_dep" && adding) {
                        ZeroDepClaimLimit.ONE_CLAIM
                    } else {
                        current.draftQuoteFilter.selectedZeroDepClaimLimit
                    }
                    current.copy(
                        draftQuoteFilter = current.draftQuoteFilter.copy(
                            selectedAddOnIds = updated,
                            selectedZeroDepClaimLimit = updatedClaimLimit
                        )
                    )
                }
            }

            is NewCarAction.OnToggleSelectAllAddOns -> {
                _uiState.update { current ->
                    val allIds = MotorQuoteFilterData.addOnItems.map { it.id }.toSet()
                    val newSelection = if (action.selectAll) allIds else emptySet()
                    current.copy(
                        draftQuoteFilter = current.draftQuoteFilter.copy(
                            selectedAddOnIds = newSelection,
                            selectedZeroDepClaimLimit = if (action.selectAll) ZeroDepClaimLimit.ONE_CLAIM else current.draftQuoteFilter.selectedZeroDepClaimLimit
                        )
                    )
                }
            }

            is NewCarAction.OnSelectZeroDepClaimLimit -> {
                _uiState.update { current ->
                    current.copy(
                        draftQuoteFilter = current.draftQuoteFilter.copy(
                            selectedZeroDepClaimLimit = action.claimLimit
                        )
                    )
                }
            }

            is NewCarAction.OnResetDraftFilters -> {
                _uiState.update { current ->
                    // TP-only shows Additional Cover alone, so Reset leaves the hidden categories alone
                    if (current.isTpOnlyFilter) {
                        return@update current.copy(
                            draftQuoteFilter = current.draftQuoteFilter.resetAdditionalCover(isCompany = current.isCompany)
                        )
                    }
                    current.copy(
                        draftQuoteFilter = QuoteFilterState(
                            selectedIdv = current.draftQuoteFilter.defaultIdv,
                            defaultIdv = current.draftQuoteFilter.defaultIdv,
                            minIdv = current.draftQuoteFilter.minIdv,
                            maxIdv = current.draftQuoteFilter.maxIdv,
                            isIdvExplicitlyChanged = false,
                            selectedAddOnIds = emptySet(),
                            selectedLiabilityCoverIds = emptySet(),
                            selectedUnNamedPassengerCover = "No",
                            electricalAccessoriesAmount = "0",
                            nonElectricalAccessoriesAmount = "0",
                            antiTheftDevice = false,
                            payAsYouDrive = false,
                            voluntaryExcess = "No"
                        )
                    )
                }
            }

            is NewCarAction.OnToggleDraftAddOnExpanded -> {
                _uiState.update { current ->
                    val updated = current.draftQuoteFilter.expandedAddOnIds.toMutableSet()
                    if (updated.contains(action.addOnId)) {
                        updated.remove(action.addOnId)
                    } else {
                        updated.add(action.addOnId)
                    }
                    current.copy(
                        draftQuoteFilter = current.draftQuoteFilter.copy(expandedAddOnIds = updated)
                    )
                }
            }

            is NewCarAction.OnToggleDraftLiabilityCover -> {
                _uiState.update { current ->
                    if (current.isCompany && action.coverId == "owner_driver_pa") {
                        return@update current
                    }
                    val updated = current.draftQuoteFilter.selectedLiabilityCoverIds.toMutableSet()
                    if (updated.contains(action.coverId)) {
                        updated.remove(action.coverId)
                    } else {
                        updated.add(action.coverId)
                    }
                    current.copy(
                        draftQuoteFilter = current.draftQuoteFilter.copy(selectedLiabilityCoverIds = updated)
                    )
                }
            }

            is NewCarAction.OnUpdateDraftLiabilityRange -> {
                _uiState.update { current ->
                    val updated = current.draftQuoteFilter.liabilityCoverRanges.toMutableMap()
                    updated[action.coverId] = action.range
                    current.copy(
                        draftQuoteFilter = current.draftQuoteFilter.copy(liabilityCoverRanges = updated)
                    )
                }
            }

            is NewCarAction.OnSelectDraftUnNamedPassengerCover -> {
                _uiState.update {
                    it.copy(
                        draftQuoteFilter = it.draftQuoteFilter.copy(
                            selectedUnNamedPassengerCover = action.amount
                        )
                    )
                }
            }

            is NewCarAction.OnUpdateDraftElectricalAccessories -> {
                _uiState.update { current ->
                    val cleaned = action.amount.filter { it.isDigit() }.take(6)
                    current.copy(
                        draftQuoteFilter = current.draftQuoteFilter.copy(
                            electricalAccessoriesAmount = cleaned
                        )
                    )
                }
            }

            is NewCarAction.OnUpdateDraftNonElectricalAccessories -> {
                _uiState.update { current ->
                    val cleaned = action.amount.filter { it.isDigit() }.take(6)
                    current.copy(
                        draftQuoteFilter = current.draftQuoteFilter.copy(
                            nonElectricalAccessoriesAmount = cleaned
                        )
                    )
                }
            }

            is NewCarAction.OnUpdateDraftAntiTheft -> {
                _uiState.update {
                    it.copy(
                        draftQuoteFilter = it.draftQuoteFilter.copy(
                            antiTheftDevice = action.enabled
                        )
                    )
                }
            }

            is NewCarAction.OnUpdateDraftPayAsYouDrive -> {
                _uiState.update {
                    it.copy(
                        draftQuoteFilter = it.draftQuoteFilter.copy(
                            payAsYouDrive = action.enabled
                        )
                    )
                }
            }

            is NewCarAction.OnSelectDraftVoluntaryExcess -> {
                _uiState.update {
                    it.copy(
                        draftQuoteFilter = it.draftQuoteFilter.copy(
                            voluntaryExcess = action.amount
                        )
                    )
                }
            }

            is NewCarAction.DiscardDraftFilter -> {
                _uiState.update { it.copy(draftQuoteFilter = it.appliedQuoteFilter) }
            }

            is NewCarAction.ApplyFilter -> {
                val idvBeforeApply = _uiState.value.appliedQuoteFilter.selectedIdv
                val idvChangedBeforeApply = _uiState.value.appliedQuoteFilter.isIdvExplicitlyChanged
                _uiState.update { current ->
                    val clampedIdv = current.draftQuoteFilter.selectedIdv.coerceIn(
                        current.draftQuoteFilter.minIdv,
                        current.draftQuoteFilter.maxIdv
                    )
                    val liabilityCovers = if (current.isCompany) {
                        current.draftQuoteFilter.selectedLiabilityCoverIds - "owner_driver_pa"
                    } else {
                        current.draftQuoteFilter.selectedLiabilityCoverIds
                    }
                    val finalDraft = current.draftQuoteFilter.copy(
                        selectedIdv = clampedIdv,
                        selectedLiabilityCoverIds = liabilityCovers
                    )
                    // Two-way CPA <-> owner_driver_pa sync (Individual only)
                    val resolvedCpa = if (!current.isCompany) {
                        when {
                            liabilityCovers.contains("owner_driver_pa") &&
                                    current.cpaCoverageOption != CpaCoverageOption.INSURER_CPA ->
                                CpaCoverageOption.INSURER_CPA
                            !liabilityCovers.contains("owner_driver_pa") &&
                                    current.cpaCoverageOption == CpaCoverageOption.INSURER_CPA ->
                                CpaCoverageOption.NO_CPA
                            else -> current.cpaCoverageOption
                        }
                    } else {
                        current.cpaCoverageOption
                    }
                    val resolvedZextra = resolvedCpa == CpaCoverageOption.ZEXTRA_SWITCH
                    val resolvedNotice = when (resolvedCpa) {
                        CpaCoverageOption.ZEXTRA_SWITCH -> "Special Zextra Bundle (CPA + RSA) included"
                        CpaCoverageOption.INSURER_CPA -> "Insurer CPA included"
                        CpaCoverageOption.NO_CPA -> "CPA: Don't have Personal Accident cover"
                    }
                    current.copy(
                        draftQuoteFilter = finalDraft,
                        appliedQuoteFilter = finalDraft,
                        cpaCoverageOption = resolvedCpa,
                        isZextraBundleEnabled = resolvedZextra,
                        cpaNoticeText = resolvedNotice
                    )
                }
                // Trigger IDV re-quote if IDV changed (either newly set/updated or reset)
                val newState = _uiState.value
                val idvNowChanged = newState.appliedQuoteFilter.isIdvExplicitlyChanged
                val idvNowValue = newState.appliedQuoteFilter.selectedIdv
                if (idvNowChanged != idvChangedBeforeApply || (idvNowChanged && idvNowValue != idvBeforeApply)) {
                    runPremiumQuoteBundle(QuoteBundleTrigger.IDV_CHANGE)
                }
            }

            is NewCarAction.OnSelectCpaCoverageOption -> {
                _uiState.update { current ->
                    val zextraEnabled = action.option == CpaCoverageOption.ZEXTRA_SWITCH
                    val notice = when (action.option) {
                        CpaCoverageOption.ZEXTRA_SWITCH -> "Special Zextra Bundle (CPA + RSA) included"
                        CpaCoverageOption.INSURER_CPA -> "Insurer CPA included"
                        CpaCoverageOption.NO_CPA -> "CPA: Don't have Personal Accident cover"
                    }
                    // Auto-add/remove owner_driver_pa based on CPA choice (Individual only)
                    val updatedLiabilityIds = when {
                        action.option == CpaCoverageOption.INSURER_CPA && !current.isCompany ->
                            current.draftQuoteFilter.selectedLiabilityCoverIds + "owner_driver_pa"
                        action.option == CpaCoverageOption.NO_CPA ||
                                action.option == CpaCoverageOption.ZEXTRA_SWITCH ->
                            current.draftQuoteFilter.selectedLiabilityCoverIds - "owner_driver_pa"
                        else -> current.draftQuoteFilter.selectedLiabilityCoverIds
                    }
                    val updatedDraft = current.draftQuoteFilter.copy(
                        selectedLiabilityCoverIds = updatedLiabilityIds
                    )
                    val updatedApplied = current.appliedQuoteFilter.copy(
                        selectedLiabilityCoverIds = updatedLiabilityIds
                    )
                    current.copy(
                        cpaCoverageOption = action.option,
                        isZextraBundleEnabled = zextraEnabled,
                        cpaNoticeText = notice,
                        draftQuoteFilter = updatedDraft,
                        appliedQuoteFilter = updatedApplied
                    )
                }
            }

            is NewCarAction.OnShowVariantSheet -> {
                if (action.show) {
                    // Normally already cached after vehicle_info_user; the sheet updates when it arrives
                    val state = _uiState.value
                    loadFuelVariants(state.modelId, state.make)
                }
                _uiState.update { it.copy(showVariantSheet = action.show) }
            }

            is NewCarAction.OnShowQuoteControlInfo -> {
                _uiState.update {
                    it.copy(
                        infoDialogTitle = action.title,
                        infoDialogMessage = action.message
                    )
                }
            }

            is NewCarAction.OnDismissQuoteControlInfo -> {
                _uiState.update {
                    it.copy(
                        infoDialogTitle = null,
                        infoDialogMessage = null
                    )
                }
            }
        }
    }

    private fun handleGetQuotes(isRetry: Boolean) {
        val state = _uiState.value

        // Prevent duplicate in-flight submissions
        if (quoteJob?.isActive == true || state.quoteState is QuoteFetchState.Loading) {
            return
        }

        if (!state.isFormValid) {
            if (state.customerName.isBlank()) {
                _uiState.update { it.copy(customerNameError = "Customer name is required") }
            }
            sendFormEvent(NewCarUiEvent.ShowSnackbar("Please fill all mandatory fields and give consent"))
            return
        }

        // Safety net for the results-screen variant sheet: no-op when the catalogue for this
        // model is already loaded or in flight; refetches if the post-vahan prefetch failed.
        loadFuelVariants(state.modelId, state.make)

        drainPendingQuoteLoadingEvents()
        cancelPremiumPolling()
        isRefreshingQuotesInPlace = false

        _uiState.update {
            it.copy(
                quoteState = QuoteFetchState.Loading,
                premiumInitiate = null,
                premiumPollStatus = null,
                premiumProgress = null,
                premiumQuoteList = null,
                errorMessage = null
            )
        }

        if (!isRetry) {
            sendFormEvent(NewCarUiEvent.NavigateToQuoteLoading)
        }

        // Registration-number journey: price through premium_initiate using the vahan data
        val vehicle = vehicleInfo
        if (vehicle != null) {
            runPremiumQuoteBundle(QuoteBundleTrigger.GET_QUOTES)
            return
        }

        val request = CarQuoteRequest(
            customerName = state.customerName.trim(),
            rtoCode = state.selectedRto?.value ?: "MH-01",
            rtoCity = state.selectedRto?.label ?: "Mumbai",
            registeredOn = state.ownershipType.displayName,
            policyType = state.selectedPolicyType,
            isNewCar = state.entryMode == CarEntryMode.NEW_CAR,
            make = state.make.ifBlank { "HONDA" },
            makeId = state.makeId.ifBlank { null },
            modelId = state.modelId.ifBlank { null },
            fuelId = state.fuelId.ifBlank { null },
            variantId = state.variantId.ifBlank { null },
            model = state.model.ifBlank { state.selectedModel?.label ?: "Honda Amaze" },
            variant = state.variant.ifBlank { state.selectedVariant?.label ?: "1.2 S CVT (1198CC)" },
            fuel = state.fuel.ifBlank { state.selectedFuel?.label ?: "Petrol" },
            registrationDate = state.registrationDate,
            manufacturingDate = state.manufacturingDate,
            hasExistingPolicy = state.hasExistingPolicy,
            previousInsurer = if (state.shouldShowPreviousInsurer) state.selectedInsurer?.label else null,
            tpInsurer = if (state.shouldShowTpInsurer) state.selectedTpInsurer?.label else null,
            policyExpiryDate = if (state.shouldShowExpiryDate) state.policyExpiryDate else null,
            hasClaim = if (state.shouldShowClaimAndNcb) state.hasClaim else null,
            ncbStatus = if (state.shouldShowClaimAndNcb) state.selectedNcb?.label else null,
            consent = state.consentGiven
        )

        quoteJob = viewModelScope.launch {
            try {
                when (val result = repository.getCarQuotes(request)) {
                    is Resource.Success -> {
                        val quoteId = result.data.quoteId ?: "QUOTE_DEFAULT"
                        val quoteUrl = result.data.quoteUrl
                        val domainQuotes = result.data.toDomainQuotes()
                        if (domainQuotes.isEmpty()) {
                            val message = "No quotes are available for these details. Please review and try again."
                            _uiState.update {
                                it.copy(
                                    quoteState = QuoteFetchState.Error(message),
                                    errorMessage = message
                                )
                            }
                            return@launch
                        }
                        val quotesCount = result.data.quotesCount?.takeIf { it > 0 } ?: domainQuotes.size

                        _uiState.update {
                            it.copy(
                                quoteState = QuoteFetchState.Success(
                                    quoteId = quoteId,
                                    quoteUrl = quoteUrl,
                                    quotesCount = quotesCount
                                ),
                                quotes = domainQuotes,
                                quoteValidUpto = result.data.validUpto,
                                expandedAddOnQuoteIds = setOf(domainQuotes.firstOrNull()?.id ?: "ACKO-01"),
                                errorMessage = null
                            )
                        }
                        _quoteLoadingEvent.send(
                            QuoteLoadingUiEvent.NavigateToQuotes(
                                quoteId = quoteId,
                                quoteUrl = quoteUrl
                            )
                        )
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                quoteState = QuoteFetchState.Error(result.message),
                                errorMessage = result.message
                            )
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val message = e.localizedMessage?.takeIf { it.isNotBlank() }
                    ?: "Something went wrong while curating your quotes. Please try again."
                _uiState.update {
                    it.copy(
                        quoteState = QuoteFetchState.Error(message),
                        errorMessage = message
                    )
                }
            }
        }
    }

    /** What started a [PremiumQuoteBundle] run. */
    enum class QuoteBundleTrigger {
        /** Get Quotes: loader screen, then the results open on the first answer. */
        GET_QUOTES,
        /** Results-screen variant change, after the Re-calculate popup: re-quote in place. */
        VARIANT_CHANGE,
        /** Results-screen IDV change applied from the filter screen: re-quote in place. */
        IDV_CHANGE
        // Later: ADDONS, CPA ... (all in-place re-quotes)
    }

    /** premium_initiate -> premium_summary -> premium_list_db, always run together. */
    private val premiumQuoteBundle = PremiumQuoteBundle(repository)

    /** True while an in-place re-quote (results screen) is waiting for its first answer. */
    private var isRefreshingQuotesInPlace = false

    /**
     * The single entry point for the three quote APIs ([PremiumQuoteBundle]). Cancels any running
     * bundle / polling first.
     * - [QuoteBundleTrigger.GET_QUOTES]: handleGetQuotes has already reset the state and shown the
     *   loader; the results open on the first premium_list_db answer.
     * - Any other trigger re-quotes in place: the list empties and the results-screen loaders show
     *   until plans arrive; nothing navigates.
     *
     * The bundle collects in [premiumPollJob] so polling outlives the loader; [quoteJob] only waits
     * for the first answer, and cancelling it before then (loader closed) cancels the bundle too.
     */
    private fun runPremiumQuoteBundle(trigger: QuoteBundleTrigger) {
        // Demo / manual flow has no vahan vehicle to price through premium_initiate
        val vehicle = vehicleInfo ?: return
        val openResults = trigger == QuoteBundleTrigger.GET_QUOTES

        quoteJob?.cancel()
        cancelPremiumPolling()
        drainPendingQuoteLoadingEvents()
        isRefreshingQuotesInPlace = !openResults
        if (!openResults) {
            _uiState.update {
                it.copy(
                    quoteState = QuoteFetchState.Loading,
                    premiumInitiate = null,
                    premiumProgress = null,
                    premiumQuoteList = null,
                    quotes = emptyList(),
                    expandedAddOnQuoteIds = emptySet(),
                    premiumPollStatus = PremiumPollStatus.POLLING,
                    errorMessage = null
                )
            }
        }

        val state = _uiState.value
        val input = state.toPremiumInitiateInput(vehicle.withFormSelections(state))
        val firstAnswer = CompletableDeferred<Unit>()

        val bundleJob = viewModelScope.launch {
            try {
                var initiate: PremiumInitiateResult? = null
                var isFirst = true
                premiumQuoteBundle.run(input).collect { event ->
                    when (event) {
                        is PremiumQuoteBundleEvent.Initiated -> {
                            initiate = event.result
                            onPremiumInitiated(event.result, openResults)
                        }

                        is PremiumQuoteBundleEvent.Summary -> {
                            _uiState.update { it.copy(premiumProgress = event.progress) }
                        }

                        is PremiumQuoteBundleEvent.Quotes -> {
                            val result = initiate ?: return@collect
                            applyPremiumQuotes(result, event.list, event.status, isFirst, openResults)
                            isFirst = false
                            firstAnswer.complete(Unit)
                        }

                        is PremiumQuoteBundleEvent.Failed -> {
                            failQuoteFetch(event.message, openResults)
                            firstAnswer.complete(Unit)
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val message = e.localizedMessage?.takeIf { it.isNotBlank() } ?: PremiumQuoteBundle.GENERIC_ERROR
                failQuoteFetch(message, openResults)
            } finally {
                firstAnswer.complete(Unit)
            }
        }
        premiumPollJob = bundleJob

        quoteJob = viewModelScope.launch {
            try {
                firstAnswer.await()
            } catch (e: CancellationException) {
                bundleJob.cancel()
                throw e
            } finally {
                // Only this job's own end clears the flag (a cancelled older job must not)
                if (!openResults && quoteJob === coroutineContext[Job]) isRefreshingQuotesInPlace = false
            }
        }
    }

    /**
     * Results-screen variant sheet: a different variant is only held as pending and the
     * Re-calculate popup is shown; nothing is committed or fetched until the agent confirms.
     */
    private fun onQuoteVariantSelected(option: DropdownOption<String>) {
        val state = _uiState.value
        val item = state.quoteVariantItems.firstOrNull { it.variantId == option.value }
        val isSameVariant = item == null || item.variantId.equals(state.variantId, ignoreCase = true)
        if (isSameVariant) {
            _uiState.update { it.copy(showVariantSheet = false) }
            // Same variant after a failed fetch: retry it straight away
            if (state.quoteState is QuoteFetchState.Error) runPremiumQuoteBundle(QuoteBundleTrigger.VARIANT_CHANGE)
            return
        }
        _uiState.update {
            it.copy(
                showVariantSheet = false,
                pendingQuoteVariant = item,
                activeQuotePopup = QuotePopup.RecalculateVariant
            )
        }
    }

    /** Commits the pending variant (the fields premiums depend on) and re-quotes in place. */
    private fun commitPendingQuoteVariant() {
        val item = _uiState.value.pendingQuoteVariant
        _uiState.update { it.copy(activeQuotePopup = null, pendingQuoteVariant = null) }
        if (item == null) return
        _uiState.update {
            it.copy(
                variant = item.variantName,
                variantId = item.variantId,
                fuelId = item.fuelId.ifBlank { it.fuelId },
                cubicCapacity = item.cubicCapacity,
                seatingCapacity = item.seatingCapacity,
                exShowroomPrice = item.exShowroomPrice,
                selectedVariant = DropdownOption(item.variantId, item.displayLabel),
                carDetailsSummary = buildCarDetailsSummary(it.make, it.model, item.variantName, it.fuel, item.cubicCapacity)
            )
        }
        runPremiumQuoteBundle(QuoteBundleTrigger.VARIANT_CHANGE)
    }

    private fun onConfirmQuotePopup() {
        when (_uiState.value.activeQuotePopup) {
            QuotePopup.RecalculateVariant -> commitPendingQuoteVariant()
            null -> Unit
        }
    }

    /** X, back press or outside tap: drop whatever the popup was confirming. No API calls. */
    private fun dismissQuotePopup() {
        _uiState.update { it.copy(activeQuotePopup = null, pendingQuoteVariant = null) }
    }

    /** Error from premium_initiate or the first premium_list_db answer. */
    private fun failQuoteFetch(message: String, openResults: Boolean) {
        showQuoteError(message)
        if (!openResults) {
            // Results stay open: stop the loaders and tell the agent
            _uiState.update { it.copy(premiumPollStatus = PremiumPollStatus.STOPPED_ON_ERROR) }
            sendFormEvent(NewCarUiEvent.ShowSnackbar(message))
        }
    }

    /** premium_initiate gave a search reference; premium_summary / premium_list_db follow. */
    private fun onPremiumInitiated(result: PremiumInitiateResult, openResults: Boolean) {
        _uiState.update {
            it.copy(
                premiumInitiate = result,
                crnNumber = result.crn,
                premiumProgress = null,
                // In-place re-quote keeps the results-screen loaders up until the first answer
                premiumPollStatus = if (openResults) null else PremiumPollStatus.POLLING
            )
        }
    }

    /** Binds one premium_list_db snapshot; the first one also opens the results screen. */
    private suspend fun applyPremiumQuotes(
        initiate: PremiumInitiateResult,
        list: PremiumQuoteList,
        status: PremiumPollStatus,
        isFirst: Boolean,
        openResults: Boolean = true
    ) {
        val quotes = list.quotes.mapIndexed { index, quote -> quote.copy(isRecommended = index == 0) }
        val quoteIds = quotes.map { it.id }.toSet()
        val quoteId = initiate.requestUniqueId
        val isPolling = status == PremiumPollStatus.POLLING

        _uiState.update { current ->
            // Bind IDV min/max and expected IDV from API response
            val newMinIdv = list.minIdv.toFloat().takeIf { it > 0f } ?: current.draftQuoteFilter.minIdv
            val newMaxIdv = maxOf(newMinIdv, list.maxIdv.toFloat().takeIf { it > 0f } ?: current.draftQuoteFilter.maxIdv)
            val apiExpectedIdv = list.vehicleExpectedIdv.takeIf { it > 0 }?.toFloat()?.coerceIn(newMinIdv, newMaxIdv)
            val updatedDraftIdv = current.draftQuoteFilter.copy(
                minIdv = newMinIdv,
                maxIdv = newMaxIdv,
                defaultIdv = newMinIdv,
                selectedIdv = when {
                    current.draftQuoteFilter.isIdvExplicitlyChanged ->
                        current.draftQuoteFilter.selectedIdv.coerceIn(newMinIdv, newMaxIdv)
                    apiExpectedIdv != null -> apiExpectedIdv
                    else -> newMinIdv
                },
                isIdvExplicitlyChanged = current.draftQuoteFilter.isIdvExplicitlyChanged || apiExpectedIdv != null
            )
            val updatedAppliedIdv = current.appliedQuoteFilter.copy(
                minIdv = newMinIdv,
                maxIdv = newMaxIdv,
                defaultIdv = newMinIdv,
                selectedIdv = when {
                    current.appliedQuoteFilter.isIdvExplicitlyChanged ->
                        current.appliedQuoteFilter.selectedIdv.coerceIn(newMinIdv, newMaxIdv)
                    apiExpectedIdv != null -> apiExpectedIdv
                    else -> newMinIdv
                },
                isIdvExplicitlyChanged = current.appliedQuoteFilter.isIdvExplicitlyChanged || apiExpectedIdv != null
            )
            current.copy(
                quoteState = if (isFirst) {
                    QuoteFetchState.Success(quoteId = quoteId, quoteUrl = null, quotesCount = quotes.size)
                } else {
                    current.quoteState
                },
                crnNumber = initiate.crn.ifBlank { list.crn },
                premiumProgress = list.progress.takeIf { isPolling },
                premiumPollStatus = status,
                premiumQuoteList = list,
                quotes = quotes,
                draftQuoteFilter = updatedDraftIdv,
                appliedQuoteFilter = updatedAppliedIdv,
                // Keep what the agent expanded or ticked; new cards arrive collapsed
                expandedAddOnQuoteIds = if (isFirst) {
                    quotes.firstOrNull()?.let { setOf(it.id) } ?: emptySet()
                } else {
                    current.expandedAddOnQuoteIds.intersect(quoteIds)
                },
                // Zextra Switch follows the CPA choice shown on the results screen
                isZextraBundleEnabled = if (isFirst) {
                    current.cpaCoverageOption == CpaCoverageOption.ZEXTRA_SWITCH
                } else {
                    current.isZextraBundleEnabled
                },
                errorMessage = null
            )
        }
        if (isFirst && openResults) {
            _quoteLoadingEvent.send(QuoteLoadingUiEvent.NavigateToQuotes(quoteId = quoteId, quoteUrl = null))
        }
    }

    /** Stops premium_list_db polling (new search, loader closed, results left). */
    private fun cancelPremiumPolling() {
        premiumPollJob?.cancel()
        premiumPollJob = null
        _uiState.update { state ->
            if (state.premiumPollStatus == PremiumPollStatus.POLLING) {
                state.copy(premiumPollStatus = null, premiumProgress = null)
            } else {
                state
            }
        }
    }

    private fun showQuoteError(message: String) {
        _uiState.update {
            it.copy(quoteState = QuoteFetchState.Error(message), premiumProgress = null, errorMessage = message)
        }
    }

    /**
     * The vahan vehicle with any car/RTO the user re-picked on the form; only numeric ids
     * (real master ids) replace the vahan values.
     */
    private fun VehicleInfoDomain.withFormSelections(state: NewCarUiState): VehicleInfoDomain {
        val formVariantId = state.variantId.takeIf { id -> id.isNotBlank() && id.all { it.isDigit() } }
        val formRtoId = state.selectedRto?.value?.takeIf { id -> id.isNotBlank() && id.all { it.isDigit() } }
        return copy(
            variantId = formVariantId ?: variantId,
            fuelType = state.fuel.ifBlank { fuelType },
            rtoCode = formRtoId ?: rtoCode
        )
    }

    private fun cancelQuoteLoading() {
        quoteJob?.cancel()
        quoteJob = null
        cancelPremiumPolling()
        cancelVehicleLookupJob()
        drainPendingQuoteLoadingEvents()

        initializedMode = null
        initializedCarNumber = null

        _uiState.update {
            it.copy(
                quoteState = QuoteFetchState.Idle,
                vehicleLookupState = VehicleLookupState.Idle,
                errorMessage = null
            )
        }
        sendQuoteLoadingEvent(QuoteLoadingUiEvent.CloseLoading)
    }

    private fun drainPendingQuoteLoadingEvents() {
        while (_quoteLoadingEvent.tryReceive().isSuccess) {
            // Discard stale events prior to a new request or cancellation
        }
    }

    private fun sendFormEvent(event: NewCarUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    fun ensureQuotesLoaded() {
        if (_uiState.value.quotes.isNotEmpty() || _uiState.value.premiumInitiate != null) return

        if (!AppConfig.USE_DUMMY_DATA) {
            // No real quotes in memory (e.g. restored after process death): never fabricate
            // premiums in release; send the user back to the form to fetch fresh quotes.
            sendFormEvent(NewCarUiEvent.ShowSnackbar("Your quotes have expired. Please fetch them again."))
            sendFormEvent(NewCarUiEvent.NavigateBackToForm)
            return
        }

        _uiState.update { current ->
            val defaultCpa = if (current.isCompany) CpaCoverageOption.NO_CPA else CpaCoverageOption.ZEXTRA_SWITCH
            current.copy(
                quotes = com.policyboss.policybosspro.feature.carInsurance.model.MotorQuoteDefaults.generateDefaultQuotes(),
                expandedAddOnQuoteIds = setOf("ACKO-01"),
                cpaCoverageOption = if (current.cpaCoverageOption == CpaCoverageOption.INSURER_CPA) defaultCpa else current.cpaCoverageOption,
                isZextraBundleEnabled = (if (current.cpaCoverageOption == CpaCoverageOption.INSURER_CPA) defaultCpa else current.cpaCoverageOption) == CpaCoverageOption.ZEXTRA_SWITCH
            )
        }
    }

    private fun sendQuoteLoadingEvent(event: QuoteLoadingUiEvent) {
        viewModelScope.launch {
            _quoteLoadingEvent.send(event)
        }
    }
}
