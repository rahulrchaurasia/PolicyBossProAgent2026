package com.policyboss.policybosspro.feature.carInsurance.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.ui.theme.AppColors

/**
 * Standardized Vahan Data Card displayed below the header in WITH_CAR_NUMBER flow.
 * Elevated inside a subtle white card with 12.dp rounded corners, muted label,
 * and bold brand blue specification text.
 */
@Composable
fun VahanDataBanner(
    vahanDetails: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AppColors.BorderSecondary),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Vahan Data:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = AppColors.TextSecondary
            )
            Text(
                text = vahanDetails,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.PrimaryBlue,
                lineHeight = 18.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VahanDataBannerPreview() {
    VahanDataBanner(
        vahanDetails = "HONDA CARS INDIA LTD, AMAZE 1.2 V CVT (I-VTEC), PETROL (1199 CC)",
        modifier = Modifier.padding(16.dp)
    )
}
