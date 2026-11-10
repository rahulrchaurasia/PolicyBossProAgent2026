package com.policyboss.policybosspro.feature.login.ui




import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.policyboss.policybosspro.navigation.Dest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.policyboss.policybosspro.ui.theme.AppTypography


import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.policyboss.policybosspro.feature.login.model.verifyAccount.VerifyOtpEvent
import com.policyboss.policybosspro.ui.ObserveAsEvents
import com.policyboss.policybosspro.feature.login.component.verifyAccount.OTPFieldState

import com.policyboss.policybosspro.feature.login.model.verifyAccount.VerifyOtpSource
import com.policyboss.policybosspro.feature.login.viewmodel.VerifyOtpViewModel

import com.policyboss.policybosspro.navigation.AppNavigator

import com.policyboss.policybosspro.ui.components.loading.CustomAppLoader
import com.policyboss.policybosspro.ui.theme.LightColorScheme
import com.policyboss.policybosspro.utils.AppFormatter

//*********************************************************************************
//** So pass AppNavigator to Screen is recommended way.But its content view we have to used callback  *************************/
//*********************************************************************************
/*
    THE REAL INDUSTRY STANDARD TODAY
    LEVEL 1 — PURE UI COMPONENTS

    Use callbacks only.

    Example:

    LoginContent(
        onRegisterClick = {}
    )

    Correct.

    Because this UI should:

    preview easily
    test easily
    be reusable
    LEVEL 2 — SCREEN COMPOSABLES

    This is where industry differs.

    Two common patterns exist:

    Pattern	Common In
    Pass navigator	Medium/Large apps
    Pass grouped actions	Google samples / strict clean architecture

    Both are valid.

    THE IMPORTANT DISTINCTION
    THIS IS BAD
    OTPTextField(
        navigator = navigator
    )

    UI component navigation aware ❌

    THIS IS ACCEPTABLE
    VerifyAccountScreen(
        navigator = navigator
    )

    because Screen is already:

    navigation-aware
    ViewModel-aware
    lifecycle-aware

    This is NOT reusable UI anymore.This is orchestration layer.

    Big difference. YOUR CURRENT STRUCTURE
    You already separated:VerifyAccountScreen  VerifyAccountContent

    This is excellent.

    Now: Content  Pure UI.
    Screen  Coordinator/orchestrator.

    That is already clean architecture.

    SO IS PASSING AppNavigator TO SCREEN WRONG?
    NO

    This is the most misunderstood Compose topic.

    Passing navigator to SCREEN level is completely acceptable in many production apps.
 */

@Composable
fun VerifyAccountScreen(
    navigator: AppNavigator,
    fullName: String,
    mobileNumber: String,
    source : VerifyOtpSource,
    loginInput: String = mobileNumber,
    ssId: String = "",
    viewModel: VerifyOtpViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(mobileNumber, loginInput, ssId, fullName) {
        viewModel.initVerifySession(
            mobile = mobileNumber,
            loginInput = loginInput,
            ssId = ssId,
            fullName = fullName
        )
    }

    // =====================================
    // OBSERVE EVENTS
    // =====================================

    ObserveAsEvents(

        flow = viewModel.event
    ){event ->

        when(event){
            is VerifyOtpEvent.NavigateToLogin -> {

                navigator.navigateToLoginAndClear()
            }

            is VerifyOtpEvent.NavigateToWelcomeSyncContact -> {
                navigator.navigateTo(Dest.WelcomeSyncContact) {
                    popUpTo<Dest.Login> {
                        inclusive = true
                    }
                }
            }

            is VerifyOtpEvent.NavigateToHome -> {
                navigator.navigateToHome()
            }
        }
    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // =====================================
        // MAIN CONTENT
        // =====================================

        VerifyAccountContent(

            mobileNumber = AppFormatter.maskMobile(mobileNumber),

            otpText = uiState.otp,

            otpLength = 4,

            fieldState = uiState.fieldState,

            errorMessage = uiState.errorMessage,

            resendMessage = uiState.resendMessage,

            resendSeconds = uiState.resendSeconds,

            isVerifyingOtp = uiState.isVerifyingOtp,

            isResendingOtp = uiState.isResendingOtp,

            onOtpChange = { value, isComplete ->

                viewModel.onOtpChange(
                    otp = value,
                    isComplete = isComplete,
                    source = source,
                    mobile = mobileNumber,
                    loginInput = loginInput,
                    ssId = ssId,
                    fullName = fullName
                )
            },

            onResendClick = {
                viewModel.resendOtp(
                    loginInput = loginInput,
                    mobile = mobileNumber
                )
            }
        )

        // =====================================
        // GLOBAL FULLSCREEN LOADER
        // =====================================

        if (uiState.isVerifyingOtp) {

            CustomAppLoader(
                showBackground = true
            )
        }
    }
}



@Preview(
    name = "Verify Account Screen - Success",
    showBackground = true,
    showSystemUi = true
)

@Preview(
    name = "Verify Account Screen - Error",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun VerifyAccountScreenPreview_Error() {

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography
    ) {

        VerifyAccountContent(

            mobileNumber = "+91 70450 *****",

            otpText = "1234",

            otpLength = 4,

            fieldState = OTPFieldState.ERROR,

            errorMessage = "Invalid code",

            resendMessage = null,

            resendSeconds = 52,

            isVerifyingOtp = false,

            isResendingOtp = false,

            onOtpChange = { _, _ -> },

            onResendClick = {}
        )
    }
}