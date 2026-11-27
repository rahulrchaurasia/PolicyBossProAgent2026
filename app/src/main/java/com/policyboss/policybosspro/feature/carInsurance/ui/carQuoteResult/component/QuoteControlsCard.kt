package com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteResult.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.carInsurance.model.CpaCoverageOption
import com.policyboss.policybosspro.feature.carInsurance.model.MotorQuote
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun QuoteControlsCard(
    make: String,
    model: String,
    fuel: String,
    variantText: String,
    crnNumber: String,
    registrationNumber: String,
    isNewCar: Boolean,
    idvAmount: Int = 0,
    carValueLabel: String = if (idvAmount == 0) "Set IDV" else "₹${MotorQuote.formatIndianCurrency(idvAmount)}",
    addonsCount: Int,
    addonsValue: String = "$addonsCount Addons",
    additionalCoverValue: String = "Select",
    selectedCpaOption: CpaCoverageOption,
    onVariantClick: () -> Unit,
    onIdvClick: () -> Unit,
    onIdvInfoClick: () -> Unit,
    onAddonsClick: () -> Unit,
    onAddonsInfoClick: () -> Unit,
    onAdditionalCoverClick: () -> Unit,
    onAdditionalCoverInfoClick: (() -> Unit)? = null,
    onSelectCpaOption: (CpaCoverageOption) -> Unit,
    onCpaOptionInfoClick: (CpaCoverageOption) -> Unit,
    modifier: Modifier = Modifier,
    /** TP-only policies have no car value (IDV) or add-ons, so these rows are hidden. */
    showCarValue: Boolean = true,
    showAddons: Boolean = true
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // ==========================================
            // Line 1: Make, Model, Fuel (Single Line, No Trailing Comma)
            // ==========================================
            val specsSummary = listOf(make, model, fuel).filter { it.isNotBlank() }.joinToString(", ")

            Text(
                text = specsSummary,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ==========================================
            // Line 2: Interactive Variant Selector Pill (Directly Below Line 1, Above CRN)
            // ==========================================
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onVariantClick() },
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, AppColors.BorderSecondary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = variantText.ifBlank { "Select variant" },
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Select Variant",
                        tint = AppColors.PrimaryBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ==========================================
            // Line 3: CRN Badge + Registration Number Row
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Vibrant Blue Gradient CRN Badge
                Box(
                    modifier = Modifier
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF1D4ED8), Color(0xFF3B82F6))
                            )
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CRN: ${crnNumber.ifBlank { "12678184" }}",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Car Registration Number with safe trailing clearance
                val displayRegNumber = if (isNewCar) {
                    "NEW CAR"
                } else {
                    registrationNumber.ifBlank { "RJ 14 CV 0002" }
                }
                Text(
                    text = displayRegNumber,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary,
                    modifier = Modifier.padding(end = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // Parameter Rows (Car Value, Addons, Additional Cover)
            // ==========================================
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Visible rows only; dividers go between rows, never before the first or after the last
                val rows = buildList<@Composable () -> Unit> {
                    if (showCarValue) add {
                        QuoteParameterRow(
                            label = "Car Value",
                            value = carValueLabel,
                            showInfoIcon = true,
                            onInfoClick = onIdvInfoClick,
                            onClick = onIdvClick
                        )
                    }
                    if (showAddons) add {
                        QuoteParameterRow(
                            label = "Addons",
                            value = addonsValue,
                            showInfoIcon = true,
                            onInfoClick = onAddonsInfoClick,
                            onClick = onAddonsClick
                        )
                    }
                    add {
                        QuoteParameterRow(
                            label = "Additional Cover",
                            value = additionalCoverValue,
                            showInfoIcon = onAdditionalCoverInfoClick != null,
                            onInfoClick = onAdditionalCoverInfoClick,
                            onClick = onAdditionalCoverClick
                        )
                    }
                }
                rows.forEachIndexed { index, row ->
                    if (index > 0) {
                        HorizontalDivider(
                            color = Color(0xFFE2E8F0).copy(alpha = 0.7f),
                            thickness = 0.8.dp
                        )
                    }
                    row()
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // Row 5, 6, 7: 3 Single-Line Custom Toggle Rows (Vertical Stack)
            // ==========================================
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Row 1: Zextra Switch
                CpaToggleRow(
                    isSelected = selectedCpaOption == CpaCoverageOption.ZEXTRA_SWITCH,
                    onToggle = { onSelectCpaOption(CpaCoverageOption.ZEXTRA_SWITCH) },
                    onInfoClick = { onCpaOptionInfoClick(CpaCoverageOption.ZEXTRA_SWITCH) },
                    leadContent = {
                        // Zextra Switch Logo Badge
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFDB833))
                                .padding(horizontal = 6.dp, vertical = 2.5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Zextra",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AppColors.Slate900
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(AppColors.PrimaryBlue)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "Switch",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                )

                // Row 2: Insurer CPA
                CpaToggleRow(
                    isSelected = selectedCpaOption == CpaCoverageOption.INSURER_CPA,
                    onToggle = { onSelectCpaOption(CpaCoverageOption.INSURER_CPA) },
                    onInfoClick = { onCpaOptionInfoClick(CpaCoverageOption.INSURER_CPA) },
                    leadContent = {
                        Text(
                            text = "Insurer CPA",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.TextPrimary,
                            maxLines = 1
                        )
                    }
                )

                // Row 3: No CPA
                CpaToggleRow(
                    isSelected = selectedCpaOption == CpaCoverageOption.NO_CPA,
                    onToggle = { onSelectCpaOption(CpaCoverageOption.NO_CPA) },
                    onInfoClick = { onCpaOptionInfoClick(CpaCoverageOption.NO_CPA) },
                    leadContent = {
                        Text(
                            text = "No CPA",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.TextPrimary,
                            maxLines = 1
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ==========================================
            // Bottom Banner: Stylish Zextra Qualification Strip
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFFDBEAFE), Color(0xFFBFDBFE))
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "This search qualifies for special Zextra Bundle - CPA/RSA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                    maxLines = 2
                )
            }
        }
    }
}

/**
 * Single-line parameter row with label + blue ⓘ info icon on the left,
 * and an interactive soft-blue editable value pill with an edit pencil icon on the right.
 */
@Composable
private fun QuoteParameterRow(
    label: String,
    value: String,
    showInfoIcon: Boolean = false,
    onInfoClick: (() -> Unit)? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Group: Parameter Label + Clickable Blue Info Icon (32.dp touch target)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.TextPrimary
            )

            if (showInfoIcon && onInfoClick != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable(
                            role = Role.Button,
                            onClick = onInfoClick
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "$label Info",
                        tint = AppColors.PrimaryBlue,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }

        // Right Group: Interactive Editable Value Pill with edit pencil icon
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onClick() },
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFEFF6FF),
            border = BorderStroke(1.dp, Color(0xFFBFDBFE))
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = value,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.PrimaryBlue,
                    maxLines = 1,
                    softWrap = false
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit $label",
                    tint = AppColors.PrimaryBlue,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

/**
 * Reusable single-line CPA toggle row with subtle border, left-aligned badge/text + red info icon,
 * and right-aligned sleek custom toggle switch.
 */
@Composable
private fun CpaToggleRow(
    isSelected: Boolean,
    onToggle: () -> Unit,
    onInfoClick: () -> Unit,
    leadContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBg by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFF0FDF4) else Color.White,
        animationSpec = tween(durationMillis = 200),
        label = "cpaRowBgColor"
    )
    val cardBorder by animateColorAsState(
        targetValue = if (isSelected) AppColors.SuccessGreenDark else AppColors.Slate200,
        animationSpec = tween(durationMillis = 200),
        label = "cpaRowBorderColor"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(cardBg)
            .border(1.dp, cardBorder, RoundedCornerShape(10.dp))
            .clickable { onToggle() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Content: Lead content + Red Info (ⓘ) icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                leadContent()

                Spacer(modifier = Modifier.width(8.dp))

                // Clickable Red Info (ⓘ) Icon
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Details",
                    tint = Color(0xFFDC2626), // AppColors.ErrorRed
                    modifier = Modifier
                        .size(18.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onInfoClick()
                        }
                )
            }

            // Right Content: Compact custom toggle switch
            CompactSwitch(
                checked = isSelected,
                onCheckedChange = { onToggle() }
            )
        }
    }
}

/**
 * Compact custom toggle switch matching the sleek proportions of PolicyBoss Pro cards.
 * Provides a slim width (38.dp) and height (22.dp) with smooth thumb sliding animation.
 */
@Composable
fun CompactSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 18.dp else 2.dp,
        animationSpec = tween(durationMillis = 200),
        label = "switchThumbOffset"
    )
    val trackColor by animateColorAsState(
        targetValue = if (checked) AppColors.Slate800 else AppColors.Slate300,
        animationSpec = tween(durationMillis = 200),
        label = "switchTrackColor"
    )

    Box(
        modifier = modifier
            .size(width = 38.dp, height = 22.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(trackColor)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(18.dp)
                .shadow(elevation = 2.dp, shape = CircleShape)
                .background(color = Color.White, shape = CircleShape)
        )
    }
}

@Preview(name = "Quote Controls Card - Interactive", showBackground = true)
@Composable
private fun QuoteControlsCardInteractivePreview() {
    var selectedOption by remember { mutableStateOf(CpaCoverageOption.ZEXTRA_SWITCH) }

    QuoteControlsCard(
        make = "Kia",
        model = "Seltos",
        fuel = "Diesel",
        variantText = "PLUS 1.5 GTX AT (1493 CC)",
        crnNumber = "12678184",
        registrationNumber = "RJ 14 CV 0002",
        isNewCar = false,
        idvAmount = 986745,
        addonsCount = 17,
        addonsValue = "17 Addons",
        additionalCoverValue = "1 Selected",
        selectedCpaOption = selectedOption,
        onVariantClick = {},
        onIdvClick = {},
        onIdvInfoClick = {},
        onAddonsClick = {},
        onAddonsInfoClick = {},
        onAdditionalCoverClick = {},
        onAdditionalCoverInfoClick = {},
        onSelectCpaOption = { selectedOption = it },
        onCpaOptionInfoClick = {}
    )
}

@Preview(name = "Quote Controls Card - Insurer CPA", showBackground = true)
@Composable
private fun QuoteControlsCardInsurerCpaPreview() {
    QuoteControlsCard(
        make = "Kia",
        model = "Seltos",
        fuel = "Diesel",
        variantText = "PLUS 1.5 GTX AT (1493 CC)",
        crnNumber = "12678184",
        registrationNumber = "RJ 14 CV 0002",
        isNewCar = false,
        idvAmount = 986745,
        addonsCount = 17,
        addonsValue = "17 Addons",
        additionalCoverValue = "Select",
        selectedCpaOption = CpaCoverageOption.INSURER_CPA,
        onVariantClick = {},
        onIdvClick = {},
        onIdvInfoClick = {},
        onAddonsClick = {},
        onAddonsInfoClick = {},
        onAdditionalCoverClick = {},
        onAdditionalCoverInfoClick = {},
        onSelectCpaOption = {},
        onCpaOptionInfoClick = {}
    )
}

@Preview(name = "Quote Controls Card - New Car", showBackground = true)
@Composable
private fun QuoteControlsCardNewCarPreview() {
    QuoteControlsCard(
        make = "Hyundai",
        model = "Creta",
        fuel = "Petrol",
        variantText = "1.5 SX (O) CVT (1497 CC)",
        crnNumber = "12984120",
        registrationNumber = "",
        isNewCar = true,
        idvAmount = 1450000,
        addonsCount = 17,
        addonsValue = "17 Addons",
        additionalCoverValue = "Select",
        selectedCpaOption = CpaCoverageOption.NO_CPA,
        onVariantClick = {},
        onIdvClick = {},
        onIdvInfoClick = {},
        onAddonsClick = {},
        onAddonsInfoClick = {},
        onAdditionalCoverClick = {},
        onAdditionalCoverInfoClick = {},
        onSelectCpaOption = {},
        onCpaOptionInfoClick = {}
    )
}

@Preview(name = "Quote Controls - TP only", showBackground = true)
@Composable
private fun QuoteControlsCardInteractivePreviewTp() {
    var selectedOption by remember { mutableStateOf(CpaCoverageOption.ZEXTRA_SWITCH) }

    QuoteControlsCard(
        make = "Kia",
        model = "Seltos",
        fuel = "Diesel",
        variantText = "PLUS 1.5 GTX AT (1493 CC)",
        crnNumber = "12678184",
        registrationNumber = "RJ 14 CV 0002",
        isNewCar = false,
        idvAmount = 986745,
        addonsCount = 17,
        addonsValue = "17 Addons",
        additionalCoverValue = "1 Selected",
        selectedCpaOption = selectedOption,
        onVariantClick = {},
        onIdvClick = {},
        onIdvInfoClick = {},
        onAddonsClick = {},
        onAddonsInfoClick = {},
        onAdditionalCoverClick = {},
        onAdditionalCoverInfoClick = {},
        onSelectCpaOption = { selectedOption = it },
        onCpaOptionInfoClick = {},
        showCarValue = false,
        showAddons = false
    )
}
