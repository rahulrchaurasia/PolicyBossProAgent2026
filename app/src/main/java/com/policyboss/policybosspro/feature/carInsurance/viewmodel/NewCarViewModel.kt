package com.policyboss.policybosspro.feature.carInsurance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.Resource
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteRequest
import com.policyboss.policybosspro.feature.carInsurance.data.repository.CarInsuranceRepository
import com.policyboss.policybosspro.feature.carInsurance.model.CarBrandModelItem
import com.policyboss.policybosspro.feature.carInsurance.model.CarDetailsStep
import com.policyboss.policybosspro.feature.carInsurance.model.CarEntryMode
import com.policyboss.policybosspro.feature.carInsurance.model.CarInsuranceData
import com.policyboss.policybosspro.feature.carInsurance.model.CarVariantItem
import com.policyboss.policybosspro.feature.carInsurance.model.CpaCoverageOption
import com.policyboss.policybosspro.feature.carInsurance.model.DatePickerType
import com.policyboss.policybosspro.feature.carInsurance.model.NavigationDirection
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarAction
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiEvent
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiState
import com.policyboss.policybosspro.feature.carInsurance.model.OwnershipType
import com.policyboss.policybosspro.feature.carInsurance.model.QuoteFetchState
import com.policyboss.policybosspro.feature.carInsurance.model.QuoteLoadingUiEvent
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.MotorQuoteFilterData
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.QuoteFilterState
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.ZeroDepClaimLimit
import com.policyboss.policybosspro.ui.components.dropDown.DropdownOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
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

    fun initForMode(
        entryMode: CarEntryMode,
        carNumber: String? = null
    ) {
        if (initializedMode == entryMode && initializedCarNumber == carNumber) return
        initializedMode = entryMode
        initializedCarNumber = carNumber

        val seltosModel = DropdownOption("KIA_SELTOS", "Kia Seltos")
        val seltosVariant = DropdownOption("SELTOS_PLUS_1.5_GTX_AT", "Plus 1.5 GTX AT - Diesel - (1493 CC)")
        val seltosFuel = DropdownOption("DIESEL", "Diesel")
        val jaipurRto = DropdownOption("RJ-14", "RJ14 - Jaipur")
        val orientalInsurer = DropdownOption("ORIENTAL", "ORIENTAL INSURANCE CO. LTD.", com.policyboss.policybosspro.R.drawable.ic_acko_logo)
        val defaultTpInsurer = DropdownOption("ACKO", "Acko General Insurance", com.policyboss.policybosspro.R.drawable.ic_acko_logo)

        when (entryMode) {
            CarEntryMode.WITH_CAR_NUMBER -> {
                val vahanText = "KIA INDIA PRIVATE LIMITED, SELTOS D1.5 6AT GTX PLUS, DIESEL(1493 CC)"
                val carSummary = "KIA - SELTOS - PLUS 1.5 GTX AT - DIESEL - (1493 CC)"

                _uiState.update { current ->
                    current.copy(
                        entryMode = CarEntryMode.WITH_CAR_NUMBER,
                        headerTitle = "Continue with Car Number",
                        registrationNumber = carNumber ?: "RJ14-1234",
                        vahanData = vahanText,
                        vahanDetails = vahanText,
                        showVahanBanner = true,
                        carDetailsSummary = carSummary,
                        make = "KIA",
                        makeId = "KIA",
                        model = "SELTOS",
                        modelId = "SELTOS",
                        fuel = "DIESEL",
                        fuelId = "DIESEL",
                        variant = "Plus 1.5 GTX AT (1493CC)",
                        variantId = "SELTOS_PLUS_1.5_GTX_AT",
                        isCarDetailsSheetOpen = false,
                        sheetStep = CarDetailsStep.MAKE_MODEL,
                        draftBrandModel = null,
                        draftFuel = null,
                        draftVariant = null,
                        sheetSearchQuery = "",
                        policyTypeOptions = CarInsuranceData.existingCarPolicyTypes,
                        policyType = com.policyboss.policybosspro.feature.carInsurance.model.PolicyType.COMPREHENSIVE,
                        selectedPolicyType = "Comprehensive",
                        customerName = if (current.customerName.isNotBlank() && current.customerName != "Nida Hussaini") current.customerName else "PRIYANKA VYAS",
                        customerNameError = null,
                        selectedRto = jaipurRto,
                        ownershipType = OwnershipType.INDIVIDUAL,
                        selectedModel = seltosModel,
                        selectedVariant = seltosVariant,
                        selectedFuel = seltosFuel,
                        registrationDate = "01-Feb-2021",
                        manufacturingMonth = "Jan",
                        manufacturingYear = "2021",
                        manufacturingDate = "01/01/2021",
                        hasExistingPolicy = true,
                        selectedInsurer = orientalInsurer,
                        selectedTpInsurer = defaultTpInsurer,
                        policyExpiryDate = "29-Jan-2027",
                        hasClaim = false,
                        selectedNcb = DropdownOption("0", "0%"),
                        consentGiven = true
                    )
                }
            }
            CarEntryMode.WITHOUT_CAR_NUMBER -> {
                _uiState.update { current ->
                    current.copy(
                        entryMode = CarEntryMode.WITHOUT_CAR_NUMBER,
                        headerTitle = "Continue without car number",
                        registrationNumber = "",
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
                        isCarDetailsSheetOpen = false,
                        sheetStep = CarDetailsStep.MAKE_MODEL,
                        draftBrandModel = null,
                        draftFuel = null,
                        draftVariant = null,
                        sheetSearchQuery = "",
                        policyTypeOptions = CarInsuranceData.existingCarPolicyTypes,
                        policyType = com.policyboss.policybosspro.feature.carInsurance.model.PolicyType.COMPREHENSIVE,
                        selectedPolicyType = "Comprehensive",
                        customerName = if (current.customerName.isNotBlank() && current.customerName != "PRIYANKA VYAS") current.customerName else "",
                        customerNameError = null,
                        selectedRto = null,
                        ownershipType = OwnershipType.INDIVIDUAL,
                        selectedModel = null,
                        selectedVariant = null,
                        selectedFuel = null,
                        registrationDate = "",
                        manufacturingMonth = "Jan",
                        manufacturingYear = "2021",
                        manufacturingDate = "",
                        hasExistingPolicy = true,
                        selectedInsurer = null,
                        selectedTpInsurer = null,
                        policyExpiryDate = "",
                        hasClaim = false,
                        selectedNcb = DropdownOption("0", "0%"),
                        consentGiven = true
                    )
                }
            }
            CarEntryMode.NEW_CAR -> {
                val today = java.time.LocalDate.now()
                val regFormatted = today.format(java.time.format.DateTimeFormatter.ofPattern("dd-MMM-yyyy", java.util.Locale.ENGLISH))
                val mfgMonth = today.format(java.time.format.DateTimeFormatter.ofPattern("MMM", java.util.Locale.ENGLISH))
                val mfgYear = today.year.toString()

                _uiState.update { current ->
                    current.copy(
                        entryMode = CarEntryMode.NEW_CAR,
                        headerTitle = "New Car",
                        registrationNumber = "",
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
                        isCarDetailsSheetOpen = false,
                        sheetStep = CarDetailsStep.MAKE_MODEL,
                        draftBrandModel = null,
                        draftFuel = null,
                        draftVariant = null,
                        sheetSearchQuery = "",
                        policyTypeOptions = listOf("TP (3 Yr)", "OD (1 Yr) + TP (3 Yr)"),
                        policyType = com.policyboss.policybosspro.feature.carInsurance.model.PolicyType.COMPREHENSIVE,
                        selectedPolicyType = "OD (1 Yr) + TP (3 Yr)",
                        customerName = if (current.customerName.isNotBlank() && current.customerName != "PRIYANKA VYAS") current.customerName else "",
                        customerNameError = null,
                        selectedRto = null,
                        ownershipType = OwnershipType.INDIVIDUAL,
                        selectedModel = null,
                        selectedVariant = null,
                        selectedFuel = null,
                        registrationDate = regFormatted,
                        manufacturingMonth = mfgMonth,
                        manufacturingYear = mfgYear,
                        manufacturingDate = "01/$mfgMonth/$mfgYear",
                        hasExistingPolicy = false,
                        selectedInsurer = null,
                        selectedTpInsurer = null,
                        policyExpiryDate = "",
                        hasClaim = false,
                        selectedNcb = DropdownOption("0", "0%"),
                        consentGiven = true
                    )
                }
            }
        }
    }

    fun init(
        entryMode: CarEntryMode = CarEntryMode.WITHOUT_CAR_NUMBER,
        carNumber: String? = null
    ) {
        initForMode(entryMode = entryMode, carNumber = carNumber)
    }

    fun onAction(action: NewCarAction) {
        when (action) {
            is NewCarAction.UpdateEntryMode -> {
                initForMode(entryMode = action.mode)
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
                _uiState.update { it.copy(ownershipType = action.type) }
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
                val state = _uiState.value
                val hasCommittedVehicle = state.make.isNotBlank() && state.model.isNotBlank() && state.variant.isNotBlank()
                if (hasCommittedVehicle) {
                    val brandModel = CarInsuranceData.brandModels.firstOrNull {
                        it.makeId.equals(state.makeId, ignoreCase = true) && it.modelId.equals(state.modelId, ignoreCase = true)
                    } ?: CarBrandModelItem(
                        makeId = state.makeId.ifBlank { state.make },
                        makeName = state.make,
                        modelId = state.modelId.ifBlank { state.model },
                        modelName = state.model
                    )
                    val variantItem = CarInsuranceData.getVariantsFor(brandModel.makeId, brandModel.modelId, state.fuel)
                        .firstOrNull {
                            it.variantId.equals(state.variantId, ignoreCase = true) ||
                                    it.variantName.equals(state.variant, ignoreCase = true)
                        } ?: CarVariantItem(
                            variantId = state.variantId.ifBlank { state.variant },
                            variantName = state.variant,
                            fuelType = state.fuel
                        )
                    _uiState.update {
                        it.copy(
                            isCarDetailsSheetOpen = true,
                            sheetStep = CarDetailsStep.VARIANT,
                            sheetNavigationDirection = NavigationDirection.FORWARD,
                            draftBrandModel = brandModel,
                            draftFuel = state.fuel.ifBlank { "PETROL" },
                            draftVariant = variantItem,
                            sheetSearchQuery = ""
                        )
                    }
                } else {
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
                val canNavigate = when (action.step) {
                    CarDetailsStep.MAKE_MODEL -> true
                    CarDetailsStep.FUEL -> state.draftBrandModel != null || state.model.isNotBlank()
                    CarDetailsStep.VARIANT -> (state.draftBrandModel != null || state.model.isNotBlank()) &&
                            (!state.draftFuel.isNullOrBlank() || state.fuel.isNotBlank())
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
                val brandModel = state.draftBrandModel ?: CarInsuranceData.brandModels.firstOrNull {
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
                val modelId = brandModel.modelId
                val variantName = action.item.variantName
                val variantId = action.item.variantId
                val summary = "$makeName, $modelName, $chosenFuel, $variantName".uppercase()

                _uiState.update {
                    it.copy(
                        make = makeName,
                        makeId = makeId,
                        model = modelName,
                        modelId = modelId,
                        fuel = chosenFuel,
                        fuelId = chosenFuel,
                        variant = variantName,
                        variantId = variantId,
                        selectedModel = DropdownOption(modelId, modelName),
                        selectedVariant = DropdownOption(variantId, variantName),
                        selectedFuel = DropdownOption(chosenFuel, chosenFuel),
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
                _uiState.update {
                    val summary = "${it.selectedModel?.label ?: ""} - ${action.option.label} - ${it.selectedFuel?.label ?: ""}"
                    it.copy(
                        selectedVariant = action.option,
                        variant = action.option.label,
                        carDetailsSummary = summary,
                        showVariantSheet = false
                    )
                }
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
                val targetUrl = action.quote.policyDetailsUrl
                    ?: "https://www.policyboss.com/policy-details"
                sendFormEvent(NewCarUiEvent.OpenUrl(targetUrl))
            }

            is NewCarAction.OnEditVehicleClick -> {
                sendFormEvent(NewCarUiEvent.NavigateBackToForm)
            }

            is NewCarAction.OpenFilterScreen -> {
                _uiState.update {
                    it.copy(
                        selectedFilterCategory = action.category,
                        draftQuoteFilter = it.appliedQuoteFilter
                    )
                }
            }

            is NewCarAction.OnSelectFilterCategory -> {
                _uiState.update { it.copy(selectedFilterCategory = action.category) }
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

            is NewCarAction.OnSelectDraftCoverage -> {
                _uiState.update {
                    it.copy(
                        draftQuoteFilter = it.draftQuoteFilter.copy(
                            selectedCoverageId = action.coverageId
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
                    current.copy(
                        draftQuoteFilter = QuoteFilterState(
                            selectedIdv = current.draftQuoteFilter.defaultIdv,
                            defaultIdv = current.draftQuoteFilter.defaultIdv,
                            minIdv = current.draftQuoteFilter.minIdv,
                            maxIdv = current.draftQuoteFilter.maxIdv,
                            isIdvExplicitlyChanged = false,
                            selectedCoverageId = "",
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
                _uiState.update { current ->
                    val clampedIdv = current.draftQuoteFilter.selectedIdv.coerceIn(
                        current.draftQuoteFilter.minIdv,
                        current.draftQuoteFilter.maxIdv
                    )
                    val finalDraft = current.draftQuoteFilter.copy(selectedIdv = clampedIdv)
                    current.copy(
                        draftQuoteFilter = finalDraft,
                        appliedQuoteFilter = finalDraft
                    )
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
                    current.copy(
                        cpaCoverageOption = action.option,
                        isZextraBundleEnabled = zextraEnabled,
                        cpaNoticeText = notice
                    )
                }
            }

            is NewCarAction.OnShowVariantSheet -> {
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

        drainPendingQuoteLoadingEvents()

        _uiState.update {
            it.copy(
                quoteState = QuoteFetchState.Loading,
                errorMessage = null
            )
        }

        if (!isRetry) {
            sendFormEvent(NewCarUiEvent.NavigateToQuoteLoading)
        }

        val request = CarQuoteRequest(
            customerName = state.customerName.trim(),
            rtoCode = state.selectedRto?.value ?: "MH-01",
            rtoCity = state.selectedRto?.label ?: "Mumbai",
            registeredOn = state.ownershipType.displayName,
            policyType = state.selectedPolicyType,
            isNewCar = state.entryMode == CarEntryMode.NEW_CAR,
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

    private fun cancelQuoteLoading() {
        quoteJob?.cancel()
        quoteJob = null
        drainPendingQuoteLoadingEvents()

        _uiState.update {
            it.copy(
                quoteState = QuoteFetchState.Idle,
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
        if (_uiState.value.quotes.isEmpty()) {
            _uiState.update {
                it.copy(
                    quotes = com.policyboss.policybosspro.feature.carInsurance.model.MotorQuoteDefaults.generateDefaultQuotes(),
                    expandedAddOnQuoteIds = setOf("ACKO-01")
                )
            }
        }
    }

    private fun sendQuoteLoadingEvent(event: QuoteLoadingUiEvent) {
        viewModelScope.launch {
            _quoteLoadingEvent.send(event)
        }
    }
}
