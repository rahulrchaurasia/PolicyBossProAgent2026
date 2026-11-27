package com.policyboss.policybosspro.feature.carInsurance.model

import androidx.compose.runtime.Immutable
import com.policyboss.policybosspro.core.AppConfig
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.FilterCategory
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.QuoteFilterState
import com.policyboss.policybosspro.ui.components.dropDown.DropdownOption

enum class OwnershipType(val displayName: String) {
    INDIVIDUAL("Individual"),
    COMPANY("Company")
}

enum class DatePickerType {
    REGISTRATION,
    MANUFACTURING,
    POLICY_EXPIRY
}

@Immutable
sealed interface QuoteFetchState {
    data object Idle : QuoteFetchState
    data object Loading : QuoteFetchState
    data class Success(
        val quoteId: String,
        val quoteUrl: String?,
        val quotesCount: Int = 0
    ) : QuoteFetchState

    data class Error(val message: String) : QuoteFetchState
}

/**
 * Lifecycle of the registration-number vahan lookup (vehicle_info_user + model_list)
 * that runs before the WITH_CAR_NUMBER form is shown. Rendered with the quote loader.
 */
@Immutable
sealed interface VehicleLookupState {
    data object Idle : VehicleLookupState
    data object Loading : VehicleLookupState
    data class Error(val message: String) : VehicleLookupState
}

/**
 * Lifecycle of the live Make/Model catalogue shown in the car details bottom sheet.
 */
@Immutable
sealed interface ModelListState {
    data object Idle : ModelListState
    data object Loading : ModelListState
    data object Success : ModelListState
    data class Error(val message: String) : ModelListState
}

/**
 * Lifecycle of beta_GetFuelVariant for the Make/Model chosen in the sheet.
 */
@Immutable
sealed interface FuelVariantState {
    data object Idle : FuelVariantState
    data object Loading : FuelVariantState
    data object Success : FuelVariantState
    data class Error(val message: String) : FuelVariantState
}

enum class CpaCoverageOption {
    ZEXTRA_SWITCH,
    INSURER_CPA,
    NO_CPA
}

@Immutable
data class NewCarUiState(
    val entryMode: CarEntryMode = CarEntryMode.WITHOUT_CAR_NUMBER,
    val headerTitle: String = "New Car",
    val registrationNumber: String = "",
    val vahanData: String = "",
    val vahanDetails: String = "",
    val showVahanBanner: Boolean = false,
    val carDetailsSummary: String = "",

    // Customer Name
    val customerName: String = "",
    val customerNameError: String? = null,

    // Registration Place (RTO)
    val selectedRto: DropdownOption<String>? = null,
    val rtoOptions: List<DropdownOption<String>> = CarInsuranceData.rtoOptions,

    // Ownership
    val ownershipType: OwnershipType = OwnershipType.INDIVIDUAL,

    // Preferred Policy Type
    val policyType: PolicyType = PolicyType.COMPREHENSIVE,
    val selectedPolicyType: String = policyType.displayName,
    val policyTypeOptions: List<String> = CarInsuranceData.existingCarPolicyTypes,

    // Existing Policy Section (For rollover / non-new car)
    val hasExistingPolicy: Boolean = true,
    val selectedInsurer: DropdownOption<String>? = null,
    /** Previous-insurer master list from insurers/getPrevInsurer/1 (empty until loaded). */
    val insurerOptions: List<DropdownOption<String>> = emptyList(),
    val selectedTpInsurer: DropdownOption<String>? = null,
    val tpInsurerOptions: List<DropdownOption<String>> = emptyList(),
    val policyExpiryDate: String = "",

    // Car Specs
    val make: String = "",
    val makeId: String = "",
    val model: String = "",
    val modelId: String = "",
    val fuel: String = "",
    val fuelId: String = "",
    val variant: String = "",
    val variantId: String = "",
    val selectedModel: DropdownOption<String>? = null,
    val modelOptions: List<DropdownOption<String>> = CarInsuranceData.carModels,
    val selectedVariant: DropdownOption<String>? = null,
    val variantOptions: List<DropdownOption<String>> = CarInsuranceData.defaultVariants,
    val selectedFuel: DropdownOption<String>? = null,
    val fuelOptions: List<DropdownOption<String>> = CarInsuranceData.fuelOptions,

    // Bottom Sheet Draft State
    val isCarDetailsSheetOpen: Boolean = false,
    val sheetStep: CarDetailsStep = CarDetailsStep.MAKE_MODEL,
    val sheetNavigationDirection: NavigationDirection = NavigationDirection.FORWARD,
    val draftBrandModel: CarBrandModelItem? = null,
    val draftFuel: String? = null,
    val draftVariant: CarVariantItem? = null,
    val sheetSearchQuery: String = "",

    // Live Make/Model catalogue (vehicles/model_list) & registration lookup
    val brandModels: List<CarBrandModelItem> = emptyList(),
    val modelListState: ModelListState = ModelListState.Idle,
    val vehicleLookupState: VehicleLookupState = VehicleLookupState.Idle,
    val fuelVariantState: FuelVariantState = FuelVariantState.Idle,
    val fuelVariantCatalog: FuelVariantCatalog? = null,

    // Confirm popup on the quote screens (texts in QuotePopup)
    val activeQuotePopup: QuotePopup? = null,
    /** Results-screen variant picked but not yet confirmed via [QuotePopup.RecalculateVariant]. */
    val pendingQuoteVariant: CarVariantItem? = null,

    // Selected variant details (from beta_GetFuelVariant / vehicle_info_user)
    val cubicCapacity: String = "",
    val seatingCapacity: String = "",
    val exShowroomPrice: String = "",

    // Dates
    val registrationDate: String = "",
    val manufacturingDate: String = "",
    val manufacturingMonth: String = "Jan",
    val manufacturingYear: String = "2021",
    val manufacturingMonths: List<String> = CarInsuranceData.months,
    val manufacturingYears: List<String> = CarInsuranceData.years,

    // Claims & NCB
    val hasClaim: Boolean = false,
    val selectedNcb: DropdownOption<String>? = DropdownOption("0", "0%"),
    val ncbOptions: List<DropdownOption<String>> = CarInsuranceData.ncbOptions,

    // Consent
    val consentGiven: Boolean = true,

    // UI controls & Quote Operation Lifecycle
    val activeDatePicker: DatePickerType? = null,
    val quoteState: QuoteFetchState = QuoteFetchState.Idle,
    val errorMessage: String? = null,

    // ==========================================
    // Motor Quote Results State
    // ==========================================
    val quotes: List<MotorQuote> = emptyList(),
    val quoteValidUpto: String? = null,
    val isZextraBundleEnabled: Boolean = (ownershipType == OwnershipType.INDIVIDUAL),
    val cpaNoticeText: String = if (ownershipType == OwnershipType.COMPANY) "CPA: Don't have Personal Accident cover" else "Special Zextra Bundle (CPA + RSA) included",
    val expandedAddOnQuoteIds: Set<String> = setOf("ACKO-01"),
    val selectedAddOnsByQuoteId: Map<String, Set<String>> = emptyMap(),
    val selectedQuotesForComparison: Set<String> = emptySet(),
    val selectedSortOption: MotorQuoteSortOption = MotorQuoteSortOption.RECOMMENDED,
    val showSortSheet: Boolean = false,
    val showFilterSheet: Boolean = false,
    val showFeedbackDialog: Boolean = false,
    val feedbackRating: Int = 5,
    val feedbackText: String = "",

    // Filter Screen State
    val selectedFilterCategory: FilterCategory = FilterCategory.SET_IDV,
    val appliedQuoteFilter: QuoteFilterState = if (ownershipType == OwnershipType.COMPANY) QuoteFilterState(selectedLiabilityCoverIds = emptySet()) else QuoteFilterState(),
    val draftQuoteFilter: QuoteFilterState = if (ownershipType == OwnershipType.COMPANY) QuoteFilterState(selectedLiabilityCoverIds = emptySet()) else QuoteFilterState(),

    // Quote Controls & CPA Options State
    val cpaCoverageOption: CpaCoverageOption = if (ownershipType == OwnershipType.COMPANY) CpaCoverageOption.NO_CPA else CpaCoverageOption.ZEXTRA_SWITCH,
    val crnNumber: String = "12678184",
    val showVariantSheet: Boolean = false,
    val infoDialogTitle: String? = null,
    val infoDialogMessage: String? = null,
    val selectedPolicyDetailQuote: MotorQuote? = null,

    // premium_initiate -> premium_summary -> premium_list_db (registration-number journey)
    val premiumInitiate: PremiumInitiateResult? = null,
    /** Latest premium_summary while insurers are still answering; null when not polling. */
    val premiumProgress: PremiumProgress? = null,
    /** Insurer quotes from premium_list_db; [quotes] holds the same plans for the list UI. */
    val premiumQuoteList: PremiumQuoteList? = null,
    /** premium_list_db polling state; null before the first answer or when polling was cancelled. */
    val premiumPollStatus: PremiumPollStatus? = null
) {
    /** More insurers may still answer; the results list keeps growing. */
    val isFetchingMoreQuotes: Boolean
        get() = premiumPollStatus == PremiumPollStatus.POLLING

    /** Answered / total insurers (0..1) for the footer progress bar; null when unknown. */
    val premiumProgressFraction: Float?
        get() = premiumProgress?.takeIf { it.total > 0 }?.let { progress ->
            (progress.total - progress.pending).coerceIn(0, progress.total).toFloat() / progress.total
        }

    /**
     * Results are open, insurers are still answering and no plan has been bound yet: the summary
     * row shows a spinner instead of 0 / ₹0. Earnings are not used (the quote APIs send none).
     */
    val isQuoteSummaryLoading: Boolean
        get() = isFetchingMoreQuotes && quotes.isEmpty()

    /** Polling hit the time limit while some insurers had not answered. */
    val isQuotesFetchTimedOut: Boolean
        get() = premiumPollStatus == PremiumPollStatus.TIMED_OUT

    val isLoading: Boolean
        get() = quoteState is QuoteFetchState.Loading

    val isContinueWithCarNumber: Boolean
        get() = entryMode == CarEntryMode.WITH_CAR_NUMBER

    val isVehicleLookupInProgress: Boolean
        get() = vehicleLookupState !is VehicleLookupState.Idle

    val isCompany: Boolean
        get() = ownershipType == OwnershipType.COMPANY

    val isIndividual: Boolean
        get() = ownershipType == OwnershipType.INDIVIDUAL

    val visibleAdditionalCoverItems: List<com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.LiabilityCoverItem>
        get() = if (isCompany) {
            com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.MotorQuoteFilterData.additionalCoverItems.filter { it.id != "owner_driver_pa" }
        } else {
            com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.MotorQuoteFilterData.additionalCoverItems
        }

    val displayCustomerFirstName: String
        get() = customerName.trim().split(Regex("\\s+")).firstOrNull().orEmpty()

    val existingPolicyQuestionLabel: String
        get() = if (policyType == PolicyType.TP_ONLY) {
            "Do You Have Existing Policy?"
        } else {
            "Do You Have Existing Comprehensive Policy?"
        }

    val isComprehensivePolicy: Boolean
        get() = policyType == PolicyType.COMPREHENSIVE

    val isTpPolicy: Boolean
        get() = policyType == PolicyType.TP_ONLY

    val isSaodPolicy: Boolean
        get() = policyType == PolicyType.OD_ONLY

    val shouldShowExistingPolicySection: Boolean
        get() = entryMode != CarEntryMode.NEW_CAR

    val shouldShowExistingPolicyCard: Boolean
        get() = shouldShowExistingPolicySection && (hasExistingPolicy || shouldShowTpInsurer)

    val shouldShowExpiryDate: Boolean
        get() = shouldShowExistingPolicySection && hasExistingPolicy

    val shouldShowPreviousInsurer: Boolean
        get() = shouldShowExistingPolicySection && hasExistingPolicy

    val shouldShowTpInsurer: Boolean
        get() = shouldShowExistingPolicySection && policyType == PolicyType.OD_ONLY

    val shouldShowClaimAndNcb: Boolean
        get() = shouldShowExistingPolicySection && hasExistingPolicy && (policyType == PolicyType.OD_ONLY || policyType == PolicyType.COMPREHENSIVE)

    val shouldShowClaimAndNcbSection: Boolean
        get() = shouldShowClaimAndNcb

    val selectedPreviousInsurer: DropdownOption<String>?
        get() = selectedInsurer

    val formattedCarDetails: String
        get() {
            // "MARUTI-WAGON R-1.0 LXI ABS-PETROL- (998 CC)" from the lookup or the sheet
            if (carDetailsSummary.isNotBlank()) return carDetailsSummary
            if (make.isBlank() && model.isBlank() && fuel.isBlank() && variant.isBlank()) return ""
            val parts = listOf(make, model, fuel, variant).filter { it.isNotBlank() }
            return parts.joinToString(", ").uppercase()
        }

    val canStepBackInsideSheet: Boolean
        get() = isCarDetailsSheetOpen && sheetStep != CarDetailsStep.MAKE_MODEL

    val sheetTitle: String
        get() = sheetStep.title

    val sheetSearchPlaceholder: String
        get() = when (sheetStep) {
            CarDetailsStep.MAKE_MODEL -> "Search car brand"
            CarDetailsStep.FUEL -> "Search car fuel"
            CarDetailsStep.VARIANT -> "Search car variant"
        }

    val filteredBrandModels: List<CarBrandModelItem>
        get() {
            if (sheetSearchQuery.isBlank()) return brandModels
            val query = sheetSearchQuery.trim().lowercase()
            return brandModels.filter {
                it.displayName.lowercase().contains(query) ||
                        it.vehicleName.lowercase().contains(query) ||
                        it.makeName.lowercase().contains(query) ||
                        it.modelName.lowercase().contains(query)
            }
        }

    /** The car currently on the form, highlighted in step 1 when the sheet opens. */
    val committedBrandModel: CarBrandModelItem?
        get() {
            if (model.isBlank()) return null
            return brandModels.firstOrNull { it.modelId.equals(modelId, ignoreCase = true) }
                ?: CarBrandModelItem(makeId.ifBlank { make }, make, modelId.ifBlank { model }, model)
        }

    private val isEditingCommittedModel: Boolean
        get() = model.isNotBlank() && (draftBrandModel == null || draftBrandModel.modelId.equals(modelId, ignoreCase = true))

    /** Highlights: the draft choice, else the committed one while the same car is being edited. */
    val highlightedBrandModel: CarBrandModelItem?
        get() = draftBrandModel ?: committedBrandModel

    val highlightedFuel: String?
        get() = draftFuel ?: fuel.takeIf { isEditingCommittedModel && it.isNotBlank() }

    val highlightedVariant: CarVariantItem?
        get() = draftVariant ?: if (isEditingCommittedModel && variant.isNotBlank() &&
            (draftFuel == null || draftFuel.equals(fuel, ignoreCase = true))
        ) {
            filteredVariants.firstOrNull { it.variantId.equals(variantId, ignoreCase = true) }
        } else {
            null
        }

    /** Model whose fuels/variants the sheet is showing (draft selection, else committed). */
    val sheetModelId: String
        get() = draftBrandModel?.modelId ?: modelId

    /** Live catalogue for [sheetModelId], or null while it is not loaded. */
    val activeFuelVariantCatalog: FuelVariantCatalog?
        get() = fuelVariantCatalog?.takeIf { it.modelId.equals(sheetModelId, ignoreCase = true) }

    val filteredFuelTypes: List<String>
        get() {
            val fuels = activeFuelVariantCatalog?.selectableFuels.orEmpty()
            if (sheetSearchQuery.isBlank()) return fuels
            val query = sheetSearchQuery.trim().lowercase()
            return fuels.filter { it.lowercase().contains(query) }
        }

    val filteredVariants: List<CarVariantItem>
        get() {
            val selectedFuel = draftFuel ?: fuel
            val catalogueVariants = activeFuelVariantCatalog?.variantsFor(selectedFuel).orEmpty()
            // Keep the committed (e.g. vahan-fetched) variant selectable for the same Make + Model + Fuel
            val isCommittedVehicle = sheetModelId.equals(modelId, ignoreCase = true) &&
                    (draftFuel == null || draftFuel.equals(fuel, ignoreCase = true))
            val committedVariant = if (isCommittedVehicle && variant.isNotBlank() &&
                catalogueVariants.none {
                    it.variantId.equals(variantId, ignoreCase = true) || it.variantName.equals(variant, ignoreCase = true)
                }
            ) {
                CarVariantItem(
                    variantId = variantId.ifBlank { variant },
                    variantName = variant,
                    fuelType = fuel,
                    modelId = modelId,
                    fuelId = fuelId,
                    cubicCapacity = cubicCapacity,
                    seatingCapacity = seatingCapacity,
                    exShowroomPrice = exShowroomPrice
                )
            } else {
                null
            }
            val allVariants = listOfNotNull(committedVariant) + catalogueVariants
            if (sheetSearchQuery.isBlank()) return allVariants
            val query = sheetSearchQuery.trim().lowercase()
            return allVariants.filter { it.displayLabel.lowercase().contains(query) }
        }

    /**
     * Variants for the results-screen "Select Variant" sheet: the committed Make + Model + Fuel
     * from the cached beta_GetFuelVariant response. The committed (vehicle_info_user) variant is
     * kept selectable even when the catalogue does not list it or is not loaded yet.
     */
    val quoteVariantItems: List<CarVariantItem>
        get() {
            val catalogue = fuelVariantCatalog
                ?.takeIf { it.modelId.equals(modelId, ignoreCase = true) }
                ?.variantsFor(fuel)
                .orEmpty()
            val committed = if (variant.isNotBlank() && catalogue.none {
                    it.variantId.equals(variantId, ignoreCase = true) || it.variantName.equals(variant, ignoreCase = true)
                }
            ) {
                CarVariantItem(
                    variantId = variantId.ifBlank { variant },
                    variantName = variant,
                    fuelType = fuel,
                    modelId = modelId,
                    fuelId = fuelId,
                    cubicCapacity = cubicCapacity,
                    seatingCapacity = seatingCapacity,
                    exShowroomPrice = exShowroomPrice
                )
            } else {
                null
            }
            return listOfNotNull(committed) + catalogue
        }

    /**
     * Results-screen variant pill, same format as the sheet rows: `1.0 VXI AMT BS-VI (998CC)`.
     * Works for the vehicle_info_user variant (Cubic_Capacity) and a variant picked later.
     */
    val quoteVariantDisplayText: String
        get() {
            val name = variant.trim()
            if (name.isBlank()) return ""
            val cc = cubicCapacity.trim()
            // Some names already carry the capacity, e.g. "Plus 1.5 GTX AT (1493CC)"
            val hasCc = name.endsWith("CC)", ignoreCase = true)
            return if (cc.isNotBlank() && !hasCc) "$name (${cc}CC)" else name
        }

    val quoteVariantOptions: List<DropdownOption<String>>
        get() = quoteVariantItems.map { DropdownOption(it.variantId, it.displayLabel) }

    /**
     * Load state of the catalogue behind [quoteVariantItems]: Success once the committed
     * model's catalogue is in state, otherwise the in-flight / failed fetch state.
     */
    val quoteVariantLoadState: FuelVariantState
        get() = when {
            fuelVariantCatalog?.modelId?.equals(modelId, ignoreCase = true) == true -> FuelVariantState.Success
            fuelVariantState is FuelVariantState.Success -> FuelVariantState.Idle
            else -> fuelVariantState
        }

    /** The committed variant in [quoteVariantOptions] (matched by Vehicle_ID, else by name). */
    val selectedQuoteVariantOption: DropdownOption<String>?
        get() {
            val items = quoteVariantItems
            val match = items.firstOrNull { it.variantId.equals(variantId, ignoreCase = true) }
                ?: items.firstOrNull { it.variantName.equals(variant, ignoreCase = true) }
            return match?.let { DropdownOption(it.variantId, it.displayLabel) }
        }

    val isFormValid: Boolean
        get() {
            if (customerName.trim().isEmpty()) return false
            if (selectedRto == null) return false
            if (!consentGiven) return false

            val hasCarDetails = formattedCarDetails.isNotBlank() ||
                    (selectedModel != null && selectedVariant != null && selectedFuel != null) ||
                    (entryMode == CarEntryMode.WITH_CAR_NUMBER && carDetailsSummary.isNotBlank())
            if (!hasCarDetails) return false

            return when (entryMode) {
                CarEntryMode.WITH_CAR_NUMBER,
                CarEntryMode.WITHOUT_CAR_NUMBER -> {
                    if (registrationDate.isBlank()) return false
                    if (manufacturingMonth.isBlank() || manufacturingYear.isBlank()) return false
                    if (hasExistingPolicy) {
                        if (shouldShowExpiryDate && policyExpiryDate.isBlank()) return false
                        if (shouldShowPreviousInsurer && selectedInsurer == null) return false
                    }
                    if (shouldShowTpInsurer && selectedTpInsurer == null) return false
                    true
                }
                CarEntryMode.NEW_CAR -> {
                    if (registrationDate.isBlank()) return false
                    if (manufacturingMonth.isBlank() || manufacturingYear.isBlank()) return false
                    true
                }
            }
        }

    val totalQuotesCount: Int
        get() = quotes.size.takeIf { it > 0 }
            ?: (quoteState as? QuoteFetchState.Success)?.quotesCount
            ?: if (premiumInitiate != null) 0 else 8

    val minPremium: Int
        get() = quotes.minOfOrNull { it.effectivePremium(isZextraBundleEnabled) }
            ?: if (premiumInitiate != null) 0 else 6403

    val maxEarnings: Int
        get() = quotes.maxOfOrNull { it.effectiveEarnings(isZextraBundleEnabled) }
            ?: if (premiumInitiate != null) 0 else 1403

    val recommendedQuote: MotorQuote?
        get() = sortedQuotes.firstOrNull { it.isRecommended } ?: sortedQuotes.firstOrNull()

    val otherQuotes: List<MotorQuote>
        get() {
            val rec = recommendedQuote
            return if (rec != null) {
                sortedQuotes.filter { it.id != rec.id }
            } else {
                sortedQuotes
            }
        }

    val sortedQuotes: List<MotorQuote>
        get() {
            val effectiveList = if (quotes.isEmpty() && AppConfig.USE_DUMMY_DATA) {
                MotorQuoteDefaults.generateDefaultQuotes()
            } else {
                quotes
            }
            return when (selectedSortOption) {
                MotorQuoteSortOption.RECOMMENDED -> effectiveList.sortedWith(
                    compareByDescending<MotorQuote> { it.isRecommended }
                        .thenBy { it.effectivePremium(isZextraBundleEnabled) }
                )
                MotorQuoteSortOption.LOWEST_PREMIUM -> effectiveList.sortedBy {
                    it.effectivePremium(isZextraBundleEnabled)
                }
                MotorQuoteSortOption.HIGHEST_EARNINGS -> effectiveList.sortedByDescending {
                    it.effectiveEarnings(isZextraBundleEnabled)
                }
                MotorQuoteSortOption.BEST_CLAIM_RATIO -> effectiveList.sortedByDescending {
                    it.benefits.firstOrNull { b -> b.contains("%") }
                        ?.filter { c -> c.isDigit() }
                        ?.toIntOrNull() ?: 0
                }
            }
        }

    // ----- Quote filter: TP-only shows Additional Cover alone -----

    val isTpOnlyFilter: Boolean
        get() = policyType == PolicyType.TP_ONLY

    /** Filter categories for the selected policy type. */
    val availableFilterCategories: List<FilterCategory>
        get() = if (isTpOnlyFilter) listOf(FilterCategory.ADDITIONAL_COVER) else FilterCategory.entries

    /** The left rail is pointless with a single category. */
    val showFilterCategoryRail: Boolean
        get() = availableFilterCategories.size > 1

    /** Electrical / Non-Electrical accessories are own-damage covers. */
    val showAccessoriesFilter: Boolean
        get() = !isTpOnlyFilter

    /** Apply-button count for the draft filter, limited to what the screen shows. */
    val draftFilterCount: Int
        get() = draftQuoteFilter.activeFilterCount(additionalCoverOnly = isTpOnlyFilter)

    /** [category] if it is available for this policy type, else the first available one. */
    fun coerceFilterCategory(category: FilterCategory): FilterCategory =
        category.takeIf { it in availableFilterCategories } ?: availableFilterCategories.first()

    /** premium_initiate succeeded but no insurer returned a plan (and none are still expected). */
    val isAwaitingInsurerQuotes: Boolean
        get() = premiumInitiate != null && quotes.isEmpty() && !isFetchingMoreQuotes

    val awaitingQuotesMessage: String
        get() {
            val crn = premiumQuoteList?.crn?.ifBlank { null } ?: premiumInitiate?.crn.orEmpty()
            val prefix = if (crn.isNotBlank()) "CRN $crn: " else ""
            val declined = premiumQuoteList?.declinedInsurers.orEmpty().size
            return prefix + if (declined > 0) {
                "none of the $declined insurers could quote this vehicle."
            } else {
                "no insurer quotes are available for this vehicle."
            }
        }

    /** Insurers that answered without a plan (underwriting restriction, errors). */
    val declinedInsurers: List<DeclinedInsurer>
        get() = premiumQuoteList?.declinedInsurers.orEmpty()

    /** Loader sub-text while premium_summary is being polled, e.g. "6 of 18 insurers responded". */
    val premiumProgressText: String?
        get() = premiumProgress?.takeIf { it.total > 0 }?.let { progress ->
            val answered = (progress.total - progress.pending).coerceIn(0, progress.total)
            "$answered of ${progress.total} insurers responded"
        }

    val vehicleSummaryText: String
        get() {
            val modelName = selectedModel?.label ?: "Kia Seltos"
            val fuelName = selectedFuel?.label ?: "Diesel"
            val variantName = selectedVariant?.label ?: "Plus 1.5 GTX AT (1493CC)"
            val policyName = if (selectedPolicyType.isNotBlank()) selectedPolicyType else "Comprehensive"
            // NCB does not apply to a third-party-only policy
            if (isTpOnlyFilter) return "$modelName • $fuelName • $variantName • $policyName"
            val ncbText = selectedNcb?.label ?: "0%"
            return "$modelName • $fuelName • $variantName • $policyName • NCB status: $ncbText"
        }

    val formattedVehicleTitle: String
        get() {
            val m = make.trim()
            val mod = model.trim().ifBlank { selectedModel?.label ?: "" }
            if (m.isNotBlank() && mod.isNotBlank()) {
                return if (mod.startsWith(m, ignoreCase = true)) {
                    mod
                } else {
                    "$m $mod"
                }
            }
            if (mod.isNotBlank()) return mod
            if (m.isNotBlank()) return m
            if (carDetailsSummary.isNotBlank()) {
                val parts = carDetailsSummary.split(",")
                if (parts.size >= 2) return "${parts[0].trim()} ${parts[1].trim()}"
                return parts.first().trim()
            }
            return "Honda Amaze"
        }

    val formattedVehicleSubtitle: String
        get() {
            val f = fuel.trim().ifBlank { selectedFuel?.label ?: "" }
            val v = variant.trim().ifBlank { selectedVariant?.label ?: "" }
            val parts = listOf(f, v).filter { it.isNotBlank() }
            if (parts.isNotEmpty()) return parts.joinToString(" • ")
            if (carDetailsSummary.isNotBlank()) {
                val split = carDetailsSummary.split(",")
                if (split.size > 2) {
                    return split.drop(2).map { it.trim() }.filter { it.isNotBlank() }.joinToString(" • ")
                }
            }
            return "Petrol • 1.2 S CVT (1198CC)"
        }

    val formattedRegistrationNumber: String
        get() {
            if (entryMode == CarEntryMode.NEW_CAR || registrationNumber.isBlank()) {
                return if (selectedRto != null && selectedRto.value.isNotBlank() && entryMode == CarEntryMode.WITHOUT_CAR_NUMBER) {
                    "RTO: ${selectedRto.value}"
                } else {
                    "NEW CAR"
                }
            }
            val compact = registrationNumber.replace("-", "").replace(Regex("\\s+"), "").uppercase()
            val match = Regex("^([A-Z]{2})([0-9]{1,2})([A-Z]{1,3})([0-9]{4})$").find(compact)
            return if (match != null) {
                "${match.groupValues[1]} ${match.groupValues[2]} ${match.groupValues[3]} ${match.groupValues[4]}"
            } else {
                registrationNumber.replace("-", " ").replace(Regex("\\s+"), " ").trim().uppercase()
            }
        }

    val quoteValidUptoText: String
        get() {
            if (!quoteValidUpto.isNullOrBlank()) {
                val candidate = quoteValidUpto.trim()
                val patterns = listOf(
                    "yyyy-MM-dd",
                    "dd/MM/yyyy",
                    "dd-MM-yyyy",
                    "yyyy/MM/dd",
                    "dd-MMM-yyyy",
                    "dd MMM yyyy"
                )
                for (pattern in patterns) {
                    try {
                        val parsed = java.text.SimpleDateFormat(pattern, java.util.Locale.ENGLISH).parse(candidate)
                        if (parsed != null) {
                            return java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.ENGLISH).format(parsed)
                        }
                    } catch (_: Exception) { }
                }
            }
            // Quotes stay valid until the end of tomorrow, e.g. "27 Nov 2026, 11:59 PM"
            return quoteValidUptoLabel(java.time.LocalDate.now())
        }

    val vahanDescriptionText: String
        get() {
            if (vahanData.isNotBlank()) return vahanData
            if (vahanDetails.isNotBlank()) return vahanDetails
            if (formattedCarDetails.isNotBlank()) return formattedCarDetails
            if (carDetailsSummary.isNotBlank()) return carDetailsSummary
            val parts = listOf(make, model, variant, fuel).filter { it.isNotBlank() }
            if (parts.isNotEmpty()) return parts.joinToString(", ").uppercase()
            return "KIA INDIA PRIVATE LIMITED, SELTOS D1.5 6AT GTX PLUS, DIESEL, 1493CC"
        }
}

/** Quote validity: [today] + 1 day at 23:59:59, formatted as "27 Nov 2026, 11:59 PM". */
fun quoteValidUptoLabel(today: java.time.LocalDate): String =
    today.plusDays(1)
        .atTime(23, 59, 59)
        .format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", java.util.Locale.ENGLISH))
