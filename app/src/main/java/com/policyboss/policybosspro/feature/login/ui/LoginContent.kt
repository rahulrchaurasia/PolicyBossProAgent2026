package com.policyboss.policybosspro.feature.login.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.login.component.AuthFloatingIcon
import com.policyboss.policybosspro.feature.login.component.AuthHeaderPattern
import com.policyboss.policybosspro.feature.login.model.login.LoginUiState
import com.policyboss.policybosspro.ui.components.button.PrimaryCTAButton
import com.policyboss.policybosspro.ui.components.loading.CustomAppLoader
import com.policyboss.policybosspro.ui.components.textfield.AppOutlinedTextField
import com.policyboss.policybosspro.ui.theme.textPrimary
import com.policyboss.policybosspro.ui.theme.textSecondary




@Composable
fun LoginContent(
    uiState: LoginUiState,
    onLoginIdChange: (String) -> Unit,
    onSendOtpClick: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        AuthHeaderPattern()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.78f)
                .align(Alignment.BottomCenter)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(
                        topStart = 32.dp,
                        topEnd = 32.dp
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .navigationBarsPadding()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
                    .padding(top = 56.dp)
            ) {
                Text(
                    text = "Login to your account",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.textPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Enter your registered Mobile No., Email ID, POSP No. or ERP code.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.textSecondary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Mobile No. / Email ID / POSP No. / ERP Code",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.textPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Flexible input field allowing unconstrained typing for all identifier types
                AppOutlinedTextField(
                    value = uiState.loginId,
                    onValueChange = onLoginIdChange, // Updates state locally without triggering network/heavy validation
                    placeholder = "Enter your input",
                    keyboardType = KeyboardType.Text,
                    enabled = !uiState.isLoading,
                    isError = uiState.isError,
                    errorMessage = uiState.errorMessage,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
//                            if (!uiState.isLoading) {
//                                onSendOtpClick()
//                            }
                        }
                    ),
                    leadingContent = null
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Don't have an account?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.textSecondary
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Create account",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.clickable {
                            if (!uiState.isLoading) {
                                onNavigateToRegister()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                PrimaryCTAButton(
                    text = if (uiState.isLoading) "Sending OTP..." else "Send OTP",
                    enabled = !uiState.isLoading,
                    onClick = {
                        keyboardController?.hide()
                        if (!uiState.isLoading) {
                            onSendOtpClick()
                        }
                    }
                )

                Spacer(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .height(24.dp)
                )
            }
        }

        AuthFloatingIcon(
            modifier = Modifier.align(Alignment.BottomCenter),
            iconRes = R.drawable.ic_login
        )

        if (uiState.isLoading) {
            CustomAppLoader(showBackground = true)
        }
    }
}
/**
 * Backward-compatible overload for callers still using onMobileChange parameter.
 */
@Deprecated(
    message = "Use onLoginIdChange instead of onMobileChange to support all login identifier types",
    replaceWith = ReplaceWith("LoginContent(uiState, onLoginIdChange = onMobileChange, onSendOtpClick = onSendOtpClick, onNavigateToRegister = onNavigateToRegister, modifier = modifier)")
)
@Composable
fun LoginContent(
    uiState: LoginUiState,
    onMobileChange: (String) -> Unit,
    onSendOtpClick: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier,
    @Suppress("UNUSED_PARAMETER") legacyFlag: Boolean = true
) {
    LoginContent(
        uiState = uiState,
        onLoginIdChange = onMobileChange,
        onSendOtpClick = onSendOtpClick,
        onNavigateToRegister = onNavigateToRegister,
        modifier = modifier
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun LoginContentPreview() {
    LoginContent(
        uiState = LoginUiState(
            loginId = "9876543210"
        ),
        onLoginIdChange = {},
        onSendOtpClick = {},
        onNavigateToRegister = {}
    )
}
