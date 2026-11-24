package com.policyboss.policybosspro.feature.carInsurance.ui.carPolicyDetail.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.carInsurance.model.MotorQuote
import com.policyboss.policybosspro.feature.carInsurance.ui.carPolicyDetail.model.PolicyBreakupDetails
import com.policyboss.policybosspro.feature.carInsurance.ui.carPolicyDetail.model.PremiumBreakupGroup
import com.policyboss.policybosspro.feature.carInsurance.ui.carPolicyDetail.model.PremiumBreakupItem
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun PremiumBreakupSection(
    breakupDetails: PolicyBreakupDetails,
    modifier: Modifier = Modifier
) {
    // Track expanded state for accordions (default open for primary covers)
    val expandedStates = remember {
        mutableStateMapOf(
            "base_cover" to true,
            "third_party" to true,
            "bundle" to true,
            "addons" to false
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // ==========================================
        // 1. Tab Indicator: "Premium Breakup"
        // ==========================================
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Premium Breakup",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.PrimaryBlue,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Box(
                modifier = Modifier
                    .width(110.dp)
                    .height(2.5.dp)
                    .background(AppColors.PrimaryBlue, RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
            )
            HorizontalDivider(
                color = Color(0xFFE2E8F0),
                thickness = 1.dp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Heading
        Text(
            text = "Premium Breakup",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ==========================================
        // 2. Base Cover Group
        // ==========================================
        BreakupAccordionGroup(
            group = breakupDetails.baseCoverGroup,
            isExpanded = expandedStates["base_cover"] == true,
            onToggleExpand = {
                expandedStates["base_cover"] = !(expandedStates["base_cover"] ?: false)
            }
        )

        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

        // ==========================================
        // 3. Third Party Cover Group
        // ==========================================
        BreakupAccordionGroup(
            group = breakupDetails.thirdPartyGroup,
            isExpanded = expandedStates["third_party"] == true,
            onToggleExpand = {
                expandedStates["third_party"] = !(expandedStates["third_party"] ?: false)
            }
        )

        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

        // ==========================================
        // 4. Bundle Details Group
        // ==========================================
        BreakupAccordionGroup(
            group = breakupDetails.bundleGroup,
            isExpanded = expandedStates["bundle"] == true,
            onToggleExpand = {
                expandedStates["bundle"] = !(expandedStates["bundle"] ?: false)
            }
        )

        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

        // ==========================================
        // 5. Addons Group
        // ==========================================
        BreakupAccordionGroup(
            group = breakupDetails.addonGroup,
            isExpanded = expandedStates["addons"] == true,
            onToggleExpand = {
                expandedStates["addons"] = !(expandedStates["addons"] ?: false)
            }
        )

        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

        // ==========================================
        // 6. Taxes Group
        // ==========================================
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = breakupDetails.taxesGroup.categoryCaption,
                fontSize = 12.sp,
                color = AppColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = breakupDetails.taxesGroup.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextPrimary
                )
                Text(
                    text = breakupDetails.taxesGroup.formattedTotalAmount,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = AppColors.TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

        // ==========================================
        // 7. Final Premium Row
        // ==========================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Final Premium",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )
            Text(
                text = breakupDetails.formattedFinalPremium,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )
        }
    }
}

@Composable
private fun BreakupAccordionGroup(
    group: PremiumBreakupGroup,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Group Category Caption
        Text(
            text = group.categoryCaption,
            fontSize = 12.sp,
            color = AppColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Header Row (Clickable)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .clickable(enabled = group.isExpandable) { onToggleExpand() }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = group.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = AppColors.TextPrimary,
                modifier = Modifier.weight(1f)
            )

            if (group.isExpandable) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = AppColors.TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            Text(
                text = group.formattedTotalAmount,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )
        }

        // Expanded Children
        AnimatedVisibility(
            visible = isExpanded && group.items.isNotEmpty(),
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                group.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.label,
                            fontSize = 13.5.sp,
                            color = AppColors.TextSecondary
                        )
                        Text(
                            text = item.formattedAmount,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.TextSecondary
                        )
                    }
                }
            }
        }

        // Standalone Item (e.g. No Claim Bonus under Base Cover)
        group.standaloneItem?.let { standalone ->
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = standalone.label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = AppColors.TextPrimary
                )
                Text(
                    text = standalone.formattedAmount,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PremiumBreakupSectionPreview() {
    PremiumBreakupSection(
        breakupDetails = PolicyBreakupDetails.createSample()
    )
}
