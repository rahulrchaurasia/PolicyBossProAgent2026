package com.policyboss.policybosspro.feature.carInsurance.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.policyboss.policybosspro.feature.carInsurance.model.CarInsuranceData
import com.policyboss.policybosspro.ui.components.bottomSheet.ClickableSelectorField
import com.policyboss.policybosspro.ui.components.dropDown.DropdownOption
import com.policyboss.policybosspro.ui.components.dropDown.GenericM3Dropdown
import com.policyboss.policybosspro.ui.components.radio.AppRadioButtonOption
import com.policyboss.policybosspro.ui.theme.AppColors

/**
 * Standardized Existing Policy Section.
 * Shows the dynamic policy question (e.g. "Do You Have Existing Policy?" vs
 * "Do You Have Existing Comprehensive Policy?") with Yes / No radio options.
 *
 * When "Yes" is selected:
 * Smoothly reveals conditional rollover fields inside a soft slate tinted card
 * (Color(0xFFF8FAFC) container with 1.dp AppColors.BorderLight border and 14.dp rounded corners).
 *
 * Dynamically reveals or hides Expiry Date, Previous Insurer, Policy TP Insurer,
 * and Claim/NCB based on the active preferred policy type (TP Only, OD Only, Comprehensive).
 */
@Composable
fun ExistingPolicySection(
    hasExistingPolicy: Boolean,
    onExistingPolicyToggle: (Boolean) -> Unit,
    policyExpiryDate: String,
    onExpiryDateClick: () -> Unit,
    modifier: Modifier = Modifier,
    questionLabel: String = "Do You Have Existing Policy?",
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
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

        // Conditional fields grouped inside a soft tinted container card
        AnimatedVisibility(
            visible = hasExistingPolicy,
            enter = enterTransition,
            exit = exitTransition
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF8FAFC), // Soft slate neutral tint
                border = BorderStroke(1.dp, AppColors.BorderLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Policy Expiry Date Picker Field (Shown for TP Only, OD Only, Comprehensive)
                    AnimatedVisibility(
                        visible = shouldShowExpiryDate,
                        enter = enterTransition,
                        exit = exitTransition
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Expiry Date",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.TextPrimary
                            )

                            OutlinedTextField(
                                value = policyExpiryDate,
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
                                        painter = painterResource(id = R.drawable.ic_calendar),
                                        contentDescription = "Select expiry date",
                                        tint = AppColors.TextSecondary,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable { onExpiryDateClick() }
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
                                    .heightIn(min = 52.dp)
                                    .clickable { onExpiryDateClick() }
                            )
                        }
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

@Preview(showBackground = true, name = "OD Only Mode (Grouped Card)")
@Composable
private fun ExistingPolicySectionOdOnlyPreview() {
    ExistingPolicySection(
        hasExistingPolicy = true,
        onExistingPolicyToggle = {},
        questionLabel = "Do You Have Existing Comprehensive Policy?",
        shouldShowExpiryDate = true,
        shouldShowPreviousInsurer = false,
        shouldShowTpInsurer = true,
        selectedTpInsurer = DropdownOption("ACKO", "Acko General Insurance", R.drawable.ic_acko_logo),
        tpInsurerOptions = CarInsuranceData.insurerOptions,
        policyExpiryDate = "29-Jan-2027",
        onExpiryDateClick = {},
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

@Preview(showBackground = true, name = "TP Only Mode (Grouped Card)")
@Composable
private fun ExistingPolicySectionTpOnlyPreview() {
    ExistingPolicySection(
        hasExistingPolicy = true,
        onExistingPolicyToggle = {},
        questionLabel = "Do You Have Existing Policy?",
        shouldShowExpiryDate = true,
        shouldShowPreviousInsurer = false,
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
