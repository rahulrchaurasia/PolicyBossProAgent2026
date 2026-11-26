package com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteResult.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun QuoteSummaryCard(
    uiState: NewCarUiState,
    onAction: (NewCarAction) -> Unit,
    onNavigateToFilters: (FilterCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(16.dp),
        color = AppColors.White,
        border = BorderStroke(1.dp, AppColors.Slate200)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // 1. 3-Stat Row: Quotes, Min. Premiums, Max. Earnings
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Column 1: Quotes
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.totalQuotesCount.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Slate900
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Quotes",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Slate500
                    )
                }

                VerticalDivider(
                    modifier = Modifier.height(34.dp),
                    color = AppColors.Slate200,
                    thickness = 1.dp
                )

                // Column 2: Min. Premiums
                Column(
                    modifier = Modifier.weight(1.3f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "₹${MotorQuote.formatIndianCurrency(uiState.minPremium)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Slate900
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Min. Premiums",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Slate500
                    )
                }

                VerticalDivider(
                    modifier = Modifier.height(34.dp),
                    color = AppColors.Slate200,
                    thickness = 1.dp
                )

                // Column 3: Max. Earnings
                Column(
                    modifier = Modifier.weight(1.3f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .background(
                                color = Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(999.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.img_gold_coin),
                            contentDescription = "Earnings",
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = MotorQuote.formatIndianCurrency(uiState.maxEarnings),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Slate900
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Max. Earnings",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Slate500
                    )
                }
            }

            // 2. Embedded Blue Vahan Data Child Card
            val isNewCar = uiState.entryMode == CarEntryMode.NEW_CAR
            val regNumber = if (!isNewCar && uiState.registrationNumber.isNotBlank()) {
                uiState.formattedRegistrationNumber
            } else {
                ""
            }

            if (uiState.vahanDescriptionText.isNotBlank() || regNumber.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                VahanDataCard(
                    vahanDescription = uiState.vahanDescriptionText,
                    registrationNumber = regNumber,
                    isNewCar = isNewCar
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Dedicated Quote Controls & CPA/Bundle Card
            val variantText = uiState.variant.ifBlank {
                uiState.selectedVariant?.label ?: "PLUS 1.5 GTX AT (1493 CC)"
            }

            val additionalCoverSummary = run {
                // TP hides the accessories fields, so only liability + passenger covers count
                if (uiState.isTpOnlyFilter) {
                    val tpCount = uiState.appliedQuoteFilter.additionalCoverFilterCount
                    return@run if (tpCount > 0) "$tpCount Selected" else "Select"
                }
                var count = uiState.appliedQuoteFilter.selectedLiabilityCoverIds.size
                val unNamed = uiState.appliedQuoteFilter.selectedUnNamedPassengerCover
                if (!unNamed.isNullOrBlank() && unNamed != "No") {
                    count++
                }
                if (uiState.appliedQuoteFilter.isElectricalActive) {
                    count++
                }
                if (uiState.appliedQuoteFilter.isNonElectricalActive) {
                    count++
                }
                if (count > 0) "$count Selected" else "Select"
            }

            QuoteControlsCard(
                make = uiState.make.ifBlank { "Kia" },
                model = uiState.model.ifBlank { "Seltos" },
                fuel = uiState.fuel.ifBlank { "Diesel" },
                variantText = variantText,
                crnNumber = uiState.crnNumber,
                registrationNumber = uiState.formattedRegistrationNumber,
                isNewCar = isNewCar,
                idvAmount = uiState.appliedQuoteFilter.selectedIdv.toInt(),
                addonsCount = 17, // Matches 17 Addons reference layout
                addonsValue = "17 Addons",
                additionalCoverValue = additionalCoverSummary,
                selectedCpaOption = uiState.cpaCoverageOption,
                onVariantClick = { onAction(NewCarAction.OnShowVariantSheet(true)) },
                onIdvClick = { onNavigateToFilters(FilterCategory.SET_IDV) },
                onIdvInfoClick = {
                    onAction(
                        NewCarAction.OnShowQuoteControlInfo(
                            title = "Car Value (IDV)",
                            message = "Insured Declared Value (IDV) is the maximum sum insured for your vehicle in case of total loss or theft."
                        )
                    )
                },
                onAddonsClick = { onNavigateToFilters(FilterCategory.ADD_ONS) },
                onAddonsInfoClick = {
                    onAction(
                        NewCarAction.OnShowQuoteControlInfo(
                            title = "Add-on Covers",
                            message = "Add-on covers offer additional protection beyond standard comprehensive policies, such as Zero Depreciation, Engine Protect, RSA, etc."
                        )
                    )
                },
                onAdditionalCoverClick = { onNavigateToFilters(FilterCategory.ADDITIONAL_COVER) },
                onAdditionalCoverInfoClick = {
                    onAction(
                        NewCarAction.OnShowQuoteControlInfo(
                            title = "Additional Cover",
                            message = "Additional covers provide extra liability protection including Owner-Driver PA Cover, Paid Driver Liability, and Passenger Cover."
                        )
                    )
                },
                showCarValue = !uiState.isTpOnlyFilter,
                showAddons = !uiState.isTpOnlyFilter,
                onSelectCpaOption = { onAction(NewCarAction.OnSelectCpaCoverageOption(it)) },
                onCpaOptionInfoClick = { option ->
                    val (title, message) = when (option) {
                        CpaCoverageOption.ZEXTRA_SWITCH -> "Zextra Switch" to "Special Zextra Bundle includes CPA and 24x7 Roadside Assistance."
                        CpaCoverageOption.INSURER_CPA -> "Insurer CPA" to "Compulsory Personal Accident cover provided directly by the selected insurer."
                        CpaCoverageOption.NO_CPA -> "No CPA" to "Choose this if you already hold an active personal accident cover of ₹15 Lakh or more."
                    }
                    onAction(NewCarAction.OnShowQuoteControlInfo(title, message))
                }
            )
        }
    }
}

@Preview(name = "Quote Summary Card - Complete Flow", showBackground = true)
@Composable
private fun QuoteSummaryCardPreview() {
    QuoteSummaryCard(
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
        onNavigateToFilters = {}
    )
}
