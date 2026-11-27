# Prompt: Results screen — variant sheet from beta_GetFuelVariant, re-quote on change

Act as a Senior Android Engineer (Kotlin, Jetpack Compose, Material 3, MVVM, StateFlow).
Keep the existing architecture. Do not commit.

## Requirement
On `MotorQuoteResultsScreen`, the quote controls card shows:
- Line 1 `MARUTI, WAGON R, PETROL`: `make / model / fuel` from `vehicle_info_user` (or the form choice).
- Variant pill `VXI+`: `Variant_Name` from `vehicle_info_user` (or the variant picked later).
Tapping the pill opens "Select Variant". The list must come from the existing
`GET vehicles/beta_GetFuelVariant/{Model_ID}/1/{Make_Name}` response (already cached after
`vehicle_info_user`), not the hard-coded `CarInsuranceData.defaultVariants`.
Picking a different variant changes `Vehicle_ID`, so quotes are fetched again.

## Keep exactly as is
- `beta_GetFuelVariant` API, repository cache, car details sheet (Make/Model → Fuel → Variant).
- Get Quotes journey (loader → results on first answer), polling timing, stop rules.
- Top summary spinner overlay and footer loader (they are reused for the re-quote).

## Changes
1. `NewCarUiState` (derived only):
   - `quoteVariantItems`: `fuelVariantCatalog` for the committed `modelId` → `variantsFor(fuel)`
     (external fitted CNG/LPG → PETROL variants), plus the committed vahan variant when the
     catalogue does not list it.
   - `quoteVariantOptions` (`DropdownOption(Vehicle_ID, "VXI+ (998CC)")`), `selectedQuoteVariantOption`
     (match by `variantId`, else by name).
2. `MotorQuoteResultsScreen`: sheet uses `quoteVariantOptions` / `selectedQuoteVariantOption`.
3. `NewCarViewModel`:
   - `OnShowVariantSheet(true)` calls `loadFuelVariants(modelId, make)` (no-op when cached).
   - `OnVariantSelected` → `onQuoteVariantSelected`: same variant → close sheet only. New variant →
     commit `variant`, `variantId`, `fuelId`, CC, seating, ex-showroom, `selectedVariant`,
     `carDetailsSummary`, then `refreshQuotesInPlace()`.
   - `refreshQuotesInPlace()`: cancel quote job + polling, clear quotes, set `premiumPollStatus = POLLING`
     (top spinner + footer "Fetching quotes…"), run `startPremiumInitiate(..., openResults = false)`.
     Needs the vahan vehicle; the manual/demo flow only updates the label.
   - `openResults = false` → no `NavigateToQuotes`; errors → `STOPPED_ON_ERROR` + toast.
   - `OnLeaveQuoteResults` also cancels an in-flight re-quote and resets `quoteState` from Loading.
4. `QuoteSummaryCard` / `QuoteControlsCard`: remove the dummy "Kia / Seltos / Diesel /
   PLUS 1.5 GTX AT" fallbacks; empty variant shows "Select variant".

## Tests (`QuoteVariantRequoteTest`)
- Sheet lists the catalogue for the committed fuel, vahan variant selected.
- New variant → fields committed, loaders on, premium_initiate with the new Vehicle_ID, quotes bound,
  no second navigation.
- Same variant → sheet closes, no API call.
- premium_initiate failure → STOPPED_ON_ERROR, loaders off, toast.
- Leave during re-quote → cancelled, Get Quotes still works.
