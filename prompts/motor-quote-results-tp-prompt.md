# Prompt: MotorQuoteResultsScreen (and Policy Details) — TP-only mode

Act as a Senior Android Engineer (Kotlin, Jetpack Compose, Material 3, MVVM, StateFlow, Hilt,
reusable UI components). Follow the existing architecture: the shared `NewCarViewModel` owns
`NewCarUiState`; screens are stateless and render from it. Do not add a new ViewModel. Do not commit.

## Context

"Select your preferred policy" on the New Car screen sets `NewCarUiState.policyType`
(`PolicyType.TP_ONLY`, `OD_ONLY`, `COMPREHENSIVE`). A third-party-only policy has no own-damage
cover, so anything tied to car value (IDV), add-ons, own-damage discounts or NCB does not apply.
The filter screen already handles TP (`NewCarUiState.isTpOnlyFilter`, `availableFilterCategories`,
`showAccessoriesFilter`) — reuse that, do not duplicate the TP check.

**OD and Comprehensive must look and behave exactly as today.** Every change below is TP-only.

## 1. Results screen: remove "Car Value" and "Addons" rows (TP only)

- `feature/carInsurance/ui/carQuoteResult/component/QuoteControlsCard.kt` — "Parameter Rows" block renders
  Row 1 **Car Value** (`idvAmount`, `onIdvClick`, `onIdvInfoClick`), Row 2 **Addons** (`addonsValue`,
  `onAddonsClick`, `onAddonsInfoClick`), Row 3 **Additional Cover**, each separated by a `HorizontalDivider`.
- `feature/carInsurance/ui/carQuoteResult/component/QuoteSummaryCard.kt` — builds `QuoteControlsCard(...)`.

Changes:
1. Add `showCarValue: Boolean = true` and `showAddons: Boolean = true` to `QuoteControlsCard`. When false,
   skip that row **and its divider**; never leave a leading/trailing/double divider (build the visible rows
   as a list and put dividers only *between* them). Additional Cover always stays.
2. `QuoteSummaryCard` passes `showCarValue = !uiState.isTpOnlyFilter` and `showAddons = !uiState.isTpOnlyFilter`.
3. Additional Cover summary in `QuoteSummaryCard` (`additionalCoverSummary`) counts electrical /
   non-electrical accessories; in TP those fields are hidden, so count only liability covers + un-named
   passenger. Reuse `QuoteFilterState.additionalCoverFilterCount` for TP instead of the inline counting.
4. Add a TP preview of `QuoteControlsCard` (no Car Value / Addons rows).

## 2. Discounts — confirm nothing is reachable in TP

Findings (verify, then keep as is unless stated):
- The results screen has **no Discounts row**. Discounts only exist as the `FilterCategory.DISCOUNTS`
  section of `MotorQuoteFilterScreen`, which TP already hides; `coerceFilterCategory` makes every entry
  point (bottom-bar **Filters** → `SET_IDV`, Car Value → `SET_IDV`, Addons → `ADD_ONS`) land on
  Additional Cover in TP. Keep it that way.
- `ui/carQuoteResult/component/FilterBottomSheet.kt` (old "Filter Quotes" sheet with Zero Dep) is only shown
  when `uiState.showFilterSheet` is true, and nothing in the car flow sets it. Leave it untouched; do not
  make it reachable.
- Zextra "Reduced by ₹X" in `InsurerQuoteCard` is a Zextra bundle price, not a policy discount — leave it.

## 3. Bottom summary strip: drop "NCB status" in TP

`NewCarUiState.vehicleSummaryText` (shown by `QuoteResultsBottomBar`) currently returns
`"$modelName • $fuelName • $variantName • $policyName • NCB status: $ncbText"`.
For TP return `"$modelName • $fuelName • $variantName • $policyName"` (e.g. `WAGON R • PETROL • VXI+ • TP Only`).
OD / Comprehensive unchanged.

## 4. Policy Details screen: no OD / discount / NCB in TP

- `feature/carInsurance/ui/carPolicyDetail/model/MotorPolicyDetailModels.kt` —
  `PolicyBreakupDetails.createSample(quote, uiState)` builds:
  `baseCoverGroup` "Own Damage Premium (A)" (Basic OD, O.D. Discount, Garage Benefit Discount, standalone
  No Claim Bonus), `thirdPartyGroup` "Third Party Premium (B)", `bundleGroup`, and `PolicyVehicleInfo`
  (`idv`, `newNcb`, …). Note: these values are still sample data.
- `MotorPolicyDetailRoute` / `MotorPolicyDetailScreen`, components `PolicyVehicleInfoCard`, `PremiumBreakupSection`.

For TP (`uiState?.isTpOnlyFilter == true`):
1. Do **not** add the Own Damage group (no Basic OD, O.D. Discount, Garage Benefit Discount, No Claim Bonus).
2. Third Party and Bundle groups stay; every total / tax / final premium computed from the groups must still
   add up (check how totals are derived and exclude OD there too — no hard-coded OD amount left in the sum).
3. `PolicyVehicleInfoCard`: hide the **IDV** and **New NCB** rows (make those fields nullable or add
   `showIdv` / `showNcb` flags; keep CRN, Reg. date, plan, car model).
4. Group captions/letters must still read correctly when OD is missing (e.g. "Third Party Premium (B)" →
   relabel to "(A)" if the letters are shown sequentially, or drop the letter — pick one and apply consistently).
5. Add TP previews for `MotorPolicyDetailScreen` and `PolicyVehicleInfoCard`.

## Tests (unit, `app/src/test/...`)
- `vehicleSummaryText`: TP has no "NCB status"; Comprehensive / OD keep it.
- `PolicyBreakupDetails.createSample(uiState = TP state)`: no `base_cover` group, no item whose label contains
  "Discount" or "No Claim Bonus", vehicle info has no IDV / NCB; Comprehensive still has `base_cover`.
- Totals for TP equal the sum of the remaining groups.
- TP Additional Cover summary ignores accessories amounts.
- Run `./gradlew :app:testDebugUnitTest` — all tests must pass.

## Done when
- TP results screen: no Car Value row, no Addons row, no stray dividers, Additional Cover row works and opens
  the TP filter; summary strip has no NCB.
- TP Policy Details: no OD premium, no discounts, no NCB, no IDV; totals consistent.
- Discounts unreachable anywhere in the TP flow.
- OD / Comprehensive screens unchanged.
