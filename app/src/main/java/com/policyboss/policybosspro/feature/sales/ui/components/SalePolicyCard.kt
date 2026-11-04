package com.policyboss.policybosspro.feature.sales.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.sales.model.SalePolicy
import com.policyboss.policybosspro.ui.components.text.TextWithChevron
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun SalePolicyCard(
    policy: SalePolicy,
    onViewDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AppColors.CardBorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Customer Name & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = policy.customerName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = AppColors.Slate900
                    )
                    policy.customerBadge?.let { badge ->
                        Box(
                            modifier = Modifier
                                .background(AppColors.Slate100, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badge,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.Slate500
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Vehicle Category & Model
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_car),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = AppColors.Slate500
                )
                Text(
                    text = "${policy.insuranceCategory} • ${policy.vehicleModel}",
                    fontSize = 12.sp,
                    color = AppColors.Slate500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Agent & Sub User Information Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.Slate50, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Agent",
                        fontSize = 13.sp,
                        color = AppColors.Slate500
                    )
                    Text(
                        text = policy.agentName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.Slate900
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sub User",
                        fontSize = 13.sp,
                        color = AppColors.Slate500
                    )
                    Text(
                        text = policy.subUserName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.Slate900
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Premium & Boss Coins
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Premium",
                        fontSize = 13.sp,
                        color = AppColors.Slate500
                    )
                    Text(
                        text = policy.premiumAmount,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Slate900
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Boss Coins",
                        fontSize = 13.sp,
                        color = AppColors.Slate500
                    )
                    Box(
                        modifier = Modifier
                            .background(AppColors.WarmAmberBadge, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_gold_coin),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = policy.bossCoins,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.WarmAmberText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(thickness = 0.5.dp, color = AppColors.Slate100)
            Spacer(modifier = Modifier.height(12.dp))

            // Total Earnings Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total earnings",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Slate900
                )
                Box(
                    modifier = Modifier
                        .background(AppColors.WarmAmberBadge, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_gold_coin),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = policy.totalEarnings,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.WarmAmberText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Breakdown
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Standard Brokerage",
                        fontSize = 12.sp,
                        color = AppColors.Slate500
                    )
                    Text(
                        text = policy.standardBrokerage,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Slate900
                    )
                }

                policy.specialDeal?.let { specialDeal ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = policy.specialDealBy ?: "Special Deal",
                            fontSize = 12.sp,
                            color = AppColors.Slate500
                        )
                        Text(
                            text = specialDeal,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.Slate900
                        )
                    }
                }

                policy.extraSyncEarnings?.let { extraSync ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Extra Sync Earnings",
                            fontSize = 12.sp,
                            color = AppColors.Slate500
                        )
                        Text(
                            text = extraSync,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.Slate900
                        )
                    }
                }
            }

            // Zaxtra Banner
            policy.zaxtraEarnings?.let { zaxtraEarnings ->
                Spacer(modifier = Modifier.height(10.dp))
                ZaxtraEarningsBanner(zaxtraEarnings = zaxtraEarnings)
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(thickness = 0.5.dp, color = AppColors.Slate100)
            Spacer(modifier = Modifier.height(10.dp))

            // View Details Action Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                TextWithChevron(
                    text = "View Details",
                    onClick = { onViewDetails(policy.id) },
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    textColor = AppColors.Primary,
                    iconTint = AppColors.Primary,
                    iconSize = 16.dp,
                    spacing = 4.dp
                )
            }
        }
    }
}

@Composable
fun ZaxtraEarningsBanner(
    zaxtraEarnings: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        AppColors.AccentBlue,
                        AppColors.AccentBlueLight
                    )
                )
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Yellow Zaxtra Badge
            Box(
                modifier = Modifier
                    .background(AppColors.TagYellow, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Zaxtra",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Slate950
                )
            }
            Text(
                text = "Extra Zaxtra Earnings",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
        Text(
            text = zaxtraEarnings,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
