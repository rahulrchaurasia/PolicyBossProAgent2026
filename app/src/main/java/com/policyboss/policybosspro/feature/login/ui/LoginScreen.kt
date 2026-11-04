package com.policyboss.policybosspro.feature.login.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.policyboss.policybosspro.feature.login.model.login.LoginEvent
import com.policyboss.policybosspro.feature.login.model.login.LoginUiState
import com.policyboss.policybosspro.feature.login.model.verifyAccount.VerifyOtpSource
import com.policyboss.policybosspro.feature.login.viewmodel.LoginViewModel
import com.policyboss.policybosspro.ui.ObserveAsEvents

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToVerifyOtp: (
        fullName: String,
        mobileNumber: String,
        loginInput: String,
        ssId: String,
        source: VerifyOtpSource
    ) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(
        flow = viewModel.event
    ) { event ->
        when (event) {
            is LoginEvent.NavigateToVerifyOtp -> {
                onNavigateToVerifyOtp(
                    event.fullName,
                    event.mobileNumber,
                    event.loginInput,
                    event.ssId,
                    event.source
                )
            }
            is LoginEvent.ShowSnackbar -> {
                // Handle snackbar if needed
            }
        }
    }

    LoginContent(
        uiState = uiState,
        onLoginIdChange = viewModel::onLoginIdChange,
        onSendOtpClick = viewModel::onSendOtpClick,
        onNavigateToRegister = onNavigateToRegister
    )
}

/**
 * Backward-compatible overload for 3-parameter callback.
 */
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToVerifyOtp: (
        fullName: String,
        mobileNumber: String,
        source: VerifyOtpSource
    ) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    LoginScreen(
        onNavigateToRegister = onNavigateToRegister,
        onNavigateToVerifyOtp = { fullName, mobileNumber, _, _, source ->
            onNavigateToVerifyOtp(fullName, mobileNumber, source)
        },
        viewModel = viewModel
    )
}

/**
 * Backward-compatible overload for legacy 2-parameter callback.
 */
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToVerifyOtp: (
        mobileNumber: String,
        source: VerifyOtpSource
    ) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    LoginScreen(
        onNavigateToRegister = onNavigateToRegister,
        onNavigateToVerifyOtp = { _, mobileNumber, _, _, source ->
            onNavigateToVerifyOtp(mobileNumber, source)
        },
        viewModel = viewModel
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun LoginScreenPreview() {
    LoginContent(
        uiState = LoginUiState(
            loginId = "9876543210"
        ),
        onLoginIdChange = {},
        onSendOtpClick = {},
        onNavigateToRegister = {}
    )
}
