package com.policyboss.policybosspro.ui.components.button

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.PolicyBossTheme

/**
 * Standardized Back Button Component for PolicyBoss Pro.
 * Adheres strictly to design system guidelines across all screens:
 * - Back icon resource: R.drawable.ic_chevron_left
 * - Minimum touch target: 48dp x 48dp
 * - Centered icon size: 24dp x 24dp
 * - Screen-specific tinting (defaults to AppColors.TextPrimary for light backgrounds)
 */
@Composable
fun AppBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = AppColors.TextPrimary,
    contentDescription: String = "Back"
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_left),
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            tint = tint
        )
    }
}

@Preview(name = "AppBackButton - Light Background", showBackground = true)
@Composable
private fun AppBackButtonLightPreview() {
    PolicyBossTheme {
        AppBackButton(
            onClick = {},
            tint = AppColors.TextPrimary
        )
    }
}

@Preview(name = "AppBackButton - Dark/Gradient Background", showBackground = true, backgroundColor = 0xFF0D47A1)
@Composable
private fun AppBackButtonDarkPreview() {
    PolicyBossTheme {
        AppBackButton(
            onClick = {},
            tint = AppColors.White
        )
    }
}

