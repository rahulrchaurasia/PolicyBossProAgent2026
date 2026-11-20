package com.policyboss.policybosspro.feature.carInsurance.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.carInsurance.model.CarInsuranceData
import com.policyboss.policybosspro.ui.components.bottomSheet.ClickableSelectorField
import com.policyboss.policybosspro.ui.components.dropDown.DropdownOption
import com.policyboss.policybosspro.ui.components.dropDown.GenericM3Dropdown
import com.policyboss.policybosspro.ui.components.radio.AppRadioButtonOption
import com.policyboss.policybosspro.ui.theme.AppColors

/**
 * Standardized Claim & NCB Section.
 * Features standard AppRadioButtonOption (Yes / No) for "Have you made a claim?"
 * and a standardized 52.dp height NCB field.
 *
 * When "Yes" is selected:
 * Automatically resets and locks NCB to "0%" in a disabled/read-only container with #F3F4F6 background.
 *
 * When "No" is selected:
 * Enables NCB selection via bottom sheet or dropdown.
 */
@Composable
fun ClaimAndNcbSection(
    hasClaim: Boolean,
    onClaimToggle: (Boolean) -> Unit,
    selectedNcb: DropdownOption<String>?,
    ncbOptions: List<DropdownOption<String>>,
    onNcbSelected: (DropdownOption<String>) -> Unit,
    modifier: Modifier = Modifier,
    claimQuestionLabel: String = "Made any claim in previous policy?",
    ncbLabel: String = "Select NCB",
    onNcbClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Question: Made any claim in previous policy?
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = claimQuestionLabel,
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
                    selected = hasClaim,
                    label = "Yes",
                    onClick = { onClaimToggle(true) }
                )

                AppRadioButtonOption(
                    selected = !hasClaim,
                    label = "No",
                    onClick = { onClaimToggle(false) }
                )
            }
        }

        // Question: Select NCB
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = ncbLabel,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary
            )

            if (hasClaim) {
                // Read-only container with 0% on neutral fill Color(0xFFF3F4F6) per specification
                OutlinedTextField(
                    value = "0%",
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = AppColors.TextPlaceholder
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledContainerColor = Color(0xFFF3F4F6),
                        disabledBorderColor = Color.Transparent,
                        disabledTextColor = AppColors.TextPlaceholder
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                )
            } else {
                if (onNcbClick != null) {
                    ClickableSelectorField(
                        label = "",
                        value = selectedNcb?.label ?: ncbOptions.firstOrNull()?.label ?: "0%",
                        placeholder = "Select NCB",
                        onClick = onNcbClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    GenericM3Dropdown(
                        label = "",
                        items = ncbOptions,
                        selectedItem = selectedNcb ?: ncbOptions.firstOrNull(),
                        onItemSelected = onNcbSelected,
                        placeholder = "Select NCB",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ClaimAndNcbSectionClaimYesPreview() {
    ClaimAndNcbSection(
        hasClaim = true,
        onClaimToggle = {},
        selectedNcb = DropdownOption("0", "0%"),
        ncbOptions = CarInsuranceData.ncbOptions,
        onNcbSelected = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun ClaimAndNcbSectionClaimNoPreview() {
    ClaimAndNcbSection(
        hasClaim = false,
        onClaimToggle = {},
        selectedNcb = DropdownOption("20", "20%"),
        ncbOptions = CarInsuranceData.ncbOptions,
        onNcbSelected = {}
    )
}
