package com.policyboss.policybosspro.feature.carInsurance.ui.results

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun QuoteResultsBottomBar(
    vehicleSummary: String,
    onEditVehicleClick: () -> Unit,
    onFiltersClick: () -> Unit,
    onSortClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                spotColor = Color.Black.copy(alpha = 0.12f)
            ),
        color = AppColors.White,
        border = BorderStroke(1.dp, AppColors.Slate200)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            // Vehicle Info Summary Row with Edit Pencil
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = vehicleSummary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.Slate600,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 15.sp,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onEditVehicleClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit Vehicle Details",
                        tint = AppColors.PrimaryBlue,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            HorizontalDivider(
                color = AppColors.Slate200,
                thickness = 1.dp
            )

            // Action Buttons: Filters | Sort by | Share
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Filters
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onFiltersClick() }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_filter),
                        contentDescription = "Filters",
                        tint = AppColors.Slate700,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Filters",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.Slate700
                    )
                }

                VerticalDivider(
                    modifier = Modifier.height(20.dp),
                    color = AppColors.Slate200,
                    thickness = 1.dp
                )

                // Sort by
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSortClick() }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_sort_by),
                        contentDescription = "Sort by",
                        tint = AppColors.Slate700,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Sort by",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.Slate700
                    )
                }

                VerticalDivider(
                    modifier = Modifier.height(20.dp),
                    color = AppColors.Slate200,
                    thickness = 1.dp
                )

                // Share
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onShareClick() }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share",
                        tint = AppColors.Slate700,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Share",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.Slate700
                    )
                }
            }
        }
    }
}

