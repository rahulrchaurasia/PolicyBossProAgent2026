package com.policyboss.policybosspro.ui.components.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import coil3.compose.AsyncImage
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun AppImage(
    url: String,
    modifier: Modifier = Modifier
) {
    if (LocalInspectionMode.current) {
        // 👇 Preview mode → show placeholder
        Box(
            modifier = modifier
                .background(AppColors.ShimmerBase)
        )
    } else {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }
}