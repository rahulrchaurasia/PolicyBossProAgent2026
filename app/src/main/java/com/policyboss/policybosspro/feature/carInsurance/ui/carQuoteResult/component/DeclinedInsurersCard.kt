package com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteResult.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.policyboss.policybosspro.feature.carInsurance.model.DeclinedInsurer
import com.policyboss.policybosspro.ui.theme.AppColors

/** Insurers that answered premium_list_db without a plan, each with its reason. */
@Composable
fun DeclinedInsurersCard(
    insurers: List<DeclinedInsurer>,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = AppColors.White,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Quotes not available from ${insurers.size} insurer${if (insurers.size == 1) "" else "s"}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Slate800
            )
            insurers.forEach { insurer ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!insurer.logoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = insurer.logoUrl,
                            contentDescription = insurer.insurerName,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(width = 38.dp, height = 22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = insurer.insurerName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.Slate800
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = insurer.reason,
                            fontSize = 12.sp,
                            color = AppColors.Slate500,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeclinedInsurersCardPreview() {
    DeclinedInsurersCard(
        insurers = listOf(
            DeclinedInsurer(4, "Future Generali", null, "LM009", "Vehicle Underwriting Restriction"),
            DeclinedInsurer(13, "Oriental Insurance", null, "LM002", "Un-Identified Error")
        ),
        modifier = Modifier.padding(16.dp)
    )
}
