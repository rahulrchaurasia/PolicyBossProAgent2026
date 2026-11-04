package com.policyboss.policybosspro.feature.profile.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.profile.model.PersonalInformation
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun PersonalInformationSection(
    personalInfo: PersonalInformation,
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
            text = "Personal Information",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(14.dp))

        ProfileInfoField(
            label = "Mobile",
            value = personalInfo.mobile
        )

        Spacer(modifier = Modifier.height(14.dp))

        ProfileInfoField(
            label = "Date of Birth",
            value = personalInfo.dob
        )

        Spacer(modifier = Modifier.height(14.dp))

        ProfileInfoField(
            label = "Email",
            value = personalInfo.email
        )

        Spacer(modifier = Modifier.height(14.dp))

        ProfileInfoField(
            label = "PAN Card",
            value = personalInfo.pan
        )

        Spacer(modifier = Modifier.height(14.dp))

        ProfileInfoField(
            label = "Address",
            value = personalInfo.address
        )

        Spacer(modifier = Modifier.height(14.dp))

        ProfileInfoField(
            label = "City",
            value = personalInfo.city
        )

        Spacer(modifier = Modifier.height(14.dp))

        ProfileInfoField(
            label = "State",
            value = personalInfo.state
        )

        Spacer(modifier = Modifier.height(14.dp))

        ProfileInfoField(
            label = "Pincode",
            value = personalInfo.pincode
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

@Preview(showBackground = true)
@Composable
private fun PersonalInformationSectionPreview() {
    PersonalInformationSection(
        personalInfo = PersonalInformation(),
        onEditClick = {}
    )
}

