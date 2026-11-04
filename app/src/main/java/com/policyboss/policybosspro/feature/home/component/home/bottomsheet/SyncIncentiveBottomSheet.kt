package com.policyboss.policybosspro.feature.home.component.home.bottomsheet

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.home.model.home.HomeLead
import com.policyboss.policybosspro.ui.components.bottomSheet.policyProtectedBottomSheet.component.FloatingCloseButton
import com.policyboss.policybosspro.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncIncentiveBottomSheet(
    lead: HomeLead,
    onDismiss: () -> Unit,
    onUnlockRewardClick: (HomeLead) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = Color.Transparent,
        scrimColor = Color.Black.copy(alpha = 0.65f),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            // Main Bottom Sheet Body
            Column(
                modifier = Modifier
                    .padding(top = 26.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                AppColors.RoyalBlueGradientStart,
                                AppColors.RoyalBlueGradientMid,
                                AppColors.RoyalBlueGradientEnd
                            )
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // 1. Stylized Incentive Graphic (Money Bag with Rupee + Security Shield + Coins)
                SyncIncentiveGraphic(
                    modifier = Modifier.size(90.dp)
                )

                // 2. Title & Subtitle
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "You are eligible for sync incentive on this vehicle.",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp
                    )

                    Text(
                        text = "For every sync lead,\nyou earn extra brokerage!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }

                // 3. Stats Card with 3 Columns
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Quotes Found
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "${lead.quotesFoundCount}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Quotes Found",
                                fontSize = 11.sp,
                                color = AppColors.TextSecondary
                            )
                        }

                        // Divider 1
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(AppColors.CardBorderSubtle)
                        )

                        // Min Premium
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = lead.minPremium,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Min Premium",
                                fontSize = 11.sp,
                                color = AppColors.TextSecondary
                            )
                        }

                        // Divider 2
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(AppColors.CardBorderSubtle)
                        )

                        // Max Brokerage
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = lead.maxBrokerage,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Max Brokerage",
                                fontSize = 11.sp,
                                color = AppColors.TextSecondary
                            )
                        }
                    }
                }

                // 4. Action Button: "Unlock extra sync reward ->"
                Button(
                    onClick = { onUnlockRewardClick(lead) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.DarkNavyBlue
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Unlock extra sync reward",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Unlock",
                                tint = AppColors.DarkNavyBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Top Floating Circular Close Button
            FloatingCloseButton(
                onDismiss = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopCenter)
            )
        }
    }
}

/**
 * High-fidelity vector graphic with money bag, rupee symbol, and coin sparkles.
 */
@Composable
private fun SyncIncentiveGraphic(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(80.dp)) {
            // Soft background glow
            drawCircle(
                color = Color.White.copy(alpha = 0.2f),
                radius = size.minDimension / 2
            )

            // Outer gold coins
            drawCircle(
                color = AppColors.StarAmber,
                radius = 10.dp.toPx(),
                center = Offset(size.width * 0.22f, size.height * 0.35f)
            )
            drawCircle(
                color = AppColors.WarmAmberBorder,
                radius = 7.dp.toPx(),
                center = Offset(size.width * 0.8f, size.height * 0.3f)
            )
            drawCircle(
                color = AppColors.AmberBadgeText,
                radius = 8.dp.toPx(),
                center = Offset(size.width * 0.78f, size.height * 0.65f)
            )

            // Main central blue-tinted pouch/moneybag
            drawCircle(
                color = AppColors.AccentBlueLight,
                radius = 24.dp.toPx(),
                center = Offset(size.width * 0.5f, size.height * 0.55f)
            )
        }

        // Rupee Symbol in Center
        Text(
            text = "₹",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SyncIncentiveBottomSheetPreview() {
    SyncIncentiveBottomSheet(
        lead = AppDummyData.topLeads.first(),
        onDismiss = {},
        onUnlockRewardClick = {}
    )
}
