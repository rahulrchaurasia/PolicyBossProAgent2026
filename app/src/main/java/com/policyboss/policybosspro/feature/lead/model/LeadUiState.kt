package com.policyboss.policybosspro.feature.lead.model

import com.policyboss.policybosspro.feature.home.model.home.HomeLead
import kotlin.math.roundToInt

enum class FilterCategory(val displayName: String) {
    STAGE("Stage"),
    VEHICLE_TYPE("Vehicle Type"),
    RENEWAL_TIME("Renewal Time"),
    EARNINGS("Earnings"),
    RELATIONSHIP("Relationship"),
    SORT_BY("Sort By")
}

enum class LeadSortOption(val displayName: String) {
    HIGHEST_EARNINGS("Highest Earnings"),
    UPCOMING_RENEWAL("Upcoming Renewal"),
    RECENTLY_ADDED("Recently Added"),
    LOWEST_PREMIUM("Lowest Premium"),
    HIGHEST_DISCOUNT("Highest Discount")
}

data class FilterCriteria(
    val stages: Set<String> = emptySet(),
    val vehicleTypes: Set<String> = emptySet(),
    val renewalTimes: Set<String> = emptySet(),
    val earningsRange: ClosedFloatingPointRange<Float> = DEFAULT_EARNINGS_RANGE,
    val relationships: Set<String> = emptySet(),
    val sortBy: LeadSortOption = LeadSortOption.HIGHEST_EARNINGS
) {
    companion object {
        val DEFAULT_EARNINGS_RANGE: ClosedFloatingPointRange<Float> = 100f..5000f
    }

    val hasCustomEarningsFilter: Boolean
        get() = earningsRange.start.roundToInt() != DEFAULT_EARNINGS_RANGE.start.roundToInt() ||
                earningsRange.endInclusive.roundToInt() != DEFAULT_EARNINGS_RANGE.endInclusive.roundToInt()

    val totalActiveFiltersCount: Int
        get() = stages.size +
                vehicleTypes.size +
                renewalTimes.size +
                relationships.size +
                if (hasCustomEarningsFilter) 1 else 0
}

data class LeadUiState(
    val isLoading: Boolean = false,
    val isOnline: Boolean = true,
    val isRefreshing: Boolean = false,
    val isInitialLoadCompleted: Boolean = false,
    val error: String? = null,
    val allLeads: List<HomeLead> = emptyList(),

    val selectedTab: String = "All Leads",
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val activeFilterCategory: FilterCategory = FilterCategory.STAGE,
    val filterCriteria: FilterCriteria = FilterCriteria(),
    val tempFilterCriteria: FilterCriteria = FilterCriteria(),
    val showFilterSheet: Boolean = false,
    val showSortDropdown: Boolean = false,

    val selectedLead: HomeLead? = null,
    val activeDialog: LeadDialogType? = null
) {
    val hasCachedData: Boolean
        get() = allLeads.isNotEmpty()

    val isOffline: Boolean
        get() = !isOnline

    val showNoInternetScreen: Boolean
        get() = !isOnline && !hasCachedData && (error != null || !isInitialLoadCompleted) && !isLoading

    val filteredLeads: List<HomeLead>
        get() {
            val trimmedQuery = searchQuery.trim()
            val minEarnings = filterCriteria.earningsRange.start.roundToInt()
            val maxEarnings = filterCriteria.earningsRange.endInclusive.roundToInt()

            val filtered = allLeads.filter { lead ->
                val matchesTab = when {
                    selectedTab.equals("Renewals", ignoreCase = true) ->
                        lead.leadType.equals("Renewal", ignoreCase = true)
                    selectedTab.equals("Synced contacts", ignoreCase = true) ->
                        lead.leadType.equals("Sync", ignoreCase = true) ||
                                lead.leadType.contains("Synced", ignoreCase = true)
                    selectedTab.equals("Added by me", ignoreCase = true) ->
                        lead.leadType.equals("Added by me", ignoreCase = true) ||
                                lead.leadType.equals("Manual", ignoreCase = true)
                    else -> true
                }

                val matchesSearch = trimmedQuery.isEmpty() ||
                        lead.name.contains(trimmedQuery, ignoreCase = true) ||
                        lead.vehicleModel.contains(trimmedQuery, ignoreCase = true) ||
                        lead.registrationNumber.contains(trimmedQuery, ignoreCase = true)

                val matchesStage = filterCriteria.stages.isEmpty() ||
                        filterCriteria.stages.any { matchesStageFilter(lead.status, it) }

                val matchesVehicle = filterCriteria.vehicleTypes.isEmpty() ||
                        filterCriteria.vehicleTypes.any { matchesVehicleFilter(lead.vehicleType, it) }

                val matchesRenewal = filterCriteria.renewalTimes.isEmpty() ||
                        filterCriteria.renewalTimes.any { matchesRenewalTimeFilter(lead.expiryText, it) }

                val leadEarnings = extractEarningsAmount(lead)
                val matchesEarnings = !filterCriteria.hasCustomEarningsFilter ||
                        leadEarnings in minEarnings..maxEarnings

                val matchesRelationship = filterCriteria.relationships.isEmpty() ||
                        filterCriteria.relationships.any { matchesRelationshipFilter(lead.relationCategory, it) }

                matchesTab &&
                        matchesSearch &&
                        matchesStage &&
                        matchesVehicle &&
                        matchesRenewal &&
                        matchesEarnings &&
                        matchesRelationship
            }

            return when (filterCriteria.sortBy) {
                LeadSortOption.HIGHEST_EARNINGS ->
                    filtered.sortedByDescending { extractEarningsAmount(it) }
                LeadSortOption.UPCOMING_RENEWAL ->
                    filtered.sortedBy { extractExpiryDays(it.expiryText) ?: Int.MAX_VALUE }
                LeadSortOption.RECENTLY_ADDED ->
                    filtered.sortedByDescending {
                        if (it.leadType.equals("Added by me", ignoreCase = true) ||
                            it.leadType.equals("Manual", ignoreCase = true)
                        ) 1 else 0
                    }
                LeadSortOption.LOWEST_PREMIUM ->
                    filtered.sortedBy { it.minPremium.filter(Char::isDigit).toIntOrNull() ?: Int.MAX_VALUE }
                LeadSortOption.HIGHEST_DISCOUNT ->
                    filtered.sortedByDescending { it.specialDealsCount }
            }
        }
}

enum class LeadDialogType {
    MARK_LOST,
    MARK_SOLD,
    ADD_RELATIONSHIP,
    EDIT_DETAILS
}

private val EXPIRY_DAYS_REGEX = Regex("(\\d+)\\s*(?:d\\b|day|days)", RegexOption.IGNORE_CASE)
private val TRAILING_COUNT_REGEX = Regex("\\s*\\(\\d+\\)$")

internal fun extractExpiryDays(expiryText: String): Int? =
    EXPIRY_DAYS_REGEX.find(expiryText)?.groupValues?.getOrNull(1)?.toIntOrNull()

internal fun extractEarningsAmount(lead: HomeLead): Int {
    val raw = lead.maxBrokerage.ifBlank { lead.brokerageAmount }.ifBlank { lead.minPremium }
    return raw.filter(Char::isDigit).toIntOrNull() ?: 0
}

private fun matchesStageFilter(leadStatus: String, stageFilter: String): Boolean {
    return when {
        stageFilter.equals("Fresh/New", ignoreCase = true) ->
            leadStatus.equals("New", ignoreCase = true) || leadStatus.equals("Fresh", ignoreCase = true)
        stageFilter.equals("Ongoing", ignoreCase = true) ->
            leadStatus.equals("In Progress", ignoreCase = true) || leadStatus.equals("Ongoing", ignoreCase = true)
        else -> leadStatus.equals(stageFilter, ignoreCase = true)
    }
}

private fun matchesVehicleFilter(leadVehicleType: String, vehicleFilter: String): Boolean {
    return if (vehicleFilter.equals("CV", ignoreCase = true)) {
        leadVehicleType.equals("Commercial Vehicle", ignoreCase = true) ||
                leadVehicleType.equals("CV", ignoreCase = true)
    } else {
        leadVehicleType.equals(vehicleFilter, ignoreCase = true)
    }
}

private fun matchesRenewalTimeFilter(expiryText: String, timeFilter: String): Boolean {
    val days = extractExpiryDays(expiryText) ?: return false
    return when (timeFilter) {
        "7 days" -> days in 0..7
        "7-14 days" -> days in 8..14
        "14+ days" -> days > 14
        else -> expiryText.contains(timeFilter, ignoreCase = true)
    }
}

private fun matchesRelationshipFilter(relationCategory: String?, filterLabel: String): Boolean {
    val category = relationCategory?.trim().orEmpty()
    if (category.isEmpty()) return false
    val token = filterLabel.replace(TRAILING_COUNT_REGEX, "").trim()
    return when {
        token.equals("Friends", ignoreCase = true) ->
            category.contains("Friend", ignoreCase = true)
        token.equals("Random/Others", ignoreCase = true) ->
            category.contains("Random", ignoreCase = true) || category.contains("Other", ignoreCase = true)
        else -> category.contains(token, ignoreCase = true)
    }
}
