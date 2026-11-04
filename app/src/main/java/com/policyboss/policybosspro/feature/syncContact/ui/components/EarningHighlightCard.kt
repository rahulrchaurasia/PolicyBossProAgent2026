package com.policyboss.policybosspro.feature.syncContact.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun EarningHighlightCard(
    title: String,
    amount: String,
    suffix: String = "/year",
    modifier: Modifier = Modifier,
    backgroundColor: Color = AppColors.HighlightCardGold
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(horizontal = 24.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 11.5.sp,
            color = AppColors.HighlightCardTitle,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = amount,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.HighlightCardAmount
            )

            Text(
                text = suffix,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = AppColors.HighlightCardSuffix,
                modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
            )
        }
    }
}

