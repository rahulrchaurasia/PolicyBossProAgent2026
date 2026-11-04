package com.policyboss.policybosspro.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val LightColorScheme = lightColorScheme(
    primary = AppColors.PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = AppColors.BlueTintBackground,
    onPrimaryContainer = AppColors.PrimaryDark,

    secondary = AppColors.BrandBlue,
    onSecondary = Color.White,
    secondaryContainer = AppColors.FilterPanelBackground,
    onSecondaryContainer = AppColors.PrimaryDark,

    tertiary = AppColors.BrandSuccess,
    onTertiary = Color.White,
    tertiaryContainer = AppColors.ChipGreenLight,
    onTertiaryContainer = AppColors.ChipGreenDark,

    background = AppColors.Background,
    onBackground = AppColors.TextPrimary,

    surface = AppColors.Surface,
    onSurface = AppColors.TextPrimary,

    surfaceVariant = AppColors.Slate50,
    onSurfaceVariant = AppColors.Slate600,

    outline = AppColors.BorderSecondary,
    outlineVariant = AppColors.BorderLight,

    error = AppColors.ErrorRed,
    onError = Color.White,
    errorContainer = AppColors.AlertCardBackground,
    onErrorContainer = AppColors.AlertRedDark
)