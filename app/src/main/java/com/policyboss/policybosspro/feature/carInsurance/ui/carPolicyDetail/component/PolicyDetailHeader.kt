package com.policyboss.policybosspro.feature.carInsurance.ui.carPolicyDetail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun PolicyDetailHeader(
    crnNumber: String,
    insurerName: String,
    insurerLogoType: String,
    isInfoExpanded: Boolean,
    onToggleInfo: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            // ==========================================
            // Row 1: CRN Badge + Center Insurer Logo + Close (X) Button
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: CRN Gradient Pill Badge
                Box(
                    modifier = Modifier
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF1D4ED8), Color(0xFF3B82F6))
                            )
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CRN: ${crnNumber.ifBlank { "12682505" }}",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Center: Insurer Logo (Cleanly centered with zero CRN overlap)
                val logoRes = when {
                    insurerLogoType.contains("tata", ignoreCase = true) || insurerName.contains("tata", ignoreCase = true) -> R.drawable.img_tata
                    insurerLogoType.contains("acko", ignoreCase = true) || insurerName.contains("acko", ignoreCase = true) -> R.drawable.ic_acko_logo
                    else -> R.drawable.img_tata
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(logoRes),
                        contentDescription = "$insurerName Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(32.dp)
                            .defaultMinSize(minWidth = 60.dp)
                    )
                }

                // Right: Close Button
                IconButton(
                    onClick = onCloseClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = AppColors.TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // ==========================================
            // Row 2: Insurer Name (Left) + View Info / Hide Info (Right)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = insurerName.ifBlank { "Tata AIG" },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .defaultMinSize(minHeight = 32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onToggleInfo() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isInfoExpanded) "Hide Info" else "View Info",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.PrimaryBlue
                    )
                }
            }

            HorizontalDivider(
                color = Color(0xFFE2E8F0),
                thickness = 1.dp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PolicyDetailHeaderPreview() {
    Column {
        PolicyDetailHeader(
            crnNumber = "12682505",
            insurerName = "Tata AIG",
            insurerLogoType = "tata",
            isInfoExpanded = false,
            onToggleInfo = {},
            onCloseClick = {}
        )
        PolicyDetailHeader(
            crnNumber = "12682505",
            insurerName = "Tata AIG",
            insurerLogoType = "tata",
            isInfoExpanded = true,
            onToggleInfo = {},
            onCloseClick = {}
        )
    }
}
