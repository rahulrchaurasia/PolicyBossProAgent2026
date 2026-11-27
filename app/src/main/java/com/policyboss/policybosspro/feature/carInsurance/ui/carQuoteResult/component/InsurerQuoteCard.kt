package com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteResult.component

import coil3.compose.AsyncImage
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.carInsurance.model.MotorQuote
import com.policyboss.policybosspro.ui.components.button.PrimaryCTAButton
import com.policyboss.policybosspro.ui.theme.AppColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InsurerQuoteCard(
    quote: MotorQuote,
    isZextraBundleEnabled: Boolean,
    isAddOnsExpanded: Boolean = false,
    isCompared: Boolean = false,
    onToggleAddOnsExpanded: () -> Unit = {},
    onToggleAddOn: (String) -> Unit = {},
    onToggleCompare: () -> Unit,
    onBuyNowClick: () -> Unit,
    onShareClick: () -> Unit,
    onPolicyDetailsClick: () -> Unit,
    modifier: Modifier = Modifier,
    /** TP-only: no add-ons. */
    showAddOns: Boolean = false,
    /** TP-only: no car value (IDV). */
    showIdv: Boolean = true
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.06f)
            ),
        shape = RoundedCornerShape(16.dp),
        color = AppColors.White,
        border = BorderStroke(1.dp, AppColors.Slate200)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Insurer Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Insurer Logo
                Box(
                    modifier = Modifier
                        .size(width = 46.dp, height = 28.dp)
                        .border(1.dp, AppColors.Slate200, RoundedCornerShape(6.dp))
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (!quote.insurerLogoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = quote.insurerLogoUrl,
                            contentDescription = quote.insurerName,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(width = 38.dp, height = 22.dp)
                        )
                    } else {
                        val logoRes = when (quote.insurerLogoType.lowercase()) {
                            "acko" -> R.drawable.ic_acko_logo
                            "tata" -> R.drawable.img_tata
                            else -> R.drawable.ic_acko_logo
                        }
                        Image(
                            painter = painterResource(logoRes),
                            contentDescription = quote.insurerName,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(width = 38.dp, height = 22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = quote.insurerName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Slate900
                    )
                    if (!quote.planName.isNullOrBlank()) {
                        Text(
                            text = "Plan: ${quote.planName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.Slate500
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Premium Row (SpaceBetween: "Premium" on Left, Amount on Right)
            val displayPremium = quote.effectivePremium(isZextraBundleEnabled)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Premium",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                )

                Text(
                    text = "₹${MotorQuote.formatIndianCurrency(displayPremium)}",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                )
            }

            if (quote.zextraDiscountAmount != null && isZextraBundleEnabled) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFEEF4FF))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Reduced by ₹${quote.zextraDiscountAmount} with Zextra ↓",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF3538CD)
                    )
                }
            }

            // Total Earnings Breakdown (Shown when Zextra is ON and the quote carries earnings)
            if (isZextraBundleEnabled && quote.totalEarnings > 0) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppColors.Slate50)
                        .border(1.dp, AppColors.Slate200, RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total earnings",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.Slate900
                            )
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(Color(0xFFFEF3C7))
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.img_gold_coin),
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = MotorQuote.formatIndianCurrency(quote.totalEarnings),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.Slate900
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Line items
                        EarningsBreakdownRow(
                            label = "Standard Brokerage",
                            amount = quote.earningsBreakdown.standardBrokerage
                        )
                        EarningsBreakdownRow(
                            label = quote.earningsBreakdown.specialDealTitle,
                            amount = quote.earningsBreakdown.specialDealAmount
                        )
                        EarningsBreakdownRow(
                            label = "Extra Sync Earnings",
                            amount = quote.earningsBreakdown.extraSyncEarnings
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Blue Extra Zextra Banner
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(AppColors.PrimaryBlue)
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFFDB833))
                                    .padding(horizontal = 5.dp, vertical = 1.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Zextra",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AppColors.Slate900
                                )
                            }

                            Text(
                                text = "Extra Zextra Earnings: ₹${quote.earningsBreakdown.extraZextraEarnings} ↑",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Insurer Benefits Badges
            if (quote.benefits.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quote.benefits.forEach { benefit ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(AppColors.GreenBadgeBg)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = benefit,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.GreenBadgeText
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Car Value (IDV) & Zextra Switch Cover Block (Stacked Row-Wise)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Tier 1 — Highlighted Car Value (IDV) Box (Full Width with SpaceBetween Alignment); hidden when there is no IDV (TP-only)
                if (showIdv) Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFEFF6FF))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Row 1: Primary IDV Line (Label Left, Amount Right)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Car Value (IDV) :",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.TextSecondary
                            )
                            Text(
                                text = "₹${MotorQuote.formatIndianCurrency(quote.idvValue)}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Row 2: IDV Min-Max Range Line (Label Left, Range Right)
                        val minIdvStr = MotorQuote.formatIndianCurrency(quote.minIdv)
                        val maxIdvStr = MotorQuote.formatIndianCurrency(quote.maxIdv)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "IDV Range :",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.TextSecondary
                            )
                            Text(
                                text = "₹$minIdvStr to ₹$maxIdvStr",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.TextSecondary
                            )
                        }
                    }
                }

                // Tier 2 — Zextra Switch Cover Row (Rich Gradient Strip matching CRN badge)
                AnimatedVisibility(
                    visible = isZextraBundleEnabled && quote.zextraSwitchCoverPrice > 0,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF1D4ED8), // Deep Royal Blue (matches CRN Badge start)
                                        Color(0xFF3B82F6), // Vibrant Electric Blue
                                        Color(0xFF7C3AED)  // Rich Violet/Purple (matches Image 1 end)
                                    )
                                )
                            )
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            ZextraSwitchLogo(height = 20.dp)
                            Text(
                                text = "Zextra Switch Cover :",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "₹${MotorQuote.formatIndianCurrency(quote.zextraSwitchCoverPrice)}",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row 1: Share & Policy Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onShareClick,
                    shape = RoundedCornerShape(999.dp),
                    border = BorderStroke(1.dp, AppColors.Slate300),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Share",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.Slate700
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share",
                            tint = AppColors.Slate700,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                OutlinedButton(
                    onClick = onPolicyDetailsClick,
                    shape = RoundedCornerShape(999.dp),
                    border = BorderStroke(1.dp, AppColors.Slate300),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(40.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text(
                        text = "Policy Details >",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.Slate700
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Buy Now Primary CTA
            PrimaryCTAButton(
                text = "Buy Now",
                onClick = onBuyNowClick,
                containerColor = AppColors.PrimaryBlue,
                contentColor = Color.White,
                arrowBackgroundColor = Color.White,
                arrowTint = AppColors.PrimaryBlue,
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Compare Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleCompare() }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isCompared,
                    onCheckedChange = { onToggleCompare() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = AppColors.PrimaryBlue,
                        uncheckedColor = AppColors.Slate400,
                        checkmarkColor = Color.White
                    ),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Compare",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.Slate700
                )
            }
        }
    }
}

@Composable
private fun EarningsBreakdownRow(
    label: String,
    amount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = AppColors.Slate600
        )
        Text(
            text = "₹${MotorQuote.formatIndianCurrency(amount)}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.Slate800
        )
    }
}

@Preview(name = "Insurer Quote Card - Recommended", showBackground = true)
@Composable
private fun InsurerQuoteCardPreview() {
    val sampleQuote = com.policyboss.policybosspro.feature.carInsurance.model.MotorQuoteDefaults.generateDefaultQuotes().first()

    InsurerQuoteCard(
        quote = sampleQuote,
        isZextraBundleEnabled = true,
        isAddOnsExpanded = true,
        isCompared = false,
        onToggleAddOnsExpanded = {},
        onToggleAddOn = {},
        onToggleCompare = {},
        onBuyNowClick = {},
        onShareClick = {},
        onPolicyDetailsClick = {},
        modifier = Modifier.padding(16.dp)
    )
}

