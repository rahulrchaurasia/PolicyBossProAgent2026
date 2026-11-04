package com.policyboss.policybosspro.ui.components.toolbarHeader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun AuthCompositeHeaderIcon(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp // Making size a parameter makes the math easier in the parent screen
) {
    val lightBlue = AppColors.lightBlue

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(lightBlue),
        contentAlignment = Alignment.Center
    ) {
        // 1. Base Mail Icon (Centered)
        Icon(
            painter = painterResource(id = R.drawable.ic_email),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(52.dp)
        )

        // 2. Overlapping Secure Shield Icon
        Icon(
            painter = painterResource(id = R.drawable.ic_secure_key),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(36.dp)
                .align(Alignment.BottomEnd) // Anchor to bottom right
                .offset(x = (-14).dp, y = (-14).dp) // 🚀 Pull it inward to overlap the email
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun AuthCompositeHeaderIconPreview() {
    AuthCompositeHeaderIcon(
        modifier = Modifier.padding(16.dp)
    )
}