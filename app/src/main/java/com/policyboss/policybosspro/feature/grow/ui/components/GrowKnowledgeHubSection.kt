package com.policyboss.policybosspro.feature.grow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.grow.model.KnowledgeCategory
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun GrowKnowledgeHubSection(
    selectedCategory: KnowledgeCategory,
    onCategorySelect: (KnowledgeCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Knowledge Hub",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.Slate900
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Segmented selector pill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(AppColors.Slate100)
                .padding(4.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                KnowledgeCategory.entries.forEach { category ->
                    val isSelected = category == selectedCategory
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) AppColors.Surface else AppColors.Transparent)
                            .clickable { onCategorySelect(category) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category.title,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) AppColors.Slate900 else AppColors.Slate500
                        )
                    }
                }
            }
        }
    }
}
