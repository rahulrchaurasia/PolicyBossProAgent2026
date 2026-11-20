package com.policyboss.policybosspro.ui.components.textfield

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.policyboss.policybosspro.feature.home.util.VehicleRegistrationFormatter
import com.policyboss.policybosspro.feature.home.util.VehicleRegistrationValidator

/**
 * VisualTransformation that renders hyphenated Indian vehicle registration numbers
 * in real time without mutating the clean underlying alphanumeric state.
 *
 * Prevents cursor jumps and backspace loops by computing exact bidirectional [OffsetMapping].
 */
class VehicleRegistrationVisualTransformation(
    private val allowBhSeries: Boolean = true,
    private val isSeriesClosed: Boolean = false
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val result = VehicleRegistrationFormatter.format(
            raw = text.text,
            allowBhSeries = allowBhSeries,
            isSeriesClosed = isSeriesClosed
        )

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return result.originalToTransformed(offset)
            }

            override fun transformedToOriginal(offset: Int): Int {
                return result.transformedToOriginal(offset)
            }
        }

        return TransformedText(
            text = AnnotatedString(result.formatted),
            offsetMapping = offsetMapping
        )
    }
}

/**
 * Reusable Vehicle Registration Number input field with dynamic software keyboard orchestration
 * and live auto-hyphen formatting.
 *
 * Supports:
 * - Standard Indian RTO Format: `SS-RR-SERIES-NNNN` (e.g. `RJ-14-CV-0002`, `MH-02-AB-1234`, `DL-01-S-5678`)
 * - Bharat (BH) Series Format: `YY-BH-NNNN-XX` (e.g. `22-BH-1234-AA`) when [allowBhSeries] is true
 *
 * Dynamically switches `KeyboardType` and `KeyboardCapitalization` based on current string length,
 * active registration segment, and explicit series completion.
 */
@Composable
fun VehicleRegistrationTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Enter vehicle number",
    isError: Boolean = false,
    errorMessage: String? = null,
    allowBhSeries: Boolean = true,
    shape: Shape = RoundedCornerShape(12.dp),
    imeAction: ImeAction? = null,
    onDone: (() -> Unit)? = null
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val view = LocalView.current

    val clean = VehicleRegistrationFormatter.clean(value)
    val seriesLength = VehicleRegistrationValidator.getSeriesLength(clean, allowBhSeries)
    val isNumberBlockStarted = VehicleRegistrationValidator.isNumberBlockStarted(clean, allowBhSeries)

    var isSeriesExplicitlyClosed by remember { mutableStateOf(false) }

    // Reset explicit closure if clean text has no series letters
    if (seriesLength == 0 && isSeriesExplicitlyClosed) {
        isSeriesExplicitlyClosed = false
    }

    val keyboardType = VehicleRegistrationValidator.resolveKeyboardType(
        clean = clean,
        allowBhSeries = allowBhSeries,
        isSeriesClosed = isSeriesExplicitlyClosed
    )
    val capitalization = VehicleRegistrationValidator.resolveCapitalization(keyboardType)

    val resolvedImeAction = imeAction ?: if (isNumberBlockStarted) {
        ImeAction.Done
    } else {
        ImeAction.Next
    }

    // IME Refresh Fix: forces the keyboard to update input type (Ascii <-> Number)
    // without closing, reopening, or dropping focus
    LaunchedEffect(keyboardType) {
        keyboardController?.show()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.restartInput(view)
    }

    val visualTransformation = remember(allowBhSeries, isSeriesExplicitlyClosed) {
        VehicleRegistrationVisualTransformation(
            allowBhSeries = allowBhSeries,
            isSeriesClosed = isSeriesExplicitlyClosed
        )
    }

    AppOutlinedTextField(
        value = value,
        onValueChange = { input ->
            if (input.endsWith("-") && seriesLength in 1..2 && !isNumberBlockStarted) {
                isSeriesExplicitlyClosed = true
            }
            val filtered = VehicleRegistrationValidator.filterVehicleRegistration(input, allowBhSeries)
            onValueChange(filtered)
        },
        modifier = modifier.fillMaxWidth(),
        placeholder = placeholder,
        isError = isError,
        errorMessage = errorMessage,
        shape = shape,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            capitalization = capitalization,
            imeAction = resolvedImeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                if (seriesLength in 1..2 && !isNumberBlockStarted) {
                    isSeriesExplicitlyClosed = true
                    keyboardController?.show()
                } else {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            },
            onDone = {
                keyboardController?.hide()
                focusManager.clearFocus()
                onDone?.invoke()
            }
        ),
        singleLine = true
    )
}

@Preview(showBackground = true, name = "Car Registration Field - Empty")
@Composable
private fun VehicleRegistrationTextFieldCarEmptyPreview() {
    VehicleRegistrationTextField(
        value = "",
        onValueChange = {},
        placeholder = "Enter car number",
        allowBhSeries = true
    )
}

@Preview(showBackground = true, name = "Car Registration Field - Formatted (RJ-14-CV-0002)")
@Composable
private fun VehicleRegistrationTextFieldCarFilledPreview() {
    VehicleRegistrationTextField(
        value = "RJ14CV0002",
        onValueChange = {},
        placeholder = "Enter car number",
        allowBhSeries = true
    )
}

@Preview(showBackground = true, name = "Two Wheeler Field - Formatted (MH-02-AB-1234)")
@Composable
private fun VehicleRegistrationTextFieldTwPreview() {
    VehicleRegistrationTextField(
        value = "MH02AB1234",
        onValueChange = {},
        placeholder = "Enter two wheeler number",
        allowBhSeries = true
    )
}

@Preview(showBackground = true, name = "BH Series Field - Formatted (22-BH-1234-AA)")
@Composable
private fun VehicleRegistrationTextFieldBhPreview() {
    VehicleRegistrationTextField(
        value = "22BH1234AA",
        onValueChange = {},
        placeholder = "Enter car number",
        allowBhSeries = true
    )
}

@Preview(showBackground = true, name = "Commercial Vehicle Field - Error State")
@Composable
private fun VehicleRegistrationTextFieldCvErrorPreview() {
    VehicleRegistrationTextField(
        value = "RJ",
        onValueChange = {},
        placeholder = "Enter commercial vehicle number",
        allowBhSeries = false,
        isError = true,
        errorMessage = "Please enter a valid commercial vehicle number"
    )
}
