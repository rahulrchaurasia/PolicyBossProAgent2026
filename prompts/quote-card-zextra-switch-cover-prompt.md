# Prompt: Quote card — "Zextra Switch Cover" row with Zextra logo when the toggle is ON

Act as a Senior Android Engineer (Kotlin, Jetpack Compose, Material 3, MVVM, StateFlow).
Keep the existing architecture. Do not commit. Do not redesign the card.

## Requirement
On `MotorQuoteResultsScreen`, when the agent turns ON the **Zextra Switch** toggle (quote controls card),
every insurer quote card, the recommended card included, shows near its bottom one strip:

`[Zextra Switch logo]  Zextra Switch Cover :                ₹354`

- Value = `premium_list_db` → `Response[i].Premium_Breakup.rsa_cpa_bundle_breakup.bundle_final_premium`
  (e.g. 354), not `bundle_net_premium` (300) or `bundle_service_tax` (54).
- Logo only (image 1: yellow "ZextrA Switch" badge). **No** "Extra Zextra Earnings", no ↑ arrow,
  no earnings or brokerage lines.
- Toggle OFF (Insurer CPA / No CPA) → strip hidden.
- An insurer with no bundle (`bundle_final_premium` missing or 0) → strip hidden on that card only.
- Style reference only (image 2, last row): rounded strip, soft blue gradient background, logo on the left,
  label in the middle-left, amount right-aligned, all vertically centred. Do not copy the rest of image 2.

## What already exists (reuse, don't duplicate)
- Data: `PremiumBreakupDto.rsaCpaBundle.finalPremium` → mapper → `MotorQuote.zextraBundlePremium`
  (`PremiumQuoteMapper.kt:92`). It is already parsed.
- Toggle: `uiState.isZextraBundleEnabled` (follows `CpaCoverageOption.ZEXTRA_SWITCH`) and is
  already passed to `InsurerQuoteCard(isZextraBundleEnabled = …)` for every card.
- UI: `InsurerQuoteCard.kt` "Tier 2 — Zextra Switch Cover Row" (~line 524) already renders
  "Zextra Switch Cover : ₹…" when `isZextraBundleEnabled && quote.zextraSwitchCoverPrice > 0`.
  **It never shows for API quotes** because the mapper sets `zextraSwitchCoverPrice = 0`
  (`PremiumQuoteMapper.kt:100`). The default 675 only applies to demo quotes.
- The "Extra Zextra Earnings" banner (~line 319) only shows when `totalEarnings > 0`. API quotes have 0,
  so it stays hidden. Leave it as is.

## Changes
1. Mapper (`PremiumQuoteMapper.kt`): `zextraSwitchCoverPrice = bundle_final_premium` (rounded, same value
   as `zextraBundlePremium`), so a single field drives the row. Demo quotes keep their default.
2. Logo asset: add `res/drawable/ic_zextra_switch_logo.(webp|png)`, or a vector converted from the
   designer's SVG. Create one small reusable composable, e.g.
   `ui/carQuoteResult/component/ZextraSwitchLogo.kt`:
   `Image(painterResource(R.drawable.ic_zextra_switch_logo), contentDescription = "Zextra Switch", Modifier.height(24.dp))`.
3. `InsurerQuoteCard` Tier 2 row → strip:
   - `Row(fillMaxWidth, clip(RoundedCornerShape(12.dp)), background(horizontal gradient: light lavender → light blue,
     e.g. AppColors.PrimaryBlue at 0.10f → 0.18f alpha), padding(horizontal 10.dp, vertical 8.dp), CenterVertically)`
   - `ZextraSwitchLogo()` · 10.dp · `Text("Zextra Switch Cover :", 13.5.sp, Medium, AppColors.TextSecondary, weight(1f))`
     · `Text("₹" + MotorQuote.formatIndianCurrency(price), 14.5.sp, Bold, AppColors.TextPrimary)`
   - Use the app's typography/colours (MyAppTypography / AppColors); no hard-coded new hex colours unless
     added to AppColors.
   - Keep the position: last block of the card body, below Car Value (IDV), above Share / Policy Details.
     For TP (no IDV box) it is the only block there.
   - `semantics(mergeDescendants = true)` so TalkBack reads "Zextra Switch Cover ₹354".
   - Visibility: `AnimatedVisibility(isZextraBundleEnabled && price > 0)` (expand + fade), so toggling animates.
4. Optional: replace the hand-drawn "Zextra | Switch" text badge in `QuoteControlsCard` with the same
   `ZextraSwitchLogo` for consistency. Only with approval.

## Keep exactly as is
Polling, footer and top loaders, premium/earnings blocks, sort/min-premium logic (`effectivePremium`),
add-ons, IDV, buttons, TP rules.

## Tests
- Mapper: a row with `rsa_cpa_bundle_breakup.bundle_final_premium = 354` gives `zextraSwitchCoverPrice == 354`;
  no bundle gives 0.
- A pure helper, e.g. `showZextraCover(isZextraBundleEnabled, price)`: ON+354 → true, OFF → false, ON+0 → false.
- Run `./gradlew :app:testDebugUnitTest`; all tests pass.

## Files touched
`PremiumQuoteMapper.kt`, `InsurerQuoteCard.kt`, new `ZextraSwitchLogo.kt`, new drawable, tests.
