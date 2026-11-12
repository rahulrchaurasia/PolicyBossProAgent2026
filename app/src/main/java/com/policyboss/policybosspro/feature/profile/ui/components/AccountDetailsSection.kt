package com.policyboss.policybosspro.feature.profile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.profile.model.AccountDetails
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun AccountDetailsSection(
    accountDetails: AccountDetails,
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
            text = "Account Details",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            ProfileInfoField(
                label = "SS ID",
                value = accountDetails.ssId,
                modifier = Modifier.weight(1f)
            )
            ProfileInfoField(
                label = "Plan",
                value = accountDetails.plan,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            ProfileInfoField(
                label = "RM Name",
                value = accountDetails.rmName,
                modifier = Modifier.weight(1f)
            )
            ProfileInfoField(
                label = "RM Mobile",
                value = accountDetails.rmMobile,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        ProfileInfoField(
            label = "RM Email",
            value = accountDetails.rmEmail,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AccountDetailsSectionPreview() {
    AccountDetailsSection(
        accountDetails = AppDummyData.defaultUserProfile.accountDetails
    )
}

