package com.policyboss.policybosspro.feature.syncContact.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun PhoneNetworkIllustration(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(260.dp),
        contentAlignment = Alignment.Center
    ) {
        // Concentric circular rings
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val rings = listOf(52.dp.toPx(), 86.dp.toPx(), 118.dp.toPx())

            rings.forEachIndexed { index, radius ->
                drawCircle(
                    color = AppColors.NetworkRing.copy(alpha = if (index == 2) 0.6f else 0.9f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 1.2.dp.toPx())
                )
            }
        }

        // Center Phone Mockup
        Box(
            modifier = Modifier
                .size(width = 54.dp, height = 82.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(AppColors.MockupDeviceBody)
                .border(2.dp, AppColors.MockupDeviceBorder, RoundedCornerShape(12.dp))
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(9.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                // Speaker bar at top
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 4.dp)
                        .size(width = 16.dp, height = 2.5.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(AppColors.Slate300)
                )

                // User avatar on phone screen
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(AppColors.lightBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = AppColors.Primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Surrounding 4 Floating Contact Avatars
        // 1. Top-Left
        AvatarWithSyncPill(
            imageRes = R.drawable.ic_avatar1,
            size = 46.dp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 22.dp, y = 28.dp)
        )

        // 2. Top-Right
        AvatarWithSyncPill(
            imageRes = R.drawable.ic_avatar2,
            size = 46.dp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-22).dp, y = 28.dp)
        )

        // 3. Bottom-Left
        AvatarWithSyncPill(
            imageRes = R.drawable.ic_avatar3,
            size = 46.dp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 22.dp, y = (-28).dp)
        )

        // 4. Bottom-Right
        AvatarWithSyncPill(
            imageRes = R.drawable.ic_avatar4,
            size = 46.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-22).dp, y = (-28).dp)
        )
    }
}

@Composable
private fun AvatarWithSyncPill(
    imageRes: Int,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .border(2.dp, Color.White, CircleShape),
            contentScale = ContentScale.Crop
        )

        // Small Cyan Sync badge on the bottom right
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 3.dp, y = 3.dp)
                .size(17.dp)
                .clip(CircleShape)
                .background(AppColors.SyncAvatarBadgeCyan)
                .border(1.5.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Sync,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(11.dp)
            )
        }
    }
}

