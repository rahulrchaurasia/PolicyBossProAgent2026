package com.policyboss.policybosspro.feature.home.component.home.item

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import com.policyboss.policybosspro.R
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.home.model.home.InsurerQuote
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
 fun InsurerQuoteCardItem(
    quote: InsurerQuote,
    modifier: Modifier = Modifier
)
{
    Card(
        modifier = modifier.width(220.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        border = BorderStroke(1.dp, AppColors.BorderSecondary)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Insurer Logo & Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = quote.logoRes),
                        contentDescription = quote.insurerName,
                        modifier = Modifier
                            .height(20.dp)
                            .width(50.dp)
                    )
                }

                Text(
                    text = quote.insurerName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Premium & IDV Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Premium",
                            fontSize = 11.sp,
                            color = AppColors.TextSecondary
                        )
                        Text(
                            text = quote.premium,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "IDV",
                            fontSize = 11.sp,
                            color = AppColors.TextSecondary
                        )
                        Text(
                            text = quote.idv,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary
                        )
                    }
                }

                // Add-ons list
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Add-ons",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextSecondary
                    )
                    quote.addons.forEach { addon ->
                        Text(
                            text = "• $addon",
                            fontSize = 11.sp,
                            color = AppColors.TextSecondaryDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Brokerage Yellow Highlight Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.StarBadgeBackground)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Brokerage",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.BrokerageText
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_gold_coin),
                            contentDescription = null,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = quote.brokerage,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.BrokerageText
                        )
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------------------
// 3. Android Studio Previews
// ------------------------------------------------------------------------

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC, name = "Single Quote Card")
@Composable
private fun InsurerQuoteCardItemPreview() {
    val sampleQuote = InsurerQuote(
        // Using built-in Android drawable so Preview works out-of-the-box;
        // Replace with your R.drawable.ic_insurer_logo
        logoRes = android.R.drawable.sym_def_app_icon,
        insurerName = "HDFC ERGO General Insurance",
        premium = "₹12,450",
        idv = "₹6,85,000",
        addons = listOf(
            "Zero Depreciation",
            "Engine & Gearbox Protection",
            "24x7 Roadside Assistance"
        ),
        brokerage = "₹1,250",
        id = "1",
    )

    Surface(
        color = AppColors.Slate50,
        modifier = Modifier.padding(16.dp)
    ) {
        InsurerQuoteCardItem(quote = sampleQuote)
    }
}