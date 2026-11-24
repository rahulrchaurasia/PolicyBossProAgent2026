package com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.AddOnFilterItem
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.MotorQuoteFilterData
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.ZeroDepClaimLimit
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun AddOnsFilterSection(
    selectedAddOnIds: Set<String>,
    selectedZeroDepClaimLimit: ZeroDepClaimLimit,
    onToggleAddOn: (String) -> Unit,
    onToggleSelectAll: (Boolean) -> Unit,
    onSelectZeroDepClaimLimit: (ZeroDepClaimLimit) -> Unit,
    modifier: Modifier = Modifier,
    items: List<AddOnFilterItem> = MotorQuoteFilterData.addOnItems
) {
    val allAddOnsSelected = items.isNotEmpty() && items.all { selectedAddOnIds.contains(it.id) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        // Section Header
        Text(
            text = "Add-ons",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.Slate900
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 1. "Select All" Master Checkbox Row (Top of List)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { onToggleSelectAll(!allAddOnsSelected) }
                .padding(vertical = 8.dp, horizontal = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = allAddOnsSelected,
                onCheckedChange = { onToggleSelectAll(!allAddOnsSelected) },
                colors = CheckboxDefaults.colors(
                    checkedColor = AppColors.PrimaryBlue,
                    uncheckedColor = AppColors.BorderSecondary
                ),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Select All",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.TextPrimary
            )
        }

        HorizontalDivider(
            color = Color(0xFFF1F5F9),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // 2. Multi-Select Add-on Rows
        items.forEachIndexed { index, item ->
            val isSelected = selectedAddOnIds.contains(item.id)

            if (item.id == "zero_dep") {
                ZeroDepAddOnRow(
                    item = item,
                    isSelected = isSelected,
                    selectedClaimLimit = selectedZeroDepClaimLimit,
                    onToggleSelect = { onToggleAddOn(item.id) },
                    onSelectClaimLimit = onSelectZeroDepClaimLimit
                )
            } else {
                StandardAddOnRow(
                    item = item,
                    isSelected = isSelected,
                    onToggleSelect = { onToggleAddOn(item.id) }
                )
            }

            if (index < items.lastIndex) {
                HorizontalDivider(
                    color = Color(0xFFF1F5F9),
                    thickness = 0.8.dp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun ZeroDepAddOnRow(
    item: AddOnFilterItem,
    isSelected: Boolean,
    selectedClaimLimit: ZeroDepClaimLimit,
    onToggleSelect: () -> Unit,
    onSelectClaimLimit: (ZeroDepClaimLimit) -> Unit,
    modifier: Modifier = Modifier
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .padding(vertical = 4.dp, horizontal = 2.dp)
    ) {
        // Line 1: Parent Checkbox Row (Always Single-Line)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .clickable { onToggleSelect() }
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggleSelect() },
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
                color = AppColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }

        // Line 2: Conditional Claim-Limit Selector (Indented Sub-Row directly below Line 1)
        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 36.dp, top = 4.dp, bottom = 6.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { dropdownExpanded = true },
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, AppColors.BorderSecondary)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = selectedClaimLimit.displayName,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Claim limit selector",
                            tint = AppColors.TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    ZeroDepClaimLimit.values().forEach { limit ->
                        val isLimitSelected = limit == selectedClaimLimit
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = limit.displayName,
                                    fontSize = 14.sp,
                                    fontWeight = if (isLimitSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isLimitSelected) Color.White else AppColors.TextPrimary
                                )
                            },
                            onClick = {
                                onSelectClaimLimit(limit)
                                dropdownExpanded = false
                            },
                            modifier = Modifier.background(
                                if (isLimitSelected) AppColors.PrimaryBlue else Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StandardAddOnRow(
    item: AddOnFilterItem,
    isSelected: Boolean,
    onToggleSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onToggleSelect() }
            .padding(vertical = 8.dp, horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggleSelect() },
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

@Preview(name = "Add-ons Multi-Select Section", showBackground = true)
@Composable
private fun AddOnsFilterSectionPreview() {
    AddOnsFilterSection(
        selectedAddOnIds = setOf("zero_dep", "rsa"),
        selectedZeroDepClaimLimit = ZeroDepClaimLimit.ONE_CLAIM,
        onToggleAddOn = {},
        onToggleSelectAll = {},
        onSelectZeroDepClaimLimit = {}
    )
}
