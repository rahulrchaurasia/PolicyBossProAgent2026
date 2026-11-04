package com.policyboss.policybosspro.feature.register.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.login.component.AuthFloatingIcon
import com.policyboss.policybosspro.feature.login.component.AuthHeaderPattern
import com.policyboss.policybosspro.feature.register.model.RegisterEvent
import com.policyboss.policybosspro.feature.register.model.RegisterUiState
import com.policyboss.policybosspro.feature.register.ui.components.AppStoreBadges
import com.policyboss.policybosspro.ui.ObserveAsEvents
import com.policyboss.policybosspro.ui.components.button.PrimaryCTAButton
import com.policyboss.policybosspro.ui.components.radio.AppRadioButtonOption
import com.policyboss.policybosspro.ui.components.textfield.AppOutlinedTextField
import com.policyboss.policybosspro.ui.components.textfield.PanTextField
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.PolicyBossTheme
import com.policyboss.policybosspro.ui.theme.border
import com.policyboss.policybosspro.ui.theme.textPrimary
import com.policyboss.policybosspro.ui.theme.textSecondary
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RegisterContent(
    uiState: RegisterUiState,
    eventFlow: SharedFlow<RegisterEvent>? = null,
    onFullNameChange: (String) -> Unit,
    onMobileChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onDobClick: () -> Unit,
    onPincodeChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onPanChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onSubscriptionChange: (String) -> Unit,
    onExistingAgentChange: (Boolean) -> Unit,
    onSubmitClick: () -> Unit,
    onLoginClick: () -> Unit = {},
    onBackClick: () -> Unit = onLoginClick,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val cardFraction = 0.82f

    // Focus requesters for input fields
    val fullNameFocusRequester = remember { FocusRequester() }
    val (emailFocusRequester, mobileFocusRequester, pincodeFocusRequester, cityFocusRequester, panFocusRequester) = remember {
        FocusRequester.createRefs()
    }

    // Bring into view requesters for auto-scrolling to error fields
    val fullNameBringIntoView = remember { BringIntoViewRequester() }
    val mobileBringIntoView = remember { BringIntoViewRequester() }
    val emailBringIntoView = remember { BringIntoViewRequester() }
    val dobBringIntoView = remember { BringIntoViewRequester() }
    val pincodeBringIntoView = remember { BringIntoViewRequester() }
    val cityBringIntoView = remember { BringIntoViewRequester() }
    val panBringIntoView = remember { BringIntoViewRequester() }
    val genderBringIntoView = remember { BringIntoViewRequester() }
    val subscriptionBringIntoView = remember { BringIntoViewRequester() }
    val existingAgentBringIntoView = remember { BringIntoViewRequester() }

    val focusAndScrollToField: (String) -> Unit = remember(coroutineScope) {
        { fieldName ->
            coroutineScope.launch {
                when (fieldName) {
                    "fullName" -> {
                        fullNameBringIntoView.bringIntoView()
                        try { fullNameFocusRequester.requestFocus() } catch (_: Exception) {}
                    }
                    "mobile" -> {
                        mobileBringIntoView.bringIntoView()
                        try { mobileFocusRequester.requestFocus() } catch (_: Exception) {}
                    }
                    "email" -> {
                        emailBringIntoView.bringIntoView()
                        try { emailFocusRequester.requestFocus() } catch (_: Exception) {}
                    }
                    "dob" -> {
                        dobBringIntoView.bringIntoView()
                    }
                    "pincode" -> {
                        pincodeBringIntoView.bringIntoView()
                        try { pincodeFocusRequester.requestFocus() } catch (_: Exception) {}
                    }
                    "city" -> {
                        cityBringIntoView.bringIntoView()
                        try { cityFocusRequester.requestFocus() } catch (_: Exception) {}
                    }
                    "pan" -> {
                        panBringIntoView.bringIntoView()
                        try { panFocusRequester.requestFocus() } catch (_: Exception) {}
                    }
                    "gender" -> {
                        genderBringIntoView.bringIntoView()
                    }
                    "subscriptionType", "subscription" -> {
                        subscriptionBringIntoView.bringIntoView()
                    }
                    "existingAgent" -> {
                        existingAgentBringIntoView.bringIntoView()
                    }
                }
            }
        }
    }

    if (eventFlow != null) {
        ObserveAsEvents(flow = eventFlow) { event ->
            if (event is RegisterEvent.FocusField) {
                focusAndScrollToField(event.fieldName)
            }
        }
    }

    LaunchedEffect(uiState.validationErrorTrigger) {
        if (uiState.validationErrorTrigger > 0L) {
            val firstError = uiState.fieldErrors.keys.firstOrNull()
            if (firstError != null) {
                focusAndScrollToField(firstError)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        // =========================================================
        // 1. AUTH HEADER PATTERN (Top Visual Branding)
        // =========================================================
        AuthHeaderPattern(
            heightFraction = 0.35f
        )

        // =========================================================
        // 2. TOP NAVIGATION (Back button over Header Pattern)
        // =========================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Login",
                    tint = Color.White
                )
            }
        }

        // =========================================================
        // 3. MAIN WHITE SURFACE CARD (Rounded top corners)
        // =========================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(cardFraction)
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
                // Screen Title and Subtitle matching CreateAccountContent
                Text(
                    text = "Become A Certified POSP",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.textPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Add following details to register with PolicyBoss",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.textSecondary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // General Error Banner (if any)
                if (!uiState.generalError.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppColors.AlertCardBackground)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = uiState.generalError,
                            color = AppColors.ErrorRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // =====================================================
                // 1. FULL NAME (Single Column)
                // =====================================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bringIntoViewRequester(fullNameBringIntoView)
                ) {
                    Text(
                        text = "Full name",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.textPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AppOutlinedTextField(
                        modifier = Modifier.focusRequester(fullNameFocusRequester),
                        value = uiState.fullName,
                        onValueChange = onFullNameChange,
                        placeholder = "Enter your full name",
                        isError = uiState.fieldErrors.containsKey("fullName"),
                        errorMessage = uiState.fieldErrors["fullName"],
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { mobileFocusRequester.requestFocus() }
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // =====================================================
                // 2. MOBILE NUMBER (With +91 Prefix, Single Column)
                // =====================================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bringIntoViewRequester(mobileBringIntoView)
                ) {
                    Text(
                        text = "Mobile number",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.textPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AppOutlinedTextField(
                        modifier = Modifier.focusRequester(mobileFocusRequester),
                        value = uiState.mobile,
                        onValueChange = onMobileChange,
                        placeholder = "Enter 10-digit mobile number",
                        isError = uiState.fieldErrors.containsKey("mobile"),
                        errorMessage = uiState.fieldErrors["mobile"],
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { emailFocusRequester.requestFocus() }
                        ),
                        leadingContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 16.dp)
                            ) {
                                Text(
                                    text = "+91",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AppColors.TextPrimary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Box(
                                    modifier = Modifier
                                        .height(22.dp)
                                        .width(1.dp)
                                        .background(MaterialTheme.colorScheme.border)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // =====================================================
                // 3. EMAIL ID (Single Column)
                // =====================================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bringIntoViewRequester(emailBringIntoView)
                ) {
                    Text(
                        text = "Email ID",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.textPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AppOutlinedTextField(
                        modifier = Modifier.focusRequester(emailFocusRequester),
                        value = uiState.email,
                        onValueChange = onEmailChange,
                        placeholder = "Enter your email address",
                        isError = uiState.fieldErrors.containsKey("email"),
                        errorMessage = uiState.fieldErrors["email"],
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.clearFocus()
                                onDobClick()
                            }
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // =====================================================
                // 4. DATE OF BIRTH (Single Column, Clickable)
                // =====================================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bringIntoViewRequester(dobBringIntoView)
                ) {
                    Text(
                        text = "Date of birth",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.textPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        AppOutlinedTextField(
                            value = uiState.dob,
                            onValueChange = {},
                            placeholder = "DD/MM/YYYY",
                            readOnly = true,
                            isError = uiState.fieldErrors.containsKey("dob"),
                            errorMessage = uiState.fieldErrors["dob"],
                            trailingContent = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_calendar),
                                    contentDescription = "Select DOB",
                                    tint = AppColors.TextSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        )

                        // Click interceptor overlay to launch DatePickerDialog reliably
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable {
                                    focusManager.clearFocus()
                                    onDobClick()
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // =====================================================
                // 5. PINCODE (Single Column)
                // =====================================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bringIntoViewRequester(pincodeBringIntoView)
                ) {
                    Text(
                        text = "Pincode",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.textPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AppOutlinedTextField(
                        modifier = Modifier.focusRequester(pincodeFocusRequester),
                        value = uiState.pincode,
                        onValueChange = onPincodeChange,
                        placeholder = "Enter 6-digit pincode",
                        isError = uiState.fieldErrors.containsKey("pincode"),
                        errorMessage = uiState.fieldErrors["pincode"],
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { cityFocusRequester.requestFocus() }
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // =====================================================
                // 6. CITY (Single Column)
                // =====================================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bringIntoViewRequester(cityBringIntoView)
                ) {
                    Text(
                        text = "City",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.textPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AppOutlinedTextField(
                        modifier = Modifier.focusRequester(cityFocusRequester),
                        value = uiState.city,
                        onValueChange = onCityChange,
                        placeholder = "Enter your city",
                        isError = uiState.fieldErrors.containsKey("city"),
                        errorMessage = uiState.fieldErrors["city"],
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { panFocusRequester.requestFocus() }
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // =====================================================
                // 7. PAN CARD (Single Column, PanTextField)
                // =====================================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bringIntoViewRequester(panBringIntoView)
                ) {
                    Text(
                        text = "PAN card",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.textPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PanTextField(
                        modifier = Modifier.focusRequester(panFocusRequester),
                        value = uiState.pan,
                        onValueChange = onPanChange,
                        placeholder = "Enter 10-character PAN (e.g. ABCDE1234F)",
                        isError = uiState.fieldErrors.containsKey("pan"),
                        errorMessage = uiState.fieldErrors["pan"],
                        imeAction = ImeAction.Done,
                        onDone = { focusManager.clearFocus() }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // =====================================================
                // 8. GENDER SELECTION (Single Column)
                // =====================================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bringIntoViewRequester(genderBringIntoView)
                ) {
                    Text(
                        text = "Gender",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.textPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        AppRadioButtonOption(
                            selected = uiState.gender == "M",
                            label = "Male",
                            onClick = { onGenderChange("M") }
                        )

                        AppRadioButtonOption(
                            selected = uiState.gender == "F",
                            label = "Female",
                            onClick = { onGenderChange("F") }
                        )
                    }

                    if (uiState.fieldErrors.containsKey("gender")) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.fieldErrors["gender"].orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // =====================================================
                // 9. SUBSCRIPTION TYPE (Single Column)
                // =====================================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bringIntoViewRequester(subscriptionBringIntoView)
                ) {
                    Text(
                        text = "Subscription Type",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.textPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AppRadioButtonOption(
                        selected = uiState.subscriptionType == "All-In-One(₹999)",
                        label = "All-In-One(₹999)",
                        onClick = { onSubscriptionChange("All-In-One(₹999)") }
                    )

                    val subscriptionError = uiState.fieldErrors["subscriptionType"] ?: uiState.fieldErrors["subscription"]
                    if (!subscriptionError.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = subscriptionError,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // =====================================================
                // 10. EXISTING AGENT / POSP (Single Column)
                // =====================================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bringIntoViewRequester(existingAgentBringIntoView)
                ) {
                    Text(
                        text = "Are you an existing insurance Agent/POSP ?",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.textPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        AppRadioButtonOption(
                            selected = uiState.isExistingAgent == false,
                            label = "No",
                            onClick = { onExistingAgentChange(false) }
                        )

                        AppRadioButtonOption(
                            selected = uiState.isExistingAgent == true,
                            label = "Yes",
                            onClick = { onExistingAgentChange(true) }
                        )
                    }

                    if (uiState.fieldErrors.containsKey("existingAgent")) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.fieldErrors["existingAgent"].orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // =====================================================
                // 11. HAVE AN ACCOUNT? LOGIN LINK
                // =====================================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Have an account?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.textSecondary
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            onLoginClick()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // =====================================================
                // 12. SUBMIT BUTTON (PrimaryCTAButton)
                // =====================================================
                PrimaryCTAButton(
                    text = if (uiState.isLoading) "Submitting..." else "Submit",
                    enabled = !uiState.isLoading,
                    showArrow = true,
                    onClick = onSubmitClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(36.dp))

                // =========================================================
                // 13. FOOTER STORE BADGES
                // =========================================================
//                AppStoreBadges()

                Spacer(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .height(24.dp)
                )
            }
        }

        // =========================================================
        // 4. AUTH FLOATING ICON (Top overlap badge)
        // =========================================================
        AuthFloatingIcon(
            iconRes = R.drawable.ic_login,
            cardHeightFraction = cardFraction,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Register Screen - Default", showBackground = true, showSystemUi = true)
@Composable
private fun RegisterContentPreview_Default() {
    PolicyBossTheme {
        RegisterContent(
            uiState = RegisterUiState(
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

@Preview(name = "Register Screen - Filled", showBackground = true, showSystemUi = true)
@Composable
private fun RegisterContentPreview_Filled() {
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

@Preview(name = "Register Screen - Error", showBackground = true, showSystemUi = true)
@Composable
private fun RegisterContentPreview_Errors() {
    PolicyBossTheme {
        RegisterContent(
            uiState = RegisterUiState(
                fullName = "",
                fieldErrors = mapOf(
                    "fullName" to "Please enter your full name"
                )
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

@Preview(name = "Register Screen - Loading", showBackground = true, showSystemUi = true)
@Composable
private fun RegisterContentPreview_Loading() {
    PolicyBossTheme {
        RegisterContent(
            uiState = RegisterUiState(
                fullName = "Rahul Chaurasia",
                mobile = "9773113793",
                email = "rahul.chaurasia@policyboss.com",
                dob = "24/01/1988",
                pincode = "400063",
                pan = "HPFOO4389L",
                isLoading = true,
                loadingMessage = "Checking data availability..."
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
