package com.policyboss.policybosspro.feature.lead.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.home.component.home.item.LeadCardItem
import com.policyboss.policybosspro.feature.lead.model.LeadAction
import com.policyboss.policybosspro.feature.lead.model.LeadUiState
import com.policyboss.policybosspro.ui.components.offline.NoInternetContent
import com.policyboss.policybosspro.ui.components.offline.OfflineBanner
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun LeadScreen(
    uiState: LeadUiState,
    onAction: (LeadAction) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    if (uiState.showNoInternetScreen) {
        NoInternetContent(
            onRetry = { onAction(LeadAction.OnRetryClick) },
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding)
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .background(AppColors.Background)
    ) {
        // Non-blocking offline indicator when cached leads are available without internet
        if (!uiState.isOnline && uiState.hasCachedData) {
            OfflineBanner(
                message = "You're offline. Displaying saved leads.",
                onRetry = { onAction(LeadAction.OnRetryClick) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Leads",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = AppColors.TextPrimary
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Add, "Add", tint = AppColors.TextPrimary, modifier = Modifier.size(22.dp))
                }
                IconButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Search, "Search", tint = AppColors.TextPrimary, modifier = Modifier.size(22.dp))
                }
                IconButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Outlined.FilterAlt, "Filter", tint = AppColors.TextPrimary, modifier = Modifier.size(22.dp))
                }
                IconButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.AutoMirrored.Outlined.Sort, "Sort", tint = AppColors.TextPrimary, modifier = Modifier.size(22.dp))
                }
            }
        }

        // Tabs
        val leadTabs = listOf("All Leads", "Renewals", "Synced contacts", "Added by me")
        val selectedTabIndex = leadTabs.indexOf(uiState.selectedTab).coerceAtLeast(0)

        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 16.dp,
            containerColor = AppColors.Background,
            indicator = { tabPositions ->
                if (selectedTabIndex in tabPositions.indices) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        height = 2.dp,
                        color = AppColors.PrimaryBlue
                    )
                }
            },
            divider = {}
        ) {
            leadTabs.forEach { tab ->
                val isSelected = uiState.selectedTab == tab
                Tab(
                    selected = isSelected,
                    onClick = { onAction(LeadAction.SelectLeadTab(tab)) },
                    text = {
                        Text(
                            text = tab,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) AppColors.PrimaryBlue else AppColors.TextSecondary
                        )
                    },
                    modifier = if (isSelected) {
                        Modifier.background(AppColors.BlueTintBackground)
                    } else {
                        Modifier
                    }
                )
            }
        }
            
        // Sub-tabs / Filters (Status)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (uiState.selectedTab == "Renewals") {
                val expiryFilters = listOf("7 days", "7-14 days", "14+ days")
                items(expiryFilters) { filter ->
                    val isSelected = uiState.selectedExpiryFilter == filter
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) AppColors.BlueTintBackground else AppColors.Surface,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) AppColors.PrimaryBlue else AppColors.BorderLight
                        ),
                        modifier = Modifier.clickable {
                            onAction(LeadAction.SelectExpiryFilter(filter))
                        }
                    ) {
                        Text(
                            text = filter,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) AppColors.PrimaryBlue else AppColors.Slate800,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
                val vehicleFilters = listOf("Car", "Two Wheeler", "Commercial Vehicle")
                items(vehicleFilters) { filter ->
                    val isSelected = uiState.selectedVehicleFilters.contains(filter)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) AppColors.BlueTintBackground else AppColors.Surface,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) AppColors.PrimaryBlue else AppColors.BorderLight
                        ),
                        modifier = Modifier.clickable {
                            onAction(LeadAction.ToggleVehicleFilter(filter))
                        }
                    ) {
                        Text(
                            text = filter,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) AppColors.PrimaryBlue else AppColors.Slate800,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            } else {
                val statusChips = listOf("New", "In Progress", "Won", "Lost")
                items(statusChips) { status ->
                    val isSelected = uiState.selectedStatusFilter == status
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) AppColors.BlueTintBackground else AppColors.Surface,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) AppColors.PrimaryBlue else AppColors.BorderLight
                        ),
                        modifier = Modifier.clickable {
                            onAction(LeadAction.SelectStatusFilter(status))
                        }
                    ) {
                        Text(
                            text = status,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) AppColors.PrimaryBlue else AppColors.Slate800,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }
            
            // Count
            Text(
                text = "Showing ${uiState.filteredLeads.size} results",
                fontSize = 12.sp,
                color = AppColors.TextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

        // List
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(uiState.filteredLeads) { lead ->
                LeadCardItem(
                    lead = lead,
                    onLeadClick = { onAction(LeadAction.OpenLeadDetails(lead)) },
                    onShareClick = { onAction(LeadAction.ShareLead(lead)) },
                    onViewQuotesClick = { onAction(LeadAction.ContinueToQuotes(lead)) }
                )
            }
        }
    }
}
