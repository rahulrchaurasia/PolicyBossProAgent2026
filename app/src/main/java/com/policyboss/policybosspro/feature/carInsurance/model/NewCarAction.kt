package com.policyboss.policybosspro.feature.carInsurance.model

import com.policyboss.policybosspro.ui.components.dropDown.DropdownOption

sealed interface NewCarAction {
    // Mode Action
    data class UpdateEntryMode(val mode: CarEntryMode) : NewCarAction

    // Registration lookup (vehicle_info_user + model_list) shown on the quote loader
    data object OnRetryVehicleLookup : NewCarAction
    data object OnCancelVehicleLookup : NewCarAction
    data object OnContinueWithoutVehicleInfo : NewCarAction

    // Live Make/Model catalogue (bottom sheet)
    data object OnRetryModelList : NewCarAction
    data object OnRetryFuelVariants : NewCarAction
    /** Retry beta_GetFuelVariant for the committed model (results-screen variant sheet). */
    data object OnRetryQuoteVariants : NewCarAction

    // Previous / TP insurer sheet opened: (re)load the getPrevInsurer list if it is empty
    data object OnInsurerSheetOpened : NewCarAction

    // Customer Name
    data class OnCustomerNameChange(val name: String) : NewCarAction
    data class OnRtoSelected(val option: DropdownOption<String>) : NewCarAction
    data class OnOwnershipSelected(val type: OwnershipType) : NewCarAction
    data class OnPolicyTypeSelected(val policyType: String) : NewCarAction
    data class SelectPolicyType(val policyType: PolicyType) : NewCarAction

    // Existing Policy Actions
    data class OnExistingPolicyToggle(val hasExisting: Boolean) : NewCarAction
    data class OnInsurerSelected(val option: DropdownOption<String>) : NewCarAction
    data class OnTpInsurerSelected(val option: DropdownOption<String>) : NewCarAction

    // Unified Car Details Selection Actions
    data object OpenCarDetailsSheet : NewCarAction
    data object DismissCarDetailsSheet : NewCarAction
    data class OnSheetStepClick(val step: CarDetailsStep) : NewCarAction
    data object OnSheetBackClick : NewCarAction
    data class OnDraftBrandModelSelected(val item: CarBrandModelItem) : NewCarAction
    data class OnDraftFuelSelected(val fuel: String) : NewCarAction
    data class OnDraftVariantSelected(val item: CarVariantItem) : NewCarAction
    data class OnSheetSearchQueryChange(val query: String) : NewCarAction

    // Car Specs Actions (Retained for backward compatibility)
    data class OnModelSelected(val option: DropdownOption<String>) : NewCarAction
    data class OnVariantSelected(val option: DropdownOption<String>) : NewCarAction
    data class OnFuelSelected(val option: DropdownOption<String>) : NewCarAction
    data class UpdateModel(val model: String) : NewCarAction
    data class UpdateVariant(val variant: String) : NewCarAction
    data class UpdateFuel(val fuel: String) : NewCarAction

    // Date Picker & Manufacturing Month/Year Actions
    data class OnOpenDatePicker(val type: DatePickerType) : NewCarAction
    data class OnDateSelected(val dateString: String) : NewCarAction
    data object OnDismissDatePicker : NewCarAction
    data class UpdateManufacturingMonth(val month: String) : NewCarAction
    data class UpdateManufacturingYear(val year: String) : NewCarAction

    // Claims & NCB Actions
    data class OnClaimToggle(val hasClaim: Boolean) : NewCarAction
    data class OnNcbSelected(val option: DropdownOption<String>) : NewCarAction

    // Consent
    data class OnConsentChange(val isConsentGiven: Boolean) : NewCarAction
    data object OnNoticeForConsentClick : NewCarAction

    // Primary & Quote Loading Actions
    data object OnGetQuotesClick : NewCarAction
    data object OnRetryGetQuotes : NewCarAction
    data object OnCancelQuoteLoading : NewCarAction
    /** Results screen left (back / edit vehicle): stop premium_list_db polling. */
    data object OnLeaveQuoteResults : NewCarAction
    data object OnQuoteNavigationHandled : NewCarAction
    data object OnBackClick : NewCarAction

    // ==========================================
    // Motor Quote Results Actions
    // ==========================================
    data object ToggleZextraBundle : NewCarAction
    data class ToggleAddOnsExpanded(val quoteId: String) : NewCarAction
    data class ToggleAddOnSelected(val quoteId: String, val addOnId: String) : NewCarAction
    data class ToggleCompareQuote(val quoteId: String) : NewCarAction
    data class OnSortOptionSelected(val option: MotorQuoteSortOption) : NewCarAction
    data class OnShowSortSheet(val show: Boolean) : NewCarAction
    data class OnShowFilterSheet(val show: Boolean) : NewCarAction
    data class OnShowFeedbackDialog(val show: Boolean) : NewCarAction
    data class OnFeedbackRatingChange(val rating: Int) : NewCarAction
    data class OnFeedbackTextChange(val text: String) : NewCarAction
    data object OnSubmitFeedback : NewCarAction
    data object OnCpaEditClick : NewCarAction
    data class OnBuyNowClick(val quote: MotorQuote) : NewCarAction
    data class OnShareQuoteClick(val quote: MotorQuote) : NewCarAction
    data object OnShareAllQuotesClick : NewCarAction
    data class OnPolicyDetailsClick(val quote: MotorQuote) : NewCarAction
    data object OnEditVehicleClick : NewCarAction

    // Filter Screen Actions
    data class OpenFilterScreen(
        val category: com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.FilterCategory = com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.FilterCategory.SET_IDV
    ) : NewCarAction
    data class OnSelectFilterCategory(val category: com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.FilterCategory) : NewCarAction
    data class OnUpdateDraftIdv(val idv: Float) : NewCarAction
    data class OnToggleDraftAddOn(val addOnId: String) : NewCarAction
    data class OnToggleDraftAddOnExpanded(val addOnId: String) : NewCarAction
    data class OnToggleSelectAllAddOns(val selectAll: Boolean) : NewCarAction
    data class OnSelectZeroDepClaimLimit(val claimLimit: com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.ZeroDepClaimLimit) : NewCarAction
    data object OnResetDraftFilters : NewCarAction
    data class OnToggleDraftLiabilityCover(val coverId: String) : NewCarAction
    data class OnUpdateDraftLiabilityRange(val coverId: String, val range: ClosedFloatingPointRange<Float>) : NewCarAction
    data class OnSelectDraftUnNamedPassengerCover(val amount: String?) : NewCarAction
    data class OnUpdateDraftElectricalAccessories(val amount: String) : NewCarAction
    data class OnUpdateDraftNonElectricalAccessories(val amount: String) : NewCarAction
    data class OnUpdateDraftAntiTheft(val enabled: Boolean) : NewCarAction
    data class OnUpdateDraftPayAsYouDrive(val enabled: Boolean) : NewCarAction
    data class OnSelectDraftVoluntaryExcess(val amount: String) : NewCarAction
    data object DiscardDraftFilter : NewCarAction
    data object ApplyFilter : NewCarAction

    // Quote Controls & CPA Options Actions
    data class OnSelectCpaCoverageOption(val option: CpaCoverageOption) : NewCarAction
    data class OnShowVariantSheet(val show: Boolean) : NewCarAction
    /** Primary button of NewCarUiState.activeQuotePopup. */
    data object OnConfirmQuotePopup : NewCarAction
    /** Close (X), back press or outside tap on the active quote popup. */
    data object OnDismissQuotePopup : NewCarAction
    data class OnShowQuoteControlInfo(val title: String, val message: String) : NewCarAction
    data object OnDismissQuoteControlInfo : NewCarAction
}
