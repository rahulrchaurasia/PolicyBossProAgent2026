package com.policyboss.policybosspro.feature.home.component.home.item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.home.model.home.HomeLead
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.labelSmallBold

@Composable
fun LeadCardItem(
    lead: HomeLead,
    onLeadClick: () -> Unit = {},
    onSyncRewardClick: () -> Unit = {},
    onShareClick: () -> Unit,
    onViewQuotesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onLeadClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        border = BorderStroke(1.dp, AppColors.BorderSecondary),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Name + Sync Badge (Left) and Top Lead / Expiry Badge (Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = lead.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (lead.leadType == "Renewal") AppColors.BadgeRenewalBackground else AppColors.SyncBadgeBg
                    ) {
                        Text(
                            text = lead.leadType,
                            color = if (lead.leadType == "Renewal") AppColors.BadgeRenewalText else AppColors.SyncBadgeText,
                            style = MaterialTheme.typography.labelSmallBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                        )
                    }
                }

                if (lead.expiryBadge != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AppColors.BadgeRedBackground
                    ) {
                        Text(
                            text = lead.expiryBadge,
                            color = AppColors.BadgeRedText,
                            style = MaterialTheme.typography.labelSmallBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                } else if (lead.isTopLead) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AppColors.TopLeadBadgeAmber
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.star_circle),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Top lead",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmallBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Vehicle and Expiry Information
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_car),
                        contentDescription = null,
                        tint = AppColors.TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${lead.vehicleType} • ${lead.vehicleModel}",
                        fontSize = 13.sp,
                        color = AppColors.TextSecondary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar),
                        contentDescription = null,
                        tint = AppColors.TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = lead.expiryText,
                        fontSize = 13.sp,
                        color = AppColors.TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Badges row: Special Deals & Sync Reward (Pill style matching Figma)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = AppColors.DealBadgeBackground,
                    border = BorderStroke(1.dp, AppColors.DealBadgeBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check_circle),
                            contentDescription = null,
                            tint = AppColors.DealBadgeText,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "${lead.specialDealsCount} Special Deals Available",
                            color = AppColors.DealBadgeText,
                            style = MaterialTheme.typography.labelSmallBold
                        )
                    }
                }

                if (lead.hasSyncReward) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = AppColors.Surface,
                        border = BorderStroke(1.dp, AppColors.PrimaryBlue.copy(alpha = 0.8f)),
                        modifier = Modifier.clickable { onSyncRewardClick() }
                    ) {
                        Text(
                            text = "+ Sync Reward",
                            color = AppColors.PrimaryBlue,
                            style = MaterialTheme.typography.labelSmallBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quotes count subhead
            Text(
                text = "${lead.quotesCount} quotes available",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontal Carousel of Quotation Cards
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = lead.quotes,
                    key = { it.id },
                    contentType = { "insurer_quote" }
                ) { quote ->
                    InsurerQuoteCardItem(quote = quote)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons Row: Share & View all quotes (36dp height, Figma typography & share icon)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onShareClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryBlue)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Share",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                OutlinedButton(
                    onClick = onViewQuotesClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AppColors.BorderPrimary)
                ) {
                    Text(
                        text = "View all quotes",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.TextPrimary
                    )
                }
            }
        }
    }
}