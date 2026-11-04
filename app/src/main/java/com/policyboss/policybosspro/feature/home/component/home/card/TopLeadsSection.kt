package com.policyboss.policybosspro.feature.home.component.home.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.home.model.home.HomeLead
import com.policyboss.policybosspro.feature.home.model.home.InsurerQuote
import androidx.compose.foundation.lazy.LazyRow
import com.policyboss.policybosspro.ui.components.text.TextWithChevron
import androidx.compose.foundation.lazy.items
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.labelSmallBold
import com.policyboss.policybosspro.feature.home.component.home.item.LeadCardItem

@Composable
fun TopLeadsHeader(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Title
        Text(
            text = "Top leads for you",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )

        // Segmented Control Tabs ("Sync Leads" vs "Renewals")
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = AppColors.LightGraySurface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tabs = listOf("Sync Leads", "Renewals")
                tabs.forEach { tabName ->
                    val isSelected = selectedTab == tabName
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onTabSelected(tabName) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) AppColors.Surface else Color.Transparent,
                        shadowElevation = if (isSelected) 1.dp else 0.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tabName,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) AppColors.PrimaryBlue else AppColors.TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ViewAllSyncLeadsLink(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "View all Sync Leads"
) {
    val displayTitle = if (title.endsWith(" >")) title.removeSuffix(" >") else title
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        TextWithChevron(
            text = displayTitle,
            onClick = onClick,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            ),
            textColor = AppColors.TextPrimary,
            iconTint = AppColors.TextPrimary,
            iconSize = 16.dp,
            spacing = 4.dp
        )
    }
}

@Composable
fun TopLeadsSection(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    leads: List<HomeLead>,
    onLeadClick: (HomeLead) -> Unit = {},
    onSyncRewardClick: (HomeLead) -> Unit = {},
    onShareLeadClick: (HomeLead) -> Unit,
    onViewAllQuotesClick: (HomeLead) -> Unit,
    onViewAllSyncLeadsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopLeadsHeader(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected
        )

        // List of Lead Cards
        if (leads.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                border = BorderStroke(1.dp, AppColors.BorderSecondary)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "No $selectedTab Available",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                    Text(
                        text = "Check back later for newly assigned leads.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextSecondary
                    )
                }
            }
        } else {
            leads.forEach { lead ->
                LeadCardItem(
                    lead = lead,
                    onLeadClick = { onLeadClick(lead) },
                    onSyncRewardClick = { onSyncRewardClick(lead) },
                    onShareClick = { onShareLeadClick(lead) },
                    onViewQuotesClick = { onViewAllQuotesClick(lead) }
                )
            }
        }

        ViewAllSyncLeadsLink(
            onClick = onViewAllSyncLeadsClick,
            title = if (selectedTab == "Renewals") "View all Renewals" else "View all Sync Leads"
        )
    }
}





@Preview(showBackground = true)
@Composable
private fun TopLeadsSectionPreview() {
    TopLeadsSection(
        selectedTab = "Sync Leads",
        onTabSelected = {},
        leads = AppDummyData.topLeads,
        onShareLeadClick = {},
        onViewAllQuotesClick = {},
        onViewAllSyncLeadsClick = {}
    )
}
