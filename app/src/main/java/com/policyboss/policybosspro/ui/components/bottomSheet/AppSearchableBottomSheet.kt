package com.policyboss.policybosspro.ui.components.bottomSheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.policyboss.policybosspro.ui.components.dropDown.DropdownOption
import com.policyboss.policybosspro.ui.theme.AppColors
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextOverflow
/**
 * Standardized Searchable Modal Bottom Sheet for PolicyBoss Pro.
 * Supports both searchable lists (RTO, Insurer, Model, Variant) and
 * non-searchable fixed lists (Fuel, NCB).
 *
 * Adheres strictly to the Figma bottom sheet specifications:
 * - Rounded top corners (20.dp)
 * - Header title with top-right dismiss icon (✕)
 * - Optional search bar with magnifying glass icon and clear button
 * - Divider lines between list items
 * - Optional leading logo/icon for insurers
 * - Trailing blue checkmark (✓) for currently selected item
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> AppSearchableBottomSheet(
    title: String,
    items: List<DropdownOption<T>>,
    onItemSelected: (DropdownOption<T>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    searchPlaceholder: String = "Search...",
    selectedItem: DropdownOption<T>? = null,
    showSearch: Boolean = true
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }

    val filteredItems = remember(items, searchQuery) {
        if (searchQuery.isBlank()) {
            items
        } else {
            val query = searchQuery.trim().lowercase()
            items.filter {
                it.label.lowercase().contains(query) || it.value.toString().lowercase().contains(query)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = Color.White,
        dragHandle = null,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            // Header Row: Title & Close Button (✕)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (title.isNotBlank()) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.TextPrimary
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Close",
                        tint = AppColors.TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar (Shown when showSearch is true)
            // Inside AppSearchableBottomSheet.kt (replace the Search Bar OutlinedTextField block)
            if (showSearch) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, AppColors.BorderSecondary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(48.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = "Search",
                            tint = AppColors.TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = searchPlaceholder,
                                    fontSize = 14.sp,
                                    color = AppColors.TextPlaceholder,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = AppColors.TextPrimary
                                ),
                                cursorBrush = SolidColor(AppColors.PrimaryBlue),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (searchQuery.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_close),
                                    contentDescription = "Clear",
                                    tint = AppColors.TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

            // Items List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 440.dp)
            ) {
                if (filteredItems.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No results found",
                                fontSize = 14.sp,
                                color = AppColors.TextSecondary
                            )
                        }
                    }
                } else {
                    items(filteredItems, key = { it.value.toString() }) { item ->
                        val isSelected = selectedItem?.value == item.value

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clickable {
                                    onItemSelected(item)
                                    onDismiss()
                                }
                                .padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Optional Leading Icon / Insurer Logo
                            if (item.iconRes != null) {
                                Image(
                                    painter = painterResource(id = item.iconRes),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                )
                                Spacer(modifier = Modifier.size(12.dp))
                            }

                            // Item Label
                            Text(
                                text = item.label,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) AppColors.TextPrimary else AppColors.TextPrimary,
                                modifier = Modifier.weight(1f)
                            )

                            // Trailing Blue Checkmark (✓) for active selection
                            if (isSelected) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_check),
                                    contentDescription = "Selected",
                                    tint = AppColors.PrimaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        HorizontalDivider(
                            color = Color(0xFFF3F4F6),
                            thickness = 0.8.dp,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier
                    .navigationBarsPadding()
                    .height(16.dp)
            )
        }
    }
}

/**
 * Standardized Clickable Selector Field (52.dp height, 12.dp rounded corners).
 * Displays label, placeholder, leading icon (if any), and trailing chevron down icon.
 * Triggers modal bottom sheet onClick.
 */
@Composable
fun ClickableSelectorField(
    value: String,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    iconRes: Int? = null,
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
                placeholder = {
                    Text(
                        text = placeholder,
                        color = AppColors.TextPlaceholder,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = iconRes?.let { res ->
                    {
                        Image(
                            painter = painterResource(id = res),
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
                        contentDescription = "Select",
                        tint = AppColors.TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = AppColors.TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isError) AppColors.ErrorRed else AppColors.BorderSecondary,
                    unfocusedBorderColor = if (isError) AppColors.ErrorRed else AppColors.BorderSecondary,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = AppColors.TextPrimary,
                    unfocusedTextColor = AppColors.TextPrimary
                ),
                singleLine = true,
                isError = isError,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
            )

            // Transparent overlay intercepts click reliably without opening software keyboard
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onClick() }
            )
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
private fun ClickableSelectorFieldPreview() {
    ClickableSelectorField(
        label = "Registration details",
        value = "RJ14 - Jaipur",
        placeholder = "Select registration details",
        onClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun ClickableSelectorFiel2dPreview() {
    ClickableSelectorField(
        label = "Registration details",
        value = "",
        placeholder = "Select registration details",
        onClick = {}
    )
}
