package com.policyboss.policybosspro.feature.carInsurance.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class MotorQuote(
    val id: String,
    val insurerName: String,
    val insurerLogoType: String = "acko", // "acko", "tata", "hdfc", "icici", "bajaj"
    val isRecommended: Boolean = false,
    val finalPremium: Int,
    val originalPremium: Int? = null,
    val zextraDiscountAmount: Int? = null,
    val totalEarnings: Int = 1403,
    val earningsBreakdown: EarningsBreakdown = EarningsBreakdown(),
    val addOns: List<MotorAddOn> = emptyList(),
    val benefits: List<String> = emptyList(),
    val idvValue: Int = 733600,
    val minIdv: Int = 71414,
    val maxIdv: Int = 234780,
    val zextraSwitchCoverPrice: Int = 675,
    val policyDetailsUrl: String? = null,
    val buyNowUrl: String? = null
) {
    val idvFormatted: String
        get() = formatIndianCurrency(idvValue)

    fun effectivePremium(isZextraEnabled: Boolean): Int {
        return if (isZextraEnabled) {
            finalPremium
        } else {
            // When Zextra is off, price does not include Zextra discount
            (originalPremium ?: finalPremium).coerceAtLeast(finalPremium)
        }
    }

    fun effectiveEarnings(isZextraEnabled: Boolean): Int {
        return if (isZextraEnabled) {
            totalEarnings
        } else {
            (totalEarnings - earningsBreakdown.extraZextraEarnings).coerceAtLeast(0)
        }
    }

    companion object {
        fun formatIndianCurrency(amount: Int): String {
            val s = amount.toString()
            if (s.length <= 3) return s
            val lastThree = s.substring(s.length - 3)
            val rest = s.substring(0, s.length - 3)
            val formattedRest = rest.reversed().chunked(2).joinToString(",").reversed()
            return "$formattedRest,$lastThree"
        }
    }
}

@Immutable
@Serializable
data class MotorAddOn(
    val id: String,
    val name: String,
    val price: Int,
    val isSelected: Boolean = true
)

@Immutable
@Serializable
data class EarningsBreakdown(
    val standardBrokerage: Int = 1000,
    val specialDealTitle: String = "Special Deal By HDFC",
    val specialDealAmount: Int = 103,
    val extraSyncEarnings: Int = 200,
    val extraZextraEarnings: Int = 100
) {
    val total: Int
        get() = standardBrokerage + specialDealAmount + extraSyncEarnings + extraZextraEarnings
}

enum class MotorQuoteSortOption(val displayName: String) {
    RECOMMENDED("Recommended"),
    LOWEST_PREMIUM("Lowest Premium"),
    HIGHEST_EARNINGS("Highest Earnings"),
    BEST_CLAIM_RATIO("Best Claim Settlement")
}

object MotorQuoteDefaults {

    fun generateDefaultQuotes(): List<MotorQuote> = listOf(
        MotorQuote(
            id = "ACKO-01",
            insurerName = "Acko General Insurance",
            insurerLogoType = "acko",
            isRecommended = true,
            finalPremium = 6700,
            originalPremium = 7300,
            zextraDiscountAmount = 606,
            totalEarnings = 1403,
            earningsBreakdown = EarningsBreakdown(
                standardBrokerage = 1000,
                specialDealTitle = "Special Deal By HDFC",
                specialDealAmount = 103,
                extraSyncEarnings = 200,
                extraZextraEarnings = 100
            ),
            addOns = listOf(
                MotorAddOn("1", "Zextra Switch Cover", 375, isSelected = true),
                MotorAddOn("2", "Zero Depreciation", 5132, isSelected = true),
                MotorAddOn("3", "Consumables", 323, isSelected = true),
                MotorAddOn("4", "Key Cover", 100, isSelected = true),
                MotorAddOn("5", "Loss of Personal Belongings", 500, isSelected = true)
            ),
            benefits = listOf(
                "Claim settlement ratio: 91%",
                "10+ cashless garages"
            ),
            idvValue = 733600,
            zextraSwitchCoverPrice = 675,
            policyDetailsUrl = "https://www.policyboss.com/motor/acko/policy-details",
            buyNowUrl = "https://www.policyboss.com/motor/acko/buy-now"
        ),
        MotorQuote(
            id = "ACKO-02",
            insurerName = "Acko General Insurance",
            insurerLogoType = "acko",
            isRecommended = false,
            finalPremium = 6700,
            originalPremium = 7300,
            zextraDiscountAmount = 606,
            totalEarnings = 1403,
            earningsBreakdown = EarningsBreakdown(
                standardBrokerage = 1000,
                specialDealTitle = "Special Deal By HDFC",
                specialDealAmount = 103,
                extraSyncEarnings = 200,
                extraZextraEarnings = 100
            ),
            addOns = listOf(
                MotorAddOn("1", "Zextra Switch Cover", 375, isSelected = true),
                MotorAddOn("2", "Zero Depreciation", 5132, isSelected = true),
                MotorAddOn("3", "Consumables", 323, isSelected = true),
                MotorAddOn("4", "Key Cover", 100, isSelected = true),
                MotorAddOn("5", "Loss of Personal Belongings", 500, isSelected = true)
            ),
            benefits = listOf(
                "Claim settlement ratio: 91%",
                "10+ cashless garages"
            ),
            idvValue = 733600,
            zextraSwitchCoverPrice = 675,
            policyDetailsUrl = "https://www.policyboss.com/motor/acko/policy-details",
            buyNowUrl = "https://www.policyboss.com/motor/acko/buy-now"
        ),
        MotorQuote(
            id = "TATA-01",
            insurerName = "Tata AIG General Insurance",
            insurerLogoType = "tata",
            isRecommended = false,
            finalPremium = 6403,
            originalPremium = 7050,
            zextraDiscountAmount = 647,
            totalEarnings = 1380,
            earningsBreakdown = EarningsBreakdown(
                standardBrokerage = 980,
                specialDealTitle = "Special Deal By Tata",
                specialDealAmount = 120,
                extraSyncEarnings = 180,
                extraZextraEarnings = 100
            ),
            addOns = listOf(
                MotorAddOn("1", "Zextra Switch Cover", 375, isSelected = true),
                MotorAddOn("2", "Zero Depreciation", 4890, isSelected = true),
                MotorAddOn("3", "Consumables", 299, isSelected = true),
                MotorAddOn("4", "Roadside Assistance", 199, isSelected = true),
                MotorAddOn("5", "Engine Protection", 650, isSelected = true)
            ),
            benefits = listOf(
                "Claim settlement ratio: 96%",
                "15+ cashless garages"
            ),
            idvValue = 733600,
            zextraSwitchCoverPrice = 650,
            policyDetailsUrl = "https://www.policyboss.com/motor/tata/policy-details",
            buyNowUrl = "https://www.policyboss.com/motor/tata/buy-now"
        ),
        MotorQuote(
            id = "HDFC-01",
            insurerName = "HDFC ERGO General Insurance",
            insurerLogoType = "hdfc",
            isRecommended = false,
            finalPremium = 6850,
            originalPremium = 7450,
            zextraDiscountAmount = 600,
            totalEarnings = 1420,
            earningsBreakdown = EarningsBreakdown(
                standardBrokerage = 1050,
                specialDealTitle = "Special Deal By HDFC",
                specialDealAmount = 120,
                extraSyncEarnings = 150,
                extraZextraEarnings = 100
            ),
            addOns = listOf(
                MotorAddOn("1", "Zextra Switch Cover", 375, isSelected = true),
                MotorAddOn("2", "Zero Depreciation", 5200, isSelected = true),
                MotorAddOn("3", "Consumables", 350, isSelected = true)
            ),
            benefits = listOf(
                "Claim settlement ratio: 98%",
                "25+ cashless garages"
            ),
            idvValue = 745000,
            zextraSwitchCoverPrice = 680,
            policyDetailsUrl = "https://www.policyboss.com/motor/hdfc/policy-details",
            buyNowUrl = "https://www.policyboss.com/motor/hdfc/buy-now"
        ),
        MotorQuote(
            id = "ICICI-01",
            insurerName = "ICICI Lombard General Insurance",
            insurerLogoType = "icici",
            isRecommended = false,
            finalPremium = 6920,
            originalPremium = 7520,
            zextraDiscountAmount = 600,
            totalEarnings = 1360,
            earningsBreakdown = EarningsBreakdown(
                standardBrokerage = 960,
                specialDealTitle = "Special Deal By ICICI",
                specialDealAmount = 100,
                extraSyncEarnings = 200,
                extraZextraEarnings = 100
            ),
            addOns = listOf(
                MotorAddOn("1", "Zextra Switch Cover", 375, isSelected = true),
                MotorAddOn("2", "Zero Depreciation", 5100, isSelected = true),
                MotorAddOn("3", "Key Replacement", 150, isSelected = true)
            ),
            benefits = listOf(
                "Claim settlement ratio: 94%",
                "18+ cashless garages"
            ),
            idvValue = 730000,
            zextraSwitchCoverPrice = 675,
            policyDetailsUrl = "https://www.policyboss.com/motor/icici/policy-details",
            buyNowUrl = "https://www.policyboss.com/motor/icici/buy-now"
        ),
        MotorQuote(
            id = "BAJAJ-01",
            insurerName = "Bajaj Allianz General Insurance",
            insurerLogoType = "bajaj",
            isRecommended = false,
            finalPremium = 7050,
            originalPremium = 7650,
            zextraDiscountAmount = 600,
            totalEarnings = 1340,
            earningsBreakdown = EarningsBreakdown(
                standardBrokerage = 950,
                specialDealTitle = "Special Deal By Bajaj",
                specialDealAmount = 90,
                extraSyncEarnings = 200,
                extraZextraEarnings = 100
            ),
            addOns = listOf(
                MotorAddOn("1", "Zero Depreciation", 5300, isSelected = true),
                MotorAddOn("2", "Consumables", 340, isSelected = true)
            ),
            benefits = listOf(
                "Claim settlement ratio: 95%",
                "12+ cashless garages"
            ),
            idvValue = 733600,
            zextraSwitchCoverPrice = 670,
            policyDetailsUrl = "https://www.policyboss.com/motor/bajaj/policy-details",
            buyNowUrl = "https://www.policyboss.com/motor/bajaj/buy-now"
        ),
        MotorQuote(
            id = "RELIANCE-01",
            insurerName = "Reliance General Insurance",
            insurerLogoType = "reliance",
            isRecommended = false,
            finalPremium = 7120,
            originalPremium = 7700,
            zextraDiscountAmount = 580,
            totalEarnings = 1320,
            earningsBreakdown = EarningsBreakdown(
                standardBrokerage = 940,
                specialDealTitle = "Special Deal By Reliance",
                specialDealAmount = 80,
                extraSyncEarnings = 200,
                extraZextraEarnings = 100
            ),
            addOns = listOf(
                MotorAddOn("1", "Zero Depreciation", 5150, isSelected = true),
                MotorAddOn("2", "Roadside Assistance", 200, isSelected = true)
            ),
            benefits = listOf(
                "Claim settlement ratio: 93%",
                "10+ cashless garages"
            ),
            idvValue = 733600,
            zextraSwitchCoverPrice = 660,
            policyDetailsUrl = "https://www.policyboss.com/motor/reliance/policy-details",
            buyNowUrl = "https://www.policyboss.com/motor/reliance/buy-now"
        ),
        MotorQuote(
            id = "SBI-01",
            insurerName = "SBI General Insurance",
            insurerLogoType = "sbi",
            isRecommended = false,
            finalPremium = 7200,
            originalPremium = 7800,
            zextraDiscountAmount = 600,
            totalEarnings = 1310,
            earningsBreakdown = EarningsBreakdown(
                standardBrokerage = 930,
                specialDealTitle = "Special Deal By SBI",
                specialDealAmount = 80,
                extraSyncEarnings = 200,
                extraZextraEarnings = 100
            ),
            addOns = listOf(
                MotorAddOn("1", "Zero Depreciation", 5250, isSelected = true),
                MotorAddOn("2", "Key Cover", 120, isSelected = true)
            ),
            benefits = listOf(
                "Claim settlement ratio: 92%",
                "14+ cashless garages"
            ),
            idvValue = 733600,
            zextraSwitchCoverPrice = 675,
            policyDetailsUrl = "https://www.policyboss.com/motor/sbi/policy-details",
            buyNowUrl = "https://www.policyboss.com/motor/sbi/buy-now"
        )
    )
}

