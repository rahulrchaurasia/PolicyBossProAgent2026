package com.policyboss.policybosspro.feature.carInsurance.ui.results

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.carInsurance.model.MotorQuote
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun QuoteSummaryCard(
    totalQuotes: Int,
    minPremium: Int,
    maxEarnings: Int,
    isZextraBundleEnabled: Boolean,
    onToggleZextraBundle: () -> Unit,
    modifier: Modifier = Modifier,
    onInfoClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(16.dp),
        color = AppColors.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Slate200)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // 3-Stat Row: Quotes, Min. Premiums, Max. Earnings
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Column 1: Quotes
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = totalQuotes.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Slate900
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Quotes",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Slate500
                    )
                }

                VerticalDivider(
                    modifier = Modifier.height(34.dp),
                    color = AppColors.Slate200,
                    thickness = 1.dp
                )

                // Column 2: Min. Premiums
                Column(
                    modifier = Modifier.weight(1.3f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "₹${MotorQuote.formatIndianCurrency(minPremium)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Slate900
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Min. Premiums",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Slate500
                    )
                }

                VerticalDivider(
                    modifier = Modifier.height(34.dp),
                    color = AppColors.Slate200,
                    thickness = 1.dp
                )

                // Column 3: Max. Earnings
                Column(
                    modifier = Modifier.weight(1.3f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .background(
                                color = Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(999.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.img_gold_coin),
                            contentDescription = "Earnings",
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = MotorQuote.formatIndianCurrency(maxEarnings),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Slate900
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Max. Earnings",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Slate500
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Zextra Bundle Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = AppColors.Slate50,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(1.dp, AppColors.Slate200, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Zextra Switch Pill
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFFDB833))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Zextra",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AppColors.Slate900
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(AppColors.PrimaryBlue)
                                        .padding(horizontal = 3.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "Switch",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = "Info",
                                tint = AppColors.Slate500,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable(enabled = onInfoClick != null) {
                                        onInfoClick?.invoke()
                                    }
                            )
                        }

                        // Switch control
                        Switch(
                            checked = isZextraBundleEnabled,
                            onCheckedChange = { onToggleZextraBundle() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AppColors.Slate800,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = AppColors.Slate300,
                                uncheckedBorderColor = Color.Transparent,
                                checkedBorderColor = Color.Transparent
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "This search qualifies for special Zextra Bundle - CPA/RSA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = AppColors.Slate600,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

