package com.policyboss.policybosspro.feature.carInsurance.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.carInsurance.model.CarInsuranceData
import com.policyboss.policybosspro.ui.components.bottomSheet.ClickableSelectorField
import com.policyboss.policybosspro.ui.components.datePicker.ClickableDateField
import com.policyboss.policybosspro.ui.components.dropDown.DropdownOption
import com.policyboss.policybosspro.ui.components.dropDown.GenericM3Dropdown
import com.policyboss.policybosspro.ui.components.radio.AppRadioButtonOption
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun ExistingPolicySection(
    hasExistingPolicy: Boolean,
    onExistingPolicyToggle: (Boolean) -> Unit,
    policyExpiryDate: String,
    onExpiryDateClick: () -> Unit,
    modifier: Modifier = Modifier,
    questionLabel: String = "Do You Have Existing Policy?",
    cardTitle: String = "Policy",
    cutoutBackgroundColor: Color = Color.White,
    shouldShowExpiryDate: Boolean = true,
    shouldShowPreviousInsurer: Boolean = true,
    selectedInsurer: DropdownOption<String>? = null,
    insurerOptions: List<DropdownOption<String>> = emptyList(),
    onInsurerSelected: (DropdownOption<String>) -> Unit = {},
    onInsurerClick: (() -> Unit)? = null,
    shouldShowTpInsurer: Boolean = false,
    selectedTpInsurer: DropdownOption<String>? = null,
    tpInsurerOptions: List<DropdownOption<String>> = emptyList(),
    onTpInsurerSelected: (DropdownOption<String>) -> Unit = {},
    onTpInsurerClick: (() -> Unit)? = null,
    shouldShowCard: Boolean = hasExistingPolicy || shouldShowTpInsurer,
    shouldShowClaimAndNcb: Boolean = false,
    claimAndNcbContent: (@Composable () -> Unit)? = null
) {
    val enterTransition = expandVertically(
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
    ) + fadeIn(animationSpec = tween(durationMillis = 220))

    val exitTransition = shrinkVertically(
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
    ) + fadeOut(animationSpec = tween(durationMillis = 180))

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Question: Do you have an existing policy? (Dynamic question label)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = questionLabel,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppRadioButtonOption(
                    selected = hasExistingPolicy,
                    label = "Yes",
                    onClick = { onExistingPolicyToggle(true) }
                )

                AppRadioButtonOption(
                    selected = !hasExistingPolicy,
                    label = "No",
                    onClick = { onExistingPolicyToggle(false) }
                )
            }
        }

        // Conditional fields grouped inside TitledBorderCard ("Policy")
        AnimatedVisibility(
            visible = shouldShowCard,
            enter = enterTransition,
            exit = exitTransition
        ) {
            TitledBorderCard(
                title = cardTitle,
                containerColor = Color(0xFFF8FAFC), // Soft slate neutral tint
                borderColor = AppColors.BorderLight,
                titleColor = AppColors.PrimaryBlue,
                cutoutBackgroundColor = cutoutBackgroundColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                // 1. Policy Expiry Date Picker Field (Shown for TP Only, OD Only, Comprehensive)
                AnimatedVisibility(
                    visible = shouldShowExpiryDate,
                    enter = enterTransition,
                    exit = exitTransition
                ) {
                    ClickableDateField(
                        label = "Expiry Date",
                        value = policyExpiryDate,
                        placeholder = "DD/MM/YYYY",
                        contentDescription = "Select Expiry Date",
                        onClick = onExpiryDateClick
                    )
                }

                // 2. Previous Insurer (Shown only for Comprehensive)
                AnimatedVisibility(
                    visible = shouldShowPreviousInsurer,
                    enter = enterTransition,
                    exit = exitTransition
                ) {
                    if (onInsurerClick != null) {
                        ClickableSelectorField(
                            label = "Previous Insurer",
                            value = selectedInsurer?.label ?: "",
                            placeholder = "Select previous insurer",
                            iconRes = selectedInsurer?.iconRes,
                            onClick = onInsurerClick,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        GenericM3Dropdown(
                            label = "Previous Insurer",
                            items = insurerOptions,
                            selectedItem = selectedInsurer,
                            onItemSelected = onInsurerSelected,
                            placeholder = "Select previous insurer",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // 3. Policy TP Insurer (Shown only for OD Only)
                AnimatedVisibility(
                    visible = shouldShowTpInsurer,
                    enter = enterTransition,
                    exit = exitTransition
                ) {
                    if (onTpInsurerClick != null) {
                        ClickableSelectorField(
                            label = "Policy TP Insurer",
                            value = selectedTpInsurer?.label ?: "",
                            placeholder = "Select TP insurer",
                            iconRes = selectedTpInsurer?.iconRes,
                            onClick = onTpInsurerClick,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        GenericM3Dropdown(
                            label = "Policy TP Insurer",
                            items = tpInsurerOptions,
                            selectedItem = selectedTpInsurer,
                            onItemSelected = onTpInsurerSelected,
                            placeholder = "Select TP insurer",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // 4. Slot for Claim and NCB fields inside the container
                AnimatedVisibility(
                    visible = shouldShowClaimAndNcb,
                    enter = enterTransition,
                    exit = exitTransition
                ) {
                    claimAndNcbContent?.invoke()
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Comprehensive Mode (Grouped Card)")
@Composable
private fun ExistingPolicySectionComprehensivePreview() {
    ExistingPolicySection(
        hasExistingPolicy = true,
        onExistingPolicyToggle = {},
        questionLabel = "Do You Have Existing Comprehensive Policy?",
        shouldShowExpiryDate = true,
        shouldShowPreviousInsurer = true,
        shouldShowTpInsurer = false,
        selectedInsurer = DropdownOption("ORIENTAL", "ORIENTAL INSURANCE CO. LTD.", R.drawable.ic_acko_logo),
        insurerOptions = CarInsuranceData.insurerOptions,
        policyExpiryDate = "29-Jan-2027",
        onExpiryDateClick = {},
        shouldShowClaimAndNcb = true,
        claimAndNcbContent = {
            ClaimAndNcbSection(
                hasClaim = false,
                onClaimToggle = {},
                selectedNcb = DropdownOption("20", "20%"),
                ncbOptions = CarInsuranceData.ncbOptions,
                onNcbSelected = {}
            )
        }
    )
}

@Preview(showBackground = true, name = "OD Only Yes Mode (Grouped Card)")
@Composable
private fun ExistingPolicySectionOdOnlyPreview() {
    ExistingPolicySection(
        hasExistingPolicy = true,
        onExistingPolicyToggle = {},
        questionLabel = "Do You Have Existing Comprehensive Policy?",
        shouldShowExpiryDate = true,
        shouldShowPreviousInsurer = true,
        selectedInsurer = DropdownOption("ORIENTAL", "ORIENTAL INSURANCE CO. LTD.", R.drawable.ic_acko_logo),
        insurerOptions = CarInsuranceData.insurerOptions,
        shouldShowTpInsurer = true,
        selectedTpInsurer = DropdownOption("ACKO", "Acko General Insurance", R.drawable.ic_acko_logo),
        tpInsurerOptions = CarInsuranceData.insurerOptions,
        policyExpiryDate = "29-Jan-2027",
        onExpiryDateClick = {},
        shouldShowClaimAndNcb = true,
        claimAndNcbContent = {
            ClaimAndNcbSection(
                hasClaim = true,
                onClaimToggle = {},
                selectedNcb = DropdownOption("0", "0%"),
                ncbOptions = CarInsuranceData.ncbOptions,
                onNcbSelected = {}
            )
        }
    )
}

@Preview(showBackground = true, name = "OD Only No Mode (Keeps TP Insurer Card)")
@Composable
private fun ExistingPolicySectionOdOnlyNoPreview() {
    ExistingPolicySection(
        hasExistingPolicy = false,
        onExistingPolicyToggle = {},
        questionLabel = "Do You Have Existing Comprehensive Policy?",
        shouldShowExpiryDate = false,
        shouldShowPreviousInsurer = false,
        shouldShowTpInsurer = true,
        selectedTpInsurer = DropdownOption("ACKO", "Acko General Insurance", R.drawable.ic_acko_logo),
        tpInsurerOptions = CarInsuranceData.insurerOptions,
        policyExpiryDate = "",
        onExpiryDateClick = {},
        shouldShowClaimAndNcb = false
    )
}

@Preview(showBackground = true, name = "TP Only Mode (Grouped Card)")
@Composable
private fun ExistingPolicySectionTpOnlyPreview() {
    ExistingPolicySection(
        hasExistingPolicy = true,
        onExistingPolicyToggle = {},
        questionLabel = "Do You Have Existing Policy?",
        shouldShowExpiryDate = true,
        shouldShowPreviousInsurer = true,
        selectedInsurer = DropdownOption("ORIENTAL", "ORIENTAL INSURANCE CO. LTD.", R.drawable.ic_acko_logo),
        insurerOptions = CarInsuranceData.insurerOptions,
        shouldShowTpInsurer = false,
        policyExpiryDate = "29-Jan-2027",
        onExpiryDateClick = {}
    )
}

@Preview(showBackground = true, name = "Existing Policy No Preview")
@Composable
private fun ExistingPolicySectionNoPreview() {
    ExistingPolicySection(
        hasExistingPolicy = false,
        onExistingPolicyToggle = {},
        questionLabel = "Do You Have Existing Comprehensive Policy?",
        shouldShowExpiryDate = false,
        shouldShowPreviousInsurer = false,
        shouldShowTpInsurer = false,
        policyExpiryDate = "",
        onExpiryDateClick = {}
    )
}
