package com.policyboss.policybosspro.feature.carInsurance.model

/**
 * Every confirm popup on the car quote screens, with all of its texts, in one place.
 * Add new popups (IDV change, add-on change, CPA change ...) here only; the UI
 * (AppConfirmPopup) has no hard-coded strings, and NewCarViewModel.onConfirmQuotePopup
 * decides what each one does.
 */
sealed class QuotePopup(
    val title: String?,
    val message: String,
    val confirmLabel: String,
    /** Optional drawable shown above the title. */
    val iconRes: Int? = null
) {
    /** Results screen: a different variant was picked; quotes are re-fetched only on confirm. */
    data object RecalculateVariant : QuotePopup(
        title = "Variant changed",
        message = "You have changed the vehicle variant. Please confirm if you want to re-calculate quotes.",
        confirmLabel = "Re-calculate"
    )
}
