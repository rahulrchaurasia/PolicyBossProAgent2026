package com.policyboss.policybosspro.feature.carInsurance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.Resource
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteRequest
import com.policyboss.policybosspro.feature.carInsurance.data.repository.CarInsuranceRepository
import com.policyboss.policybosspro.feature.carInsurance.model.CarEntryMode
import com.policyboss.policybosspro.feature.carInsurance.model.CarInsuranceData
import com.policyboss.policybosspro.feature.carInsurance.model.DatePickerType
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarAction
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiEvent
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiState
import com.policyboss.policybosspro.feature.carInsurance.model.OwnershipType
import com.policyboss.policybosspro.feature.carInsurance.model.QuoteFetchState
import com.policyboss.policybosspro.feature.carInsurance.model.QuoteLoadingUiEvent
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

    fun init(
        isNewCar: Boolean = true,
        carNumber: String? = null,
        entryMode: CarEntryMode? = null
    ) {
        val resolvedMode = entryMode ?: if (!carNumber.isNullOrBlank()) {
            CarEntryMode.WITH_CAR_NUMBER
        } else if (!isNewCar) {
            CarEntryMode.WITH_CAR_NUMBER
        } else {
            CarEntryMode.WITHOUT_CAR_NUMBER
        }

        if (initializedMode == resolvedMode && initializedCarNumber == carNumber) return
        initializedMode = resolvedMode
        initializedCarNumber = carNumber

        val policyTypes = CarInsuranceData.existingCarPolicyTypes

        val seltosModel = DropdownOption("KIA_SELTOS", "Kia Seltos")
        val seltosVariant = DropdownOption("SELTOS_PLUS_1.5_GTX_AT", "Plus 1.5 GTX AT - Diesel - (1493 CC)")
        val seltosFuel = DropdownOption("DIESEL", "Diesel")
        val jaipurRto = DropdownOption("RJ-14", "RJ14 - Jaipur")
        val orientalInsurer = DropdownOption("ORIENTAL", "ORIENTAL INSURANCE CO. LTD.")
        val defaultTpInsurer = DropdownOption("ACKO", "Acko General Insurance", com.policyboss.policybosspro.R.drawable.ic_acko_logo)

        val defaultModel = DropdownOption("HONDA_AMAZE", "Honda Amaze")
        val defaultVariant = DropdownOption("1.2_S_CVT", "1.2 S CVT (1198CC)")
        val defaultFuel = DropdownOption("PETROL", "Petrol")
        val defaultRto = DropdownOption("MH-01", "MH01 - Mumbai")
        val defaultInsurer = DropdownOption("ACKO", "Acko General Insurance")

        if (resolvedMode == CarEntryMode.WITH_CAR_NUMBER) {
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
                    isNewCar = false,
                    policyTypeOptions = policyTypes,
                    policyType = com.policyboss.policybosspro.feature.carInsurance.model.PolicyType.COMPREHENSIVE,
                    selectedPolicyType = "Comprehensive",
                    customerName = if (current.customerName.isNotBlank() && current.customerName != "Nida Hussaini") current.customerName else "PRIYANKA VYAS",
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
        } else {
            _uiState.update { current ->
                current.copy(
                    entryMode = CarEntryMode.WITHOUT_CAR_NUMBER,
                    headerTitle = "New Car",
                    registrationNumber = "",
                    vahanData = "",
                    vahanDetails = "",
                    showVahanBanner = false,
                    carDetailsSummary = "",
                    isNewCar = true,
                    policyTypeOptions = policyTypes,
                    policyType = com.policyboss.policybosspro.feature.carInsurance.model.PolicyType.TP_ONLY,
                    selectedPolicyType = "TP Only",
                    customerName = if (current.customerName.isNotBlank() && current.customerName != "PRIYANKA VYAS") current.customerName else "",
                    selectedRto = defaultRto,
                    ownershipType = OwnershipType.INDIVIDUAL,
                    selectedModel = defaultModel,
                    selectedVariant = defaultVariant,
                    selectedFuel = defaultFuel,
                    registrationDate = "",
                    manufacturingMonth = "Jan",
                    manufacturingYear = "2026",
                    manufacturingDate = "",
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

    fun onAction(action: NewCarAction) {
        when (action) {
            is NewCarAction.UpdateEntryMode -> {
                init(isNewCar = action.mode == CarEntryMode.WITHOUT_CAR_NUMBER, entryMode = action.mode)
            }

            is NewCarAction.UpdateManufacturingMonth -> {
                _uiState.update {
                    it.copy(
                        manufacturingMonth = action.month,
                        manufacturingDate = "01/${action.month}/${it.manufacturingYear}"
                    )
                }
            }

            is NewCarAction.UpdateManufacturingYear -> {
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
                _uiState.update {
                    it.copy(
                        customerName = action.name,
                        customerNameError = if (action.name.isBlank()) "Customer name is required" else null
                    )
                }
            }

            is NewCarAction.OnRtoSelected -> {
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
                        selectedPolicyType = resolved.displayName
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
                    it.copy(selectedVariant = action.option, carDetailsSummary = summary)
                }
            }

            is NewCarAction.OnFuelSelected -> {
                _uiState.update {
                    val summary = "${it.selectedModel?.label ?: ""} - ${it.selectedVariant?.label ?: ""} - ${action.option.label}"
                    it.copy(selectedFuel = action.option, carDetailsSummary = summary)
                }
            }

            is NewCarAction.OnOpenDatePicker -> {
                _uiState.update { it.copy(activeDatePicker = action.type) }
            }

            is NewCarAction.OnDateSelected -> {
                val activeType = _uiState.value.activeDatePicker
                _uiState.update { state ->
                    when (activeType) {
                        DatePickerType.REGISTRATION -> state.copy(
                            registrationDate = action.dateString,
                            activeDatePicker = null
                        )
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
            isNewCar = state.isNewCar,
            model = state.selectedModel?.label ?: "Honda Amaze",
            variant = state.selectedVariant?.label ?: "1.2 S CVT (1198CC)",
            fuel = state.selectedFuel?.label ?: "Petrol",
            registrationDate = state.registrationDate,
            manufacturingDate = state.manufacturingDate,
            hasExistingPolicy = state.hasExistingPolicy,
            previousInsurer = state.selectedInsurer?.label,
            policyExpiryDate = state.policyExpiryDate,
            hasClaim = state.hasClaim,
            ncbStatus = state.selectedNcb?.label,
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
