package com.policyboss.policybosspro.feature.dashboard.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.dashboard.model.DashboardAction
import com.policyboss.policybosspro.feature.dashboard.model.DashboardUiState
import com.policyboss.policybosspro.feature.dashboard.ui.components.BusinessSnapshotWebViewChart
import com.policyboss.policybosspro.feature.dashboard.ui.components.DateRangeCalendarCard
import com.policyboss.policybosspro.feature.dashboard.ui.components.EarningsWebViewChart
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onAction: (DashboardAction) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .background(AppColors.Background)
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Dashboard", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = AppColors.TextPrimary)
            Text("Track your progress and keep your data updated", color = AppColors.TextSecondary, fontSize = 14.sp)

            // Performance / Sync Tabs
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AppColors.LightGraySurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                    val tabs = listOf("Performance", "Sync")
                    tabs.forEach { tab ->
                        val isSelected = uiState.selectedMainTab == tab
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) AppColors.Surface else Color.Transparent,
                            shadowElevation = if (isSelected) 1.dp else 0.dp,
                            onClick = { onAction(DashboardAction.SelectMainTab(tab)) }
                        ) {
                            Text(
                                text = tab,
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = TextAlign.Center,
                                color = if (isSelected) AppColors.PrimaryBlue else AppColors.TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date Range Bar
            Surface(
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, AppColors.BorderSecondary),
                color = AppColors.Surface,
                onClick = { onAction(DashboardAction.ToggleDateRangeSelector) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = AppColors.TextSecondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(uiState.selectedDateRange, fontSize = 14.sp)
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chevron_down),
                        contentDescription = null,
                        tint = AppColors.TextSecondary,
                        modifier = Modifier
                            .size(18.dp)
                            .rotate(if (uiState.showDateRangeSelector) 180f else 0f)
                    )
                }
            }

            // Expandable Date Range Calendar Card (Wrap Content, Never Clipped)
            if (uiState.showDateRangeSelector) {
                DateRangeCalendarCard(
                    currentRangeString = uiState.selectedDateRange,
                    onApply = { newRange ->
                        onAction(DashboardAction.ApplyDateRange(newRange))
                    }
                )
            }

            // PolicyBoss Earnings Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("PolicyBoss Earnings", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = AppColors.EarningsYellowBg,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_moneybag),
                                contentDescription = null,
                                tint = AppColors.EarningsOrange,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(uiState.earnings.amount, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = AppColors.EarningsOrange)
                            Text("Total earnings", fontSize = 12.sp, color = AppColors.TextSecondary)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            color = AppColors.LightGraySurface
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Premium", fontSize = 12.sp, color = AppColors.TextSecondary)
                                Text(uiState.earnings.premium, fontWeight = FontWeight.Bold)
                            }
                        }
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            color = AppColors.EarningsYellowBg
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Potential this month", fontSize = 12.sp, color = AppColors.EarningsOrange)
                                Text(uiState.earnings.potential, fontWeight = FontWeight.Bold, color = AppColors.EarningsOrange)
                            }
                        }
                    }
                }
            }

            // Total Policies Sold Card (Wrap content, displays all items without clipping)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val totalCount = uiState.totalPoliciesSold.sumOf { it.count }
                    Text(totalCount.toString(), fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    Text("Total Policies Sold", fontSize = 12.sp, color = AppColors.TextSecondary)

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.totalPoliciesSold.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowItems.forEach { item ->
                                    Surface(
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, AppColors.BorderSecondary),
                                        color = AppColors.Surface
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .padding(12.dp)
                                                .fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(item.categoryName, fontSize = 12.sp, color = AppColors.TextSecondary)
                                                Text(item.count.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            }
                                            if (item.iconResId != null) {
                                                Icon(
                                                    painter = painterResource(id = item.iconResId),
                                                    contentDescription = null,
                                                    tint = AppColors.PrimaryBlue,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                if (rowItems.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // Your Leads Card (By Stage / By Product)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header: "Your Leads" + Segmented Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Your Leads", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("By Stage", "By Product").forEach { tab ->
                                val isSelected = uiState.selectedLeadsTab == tab
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) AppColors.PrimaryBlue else AppColors.BorderSecondary
                                    ),
                                    color = if (isSelected) AppColors.BlueTintBackground else Color.Transparent,
                                    onClick = { onAction(DashboardAction.SelectLeadsTab(tab)) }
                                ) {
                                    Text(
                                        text = tab,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) AppColors.PrimaryBlue else AppColors.TextPrimary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (uiState.selectedLeadsTab == "By Stage") {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, AppColors.BorderSecondary),
                            color = AppColors.Surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val totalLeads = uiState.leadsByStage.sumOf { it.count }
                                Text(totalLeads.toString(), fontWeight = FontWeight.Bold, fontSize = 24.sp)
                                Text("Total Leads", fontSize = 12.sp, color = AppColors.TextSecondary)

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = AppColors.BorderSecondary, thickness = 1.dp)
                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    uiState.leadsByStage.forEachIndexed { index, stageItem ->
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(stageItem.count.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                            Text(stageItem.stage, fontSize = 12.sp, color = AppColors.TextSecondary)
                                        }
                                        if (index < uiState.leadsByStage.size - 1) {
                                            VerticalDivider(
                                                modifier = Modifier.height(36.dp),
                                                color = AppColors.BorderSecondary,
                                                thickness = 1.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // By Product
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, AppColors.BorderSecondary),
                            color = AppColors.Surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("40", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                                Text("Total Leads", fontSize = 12.sp, color = AppColors.TextSecondary)

                                Spacer(modifier = Modifier.height(16.dp))

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    uiState.leadsByProduct.chunked(2).forEach { rowItems ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            rowItems.forEach { item ->
                                                Surface(
                                                    modifier = Modifier.weight(1f),
                                                    shape = RoundedCornerShape(8.dp),
                                                    border = BorderStroke(1.dp, AppColors.BorderSecondary),
                                                    color = AppColors.Surface
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .padding(12.dp)
                                                            .fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Column {
                                                            Text(item.categoryName, fontSize = 12.sp, color = AppColors.TextSecondary)
                                                            Text(item.count.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                                        }
                                                        if (item.iconResId != null) {
                                                            Icon(
                                                                painter = painterResource(id = item.iconResId),
                                                                contentDescription = null,
                                                                tint = AppColors.PrimaryBlue,
                                                                modifier = Modifier.size(24.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            if (rowItems.size == 1) {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Missed Opportunities Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.LostCardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AppColors.AlertRedDark,
                            modifier = Modifier.size(22.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Missed Opportunities",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = AppColors.TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    uiState.missedOpportunities.brokerageLost,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = AppColors.LostCardCrimson
                                )
                                Text("Brokerage Lost", fontSize = 12.sp, color = AppColors.LostCardCrimson)
                            }
                        }
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    uiState.missedOpportunities.policiesLost.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = AppColors.LostCardCrimson
                                )
                                Text("Policies Lost", fontSize = 12.sp, color = AppColors.LostCardCrimson)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.missedOpportunities.breakdown.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowItems.forEach { item ->
                                    Surface(
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.White
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .padding(12.dp)
                                                .fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(item.categoryName, fontSize = 12.sp, color = AppColors.TextSecondary)
                                                Text(item.count.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            }
                                            if (item.iconResId != null) {
                                                Icon(
                                                    painter = painterResource(id = item.iconResId),
                                                    contentDescription = null,
                                                    tint = AppColors.PrimaryBlue,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                if (rowItems.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAction(DashboardAction.ViewMissedOpportunities) },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "View Boss Alerts",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = AppColors.TextPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chevron_right),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = AppColors.TextPrimary
                        )
                    }
                }
            }

            // Business Snapshot Card (with 3D Bar Chart via WebView)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Business snapshot", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Category Pills: Car, TW, CV, Travel, Health, Life
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        uiState.businessCategories.forEach { category ->
                            val isSelected = uiState.selectedBusinessCategory == category
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) AppColors.PrimaryBlue else AppColors.BorderSecondary
                                ),
                                color = if (isSelected) AppColors.BlueTintBackground else Color.Transparent,
                                onClick = { onAction(DashboardAction.SelectBusinessCategory(category)) }
                            ) {
                                Text(
                                    text = category,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) AppColors.PrimaryBlue else AppColors.TextPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "(Count: 106 #Premium: 508K)",
                        fontSize = 12.sp,
                        color = AppColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Metric Pills: Number of Policies, Premium, Make, RTO
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Number of Policies", "Premium", "Make", "RTO").forEach { metric ->
                            val isSelected = (metric == "Number of Policies" && uiState.selectedBusinessMetric == "Count") ||
                                    (metric == "Premium" && uiState.selectedBusinessMetric == "Premium") ||
                                    (metric == "Number of Policies" && uiState.selectedBusinessMetric != "Premium")
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) AppColors.PrimaryBlue else AppColors.BorderSecondary
                                ),
                                color = if (isSelected) AppColors.BlueTintBackground else Color.Transparent,
                                onClick = {
                                    val mappedMetric = if (metric == "Premium") "Premium" else "Count"
                                    onAction(DashboardAction.SelectBusinessMetric(mappedMetric))
                                }
                            ) {
                                Text(
                                    text = metric,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) AppColors.PrimaryBlue else AppColors.TextPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3D Horizontal Bar Chart rendered inside WebView
                    BusinessSnapshotWebViewChart()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bottom Conversion % container
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AppColors.Slate100,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Conversion %", fontSize = 14.sp, color = AppColors.TextPrimary)
                            Text(
                                "${uiState.businessSnapshot.conversionPercentage}%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = AppColors.TextPrimary
                            )
                        }
                    }
                }
            }

            // Earnings on Policy Boss Chart
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        text = "Earnings on Policy Boss",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    )
                    EarningsWebViewChart(
                        points = uiState.earningsChart
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp)) // padding for bottom nav
        }
    }
}
