package com.policyboss.policybosspro.feature.syncContact.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun AvatarClusterView(
    count: Int,
    statusText: String,
    modifier: Modifier = Modifier,
    statusTextColor: Color = Color.White
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(width = 116.dp, height = 80.dp),
            contentAlignment = Alignment.Center
        ) {
            // Soft warm circle aura behind avatars
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(AppColors.StarBadgeBackground.copy(alpha = 0.40f))
            )

            // Left avatar (slightly behind)
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .align(Alignment.CenterStart)
                    .clip(CircleShape)
                    .border(2.5.dp, Color.White.copy(alpha = 0.9f), CircleShape)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_avatar1),
                    contentDescription = null,
                    modifier = Modifier.size(54.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Right/Center avatar (overlapping in front)
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .align(Alignment.Center)
                    .offset(x = 16.dp)
                    .clip(CircleShape)
                    .border(2.5.dp, Color.White, CircleShape)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_avatar2),
                    contentDescription = null,
                    modifier = Modifier.size(58.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Badge pill: "+$count"
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 6.dp, y = 2.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppColors.AvatarCountBadgeBg)
                    .border(1.5.dp, Color.White, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 2.5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+$count",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.AvatarCountBadgeText
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = statusText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = statusTextColor
        )
    }
}

