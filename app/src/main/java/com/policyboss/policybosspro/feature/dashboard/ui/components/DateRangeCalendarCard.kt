package com.policyboss.policybosspro.feature.dashboard.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.policyboss.policybosspro.ui.components.datePicker.DateRangeCalendarCard as SharedDateRangeCalendarCard

/**
 * Backward-compatible delegating composable for Dashboard screen.
 */
@Composable
fun DateRangeCalendarCard(
    currentRangeString: String,
    onApply: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    SharedDateRangeCalendarCard(
        currentRangeString = currentRangeString,
        onApply = onApply,
        modifier = modifier
    )
}
