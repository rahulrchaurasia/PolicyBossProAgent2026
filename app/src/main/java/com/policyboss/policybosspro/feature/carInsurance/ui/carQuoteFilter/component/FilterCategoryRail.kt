package com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.FilterCategory
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun FilterCategoryRail(
    selectedCategory: FilterCategory,
    onCategorySelected: (FilterCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(128.dp)
            .fillMaxHeight()
            .background(Color(0xFFF0F7FF))
    ) {
        FilterCategory.entries.forEach { category ->
            val isSelected = category == selectedCategory

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isSelected) Color.White else Color.Transparent)
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 14.dp, vertical = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = category.displayName,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isSelected) AppColors.TextPrimary else AppColors.TextSecondary,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Preview(name = "Filter Category Rail", showBackground = true, heightDp = 400)
@Composable
private fun FilterCategoryRailPreview() {
    FilterCategoryRail(
        selectedCategory = FilterCategory.SET_IDV,
        onCategorySelected = {}
    )
}
