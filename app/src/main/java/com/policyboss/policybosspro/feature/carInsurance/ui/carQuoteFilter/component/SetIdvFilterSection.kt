package com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.carInsurance.model.MotorQuote
import com.policyboss.policybosspro.ui.theme.AppColors
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetIdvFilterSection(
    selectedIdv: Float,
    minIdv: Float,
    maxIdv: Float,
    onIdvChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val safeMin = minOf(minIdv, maxIdv)
    val safeMax = maxOf(minIdv, maxIdv)
    val safeRange = safeMin..maxOf(safeMin + 1f, safeMax)

    var isFocused by remember { mutableStateOf(false) }
    var rawText by remember { mutableStateOf(MotorQuote.formatIndianCurrency(selectedIdv.roundToInt())) }

    // Sync raw text when selectedIdv changes externally while not typing
    LaunchedEffect(selectedIdv, isFocused) {
        if (!isFocused) {
            rawText = MotorQuote.formatIndianCurrency(selectedIdv.roundToInt())
        }
    }

    val currentParsedIdv = rawText.filter { it.isDigit() }.toFloatOrNull()
    val isOutOfRange = isFocused && currentParsedIdv != null && (currentParsedIdv < safeMin || currentParsedIdv > safeMax)

    val warningText = if (isOutOfRange) {
        if (currentParsedIdv < safeMin) {
            "Minimum IDV is ₹${MotorQuote.formatIndianCurrency(safeMin.roundToInt())}"
        } else {
            "Maximum IDV is ₹${MotorQuote.formatIndianCurrency(safeMax.roundToInt())}"
        }
    } else null

    // Determine slider display position: follows typed text smoothly (coerced into range) while typing
    val sliderDisplayValue = if (isFocused && currentParsedIdv != null) {
        currentParsedIdv.coerceIn(safeRange)
    } else {
        selectedIdv.coerceIn(safeRange)
    }

    val clampAndCommit = {
        val digits = rawText.filter { it.isDigit() }
        val parsed = digits.toFloatOrNull() ?: safeMin
        val clamped = parsed.coerceIn(safeRange)
        onIdvChange(clamped)
        rawText = MotorQuote.formatIndianCurrency(clamped.roundToInt())
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        // ==========================================
        // Row 1: Single-Line Title
        // ==========================================
        Text(
            text = "Set Your Own IDV",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // ==========================================
        // Row 2: Full-Width Editable Rupee Field
        // ==========================================
        val borderColor = when {
            isOutOfRange -> Color(0xFFDC2626)
            isFocused -> AppColors.PrimaryBlue
            else -> AppColors.BorderSecondary
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .border(1.2.dp, borderColor, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "₹",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.TextSecondary
            )

            Spacer(modifier = Modifier.width(6.dp))

            BasicTextField(
                value = rawText,
                onValueChange = { input ->
                    val digits = input.filter { it.isDigit() }.take(8)
                    rawText = digits
                    val num = digits.toFloatOrNull()
                    if (num != null && num in safeRange) {
                        onIdvChange(num)
                    }
                },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        clampAndCommit()
                    }
                ),
                singleLine = true,
                cursorBrush = SolidColor(AppColors.PrimaryBlue),
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            isFocused = true
                            rawText = rawText.filter { it.isDigit() }
                        } else if (isFocused) {
                            isFocused = false
                            clampAndCommit()
                        }
                    }
            )
        }

        // Subtle inline warning caption if typed value is out of range
        if (warningText != null) {
            Text(
                text = warningText,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFDC2626),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ==========================================
        // Custom Material 3 IDV Slider
        // ==========================================
        Slider(
            value = sliderDisplayValue,
            onValueChange = { newValue ->
                if (isFocused) {
                    isFocused = false
                    focusManager.clearFocus()
                }
                onIdvChange(newValue)
                rawText = MotorQuote.formatIndianCurrency(newValue.roundToInt())
            },
            valueRange = safeRange,
            modifier = Modifier.fillMaxWidth(),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .shadow(elevation = 3.dp, shape = CircleShape)
                        .background(Color.White, shape = CircleShape)
                        .padding(2.5.dp)
                        .background(AppColors.PrimaryBlue, shape = CircleShape)
                )
            },
            track = { sliderState ->
                SliderDefaults.Track(
                    sliderState = sliderState,
                    modifier = Modifier.height(7.dp),
                    colors = SliderDefaults.colors(
                        activeTrackColor = AppColors.PrimaryBlue,
                        inactiveTrackColor = Color(0xFFE2E8F0)
                    ),
                    drawStopIndicator = null,
                    thumbTrackGapSize = 0.dp
                )
            }
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Min & Max Bound Labels Directly Below Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "₹${MotorQuote.formatIndianCurrency(safeMin.roundToInt())}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.TextSecondary
            )

            Text(
                text = "₹${MotorQuote.formatIndianCurrency(safeMax.roundToInt())}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.TextSecondary
            )
        }
    }
}

@Preview(name = "Set IDV - Default Min", showBackground = true, widthDp = 260)
@Composable
private fun SetIdvFilterSectionDefaultPreview() {
    SetIdvFilterSection(
        selectedIdv = 71414f,
        minIdv = 71414f,
        maxIdv = 234780f,
        onIdvChange = {}
    )
}

@Preview(name = "Set IDV - Mid Range", showBackground = true, widthDp = 260)
@Composable
private fun SetIdvFilterSectionMidPreview() {
    SetIdvFilterSection(
        selectedIdv = 153097f,
        minIdv = 71414f,
        maxIdv = 234780f,
        onIdvChange = {}
    )
}
