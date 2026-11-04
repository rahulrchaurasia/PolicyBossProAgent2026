package com.policyboss.policybosspro.feature.sales.model

data class InsuredDetails(
    val title: String,
    val vahanData: String,
    val quoteOn: String,
    val saleOn: String,
    val crn: String,
    val cs: String,
    val zaxtra: String = "NA",
    val premiumLevel: String = "Level 1",
    val brokerageLevel: String = "Level 1",
    val saleDuration: String = "7 Minute",
    val sync: String = "No",
    val renew: String = "YES",
    val source: String = "POSP-POSP-AGENCY",
    val insurer: String = "Edelweiss",
    val customer: String = "Suresh Bandhaiya",
    val reporting: String = "Umesh Pawar Uid-120572",
    val ssId: String = "122378"
)

data class SalePolicy(
    val id: String,
    val customerName: String,
    val customerBadge: String? = "Friend",
    val insuranceCategory: String = "Car Insurance",
    val vehicleModel: String = "HONDA CITY INDIA (P) LTD: AC...",
    val detailsVehicleModel: String = "Maruti Suzuki Baleno",
    val agentName: String = "Rashmi Shah",
    val subUserName: String = "Mukesh Agarwal",
    val premiumAmount: String = "₹6,500",
    val bossCoins: String = "25+",
    val totalEarnings: String = "1,403",
    val standardBrokerage: String = "₹1,000",
    val specialDeal: String? = "₹103",
    val specialDealBy: String? = "Special Deal By HDFC",
    val extraSyncEarnings: String? = "₹200",
    val zaxtraEarnings: String? = "₹100 ↑",
    val customerDetailsId: String = "19251610",
    val insuredDetails: InsuredDetails
)

enum class FilterCategory(val displayName: String) {
    PRODUCT("Product"),
    SUB_USER("Sub User"),
    SUB_POSP("Sub POSP")
}

data class SalesFilterState(
    val selectedCategory: FilterCategory = FilterCategory.PRODUCT,
    val availableProducts: List<String> = emptyList(),
    val availableSubUsers: List<String> = emptyList(),
    val availableSubPosps: List<String> = emptyList(),
    val selectedProducts: Set<String> = emptySet(),
    val selectedSubUsers: Set<String> = emptySet(),
    val selectedSubPosps: Set<String> = emptySet(),
    val subUserSearchQuery: String = "",
    val subPospSearchQuery: String = ""
) {
    val totalSelectedCount: Int
        get() = selectedProducts.size + selectedSubUsers.size + selectedSubPosps.size

    val filteredSubUsers: List<String>
        get() = if (subUserSearchQuery.isBlank()) {
            availableSubUsers
        } else {
            availableSubUsers.filter { it.contains(subUserSearchQuery.trim(), ignoreCase = true) }
        }

    val filteredSubPosps: List<String>
        get() = if (subPospSearchQuery.isBlank()) {
            availableSubPosps
        } else {
            availableSubPosps.filter { it.contains(subPospSearchQuery.trim(), ignoreCase = true) }
        }
}

data class SalesUiState(
    val isLoading: Boolean = false,
    val dateRange: String = "1 Jul, 26 - 31 Jul, 26",
    val showDateRangeSelector: Boolean = false,
    val totalSalesAmount: String = "₹10,00,000",
    val resultsCount: Int = 24,
    val policies: List<SalePolicy> = emptyList(),
    val allPolicies: List<SalePolicy> = emptyList(),
    val filterState: SalesFilterState = SalesFilterState(),
    val appliedFilterState: SalesFilterState = SalesFilterState(),
    val errorMessage: String? = null
)

sealed interface SalesAction {
    object ToggleDateRangeSelector : SalesAction
    data class ApplyDateRange(val range: String) : SalesAction

    // Filter Actions
    object OpenFilter : SalesAction
    object CloseFilter : SalesAction
    data class SelectFilterCategory(val category: FilterCategory) : SalesAction
    data class ToggleProductFilter(val product: String) : SalesAction
    data class ToggleSubUserFilter(val subUser: String) : SalesAction
    data class ToggleSubPospFilter(val subPosp: String) : SalesAction
    data class UpdateSubUserSearch(val query: String) : SalesAction
    data class UpdateSubPospSearch(val query: String) : SalesAction
    object ClearFilterSelection : SalesAction
    object ApplyFilters : SalesAction
}

data class PolicyDetailsUiState(
    val isLoading: Boolean = false,
    val policy: SalePolicy? = null,
    val errorMessage: String? = null
)
