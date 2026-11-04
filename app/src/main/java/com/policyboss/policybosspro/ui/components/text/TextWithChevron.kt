package com.policyboss.policybosspro.ui.components.text

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.labelMediumSemiBold

/**
 * Standardized reusable composable for text action links with directional chevrons
 * (e.g., "View all", "View Details", "Explore more").
 */
@Composable
fun TextWithChevron(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int = R.drawable.ic_chevron_right,
    textStyle: TextStyle = MaterialTheme.typography.labelMediumSemiBold,
    textColor: Color = AppColors.TextPrimary,
    iconTint: Color = textColor,
    iconSize: Dp = 16.dp,
    spacing: Dp = 4.dp,
    contentDescription: String? = null
) {
    Row(
        modifier = modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        Text(
            text = text,
            style = textStyle,
            color = textColor
        )
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TextWithChevronPreview() {
    TextWithChevron(
        text = "View all",
        onClick = {}
    )
}

