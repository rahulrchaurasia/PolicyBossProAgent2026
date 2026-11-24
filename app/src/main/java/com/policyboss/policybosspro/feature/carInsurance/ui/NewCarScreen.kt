package com.policyboss.policybosspro.feature.carInsurance.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.carInsurance.component.CarDetailsBottomSheet
import com.policyboss.policybosspro.feature.carInsurance.component.ClaimAndNcbSection
import com.policyboss.policybosspro.feature.carInsurance.component.ConsentCheckboxSection
import com.policyboss.policybosspro.feature.carInsurance.component.ExistingPolicySection
import com.policyboss.policybosspro.feature.carInsurance.component.PolicyTypeChipsSection
import com.policyboss.policybosspro.feature.carInsurance.component.RegisteredOnRadioGroup
import com.policyboss.policybosspro.feature.carInsurance.component.TitledBorderCard
import com.policyboss.policybosspro.feature.carInsurance.component.VahanDataBanner
import com.policyboss.policybosspro.ui.components.radio.AppRadioButtonOption
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
import com.policyboss.policybosspro.ui.components.datePicker.ClickableDateField
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
                    fontSize = 21.sp,
                    color = AppColors.TextPrimary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
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
                    val isCustomerNameEditable = !uiState.isContinueWithCarNumber
                    OutlinedTextField(
                        value = uiState.customerName,
                        onValueChange = { if (isCustomerNameEditable) onAction(NewCarAction.OnCustomerNameChange(it)) },
                        readOnly = !isCustomerNameEditable,
                        enabled = isCustomerNameEditable,
                        placeholder = {
                            Text(
                                text = if (uiState.entryMode == CarEntryMode.NEW_CAR) "Enter Full Name" else "Enter full name",
                                color = AppColors.TextPlaceholder,
                                fontSize = 14.sp
                            )
                        },
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = if (isCustomerNameEditable) AppColors.TextPrimary else Color(0xFF64748B)
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
                            disabledBorderColor = Color(0xFFE2E8F0),
                            disabledContainerColor = Color(0xFFF1F5F9),
                            disabledTextColor = Color(0xFF64748B),
                            disabledPlaceholderColor = AppColors.TextPlaceholder,
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

                // 2. Unified Car Details Selection Field across ALL modes
                CarDetailsSelectorField(
                    value = uiState.formattedCarDetails,
                    onClick = { onAction(NewCarAction.OpenCarDetailsSheet) },
                    modifier = Modifier.fillMaxWidth()
                )

                // 4. Car is registered on (Individual vs Company)
                RegisteredOnRadioGroup(
                    selectedOwnership = uiState.ownershipType,
                    onOwnershipSelected = { onAction(NewCarAction.OnOwnershipSelected(it)) }
                )

                // 5. Select your preferred policy
                if (uiState.entryMode == CarEntryMode.NEW_CAR) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Select your preferred policy",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.TextPrimary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val newCarOptions = listOf("TP (3 Yr)", "OD (1 Yr) + TP (3 Yr)")
                            newCarOptions.forEach { option ->
                                AppRadioButtonOption(
                                    selected = uiState.selectedPolicyType == option,
                                    label = option,
                                    onClick = { onAction(NewCarAction.OnPolicyTypeSelected(option)) }
                                )
                            }
                        }
                    }
                } else {
                    PolicyTypeChipsSection(
                        options = uiState.policyTypeOptions,
                        selectedPolicyType = uiState.selectedPolicyType,
                        onPolicyTypeSelected = { onAction(NewCarAction.OnPolicyTypeSelected(it)) }
                    )
                }

                // 6. Existing Policy and Rollover Section (Controlled conditionally by policy type & entryMode)
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
                        shouldShowCard = uiState.shouldShowExistingPolicyCard,
                        shouldShowClaimAndNcb = uiState.shouldShowClaimAndNcb,
                        claimAndNcbContent = {
                            ClaimAndNcbSection(
                                hasClaim = uiState.hasClaim,
                                onClaimToggle = { onAction(NewCarAction.OnClaimToggle(it)) },
                                selectedNcb = uiState.selectedNcb,
                                ncbOptions = uiState.ncbOptions,
                                onNcbSelected = { onAction(NewCarAction.OnNcbSelected(it)) },
                                onNcbClick = { activeBottomSheet = CarInsuranceBottomSheet.NCB }
                            )
                        }
                    )
                }

                // 6. Grouped Car Registration Card with Notched Top-Left Border Title
                TitledBorderCard(
                    title = "Car Registration",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 1. Registered on (RTO / City selector field)
                    ClickableSelectorField(
                        label = "Registered on",
                        value = uiState.selectedRto?.label ?: "",
                        placeholder = if (uiState.entryMode == CarEntryMode.WITH_CAR_NUMBER) "Select registration details" else "Place of Registration",
                        enabled = !uiState.isContinueWithCarNumber,
                        onClick = {
                            if (!uiState.isContinueWithCarNumber) {
                                activeBottomSheet = CarInsuranceBottomSheet.RTO
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 2. Registration Date Picker Field (Full-field clickable with R.drawable.ic_calander)
                    ClickableDateField(
                        label = "Registration date",
                        value = uiState.registrationDate,
                        placeholder = "DD/MM/YYYY",
                        contentDescription = "Select Registration Date",
                        enabled = !uiState.isContinueWithCarNumber,
                        onClick = {
                            if (!uiState.isContinueWithCarNumber) {
                                onAction(NewCarAction.OnOpenDatePicker(DatePickerType.REGISTRATION))
                            }
                        }
                    )

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
                                onItemSelected = {
                                    if (!uiState.isContinueWithCarNumber) {
                                        onAction(NewCarAction.UpdateManufacturingMonth(it.value))
                                    }
                                },
                                enabled = !uiState.isContinueWithCarNumber,
                                modifier = Modifier.weight(1f)
                            )

                            GenericM3Dropdown(
                                label = "",
                                items = uiState.manufacturingYears.map { DropdownOption(it, it) },
                                selectedItem = DropdownOption(uiState.manufacturingYear, uiState.manufacturingYear),
                                onItemSelected = {
                                    if (!uiState.isContinueWithCarNumber) {
                                        onAction(NewCarAction.UpdateManufacturingYear(it.value))
                                    }
                                },
                                enabled = !uiState.isContinueWithCarNumber,
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

        CarInsuranceBottomSheet.MODEL,
        CarInsuranceBottomSheet.VARIANT,
        CarInsuranceBottomSheet.FUEL -> Unit

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

    // Unified Car Details Modal Bottom Sheet
    if (uiState.isCarDetailsSheetOpen) {
        CarDetailsBottomSheet(
            uiState = uiState,
            onAction = onAction
        )
    }

    // Date Picker Dialog
    if (uiState.activeDatePicker != null) {
        AppDatePickerDialog(
            onDismiss = { onAction(NewCarAction.OnDismissDatePicker) },
            onDateSelected = { onAction(NewCarAction.OnDateSelected(it)) }
        )
    }
}

/**
 * Unified Car Details Selector Field for NewCarScreen.
 * Displays "Car Details" header, formatted car specs (e.g. "HYUNDAI, CRETA, PETROL, 1.4 SX (O) DCT TURBO"),
 * placeholder "Make, Model, Fuel and Variant", and trailing edit icon or chevron.
 */
@Composable
fun CarDetailsSelectorField(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Car Details",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(
                width = 1.dp,
                color = if (isError) AppColors.ErrorRed else AppColors.BorderSecondary
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (value.isNotBlank()) {
                    Text(
                        text = value,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = AppColors.TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Text(
                        text = "Make, Model, Fuel and Variant",
                        fontSize = 14.sp,
                        color = AppColors.TextPlaceholder,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (value.isNotBlank()) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit car details",
                        tint = AppColors.PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chevron_down),
                        contentDescription = "Select car details",
                        tint = AppColors.TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = AppColors.ErrorRed,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
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
            carDetailsSummary = "KIA - SELTOS - PLUS 1.5 GTX AT - DIESEL - (1493 CC)",
            make = "KIA",
            makeId = "KIA",
            model = "SELTOS",
            modelId = "SELTOS",
            fuel = "DIESEL",
            fuelId = "DIESEL",
            variant = "Plus 1.5 GTX AT (1493CC)",
            variantId = "SELTOS_PLUS_1.5_GTX_AT",
            customerName = "PRIYANKA VYAS",
            selectedRto = DropdownOption("RJ-14", "RJ14 - Jaipur"),
            ownershipType = OwnershipType.INDIVIDUAL,
            selectedPolicyType = "Comprehensive",
            hasExistingPolicy = true,
            selectedInsurer = DropdownOption("ORIENTAL", "ORIENTAL INSURANCE CO. LTD."),
            policyExpiryDate = "29-Jan-2027",
            selectedModel = DropdownOption("KIA_SELTOS", "Kia Seltos"),
            selectedVariant = DropdownOption("SELTOS_PLUS_1.5_GTX_AT", "Plus 1.5 GTX AT - Diesel - (1493 CC)"),
            selectedFuel = DropdownOption("DIESEL", "Diesel"),
            registrationDate = "01-Feb-2021",
            manufacturingMonth = "Jan",
            manufacturingYear = "2021",
            hasClaim = false,
            selectedNcb = DropdownOption("0", "0%"),
            consentGiven = true
        ),
        onAction = {}
    )
}

@Preview(showBackground = true, name = "Without Car Number Mode (Continue without car number)")
@Composable
private fun NewCarScreen_WithoutCarNumber_Preview() {
    NewCarScreen(
        uiState = NewCarUiState(
            entryMode = CarEntryMode.WITHOUT_CAR_NUMBER,
            headerTitle = "Continue without car number",
            showVahanBanner = false,
            vahanData = "",
            carDetailsSummary = "",
            customerName = "",
            selectedRto = null,
            ownershipType = OwnershipType.INDIVIDUAL,
            selectedPolicyType = "Comprehensive",
            selectedModel = null,
            selectedVariant = null,
            selectedFuel = null,
            registrationDate = "",
            manufacturingMonth = "Jan",
            manufacturingYear = "2021",
            hasExistingPolicy = true,
            selectedInsurer = null,
            policyExpiryDate = "",
            consentGiven = true
        ),
        onAction = {}
    )
}

@Preview(showBackground = true, name = "New Car Mode (New Car)")
@Composable
private fun NewCarScreen_NewCar_Preview() {
    NewCarScreen(
        uiState = NewCarUiState(
            entryMode = CarEntryMode.NEW_CAR,
            headerTitle = "New Car",
            showVahanBanner = false,
            vahanData = "",
            carDetailsSummary = "",
            customerName = "",
            selectedRto = null,
            ownershipType = OwnershipType.INDIVIDUAL,
            policyTypeOptions = listOf("TP (3 Yr)", "OD (1 Yr) + TP (3 Yr)"),
            selectedPolicyType = "OD (1 Yr) + TP (3 Yr)",
            selectedModel = null,
            selectedVariant = null,
            selectedFuel = null,
            registrationDate = "20-Nov-2026",
            manufacturingMonth = "Nov",
            manufacturingYear = "2026",
            hasExistingPolicy = false,
            consentGiven = true
        ),
        onAction = {}
    )
}
