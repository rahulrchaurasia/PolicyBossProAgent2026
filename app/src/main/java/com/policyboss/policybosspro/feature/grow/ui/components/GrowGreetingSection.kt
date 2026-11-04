package com.policyboss.policybosspro.feature.grow.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.grow.model.GrowGreetingItem
import com.policyboss.policybosspro.ui.components.text.TextWithChevron
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun GrowGreetingSection(
    greeting: GrowGreetingItem?,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (greeting == null) return

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Greetings",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Slate900
            )
            TextWithChevron(
                text = "View all",
                onClick = onViewAllClick,
                textStyle = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                ),
                textColor = AppColors.Slate600,
                iconTint = AppColors.Slate600,
                iconSize = 14.dp,
                spacing = 2.dp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(AppColors.Surface)
                .border(1.dp, AppColors.BorderSecondary, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = greeting.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Slate900
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = greeting.subtitle,
                fontSize = 12.sp,
                color = AppColors.Slate600,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Image(
                painter = painterResource(id = greeting.imageRes),
                contentDescription = greeting.title,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.42f)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action buttons row: Edit & Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Edit button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(AppColors.Surface)
                        .border(1.dp, AppColors.BorderSecondary, RoundedCornerShape(20.dp))
                        .clickable(onClick = onEditClick),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Edit",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.Slate800
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "✎",
                            fontSize = 14.sp,
                            color = AppColors.Slate700
                        )
                    }
                }

                // Share button with WhatsApp icon
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(AppColors.Surface)
                        .border(1.dp, AppColors.BorderSecondary, RoundedCornerShape(20.dp))
                        .clickable(onClick = onShareClick),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Share",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.Slate800
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_whatsapp),
                            contentDescription = "Share on WhatsApp",
                            tint = AppColors.WhatsAppGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
