package com.policyboss.policybosspro.feature.home.component.home.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.bodyMediumBold
import com.policyboss.policybosspro.ui.theme.captionSmall
import com.policyboss.policybosspro.ui.theme.labelSmallBold

@Composable
fun SyncContactsBanner(
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bannerGradient = remember {
        Brush.horizontalGradient(
            colors = listOf(AppColors.SyncBannerGradientStart, AppColors.SyncBannerGradientEnd)
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(bannerGradient)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "BOSS ALERTS",
                            style = MaterialTheme.typography.labelSmallBold,
                            color = AppColors.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = "• PolicyBoss",
                            style = MaterialTheme.typography.captionSmall,
                            color = AppColors.White.copy(alpha = 0.75f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Get an alert when a synced contact renews their insurance policy.",
                        color = AppColors.White,
                        style = MaterialTheme.typography.bodyMediumBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        modifier = Modifier.clickable { onSyncClick() },
                        shape = RoundedCornerShape(20.dp),
                        color = AppColors.BrandDark
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sync Now  FREE",
                                color = AppColors.White,
                                style = MaterialTheme.typography.labelSmallBold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chevron_right),
                                contentDescription = null,
                                tint = AppColors.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Megaphone / Notification Bell Graphic
                Image(
                    painter = painterResource(id = R.drawable.ic_security_check),
                    contentDescription = "Alerts Bell",
                    modifier = Modifier.size(68.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SyncContactsBannerPreview() {
    SyncContactsBanner(onSyncClick = {})
}
