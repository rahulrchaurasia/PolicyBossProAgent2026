package com.policyboss.policybosspro.feature.lead.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.lead.model.FilterCriteria
import com.policyboss.policybosspro.feature.lead.model.LeadAction
import com.policyboss.policybosspro.ui.theme.AppColors
import kotlin.math.roundToInt

@Composable
fun LeadSearchHeader(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit,
    modifier: Modifier = Modifier,
    autoFocus: Boolean = true
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    if (autoFocus) {
        LaunchedEffect(Unit) {
            runCatching { focusRequester.requestFocus() }
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = AppColors.Surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    keyboardController?.hide()
                    onCloseSearch()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = AppColors.TextPrimary
                )
            }

            TextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        text = "Search by lead name, contact name",
                        fontSize = 14.sp,
                        color = AppColors.TextSecondary
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = AppColors.TextPrimary,
                    unfocusedTextColor = AppColors.TextPrimary,
                    cursorColor = AppColors.PrimaryBlue
                )
            )

            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search",
                        tint = AppColors.TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveFilterChipsRow(
    filterCriteria: FilterCriteria,
    onAction: (LeadAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeChips = buildList {
        filterCriteria.stages.forEach { add("STAGE" to it) }
        filterCriteria.vehicleTypes.forEach { add("VEHICLE" to it) }
        filterCriteria.renewalTimes.forEach { add("RENEWAL" to it) }
        filterCriteria.relationships.forEach { add("RELATION" to it) }
        if (filterCriteria.hasCustomEarningsFilter) {
            val min = filterCriteria.earningsRange.start.roundToInt()
            val max = filterCriteria.earningsRange.endInclusive.roundToInt()
            add("EARNINGS" to "₹$min - ₹$max")
        }
    }

    if (activeChips.isNotEmpty()) {
        LazyRow(
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = activeChips,
                key = { (type, label) -> "$type:$label" }
            ) { (type, label) ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AppColors.BlueTintBackground,
                    border = BorderStroke(1.dp, AppColors.PrimaryBlue.copy(alpha = 0.4f)),
                    modifier = Modifier.clickable {
                        onAction(LeadAction.RemoveFilterChip(type, label))
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            color = AppColors.PrimaryBlue,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove $label filter",
                            tint = AppColors.PrimaryBlue,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LeadSearchHeaderAndChipsPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LeadSearchHeader(
            searchQuery = "Baleno",
            onQueryChange = {},
            onCloseSearch = {},
            autoFocus = false
        )
        ActiveFilterChipsRow(
            filterCriteria = FilterCriteria(
                renewalTimes = setOf("7 days"),
                vehicleTypes = setOf("Car")
            ),
            onAction = {}
        )
    }
}

