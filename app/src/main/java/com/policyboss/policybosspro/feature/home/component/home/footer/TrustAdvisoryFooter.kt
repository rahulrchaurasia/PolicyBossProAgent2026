package com.policyboss.policybosspro.feature.home.component.home.footer

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.home.model.home.TrustBulletPoint
import com.policyboss.policybosspro.ui.theme.AppColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TrustAdvisoryFooter(
    bullets: List<TrustBulletPoint>,
    partnerLogos: List<Int>,
    onPartnerLogoClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Heading
        Text(
            text = "Comparison + Convenience + Advisory = Us ❤️",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary,
            lineHeight = 26.sp
        )

        // Bullet points with checkmarks
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            bullets.forEach { bullet ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check_circle),
                        contentDescription = null,
                        tint = AppColors.SuccessGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = bullet.text,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Partner Insurers Brand Logos Grid
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            maxItemsInEachRow = 3
        ) {
            partnerLogos.forEach { logoRes ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Transparent,
                    modifier = Modifier.clickable { onPartnerLogoClick(logoRes) }
                ) {
                    Image(
                        painter = painterResource(id = logoRes),
                        contentDescription = "Partner Insurer",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(width = 100.dp, height = 36.dp)
                            .alpha(0.85f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Regulatory / Trust Footer note
        Text(
            text = "PolicyBoss is an IRDAI licensed Direct Insurance Broker.\nCIN: U74999MH2002PTC137190",
            fontSize = 11.sp,
            color = AppColors.TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TrustAdvisoryFooterPreview() {
    TrustAdvisoryFooter(
        bullets = AppDummyData.trustBullets,
        partnerLogos = AppDummyData.partnerLogos,
        onPartnerLogoClick = {}
    )
}
