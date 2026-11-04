package com.policyboss.policybosspro.feature.carInsurance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.Resource
import com.policyboss.policybosspro.feature.carInsurance.data.model.CarQuoteRequest
import com.policyboss.policybosspro.feature.carInsurance.data.repository.CarInsuranceRepository
import com.policyboss.policybosspro.feature.carInsurance.model.CarInsuranceData
import com.policyboss.policybosspro.feature.carInsurance.model.DatePickerType
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarAction
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiEvent
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiState
import com.policyboss.policybosspro.feature.carInsurance.model.OwnershipType
import com.policyboss.policybosspro.ui.components.dropDown.DropdownOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewCarViewModel @Inject constructor(
    private val repository: CarInsuranceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewCarUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<NewCarUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun init(isNewCar: Boolean) {
        val policyTypes = if (isNewCar) {
            CarInsuranceData.newCarPolicyTypes
        } else {
            CarInsuranceData.existingCarPolicyTypes
        }

        val defaultModel = DropdownOption("HONDA_AMAZE", "Honda Amaze")
        val defaultVariant = DropdownOption("1.2_S_CVT", "1.2 S CVT (1198CC)")
        val defaultFuel = DropdownOption("PETROL", "Petrol")
        val defaultRto = DropdownOption("MH-01", "Mumbai - MH01")
        val defaultInsurer = DropdownOption("ACKO", "Acko General Insurance")

        _uiState.update { current ->
            current.copy(
                isNewCar = isNewCar,
                policyTypeOptions = policyTypes,
                selectedPolicyType = policyTypes.firstOrNull().orEmpty(),
                // Pre-fill realistic values matching design mockup
                customerName = "Nida Hussaini",
                selectedRto = defaultRto,
                ownershipType = OwnershipType.COMPANY,
                selectedModel = defaultModel,
                selectedVariant = defaultVariant,
                selectedFuel = defaultFuel,
                registrationDate = "22/08/2018",
                manufacturingDate = "04/09/2017",
                hasExistingPolicy = true,
                selectedInsurer = defaultInsurer,
                policyExpiryDate = "22/08/2027",
                hasClaim = true,
                selectedNcb = DropdownOption("0", "0%"),
                consentGiven = true
            )
        }
    }

    fun onAction(action: NewCarAction) {
        when (action) {
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
                _uiState.update { it.copy(selectedPolicyType = action.policyType) }
            }

            is NewCarAction.OnExistingPolicyToggle -> {
                _uiState.update { it.copy(hasExistingPolicy = action.hasExisting) }
            }

            is NewCarAction.OnInsurerSelected -> {
                _uiState.update { it.copy(selectedInsurer = action.option) }
            }

            is NewCarAction.OnModelSelected -> {
                val availableVariants = CarInsuranceData.variantsByModel[action.option.value]
                    ?: CarInsuranceData.defaultVariants
                _uiState.update {
                    it.copy(
                        selectedModel = action.option,
                        variantOptions = availableVariants,
                        selectedVariant = availableVariants.firstOrNull()
                    )
                }
            }

            is NewCarAction.OnVariantSelected -> {
                _uiState.update { it.copy(selectedVariant = action.option) }
            }

            is NewCarAction.OnFuelSelected -> {
                _uiState.update { it.copy(selectedFuel = action.option) }
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
                _uiState.update { it.copy(hasClaim = action.hasClaim) }
            }

            is NewCarAction.OnNcbSelected -> {
                _uiState.update { it.copy(selectedNcb = action.option) }
            }

            is NewCarAction.OnConsentChange -> {
                _uiState.update { it.copy(consentGiven = action.isConsentGiven) }
            }

            is NewCarAction.OnNoticeForConsentClick -> {
                viewModelScope.launch {
                    _uiEvent.emit(NewCarUiEvent.OpenUrl("https://www.policyboss.com/consent"))
                }
            }

            is NewCarAction.OnBackClick -> {
                viewModelScope.launch {
                    _uiEvent.emit(NewCarUiEvent.NavigateBack)
                }
            }

            is NewCarAction.OnGetQuotesClick -> {
                handleGetQuotes()
            }
        }
    }

    private fun handleGetQuotes() {
        val state = _uiState.value
        if (!state.isFormValid) {
            viewModelScope.launch {
                _uiEvent.emit(NewCarUiEvent.ShowSnackbar("Please fill all mandatory fields and give consent"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val request = CarQuoteRequest(
                customerName = state.customerName,
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

            when (val result = repository.getCarQuotes(request)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.emit(
                        NewCarUiEvent.NavigateToQuotes(
                            quoteId = result.data.quoteId ?: "QUOTE_DEFAULT",
                            quoteUrl = result.data.quoteUrl
                        )
                    )
                    _uiEvent.emit(
                        NewCarUiEvent.ShowSnackbar("Fetching ${result.data.quotesCount ?: 8} best quotes for you...")
                    )
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                    _uiEvent.emit(NewCarUiEvent.ShowSnackbar(result.message))
                }
            }
        }
    }
}
