package com.policyboss.policybosspro.feature.carInsurance.ui.carPolicyDetail.model

import androidx.compose.runtime.Immutable
import com.policyboss.policybosspro.feature.carInsurance.model.MotorQuote
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiState
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class PolicyVehicleInfo(
    val carModel: String,
    /** Null for TP-only: a third-party policy has no car value. */
    val idv: Int?,
    val crn: String,
    val regDate: String,
    /** Null for TP-only: NCB applies to own damage only. */
    val newNcb: String?,
    val planName: String
)

@Immutable
@Serializable
data class PremiumBreakupItem(
    val label: String,
    val amount: Int,
    val isDeduction: Boolean = false
) {
    val formattedAmount: String
        get() = "${if (isDeduction && amount > 0) "- " else ""}₹${MotorQuote.formatIndianCurrency(amount)}"
}

@Immutable
@Serializable
data class PremiumBreakupGroup(
    val id: String,
    val categoryCaption: String,
    val title: String,
    val totalAmount: Int,
    val items: List<PremiumBreakupItem> = emptyList(),
    val isExpandable: Boolean = true,
    val standaloneItem: PremiumBreakupItem? = null
) {
    val formattedTotalAmount: String
        get() = "₹${MotorQuote.formatIndianCurrency(totalAmount)}"
}

@Immutable
@Serializable
data class PolicyBreakupDetails(
    val insurerName: String,
    val insurerLogoType: String,
    val vehicleInfo: PolicyVehicleInfo,
    /** Own Damage premium with its discounts and NCB; null for TP-only. */
    val baseCoverGroup: PremiumBreakupGroup?,
    val thirdPartyGroup: PremiumBreakupGroup,
    val bundleGroup: PremiumBreakupGroup,
    /** Add-ons; null for TP-only. */
    val addonGroup: PremiumBreakupGroup?,
    val taxesGroup: PremiumBreakupGroup,
    val finalPremium: Int
) {
    val formattedFinalPremium: String
        get() = "₹${MotorQuote.formatIndianCurrency(finalPremium)}"

    companion object {
        fun createSample(
            quote: MotorQuote? = null,
            uiState: NewCarUiState? = null
        ): PolicyBreakupDetails {
            val q = quote ?: MotorQuote(
                id = "TATA-01",
                insurerName = "Tata AIG",
                insurerLogoType = "tata",
                finalPremium = 4122,
                idvValue = 110831,
                zextraSwitchCoverPrice = 400
            )

            val carModelText = run {
                val make = uiState?.make?.ifBlank { null } ?: "Maruti"
                val model = uiState?.model?.ifBlank { null } ?: "Wagon R"
                val fuel = uiState?.fuel?.ifBlank { null } ?: "Petrol"
                val variant = uiState?.variant?.ifBlank { null }
                    ?: uiState?.selectedVariant?.label
                    ?: "1.0 VXI PLUS (998 CC)"
                listOf(make, model, fuel, variant).joinToString(", ")
            }

            val crn = uiState?.crnNumber?.ifBlank { null } ?: "12682505"
            val regDate = uiState?.registrationDate?.ifBlank { null } ?: "18 Oct, 2011"
            val planName = if (q.isRecommended) "GOLD" else "Basic"
            // Third-party-only: no own damage, so no IDV, NCB, OD discounts or add-ons
            val isTpOnly = uiState?.isTpOnlyFilter == true

            val vehicleInfo = PolicyVehicleInfo(
                carModel = carModelText,
                idv = if (isTpOnly) null else q.idvValue,
                crn = crn,
                regDate = regDate,
                newNcb = if (isTpOnly) null else "20%",
                planName = planName
            )

            // Base Cover Breakdown:
            val basicOd = 373
            val odDiscount = 0
            val garageBenefitDiscount = 0
            val ncbDiscount = 75
            val netOd = (basicOd - odDiscount - garageBenefitDiscount - ncbDiscount).coerceAtLeast(0)

            val baseCoverGroup = if (isTpOnly) null else PremiumBreakupGroup(
                id = "base_cover",
                categoryCaption = "Base Cover",
                title = "Own Damage Premium (A)",
                totalAmount = netOd, // 298
                items = listOf(
                    PremiumBreakupItem("Basic OD", basicOd),
                    PremiumBreakupItem("O.D. Discount", odDiscount, isDeduction = true),
                    PremiumBreakupItem("Garage Benefit Discount", garageBenefitDiscount, isDeduction = true)
                ),
                standaloneItem = PremiumBreakupItem("No Claim Bonus", ncbDiscount, isDeduction = true)
            )

            // Third Party Cover Breakdown:
            val tpAmount = 2094
            val thirdPartyGroup = PremiumBreakupGroup(
                id = "third_party",
                categoryCaption = "Third Party Cover",
                // Groups are lettered in order, so Third Party is (A) when there is no Own Damage group
                title = if (isTpOnly) "Third Party Premium (A)" else "Third Party Premium (B)",
                totalAmount = tpAmount,
                items = listOf(
                    PremiumBreakupItem("Basic 3rd Party Premium", tpAmount)
                )
            )

            // Bundle Cover Breakdown:
            val bundleAmount = q.zextraSwitchCoverPrice.coerceAtLeast(400)
            val bundleGroup = PremiumBreakupGroup(
                id = "bundle",
                categoryCaption = "Bundle",
                title = "Bundle Details",
                totalAmount = bundleAmount,
                items = listOf(
                    PremiumBreakupItem("Zextra Switch Cover", bundleAmount)
                )
            )

            // Add-ons Cover Breakdown:
            val addOnItems = if (q.addOns.isNotEmpty()) {
                q.addOns.filter { it.isSelected }.map {
                    PremiumBreakupItem(it.name, it.price)
                }
            } else {
                listOf(
                    PremiumBreakupItem("Zero Depreciation", 501),
                    PremiumBreakupItem("Consumables", 200)
                )
            }
            val addonTotal = if (addOnItems.isNotEmpty()) addOnItems.sumOf { it.amount } else 701
            val addonGroup = if (isTpOnly) null else PremiumBreakupGroup(
                id = "addons",
                categoryCaption = "Addons",
                title = "Addon Details",
                totalAmount = addonTotal,
                items = addOnItems
            )

            // Net Premium = 298 + 2094 + 400 + 701 = 3493
            // GST @ 18% = ~629
            // Final = 4122
            val netPremium = (baseCoverGroup?.totalAmount ?: 0) + tpAmount + bundleAmount + (addonGroup?.totalAmount ?: 0)
            val gstAmount = (netPremium * 0.18).toInt().coerceAtLeast(629)
            val calculatedFinal = netPremium + gstAmount

            val taxesGroup = PremiumBreakupGroup(
                id = "taxes",
                categoryCaption = "Taxes",
                title = "GST@18",
                totalAmount = gstAmount,
                items = emptyList(),
                isExpandable = false
            )

            return PolicyBreakupDetails(
                insurerName = q.insurerName.ifBlank { "Tata AIG" },
                insurerLogoType = q.insurerLogoType,
                vehicleInfo = vehicleInfo,
                baseCoverGroup = baseCoverGroup,
                thirdPartyGroup = thirdPartyGroup,
                bundleGroup = bundleGroup,
                addonGroup = addonGroup,
                taxesGroup = taxesGroup,
                finalPremium = if (q.finalPremium > 0) q.finalPremium else calculatedFinal
            )
        }
    }
}
