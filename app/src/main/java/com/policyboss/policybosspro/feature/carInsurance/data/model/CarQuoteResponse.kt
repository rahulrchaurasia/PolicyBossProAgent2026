package com.policyboss.policybosspro.feature.carInsurance.data.model

import com.policyboss.policybosspro.feature.carInsurance.model.EarningsBreakdown
import com.policyboss.policybosspro.feature.carInsurance.model.MotorAddOn
import com.policyboss.policybosspro.feature.carInsurance.model.MotorQuote
import com.policyboss.policybosspro.feature.carInsurance.model.MotorQuoteDefaults
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CarQuoteResponse(
    @SerialName("status")
    val status: String? = null,
    @SerialName("message")
    val message: String? = null,
    @SerialName("quote_id")
    val quoteId: String? = null,
    @SerialName("quote_url")
    val quoteUrl: String? = null,
    @SerialName("quotes_count")
    val quotesCount: Int? = 0,
    @SerialName("min_premium")
    val minPremium: Int? = null,
    @SerialName("max_earnings")
    val maxEarnings: Int? = null,
    @SerialName("cpa_description")
    val cpaDescription: String? = null,
    @SerialName("quotes")
    val quotes: List<MotorQuoteDto>? = null
) {
    fun toDomainQuotes(): List<MotorQuote> {
        val dtoList = quotes
        if (dtoList.isNullOrEmpty()) {
            return MotorQuoteDefaults.generateDefaultQuotes()
        }
        return dtoList.map { dto ->
            MotorQuote(
                id = dto.id ?: "QUOTE-${System.currentTimeMillis() % 10000}",
                insurerName = dto.insurerName ?: "General Insurance",
                insurerLogoType = dto.insurerLogoType ?: "acko",
                isRecommended = dto.isRecommended ?: false,
                finalPremium = dto.finalPremium ?: 6700,
                originalPremium = dto.originalPremium ?: 7300,
                zextraDiscountAmount = dto.zextraDiscountAmount ?: 606,
                totalEarnings = dto.totalEarnings ?: 1403,
                earningsBreakdown = dto.earningsBreakdown?.toDomain() ?: EarningsBreakdown(),
                addOns = dto.addOns?.map { it.toDomain() } ?: emptyList(),
                benefits = dto.benefits ?: listOf("Claim settlement ratio: 91%", "10+ cashless garages"),
                idvValue = dto.idvValue ?: 733600,
                zextraSwitchCoverPrice = dto.zextraSwitchCoverPrice ?: 675,
                policyDetailsUrl = dto.policyDetailsUrl ?: quoteUrl,
                buyNowUrl = dto.buyNowUrl ?: quoteUrl
            )
        }
    }
}

@Serializable
data class MotorQuoteDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("insurer_name")
    val insurerName: String? = null,
    @SerialName("insurer_logo_type")
    val insurerLogoType: String? = null,
    @SerialName("is_recommended")
    val isRecommended: Boolean? = false,
    @SerialName("final_premium")
    val finalPremium: Int? = null,
    @SerialName("original_premium")
    val originalPremium: Int? = null,
    @SerialName("zextra_discount_amount")
    val zextraDiscountAmount: Int? = null,
    @SerialName("total_earnings")
    val totalEarnings: Int? = null,
    @SerialName("earnings_breakdown")
    val earningsBreakdown: EarningsBreakdownDto? = null,
    @SerialName("add_ons")
    val addOns: List<MotorAddOnDto>? = null,
    @SerialName("benefits")
    val benefits: List<String>? = null,
    @SerialName("idv_value")
    val idvValue: Int? = null,
    @SerialName("zextra_switch_cover_price")
    val zextraSwitchCoverPrice: Int? = null,
    @SerialName("policy_details_url")
    val policyDetailsUrl: String? = null,
    @SerialName("buy_now_url")
    val buyNowUrl: String? = null
)

@Serializable
data class MotorAddOnDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("price")
    val price: Int? = null,
    @SerialName("is_selected")
    val isSelected: Boolean? = true
) {
    fun toDomain(): MotorAddOn = MotorAddOn(
        id = id ?: "ADDON-${System.currentTimeMillis() % 1000}",
        name = name ?: "Add-on",
        price = price ?: 0,
        isSelected = isSelected ?: true
    )
}

@Serializable
data class EarningsBreakdownDto(
    @SerialName("standard_brokerage")
    val standardBrokerage: Int? = 1000,
    @SerialName("special_deal_title")
    val specialDealTitle: String? = "Special Deal By HDFC",
    @SerialName("special_deal_amount")
    val specialDealAmount: Int? = 103,
    @SerialName("extra_sync_earnings")
    val extraSyncEarnings: Int? = 200,
    @SerialName("extra_zextra_earnings")
    val extraZextraEarnings: Int? = 100
) {
    fun toDomain(): EarningsBreakdown = EarningsBreakdown(
        standardBrokerage = standardBrokerage ?: 1000,
        specialDealTitle = specialDealTitle ?: "Special Deal By HDFC",
        specialDealAmount = specialDealAmount ?: 103,
        extraSyncEarnings = extraSyncEarnings ?: 200,
        extraZextraEarnings = extraZextraEarnings ?: 100
    )
}
