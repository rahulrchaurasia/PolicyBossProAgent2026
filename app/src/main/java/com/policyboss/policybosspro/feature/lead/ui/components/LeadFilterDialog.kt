package com.policyboss.policybosspro.feature.lead.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.policyboss.policybosspro.feature.lead.model.FilterCategory
import com.policyboss.policybosspro.feature.lead.model.FilterCriteria
import com.policyboss.policybosspro.feature.lead.model.LeadAction
import com.policyboss.policybosspro.feature.lead.model.LeadSortOption
import com.policyboss.policybosspro.ui.components.button.PrimaryCTAButton
import com.policyboss.policybosspro.ui.components.radio.AppRadioButtonOption
import com.policyboss.policybosspro.ui.theme.AppColors
import kotlin.math.roundToInt

@Composable
fun LeadFilterDialog(
    activeCategory: FilterCategory,
    criteria: FilterCriteria,
    onAction: (LeadAction) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        LeadFilterDialogContent(
            activeCategory = activeCategory,
            criteria = criteria,
            onAction = onAction,
            onDismiss = onDismiss
        )
    }
}

@Composable
internal fun LeadFilterDialogContent(
    activeCategory: FilterCategory,
    criteria: FilterCriteria,
    onAction: (LeadAction) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = AppColors.White
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { onAction(LeadAction.ResetFilters) }) {
                    Text(
                        text = "Reset",
                        color = AppColors.TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "Filters",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = AppColors.TextPrimary
                    )
                }
            }

            HorizontalDivider(color = AppColors.BorderLight, thickness = 1.dp)

            // Split rail view
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Left Rail: Categories
                Column(
                    modifier = Modifier
                        .width(130.dp)
                        .fillMaxHeight()
                        .background(AppColors.FilterPanelBackground)
                ) {
                    FilterCategory.entries.forEach { category ->
                        val isSelected = activeCategory == category
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAction(LeadAction.OnSelectFilterCategory(category)) }
                                .background(if (isSelected) AppColors.White else Color.Transparent)
                                .padding(horizontal = 16.dp, vertical = 18.dp)
                        ) {
                            Text(
                                text = category.displayName,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) AppColors.PrimaryBlue else AppColors.TextPrimary
                            )
                        }
                    }
                }

                // Right Content: Options for Active Category
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(AppColors.White)
                        .padding(16.dp)
                ) {
                    when (activeCategory) {
                        FilterCategory.STAGE -> StageFilterContent(criteria, onAction)
                        FilterCategory.VEHICLE_TYPE -> VehicleFilterContent(criteria, onAction)
                        FilterCategory.RENEWAL_TIME -> RenewalFilterContent(criteria, onAction)
                        FilterCategory.EARNINGS -> EarningsFilterContent(criteria, onAction)
                        FilterCategory.RELATIONSHIP -> RelationshipFilterContent(criteria, onAction)
                        FilterCategory.SORT_BY -> SortFilterContent(criteria, onAction)
                    }
                }
            }

            // Bottom apply footer reusing PrimaryCTAButton
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                shadowElevation = 8.dp,
                color = AppColors.White
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    PrimaryCTAButton(
                        text = "Apply filters (${criteria.totalActiveFiltersCount})",
                        onClick = { onAction(LeadAction.ApplyFilters) },
                        showArrow = true,
                        textStyle = MaterialTheme.typography.titleSmall.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        containerColor = AppColors.PrimaryBlue,
                        contentColor = AppColors.White,
                        arrowBackgroundColor = AppColors.White,
                        arrowTint = AppColors.PrimaryBlue
                    )
                }
            }
        }
    }
}

@Composable
private fun StageFilterContent(criteria: FilterCriteria, onAction: (LeadAction) -> Unit) {
    val stages = listOf(
        "Fresh/New" to "New leads that need your attention",
        "Ongoing" to "Leads you're currently working on",
        "Won" to "Leads converted into policies",
        "Lost" to "Leads that did not convert"
    )
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(stages, key = { it.first }) { (title, subtitle) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction(LeadAction.ToggleStageFilter(title)) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = AppColors.TextPrimary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = AppColors.TextSecondary
                    )
                }
                Checkbox(
                    checked = criteria.stages.contains(title),
                    onCheckedChange = { onAction(LeadAction.ToggleStageFilter(title)) },
                    colors = CheckboxDefaults.colors(checkedColor = AppColors.PrimaryBlue)
                )
            }
        }
    }
}

@Composable
private fun VehicleFilterContent(criteria: FilterCriteria, onAction: (LeadAction) -> Unit) {
    val vehicles = listOf("Car", "Two Wheeler", "Commercial Vehicle")
    CheckboxOptionList(
        options = vehicles,
        selectedOptions = criteria.vehicleTypes,
        onToggle = { onAction(LeadAction.ToggleVehicleTypeFilter(it)) }
    )
}

@Composable
private fun RenewalFilterContent(criteria: FilterCriteria, onAction: (LeadAction) -> Unit) {
    val times = listOf("7 days", "7-14 days", "14+ days")
    CheckboxOptionList(
        options = times,
        selectedOptions = criteria.renewalTimes,
        onToggle = { onAction(LeadAction.ToggleRenewalTimeFilter(it)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EarningsFilterContent(criteria: FilterCriteria, onAction: (LeadAction) -> Unit) {
    Column {
        Text(
            text = "Filter by Earnings",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = AppColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "₹${criteria.earningsRange.start.roundToInt()}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )
            Text(
                text = "₹${criteria.earningsRange.endInclusive.roundToInt()}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )
        }
        RangeSlider(
            value = criteria.earningsRange,
            onValueChange = { onAction(LeadAction.UpdateEarningsRange(it)) },
            valueRange = FilterCriteria.DEFAULT_EARNINGS_RANGE,
            colors = SliderDefaults.colors(
                thumbColor = AppColors.PrimaryBlue,
                activeTrackColor = AppColors.PrimaryBlue,
                inactiveTrackColor = AppColors.Slate200
            )
        )
    }
}

@Composable
private fun RelationshipFilterContent(criteria: FilterCriteria, onAction: (LeadAction) -> Unit) {
    val relations = listOf("Family (1)", "Friends (4)", "Random/Others (4)")
    CheckboxOptionList(
        options = relations,
        selectedOptions = criteria.relationships,
        onToggle = { onAction(LeadAction.ToggleRelationshipFilter(it)) }
    )
}

@Composable
private fun SortFilterContent(criteria: FilterCriteria, onAction: (LeadAction) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(LeadSortOption.entries, key = { it.name }) { opt ->
            AppRadioButtonOption(
                selected = criteria.sortBy == opt,
                onClick = { onAction(LeadAction.SelectSortOption(opt)) },
                selectedColor = AppColors.PrimaryBlue,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                spacing = 12.dp
            ) {
                Text(
                    text = opt.displayName,
                    fontSize = 14.sp,
                    fontWeight = if (criteria.sortBy == opt) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (criteria.sortBy == opt) AppColors.PrimaryBlue else AppColors.TextPrimary
                )
            }
        }
    }
}

@Composable
private fun CheckboxOptionList(
    options: List<String>,
    selectedOptions: Set<String>,
    onToggle: (String) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(options, key = { it }) { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle(option) }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = option,
                    fontSize = 14.sp,
                    color = AppColors.TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Checkbox(
                    checked = selectedOptions.contains(option),
                    onCheckedChange = { onToggle(option) },
                    colors = CheckboxDefaults.colors(checkedColor = AppColors.PrimaryBlue)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 700)
@Composable
private fun LeadFilterDialogPreview() {
    LeadFilterDialogContent(
        activeCategory = FilterCategory.STAGE,
        criteria = FilterCriteria(stages = setOf("Fresh/New"), vehicleTypes = setOf("Car")),
        onAction = {},
        onDismiss = {}
    )
}

