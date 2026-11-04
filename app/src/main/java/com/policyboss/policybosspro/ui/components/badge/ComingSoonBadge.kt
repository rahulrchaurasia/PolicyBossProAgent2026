package com.policyboss.policybosspro.ui.components.badge

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun ComingSoonBadge(
    modifier: Modifier = Modifier
) {

    Surface(

        modifier = modifier,

        color = Color.White,

        border = BorderStroke(
            1.dp,
            AppColors.Dividers
        ),

        shape = MaterialTheme.shapes.extraLarge

    ) {

        Text(

            text = "Coming soon",

            style = MaterialTheme.typography.labelMedium,

            fontWeight = FontWeight.Medium,

            color = AppColors.TextSecondary,

            modifier = Modifier.padding(

                horizontal = 12.dp,

                vertical = 4.dp
            )
        )
    }
}