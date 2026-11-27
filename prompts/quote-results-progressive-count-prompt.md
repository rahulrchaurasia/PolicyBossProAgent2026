# Prompt: Quote results — show the count growing (0 → 4 → 8 → 14), no top loader

Act as a Senior Android Engineer (Kotlin, Jetpack Compose, MVVM, StateFlow, coroutines/Flow).
Keep the existing architecture and flow. Do not commit.

## Problem
After the loader, the results screen shows a shimmer in the summary, then jumps straight to e.g. 10 and
then 14 quotes. The agent should see the numbers grow (0, then 4, then 8, then 14) so it is clear quotes
are still arriving. Showing 0 at the start is fine.

Root cause (measured from a live response): all insurers finish in ~9 s (`Total_Execution_Time: 9.34`).
`premium_list_db` is polled once every 3 s, so only ~3 snapshots fall inside that window (0 → 10 → 14).
The data in between is never fetched, so it can never be shown.

## Keep exactly as is
- Flow: Get Quotes → `premium_initiate` → `premium_list_db` polling as a sequential `Flow`
  (`CarInsuranceRepository.pollPremiumQuotes`), results open on the first answer
  (`NewCarViewModel.onPremiumInitiated` / `applyPremiumQuotes`), stop on `complete` / 60 s / 3 failures
  in a row, cancellation on leave / close / new search.
- Footer loader (`QuoteFetchingFooter`, skeleton cards, "Fetching more quotes… x of y", timeout note).
- Card bindings, valid-plan rule, merging/dedupe by `Insurer_Id`, `animateItem()`.

## Change 1 — remove the top (summary) loader
Show the real values from the first answer, even when they are 0 / ₹0.
- `QuoteSummaryCard`: remove `SummaryStatValue` and render the three values as plain `Text` again
  (original code: `uiState.totalQuotesCount.toString()`, `"₹${MotorQuote.formatIndianCurrency(uiState.minPremium)}"`,
  `MotorQuote.formatIndianCurrency(uiState.maxEarnings)`), plus the unused imports.
- `NewCarUiState`: remove `isQuoteSummaryLoading`.
- `QuoteFetchingFooter.kt`: `rememberShimmerBrush()` back to `private` (only the footer uses it).
- Tests: remove `isQuoteSummaryLoading` assertions/tests; keep the progressive-list test.

## Change 2 — poll faster while insurers are answering (sequential, never parallel)
Do NOT fire `premium_list_db` calls in parallel: each call returns the same DB snapshot of that moment,
so parallel calls return duplicates, can arrive out of order, and multiply server load. Instead shorten the
wait between the *sequential* calls during the fast phase:
- `pollPremiumQuotes`: interval **1 s for the first 15 s**, then **3 s** until the existing 60 s cap.
  Implement as a small pure function, e.g. `premiumPollDelay(elapsed: Duration): Duration`
  (`if (elapsed < 15.seconds) 1.seconds else 3.seconds`), and track elapsed time as the sum of delays
  waited so far; stop with `TIMED_OUT` when elapsed ≥ 60 s (replace the current `maxPolls` count).
  Keep the `pollInterval`-style parameters overridable for tests (e.g. `fastInterval`, `fastPhase`,
  `slowInterval`, `maxDuration`).
- Calls still never overlap: the next call starts only after the previous response + the delay.
- Worst case is ~15 + 15 = ~30 calls per search instead of 20; tell the backend team.

## Change 3 — make every increase visible
- `QuoteSummaryCard` Quotes value and `MotorQuoteResultsScreen` "N Plans Found": animate the number
  with a count-up (`animateIntAsState(targetValue = count, animationSpec = tween(600))` and show the
  animated value), so even 0 → 10 visibly ticks 0, 1, 2 … 10.
- Min. Premium: animate the change the same way (or `AnimatedContent` slide), no shimmer.

## Tests
- `premiumPollDelay`: 1 s before 15 s elapsed, 3 s after.
- Repository on a virtual clock: always-pending → stops at 60 s with `TIMED_OUT`; first 15 calls are 1 s apart,
  later ones 3 s apart; `complete` still stops at once; one failed call is retried; 3 failures stop.
- Run `./gradlew :app:testDebugUnitTest` — all tests pass.

## Files touched
`CarInsuranceRepository.kt` (polling timing only), `QuoteSummaryCard.kt`, `MotorQuoteResultsScreen.kt`
(plans-found count animation), `NewCarUiState.kt` (remove flag), `QuoteFetchingFooter.kt` (visibility),
tests. Nothing else.
