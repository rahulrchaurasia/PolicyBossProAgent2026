package com.policyboss.policybosspro.feature.carInsurance.model

import com.policyboss.policybosspro.ui.components.dropDown.DropdownOption

sealed interface NewCarAction {
    // Mode Action
    data class UpdateEntryMode(val mode: CarEntryMode) : NewCarAction

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

    // Car Specs Actions
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
}
