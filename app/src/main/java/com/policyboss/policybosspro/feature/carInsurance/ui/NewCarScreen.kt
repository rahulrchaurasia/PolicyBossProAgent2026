package com.policyboss.policybosspro.feature.carInsurance.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.carInsurance.component.ClaimAndNcbSection
import com.policyboss.policybosspro.feature.carInsurance.component.ConsentCheckboxSection
import com.policyboss.policybosspro.feature.carInsurance.component.ExistingPolicySection
import com.policyboss.policybosspro.feature.carInsurance.component.PolicyTypeChipsSection
import com.policyboss.policybosspro.feature.carInsurance.component.RegisteredOnRadioGroup
import com.policyboss.policybosspro.feature.carInsurance.component.VahanDataBanner
import com.policyboss.policybosspro.feature.carInsurance.model.CarEntryMode
import com.policyboss.policybosspro.feature.carInsurance.model.CarInsuranceBottomSheet
import com.policyboss.policybosspro.feature.carInsurance.model.CarInsuranceData
import com.policyboss.policybosspro.feature.carInsurance.model.DatePickerType
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarAction
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiState
import com.policyboss.policybosspro.feature.carInsurance.model.OwnershipType
import com.policyboss.policybosspro.ui.components.bottomSheet.AppSearchableBottomSheet
import com.policyboss.policybosspro.ui.components.bottomSheet.ClickableSelectorField
import com.policyboss.policybosspro.ui.components.button.AppBackButton
import com.policyboss.policybosspro.ui.components.button.PrimaryCTAButton
import com.policyboss.policybosspro.ui.components.datePicker.AppDatePickerDialog
import com.policyboss.policybosspro.ui.components.dropDown.DropdownOption
import com.policyboss.policybosspro.ui.components.dropDown.GenericM3Dropdown
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.titleLargeBold

@Composable
fun NewCarScreen(
    uiState: NewCarUiState,
    onAction: (NewCarAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val scrollState = rememberScrollState()
    var activeBottomSheet by remember { mutableStateOf(CarInsuranceBottomSheet.NONE) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = statusBarPadding)
                    .padding(bottom = 6.dp)
            ) {
                // Top App Bar with Back Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppBackButton(
                        onClick = { onAction(NewCarAction.OnBackClick) },
                        tint = AppColors.TextPrimary
                    )
                }

                // Header Title
                Text(
                    text = uiState.headerTitle,
                    style = MaterialTheme.typography.titleLargeBold,
                    fontSize = 24.sp,
                    color = AppColors.TextPrimary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    PrimaryCTAButton(
                        text = if (uiState.isLoading) "Processing..." else "Get quotes",
                        onClick = { onAction(NewCarAction.OnGetQuotesClick) },
                        enabled = uiState.isFormValid && !uiState.isLoading,
                        containerColor = AppColors.PrimaryBlue,
                        contentColor = Color.White,
                        arrowBackgroundColor = Color.White,
                        arrowTint = AppColors.PrimaryBlue,
                        shape = RoundedCornerShape(999.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Vahan Data Banner Card (Scrollable with form content)
                if (uiState.showVahanBanner && uiState.vahanData.isNotBlank()) {
                    VahanDataBanner(
                        vahanDetails = uiState.vahanData,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 1. Customer Name Field
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Customer name",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.TextPrimary
                    )
                    OutlinedTextField(
                        value = uiState.customerName,
                        onValueChange = { onAction(NewCarAction.OnCustomerNameChange(it)) },
                        placeholder = {
                            Text(
                                text = "Enter full name",
                                color = AppColors.TextPlaceholder,
                                fontSize = 14.sp
                            )
                        },
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = AppColors.TextPrimary
                        ),
                        singleLine = true,
                        isError = uiState.customerNameError != null,
                        supportingText = uiState.customerNameError?.let {
                            { Text(it, color = AppColors.ErrorRed, fontSize = 12.sp) }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.PrimaryBlue,
                            unfocusedBorderColor = AppColors.BorderSecondary,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = AppColors.TextPrimary,
                            unfocusedTextColor = AppColors.TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    )
                }

                // 2. Car Details Box (Rendered when vehicle info is available)
                if (uiState.entryMode == CarEntryMode.WITH_CAR_NUMBER || uiState.carDetailsSummary.isNotBlank()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Car Details",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.TextPrimary
                        )
                        OutlinedTextField(
                            value = uiState.carDetailsSummary.ifBlank {
                                "${uiState.selectedModel?.label ?: "Kia Seltos"} - ${uiState.selectedVariant?.label ?: "Plus 1.5 GTX AT"} - ${uiState.selectedFuel?.label ?: "Diesel"}"
                            },
                            onValueChange = {},
                            readOnly = true,
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = AppColors.TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.BorderSecondary,
                                unfocusedBorderColor = AppColors.BorderSecondary,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedTextColor = AppColors.TextPrimary,
                                unfocusedTextColor = AppColors.TextPrimary
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        )
                    }
                }

                // 3. Registration Details (RTO) – Clickable Field Triggering Searchable Modal Bottom Sheet
                ClickableSelectorField(
                    label = "Registration details",
                    value = uiState.selectedRto?.label ?: "",
                    placeholder = "Select registration details",
                    onClick = { activeBottomSheet = CarInsuranceBottomSheet.RTO },
                    modifier = Modifier.fillMaxWidth()
                )

                // 4. Car is registered on (Individual vs Company)
                RegisteredOnRadioGroup(
                    selectedOwnership = uiState.ownershipType,
                    onOwnershipSelected = { onAction(NewCarAction.OnOwnershipSelected(it)) }
                )

                // 5. Select your preferred policy (Single-line non-wrapping weighted tabs)
                PolicyTypeChipsSection(
                    options = uiState.policyTypeOptions,
                    selectedPolicyType = uiState.selectedPolicyType,
                    onPolicyTypeSelected = { onAction(NewCarAction.OnPolicyTypeSelected(it)) }
                )

                // 6. Existing Policy and Rollover Section (Controlled conditionally by policy type & isNewCar)
                AnimatedVisibility(
                    visible = uiState.shouldShowExistingPolicySection,
                    enter = expandVertically(
                        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(durationMillis = 220)),
                    exit = shrinkVertically(
                        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(durationMillis = 180))
                ) {
                    ExistingPolicySection(
                        hasExistingPolicy = uiState.hasExistingPolicy,
                        onExistingPolicyToggle = { onAction(NewCarAction.OnExistingPolicyToggle(it)) },
                        questionLabel = uiState.existingPolicyQuestionLabel,
                        shouldShowExpiryDate = uiState.shouldShowExpiryDate,
                        policyExpiryDate = uiState.policyExpiryDate,
                        onExpiryDateClick = { onAction(NewCarAction.OnOpenDatePicker(DatePickerType.POLICY_EXPIRY)) },
                        shouldShowPreviousInsurer = uiState.shouldShowPreviousInsurer,
                        selectedInsurer = uiState.selectedInsurer,
                        insurerOptions = uiState.insurerOptions,
                        onInsurerSelected = { onAction(NewCarAction.OnInsurerSelected(it)) },
                        onInsurerClick = { activeBottomSheet = CarInsuranceBottomSheet.INSURER },
                        shouldShowTpInsurer = uiState.shouldShowTpInsurer,
                        selectedTpInsurer = uiState.selectedTpInsurer,
                        tpInsurerOptions = uiState.tpInsurerOptions,
                        onTpInsurerSelected = { onAction(NewCarAction.OnTpInsurerSelected(it)) },
                        onTpInsurerClick = { activeBottomSheet = CarInsuranceBottomSheet.TP_INSURER },
                        claimAndNcbContent = {
                            AnimatedVisibility(
                                visible = uiState.shouldShowClaimAndNcb,
                                enter = expandVertically(
                                    animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
                                ) + fadeIn(animationSpec = tween(durationMillis = 220)),
                                exit = shrinkVertically(
                                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
                                ) + fadeOut(animationSpec = tween(durationMillis = 180))
                            ) {
                                ClaimAndNcbSection(
                                    hasClaim = uiState.hasClaim,
                                    onClaimToggle = { onAction(NewCarAction.OnClaimToggle(it)) },
                                    selectedNcb = uiState.selectedNcb,
                                    ncbOptions = uiState.ncbOptions,
                                    onNcbSelected = { onAction(NewCarAction.OnNcbSelected(it)) },
                                    onNcbClick = { activeBottomSheet = CarInsuranceBottomSheet.NCB }
                                )
                            }
                        }
                    )
                }

                // 7. Vehicle Specs: Model, Variant, Fuel (When in WITHOUT_CAR_NUMBER or editing car specs)
                if (uiState.entryMode == CarEntryMode.WITHOUT_CAR_NUMBER) {
                    ClickableSelectorField(
                        label = "Model",
                        value = uiState.selectedModel?.label ?: "",
                        placeholder = "Select model",
                        onClick = { activeBottomSheet = CarInsuranceBottomSheet.MODEL },
                        modifier = Modifier.fillMaxWidth()
                    )

                    ClickableSelectorField(
                        label = "Variant",
                        value = uiState.selectedVariant?.label ?: "",
                        placeholder = "Select variant",
                        onClick = { activeBottomSheet = CarInsuranceBottomSheet.VARIANT },
                        modifier = Modifier.fillMaxWidth()
                    )

                    ClickableSelectorField(
                        label = "Fuel",
                        value = uiState.selectedFuel?.label ?: "",
                        placeholder = "Select fuel",
                        onClick = { activeBottomSheet = CarInsuranceBottomSheet.FUEL },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 8. Car Registration & Manufacturing Date Section
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Registration Date Picker Field
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Registration date",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.TextPrimary
                        )
                        OutlinedTextField(
                            value = uiState.registrationDate,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = {
                                Text(
                                    text = "DD/MM/YYYY",
                                    color = AppColors.TextPlaceholder,
                                    fontSize = 14.sp
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_chevron_down),
                                    contentDescription = "Select registration date",
                                    tint = AppColors.TextSecondary,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { onAction(NewCarAction.OnOpenDatePicker(DatePickerType.REGISTRATION)) }
                                )
                            },
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = AppColors.TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.PrimaryBlue,
                                unfocusedBorderColor = AppColors.BorderSecondary,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedTextColor = AppColors.TextPrimary,
                                unfocusedTextColor = AppColors.TextPrimary
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clickable { onAction(NewCarAction.OnOpenDatePicker(DatePickerType.REGISTRATION)) }
                        )
                    }

                    // Manufacturing Month + Year Row
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Manufacturing date",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.TextPrimary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            GenericM3Dropdown(
                                label = "",
                                items = uiState.manufacturingMonths.map { DropdownOption(it, it) },
                                selectedItem = DropdownOption(uiState.manufacturingMonth, uiState.manufacturingMonth),
                                onItemSelected = { onAction(NewCarAction.UpdateManufacturingMonth(it.value)) },
                                modifier = Modifier.weight(1f)
                            )

                            GenericM3Dropdown(
                                label = "",
                                items = uiState.manufacturingYears.map { DropdownOption(it, it) },
                                selectedItem = DropdownOption(uiState.manufacturingYear, uiState.manufacturingYear),
                                onItemSelected = { onAction(NewCarAction.UpdateManufacturingYear(it.value)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // 9. Consent Checkbox Section
                ConsentCheckboxSection(
                    consentGiven = uiState.consentGiven,
                    onConsentChange = { onAction(NewCarAction.OnConsentChange(it)) },
                    onNoticeClick = { onAction(NewCarAction.OnNoticeForConsentClick) }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Modal Bottom Sheets
    when (activeBottomSheet) {
        CarInsuranceBottomSheet.NONE -> Unit

        CarInsuranceBottomSheet.RTO -> {
            AppSearchableBottomSheet(
                title = "Registration details",
                searchPlaceholder = "Search RTO code or city...",
                items = uiState.rtoOptions,
                selectedItem = uiState.selectedRto,
                showSearch = true,
                onItemSelected = { onAction(NewCarAction.OnRtoSelected(it)) },
                onDismiss = { activeBottomSheet = CarInsuranceBottomSheet.NONE }
            )
        }

        CarInsuranceBottomSheet.INSURER -> {
            AppSearchableBottomSheet(
                title = "Select insurer",
                searchPlaceholder = "Search insurer...",
                items = uiState.insurerOptions,
                selectedItem = uiState.selectedInsurer,
                showSearch = true,
                onItemSelected = { onAction(NewCarAction.OnInsurerSelected(it)) },
                onDismiss = { activeBottomSheet = CarInsuranceBottomSheet.NONE }
            )
        }

        CarInsuranceBottomSheet.TP_INSURER -> {
            AppSearchableBottomSheet(
                title = "Select Policy TP Insurer",
                searchPlaceholder = "Search TP insurer...",
                items = uiState.tpInsurerOptions,
                selectedItem = uiState.selectedTpInsurer,
                showSearch = true,
                onItemSelected = { onAction(NewCarAction.OnTpInsurerSelected(it)) },
                onDismiss = { activeBottomSheet = CarInsuranceBottomSheet.NONE }
            )
        }

        CarInsuranceBottomSheet.MODEL -> {
            AppSearchableBottomSheet(
                title = "Select model",
                searchPlaceholder = "Search car model...",
                items = uiState.modelOptions,
                selectedItem = uiState.selectedModel,
                showSearch = true,
                onItemSelected = { onAction(NewCarAction.OnModelSelected(it)) },
                onDismiss = { activeBottomSheet = CarInsuranceBottomSheet.NONE }
            )
        }

        CarInsuranceBottomSheet.VARIANT -> {
            AppSearchableBottomSheet(
                title = "Select variant",
                searchPlaceholder = "Search car variant...",
                items = uiState.variantOptions,
                selectedItem = uiState.selectedVariant,
                showSearch = true,
                onItemSelected = { onAction(NewCarAction.OnVariantSelected(it)) },
                onDismiss = { activeBottomSheet = CarInsuranceBottomSheet.NONE }
            )
        }

        CarInsuranceBottomSheet.FUEL -> {
            AppSearchableBottomSheet(
                title = "Select fuel",
                items = uiState.fuelOptions,
                selectedItem = uiState.selectedFuel,
                showSearch = false,
                onItemSelected = { onAction(NewCarAction.OnFuelSelected(it)) },
                onDismiss = { activeBottomSheet = CarInsuranceBottomSheet.NONE }
            )
        }

        CarInsuranceBottomSheet.NCB -> {
            AppSearchableBottomSheet(
                title = "Select NCB",
                items = uiState.ncbOptions,
                selectedItem = uiState.selectedNcb,
                showSearch = false,
                onItemSelected = { onAction(NewCarAction.OnNcbSelected(it)) },
                onDismiss = { activeBottomSheet = CarInsuranceBottomSheet.NONE }
            )
        }
    }

    // Date Picker Dialog
    if (uiState.activeDatePicker != null) {
        AppDatePickerDialog(
            onDismiss = { onAction(NewCarAction.OnDismissDatePicker) },
            onDateSelected = { onAction(NewCarAction.OnDateSelected(it)) }
        )
    }
}

@Preview(showBackground = true, name = "With Car Number Mode (Continue with Car Number)")
@Composable
private fun NewCarScreen_WithCarNumber_Preview() {
    NewCarScreen(
        uiState = NewCarUiState(
            entryMode = CarEntryMode.WITH_CAR_NUMBER,
            headerTitle = "Continue with Car Number",
            showVahanBanner = true,
            vahanData = "HONDA CARS INDIA LTD, AMAZE 1.2 V CVT (I-VTEC), PETROL (1199 CC)",
            customerName = "Nida Hussaini",
            selectedRto = DropdownOption("MH-12", "MH12 - Pune"),
            ownershipType = OwnershipType.COMPANY,
            selectedPolicyType = "Comprehensive",
            hasExistingPolicy = true,
            selectedInsurer = DropdownOption("ACKO", "Acko General Insurance", R.drawable.ic_acko_logo),
            policyExpiryDate = "22/08/2027",
            selectedModel = DropdownOption("HONDA_AMAZE", "Honda Amaze"),
            selectedVariant = DropdownOption("1.2_S_CVT", "1.2 S CVT (1198CC)"),
            selectedFuel = DropdownOption("PETROL", "Petrol"),
            registrationDate = "22/08/2018",
            manufacturingMonth = "Sep",
            manufacturingYear = "2017",
            hasClaim = true,
            selectedNcb = DropdownOption("0", "0%"),
            consentGiven = true
        ),
        onAction = {}
    )
}

@Preview(showBackground = true, name = "Without Car Number Mode (New Car)")
@Composable
private fun NewCarScreen_WithoutCarNumber_Preview() {
    NewCarScreen(
        uiState = NewCarUiState(
            entryMode = CarEntryMode.WITHOUT_CAR_NUMBER,
            headerTitle = "New Car",
            showVahanBanner = false,
            vahanData = "",
            isNewCar = true,
            customerName = "",
            selectedRto = DropdownOption("MH-01", "MH01 - Mumbai"),
            ownershipType = OwnershipType.INDIVIDUAL,
            selectedPolicyType = "TP",
            selectedModel = DropdownOption("HONDA_AMAZE", "Honda Amaze"),
            selectedVariant = DropdownOption("1.2_S_CVT", "1.2 S CVT (1198CC)"),
            selectedFuel = DropdownOption("PETROL", "Petrol"),
            registrationDate = "",
            manufacturingMonth = "Jan",
            manufacturingYear = "2026",
            hasExistingPolicy = false,
            consentGiven = true
        ),
        onAction = {}
    )
}
