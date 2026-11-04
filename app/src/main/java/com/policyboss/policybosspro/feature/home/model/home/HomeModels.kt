package com.policyboss.policybosspro.feature.home.model.home

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable

@Immutable
data class EarningsSummary(
    val thisMonthEarnings: String = "₹20,000",
    val potentialEarnings: String = "₹1,20,000"
)

@Immutable
data class PromoBanner(
    val id: String,
    val tag: String,
    val title: String,
    val buttonText: String,
    val earningTag: String,
    @DrawableRes val imageRes: Int
)

@Immutable
data class HomeMetric(
    val id: String,
    val value: String,
    val label: String,
    val isAlert: Boolean = false
)

@Immutable
data class InsurerQuote(
    val id: String,
    val insurerName: String,
    @DrawableRes val logoRes: Int,
    val premium: String,
    val idv: String,
    val addons: List<String>,
    val brokerage: String
)

@Immutable
enum class RenewalOption(val title: String, val subtitle: String) {
    EXISTING_INSURER("Existing insurer", "A2A: Renew the exact same policy"),
    OTHER_INSURERS("Other insurers", "A2 X Y Z: Explore the best policies")
}

@Immutable
data class HomeLead(
    val id: String,
    val name: String,
    val phoneNumber: String = "9820098200",
    val leadType: String = "Sync",
    val isTopLead: Boolean = true,
    val vehicleType: String = "Car",
    val vehicleModel: String = "Maruti Suzuki Baleno",
    val registrationNumber: String = "MH-047-219",
    val expiryText: String = "Expiring in 3d: 21 May, 26",
    val expiryBadge: String? = null,
    val specialDealsCount: Int = 3,
    val hasSyncReward: Boolean = false,
    val quotesCount: Int = 16,
    val quotesFoundCount: Int = 8,
    val minPremium: String = "₹6,403",
    val maxBrokerage: String = "₹2,500",
    val brokerageAmount: String = "₹2,500",
    val ncbStatus: String = "0%",
    val status: String = "New",
    val relationCategory: String? = null,
    val relationSubCategory: String? = null,
    val quotes: List<InsurerQuote> = emptyList()
)

@Immutable
data class QuickLink(
    val id: String,
    val title: String,
    @DrawableRes val iconRes: Int
)

@Immutable
data class TrustBulletPoint(
    val id: String,
    val text: String
)

