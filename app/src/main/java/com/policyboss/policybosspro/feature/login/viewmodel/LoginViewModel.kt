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

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<LoginEvent>()
    val event = _event.asSharedFlow()

    fun onLoginIdChange(value: String) {
        _uiState.update {
            it.copy(
                loginId = value,
                isError = false,
                errorMessage = null
            )
        }
    }

    fun onMobileChange(value: String) {
        onLoginIdChange(value)
    }

    fun onSendOtpClick() {
        if (_uiState.value.isLoading) return

        val enteredIdentifier = _uiState.value.loginId.trim()

        if (enteredIdentifier.isBlank()) {
            _uiState.update {
                it.copy(
                    isError = true,
                    errorMessage = "Please enter your Mobile No., Email ID, POSP No., or ERP Code"
                )
            }
            return
        }

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

    private fun executeLoginFlow(sanitizedId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMessage = null
                )
            }

            val loginResult = loginRepository.initiateLogin(sanitizedId)

            loginResult.onSuccess { userMsg ->
                val fullName = (userMsg.effectiveName ?: userMsg.name).orEmpty()

                // CRITICAL EXTRACT: Always grab the canonical Mobile_No returned from otp_login response
                val canonicalMobile = (userMsg.effectiveMobileNo ?: userMsg.mobileNo)?.trim().orEmpty()
                val mobileReturned = if (canonicalMobile.isNotBlank()) {
                    canonicalMobile
                } else if (sanitizedId.all { it.isDigit() } && sanitizedId.length == 10) {
                    sanitizedId
                } else {
                    ""
                }

                val ssId = userMsg.ssIdString.orEmpty()
                val emailId = (userMsg.effectiveEmailId ?: userMsg.emailId).orEmpty()

                appDataManager.saveUserSession(
                    name = fullName.takeIf { it.isNotBlank() },
                    mobile = mobileReturned.takeIf { it.isNotBlank() },
                    email = emailId.takeIf { it.isNotBlank() },
                    ssId = ssId.takeIf { it.isNotBlank() }
                )

                _uiState.update { it.copy(isLoading = false) }

                _event.emit(
                    LoginEvent.NavigateToVerifyOtp(
                        fullName = fullName,
                        mobileNumber = mobileReturned, // Passes canonical Mobile_No to Verify screen
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

    fun resetLoginState() {
        _uiState.value = LoginUiState()
    }
}
