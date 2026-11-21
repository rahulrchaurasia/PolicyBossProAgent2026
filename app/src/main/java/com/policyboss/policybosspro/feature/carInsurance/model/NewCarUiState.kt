package com.policyboss.policybosspro.feature.carInsurance.model

import androidx.compose.runtime.Immutable
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
    val insurerOptions: List<DropdownOption<String>> = CarInsuranceData.insurerOptions,
    val selectedTpInsurer: DropdownOption<String>? = null,
    val tpInsurerOptions: List<DropdownOption<String>> = CarInsuranceData.insurerOptions,
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
    val isZextraBundleEnabled: Boolean = true,
    val cpaNoticeText: String = "CPA: Don't have Personal Accident cover",
    val expandedAddOnQuoteIds: Set<String> = setOf("ACKO-01"),
    val selectedAddOnsByQuoteId: Map<String, Set<String>> = emptyMap(),
    val selectedQuotesForComparison: Set<String> = emptySet(),
    val selectedSortOption: MotorQuoteSortOption = MotorQuoteSortOption.RECOMMENDED,
    val showSortSheet: Boolean = false,
    val showFilterSheet: Boolean = false,
    val showFeedbackDialog: Boolean = false,
    val feedbackRating: Int = 5,
    val feedbackText: String = ""
) {
    val isLoading: Boolean
        get() = quoteState is QuoteFetchState.Loading

    val isContinueWithCarNumber: Boolean
        get() = entryMode == CarEntryMode.WITH_CAR_NUMBER

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
            if (make.isBlank() && model.isBlank() && fuel.isBlank() && variant.isBlank()) {
                return carDetailsSummary.ifBlank { "" }
            }
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
            if (sheetSearchQuery.isBlank()) return CarInsuranceData.brandModels
            val query = sheetSearchQuery.trim().lowercase()
            return CarInsuranceData.brandModels.filter {
                it.displayName.lowercase().contains(query) ||
                        it.makeName.lowercase().contains(query) ||
                        it.modelName.lowercase().contains(query)
            }
        }

    val filteredFuelTypes: List<String>
        get() {
            val baseFuels = CarInsuranceData.fuelTypes
            if (sheetSearchQuery.isBlank()) return baseFuels
            val query = sheetSearchQuery.trim().lowercase()
            return baseFuels.filter { it.lowercase().contains(query) }
        }

    val filteredVariants: List<CarVariantItem>
        get() {
            val mId = draftBrandModel?.makeId ?: makeId.ifBlank { make }
            val modId = draftBrandModel?.modelId ?: modelId.ifBlank { model }
            val f = draftFuel ?: this.fuel
            val allVariants = CarInsuranceData.getVariantsFor(mId, modId, f)
            if (sheetSearchQuery.isBlank()) return allVariants
            val query = sheetSearchQuery.trim().lowercase()
            return allVariants.filter { it.variantName.lowercase().contains(query) }
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
            ?: 8

    val minPremium: Int
        get() = quotes.minOfOrNull { it.effectivePremium(isZextraBundleEnabled) } ?: 6403

    val maxEarnings: Int
        get() = quotes.maxOfOrNull { it.effectiveEarnings(isZextraBundleEnabled) } ?: 1403

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
            val effectiveList = if (quotes.isEmpty()) MotorQuoteDefaults.generateDefaultQuotes() else quotes
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

    val vehicleSummaryText: String
        get() {
            val modelName = selectedModel?.label ?: "Kia Seltos"
            val fuelName = selectedFuel?.label ?: "Diesel"
            val variantName = selectedVariant?.label ?: "Plus 1.5 GTX AT (1493CC)"
            val policyName = if (selectedPolicyType.isNotBlank()) selectedPolicyType else "Comprehensive"
            val ncbText = selectedNcb?.label ?: "0%"
            return "$modelName • $fuelName • $variantName • $policyName • NCB status: $ncbText"
        }
}
