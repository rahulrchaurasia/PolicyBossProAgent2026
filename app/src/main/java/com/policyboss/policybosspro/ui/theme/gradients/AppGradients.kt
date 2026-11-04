package com.policyboss.policybosspro.ui.theme.gradients

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.AppColors.PrimaryYellow

// A reusable factory for your gradient
object AppGradients {
    val BorderBrush = Brush.horizontalGradient(
        colors = listOf(PrimaryYellow, Color.Transparent)
    )

    // Fades from [Color] (on the left) to [Transparent] (on the right)
    fun fadeToRight(color: Color) = Brush.horizontalGradient(
        colors = listOf(color, Color.Transparent)
    )

    // Fades from [Transparent] (on the left) to [Color] (on the right)
    fun fadeToLeft(color: Color) = Brush.horizontalGradient(
        colors = listOf(Color.Transparent, color)
    )

    // Mark: its purpose if it is a shared UI pattern.
    //++++ Background Surface using gradient ++++++++++++++++
    val ScreenSurfaceGradient = Brush.verticalGradient(
        colorStops = arrayOf(
            0.05f to AppColors.SkyBlue,
            0.16f to AppColors.PaleCyan,
            0.24f to Color.White,
            1.00f to Color.White
        )
    )

    fun screenSurfaceGradient(endY: Float = Float.POSITIVE_INFINITY) = Brush.verticalGradient(
        colors = listOf(
            AppColors.SkyBlue,
            AppColors.PaleCyan,
            Color.White
        ),
        startY = 0f,
        endY = endY
    )

    // Soft pale-blue top header gradient for Home Screen
    val HomeHeaderGradient = Brush.verticalGradient(
        colors = listOf(
            AppColors.HomeHeaderGradientStart,
            AppColors.HomeGradientStart,
            AppColors.Background
        )
    )

    val PolicyCardGradient = Brush.linearGradient(
        colors = listOf(
            AppColors.BlueGradientLight,
            AppColors.BlueGradientDark
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    // Promo banner gradient: teal green → sky blue
    val PromoBannerGradient = Brush.linearGradient(
        colors = listOf(
            AppColors.BrandSuccess, // Start color
            AppColors.BlueGradientLight  // End color
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )


    // If you need them for other colors later, you can make a factory:
    fun horizontalFade(color: Color) = Brush.horizontalGradient(colors = listOf(Color.Transparent, color))
}


//Mark : ex
/*
//drawBehind
 Box(
       Modifier
            .weight(1f)
            .height(1.dp)
             .drawBehind { drawRect(AppGradients.fadeToLeft(AppColors.GoldText)) }
               )

 */