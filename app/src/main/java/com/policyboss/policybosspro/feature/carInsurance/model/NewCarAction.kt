package com.policyboss.policybosspro.feature.carInsurance.model

import com.policyboss.policybosspro.ui.components.dropDown.DropdownOption

sealed interface NewCarAction {
    data class OnCustomerNameChange(val name: String) : NewCarAction
    data class OnRtoSelected(val option: DropdownOption<String>) : NewCarAction
    data class OnOwnershipSelected(val type: OwnershipType) : NewCarAction
    data class OnPolicyTypeSelected(val policyType: String) : NewCarAction
    
    // Existing Policy Actions
    data class OnExistingPolicyToggle(val hasExisting: Boolean) : NewCarAction
    data class OnInsurerSelected(val option: DropdownOption<String>) : NewCarAction
    
    // Car Specs Actions
    data class OnModelSelected(val option: DropdownOption<String>) : NewCarAction
    data class OnVariantSelected(val option: DropdownOption<String>) : NewCarAction
    data class OnFuelSelected(val option: DropdownOption<String>) : NewCarAction
    
    // Date Picker Actions
    data class OnOpenDatePicker(val type: DatePickerType) : NewCarAction
    data class OnDateSelected(val dateString: String) : NewCarAction
    data object OnDismissDatePicker : NewCarAction
    
    // Claims & NCB Actions
    data class OnClaimToggle(val hasClaim: Boolean) : NewCarAction
    data class OnNcbSelected(val option: DropdownOption<String>) : NewCarAction
    
    // Consent
    data class OnConsentChange(val isConsentGiven: Boolean) : NewCarAction
    data object OnNoticeForConsentClick : NewCarAction
    
    // Primary Actions
    data object OnGetQuotesClick : NewCarAction
    data object OnBackClick : NewCarAction
}
