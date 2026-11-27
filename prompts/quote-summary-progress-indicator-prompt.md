# Prompt: Results screen — CircularProgressIndicator in the summary row until real data arrives

Act as a Senior Android Engineer (Kotlin, Jetpack Compose, Material 3, MVVM, StateFlow).
Keep the existing architecture. Do not commit.

## Requirement
When the results screen opens (on the first `premium_list_db` answer), the summary row at the top
(Quotes | Min. Premiums | Max. Earnings) shows `0`, `₹0`, `0` because no insurer has quoted yet.
Instead, show a Material 3 `CircularProgressIndicator` there until the first real plan is bound,
then hide it and show the values. **No shimmer at the top.**

## Keep exactly as is (do NOT change)
- API flow and timing: `premium_initiate` → `premium_list_db` polling (1 s for 15 s, then 3 s, max 60 s),
  results open on the first answer, stop rules, cancellation.
- Footer loader (`QuoteFetchingFooter`), card bindings, count-up animations, slide-in animations.

## Changes
1. `NewCarUiState`: add one derived flag (no stored state):
   ```kotlin
   /** Results are open, insurers are still answering and no plan has been bound yet. */
   val isQuoteSummaryLoading: Boolean
       get() = isFetchingMoreQuotes && quotes.isEmpty()
   ```
   - True only while polling runs AND no valid plan exists.
   - Becomes false on the first answer with ≥1 valid plan → values show.
   - Also false when polling ends with no plan (complete / 60 s / errors) → the row shows the real
     values (0 / ₹0) next to the existing "none of the insurers could quote" message.
   - Never true in the demo/dummy-data flow (`premiumPollStatus` is null there).
   - Do NOT use Max. Earnings in the condition: the quote APIs never send brokerage, so it stays 0.
2. `QuoteSummaryCard`, 3-stat row only:
   - When `uiState.isQuoteSummaryLoading`: replace the whole 3-stat `Row` with one centered row:
     `CircularProgressIndicator` (20.dp, `strokeWidth = 2.dp`, `color = AppColors.PrimaryBlue`,
     `trackColor = AppColors.PrimaryBlue.copy(alpha = 0.14f)`) + 8.dp gap + text
     "Getting quotes from insurers…" (12.sp, Medium, `AppColors.Slate500`).
   - Give the loading row the same height as the stats row (measure the current row: value 20.sp +
     2.dp + label 12.sp ≈ 48.dp; use `Modifier.height(48.dp)` on both or a shared min height) so the card
     does not jump when the values replace the loader.
   - Switch with `Crossfade(targetState = uiState.isQuoteSummaryLoading)`.
   - Add `semantics { contentDescription = "Getting quotes from insurers" }` on the loading row.
   - The Vahan card, controls card and everything below stay unchanged.
3. Footer: always shows its full status while polling (skeletons + spinner + text + progress bar),
   also while the top spinner is visible — option C ("skeletons only") was dropped. See
   `quote-footer-always-full-prompt.md` ("Fetching quotes…" before the first plan, "Fetching more quotes…" after).

## Tests
- `isQuoteSummaryLoading`: true when POLLING with no quotes; false once a quote is bound; false for
  COMPLETE / TIMED_OUT / STOPPED_ON_ERROR with no quotes; false for the default (dummy) state.
- ViewModel: first `premium_list_db` answer with no plan → results open with `isQuoteSummaryLoading == true`;
  next answer with plans → false, quotes bound, no second navigation.
- Run `./gradlew :app:testDebugUnitTest` — all tests pass.

## Files touched
`NewCarUiState.kt`, `QuoteSummaryCard.kt`, tests. Nothing else.
