package com.policyboss.policybosspro.ui.components.pageIndicator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun PagerIndicatorWelcome(
    totalPages: Int,
    currentPage: Int,
    activeColor: Color = AppColors.PrimaryBlue
) {
    val inactiveColor = activeColor.copy(alpha = 0.3f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalPages) { index ->
            val isSelected = index == currentPage

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(4.dp)
                    .width(if (isSelected) 24.dp else 8.dp)
                    .background(
                        color = if (isSelected) activeColor else inactiveColor,
                        shape = CircleShape
                    )
            )
        }
    }
}