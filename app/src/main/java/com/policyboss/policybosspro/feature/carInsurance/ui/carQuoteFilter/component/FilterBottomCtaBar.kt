package com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun FilterBottomCtaBar(
    activeFilterCount: Int,
    onApplyClick: () -> Unit,
    onResetClick: (() -> Unit)? = null,
    isApplyEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        color = Color.White
    ) {
        val buttonText = if (activeFilterCount > 0) {
            "Apply filters ($activeFilterCount)"
        } else {
            "Apply"
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (onResetClick != null) {
                Text(
                    text = "Reset",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.Slate500,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onResetClick() }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .shadow(elevation = if (isApplyEnabled) 4.dp else 0.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(if (isApplyEnabled) AppColors.PrimaryBlue else AppColors.PrimaryBlue.copy(alpha = 0.5f))
                    .clickable(enabled = isApplyEnabled) { onApplyClick() }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Centered Title
                Text(
                    text = buttonText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Trailing White Circular Badge with Blue Right Arrow
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(32.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Apply Filters",
                        tint = AppColors.PrimaryBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Preview(name = "Filter Bottom CTA Bar", showBackground = true)
@Composable
private fun FilterBottomCtaBarPreview() {
    FilterBottomCtaBar(
        activeFilterCount = 6,
        onApplyClick = {}
    )
}
