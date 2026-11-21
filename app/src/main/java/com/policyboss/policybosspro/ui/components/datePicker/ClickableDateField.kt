package com.policyboss.policybosspro.ui.components.datePicker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.ui.theme.AppColors

/**
 * Standardized Reusable Clickable Date Field (52.dp height, 12.dp rounded corners).
 * Displays a label, formatted date string (or "DD/MM/YYYY" placeholder), and the
 * standardized calendar icon (R.drawable.ic_calendar).
 *
 * Uses a transparent overlay covering the entire field container (left text area,
 * center empty space, and trailing calendar icon) to ensure a 100% reliable, full-field
 * tap target that opens the date picker without software keyboard flicker or double triggers.
 */
@Composable
fun ClickableDateField(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    placeholder: String = "DD/MM/YYYY",
    contentDescription: String = "Select date",
    iconRes: Int = R.drawable.ic_calendar,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (label.isNotBlank()) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary
            )
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                enabled = enabled,
                placeholder = {
                    Text(
                        text = placeholder,
                        color = AppColors.TextPlaceholder,
                        fontSize = 14.sp
                    )
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = contentDescription,
                        tint = if (enabled) AppColors.TextSecondary else Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                },
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = if (enabled) AppColors.TextPrimary else Color(0xFF64748B)
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isError) AppColors.ErrorRed else AppColors.BorderSecondary,
                    unfocusedBorderColor = if (isError) AppColors.ErrorRed else AppColors.BorderSecondary,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledBorderColor = Color(0xFFE2E8F0),
                    disabledContainerColor = Color(0xFFF1F5F9),
                    disabledTextColor = Color(0xFF64748B),
                    disabledPlaceholderColor = AppColors.TextPlaceholder,
                    focusedTextColor = AppColors.TextPrimary,
                    unfocusedTextColor = AppColors.TextPrimary
                ),
                singleLine = true,
                isError = isError,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )

            // Transparent overlay intercepts click reliably across the entire container
            // without opening the software keyboard or triggering duplicate events
            if (enabled) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onClick() }
                )
            }
        }

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = AppColors.ErrorRed,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ClickableDateFieldPreview() {
    ClickableDateField(
        label = "Registration date",
        value = "01-Feb-2021",
        onClick = {}
    )
}
