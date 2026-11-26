package com.policyboss.policybosspro.feature.carInsurance.model

import androidx.compose.runtime.Immutable

/** Progress of insurer calls for one search (`premium_summary` / `premium_list_db` Summary). */
@Immutable
data class PremiumProgress(
    val status: String = "",
    val total: Int = 0,
    val pending: Int = 0,
    val success: Int = 0,
    val fail: Int = 0
) {
    /** All insurers answered: backend says `complete`, or nothing is pending any more. */
    val isComplete: Boolean
        get() = status.equals(STATUS_COMPLETE, ignoreCase = true) || (total > 0 && pending == 0)

    companion object {
        const val STATUS_COMPLETE = "complete"
    }
}

/** An insurer that returned no plan, with the reason to show the agent. */
@Immutable
data class DeclinedInsurer(
    val insurerId: Int,
    val insurerName: String,
    val logoUrl: String?,
    val errorCode: String,
    val reason: String
)

/** Parsed `premium_list_db` result for the quote results screen. */
@Immutable
data class PremiumQuoteList(
    val searchReferenceNumber: String,
    val crn: String,
    val progress: PremiumProgress,
    /** One per insurer plan, cheapest first. */
    val quotes: List<MotorQuote>,
    val declinedInsurers: List<DeclinedInsurer>,
    val minIdv: Int,
    val maxIdv: Int
)
