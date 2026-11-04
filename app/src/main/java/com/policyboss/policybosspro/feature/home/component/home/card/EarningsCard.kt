package com.policyboss.policybosspro.feature.home.component.home.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.home.model.home.EarningsSummary
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.heroEarnings
import com.policyboss.policybosspro.ui.theme.heroEarningsLabel
import com.policyboss.policybosspro.ui.theme.potentialEarningsAmount
import com.policyboss.policybosspro.ui.theme.potentialEarningsLabel

@Composable
fun EarningsCard(
    earningsSummary: EarningsSummary,
    modifier: Modifier = Modifier
) {
    val bannerShape = RoundedCornerShape(8.dp)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Hero Earnings Amount and Label
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = earningsSummary.thisMonthEarnings,
                style = MaterialTheme.typography.heroEarnings,
                color = AppColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "This month’s earnings",
                style = MaterialTheme.typography.heroEarningsLabel,
                color = AppColors.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // 2. Potential Earnings Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
        ) {
            // Layer 1: Background & Clipped Content (Text row and overlapping coin)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(bannerShape)
                    .background(AppColors.PotentialCardBackground)
            ) {
                // Text row: amount and supporting label
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 68.dp)
                        .align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = earningsSummary.potentialEarnings,
                        style = MaterialTheme.typography.potentialEarningsAmount,
                        color = AppColors.TextSecondaryDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "potential this month",
                        style = MaterialTheme.typography.potentialEarningsLabel,
                        color = AppColors.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Layer 2: Gold Coin positioned to bleed off the right edge and clipped by the banner shape
                Image(
                    painter = painterResource(id = R.drawable.img_gold_coin),
                    contentDescription = "Gold coin representing potential earnings",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 17.dp, y = 8.dp)
                        .requiredSize(62.dp)
                        .rotate(20.88f)
                )
            }

            // Layer 3: Border outline drawn on top so the coin doesn't obscure the card outline
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .border(
                        BorderStroke(1.dp, AppColors.SegmentedBorder),
                        bannerShape
                    )
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 400
)
@Composable
private fun EarningsCardPreview() {
    EarningsCard(
        earningsSummary = EarningsSummary(
            thisMonthEarnings = "₹20,000",
            potentialEarnings = "₹1,20,000"
        ),
        modifier = Modifier.padding(16.dp)
    )
}