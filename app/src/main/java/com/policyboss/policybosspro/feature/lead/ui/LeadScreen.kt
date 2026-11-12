package com.policyboss.policybosspro.feature.lead.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.home.component.home.item.LeadCardItem
import com.policyboss.policybosspro.feature.lead.model.LeadAction
import com.policyboss.policybosspro.feature.lead.model.LeadSortOption
import com.policyboss.policybosspro.feature.lead.model.LeadUiState
import com.policyboss.policybosspro.feature.lead.ui.components.ActiveFilterChipsRow
import com.policyboss.policybosspro.feature.lead.ui.components.LeadFilterDialog
import com.policyboss.policybosspro.feature.lead.ui.components.LeadSearchHeader
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

    BackHandler(enabled = uiState.isSearchActive) {
        onAction(LeadAction.ToggleSearchMode(false))
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

        // Top Toolbar: Inline Search Header OR Default Header with 4 Action Icons
        if (uiState.isSearchActive) {
            LeadSearchHeader(
                searchQuery = uiState.searchQuery,
                onQueryChange = { onAction(LeadAction.UpdateSearchQuery(it)) },
                onCloseSearch = { onAction(LeadAction.ToggleSearchMode(false)) }
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
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
                    // 1. Add Lead
                    IconButton(
                        onClick = { onAction(LeadAction.OnAddLeadClick) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Lead",
                            tint = AppColors.TextPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // 2. Search
                    IconButton(
                        onClick = { onAction(LeadAction.ToggleSearchMode(true)) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Leads",
                            tint = AppColors.TextPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // 3. Filter + Dynamic Active Filter Badge
                    Box {
                        IconButton(
                            onClick = { onAction(LeadAction.OnToggleFilterSheet) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FilterAlt,
                                contentDescription = "Filter Leads",
                                tint = if (uiState.filterCriteria.totalActiveFiltersCount > 0) {
                                    AppColors.PrimaryBlue
                                } else {
                                    AppColors.TextPrimary
                                },
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        if (uiState.filterCriteria.totalActiveFiltersCount > 0) {
                            Badge(
                                containerColor = AppColors.RedBadges,
                                contentColor = Color.White,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 2.dp, end = 2.dp)
                            ) {
                                Text(
                                    text = "${uiState.filterCriteria.totalActiveFiltersCount}",
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    // 4. Sort + Dropdown Menu
                    Box {
                        IconButton(
                            onClick = { onAction(LeadAction.ToggleSortDropdown(true)) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Sort,
                                contentDescription = "Sort Leads",
                                tint = AppColors.TextPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = uiState.showSortDropdown,
                            onDismissRequest = { onAction(LeadAction.ToggleSortDropdown(false)) },
                            modifier = Modifier.background(AppColors.White)
                        ) {
                            LeadSortOption.entries.forEach { option ->
                                val isSelected = uiState.filterCriteria.sortBy == option
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = option.displayName,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) AppColors.PrimaryBlue else AppColors.TextPrimary
                                        )
                                    },
                                    onClick = { onAction(LeadAction.SelectSortOption(option)) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Main Lead Tabs
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

        // Dynamic Dismissible Active Filter Chips Row
        ActiveFilterChipsRow(
            filterCriteria = uiState.filterCriteria,
            onAction = onAction
        )

        val visibleLeads = uiState.filteredLeads

        // Result Count
        Text(
            text = "Showing ${visibleLeads.size} results",
            fontSize = 12.sp,
            color = AppColors.TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Content Area: Loading / Empty State / Lead Cards
        when {
            uiState.isLoading && !uiState.hasCachedData -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppColors.PrimaryBlue)
                }
            }
            visibleLeads.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No leads found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (uiState.searchQuery.isNotBlank() || uiState.filterCriteria.totalActiveFiltersCount > 0) {
                                "Try adjusting your search or filter criteria."
                            } else {
                                "Leads will appear here once available."
                            },
                            fontSize = 13.sp,
                            color = AppColors.TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        if (uiState.searchQuery.isNotBlank() || uiState.filterCriteria.totalActiveFiltersCount > 0) {
                            Spacer(modifier = Modifier.height(12.dp))
                            TextButton(
                                onClick = {
                                    if (uiState.searchQuery.isNotBlank()) {
                                        onAction(LeadAction.ToggleSearchMode(false))
                                    }
                                    onAction(LeadAction.ResetFilters)
                                    onAction(LeadAction.ApplyFilters)
                                }
                            ) {
                                Text(
                                    text = "Clear all filters",
                                    color = AppColors.PrimaryBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = visibleLeads,
                        key = { it.id }
                    ) { lead ->
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
    }

    if (uiState.showFilterSheet) {
        LeadFilterDialog(
            activeCategory = uiState.activeFilterCategory,
            criteria = uiState.tempFilterCriteria,
            onAction = onAction,
            onDismiss = { onAction(LeadAction.OnDismissFilterSheet) }
        )
    }
}
