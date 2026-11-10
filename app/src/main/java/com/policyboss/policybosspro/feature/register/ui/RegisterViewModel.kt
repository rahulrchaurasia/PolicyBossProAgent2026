package com.policyboss.policybosspro.feature.register.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.datastore.AppDataManager
import com.policyboss.policybosspro.core.repository.register.RegisterRepository
import com.policyboss.policybosspro.feature.register.data.model.CheckPospDataRequest
import com.policyboss.policybosspro.feature.register.data.model.RegistrationRequest
import com.policyboss.policybosspro.feature.register.model.RegisterEvent
import com.policyboss.policybosspro.feature.register.model.RegisterUiState
import com.policyboss.policybosspro.feature.register.util.NameSplitter
import com.policyboss.policybosspro.feature.register.util.RegisterValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerRepository: RegisterRepository,
    private val appDataManager: AppDataManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<RegisterEvent>()
    val event = _event.asSharedFlow()

    fun onFullNameChange(name: String) {
        _uiState.update { current ->
            val updatedErrors = current.fieldErrors - "fullName"
            current.copy(
                fullName = name,
                fieldErrors = updatedErrors,
                generalError = null
            )
        }
    }

    fun onMobileChange(mobile: String) {
        val filtered = mobile.filter { it.isDigit() }.take(10)
        _uiState.update { current ->
            val updatedErrors = current.fieldErrors - "mobile"
            current.copy(
                mobile = filtered,
                fieldErrors = updatedErrors,
                generalError = null
            )
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update { current ->
            val updatedErrors = current.fieldErrors - "email"
            current.copy(
                email = email.trim(),
                fieldErrors = updatedErrors,
                generalError = null
            )
        }
    }

    fun onDobChange(dob: String) {
        _uiState.update { current ->
            val updatedErrors = current.fieldErrors - "dob"
            current.copy(
                dob = dob,
                isDatePickerVisible = false,
                fieldErrors = updatedErrors,
                generalError = null
            )
        }
    }

    fun onToggleDatePicker(show: Boolean) {
        _uiState.update { it.copy(isDatePickerVisible = show) }
    }

    fun onPincodeChange(pincode: String) {
        val filtered = pincode.filter { it.isDigit() }.take(6)
        _uiState.update { current ->
            val updatedErrors = current.fieldErrors - "pincode"
            current.copy(
                pincode = filtered,
                fieldErrors = updatedErrors,
                generalError = null
            )
        }
    }

    fun onCityChange(city: String) {
        _uiState.update { current ->
            val updatedErrors = current.fieldErrors - "city"
            current.copy(
                city = city,
                fieldErrors = updatedErrors,
                generalError = null
            )
        }
    }

    fun onPanChange(pan: String) {
        val normalized = pan.uppercase().take(10)
        _uiState.update { current ->
            val updatedErrors = current.fieldErrors - "pan"
            current.copy(
                pan = normalized,
                fieldErrors = updatedErrors,
                generalError = null
            )
        }
    }

    fun onGenderChange(gender: String) {
        _uiState.update { current ->
            val updatedErrors = current.fieldErrors - "gender"
            current.copy(
                gender = gender,
                fieldErrors = updatedErrors,
                generalError = null
            )
        }
    }

    fun onSubscriptionChange(subscription: String) {
        _uiState.update { current ->
            val updatedErrors = current.fieldErrors - "subscriptionType" - "subscription"
            current.copy(
                subscriptionType = subscription,
                fieldErrors = updatedErrors,
                generalError = null
            )
        }
    }

    fun onExistingAgentChange(isExisting: Boolean) {
        _uiState.update { current ->
            val updatedErrors = current.fieldErrors - "existingAgent"
            current.copy(
                isExistingAgent = isExisting,
                fieldErrors = updatedErrors,
                generalError = null
            )
        }
    }

    fun onDismissSuccessDialog() {
        _uiState.update { it.copy(isSuccessDialogVisible = false) }
        viewModelScope.launch {
            _event.emit(RegisterEvent.NavigateToLogin)
        }
    }

    /**
     * Sequential form validation adhering to the exact required order:
     * 1. Full Name
     * 2. Mobile
     * 3. Email
     * 4. DOB
     * 5. Pincode
     * 6. City (Mandatory)
     * 7. PAN
     * 8. Gender
     * 9. Subscription Type
     * 10. Existing Insurance Agent/POSP selection (Yes/No)
     *
     * Halts immediately on the first invalid field so that errors appear sequentially.
     */
    fun validateRegistration(): Boolean {
        val state = _uiState.value

        val error = RegisterValidator.validateSequential(
            fullName = state.fullName,
            mobile = state.mobile,
            email = state.email,
            dob = state.dob,
            pincode = state.pincode,
            city = state.city,
            pan = state.pan,
            gender = state.gender,
            subscriptionType = state.subscriptionType,
            isExistingAgent = state.isExistingAgent
        )

        return if (error != null) {
            _uiState.update {
                it.copy(
                    fieldErrors = mapOf(error.first to error.second),
                    generalError = null,
                    validationErrorTrigger = System.currentTimeMillis()
                )
            }
            viewModelScope.launch {
                _event.emit(RegisterEvent.FocusField(error.first))
            }
            false
        } else {
            _uiState.update {
                it.copy(
                    fieldErrors = emptyMap(),
                    generalError = null
                )
            }
            true
        }
    }

    fun onSubmitClick() {
        if (_uiState.value.isLoading || _uiState.value.isSuccess) return

        if (!validateRegistration()) {
            return
        }

        if (USE_DUMMY_FLOW) {
            executeDummyRegistrationFlow(_uiState.value)
        } else {
            executeRegistrationFlow(_uiState.value)
        }
    }

    /**
     * Dummy registration flow:
     * - No live API call is made.
     * - Delays for 3 seconds to simulate network submission.
     * - Emits registration success and displays Lottie success state.
     */
    fun executeDummyRegistrationFlow(state: RegisterUiState = _uiState.value) {
        if (_uiState.value.isSuccess) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    loadingMessage = "Submitting registration...",
                    generalError = null,
                    fieldErrors = emptyMap()
                )
            }

            // 3-second simulation delay
            delay(3000.milliseconds)

            val dummyFbaId = "150252"
            val dummyMessage = "Registration completed successfully! Welcome to PolicyBoss Pro."

            // Save user session in DataStore
            appDataManager.saveUserSession(
                name = state.fullName.trim(),
                mobile = state.mobile.trim(),
                email = state.email.trim(),
                fbaId = dummyFbaId,
                ssId = dummyFbaId
            )
            appDataManager.saveLoginState(true)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    loadingMessage = null,
                    registeredFbaId = dummyFbaId,
                    successMessage = dummyMessage,
                    isSuccess = true,
                    isSuccessDialogVisible = false
                )
            }

            _event.emit(RegisterEvent.RegistrationSuccess(fbaId = dummyFbaId, message = dummyMessage))
        }
    }

    private fun executeRegistrationFlow(state: RegisterUiState) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    loadingMessage = "Checking data availability...",
                    generalError = null,
                    fieldErrors = emptyMap()
                )
            }

            // Step 1: Check POSP Data Availability (Email, Mobile, PAN)
            val availabilityRequest = CheckPospDataRequest(
                email = state.email.trim(),
                mobile = state.mobile.trim(),
                pan = state.pan.trim().uppercase()
            )

            val availabilityResult = registerRepository.checkDataAvailability(availabilityRequest)

            availabilityResult.onSuccess {
                // Step 2: Prepare Registration Submission
                _uiState.update { it.copy(loadingMessage = "Submitting registration...") }

                val (firstName, lastName) = NameSplitter.splitFullName(state.fullName)
                val apiDob = formatDobForApi(state.dob)
                val salesUid = appDataManager.getUserSsId().ifBlank { "" }

                val registrationRequest = RegistrationRequest(
                    FirstName = firstName,
                    LastName = lastName,
                    Mobile_1 = state.mobile.trim(),
                    EmailId = state.email.trim(),
                    DOB = apiDob,
                    PinCode = state.pincode.trim(),
                    City = state.city.trim(),
                    Posp_PAN = state.pan.trim().uppercase(),
                    Gender = state.gender,
                    Source = state.subscriptionType.ifBlank { "All-In-One(₹999)" },
                    field_sales_uid = salesUid
                )

                // Step 3: Submit Registration
                val submitResult = registerRepository.submitRegistration(registrationRequest)

                submitResult.onSuccess { response ->
                    val fbaId = response.MasterData?.fbaId ?: ""
                    val message = response.displayMessage

                    // Retain registered profile data in DataStore
                    appDataManager.saveUserSession(
                        name = state.fullName.trim(),
                        mobile = state.mobile.trim(),
                        email = state.email.trim(),
                        fbaId = fbaId.takeIf { it.isNotBlank() },
                        ssId = fbaId.takeIf { it.isNotBlank() }
                    )
                    appDataManager.saveLoginState(true)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loadingMessage = null,
                            registeredFbaId = fbaId,
                            successMessage = message,
                            isSuccess = true,
                            isSuccessDialogVisible = false
                        )
                    }

                    _event.emit(RegisterEvent.RegistrationSuccess(fbaId = fbaId, message = message))
                }.onFailure { submitError ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loadingMessage = null,
                            generalError = submitError.message ?: "Registration submission failed. Please try again."
                        )
                    }
                }

            }.onFailure { availabilityError ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loadingMessage = null,
                        generalError = availabilityError.message ?: "The entered details already exist in PolicyBoss."
                    )
                }
            }
        }
    }

    private fun formatDobForApi(dob: String): String {
        return try {
            if (dob.contains("/")) {
                val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = inputFormat.parse(dob)
                if (date != null) outputFormat.format(date) else dob
            } else {
                dob
            }
        } catch (e: Exception) {
            dob
        }
    }

    companion object {
        /**
         * Toggle to switch between dummy registration flow (3s delay, no API call)
         * and live API execution. Defaults to true as requested.
         */
        var USE_DUMMY_FLOW: Boolean = true
    }
}

