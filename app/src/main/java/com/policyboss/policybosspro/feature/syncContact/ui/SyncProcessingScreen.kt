package com.policyboss.policybosspro.feature.syncContact.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.syncContact.model.SyncProcessingState
import com.policyboss.policybosspro.feature.syncContact.ui.components.AvatarClusterView
import com.policyboss.policybosspro.feature.syncContact.ui.components.BossCoinRewardCard
import com.policyboss.policybosspro.feature.syncContact.ui.components.EarningHighlightCard
import com.policyboss.policybosspro.feature.syncContact.ui.components.PillCtaButton
import com.policyboss.policybosspro.feature.syncContact.ui.components.RadarRingsGraphic
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.PolicyBossTheme

@Composable
fun SyncProcessingScreen(
    uiState: SyncProcessingState,
    onCompleteProfileClick: () -> Unit,
    onRetryClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Crossfade(
        targetState = uiState.isSyncing,
        animationSpec = tween(durationMillis = 400),
        label = "SyncProcessingCrossfade",
        modifier = modifier.fillMaxSize()
    ) { isSyncing ->
        if (isSyncing || uiState.errorMessage != null && !uiState.isSuccess) {
            // ==========================================
            // SCREEN B: Fetching Contacts / Loading State
            // ==========================================
            FetchingContactsContent(
                uiState = uiState,
                onRetryClick = onRetryClick
            )
        } else {
            // ==========================================
            // SCREEN C: Contacts Fetched / Success State
            // ==========================================
            ContactsFetchedSuccessContent(
                uiState = uiState,
                onDiscoverClick = onCompleteProfileClick
            )
        }
    }
}

/**
 * Screen B: Dark Background with Radar Rings and Progress Bar
 */
@Composable
private fun FetchingContactsContent(
    uiState: SyncProcessingState,
    onRetryClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    key(uiState.currentPhase) {
        val animatedProgress by animateFloatAsState(
            targetValue = uiState.progress.coerceIn(0f, 1f),
            animationSpec = tween(durationMillis = 250),
            label = "AnimatedProgressBar"
        )

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(AppColors.SyncDarkSurface)
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            // Radar Concentric Rings
            RadarRingsGraphic(
                ringColor = AppColors.SyncRadarRing,
                ringCount = 5,
                enablePulse = true
            )

            // Center Content: Avatar Cluster + Progress Bar
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                AvatarClusterView(
                    count = uiState.contactsFound,
                    statusText = uiState.statusMessage,
                    statusTextColor = Color.White
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Smooth Horizontal Progress Bar
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(AppColors.SyncProgressTrack)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(AppColors.HighlightCardGold)
                    )
                }

                if (!uiState.errorMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = uiState.errorMessage,
                        color = AppColors.ErrorLight,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PillCtaButton(
                        text = "Retry Sync",
                        onClick = onRetryClick
                    )
                }
            }
        }
    }
}

@Composable
private fun FetchingContactsContent(
    progress: Float,
    contactsFound: Int,
    statusMessage: String = "Fetching contacts...",
    errorMessage: String? = null,
    onRetryClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    FetchingContactsContent(
        uiState = SyncProcessingState(
            progress = progress,
            contactsFound = contactsFound,
            statusMessage = statusMessage,
            errorMessage = errorMessage
        ),
        onRetryClick = onRetryClick,
        modifier = modifier
    )
}

/**
 * Screen C: Vibrant Blue Background with Earning Cards and Bottom White Card
 */
@Composable
private fun ContactsFetchedSuccessContent(
    uiState: SyncProcessingState,
    onDiscoverClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.SyncSuccessBackground)
            .statusBarsPadding()
    ) {
        // Background Concentric Rings (faint white)
        RadarRingsGraphic(
            ringColor = Color.White.copy(alpha = 0.12f),
            ringCount = 4,
            enablePulse = false
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Scrollable upper section with dynamic cards
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Avatar cluster with final count
                AvatarClusterView(
                    count = uiState.contactsFound,
                    statusText = "Contacts fetched",
                    statusTextColor = Color.White
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Earning Potential Card
                EarningHighlightCard(
                    title = "Your earning potential",
                    amount = uiState.earningPotential,
                    suffix = "/year"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Boss Coins Reward Card
                BossCoinRewardCard(
                    bossCoins = uiState.bossCoins
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Bottom White Card Container
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Congratulations!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Limitless earning awaiting",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    PillCtaButton(
                        text = "Discover Policy Boss",
                        onClick = onDiscoverClick
                    )
                }
            }
        }
    }
}

@Preview(name = "Screen B: Fetching Contacts Preview", showBackground = true)
@Composable
private fun FetchingContactsPreview() {
    PolicyBossTheme {
        SyncProcessingScreen(
            uiState = SyncProcessingState(
                isSyncing = true,
                progress = 0.6f,
                contactsFound = 142
            ),
            onCompleteProfileClick = {}
        )
    }
}

@Preview(name = "Screen C: Contacts Fetched Preview", showBackground = true)
@Composable
private fun ContactsFetchedPreview() {
    PolicyBossTheme {
        SyncProcessingScreen(
            uiState = SyncProcessingState(
                isSyncing = false,
                isSuccess = true,
                progress = 1.0f,
                contactsFound = 219
            ),
            onCompleteProfileClick = {}
        )
    }
}

