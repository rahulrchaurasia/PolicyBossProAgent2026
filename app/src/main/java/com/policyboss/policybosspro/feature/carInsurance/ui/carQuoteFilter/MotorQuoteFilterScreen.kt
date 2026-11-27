package com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarAction
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiState
import com.policyboss.policybosspro.feature.carInsurance.model.PolicyType
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.component.AddOnsFilterSection
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.component.DiscountsFilterSection
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.component.FilterBottomCtaBar
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.component.FilterCategoryRail
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.component.FilterTopBar
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.component.LiabilityCoversSection
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.component.SetIdvFilterSection
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.FilterCategory

@Composable
fun MotorQuoteFilterScreen(
    uiState: NewCarUiState,
    onAction: (NewCarAction) -> Unit,
    onCloseClick: () -> Unit,
    onApplyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .imePadding()
    ) {
        // 1. Minimal Top Bar with right-aligned Close (✕) button
        FilterTopBar(onCloseClick = onCloseClick)

        // 2. Split Layout: Fixed Left Rail + Scrollable Right Content Pane (weight = 1f)
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Fixed Left Category Rail (~128.dp); hidden when only one category applies (TP-only)
            if (uiState.showFilterCategoryRail) {
                FilterCategoryRail(
                    selectedCategory = uiState.selectedFilterCategory,
                    categories = uiState.availableFilterCategories,
                    onCategorySelected = { onAction(NewCarAction.OnSelectFilterCategory(it)) }
                )
            }

            // Scrollable Right Filter Section (strictly bounded above the bottom bar)
            val scrollState = rememberScrollState()
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(bottom = 16.dp)
                ) {
                    when (uiState.coerceFilterCategory(uiState.selectedFilterCategory)) {
                        FilterCategory.SET_IDV -> {
                            SetIdvFilterSection(
                                selectedIdv = uiState.draftQuoteFilter.selectedIdv,
                                minIdv = uiState.draftQuoteFilter.minIdv,
                                maxIdv = uiState.draftQuoteFilter.maxIdv,
                                onIdvChange = { onAction(NewCarAction.OnUpdateDraftIdv(it)) }
                            )
                        }
                        FilterCategory.ADD_ONS -> {
                            AddOnsFilterSection(
                                selectedAddOnIds = uiState.draftQuoteFilter.selectedAddOnIds,
                                selectedZeroDepClaimLimit = uiState.draftQuoteFilter.selectedZeroDepClaimLimit,
                                onToggleAddOn = { onAction(NewCarAction.OnToggleDraftAddOn(it)) },
                                onToggleSelectAll = { onAction(NewCarAction.OnToggleSelectAllAddOns(it)) },
                                onSelectZeroDepClaimLimit = { onAction(NewCarAction.OnSelectZeroDepClaimLimit(it)) }
                            )
                        }
                        FilterCategory.ADDITIONAL_COVER -> {
                            LiabilityCoversSection(
                                selectedLiabilityCoverIds = uiState.draftQuoteFilter.selectedLiabilityCoverIds,
                                selectedUnNamedPassengerCover = uiState.draftQuoteFilter.selectedUnNamedPassengerCover,
                                electricalAccessoriesAmount = uiState.draftQuoteFilter.electricalAccessoriesAmount,
                                nonElectricalAccessoriesAmount = uiState.draftQuoteFilter.nonElectricalAccessoriesAmount,
                                showAccessories = uiState.showAccessoriesFilter,
                                items = uiState.visibleAdditionalCoverItems,
                                onToggleLiabilityCover = { onAction(NewCarAction.OnToggleDraftLiabilityCover(it)) },
                                onSelectUnNamedPassengerCover = {
                                    onAction(NewCarAction.OnSelectDraftUnNamedPassengerCover(it))
                                },
                                onUpdateElectricalAccessories = {
                                    onAction(NewCarAction.OnUpdateDraftElectricalAccessories(it))
                                },
                                onUpdateNonElectricalAccessories = {
                                    onAction(NewCarAction.OnUpdateDraftNonElectricalAccessories(it))
                                }
                            )
                        }
                        FilterCategory.DISCOUNTS -> {
                            DiscountsFilterSection(
                                antiTheftDevice = uiState.draftQuoteFilter.antiTheftDevice,
                                payAsYouDrive = uiState.draftQuoteFilter.payAsYouDrive,
                                voluntaryExcess = uiState.draftQuoteFilter.voluntaryExcess,
                                onUpdateAntiTheft = { onAction(NewCarAction.OnUpdateDraftAntiTheft(it)) },
                                onUpdatePayAsYouDrive = { onAction(NewCarAction.OnUpdateDraftPayAsYouDrive(it)) },
                                onSelectVoluntaryExcess = { onAction(NewCarAction.OnSelectDraftVoluntaryExcess(it)) }
                            )
                        }
                    }
                }
            }
        }

        // Hairline Divider above Bottom CTA
        HorizontalDivider(
            color = Color(0xFFE2E8F0),
            thickness = 1.dp
        )

        // 3. Pinned Bottom CTA Bar (Reset on Left, Apply on Right)
        FilterBottomCtaBar(
            activeFilterCount = uiState.draftFilterCount,
            onApplyClick = {
                if (uiState.draftQuoteFilter.isAccessoriesValid) {
                    onApplyClick()
                }
            },
            onResetClick = { onAction(NewCarAction.OnResetDraftFilters) },
            isApplyEnabled = uiState.draftQuoteFilter.isAccessoriesValid
        )
    }
}

@Preview(name = "Filter - Comprehensive / OD", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun MotorQuoteFilterScreenPreview() {
    MotorQuoteFilterScreen(
        uiState = NewCarUiState(),
        onAction = {},
        onCloseClick = {},
        onApplyClick = {}
    )
}

@Preview(name = "Filter - TP only", showBackground = true, widthDp = 360, heightDp = 812)
@Composable
private fun MotorQuoteFilterScreenTpPreview() {
    MotorQuoteFilterScreen(
        uiState = NewCarUiState(policyType = PolicyType.TP_ONLY),
        onAction = {},
        onCloseClick = {},
        onApplyClick = {}
    )
}

@Preview(name = "Filter - Company Registration", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun MotorQuoteFilterScreenCompanyPreview() {
    MotorQuoteFilterScreen(
        uiState = NewCarUiState(ownershipType = com.policyboss.policybosspro.feature.carInsurance.model.OwnershipType.COMPANY),
        onAction = {},
        onCloseClick = {},
        onApplyClick = {}
    )
}
