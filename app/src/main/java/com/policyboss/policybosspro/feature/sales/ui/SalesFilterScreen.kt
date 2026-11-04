package com.policyboss.policybosspro.feature.sales.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.sales.model.FilterCategory
import com.policyboss.policybosspro.feature.sales.model.SalesAction
import com.policyboss.policybosspro.feature.sales.model.SalesFilterState
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun SalesFilterScreen(
    filterState: SalesFilterState,
    onAction: (SalesAction) -> Unit,
    onClose: () -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onClose()
    }

    val panelBackground = AppColors.FilterPanelBackground

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        // 1. Top Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left column header segment
            Box(
                modifier = Modifier
                    .width(115.dp)
                    .fillMaxHeight()
                    .background(panelBackground)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Filter",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Slate900
                )
            }

            // Right header segment with Close (X) icon
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.White)
                    .padding(end = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = AppColors.Slate900,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // 2. Main Two-Panel Body
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Left Category Navigation Panel
            Column(
                modifier = Modifier
                    .width(115.dp)
                    .fillMaxHeight()
                    .background(panelBackground)
            ) {
                FilterCategory.values().forEach { category ->
                    val isSelected = filterState.selectedCategory == category
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .clickable {
                                onAction(SalesAction.SelectFilterCategory(category))
                            }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = category.displayName,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) AppColors.Slate900 else AppColors.Slate600
                        )
                    }
                }
            }

            // Right Options Content Panel
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Category Title Heading
                Text(
                    text = "Filter by ${filterState.selectedCategory.displayName}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Slate900
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Search field for Sub User and Sub POSP
                when (filterState.selectedCategory) {
                    FilterCategory.PRODUCT -> {
                        // No search field for Product
                    }
                    FilterCategory.SUB_USER -> {
                        FilterSearchField(
                            query = filterState.subUserSearchQuery,
                            onQueryChange = { onAction(SalesAction.UpdateSubUserSearch(it)) },
                            placeholder = "Search by name"
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                    FilterCategory.SUB_POSP -> {
                        FilterSearchField(
                            query = filterState.subPospSearchQuery,
                            onQueryChange = { onAction(SalesAction.UpdateSubPospSearch(it)) },
                            placeholder = "Search by name"
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }

                // Options List
                val (itemsList, selectedSet) = when (filterState.selectedCategory) {
                    FilterCategory.PRODUCT -> Pair(filterState.availableProducts, filterState.selectedProducts)
                    FilterCategory.SUB_USER -> Pair(filterState.filteredSubUsers, filterState.selectedSubUsers)
                    FilterCategory.SUB_POSP -> Pair(filterState.filteredSubPosps, filterState.selectedSubPosps)
                }

                if (itemsList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No results found",
                            fontSize = 13.sp,
                            color = AppColors.Slate400
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(itemsList, key = { it }) { item ->
                            val isChecked = selectedSet.contains(item)
                            FilterOptionRow(
                                name = item,
                                isChecked = isChecked,
                                onToggle = {
                                    when (filterState.selectedCategory) {
                                        FilterCategory.PRODUCT -> onAction(SalesAction.ToggleProductFilter(item))
                                        FilterCategory.SUB_USER -> onAction(SalesAction.ToggleSubUserFilter(item))
                                        FilterCategory.SUB_POSP -> onAction(SalesAction.ToggleSubPospFilter(item))
                                    }
                                }
                            )
                            HorizontalDivider(
                                thickness = 0.8.dp,
                                color = AppColors.Slate100
                            )
                        }
                    }
                }
            }
        }

        // 3. Bottom Fixed Action Area
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            color = Color.White,
            border = BorderStroke(1.dp, AppColors.Slate100)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Surface(
                    onClick = onApply,
                    shape = RoundedCornerShape(26.dp),
                    color = AppColors.PrimaryBlue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val count = filterState.totalSelectedCount
                        val buttonText = if (count > 0) "Apply filters ($count)" else "Apply filters"

                        Text(
                            text = buttonText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        // Circular forward arrow icon on the right end
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier
                                .size(36.dp)
                                .align(Alignment.CenterEnd)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Apply",
                                    tint = AppColors.PrimaryBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(BorderStroke(1.dp, AppColors.Slate200), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = "Search",
            tint = AppColors.Slate400,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.CenterStart
        ) {
            if (query.isEmpty()) {
                Text(
                    text = placeholder,
                    color = AppColors.Slate400,
                    fontSize = 13.sp
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 13.sp,
                    color = AppColors.Slate900,
                    fontWeight = FontWeight.Normal
                ),
                cursorBrush = SolidColor(AppColors.PrimaryBlue),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (query.isNotEmpty()) {
            IconButton(
                onClick = { onQueryChange("") },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    tint = AppColors.Slate400,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun FilterOptionRow(
    name: String,
    isChecked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            fontSize = 14.sp,
            fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
            color = AppColors.Slate900,
            modifier = Modifier.weight(1f)
        )

        // Custom Checkbox
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(
                    color = if (isChecked) AppColors.PrimaryBlue else Color.Transparent,
                    shape = RoundedCornerShape(4.dp)
                )
                .border(
                    BorderStroke(
                        width = 1.5.dp,
                        color = if (isChecked) AppColors.PrimaryBlue else AppColors.Slate300
                    ),
                    shape = RoundedCornerShape(4.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isChecked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 700)
@Composable
private fun SalesFilterScreenProductPreview() {
    SalesFilterScreen(
        filterState = SalesFilterState(
            selectedCategory = FilterCategory.PRODUCT,
            availableProducts = listOf("Car", "Two Wheeler", "CV", "Travel", "Health", "Life", "SME"),
            selectedProducts = setOf("Car")
        ),
        onAction = {},
        onClose = {},
        onApply = {}
    )
}

@Preview(showBackground = true, widthDp = 380, heightDp = 700)
@Composable
private fun SalesFilterScreenSubUserPreview() {
    SalesFilterScreen(
        filterState = SalesFilterState(
            selectedCategory = FilterCategory.SUB_USER,
            availableSubUsers = listOf("Gopal Verma", "Ananya Singh", "Ravi Kumar", "Sofia Patel", "Arjun Mehta", "Rajesh Shah"),
            selectedSubUsers = setOf("Gopal Verma", "Ananya Singh")
        ),
        onAction = {},
        onClose = {},
        onApply = {}
    )
}

@Preview(showBackground = true, widthDp = 380, heightDp = 700)
@Composable
private fun SalesFilterScreenSubPospPreview() {
    SalesFilterScreen(
        filterState = SalesFilterState(
            selectedCategory = FilterCategory.SUB_POSP,
            availableSubPosps = listOf("Arjun Mehta", "Karan Singh", "Ravi Kumar", "Amit Patel", "Suresh Nair"),
            selectedSubPosps = setOf("Arjun Mehta", "Amit Patel")
        ),
        onAction = {},
        onClose = {},
        onApply = {}
    )
}
