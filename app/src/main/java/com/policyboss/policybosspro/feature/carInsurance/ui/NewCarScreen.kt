package com.policyboss.policybosspro.feature.carInsurance.ui

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import com.policyboss.policybosspro.feature.carInsurance.model.DatePickerType
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarAction
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiState
import com.policyboss.policybosspro.ui.components.button.AppBackButton
import com.policyboss.policybosspro.ui.components.button.PrimaryCTAButton
import com.policyboss.policybosspro.ui.components.datePicker.AppDatePickerDialog
import com.policyboss.policybosspro.ui.components.dropDown.GenericM3Dropdown
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun NewCarScreen(
    uiState: NewCarUiState,
    onAction: (NewCarAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val scrollState = rememberScrollState()

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
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                AppBackButton(
                    onClick = { onAction(NewCarAction.OnBackClick) },
                    tint = AppColors.TextPrimary
                )

                Text(
                    text = "New Car",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary,
                    modifier = Modifier.padding(start = 12.dp, top = 2.dp, bottom = 8.dp)
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
                        shape = RoundedCornerShape(999.dp)
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
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // 1. Vahan Data Banner
                if (uiState.showVahanBanner && uiState.vahanDetails.isNotBlank()) {
                    VahanDataBanner(vahanDetails = uiState.vahanDetails)
                }

                // 2. Customer Name Field
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
                        singleLine = true,
                        isError = uiState.customerNameError != null,
                        supportingText = uiState.customerNameError?.let {
                            { Text(it, color = AppColors.ErrorRed, fontSize = 12.sp) }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.PrimaryBlue,
                            unfocusedBorderColor = AppColors.BorderSecondary,
                            focusedTextColor = AppColors.TextPrimary,
                            unfocusedTextColor = AppColors.TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 3. Registration Place Dropdown (RTO)
                GenericM3Dropdown(
                    label = "Registration details",
                    items = uiState.rtoOptions,
                    selectedItem = uiState.selectedRto,
                    onItemSelected = { onAction(NewCarAction.OnRtoSelected(it)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // 4. Registered On Radio Group (Individual vs Company)
                RegisteredOnRadioGroup(
                    selectedOwnership = uiState.ownershipType,
                    onOwnershipSelected = { onAction(NewCarAction.OnOwnershipSelected(it)) }
                )

                // 5. Preferred Policy Type Chips
                PolicyTypeChipsSection(
                    options = uiState.policyTypeOptions,
                    selectedPolicyType = uiState.selectedPolicyType,
                    onPolicyTypeSelected = { onAction(NewCarAction.OnPolicyTypeSelected(it)) }
                )

                // 6. Existing Policy Section (For rollover car)
                if (!uiState.isNewCar) {
                    ExistingPolicySection(
                        hasExistingPolicy = uiState.hasExistingPolicy,
                        onExistingPolicyToggle = { onAction(NewCarAction.OnExistingPolicyToggle(it)) },
                        selectedInsurer = uiState.selectedInsurer,
                        insurerOptions = uiState.insurerOptions,
                        onInsurerSelected = { onAction(NewCarAction.OnInsurerSelected(it)) },
                        policyExpiryDate = uiState.policyExpiryDate,
                        onExpiryDateClick = { onAction(NewCarAction.OnOpenDatePicker(DatePickerType.POLICY_EXPIRY)) }
                    )
                }

                // 7. Car Specs: Model Dropdown
                GenericM3Dropdown(
                    label = "Model",
                    items = uiState.modelOptions,
                    selectedItem = uiState.selectedModel,
                    onItemSelected = { onAction(NewCarAction.OnModelSelected(it)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // 8. Car Specs: Variant Dropdown
                GenericM3Dropdown(
                    label = "Variant",
                    items = uiState.variantOptions,
                    selectedItem = uiState.selectedVariant,
                    onItemSelected = { onAction(NewCarAction.OnVariantSelected(it)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // 9. Car Specs: Fuel Dropdown
                GenericM3Dropdown(
                    label = "Fuel",
                    items = uiState.fuelOptions,
                    selectedItem = uiState.selectedFuel,
                    onItemSelected = { onAction(NewCarAction.OnFuelSelected(it)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // 10. Registration Date Picker Field
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
                                text = "Enter registration date",
                                color = AppColors.TextPlaceholder,
                                fontSize = 14.sp
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_calendar),
                                contentDescription = "Select registration date",
                                tint = AppColors.TextSecondary,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { onAction(NewCarAction.OnOpenDatePicker(DatePickerType.REGISTRATION)) }
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.PrimaryBlue,
                            unfocusedBorderColor = AppColors.BorderSecondary,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAction(NewCarAction.OnOpenDatePicker(DatePickerType.REGISTRATION)) }
                    )
                }

                // 11. Manufacturing Date Picker Field
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Manufacturing date",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.TextPrimary
                    )
                    OutlinedTextField(
                        value = uiState.manufacturingDate,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = {
                            Text(
                                text = "Enter manufacturing date",
                                color = AppColors.TextPlaceholder,
                                fontSize = 14.sp
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_calendar),
                                contentDescription = "Select manufacturing date",
                                tint = AppColors.TextSecondary,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { onAction(NewCarAction.OnOpenDatePicker(DatePickerType.MANUFACTURING)) }
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.PrimaryBlue,
                            unfocusedBorderColor = AppColors.BorderSecondary,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAction(NewCarAction.OnOpenDatePicker(DatePickerType.MANUFACTURING)) }
                    )
                }

                // 12. Claims & NCB Section (if existing car with policy)
                if (!uiState.isNewCar && uiState.hasExistingPolicy) {
                    ClaimAndNcbSection(
                        hasClaim = uiState.hasClaim,
                        onClaimToggle = { onAction(NewCarAction.OnClaimToggle(it)) },
                        selectedNcb = uiState.selectedNcb,
                        ncbOptions = uiState.ncbOptions,
                        onNcbSelected = { onAction(NewCarAction.OnNcbSelected(it)) }
                    )
                }

                // 13. Consent Checkbox Section
                ConsentCheckboxSection(
                    consentGiven = uiState.consentGiven,
                    onConsentChange = { onAction(NewCarAction.OnConsentChange(it)) },
                    onNoticeClick = { onAction(NewCarAction.OnNoticeForConsentClick) }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Loading overlay
            if (uiState.isLoading) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black.copy(alpha = 0.25f)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppColors.PrimaryBlue)
                    }
                }
            }
        }
    }

    // Date Picker Dialog
    if (uiState.activeDatePicker != null) {
        AppDatePickerDialog(
            onDateSelected = { dateStr ->
                onAction(NewCarAction.OnDateSelected(dateStr))
            },
            onDismiss = {
                onAction(NewCarAction.OnDismissDatePicker)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NewCarScreenPreview() {
    NewCarScreen(
        uiState = NewCarUiState(
            customerName = "Nida Hussaini",
            registrationDate = "22/08/2018",
            manufacturingDate = "04/09/2017"
        ),
        onAction = {}
    )
}
