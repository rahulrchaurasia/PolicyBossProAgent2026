package com.policyboss.policybosspro.feature.profile.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.profile.model.DisplayInformation
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun DisplayInformationSection(
    displayInfo: DisplayInformation,
    useSameAsPosp: Boolean,
    onToggleUseSameAsPosp: (Boolean) -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.ProfileCardBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "Display Information",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Choose the information your customers will see when interacting with you.",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = AppColors.TextSecondary,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Checkbox row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleUseSameAsPosp(!useSameAsPosp) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = useSameAsPosp,
                onCheckedChange = onToggleUseSameAsPosp,
                colors = CheckboxDefaults.colors(
                    checkedColor = AppColors.PrimaryBlue,
                    uncheckedColor = AppColors.BorderPrimary,
                    checkmarkColor = AppColors.White
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Use same as POSP",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.TextPrimary
            )
        }

        // Details shown only when "Use same as POSP" is UNCHECKED
        AnimatedVisibility(
            visible = !useSameAsPosp,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Display Picture",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = AppColors.TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Avatar Image
                Image(
                    painter = painterResource(id = displayInfo.displayPictureRes ?: R.drawable.avatar1),
                    contentDescription = "Display Picture",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .border(1.dp, AppColors.BorderSecondary, CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(14.dp))

                ProfileInfoField(
                    label = "Display Name",
                    value = displayInfo.displayName
                )

                Spacer(modifier = Modifier.height(14.dp))

                ProfileInfoField(
                    label = "Display Mobile Number",
                    value = displayInfo.displayMobile
                )

                Spacer(modifier = Modifier.height(14.dp))

                ProfileInfoField(
                    label = "Display Email",
                    value = displayInfo.displayEmail
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Edit Details Pill Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(AppColors.White)
                        .border(1.dp, AppColors.BorderSecondary, RoundedCornerShape(999.dp))
                        .clickable(onClick = onEditClick),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit details",
                            tint = AppColors.TextSecondaryDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Edit details",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.TextSecondaryDark
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DisplayInformationSectionUncheckedPreview() {
    DisplayInformationSection(
        displayInfo = AppDummyData.defaultUserProfile.displayInfo,
        useSameAsPosp = false,
        onToggleUseSameAsPosp = {},
        onEditClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun DisplayInformationSectionCheckedPreview() {
    DisplayInformationSection(
        displayInfo = AppDummyData.defaultUserProfile.displayInfo,
        useSameAsPosp = true,
        onToggleUseSameAsPosp = {},
        onEditClick = {}
    )
}

