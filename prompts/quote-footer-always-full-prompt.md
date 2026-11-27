# Prompt: Results screen footer — always show the full loader while polling

Act as a Senior Android Engineer (Kotlin, Jetpack Compose, Material 3, MVVM, StateFlow).
Keep the existing architecture. Do not commit.

## Decision
The top spinner and the footer loader mean different things, so both are shown:
- **Top spinner** (`QuoteSummaryCard`, `isQuoteSummaryLoading`): "your first quotes are loading".
  Short-lived — hides as soon as the first plan is bound (usually 1–3 s). **Keep as is.**
- **Footer** (`QuoteFetchingFooter`): "we are still asking insurers; more quotes may come".
  Visible for the whole polling time. It must show its **full** content from the start:
  skeleton card(s) + spinner + text + progress bar — never "skeletons only".

## Keep exactly as is (do NOT change)
- API flow and timing (`premium_initiate` → `premium_list_db`, 1 s for 15 s then 3 s, max 60 s),
  results open on the first answer, stop rules, cancellation.
- Top summary spinner and its `Crossfade` / 48.dp height, count-up animations, cards, slide-in.
- Footer visibility rule: shown while `uiState.isFetchingMoreQuotes`; timeout note after TIMED_OUT.
- Skeleton count: 2 while no plan has arrived, 1 once plans exist.

## Changes
1. Revert option C (footer "skeletons only"):
   - `MotorQuoteResultsScreen`: remove `showStatus = !uiState.isQuoteSummaryLoading` from `QuoteFetchingFooter(...)`.
   - `QuoteFetchingFooter`: remove the `showStatus` parameter, the `if (showStatus)` wrapper and the
     conditional semantics; the spinner, text, progress bar and `contentDescription` always render.
     Remove the "skeletons only" preview.
2. Footer wording (so it does not repeat the top text):
   - Add `hasQuotes: Boolean` to `QuoteFetchingFooter`.
   - Text: `"Fetching quotes…"` when `hasQuotes == false`, `"Fetching more quotes…"` when true,
     followed by the progress text when known (e.g. "Fetching quotes… 3 of 18 insurers responded").
   - `MotorQuoteResultsScreen` passes `hasQuotes = uiState.quotes.isNotEmpty()`.
   - Previews: keep "under quotes" (hasQuotes = true) and "no quotes yet" (hasQuotes = false).
3. Update `prompts/quote-summary-progress-indicator-prompt.md` item 3 to say the footer always shows
   its full status (option C dropped).

## Tests
- Pure function for the footer text, e.g. `quoteFetchingStatusText(hasQuotes, progressText)`:
  - `(false, "3 of 18 insurers responded")` → `"Fetching quotes… 3 of 18 insurers responded"`
  - `(true, "11 of 18 insurers responded")` → `"Fetching more quotes… 11 of 18 insurers responded"`
  - `(true, null)` → `"Fetching more quotes…"`
- Existing tests for `isQuoteSummaryLoading` and the ViewModel stay green.
- Run `./gradlew :app:testDebugUnitTest` — all tests pass.

## Files touched
`QuoteFetchingFooter.kt`, `MotorQuoteResultsScreen.kt`, `prompts/quote-summary-progress-indicator-prompt.md`,
one small test. Nothing else.
