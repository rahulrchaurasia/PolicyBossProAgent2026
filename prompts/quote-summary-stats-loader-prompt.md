# Prompt: Quote summary stats — loader instead of 0 / ₹0 while the first plans load

Act as a Senior Android Engineer (Kotlin, Jetpack Compose, Material 3, MVVM, StateFlow).
Keep the existing architecture. Do not commit.

## Keep exactly as is (do NOT change)
- Live `premium_list_db` polling: first answer opens the results screen, then one call every 3 s,
  max 60 s, stop on `complete` / timeout / 3 failures in a row (`CarInsuranceRepository.pollPremiumQuotes`,
  `NewCarViewModel.onPremiumInitiated` / `applyPremiumQuotes` as they were before step 1 below).
- Footer loader (`QuoteFetchingFooter`, skeleton cards, "Fetching more quotes… x of y", timeout note).
- Card bindings (valid-plan rule, Zextra bundle price, IDV, TP hiding, validity, CRN).

## Step 1 — take back the last change (it was not requested)
The last change held the loading screen until the first plan and replaced values with "—". Undo only that:
1. `NewCarViewModel.onPremiumInitiated`: restore the collector to open results on the **first**
   `premium_list_db` answer (`isFirst` flag → `applyPremiumQuotes(result, event, isFirst)`, then
   `isFirst = false; firstAnswer.complete(Unit)`), and the KDoc that says so.
2. `NewCarUiState`: remove `minPremiumText`, `maxEarningsText` and the private `NO_VALUE` constant.
3. `QuoteSummaryCard`: restore the two texts to
   `"₹${MotorQuote.formatIndianCurrency(uiState.minPremium)}"` and
   `MotorQuote.formatIndianCurrency(uiState.maxEarnings)`.
4. `NewCarQuoteLoadingTest`: remove the two tests added for it
   ("loader stays up until the first plan arrives…" and "results open with the no-quotes message when polling ends without a plan").

## Step 2 — the fix: loader in the summary stats while no plan has arrived
Problem: the results screen opens on the first `premium_list_db` answer, which often has no valid plan yet,
so the 3-stat row in `QuoteSummaryCard` (Quotes | Min. Premiums | Max. Earnings) shows `0`, `₹0`, `0`.

1. `NewCarUiState`: add one derived flag (no stored state)
   `val isQuoteSummaryLoading: Boolean get() = isFetchingMoreQuotes && quotes.isEmpty()`
   → true only while polling is running and no valid plan has been bound yet.
2. `QuoteSummaryCard`, 3-stat row only: when `uiState.isQuoteSummaryLoading`, show a small loader in place
   of each **value** (the `20.sp` number / premium text and the earnings amount inside the gold pill);
   keep the labels ("Quotes", "Min. Premiums", "Max. Earnings"), dividers, coin icon, sizes and layout unchanged
   so nothing jumps when the values appear.
   - Loader = a rounded shimmer placeholder sized like the value (≈ 28.dp × 20.dp for Quotes, ≈ 64.dp × 20.dp for
     Min. Premiums, ≈ 28.dp × 14.dp inside the earnings pill), using the same shimmer colours as the footer skeleton
     (`AppColors.Slate100` / `Slate50`). Reuse the shimmer from `QuoteFetchingFooter.kt` by making
     `rememberShimmerBrush()` `internal` instead of duplicating it.
   - Cross-fade loader → value with `AnimatedContent`/`Crossfade` keyed on `isQuoteSummaryLoading`.
3. When the first valid plan is bound, `quotes` becomes non-empty → the loader is removed and the real values
   show, then keep updating as later snapshots arrive (existing behaviour).
4. If polling ends with no plan (complete / timeout / errors), the flag turns false → the row shows the existing
   values (0 / ₹0) together with the existing "no insurer could quote" message — unchanged behaviour.
5. Demo / dummy-data flow (no `premiumInitiate`) never shows the loader (`premiumPollStatus` is null there).

## Tests
- `isQuoteSummaryLoading`: true when POLLING with no quotes; false once a quote is bound; false when COMPLETE /
  TIMED_OUT with no quotes; false for the dummy flow.
- ViewModel: first answer with no plan opens results with `isQuoteSummaryLoading == true`; the next answer with
  plans makes it false and binds the plans (no second navigation).
- Run `./gradlew :app:testDebugUnitTest` — all tests pass.

## Files touched
`NewCarViewModel.kt`, `NewCarUiState.kt`, `QuoteSummaryCard.kt`, `QuoteFetchingFooter.kt` (visibility only),
`NewCarQuoteLoadingTest.kt` (+ a state test). Nothing else.
