package com.policyboss.policybosspro.feature.register.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.policyboss.policybosspro.feature.register.model.RegisterEvent
import com.policyboss.policybosspro.feature.register.model.RegisterUiState
import com.policyboss.policybosspro.feature.register.ui.components.RegistrationSuccessContent
import com.policyboss.policybosspro.feature.register.ui.components.RegistrationSuccessDialog
import com.policyboss.policybosspro.ui.ObserveAsEvents
import com.policyboss.policybosspro.ui.components.datePicker.AppDatePickerDialog
import com.policyboss.policybosspro.ui.components.datePicker.DateConstraint
import com.policyboss.policybosspro.ui.components.loading.CustomAppLoader
import com.policyboss.policybosspro.ui.theme.PolicyBossTheme
import kotlinx.coroutines.delay

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onRegistrationSuccess: (String) -> Unit = {},
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var hasNavigated by remember { mutableStateOf(false) }

    val triggerSuccessNavigation: () -> Unit = {
        if (!hasNavigated) {
            hasNavigated = true
            onRegistrationSuccess(uiState.registeredFbaId.orEmpty())
        }
    }

    ObserveAsEvents(flow = viewModel.event) { event ->
        when (event) {
            is RegisterEvent.NavigateToLogin -> {
                onBackClick()
            }
            is RegisterEvent.RegistrationSuccess -> {
                // UI state `isSuccess = true` drives the Lottie animation and automatic navigation
            }
            is RegisterEvent.ShowToast -> {
                // Toast or snackbar handling if needed
            }
            is RegisterEvent.FocusField -> {
                // Handled internally by RegisterContent
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. Base content: Registration Form
        RegisterContent(
            uiState = uiState,
            eventFlow = viewModel.event,
            onFullNameChange = viewModel::onFullNameChange,
            onMobileChange = viewModel::onMobileChange,
            onEmailChange = viewModel::onEmailChange,
            onDobClick = { viewModel.onToggleDatePicker(true) },
            onPincodeChange = viewModel::onPincodeChange,
            onCityChange = viewModel::onCityChange,
            onPanChange = viewModel::onPanChange,
            onGenderChange = viewModel::onGenderChange,
            onSubscriptionChange = viewModel::onSubscriptionChange,
            onExistingAgentChange = viewModel::onExistingAgentChange,
            onSubmitClick = viewModel::onSubmitClick,
            onLoginClick = onBackClick,
            onBackClick = onBackClick
        )

        // Date of Birth Dialog
        if (uiState.isDatePickerVisible) {
            AppDatePickerDialog(
                dateConstraint = DateConstraint.PastOnly,
                onDateSelected = viewModel::onDobChange,
                onDismiss = { viewModel.onToggleDatePicker(false) }
            )
        }

        // Fallback Success Dialog if explicitly toggled
        if (uiState.isSuccessDialogVisible) {
            RegistrationSuccessDialog(
                fbaId = uiState.registeredFbaId,
                message = uiState.successMessage,
                onDismiss = viewModel::onDismissSuccessDialog
            )
        }

        // 2. Fullscreen Loading Overlay
        if (uiState.isLoading) {
            CustomAppLoader(showBackground = true)
        }

        // 3. Foreground Success Overlay with Lottie Animation
        if (uiState.isSuccess) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.98f))
                    .zIndex(10f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {} // Intercept all touch events to disable background form interaction
            ) {
                RegistrationSuccessContent(
                    fbaId = uiState.registeredFbaId,
                    message = uiState.successMessage,
                    onContinueClick = triggerSuccessNavigation
                )
            }

            // Automatic navigation timer (2.5 seconds)
            LaunchedEffect(Unit) {
                delay(2500)
                triggerSuccessNavigation()
            }
        }
    }
}

@Preview(name = "Register Screen Preview", showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    PolicyBossTheme {
        RegisterContent(
            uiState = RegisterUiState(
                fullName = "Rahul Chaurasia",
                mobile = "9773113793",
                email = "rahul.chaurasia@policyboss.com",
                dob = "24/01/1988",
                pincode = "400063",
                city = "Mumbai",
                pan = "HPFOO4389L",
                gender = "M",
                subscriptionType = "All-In-One(₹999)",
                isExistingAgent = false
            ),
            onFullNameChange = {},
            onMobileChange = {},
            onEmailChange = {},
            onDobClick = {},
            onPincodeChange = {},
            onCityChange = {},
            onPanChange = {},
            onGenderChange = {},
            onSubscriptionChange = {},
            onExistingAgentChange = {},
            onSubmitClick = {}
        )
    }
}
