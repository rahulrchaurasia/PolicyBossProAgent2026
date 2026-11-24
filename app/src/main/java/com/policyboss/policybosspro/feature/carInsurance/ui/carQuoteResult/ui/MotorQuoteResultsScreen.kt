package com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteResult.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.carInsurance.model.CarEntryMode
import com.policyboss.policybosspro.feature.carInsurance.model.CpaCoverageOption
import com.policyboss.policybosspro.feature.carInsurance.model.MotorQuote
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarAction
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiState
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.FilterCategory
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteResult.component.FeedbackDialog
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteResult.component.FilterBottomSheet
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteResult.component.InsurerQuoteCard
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteResult.component.QuoteResultsBottomBar
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteResult.component.QuoteSummaryCard
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteResult.component.QuoteValidityBanner
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteResult.component.SortBottomSheet
import com.policyboss.policybosspro.ui.components.bottomSheet.AppSearchableBottomSheet
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun MotorQuoteResultsScreen(
    uiState: NewCarUiState,
    onAction: (NewCarAction) -> Unit,
    onBackClick: () -> Unit,
    onNavigateToFilters: (FilterCategory) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Cyan/Teal to Primary Blue gradient matching design reference
    val topGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF14B8A6), // Vibrant cyan-teal
            Color(0xFF1EA0FA),
            Color(0xFF2E90FA)  // Primary Blue
        ),
        startY = 0f,
        endY = 900f
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppColors.ScreenBackground,
        bottomBar = {
            QuoteResultsBottomBar(
                vehicleSummary = uiState.vehicleSummaryText,
                onEditVehicleClick = { onAction(NewCarAction.OnEditVehicleClick) },
                onFiltersClick = { onNavigateToFilters(FilterCategory.SET_IDV) },
                onSortClick = { onAction(NewCarAction.OnShowSortSheet(true)) },
                onShareClick = { onAction(NewCarAction.OnShareAllQuotesClick) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // Gradient Header Background extending under summary cards
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .background(topGradient)
            )

            // Scrollable Content
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // 1. Top App Bar
                item(key = "top_bar") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Text(
                            text = "Top plans",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        // Feedback Pill Button
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color.White,
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .clickable { onAction(NewCarAction.OnShowFeedbackDialog(true)) }
                        ) {
                            Text(
                                text = "Feedback",
                                color = AppColors.PrimaryBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // 2. Quote Validity Banner
                item(key = "quote_validity_banner") {
                    QuoteValidityBanner(
                        validUptoText = uiState.quoteValidUptoText,
                        onInfoClick = { onAction(NewCarAction.OnCpaEditClick) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }

                // 3. Summary Card (with embedded blue VahanDataCard and QuoteControlsCard)
                item(key = "summary_card") {
                    QuoteSummaryCard(
                        uiState = uiState,
                        onAction = onAction,
                        onNavigateToFilters = onNavigateToFilters,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }

                // Recommended Quote Card Section
                val recommended = uiState.recommendedQuote
                if (recommended != null) {
                    item(key = "recommended_header") {
                        Spacer(modifier = Modifier.height(12.dp))
                        // Blue Recommended Badge
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 8.dp,
                                        topEnd = 8.dp,
                                        bottomEnd = 8.dp,
                                        bottomStart = 0.dp
                                    )
                                )
                                .background(AppColors.PrimaryBlue)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Recommended",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    item(key = "quote_${recommended.id}") {
                        InsurerQuoteCard(
                            quote = recommended,
                            isZextraBundleEnabled = uiState.isZextraBundleEnabled,
                            isAddOnsExpanded = uiState.expandedAddOnQuoteIds.contains(recommended.id),
                            isCompared = uiState.selectedQuotesForComparison.contains(recommended.id),
                            onToggleAddOnsExpanded = {
                                onAction(NewCarAction.ToggleAddOnsExpanded(recommended.id))
                            },
                            onToggleAddOn = { addOnId ->
                                onAction(NewCarAction.ToggleAddOnSelected(recommended.id, addOnId))
                            },
                            onToggleCompare = {
                                onAction(NewCarAction.ToggleCompareQuote(recommended.id))
                            },
                            onBuyNowClick = {
                                onAction(NewCarAction.OnBuyNowClick(recommended))
                            },
                            onShareClick = {
                                onAction(NewCarAction.OnShareQuoteClick(recommended))
                            },
                            onPolicyDetailsClick = {
                                onAction(NewCarAction.OnPolicyDetailsClick(recommended))
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                // Subhead: "Other X quotes"
                val otherQuotes = uiState.otherQuotes
                if (otherQuotes.isNotEmpty()) {
                    item(key = "other_quotes_header") {
                        Text(
                            text = "Other ${otherQuotes.size} quotes",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Slate800,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                        )
                    }

                    // Remaining Insurer Quote Cards
                    items(
                        items = otherQuotes,
                        key = { it.id }
                    ) { quote ->
                        InsurerQuoteCard(
                            quote = quote,
                            isZextraBundleEnabled = uiState.isZextraBundleEnabled,
                            isAddOnsExpanded = uiState.expandedAddOnQuoteIds.contains(quote.id),
                            isCompared = uiState.selectedQuotesForComparison.contains(quote.id),
                            onToggleAddOnsExpanded = {
                                onAction(NewCarAction.ToggleAddOnsExpanded(quote.id))
                            },
                            onToggleAddOn = { addOnId ->
                                onAction(NewCarAction.ToggleAddOnSelected(quote.id, addOnId))
                            },
                            onToggleCompare = {
                                onAction(NewCarAction.ToggleCompareQuote(quote.id))
                            },
                            onBuyNowClick = {
                                onAction(NewCarAction.OnBuyNowClick(quote))
                            },
                            onShareClick = {
                                onAction(NewCarAction.OnShareQuoteClick(quote))
                            },
                            onPolicyDetailsClick = {
                                onAction(NewCarAction.OnPolicyDetailsClick(quote))
                            },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Floating Headset Support Button on Right Edge (matches mockup)
//            Surface(
//                modifier = Modifier
//                    .align(Alignment.CenterEnd)
//                    .padding(end = 8.dp)
//                    .shadow(elevation = 6.dp, shape = CircleShape)
//                    .clip(CircleShape)
//                    .clickable { onAction(NewCarAction.OnCpaEditClick) },
//                shape = CircleShape,
//                color = Color.White
//            )
//            {
//                Icon(
//                    painter = painterResource(R.drawable.ic_headset),
//                    contentDescription = "Customer Support",
//                    tint = AppColors.Slate600,
//                    modifier = Modifier
//                        .padding(9.dp)
//                        .size(22.dp)
//                )
//            }
        }
    }

    // Modal Sheets and Dialogs
    if (uiState.showSortSheet) {
        SortBottomSheet(
            selectedOption = uiState.selectedSortOption,
            onOptionSelected = { onAction(NewCarAction.OnSortOptionSelected(it)) },
            onDismissRequest = { onAction(NewCarAction.OnShowSortSheet(false)) }
        )
    }

    if (uiState.showFilterSheet) {
        FilterBottomSheet(
            onDismissRequest = { onAction(NewCarAction.OnShowFilterSheet(false)) },
            onApplyFilters = { /* Handled in sheet */ }
        )
    }

    if (uiState.showFeedbackDialog) {
        FeedbackDialog(
            rating = uiState.feedbackRating,
            feedbackText = uiState.feedbackText,
            onRatingChange = { onAction(NewCarAction.OnFeedbackRatingChange(it)) },
            onFeedbackTextChange = { onAction(NewCarAction.OnFeedbackTextChange(it)) },
            onSubmit = { onAction(NewCarAction.OnSubmitFeedback) },
            onDismissRequest = { onAction(NewCarAction.OnShowFeedbackDialog(false)) }
        )
    }

    // Interactive Variant Selection Bottom Sheet
    if (uiState.showVariantSheet) {
        AppSearchableBottomSheet(
            title = "Select Variant",
            items = uiState.variantOptions,
            selectedItem = uiState.selectedVariant,
            onItemSelected = { option ->
                onAction(NewCarAction.OnVariantSelected(option))
            },
            onDismiss = { onAction(NewCarAction.OnShowVariantSheet(false)) }
        )
    }

    // Quote Control Info Dialog (IDV, Addons, CPA)
    if (uiState.infoDialogTitle != null && uiState.infoDialogMessage != null) {
        AlertDialog(
            onDismissRequest = { onAction(NewCarAction.OnDismissQuoteControlInfo) },
            title = {
                Text(
                    text = uiState.infoDialogTitle.orEmpty(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = AppColors.TextPrimary
                )
            },
            text = {
                Text(
                    text = uiState.infoDialogMessage.orEmpty(),
                    fontSize = 13.5.sp,
                    color = AppColors.TextSecondary,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { onAction(NewCarAction.OnDismissQuoteControlInfo) }) {
                    Text("OK", color = AppColors.PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Preview(name = "Motor Quote Results Screen", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun MotorQuoteResultsScreenPreview() {
    MotorQuoteResultsScreen(
        uiState = NewCarUiState(
            make = "Kia",
            model = "Seltos",
            fuel = "Diesel",
            variant = "PLUS 1.5 GTX AT (1493 CC)",
            registrationNumber = "RJ14CV0002",
            vahanData = "KIA INDIA PRIVATE LIMITED, SELTOS D1.5 6AT GTX PLUS, DIESEL",
            crnNumber = "12678184",
            cpaCoverageOption = CpaCoverageOption.ZEXTRA_SWITCH,
            isZextraBundleEnabled = true,
            quotes = com.policyboss.policybosspro.feature.carInsurance.model.MotorQuoteDefaults.generateDefaultQuotes()
        ),
        onAction = {},
        onBackClick = {}
    )
}

