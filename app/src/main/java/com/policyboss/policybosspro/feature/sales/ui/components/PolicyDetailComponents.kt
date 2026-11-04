package com.policyboss.policybosspro.feature.sales.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.sales.model.InsuredDetails
import com.policyboss.policybosspro.feature.sales.model.SalePolicy
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun CustomerSummaryCard(
    policy: SalePolicy,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AppColors.CardBorderSubtle),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = policy.customerName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
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

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_car),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = AppColors.Slate500
                )
                Text(
                    text = "Car • ${policy.detailsVehicleModel}",
                    fontSize = 13.sp,
                    color = AppColors.Slate500
                )
            }
        }
    }
}

@Composable
fun AgentInformationCard(
    policy: SalePolicy,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AppColors.CardBorderSubtle),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
    }
}

@Composable
fun PremiumRewardsCard(
    policy: SalePolicy,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AppColors.CardBorderSubtle),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                    fontSize = 15.sp,
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
    }
}

@Composable
fun TotalEarningsCard(
    policy: SalePolicy,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AppColors.CardBorderSubtle),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
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

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
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
                            text = policy.specialDealBy ?: "Special Deal By HDFC",
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

            policy.zaxtraEarnings?.let { zaxtraEarnings ->
                Spacer(modifier = Modifier.height(10.dp))
                ZaxtraEarningsBanner(zaxtraEarnings = zaxtraEarnings)
            }
        }
    }
}

@Composable
fun CustomerDetailsCard(
    customerDetailsId: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AppColors.CardBorderSubtle),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Customer Details",
                    fontSize = 12.sp,
                    color = AppColors.Slate500
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = customerDetailsId,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Slate900
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Download icon button (visual control)
                IconButton(
                    onClick = { /* Visual control */ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_right_color),
                        contentDescription = "Download",
                        modifier = Modifier.size(18.dp),
                        tint = AppColors.Primary
                    )
                }

                // Info icon button (visual control)
                IconButton(
                    onClick = { /* Visual control */ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Info",
                        modifier = Modifier.size(18.dp),
                        tint = AppColors.Primary
                    )
                }
            }
        }
    }
}

@Composable
fun InsuredDetailsCard(
    insuredDetails: InsuredDetails,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AppColors.CardBorderSubtle),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Insured Details",
                fontSize = 12.sp,
                color = AppColors.Slate500
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = insuredDetails.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Slate900,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Labeled rows
            DetailItem(label = "Vahan Data", value = insuredDetails.vahanData)
            DetailItem(label = "Quote On", value = insuredDetails.quoteOn)
            DetailItem(label = "Sale On", value = insuredDetails.saleOn)
            DetailItem(label = "CRN", value = insuredDetails.crn)
            DetailItem(
                label = "CS",
                value = insuredDetails.cs,
                valueColor = AppColors.EmeraldSuccess // Emerald Green
            )
            DetailItem(label = "Zaxtra", value = insuredDetails.zaxtra)
            DetailItem(
                label = "Premium Level",
                value = insuredDetails.premiumLevel,
                hasChevron = true
            )
            DetailItem(
                label = "Brokerage Level",
                value = insuredDetails.brokerageLevel,
                hasChevron = true
            )
            DetailItem(label = "Sale Duration", value = insuredDetails.saleDuration)
            DetailItem(label = "Sync", value = insuredDetails.sync)
            DetailItem(label = "Renew", value = insuredDetails.renew)
            DetailItem(label = "Source", value = insuredDetails.source)
            DetailItem(label = "Insurer", value = insuredDetails.insurer)
            DetailItem(label = "Customer", value = insuredDetails.customer)
            DetailItem(label = "Reporting", value = insuredDetails.reporting)
            DetailItem(label = "Ss ID", value = insuredDetails.ssId, isLast = true)
        }
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String,
    valueColor: Color = AppColors.Slate900,
    hasChevron: Boolean = false,
    isLast: Boolean = false
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = AppColors.Slate400
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = valueColor,
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f, fill = false)
            )
            if (hasChevron) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        if (!isLast) {
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

