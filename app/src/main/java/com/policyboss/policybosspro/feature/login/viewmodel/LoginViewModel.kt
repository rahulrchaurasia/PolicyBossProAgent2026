package com.policyboss.policybosspro.feature.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.datastore.AppDataManager
import com.policyboss.policybosspro.core.repository.login.LoginRepository
import com.policyboss.policybosspro.feature.login.model.login.LoginEvent
import com.policyboss.policybosspro.feature.login.model.login.LoginUiState
import com.policyboss.policybosspro.feature.login.model.verifyAccount.VerifyOtpSource
import com.policyboss.policybosspro.feature.login.util.LoginIdentifierValidator
import com.policyboss.policybosspro.feature.login.util.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val appDataManager: AppDataManager
) : ViewModel() {

    // =====================================
    // UI STATE
    // =====================================
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    // =====================================
    // EVENTS
    // =====================================
    private val _event = MutableSharedFlow<LoginEvent>()
    val event = _event.asSharedFlow()

    // =====================================
    // IDENTIFIER CHANGE
    // =====================================
    fun onLoginIdChange(value: String) {
        _uiState.update {
            it.copy(
                loginId = value,
                isError = false,
                errorMessage = null
            )
        }
    }

    /** Backward-compatible alias for existing callers */
    fun onMobileChange(value: String) {
        onLoginIdChange(value)
    }

    // =====================================
    // SEND OTP (POST /postservicecall/otp_login)
    // =====================================
    fun onSendOtpClick() {
        if (_uiState.value.isLoading) return

        val enteredIdentifier = _uiState.value.loginId.trim()

        // 1. Validate Identifier (Mobile, Email, POSP No, ERP Code)
        when (val validationResult = LoginIdentifierValidator.validate(enteredIdentifier)) {
            is ValidationResult.Error -> {
                _uiState.update {
                    it.copy(
                        isError = true,
                        errorMessage = validationResult.message
                    )
                }
                return
            }
            is ValidationResult.Success -> {
                executeLoginFlow(validationResult.sanitizedId)
            }
        }
    }

    /** Backward-compatible alias for existing login action */
    fun onLoginClick() {
        onSendOtpClick()
    }

    private fun executeLoginFlow(sanitizedId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMessage = null
                )
            }

            // Step 1: Initiate Login via POST /postservicecall/otp_login
            val loginResult = loginRepository.initiateLogin(sanitizedId)

            loginResult.onSuccess { userMsg ->
                val fullName = userMsg.name.orEmpty()
                val mobileReturned = userMsg.mobileNo?.takeIf { it.isNotBlank() }
                    ?: (if (sanitizedId.all { it.isDigit() } && sanitizedId.length == 10) sanitizedId else "")
                val ssId = userMsg.ssIdString.orEmpty()
                val emailId = userMsg.emailId.orEmpty()

                // Retain session data in DataStore atomically
                appDataManager.saveUserSession(
                    name = fullName.takeIf { it.isNotBlank() },
                    mobile = mobileReturned.takeIf { it.isNotBlank() },
                    email = emailId.takeIf { it.isNotBlank() },
                    ssId = ssId.takeIf { it.isNotBlank() }
                )

                _uiState.update { it.copy(isLoading = false) }

                // Step 2: Navigate directly to VerifyAccountScreen with original login identifier and SS ID
                _event.emit(
                    LoginEvent.NavigateToVerifyOtp(
                        fullName = fullName,
                        mobileNumber = mobileReturned,
                        loginInput = sanitizedId,
                        ssId = ssId,
                        emailId = emailId,
                        source = VerifyOtpSource.LOGIN
                    )
                )
            }.onFailure { loginError ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = loginError.message ?: "Login initiation failed. Please check your details."
                    )
                }
            }
        }
    }

    // =====================================
    // RESET LOGIN STATE
    // =====================================
    /**
     * Resets the Login UI state to its initial blank state.
     * Clears loginId, error flags, error messages, and loading state.
     * Called when returning from a successful POSP registration.
     */
    fun resetLoginState() {
        _uiState.value = LoginUiState()
    }
}
