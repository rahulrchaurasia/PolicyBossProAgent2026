package com.policyboss.policybosspro.feature.home.component.home.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.PolicyBossTheme
import com.policyboss.policybosspro.ui.theme.bodyMediumBold

@Composable
private fun PolicyDetailsCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AppColors.White)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo section
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(58.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AppColors.LogoContainerBackground),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.img_tata),
                contentDescription = "Tata AIG",
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(38.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        VerticalDivider(
            modifier = Modifier.height(40.dp),
            color = AppColors.Dividers
        )

        DataColumn(
            title = "IDV",
            value = "₹5L",
            modifier = Modifier.weight(1f)
        )

        VerticalDivider(
            modifier = Modifier.height(40.dp),
            color = AppColors.Dividers
        )

        DataColumn(
            title = "Premium",
            value = "₹6,403",
            modifier = Modifier.weight(1f)
        )

        VerticalDivider(
            modifier = Modifier.height(40.dp),
            color = AppColors.Dividers
        )

        DataColumn(
            title = "Expiry",
            value = "21.02.26",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DataColumn(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMediumBold,
            color = AppColors.TextPrimaryDark
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = AppColors.TextSecondary
        )
    }
}




@Preview(
    showBackground = true,
    showSystemUi = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
private fun PolicyDetailsCardPreview() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(16.dp)
    ) {
        PolicyDetailsCard()
    }
}

@Preview(showBackground = true)
@Composable
private fun PolicyDetailsAlterCardPreview() {

    PolicyBossTheme() {
        Box(
            modifier = Modifier
                .background(AppColors.Background)
                .padding(16.dp)
        ) {
            PolicyDetailsCard()
        }
    }
}