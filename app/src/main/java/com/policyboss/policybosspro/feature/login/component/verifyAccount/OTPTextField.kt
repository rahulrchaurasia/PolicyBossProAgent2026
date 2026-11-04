package com.policyboss.policybosspro.feature.login.component.verifyAccount



import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn

import androidx.compose.foundation.text.BasicTextField

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.AppTypography
import kotlinx.coroutines.delay






import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.ui.draw.alpha

import androidx.compose.ui.platform.LocalInspectionMode

import com.policyboss.policybosspro.ui.theme.LightColorScheme
import kotlin.time.Duration.Companion.milliseconds


enum class OTPFieldState {
    DEFAULT,
    SUCCESS,
    ERROR
}


@Composable
fun OTPTextField(
    otpText: String,
    otpLength: Int = 4,

    // from ViewModel
    fieldState: OTPFieldState,

    // loading while API verifying
    isLoading: Boolean,

    // callback
    onOtpTextChange: (String, Boolean) -> Unit,
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val isInPreview = LocalInspectionMode.current
    val focusRequester = remember {
        FocusRequester()
    }

    var isTextFieldFocused by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        if (!isInPreview) {

            delay(250.milliseconds)

            if (
                fieldState != OTPFieldState.SUCCESS
            ) {
                focusRequester.requestFocus()
            }
        }
    }

    val cursorPosition = otpText.length

    BasicTextField(

        value = TextFieldValue(
            text = otpText,
            selection = TextRange(cursorPosition)
        ),

        onValueChange = { value ->

            // prevent typing while loading/success
            if (
                isLoading ||
                fieldState == OTPFieldState.SUCCESS
            ) {
                return@BasicTextField
            }

            val filteredText = value.text
                .filter { it.isDigit() }
                .take(otpLength)

            onOtpTextChange(
                filteredText,
                filteredText.length == otpLength
            )

            // auto hide keyboard
            if (filteredText.length == otpLength) {
                keyboardController?.hide()
            }
        },

        enabled =
        !isLoading &&
                fieldState != OTPFieldState.SUCCESS,

        singleLine = true,

        cursorBrush = SolidColor(AppColors.Transparent),

        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        ),

        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),

        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged {
                isTextFieldFocused = it.isFocused
            }
            .semantics {
                contentDescription =
                    "OTP input field with $otpLength digits"
            },

        decorationBox = {

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                repeat(otpLength) { index ->

                    val value = otpText
                        .getOrNull(index)
                        ?.toString()
                        .orEmpty()

                    val isFocused =
                        fieldState == OTPFieldState.DEFAULT &&
                                isTextFieldFocused &&
                                cursorPosition == index

                    OTPDigitBox(
                        value = value,
                        isFocused = isFocused,
                        fieldState = fieldState,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    )
}

@Composable
private fun OTPDigitBox(
    value: String,
    isFocused: Boolean,
    fieldState: OTPFieldState,
    modifier: Modifier = Modifier
) {

    // =====================================================
    // BORDER COLOR
    // =====================================================

    val borderColor = when (fieldState) {

        OTPFieldState.SUCCESS -> {
            AppColors.SuccessGreen
        }

        OTPFieldState.ERROR -> {
            AppColors.ErrorRed
        }

        OTPFieldState.DEFAULT -> {

            if (isFocused) {
                AppColors.PrimaryBlue
            } else {
                AppColors.BorderPrimary
            }
        }
    }

    // =====================================================
    // BACKGROUND COLOR
    // =====================================================

    val backgroundColor = when (fieldState) {

        OTPFieldState.SUCCESS -> {
            AppColors.SuccessGreen.copy(alpha = 0.08f)
        }

        OTPFieldState.ERROR -> {
            AppColors.ErrorRed.copy(alpha = 0.08f)
        }

        OTPFieldState.DEFAULT -> {

            if (isFocused) {
                AppColors.PrimaryBlue.copy(alpha = 0.06f)
            } else {
                AppColors.Gray50
            }
        }
    }

    Box(
        modifier = modifier
            .widthIn(max = 72.dp)
            .aspectRatio(1f)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = when {

                    fieldState != OTPFieldState.DEFAULT -> {
                        1.5.dp
                    }

                    isFocused -> {
                        1.5.dp
                    }

                    else -> {
                        1.dp
                    }
                },

                color = borderColor,

                shape = RoundedCornerShape(16.dp)
            ),

        contentAlignment = Alignment.Center
    ) {

        when {

            value.isNotEmpty() -> {

                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineLarge,
                    color = AppColors.TextPrimary
                )
            }

            isFocused -> {

                OTPCursor()
            }
        }
    }
}

@Composable
private fun OTPCursor() {

    val infiniteTransition =
        rememberInfiniteTransition(label = "cursor")

    val alpha by infiniteTransition.animateFloat(

        initialValue = 1f,
        targetValue = 0.2f,

        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),

        label = "cursor_alpha"
    )

    Box(
        modifier = Modifier
            .width(2.dp)
            .height(28.dp)
            .alpha(alpha)
            .background(AppColors.PrimaryBlue)
    )
}


@Preview(showBackground = true)
@Composable
private fun OTPFieldPreview() {

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography
    ) {

        OTPTextField(

            otpText = "12",

            fieldState = OTPFieldState.DEFAULT,

            isLoading = false,

            onOtpTextChange = { _, _ -> }
        )
    }
}
