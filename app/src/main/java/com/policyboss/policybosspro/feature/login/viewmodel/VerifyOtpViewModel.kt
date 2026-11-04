package com.policyboss.policybosspro.feature.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.datastore.AppDataManager
import com.policyboss.policybosspro.core.repository.login.LoginRepository
import com.policyboss.policybosspro.feature.login.component.verifyAccount.OTPFieldState
import com.policyboss.policybosspro.feature.login.model.verifyAccount.VerifyOtpEvent
import com.policyboss.policybosspro.feature.login.model.verifyAccount.VerifyOtpSource
import com.policyboss.policybosspro.feature.login.model.verifyAccount.VerifyOtpUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyOtpViewModel @Inject constructor(
    private val appDataManager: AppDataManager,
    private val loginRepository: LoginRepository
) : ViewModel() {

    // =========================================
    // STATE & TRACKING
    // =========================================
    private val _uiState = MutableStateFlow(VerifyOtpUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<VerifyOtpEvent>()
    val event = _event.asSharedFlow()

    private var timerJob: Job? = null

    private var currentSsId: String = ""
    private var currentLoginInput: String = ""
    private var currentFullName: String = ""

    init {
        startResendTimer()
    }

    fun getSsid(): String = currentSsId

    // =========================================
    // OTP CHANGE
    // =========================================
    fun onOtpChange(
        otp: String,
        isComplete: Boolean,
        source: VerifyOtpSource,
        mobile: String,
        loginInput: String = mobile,
        ssId: String = "",
        fullName: String = ""
    ) {
        if (otp.length > 4) return

        if (loginInput.isNotBlank()) {
            currentLoginInput = loginInput
        }
        if (ssId.isNotBlank()) {
            currentSsId = ssId
        }
        if (fullName.isNotBlank()) {
            currentFullName = fullName
        }

        _uiState.update {
            it.copy(
                otp = otp,
                fieldState = OTPFieldState.DEFAULT,
                errorMessage = null
            )
        }

        // Auto-verify when 4 digits are complete
        if (isComplete && !_uiState.value.isVerifyingOtp) {
            verifyOtp(
                otp = otp,
                source = source,
                mobile = mobile
            )
        }
    }

    // =========================================
    // VERIFY OTP (API 2)
    // =========================================
    private fun verifyOtp(
        otp: String,
        source: VerifyOtpSource,
        mobile: String
    ) {
        if (otp.length < 4) {
            _uiState.update {
                it.copy(
                    fieldState = OTPFieldState.ERROR,
                    errorMessage = "Please enter the complete 4-digit code"
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isVerifyingOtp = true,
                    errorMessage = null
                )
            }

            val targetLoginInput = currentLoginInput.ifBlank { mobile }

            loginRepository.otpVerifyHorizon(
                otp = otp.trim(),
                loginInput = targetLoginInput.trim()
            ).catch { e ->
                _uiState.update {
                    it.copy(
                        isVerifyingOtp = false,
                        fieldState = OTPFieldState.ERROR,
                        errorMessage = e.message ?: "Invalid code. Please try again."
                    )
                }
            }.collect { response ->
                if (response.isSuccessful && response.body()?.msg?.equals("SUCCESS", ignoreCase = true) == true) {
                    val ssid = getSsid()
                    if (ssid.isNotBlank()) {
                        getLoginDetailHorizon(
                            ssId = ssid,
                            source = source,
                            mobile = mobile,
                            fullName = currentFullName
                        )
                    } else {
                        _uiState.update {
                            it.copy(
                                isVerifyingOtp = false,
                                fieldState = OTPFieldState.ERROR,
                                errorMessage = "SSID not generated. Please contact admin"
                            )
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isVerifyingOtp = false,
                            fieldState = OTPFieldState.ERROR,
                            errorMessage = "Invalid code. Please try again."
                        )
                    }
                }
            }
        }
    }

    // =========================================
    // LOGIN DETAIL FLOW (SS ID)
    // =========================================
    private suspend fun getLoginDetailHorizon(
        ssId: String,
        source: VerifyOtpSource,
        mobile: String,
        fullName: String
    ) {
        val detailResult = loginRepository.getLoginDetailHorizon(ssId)
        detailResult.onSuccess {
            pauseTimer()

            _uiState.update {
                it.copy(
                    isVerifyingOtp = false,
                    fieldState = OTPFieldState.SUCCESS,
                    isOtpVerified = true
                )
            }

            delay(500)

            onVerifyOtpSuccess(
                source = source,
                mobile = mobile,
                ssId = ssId,
                fullName = fullName
            )
        }.onFailure { error ->
            _uiState.update {
                it.copy(
                    isVerifyingOtp = false,
                    fieldState = OTPFieldState.ERROR,
                    errorMessage = error.message ?: "Failed to retrieve login details"
                )
            }
        }
    }

    private suspend fun onVerifyOtpSuccess(
        source: VerifyOtpSource,
        mobile: String,
        ssId: String,
        fullName: String
    ) {
        when (source) {
            VerifyOtpSource.LOGIN -> {
                appDataManager.saveUserSession(
                    mobile = mobile.takeIf { it.isNotBlank() },
                    name = fullName.takeIf { it.isNotBlank() },
                    ssId = ssId.takeIf { it.isNotBlank() },
                    isLogin = true
                )

                _event.emit(VerifyOtpEvent.NavigateToHome)
            }
            VerifyOtpSource.REGISTER -> {
                _event.emit(VerifyOtpEvent.NavigateToLogin)
            }
        }
    }

    // =========================================
    // RESEND OTP
    // =========================================
    fun resendOtp(
        loginInput: String = "",
        mobile: String = ""
    ) {
        if (_uiState.value.isVerifyingOtp || _uiState.value.isResendingOtp) return

        pauseTimer()

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isResendingOtp = true,
                    errorMessage = null,
                    resendMessage = null
                )
            }

            val target = loginInput.ifBlank { currentLoginInput.ifBlank { mobile } }

            val initiateResult = if (target.isNotBlank()) {
                loginRepository.initiateLogin(target)
            } else {
                Result.success(null)
            }

            initiateResult.onSuccess { userMsg ->
                if (userMsg != null) {
                    if (userMsg.ssId.toString().isNotBlank()) {
                        currentSsId = userMsg.ssId.toString().trim().removeSurrounding("\"")
                    }
                    if (!userMsg.name.isNullOrBlank()) {
                        currentFullName = userMsg.name
                    }
                }
                _uiState.update {
                    it.copy(
                        isResendingOtp = false,
                        otp = "",
                        fieldState = OTPFieldState.DEFAULT,
                        resendMessage = "OTP resent successfully",
                        resendSeconds = 120
                    )
                }
                startResendTimer()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isResendingOtp = false,
                        errorMessage = error.message ?: "Failed to resend OTP. Please try again."
                    )
                }
                resumeTimer()
            }
        }
    }

    // =========================================
    // TIMER CONTROLS
    // =========================================
    private fun startResendTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.resendSeconds > 0) {
                delay(1000)
                _uiState.update {
                    it.copy(resendSeconds = it.resendSeconds - 1)
                }
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
    }

    private fun resumeTimer() {
        if (_uiState.value.resendSeconds > 0) {
            startResendTimer()
        }
    }
}
