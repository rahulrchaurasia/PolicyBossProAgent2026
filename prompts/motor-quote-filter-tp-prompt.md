# Prompt: MotorQuoteFilterScreen — TP mode + Additional Cover UI polish

Act as a Senior Android Engineer (Kotlin, Jetpack Compose, Material 3, MVVM, StateFlow, Hilt,
reusable UI components). Follow the existing architecture exactly: the shared `NewCarViewModel`
owns `NewCarUiState`; the filter screen edits `uiState.draftQuoteFilter` through `NewCarAction`s
and `ApplyFilter` copies it to `appliedQuoteFilter`. Do not add a new ViewModel. Do not commit.

## Files involved

- `feature/carInsurance/ui/carQuoteFilter/MotorQuoteFilterScreen.kt` — rail + `when (selectedFilterCategory)`
- `feature/carInsurance/ui/carQuoteFilter/MotorQuoteFilterRoute.kt`
- `feature/carInsurance/ui/carQuoteFilter/component/FilterCategoryRail.kt` — iterates `FilterCategory.entries`
- `feature/carInsurance/ui/carQuoteFilter/component/LiabilityCoversSection.kt` — "Additional Cover" content:
  4 liability checkboxes (Material `Checkbox`), "Un-Named Passenger Cover" radios (private
  `PassengerRadioOption` using Material `RadioButton`), Electrical / Non-Electrical accessories fields
- `feature/carInsurance/ui/carQuoteFilter/model/MotorQuoteFilterModels.kt` — `FilterCategory`, `QuoteFilterState`
  (`activeFilterCount`), `MotorQuoteFilterData.unNamedPassengerOptions`
- `feature/carInsurance/viewmodel/NewCarViewModel.kt` — `OpenFilterScreen(category)`, `OnSelectFilterCategory`,
  `OnResetDraftFilters`, `ApplyFilter`
- `feature/home/graph/HomeGraph.kt:195` — results screen opens the filter with a category
- `ui/components/radio/AppRadioButton.kt` — existing custom radio (`AppRadioButton`, `AppRadioButtonOption`)
- `feature/carInsurance/model/PolicyType.kt` — `TP_ONLY`, `OD_ONLY`, `COMPREHENSIVE`
  (from "Select your preferred policy" on the New Car screen, `NewCarUiState.policyType`)

## Part 1 — Additional Cover UI polish (ALL policy types)

Problem: in "Un-Named Passenger Cover", labels like "₹ 1,00,000" wrap so the ₹ and the amount sit
on two lines, and the radios/checkboxes are stock Material, not the app's custom controls.

1. **Create a reusable custom checkbox** `ui/components/checkbox/AppCheckbox.kt`, styled to match
   `AppRadioButton` (same `AppColors.Primary` fill, `AppColors.PrimaryDark` border when checked,
   `AppColors.BorderPrimary` border when unchecked, 180 ms color animation, spring check-mark scale,
   rounded square ~5.dp corners, default size 20.dp, disabled state at 40 % alpha).
   - `AppCheckbox(checked, onCheckedChange: ((Boolean) -> Unit)?, modifier, enabled, size, ...)`
     using `Modifier.toggleable(role = Role.Checkbox)` when `onCheckedChange != null`.
   - `AppCheckboxOption(checked, label, onCheckedChange, modifier, enabled, labelStyle, ...)` — whole row
     toggleable (no dead zones), mirroring `AppRadioButtonOption`; plus a slot overload for custom content.
   - Previews for checked / unchecked / disabled, wrapped in `PolicyBossTheme`.
2. **Liability cover checkboxes**: replace Material `Checkbox` with `AppCheckboxOption`. Keep the existing
   toggle action `OnToggleDraftLiabilityCover(id)` and labels.
3. **Un-Named Passenger Cover radios**: delete the private `PassengerRadioOption` and use
   `AppRadioButtonOption`.
   - Keep the 2 × 2 grid (No | ₹ 50,000 / ₹ 1,00,000 | ₹ 2,00,000), each cell `Modifier.weight(1f)`.
   - Every label must stay on ONE line: `maxLines = 1`, `softWrap = false`, `overflow = TextOverflow.Ellipsis`,
     and use a non-breaking space between ₹ and the amount (`"₹ 1,00,000"`) for display only —
     the stored value / action payload stays exactly as in `MotorQuoteFilterData.unNamedPassengerOptions`.
   - Radio size 20.dp, 8.dp spacing, label 14.sp; cells vertically centered, rows 12.dp apart.
   - Must fit without wrapping on a 360.dp-wide phone with the 128.dp rail visible (OD / Comprehensive).
4. Do not change accessories validation, actions or state.

## Part 2 — TP only shows "Additional Cover"

When `uiState.policyType == PolicyType.TP_ONLY`:

1. Show only the **Additional Cover** content. **Hide the left `FilterCategoryRail` completely** and let the
   content use the full width.
2. Inside Additional Cover, **hide the Electrical and Non-Electrical Accessories fields** (own-damage covers).
   Show only the 4 liability checkboxes and Un-Named Passenger Cover.
3. Opening the filter in TP always lands on `FilterCategory.ADDITIONAL_COVER`, whatever category the
   caller passes (`OpenFilterScreen(category)` from the results screen / `HomeGraph`).
4. OD and Comprehensive keep today's screen exactly (all 4 categories, rail visible, accessories visible),
   apart from the Part 1 polish.

Implementation guidance:
- Add to `NewCarUiState` (derived, no new stored state):
  `val availableFilterCategories: List<FilterCategory>` → `listOf(ADDITIONAL_COVER)` for TP, else `FilterCategory.entries`;
  `val showFilterCategoryRail: Boolean` → `availableFilterCategories.size > 1`;
  `val showAccessoriesFilter: Boolean` → `policyType != PolicyType.TP_ONLY`.
- `FilterCategoryRail` takes `categories: List<FilterCategory>` instead of iterating `FilterCategory.entries`.
- `LiabilityCoversSection` gets `showAccessories: Boolean = true`.
- In the ViewModel (`OpenFilterScreen`, `OnSelectFilterCategory`), coerce the category to one in
  `availableFilterCategories` (fallback to the first).

### TP filter count / apply — confirm before changing (see questions)
`QuoteFilterState` defaults select `zero_dep` add-on, `antiTheftDevice = true` and `payAsYouDrive = true`, and
`activeFilterCount` counts them. In TP those categories are hidden, so the Apply button would show a count for
filters the agent cannot see. Proposed: in TP, `activeFilterCount` counts only Additional Cover items
(liability covers + un-named passenger), via a policy-aware count (e.g. `activeFilterCount(policyType)`),
and Reset in TP resets only Additional Cover fields.

## Tests (unit, `app/src/test/...`)
- `availableFilterCategories` / `showFilterCategoryRail` / `showAccessoriesFilter` for TP, OD, Comprehensive.
- `OpenFilterScreen(FilterCategory.SET_IDV)` with TP → `selectedFilterCategory == ADDITIONAL_COVER`; with OD it stays SET_IDV.
- Selecting a passenger cover still sends the original value (`"₹ 1,00,000"`, not the non-breaking-space label).
- Run `./gradlew :app:testDebugUnitTest` — all tests must pass.

## Done when
- TP: only Additional Cover, no rail, no accessories; OD / Comprehensive unchanged in structure.
- Un-Named Passenger options are one line each, aligned in a 2 × 2 grid, using `AppRadioButtonOption`.
- Liability covers use the new `AppCheckboxOption`.
- Previews added for TP and OD variants of `MotorQuoteFilterScreen` and for `AppCheckbox`.
