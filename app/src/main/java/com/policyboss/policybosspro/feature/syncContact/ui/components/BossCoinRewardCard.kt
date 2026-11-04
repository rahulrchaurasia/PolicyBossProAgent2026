package com.policyboss.policybosspro.feature.syncContact.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun BossCoinRewardCard(
    bossCoins: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        AppColors.BossCoinGradientStart.copy(alpha = 0.95f),
                        AppColors.BossCoinGradientEnd.copy(alpha = 0.90f)
                    )
                )
            )
            .border(
                1.dp,
                Color.White.copy(alpha = 0.8f),
                RoundedCornerShape(16.dp)
            )
            .padding(vertical = 14.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Gold Coin Icon
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(AppColors.BossCoinGold),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.img_gold_coin),
                    contentDescription = "Boss Coin",
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Congratulations! You've earned",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.BossCoinTextMedium
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "$bossCoins Boss Coins.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.BossCoinTextBold
            )
        }
    }
}

