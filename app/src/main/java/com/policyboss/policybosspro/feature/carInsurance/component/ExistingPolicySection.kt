package com.policyboss.policybosspro.feature.carInsurance.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.carInsurance.model.CarInsuranceData
import com.policyboss.policybosspro.ui.components.dropDown.DropdownOption
import com.policyboss.policybosspro.ui.components.dropDown.GenericM3Dropdown
import com.policyboss.policybosspro.ui.components.radio.AppRadioButtonOption
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun ExistingPolicySection(
    hasExistingPolicy: Boolean,
    onExistingPolicyToggle: (Boolean) -> Unit,
    selectedInsurer: DropdownOption<String>?,
    insurerOptions: List<DropdownOption<String>>,
    onInsurerSelected: (DropdownOption<String>) -> Unit,
    policyExpiryDate: String,
    onExpiryDateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Question: Do you have an existing policy?
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Do you have an existing policy?",
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

        // Conditional fields if Yes
        if (hasExistingPolicy) {
            // Previous Insurer Dropdown
            GenericM3Dropdown(
                label = "Previous insurer",
                items = insurerOptions,
                selectedItem = selectedInsurer,
                onItemSelected = onInsurerSelected,
                modifier = Modifier.fillMaxWidth()
            )

            // Policy Expiry Date Picker Field
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Policy expiry",
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
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.PrimaryBlue,
                        unfocusedBorderColor = AppColors.BorderSecondary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onExpiryDateClick() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExistingPolicySectionPreview() {
    ExistingPolicySection(
        hasExistingPolicy = true,
        onExistingPolicyToggle = {},
        selectedInsurer = DropdownOption("ACKO", "Acko General Insurance", R.drawable.ic_acko_logo),
        insurerOptions = CarInsuranceData.insurerOptions,
        onInsurerSelected = {},
        policyExpiryDate = "22/08/2027",
        onExpiryDateClick = {}
    )
}
