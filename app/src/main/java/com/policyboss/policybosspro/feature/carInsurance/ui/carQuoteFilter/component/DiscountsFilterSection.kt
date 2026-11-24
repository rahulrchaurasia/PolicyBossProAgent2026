package com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.MotorQuoteFilterData
import com.policyboss.policybosspro.ui.components.radio.AppRadioButtonOption
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun DiscountsFilterSection(
    antiTheftDevice: Boolean,
    payAsYouDrive: Boolean,
    voluntaryExcess: String,
    onUpdateAntiTheft: (Boolean) -> Unit,
    onUpdatePayAsYouDrive: (Boolean) -> Unit,
    onSelectVoluntaryExcess: (String) -> Unit,
    modifier: Modifier = Modifier,
    excessOptions: List<String> = MotorQuoteFilterData.voluntaryExcessOptions
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        // Section Header
        Text(
            text = "Discounts",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.Slate900
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Anti theft device
        Text(
            text = "Anti theft device:",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.Slate900
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppRadioButtonOption(
                selected = antiTheftDevice,
                label = "Yes",
                onClick = { onUpdateAntiTheft(true) },
                selectedColor = AppColors.PrimaryBlue
            )

            AppRadioButtonOption(
                selected = !antiTheftDevice,
                label = "No",
                onClick = { onUpdateAntiTheft(false) },
                selectedColor = AppColors.PrimaryBlue
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Pay as you drive
        Text(
            text = "Pay as you drive:",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.Slate900
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppRadioButtonOption(
                selected = payAsYouDrive,
                label = "Yes",
                onClick = { onUpdatePayAsYouDrive(true) },
                selectedColor = AppColors.PrimaryBlue
            )

            AppRadioButtonOption(
                selected = !payAsYouDrive,
                label = "No",
                onClick = { onUpdatePayAsYouDrive(false) },
                selectedColor = AppColors.PrimaryBlue
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. Voluntry Excess
        Text(
            text = "Voluntry Excess",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.Slate900
        )

        Spacer(modifier = Modifier.height(10.dp))

        excessOptions.forEach { option ->
            val isSelected = voluntaryExcess == option
            val displayText = if (option == "No") "No" else "₹$option"

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFAFAFA))
                    .border(
                        1.dp,
                        if (isSelected) Color(0xFFBFDBFE) else Color(0xFFF1F5F9),
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelectVoluntaryExcess(option) }
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = displayText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.Slate900
                    )

                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectVoluntaryExcess(option) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = AppColors.PrimaryBlue,
                            unselectedColor = AppColors.Slate300
                        ),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DiscountsFilterSectionPreview() {
    DiscountsFilterSection(
        antiTheftDevice = true,
        payAsYouDrive = true,
        voluntaryExcess = "No",
        onUpdateAntiTheft = {},
        onUpdatePayAsYouDrive = {},
        onSelectVoluntaryExcess = {}
    )
}
