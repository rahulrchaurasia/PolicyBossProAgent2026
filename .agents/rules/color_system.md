# AppColor Design System & Centralization Guidelines

## Strict Rule: No Hardcoded Colors
1. **Never use hardcoded `Color(0x...)` or raw hex/RGB color values in UI composables, screens, components, dialogs, modifiers, or previews.**
2. **Centralized Source of Truth**: All colors must be defined in `com.policyboss.policybosspro.ui.theme.AppColors` in `app/src/main/java/com/policyboss/policybosspro/ui/theme/Color.kt`.
3. **Adding New Colors**:
   - Whenever a new screen, design specification, or feature introduces a new color, **first define it as a reusable token in `AppColors`** with a clear semantic or palette name (e.g. `Slate*`, `Brand*`, or feature/role-specific names).
   - Only then reference `AppColors.<TokenName>` in the composable UI code.
4. **Consistency**:
   - Use existing tokens wherever the visual intention aligns (e.g., `AppColors.Slate600` for secondary/subtext labels, `AppColors.Slate800` / `AppColors.Slate900` for primary headings, `AppColors.PrimaryBlue` for main CTA/accents, `AppColors.BorderLight` / `AppColors.CardBorderSubtle` for light borders).

## Theme Defaults & Light-Mode Guidelines
1. **Explicit Light Mode Default**: `PolicyBossTheme` defaults to `darkTheme = false` so the application consistently preserves its intended crisp, light-mode design regardless of the user's system dark mode setting.
2. **Material 3 Semantic Alignment**:
   - `primary = AppColors.PrimaryBlue` (`0xFF2E90FA`) drives all primary interactive controls (tabs, selected indicators, checkboxes, radio buttons, primary buttons, focused textfields). Never set `primary` to dark/black colors.
   - `surface = AppColors.Surface` (`0xFFFFFFFF`) ensures cards, dialogs, bottom sheets, and popups render with pure white surfaces.
   - `background = AppColors.Background` (`0xFFF4F6F8`) ensures screen backgrounds are soft light gray.
   - `outline` and `outlineVariant` map to `AppColors.BorderSecondary` and `AppColors.BorderLight` rather than M3's default harsh dark brownish-gray.
3. **Tabs & Filter Chips**:
   - Selected tabs use `AppColors.PrimaryBlue` text, `AppColors.BlueTintBackground` container highlight, and `AppColors.PrimaryBlue` bottom indicator.
   - Filter chips use clean white cards (`AppColors.Surface`) with subtle 1dp borders (`AppColors.BorderLight`), transitioning to `AppColors.BlueTintBackground` / `AppColors.PrimaryBlue` when selected. Never use default dark-elevated chips.
