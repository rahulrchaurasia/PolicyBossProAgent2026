package com.policyboss.policybosspro.feature.carInsurance.data.mapper

import com.policyboss.policybosspro.feature.carInsurance.data.model.InsurerPlanDto
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
 * `premium_list_db` -> results-screen model. Every insurer plan becomes one [MotorQuote]
 * (cheapest first); insurers without a plan become [DeclinedInsurer] with their error.
 * Brokerage / Zextra values are not part of this API, so they are left at zero.
 */
fun PremiumListResponseDto.toPremiumQuoteList(searchReferenceNumber: String): PremiumQuoteList {
    val quotes = response
        .flatMap { insurer -> insurer.planList.mapNotNull { plan -> plan.toMotorQuote(insurer) } }
        .sortedBy { it.finalPremium }

    val quotedInsurerIds = response.filter { it.planList.isNotEmpty() }.mapNotNull { it.insurerId }.toSet()
    val declined = response
        .filter { it.planList.isEmpty() && it.insurerId !in quotedInsurerIds }
        .map { it.toDeclinedInsurer(errorMaster) }

    val minIdv = summary?.idvMin?.roundToInt() ?: 0
    val maxIdv = summary?.idvMax?.roundToInt() ?: 0

    return PremiumQuoteList(
        searchReferenceNumber = summary?.requestUniqueId.orEmpty().ifBlank { searchReferenceNumber },
        crn = summary?.pbCrn?.takeIf { it > 0 }?.toString().orEmpty(),
        progress = summary.toPremiumProgress(),
        quotes = quotes.map { it.copy(minIdv = minIdv, maxIdv = maxIdv) },
        declinedInsurers = declined,
        minIdv = minIdv,
        maxIdv = maxIdv
    )
}

private fun InsurerPlanDto.toMotorQuote(insurer: InsurerPremiumDto): MotorQuote? {
    val premium = (fullPremiumBreakup?.finalPremium ?: insurer.premiumBreakup?.finalPremium)
        ?.roundToInt()
        ?.takeIf { it > 0 }
        ?: return null
    val insurerId = insurer.insurerId ?: insurer.insurer?.insurerId ?: 0
    val idv = insurer.customRequest?.get("vehicle_expected_idv").toIntOrZero()

    return MotorQuote(
        id = serviceLogUniqueId.orEmpty().ifBlank { "INS-$insurerId-${planId ?: 0}" },
        insurerName = insurer.displayName(),
        insurerLogoType = insurer.insurer?.code.orEmpty(),
        insurerLogoUrl = insurer.insurer?.logoUrl,
        planName = planName,
        isRecommended = false,
        finalPremium = premium,
        originalPremium = null,
        zextraDiscountAmount = null,
        totalEarnings = 0,
        earningsBreakdown = NO_EARNINGS,
        addOns = addonBreakup.toAddOns(),
        benefits = emptyList(),
        idvValue = idv,
        minIdv = 0,
        maxIdv = 0,
        zextraSwitchCoverPrice = 0,
        policyDetailsUrl = null,
        buyNowUrl = proposalUrl
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
