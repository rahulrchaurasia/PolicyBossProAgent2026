package com.policyboss.policybosspro.feature.login.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun AuthHeaderPattern(
    modifier: Modifier = Modifier,
    heightFraction: Float = 0.42f
) {

//    AppColors.AuthDarkNavy,
//    AppColors.Slate950
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(heightFraction)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AppColors.BluePrimary,
                        AppColors.BrandGreenLight
                    )
                )
            )
    ) {

        Image(
            painter = painterResource(id = R.drawable.bg_pattern),

            contentDescription = null,

            modifier = Modifier.fillMaxSize(),

            contentScale = ContentScale.FillWidth,

            alignment = Alignment.TopStart,

            alpha = 0.45f
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun AuthHeaderPatternPreview() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.AuthDarkNavy)
    ) {

        AuthHeaderPattern()
    }
}