package com.policyboss.policybosspro.feature.syncContact.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.syncContact.model.SyncContactsUiState
import com.policyboss.policybosspro.feature.syncContact.ui.components.EarningHighlightCard
import com.policyboss.policybosspro.feature.syncContact.ui.components.PhoneNetworkIllustration
import com.policyboss.policybosspro.feature.syncContact.ui.components.PillCtaButton
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.PolicyBossTheme

@Composable
fun SyncContactsScreen(
    uiState: SyncContactsUiState,
    onSyncClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Heading
                Text(
                    text = "Account Created!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Subtitle
                Text(
                    text = "Now, add your network to help them save\non insurance while you earn on their\nrenewals",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp,
                    color = AppColors.TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Earning Highlight Card
                EarningHighlightCard(
                    title = "On an avg. users earn up to",
                    amount = uiState.avgEarnings,
                    suffix = "/year"
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Concentric Phone & Avatars Graphic
                PhoneNetworkIllustration()

                Spacer(modifier = Modifier.height(16.dp))

                // Benefits Tag Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(AppColors.RewardPillBackground)
                        .border(1.dp, AppColors.RewardPillBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Syncing helps earn extra rewards",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.PrimaryDark
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Privacy Trust Line
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_lock),
                        contentDescription = null,
                        tint = AppColors.Primary,
                        modifier = Modifier.size(13.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "No harm to privacy, no spams, trusted security",
                        fontSize = 11.5.sp,
                        color = AppColors.TextSecondary
                    )
                }
            }

            // Bottom Section: CTA Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PillCtaButton(
                    text = "Sync Contacts",
                    onClick = onSyncClicked
                )
            }
        }
    }
}

@Preview(name = "Sync Contacts Introduction Preview", showBackground = true)
@Composable
private fun SyncContactsScreenPreview() {
    PolicyBossTheme {
        SyncContactsScreen(
            uiState = SyncContactsUiState(),
            onSyncClicked = {}
        )
    }
}

