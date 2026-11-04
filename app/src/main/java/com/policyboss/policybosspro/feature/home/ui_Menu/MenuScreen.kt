package com.policyboss.policybosspro.feature.home.ui_Menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.ui.theme.AppColors
// Import typography extensions
import com.policyboss.policybosspro.ui.theme.*

@Composable
fun MenuScreen(
    contentPadding: PaddingValues,
    onCloseClick: () -> Unit,
    onNavigateToProfile: () -> Unit,
    uiState: MenuUiState = MenuUiState(),
    onAction: (MenuAction) -> Unit = {}
) {
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val displayCutoutTop = WindowInsets.displayCutout.asPaddingValues().calculateTopPadding()
    val topSafeArea = maxOf(contentPadding.calculateTopPadding(), statusBarTop, displayCutoutTop)

    // Logout confirmation dialog
    if (uiState.showLogoutDialog) {
        LogoutConfirmationDialog(
            isLoading = uiState.isLoggingOut,
            onConfirm = { onAction(MenuAction.OnConfirmLogout) },
            onDismiss = { onAction(MenuAction.OnDismissLogoutDialog) }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppColors.Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = topSafeArea,
                    bottom = contentPadding.calculateBottomPadding()
                )
        ) {
            // Top App Bar for Menu with Close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.titleLargeBold
                )
                IconButton(onClick = onCloseClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Close Menu"
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. User Header Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(AppColors.PrimaryBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = uiState.userInitials,
                                        color = Color.White,
                                        style = MaterialTheme.typography.headlineMediumBold
                                    )
                                }
                                Column {
                                    Text(
                                        text = uiState.displayName,
                                        style = MaterialTheme.typography.displayMedium
                                    )
                                    Text(
                                        text = "SSID: ${uiState.displaySsId}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AppColors.TextSecondary
                                    )
                                    Text(
                                        text = "RFA ID: ${uiState.displayFbaId}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AppColors.TextSecondary
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onNavigateToProfile,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "Edit Profile",
                                        style = MaterialTheme.typography.labelMediumSemiBold
                                    )
                                }

                                Button(
                                    onClick = onNavigateToProfile,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryBlue)
                                ) {
                                    Text(
                                        text = "View Profile",
                                        style = MaterialTheme.typography.labelMediumSemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Search Bar
                item {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Try searching \"Make a Claim\"",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = { Icon(painter = painterResource(id = R.drawable.ic_camera), contentDescription = null) },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                // 3. Products Grid Section Header
                item {
                    Text(
                        text = "PRODUCTS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = AppColors.TextSecondary
                    )
                }

                // Products list rows
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppDummyData.menuProducts.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowItems.forEach { product ->
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { },
                                        shape = RoundedCornerShape(12.dp),
                                        color = AppColors.Surface,
                                        border = BorderStroke(1.dp, AppColors.BorderSecondary)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                painter = painterResource(id = product.iconRes),
                                                contentDescription = null,
                                                tint = AppColors.PrimaryBlue,
                                                modifier = Modifier.size(22.dp)
                                            )
                                            Column {
                                                Text(
                                                    text = product.title,
                                                    style = MaterialTheme.typography.labelMediumSemiBold
                                                )
                                                if (product.isComingSoon) {
                                                    Text(
                                                        text = "Coming soon",
                                                        style = MaterialTheme.typography.captionSmall.copy(
                                                            fontWeight = FontWeight.Normal
                                                        ),
                                                        color = AppColors.ComingSoonOrange
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                if (rowItems.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                // 4. Manage Business Items
                item {
                    Text(
                        text = "MANAGE BUSINESS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = AppColors.TextSecondary
                    )
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
                    ) {
                        Column {
                            MenuItemRow(title = "Boss Alerts", badgeCount = "5")
                            HorizontalDivider(color = AppColors.BorderSecondary)
                            MenuItemRow(title = "Sync Dashboard")
                            HorizontalDivider(color = AppColors.BorderSecondary)
                            MenuItemRow(title = "Wallet", trailingText = "₹2,034")
                            HorizontalDivider(color = AppColors.BorderSecondary)
                            MenuItemRow(title = "Zextra")
                        }
                    }
                }

                // 5. Help & Support Items
                item {
                    Text(
                        text = "HELP & SUPPORT",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = AppColors.TextSecondary
                    )
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
                    ) {
                        Column {
                            MenuItemRow(title = "Raise a Ticket")
                            HorizontalDivider(color = AppColors.BorderSecondary)
                            MenuItemRow(title = "Make a Claim")
                            HorizontalDivider(color = AppColors.BorderSecondary)
                            MenuItemRow(title = "Health Advisory")
                        }
                    }
                }

                // 6. Account & Session Section (Logout)
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
                    ) {
                        MenuItemRow(
                            title = "Logout",
                            iconRes = R.drawable.ic_logout,
                            iconTint = AppColors.ErrorRed,
                            titleColor = AppColors.ErrorRed,
                            showChevron = true,
                            onClick = { onAction(MenuAction.OnLogoutClick) }
                        )
                    }
                }
            }

            // Bottom Version Footer (placed at the bottom of the Column above the system navigation bar)
            AppVersionFooter(
                version = uiState.appVersion
            )
        }
    }
}

@Composable
fun MenuItemRow(
    title: String,
    modifier: Modifier = Modifier,
    iconRes: Int? = null,
    iconTint: Color = AppColors.TextSecondary,
    titleColor: Color = Color.Unspecified,
    badgeCount: String? = null,
    trailingText: String? = null,
    showChevron: Boolean = true,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (iconRes != null) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                color = titleColor
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (badgeCount != null) {
                Surface(
                    shape = CircleShape,
                    color = AppColors.RedBadges
                ) {
                    Text(
                        text = badgeCount,
                        color = Color.White,
                        style = MaterialTheme.typography.captionSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            if (trailingText != null) {
                Text(
                    text = trailingText,
                    color = AppColors.PrimaryBlue,
                    style = MaterialTheme.typography.bodyMediumBold
                )
            }
            if (showChevron) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = AppColors.TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Logout Confirmation Dialog with loading progress and safe dismissal
 */
@Composable
fun LogoutConfirmationDialog(
    isLoading: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Text(
                text = "Log Out",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            if (isLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = AppColors.PrimaryBlue
                    )
                    Text(
                        text = "Logging out...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextSecondary
                    )
                }
            } else {
                Text(
                    text = "Are you sure you want to log out?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !isLoading,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = AppColors.ErrorRed
                )
            ) {
                Text(
                    text = "Log Out",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = AppColors.TextSecondary
                )
            ) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Clean, secondary app version footer
 */
@Composable
fun AppVersionFooter(
    version: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        val displayVersion = if (version.isNotBlank()) {
            if (version.startsWith("Version", ignoreCase = true) || version.startsWith("v", ignoreCase = true)) {
                version
            } else {
                "Version $version"
            }
        } else {
            "Version 1.0"
        }
        Text(
            text = displayVersion,
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7FAFC, name = "Menu Screen Preview")
@Composable
fun MenuScreenPreview() {
    MenuScreen(
        contentPadding = PaddingValues(0.dp),
        onCloseClick = {},
        onNavigateToProfile = {}
    )
}
