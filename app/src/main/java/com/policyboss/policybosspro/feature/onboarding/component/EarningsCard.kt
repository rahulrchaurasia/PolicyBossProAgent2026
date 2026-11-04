package com.policyboss.customer.feature.onboarding.component

import androidx.compose.runtime.Composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Text

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun EarningsCard() {
    Column(
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.IncentiveCardBg)
            .border(1.dp, AppColors.BorderLight.copy(0.5f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        // Yellow Header with Right Arrow
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .background(AppColors.IncentivePillBg)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Earn back ₹2500",
                color = AppColors.IncentiveGoldText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            // The white circle arrow shown in the image
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = R.drawable.arrow_right_color,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Skeleton UI
        Row {
            Box(Modifier.width(40.dp).height(8.dp).background(AppColors.IncentiveSkeleton, CircleShape))
            Spacer(Modifier.width(8.dp))
            Box(Modifier.width(60.dp).height(8.dp).background(AppColors.IncentiveSkeleton, CircleShape))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(24.dp)
                .background(AppColors.IncentiveSkeleton, RoundedCornerShape(6.dp))
        )
    }
}
@Preview(
    name = "Earnings Card Preview",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun PreviewEarningsCard() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            EarningsCard()
        }
    }
}