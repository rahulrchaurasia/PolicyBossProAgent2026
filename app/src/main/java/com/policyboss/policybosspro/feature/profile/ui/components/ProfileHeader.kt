package com.policyboss.policybosspro.feature.profile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.ui.components.button.AppBackButton
import com.policyboss.policybosspro.ui.theme.AppColors


import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize

import androidx.compose.ui.draw.clipToBounds


@Composable
fun ProfileHeader(
    fullName: String,
    initials: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val patternScale = 1.1f        // bigger = rings reach further left, and lines are spaced wider
    val centerX = 300.dp           // ring center X in the header (smaller = pattern moves left)
    val centerY = (-230).dp         // ring center Y (negative = above the header)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp)
    ) {
        // Blue Gradient Background with pattern offset to top-right
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clipToBounds() // Important: cuts off the part of the pattern outside the header
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AppColors.PrimaryDark,
                            AppColors.PrimaryBlue
                        )
                    )
                )
        ) {
//            Image(
//                painter = painterResource(id = R.drawable.bg_pattern),
//                contentDescription = null,
//                contentScale = ContentScale.FillBounds,
//                alpha = 0.35f,
//                modifier = Modifier
//                    // Let the 375x812 drawable be bigger than the header
//                    .wrapContentSize(align = Alignment.TopStart, unbounded = true)
//                    .size(width = 375.dp, height = 812.dp)
//                    // Ring center is at (196, 418) inside the drawable.
//                    // This moves it to roughly (x = 440dp, y = -40dp): top-right, outside the header
//                    .offset(x = 244.dp, y = (-458).dp)
//            )

            Image(
                painter = painterResource(id = R.drawable.bg_pattern),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                alpha = 0.5f,
                modifier = Modifier
                    .wrapContentSize(align = Alignment.TopStart, unbounded = true)
                    .size(width = 375.dp * patternScale, height = 812.dp * patternScale)
                    // ring center in the drawable is (196, 418) -> move it to (centerX, centerY)
                    .offset(
                        x = centerX - 196.dp * patternScale,
                        y = centerY - 418.dp * patternScale
                    )
            )

            // Back Button
            AppBackButton(
                onClick = onBackClick,
                tint = AppColors.White,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(start = 4.dp, top = 4.dp)
                    .align(Alignment.TopStart)
            )
        }

        // White curved sheet overlapping the bottom of the blue header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(AppColors.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = fullName.ifBlank { "User" },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
            }
        }

        // Circular Avatar overlapping the border between blue header and white container
        ProfileAvatar(
            initials = initials,
            size = 80.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-52).dp)
        )
    }
}



@Preview(showBackground = true)
@Composable
private fun ProfileHeaderPreview() {
    ProfileHeader(
        fullName = "Dinesh Das",
        initials = "DD",
        onBackClick = {}
    )
}

