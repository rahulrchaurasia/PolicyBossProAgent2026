package com.policyboss.policybosspro.feature.home.component.home.header

import com.policyboss.policybosspro.ui.theme.gradients.AppGradients

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.ui.theme.AppColors

import com.policyboss.policybosspro.ui.theme.labelMediumSemiBold


@Composable
fun HomeTopBar(
    userInitials: String,
    onProfileClick: () -> Unit,
    onWalletClick: () -> Unit,
    onSupportClick: () -> Unit,
    onNotificationClick: () -> Unit = {},
    onLanguageClick: () -> Unit = {},
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User Initials Avatar with AppGradients
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(brush = AppGradients.PolicyCardGradient)
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userInitials.ifEmpty { "DD" },
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Action Buttons Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Wallet Badge Pill
            Surface(
                onClick = onWalletClick,
                shape = CircleShape, // Creates the perfect pill shape
                color = AppColors.Surface, // White background matching screenshot
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_wallet),
                        contentDescription = "Wallet",
                        tint = AppColors.PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Wallet",
                        style = MaterialTheme.typography.labelMediumSemiBold,
                        color = AppColors.TextPrimary
                    )
                }
            }

            // Support Headset Icon
            TopBarIconButton(
                iconRes = R.drawable.ic_headset,
                contentDescription = "Support",
                onClick = onSupportClick
            )

            // Language / Translation Icon
//            TopBarIconButton(
//                iconRes = R.drawable.ic_translate,
//                contentDescription = "Language",
//                onClick = onLanguageClick
//            )

            // Menu Hamburger Icon
            TopBarIconButton(
                iconRes = R.drawable.ic_menu,
                contentDescription = "Menu",
                onClick = onMenuClick
            )
        }
    }
}

// Reusable component for the circular white icon buttons to keep code DRY
@Composable
private fun TopBarIconButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = AppColors.Surface, // White background matching screenshot
        modifier = Modifier.size(48.dp) // Matches the height of the avatar and wallet pill
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = contentDescription,
                tint = AppColors.TextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFE2F0FE)
@Composable
private fun HomeTopBarPreview() {
    HomeTopBar(
        userInitials = "DD",
        onProfileClick = {},
        onWalletClick = {},
        onSupportClick = {},
        onMenuClick = {}
    )
}