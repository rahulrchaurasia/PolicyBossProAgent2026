package com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.LiabilityCoverItem
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.MotorQuoteFilterData
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun LiabilityCoversSection(
    selectedLiabilityCoverIds: Set<String>,
    selectedUnNamedPassengerCover: String?,
    electricalAccessoriesAmount: String,
    nonElectricalAccessoriesAmount: String,
    onToggleLiabilityCover: (String) -> Unit,
    onSelectUnNamedPassengerCover: (String) -> Unit,
    onUpdateElectricalAccessories: (String) -> Unit,
    onUpdateNonElectricalAccessories: (String) -> Unit,
    modifier: Modifier = Modifier,
    items: List<LiabilityCoverItem> = MotorQuoteFilterData.additionalCoverItems,
    unNamedOptions: List<String> = MotorQuoteFilterData.unNamedPassengerOptions,
    // Retained for backward-compatibility if invoked from older callers
    liabilityCoverRanges: Map<String, ClosedFloatingPointRange<Float>> = emptyMap(),
    onUpdateLiabilityRange: ((String, ClosedFloatingPointRange<Float>) -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current

    // Accessory bound validation (if > 0, must be in 10,000..50,000)
    val electricalAmount = electricalAccessoriesAmount.filter { it.isDigit() }.toIntOrNull() ?: 0
    val electricalError = if (electricalAmount > 0 && (electricalAmount < 10000 || electricalAmount > 50000)) {
        "Amount must be between ₹10,000 and ₹50,000"
    } else null

    val nonElectricalAmount = nonElectricalAccessoriesAmount.filter { it.isDigit() }.toIntOrNull() ?: 0
    val nonElectricalError = if (nonElectricalAmount > 0 && (nonElectricalAmount < 10000 || nonElectricalAmount > 50000)) {
        "Amount must be between ₹10,000 and ₹50,000"
    } else null

    val effectivePassengerSelection = selectedUnNamedPassengerCover ?: "No"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        // Section Header
        Text(
            text = "Additional Cover",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.Slate900
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ==========================================
        // Group 1: Multi-Select Liability Cover Checkboxes
        // ==========================================
        items.forEach { item ->
            val isChecked = selectedLiabilityCoverIds.contains(item.id)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onToggleLiabilityCover(item.id) }
                    .padding(vertical = 8.dp, horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { onToggleLiabilityCover(item.id) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = AppColors.PrimaryBlue,
                        uncheckedColor = AppColors.BorderSecondary
                    ),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = item.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.TextPrimary
                )
            }
        }

        HorizontalDivider(
            color = Color(0xFFF1F5F9),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        // ==========================================
        // Group 2: Un-Named Passenger Cover (2x2 Radio Grid)
        // ==========================================
        Text(
            text = "Un-Named Passenger Cover :",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Row 1: No | ₹ 50,000
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PassengerRadioOption(
                label = "No",
                isSelected = (effectivePassengerSelection == "No"),
                onSelect = { onSelectUnNamedPassengerCover("No") },
                modifier = Modifier.weight(1f)
            )
            PassengerRadioOption(
                label = "₹ 50,000",
                isSelected = (effectivePassengerSelection == "₹ 50,000"),
                onSelect = { onSelectUnNamedPassengerCover("₹ 50,000") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Row 2: ₹ 1,00,000 | ₹ 2,00,000
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PassengerRadioOption(
                label = "₹ 1,00,000",
                isSelected = (effectivePassengerSelection == "₹ 1,00,000"),
                onSelect = { onSelectUnNamedPassengerCover("₹ 1,00,000") },
                modifier = Modifier.weight(1f)
            )
            PassengerRadioOption(
                label = "₹ 2,00,000",
                isSelected = (effectivePassengerSelection == "₹ 2,00,000"),
                onSelect = { onSelectUnNamedPassengerCover("₹ 2,00,000") },
                modifier = Modifier.weight(1f)
            )
        }

        HorizontalDivider(
            color = Color(0xFFF1F5F9),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        // ==========================================
        // Group 3: Accessories Rupee Numeric Inputs
        // ==========================================
        // 1. Electrical Accessories
        Text(
            text = "Electrical Accessories:",
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        AccessoryInputField(
            value = electricalAccessoriesAmount,
            onValueChange = onUpdateElectricalAccessories,
            isError = electricalError != null,
            imeAction = ImeAction.Next,
            keyboardActions = KeyboardActions(onNext = { focusManager.clearFocus() })
        )

        Text(
            text = "Min: 10,000 To Max: 50,000",
            fontSize = 12.sp,
            color = AppColors.TextSecondary,
            modifier = Modifier.padding(top = 4.dp, start = 2.dp)
        )

        if (electricalError != null) {
            Text(
                text = electricalError,
                fontSize = 11.5.sp,
                color = Color(0xFFDC2626),
                modifier = Modifier.padding(top = 2.dp, start = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Non-Electrical Accessories
        Text(
            text = "Non-Electrical Accessories:",
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        AccessoryInputField(
            value = nonElectricalAccessoriesAmount,
            onValueChange = onUpdateNonElectricalAccessories,
            isError = nonElectricalError != null,
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )

        Text(
            text = "Min: 10,000 To Max: 50,000",
            fontSize = 12.sp,
            color = AppColors.TextSecondary,
            modifier = Modifier.padding(top = 4.dp, start = 2.dp)
        )

        if (nonElectricalError != null) {
            Text(
                text = nonElectricalError,
                fontSize = 11.5.sp,
                color = Color(0xFFDC2626),
                modifier = Modifier.padding(top = 2.dp, start = 2.dp)
            )
        }
    }
}

@Composable
private fun PassengerRadioOption(
    label: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { onSelect() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = AppColors.PrimaryBlue,
                unselectedColor = Color(0xFFCBD5E1)
            ),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Normal,
            color = AppColors.TextPrimary
        )
    }
}

@Composable
private fun AccessoryInputField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val borderColor = if (isError) Color(0xFFDC2626) else Color(0xFFCBD5E1)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "₹",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.TextSecondary
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = "0",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextSecondary
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = { input ->
                        val digits = input.filter { it.isDigit() }.take(6)
                        onValueChange(digits)
                    },
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextPrimary
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = imeAction
                    ),
                    keyboardActions = keyboardActions,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(name = "Additional Cover - Default", showBackground = true, widthDp = 260)
@Composable
private fun LiabilityCoversSectionDefaultPreview() {
    LiabilityCoversSection(
        selectedLiabilityCoverIds = setOf("owner_driver_pa"),
        selectedUnNamedPassengerCover = "No",
        electricalAccessoriesAmount = "0",
        nonElectricalAccessoriesAmount = "0",
        onToggleLiabilityCover = {},
        onSelectUnNamedPassengerCover = {},
        onUpdateElectricalAccessories = {},
        onUpdateNonElectricalAccessories = {}
    )
}

@Preview(name = "Additional Cover - Selected", showBackground = true, widthDp = 260)
@Composable
private fun LiabilityCoversSectionSelectedPreview() {
    LiabilityCoversSection(
        selectedLiabilityCoverIds = setOf("owner_driver_pa", "paid_driver_liability"),
        selectedUnNamedPassengerCover = "₹ 50,000",
        electricalAccessoriesAmount = "15000",
        nonElectricalAccessoriesAmount = "20000",
        onToggleLiabilityCover = {},
        onSelectUnNamedPassengerCover = {},
        onUpdateElectricalAccessories = {},
        onUpdateNonElectricalAccessories = {}
    )
}
