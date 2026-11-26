package com.policyboss.policybosspro.ui.components.checkbox

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
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.PolicyBossTheme
import com.policyboss.policybosspro.ui.theme.textPrimary

/**
 * Custom checkbox for PolicyBoss Pro, the square sibling of `AppRadioButton`.
 *
 * - Unchecked: rounded square with a light border ([uncheckedBorderColor]).
 * - Checked: filled with [checkedColor] (`AppColors.Primary`), framed by [checkedBorderColor]
 *   (`AppColors.PrimaryDark`), with a white check mark that springs in.
 */
@Composable
fun AppCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = 20.dp,
    cornerRadius: Dp = 5.dp,
    checkedColor: Color = AppColors.Primary,
    uncheckedBorderColor: Color = AppColors.BorderPrimary,
    checkedBorderColor: Color = AppColors.PrimaryDark,
    checkMarkColor: Color = Color.White,
    borderWidth: Dp = 1.75.dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val checkScale by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "checkScale"
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            !enabled -> uncheckedBorderColor.copy(alpha = 0.4f)
            checked -> checkedBorderColor
            else -> uncheckedBorderColor
        },
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "borderColor"
    )
    val fillColor by animateColorAsState(
        targetValue = when {
            !checked -> Color.Transparent
            !enabled -> checkedColor.copy(alpha = 0.4f)
            else -> checkedColor
        },
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "fillColor"
    )

    val toggleModifier = if (onCheckedChange != null) {
        Modifier.toggleable(
            value = checked,
            enabled = enabled,
            role = Role.Checkbox,
            interactionSource = interactionSource,
            indication = ripple(bounded = false, radius = size),
            onValueChange = onCheckedChange
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(toggleModifier)
            .size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = borderWidth.toPx()
            val radius = CornerRadius(cornerRadius.toPx())
            val inset = strokePx / 2f
            val boxSize = Size(this.size.width - strokePx, this.size.height - strokePx)

            drawRoundRect(color = fillColor, topLeft = Offset(inset, inset), size = boxSize, cornerRadius = radius)
            drawRoundRect(
                color = borderColor,
                topLeft = Offset(inset, inset),
                size = boxSize,
                cornerRadius = radius,
                style = Stroke(width = strokePx)
            )

            if (checkScale > 0f) {
                val w = this.size.width
                val h = this.size.height
                val checkPath = Path().apply {
                    moveTo(w * 0.26f, h * 0.52f)
                    lineTo(w * 0.43f, h * 0.68f)
                    lineTo(w * 0.75f, h * 0.34f)
                }
                scale(scale = checkScale) {
                    drawPath(
                        path = checkPath,
                        color = checkMarkColor,
                        style = Stroke(width = strokePx * 1.15f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
            }
        }
    }
}

/**
 * [AppCheckbox] with a text label; the whole row toggles, with no dead zones.
 */
@Composable
fun AppCheckboxOption(
    checked: Boolean,
    label: String,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = 20.dp,
    labelStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    labelColor: Color = MaterialTheme.colorScheme.textPrimary,
    spacing: Dp = 10.dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    AppCheckboxOption(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        size = size,
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

/** Overload of [AppCheckboxOption] for custom label content. */
@Composable
fun AppCheckboxOption(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = 20.dp,
    spacing: Dp = 10.dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier.toggleable(
            value = checked,
            enabled = enabled,
            role = Role.Checkbox,
            interactionSource = interactionSource,
            indication = null,
            onValueChange = onCheckedChange
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppCheckbox(
            checked = checked,
            onCheckedChange = null,
            enabled = enabled,
            size = size,
            interactionSource = interactionSource
        )
        Spacer(modifier = Modifier.width(spacing))
        content()
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "AppCheckbox - States", showBackground = true)
@Composable
private fun AppCheckboxPreview() {
    PolicyBossTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppCheckboxOption(checked = true, label = "Checked", onCheckedChange = {})
            AppCheckboxOption(checked = false, label = "Unchecked", onCheckedChange = {})
            AppCheckboxOption(checked = true, label = "Disabled checked", enabled = false, onCheckedChange = {})
            AppCheckboxOption(checked = false, label = "Disabled unchecked", enabled = false, onCheckedChange = {})
        }
    }
}
