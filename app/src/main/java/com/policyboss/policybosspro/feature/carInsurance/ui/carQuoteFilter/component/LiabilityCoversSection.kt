package com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.LiabilityCoverItem
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.MotorQuoteFilterData
import com.policyboss.policybosspro.ui.components.checkbox.AppCheckboxOption
import com.policyboss.policybosspro.ui.components.radio.AppRadioButtonOption
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
    /** Electrical / Non-Electrical accessories are own-damage covers: hidden for TP-only. */
    showAccessories: Boolean = true,
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
            AppCheckboxOption(
                checked = selectedLiabilityCoverIds.contains(item.id),
                onCheckedChange = { onToggleLiabilityCover(item.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 9.dp, horizontal = 2.dp),
                spacing = 12.dp
            ) {
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

        UnNamedPassengerOptions(
            options = unNamedOptions,
            selected = effectivePassengerSelection,
            onSelect = onSelectUnNamedPassengerCover
        )

        if (showAccessories) {
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
}

/**
 * Un-Named Passenger Cover choices. Two columns when each cell has room for a one-line label,
 * otherwise a single column, so "₹ 1,00,000" never splits across lines.
 */
@Composable
private fun UnNamedPassengerOptions(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val columns = if (maxWidth / 2 >= MIN_PASSENGER_CELL_WIDTH) 2 else 1
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            options.chunked(columns).forEach { rowOptions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    rowOptions.forEach { option ->
                        AppRadioButtonOption(
                            selected = option == selected,
                            onClick = { onSelect(option) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                // Non-breaking space keeps "₹" with the amount; the value sent stays unchanged
                                text = option.replace(' ', '\u00A0'),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = AppColors.TextPrimary,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    // Keep the grid aligned when the last row is not full
                    repeat(columns - rowOptions.size) { Spacer(modifier = Modifier.weight(1f)) }
                }
            }
        }
    }
}

/** Radio (20.dp) + spacing (8.dp) + one-line "₹ 2,00,000" at 14.sp. */
private val MIN_PASSENGER_CELL_WIDTH = 116.dp

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

@Preview(name = "Additional Cover - TP (no accessories)", showBackground = true, widthDp = 360)
@Composable
private fun LiabilityCoversSectionTpPreview() {
    LiabilityCoversSection(
        selectedLiabilityCoverIds = setOf("owner_driver_pa"),
        selectedUnNamedPassengerCover = "₹ 1,00,000",
        electricalAccessoriesAmount = "0",
        nonElectricalAccessoriesAmount = "0",
        onToggleLiabilityCover = {},
        onSelectUnNamedPassengerCover = {},
        onUpdateElectricalAccessories = {},
        onUpdateNonElectricalAccessories = {},
        showAccessories = false
    )
}

@Preview(name = "Additional Cover - Company (3 covers, no owner driver)", showBackground = true, widthDp = 260)
@Composable
private fun LiabilityCoversSectionCompanyPreview() {
    LiabilityCoversSection(
        selectedLiabilityCoverIds = emptySet(),
        selectedUnNamedPassengerCover = "No",
        electricalAccessoriesAmount = "0",
        nonElectricalAccessoriesAmount = "0",
        items = MotorQuoteFilterData.additionalCoverItems.filter { it.id != "owner_driver_pa" },
        onToggleLiabilityCover = {},
        onSelectUnNamedPassengerCover = {},
        onUpdateElectricalAccessories = {},
        onUpdateNonElectricalAccessories = {}
    )
}
