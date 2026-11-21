package com.policyboss.policybosspro.ui.components.dropDown

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.ui.theme.AppColors

data class DropdownOption<T>(
    val value: T,
    val label: String,
    val iconRes: Int? = null
)

/**
 * Standardized Material 3 Dropdown for PolicyBoss Pro.
 *
 * Enforces uniform 52.dp height, 12.dp rounded corners, subtle borders,
 * leading icon support, and rotating chevron indicator.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GenericM3Dropdown(
    label: String,
    items: List<DropdownOption<T>>,
    selectedItem: DropdownOption<T>?,
    onItemSelected: (DropdownOption<T>) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Select",
    enabled: Boolean = true,
    height: Dp = 52.dp,
    shape: Shape = RoundedCornerShape(12.dp),
    containerColor: Color = Color.White
) {
    var expanded by remember { mutableStateOf(false) }
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "chevronRotation"
    )

    Column(modifier = modifier) {
        if (label.isNotBlank()) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        ExposedDropdownMenuBox(
            expanded = expanded && enabled,
            onExpandedChange = { if (enabled) expanded = it }
        ) {
            OutlinedTextField(
                value = selectedItem?.label.orEmpty(),
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
                leadingIcon = selectedItem?.iconRes?.let { iconRes ->
                    {
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chevron_down),
                        contentDescription = "Dropdown icon",
                        tint = if (enabled) AppColors.TextSecondary else Color(0xFF94A3B8),
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(chevronRotation)
                    )
                },
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = if (enabled) AppColors.TextPrimary else Color(0xFF64748B)
                ),
                shape = shape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = containerColor,
                    unfocusedContainerColor = containerColor,
                    disabledContainerColor = Color(0xFFF1F5F9),
                    focusedBorderColor = AppColors.PrimaryBlue,
                    unfocusedBorderColor = AppColors.BorderSecondary,
                    disabledBorderColor = Color(0xFFE2E8F0),
                    focusedTextColor = AppColors.TextPrimary,
                    unfocusedTextColor = AppColors.TextPrimary,
                    disabledTextColor = Color(0xFF64748B)
                ),
                singleLine = true,
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
                    .height(height)
            )

            ExposedDropdownMenu(
                expanded = expanded && enabled,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                items.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option.label,
                                fontSize = 14.sp,
                                color = AppColors.TextPrimary
                            )
                        },
                        leadingIcon = option.iconRes?.let {
                            {
                                Image(
                                    painter = painterResource(id = it),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                )
                            }
                        },
                        onClick = {
                            onItemSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun GenericM3DropdownPreview() {
    val sampleOptions = listOf(
        DropdownOption("acko", "Acko General Insurance"),
        DropdownOption("hdfc", "HDFC Ergo"),
        DropdownOption("icici", "ICICI Lombard")
    )

    var selectedOption by remember { mutableStateOf<DropdownOption<String>?>(sampleOptions.first()) }

    GenericM3Dropdown(
        label = "Previous insurer",
        items = sampleOptions,
        selectedItem = selectedOption,
        onItemSelected = { selectedOption = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}
