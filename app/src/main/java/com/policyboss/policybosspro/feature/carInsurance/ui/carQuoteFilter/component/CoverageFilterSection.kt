package com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.component

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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.carInsurance.model.MotorQuote
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.CoverageBadgeType
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.CoverageOption
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.MotorQuoteFilterData
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun CoverageFilterSection(
    selectedCoverageId: String,
    onCoverageSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    options: List<CoverageOption> = MotorQuoteFilterData.coverageOptions
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        // Top Soft-Amber CPA Callout Card
        val callout = MotorQuoteFilterData.coverageCallouts.first()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFFEFCE8))
                .border(1.dp, Color(0xFFFDE047), RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = callout.title,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB45309)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = callout.description,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFB45309),
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Pager Indicator Dots (• ◦ ◦ ◦)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(4) { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 2.5.dp)
                                .size(5.dp)
                                .background(
                                    color = if (index == 0) Color(0xFF0F172A) else Color(0xFFCBD5E1),
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Single-Select Coverage Rows
        options.forEachIndexed { index, option ->
            val isSelected = option.id == selectedCoverageId

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCoverageSelected(option.id) }
                    .padding(vertical = 10.dp)
            ) {
                // Top Row: Badge + Radio Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CoverageBadge(badgeText = option.badgeText, badgeType = option.badgeType)

                    RadioButton(
                        selected = isSelected,
                        onClick = { onCoverageSelected(option.id) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = AppColors.PrimaryBlue,
                            unselectedColor = AppColors.Slate300
                        ),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Title
                Text(
                    text = option.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.Slate900,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Premium Range Subtitle
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Premium range : ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Slate500
                    )
                    Text(
                        text = "₹${MotorQuote.formatIndianCurrency(option.minPrice)} - ${MotorQuote.formatIndianCurrency(option.maxPrice)}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Slate900
                    )
                    Text(
                        text = " (excl. GST)",
                        fontSize = 11.sp,
                        color = AppColors.Slate500
                    )
                }
            }

            if (index < options.lastIndex) {
                HorizontalDivider(color = AppColors.Slate100, thickness = 1.dp)
            }
        }
    }
}

@Composable
private fun CoverageBadge(
    badgeText: String,
    badgeType: CoverageBadgeType,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (badgeType) {
        CoverageBadgeType.RECOMMENDED -> Color(0xFF2563EB) to Color.White
        CoverageBadgeType.SMART_VALUE -> Color(0xFFE0F2FE) to Color(0xFF0284C7)
        CoverageBadgeType.ALL_ROUND -> Color(0xFFE0F2FE) to Color(0xFF0284C7)
        CoverageBadgeType.BASE -> Color(0xFFF1F5F9) to Color(0xFF64748B)
        CoverageBadgeType.PA_EXCLUDED -> Color(0xFFF1F5F9) to Color(0xFF64748B)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        if (badgeType == CoverageBadgeType.RECOMMENDED) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(10.dp)
            )
        }
        Text(
            text = badgeText,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CoverageFilterSectionPreview() {
    CoverageFilterSection(
        selectedCoverageId = "comprehensive_cpa_zero_dep_rsa",
        onCoverageSelected = {}
    )
}
