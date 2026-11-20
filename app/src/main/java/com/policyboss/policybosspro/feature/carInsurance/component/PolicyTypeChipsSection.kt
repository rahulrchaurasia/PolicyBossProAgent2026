package com.policyboss.policybosspro.feature.carInsurance.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.ui.theme.AppColors

/**
 * Standardized Preferred Policy Type Segmented Control.
 * Renders options in a single bordered rounded container where the active option
 * has a soft mint green background (#E8F8F0) and dark green text (#15803D).
 *
 * Enforces a single horizontal row with weighted distribution so labels
 * never wrap onto a second line across different device screen widths.
 */
@Composable
fun PolicyTypeChipsSection(
    options: List<String>,
    selectedPolicyType: String,
    onPolicyTypeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Preferred policy type",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = Color.White,
            border = BorderStroke(1.dp, AppColors.BorderSecondary)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                options.forEach { type ->
                    val isSelected = selectedPolicyType.equals(type, ignoreCase = true) ||
                            (type == "TP" && selectedPolicyType.startsWith("TP", ignoreCase = true)) ||
                            (type.startsWith("Comprehensive", ignoreCase = true) && selectedPolicyType.startsWith("Comprehensive", ignoreCase = true))

                    val displayName = when {
                        type.startsWith("Comprehensive", ignoreCase = true) -> "Comprehensive"
                        type.startsWith("TP", ignoreCase = true) -> "TP"
                        type.startsWith("OD", ignoreCase = true) -> "OD"
                        else -> type
                    }

                    val weight = if (displayName.equals("Comprehensive", ignoreCase = true)) 1.35f else 1f

                    Box(
                        modifier = Modifier
                            .weight(weight)
                            .height(40.dp)
                            .background(
                                color = if (isSelected) Color(0xFFE8F8F0) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onPolicyTypeSelected(type) }
                            .padding(horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayName,
                            fontSize = 12.5.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFF15803D) else AppColors.TextSecondary,
                            maxLines = 1,
                            softWrap = false,
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
private fun PolicyTypeChipsSectionPreview() {
    PolicyTypeChipsSection(
        options = listOf("Comprehensive", "TP"),
        selectedPolicyType = "Comprehensive",
        onPolicyTypeSelected = {}
    )
}
