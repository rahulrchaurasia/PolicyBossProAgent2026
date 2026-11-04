package com.policyboss.policybosspro.feature.sales.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.sales.model.SalesAction
import com.policyboss.policybosspro.feature.sales.model.SalesUiState
import com.policyboss.policybosspro.feature.sales.ui.components.SalePolicyCard
import com.policyboss.policybosspro.ui.components.datePicker.DateRangeBar
import com.policyboss.policybosspro.ui.components.datePicker.DateRangeCalendarCard
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun SalesScreen(
    uiState: SalesUiState,
    onViewDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
    onOpenFilter: () -> Unit = {},
    onAction: (SalesAction) -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .background(AppColors.Slate50)
    ) {
        if (uiState.isLoading && uiState.policies.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AppColors.Primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header (Sales, Search, Filter)
                item(key = "sales_header") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sales",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Slate900
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { /* Visual search control */ },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_search),
                                    contentDescription = "Search",
                                    tint = AppColors.Slate600,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(
                                onClick = onOpenFilter,
                                modifier = Modifier.size(36.dp)
                            ) {
                                val hasFilters = uiState.appliedFilterState.totalSelectedCount > 0
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_sort_by),
                                    contentDescription = "Filter",
                                    tint = if (hasFilters) AppColors.PrimaryBlue else AppColors.Slate600,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Date Range Selector Bar
                item(key = "date_range_selector") {
                    DateRangeBar(
                        dateRange = uiState.dateRange,
                        isExpanded = uiState.showDateRangeSelector,
                        onClick = { onAction(SalesAction.ToggleDateRangeSelector) },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AppColors.Slate200),
                        iconTint = AppColors.Slate500,
                        arrowTint = AppColors.Primary
                    )
                }

                // Expandable Date Range Calendar Card (Wrap Content, Never Clipped)
                if (uiState.showDateRangeSelector) {
                    item(key = "date_range_calendar") {
                        DateRangeCalendarCard(
                            currentRangeString = uiState.dateRange,
                            onApply = { newRange ->
                                onAction(SalesAction.ApplyDateRange(newRange))
                            }
                        )
                    }
                }

                // Total Sales Summary Card (Clipped overlapping coin referencing Home view potential card style)
                item(key = "total_sales_card") {
                    val bannerShape = RoundedCornerShape(8.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                    ) {
                        // Layer 1: Background & Clipped Content (Text row and overlapping coin)
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clip(bannerShape)
                                .background(AppColors.WarmCreamBackground)
                        ) {
                            // Text row: amount and supporting label
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp, end = 68.dp)
                                    .align(Alignment.CenterStart),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = uiState.totalSalesAmount,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.WarmAmberDark,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Total Sales",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppColors.WarmAmberDark,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Layer 2: Gold Coin positioned to bleed off the right edge and clipped by the banner shape
                            Image(
                                painter = painterResource(id = R.drawable.img_gold_coin),
                                contentDescription = "Total Sales Coin",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .offset(x = 17.dp, y = 6.dp)
                                    .requiredSize(62.dp)
                                    .rotate(20.88f)
                            )
                        }

                        // Layer 3: Border outline drawn on top so the coin doesn't obscure the card outline
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .border(
                                    BorderStroke(1.dp, AppColors.WarmAmberBorder),
                                    bannerShape
                                )
                        )
                    }
                }

                // Results Count
                item(key = "results_count") {
                    Text(
                        text = "Showing ${uiState.resultsCount} results",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = AppColors.Slate500,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }

                // List of SalePolicyCards
                items(
                    items = uiState.policies,
                    key = { it.id }
                ) { policy ->
                    SalePolicyCard(
                        policy = policy,
                        onViewDetails = onViewDetails
                    )
                }
            }
        }
    }
}
