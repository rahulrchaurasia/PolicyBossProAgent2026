package com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteResult.component

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
    isAddOnsExpanded: Boolean,
    isCompared: Boolean,
    onToggleAddOnsExpanded: () -> Unit,
    onToggleAddOn: (String) -> Unit,
    onToggleCompare: () -> Unit,
    onBuyNowClick: () -> Unit,
    onShareClick: () -> Unit,
    onPolicyDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
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

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = quote.insurerName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Slate900
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Premium & Brokerage Section
            if (isZextraBundleEnabled) {
                // Zextra Bundle is ON: Shows discount badge
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Premium",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Slate500
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (quote.originalPremium != null) {
                            Text(
                                text = "₹${MotorQuote.formatIndianCurrency(quote.originalPremium)}",
                                style = TextStyle(textDecoration = TextDecoration.LineThrough),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = AppColors.Slate400
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = "₹${MotorQuote.formatIndianCurrency(quote.finalPremium)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Slate900
                        )
                    }

                    if (quote.zextraDiscountAmount != null) {
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
                }
            } else {
                // Zextra Bundle is OFF: Shows simple Premium on left and Brokerage on right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Premium",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.Slate500
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "₹${MotorQuote.formatIndianCurrency(quote.effectivePremium(false))}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Slate900
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Brokerage",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.Slate500
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color(0xFFFEF3C7))
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.img_gold_coin),
                                contentDescription = "Brokerage",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = MotorQuote.formatIndianCurrency(quote.effectiveEarnings(false)),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.Slate900
                            )
                        }
                    }
                }
            }

            // Total Earnings Breakdown (Shown when Zextra is ON)
            if (isZextraBundleEnabled) {
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

            // Expandable Add-ons Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, AppColors.Slate200, RoundedCornerShape(10.dp))
                    .clickable { onToggleAddOnsExpanded() }
                    .padding(10.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isAddOnsExpanded && quote.addOns.isNotEmpty()) {
                                "Add-ons (${quote.addOns.size})"
                            } else {
                                "Add-ons"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.Slate800
                        )

                        if (isAddOnsExpanded) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Edit Add-ons",
                                tint = AppColors.Slate700,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.ic_plus_fill),
                                contentDescription = "Expand Add-ons",
                                tint = AppColors.Slate700,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = isAddOnsExpanded && quote.addOns.isNotEmpty(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            quote.addOns.forEach { addOn ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onToggleAddOn(addOn.id) },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_check_circle),
                                            contentDescription = "Selected",
                                            tint = if (addOn.isSelected) AppColors.SuccessGreen else AppColors.Slate300,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = addOn.name,
                                            fontSize = 12.sp,
                                            color = AppColors.Slate700
                                        )
                                    }

                                    Text(
                                        text = "₹${MotorQuote.formatIndianCurrency(addOn.price)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppColors.Slate900
                                    )
                                }
                            }
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

            // Car Value (IDV) Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppColors.Slate50)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Car Value (IDV)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.Slate500
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "₹${quote.idvFormatted}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Slate900
                        )
                    }

                    if (isZextraBundleEnabled) {
                        VerticalDivider(
                            modifier = Modifier.height(26.dp),
                            color = AppColors.Slate200,
                            thickness = 1.dp
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Zextra Switch Cover",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.Slate500
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "₹${quote.zextraSwitchCoverPrice}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.Slate900
                            )
                        }
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

