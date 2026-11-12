package com.policyboss.policybosspro.feature.profile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun ProfileAvatar(
    initials: String,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .border(3.dp, Color.White, CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        AppColors.ProfileAvatarGradientStart,
                        AppColors.ProfileAvatarGradientEnd
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials.ifBlank { "PB" },
            color = AppColors.White,
            fontSize = (size.value * 0.35f).sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileAvatarPreview() {
    ProfileAvatar(initials = "RC")
}

