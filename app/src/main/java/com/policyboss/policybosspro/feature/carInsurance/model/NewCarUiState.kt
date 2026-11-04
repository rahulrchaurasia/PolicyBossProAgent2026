package com.policyboss.policybosspro.feature.carInsurance.model

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

data class NewCarUiState(
    val isNewCar: Boolean = true,
    val vahanDetails: String = "HONDA CARS INDIA LTD, AMAZE 1.2 V CVT (I-VTEC), PETROL (1199 CC)",
    val showVahanBanner: Boolean = true,
    
    // Customer Name
    val customerName: String = "",
    val customerNameError: String? = null,
    
    // Registration Place (RTO)
    val selectedRto: DropdownOption<String>? = null,
    val rtoOptions: List<DropdownOption<String>> = CarInsuranceData.rtoOptions,
    
    // Ownership
    val ownershipType: OwnershipType = OwnershipType.INDIVIDUAL,
    
    // Preferred Policy Type
    val selectedPolicyType: String = "TP (3 Yr)",
    val policyTypeOptions: List<String> = CarInsuranceData.newCarPolicyTypes,
    
    // Existing Policy Section (For rollover / non-new car)
    val hasExistingPolicy: Boolean = true,
    val selectedInsurer: DropdownOption<String>? = null,
    val insurerOptions: List<DropdownOption<String>> = CarInsuranceData.insurerOptions,
    val policyExpiryDate: String = "",
    
    // Car Specs
    val selectedModel: DropdownOption<String>? = null,
    val modelOptions: List<DropdownOption<String>> = CarInsuranceData.carModels,
    val selectedVariant: DropdownOption<String>? = null,
    val variantOptions: List<DropdownOption<String>> = CarInsuranceData.defaultVariants,
    val selectedFuel: DropdownOption<String>? = null,
    val fuelOptions: List<DropdownOption<String>> = CarInsuranceData.fuelOptions,
    
    // Dates
    val registrationDate: String = "",
    val manufacturingDate: String = "",
    
    // Claims & NCB
    val hasClaim: Boolean = true,
    val selectedNcb: DropdownOption<String>? = DropdownOption("0", "0%"),
    val ncbOptions: List<DropdownOption<String>> = CarInsuranceData.ncbOptions,
    
    // Consent
    val consentGiven: Boolean = true,
    
    // UI controls
    val activeDatePicker: DatePickerType? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isFormValid: Boolean
        get() {
            if (customerName.trim().isEmpty()) return false
            if (selectedRto == null) return false
            if (selectedModel == null) return false
            if (selectedVariant == null) return false
            if (selectedFuel == null) return false
            if (registrationDate.isBlank()) return false
            if (manufacturingDate.isBlank()) return false
            if (!isNewCar && hasExistingPolicy) {
                if (selectedInsurer == null) return false
                if (policyExpiryDate.isBlank()) return false
            }
            if (!consentGiven) return false
            return true
        }
}
