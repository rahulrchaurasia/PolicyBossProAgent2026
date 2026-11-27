package com.policyboss.policybosspro.feature.carInsurance.data.mapper

import com.policyboss.policybosspro.feature.carInsurance.data.model.InsurerPremiumDto
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumListResponseDto
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumListSummaryDto
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumSummaryDto
import com.policyboss.policybosspro.feature.carInsurance.model.DeclinedInsurer
import com.policyboss.policybosspro.feature.carInsurance.model.EarningsBreakdown
import com.policyboss.policybosspro.feature.carInsurance.model.MotorAddOn
import com.policyboss.policybosspro.feature.carInsurance.model.MotorQuote
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumProgress
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumQuoteList
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import java.util.Locale
import kotlin.math.roundToInt

fun PremiumSummaryDto?.toPremiumProgress(): PremiumProgress = PremiumProgress(
    status = this?.status.orEmpty(),
    total = this?.total ?: 0,
    pending = this?.pending ?: 0,
    success = this?.success ?: 0,
    fail = this?.fail ?: 0
)

fun PremiumListSummaryDto?.toPremiumProgress(): PremiumProgress = PremiumProgress(
    status = this?.status.orEmpty(),
    total = this?.total ?: 0,
    pending = this?.pending ?: 0,
    success = this?.success ?: 0,
    fail = this?.fail ?: 0
)

/**
 * A row is a valid plan only when the insurer reported no error and returned a premium:
 * `Error_Code` blank, `Premium_Breakup` present and `net_premium > 0`.
 */
fun InsurerPremiumDto.isValidPlan(): Boolean =
    errorCode.isNullOrBlank() && (premiumBreakup?.netPremium ?: 0.0) > 0.0

/**
 * `premium_list_db` -> results-screen model. Every valid insurer row becomes one [MotorQuote]
 * (one card per insurer, cheapest first); every other row becomes a [DeclinedInsurer].
 * Brokerage is not part of this API, so it is left at zero.
 */
fun PremiumListResponseDto.toPremiumQuoteList(searchReferenceNumber: String): PremiumQuoteList {
    val minIdv = summary?.idvMin?.roundToInt() ?: 0
    val maxIdv = summary?.idvMax?.roundToInt() ?: 0

    val (validRows, otherRows) = response.partition { it.isValidPlan() }
    val quotes = validRows
        .distinctBy { it.insurerId ?: it.insurer?.insurerId ?: it.serviceLogUniqueId }
        .map { it.toMotorQuote(minIdv, maxIdv) }
        .sortedBy { it.finalPremium }
    val quotedIds = quotes.mapNotNull { it.insurerId }.toSet()
    val declined = otherRows
        .filter { (it.insurerId ?: it.insurer?.insurerId) !in quotedIds }
        .distinctBy { it.insurerId ?: it.insurer?.insurerId }
        .map { it.toDeclinedInsurer(errorMaster) }

    return PremiumQuoteList(
        searchReferenceNumber = summary?.requestUniqueId.orEmpty().ifBlank { searchReferenceNumber },
        crn = summary?.pbCrn?.takeIf { it > 0 }?.toString().orEmpty(),
        progress = summary.toPremiumProgress(),
        quotes = quotes,
        declinedInsurers = declined,
        minIdv = minIdv,
        maxIdv = maxIdv
    )
}

private fun InsurerPremiumDto.toMotorQuote(minIdv: Int, maxIdv: Int): MotorQuote {
    val breakup = premiumBreakup
    val plan = planList.firstOrNull()
    val insurerId = insurerId ?: insurer?.insurerId ?: 0
    val finalPremium = (breakup?.finalPremium ?: 0.0).roundToInt()
    val bundleFinal = (breakup?.rsaCpaBundle?.finalPremium ?: 0.0).roundToInt()

    return MotorQuote(
        id = serviceLogUniqueId.orEmpty()
            .ifBlank { plan?.serviceLogUniqueId.orEmpty() }
            .ifBlank { "INS-$insurerId" },
        insurerName = displayName(),
        insurerLogoType = insurer?.code.orEmpty(),
        insurerLogoUrl = insurer?.logoUrl,
        planName = plan?.planName ?: "TP",
        insurerId = insurerId,
        isRecommended = false,
        finalPremium = finalPremium,
        originalPremium = null,
        zextraDiscountAmount = null,
        zextraBundlePremium = bundleFinal,
        totalEarnings = 0,
        earningsBreakdown = NO_EARNINGS,
        addOns = plan?.addonBreakup.orEmpty().toAddOns(),
        benefits = emptyList(),
        idvValue = customRequest?.get("vehicle_expected_idv").toIntOrZero(),
        minIdv = minIdv,
        maxIdv = maxIdv,
        zextraSwitchCoverPrice = bundleFinal,
        policyDetailsUrl = null,
        buyNowUrl = plan?.proposalUrl
    )
}

private fun InsurerPremiumDto.toDeclinedInsurer(errorMaster: Map<String, String>): DeclinedInsurer {
    val code = errorCode.orEmpty()
    // Error_Msg looks like "PREMIUM :: LM009 :: Vehicle Underwriting Restriction"
    val reason = errorMessage?.substringAfterLast("::")?.trim()?.takeIf { it.isNotBlank() }
        ?: errorMaster[code]
        ?: "Quote not available"
    return DeclinedInsurer(
        insurerId = insurerId ?: insurer?.insurerId ?: 0,
        insurerName = displayName(),
        logoUrl = insurer?.logoUrl,
        errorCode = code,
        reason = reason
    )
}

private fun InsurerPremiumDto.displayName(): String =
    insurer?.code?.takeIf { it.isNotBlank() }
        ?: insurer?.name?.takeIf { it.isNotBlank() }
        ?: "Insurer ${insurerId ?: ""}".trim()

/** Only priced add-ons are shown: `addon_zero_dep_cover: 680` -> "Zero Dep" ₹680. */
private fun Map<String, JsonElement>.toAddOns(): List<MotorAddOn> = mapNotNull { (code, value) ->
    val price = value.toIntOrZero().takeIf { it > 0 } ?: return@mapNotNull null
    MotorAddOn(id = code, name = addonDisplayName(code), price = price, isSelected = true)
}

internal fun addonDisplayName(code: String): String =
    code.removePrefix("addon_").removeSuffix("_cover")
        .split('_').filter { it.isNotBlank() }
        .joinToString(" ") { word -> word.replaceFirstChar { it.titlecase(Locale.ENGLISH) } }

private fun JsonElement?.toIntOrZero(): Int {
    val primitive = this as? JsonPrimitive ?: return 0
    return (primitive.doubleOrNull ?: primitive.content.trim().toDoubleOrNull())?.roundToInt() ?: 0
}

private val NO_EARNINGS = EarningsBreakdown(
    standardBrokerage = 0,
    specialDealTitle = "",
    specialDealAmount = 0,
    extraSyncEarnings = 0,
    extraZextraEarnings = 0
)
