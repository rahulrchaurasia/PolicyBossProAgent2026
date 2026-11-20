
package com.policyboss.policybosspro.ui.components.radio

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.PolicyBossTheme
import com.policyboss.policybosspro.ui.theme.textPrimary

/**
 * Modern, customized Radio Button for PolicyBoss Pro.
 *
 * Visual behavior:
 * - Unselected: Clean circle with a light border ([unselectedBorderColor]) and transparent center.
 * - Selected: Smoothly filled with [selectedColor] (`AppColors.Primary`) framed by a crisp
 *   tonal dark border ([selectedBorderColor] = `AppColors.PrimaryDark`) with a spring scale transition.
 */
@Composable
fun AppRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = 20.dp,
    selectedColor: Color = AppColors.Primary,
    unselectedBorderColor: Color = AppColors.BorderPrimary,
    selectedBorderColor: Color = AppColors.PrimaryDark,
    borderWidth: Dp = 1.75.dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "fillScale"
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = when {
            !enabled -> unselectedBorderColor.copy(alpha = 0.4f)
            selected -> selectedBorderColor
            else -> unselectedBorderColor
        },
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "borderColor"
    )

    val animatedFillColor by animateColorAsState(
        targetValue = when {
            !enabled -> selectedColor.copy(alpha = 0.4f)
            selected -> selectedColor
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "fillColor"
    )

    val clickableModifier = if (onClick != null) {
        Modifier.selectable(
            selected = selected,
            enabled = enabled,
            role = Role.RadioButton,
            interactionSource = interactionSource,
            indication = ripple(bounded = false, radius = size),
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(clickableModifier)
            .size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = borderWidth.toPx()
            val outerRadius = size.toPx() / 2f
            val borderCenterRadius = outerRadius - (strokePx / 2f)

            // 1. Draw the filled circle inset inside the border stroke so spring bounce never bleeds outside
            if (scale > 0f) {
                val maxFillRadius = (outerRadius - strokePx * 0.5f).coerceAtLeast(0f)
                drawCircle(
                    color = animatedFillColor,
                    radius = (maxFillRadius * scale).coerceAtMost(borderCenterRadius),
                    style = Fill
                )
            }

            // 2. Draw the crisp outer tonal border (PrimaryDark when selected)
            drawCircle(
                color = animatedBorderColor,
                radius = borderCenterRadius,
                style = Stroke(width = strokePx)
            )
        }
    }
}

/**
 * Reusable composite component pairing [AppRadioButton] with a text label.
 * The entire row is accessible and clickable with zero dead zones.
 */
@Composable
fun AppRadioButtonOption(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = 20.dp,
    selectedColor: Color = AppColors.Primary,
    unselectedBorderColor: Color = AppColors.BorderPrimary,
    selectedBorderColor: Color = AppColors.PrimaryDark,
    borderWidth: Dp = 1.75.dp,
    labelStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    labelColor: Color = MaterialTheme.colorScheme.textPrimary,
    spacing: Dp = 8.dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    AppRadioButtonOption(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        size = size,
        selectedColor = selectedColor,
        unselectedBorderColor = unselectedBorderColor,
        selectedBorderColor = selectedBorderColor,
        borderWidth = borderWidth,
        spacing = spacing,
        interactionSource = interactionSource
    ) {
        Text(
            text = label,
            style = labelStyle,
            color = if (enabled) labelColor else labelColor.copy(alpha = 0.5f)
        )
    }
}

/**
 * Overload of [AppRadioButtonOption] for custom composable label content.
 */
@Composable
fun AppRadioButtonOption(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = 20.dp,
    selectedColor: Color = AppColors.Primary,
    unselectedBorderColor: Color = AppColors.BorderPrimary,
    selectedBorderColor: Color = AppColors.PrimaryDark,
    borderWidth: Dp = 1.75.dp,
    spacing: Dp = 8.dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.RadioButton,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppRadioButton(
            selected = selected,
            onClick = null,
            enabled = enabled,
            size = size,
            selectedColor = selectedColor,
            unselectedBorderColor = unselectedBorderColor,
            selectedBorderColor = selectedBorderColor,
            borderWidth = borderWidth,
            interactionSource = interactionSource
        )

        Spacer(modifier = Modifier.width(spacing))

        content()
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "AppRadioButton - Tonal Border States", showBackground = true)
@Composable
private fun AppRadioButtonPreview() {
    PolicyBossTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppRadioButtonOption(
                    selected = true,
                    label = "Selected (Primary + PrimaryDark Border)",
                    onClick = {}
                )

                AppRadioButtonOption(
                    selected = false,
                    label = "Unselected",
                    onClick = {}
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppRadioButtonOption(
                    selected = true,
                    label = "Disabled Selected",
                    enabled = false,
                    onClick = {}
                )

                AppRadioButtonOption(
                    selected = false,
                    label = "Disabled Unselected",
                    enabled = false,
                    onClick = {}
                )
            }
        }
    }
}