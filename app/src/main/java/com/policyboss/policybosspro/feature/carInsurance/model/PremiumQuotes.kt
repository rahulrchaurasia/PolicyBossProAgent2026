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

/** Where the premium_list_db polling stands. */
enum class PremiumPollStatus {
    /** Summary.Status is still pending; more insurers may answer. */
    POLLING,
    /** Summary.Status is complete. */
    COMPLETE,
    /** The polling time limit was reached while still pending. */
    TIMED_OUT,
    /** Several calls in a row failed after plans were already shown. */
    STOPPED_ON_ERROR
}

/** One step of premium_list_db polling. */
sealed interface PremiumQuotePollEvent {
    /** Latest merged snapshot of every insurer that has answered so far. */
    data class Update(val quotes: PremiumQuoteList, val status: PremiumPollStatus) : PremiumQuotePollEvent

    /** The very first call failed, so there is nothing to show. */
    data class Failed(val message: String) : PremiumQuotePollEvent
}

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
    val maxIdv: Int,
    val vehicleExpectedIdv: Int = 0
) {
    /**
     * Each premium_list_db response is a full snapshot, so the newer one wins. Insurers that were
     * quoted before but are missing from [newer] are kept, so a card never disappears mid-poll.
     */
    fun mergedWith(newer: PremiumQuoteList): PremiumQuoteList {
        val newerIds = newer.quotes.mapNotNull { it.insurerId }.toSet() +
            newer.declinedInsurers.map { it.insurerId }.toSet()
        val keptQuotes = quotes.filter { it.insurerId != null && it.insurerId !in newerIds }
        val keptDeclined = declinedInsurers.filter { it.insurerId !in newerIds }
        return newer.copy(
            quotes = (newer.quotes + keptQuotes).sortedBy { it.finalPremium },
            declinedInsurers = newer.declinedInsurers + keptDeclined
        )
    }
}
