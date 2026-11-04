package com.policyboss.policybosspro.feature.sales

import com.policyboss.policybosspro.feature.sales.model.FilterCategory
import com.policyboss.policybosspro.feature.sales.model.SalesFilterState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SalesFilterTest {

    @Test
    fun testTotalSelectedCount_calculatesAcrossAllCategories() {
        val state = SalesFilterState(
            selectedProducts = setOf("Car", "Health"),
            selectedSubUsers = setOf("Gopal Verma"),
            selectedSubPosps = setOf("Arjun Mehta", "Amit Patel")
        )

        assertEquals(5, state.totalSelectedCount)
    }

    @Test
    fun testFilteredSubUsers_matchesSearchQueryCaseInsensitively() {
        val state = SalesFilterState(
            availableSubUsers = listOf("Gopal Verma", "Ananya Singh", "Ravi Kumar", "Sofia Patel"),
            subUserSearchQuery = "an"
        )

        val filtered = state.filteredSubUsers
        assertEquals(1, filtered.size)
        assertEquals("Ananya Singh", filtered[0])
    }

    @Test
    fun testFilteredSubPosps_returnsAllWhenSearchQueryBlank() {
        val posps = listOf("Arjun Mehta", "Karan Singh", "Ravi Kumar")
        val state = SalesFilterState(
            availableSubPosps = posps,
            subPospSearchQuery = "   "
        )

        assertEquals(posps, state.filteredSubPosps)
    }

    @Test
    fun testCategorySwitching_preservesSelectedItems() {
        var state = SalesFilterState(
            selectedCategory = FilterCategory.PRODUCT,
            selectedProducts = setOf("Car")
        )

        state = state.copy(
            selectedCategory = FilterCategory.SUB_USER,
            selectedSubUsers = setOf("Gopal Verma")
        )

        assertTrue(state.selectedProducts.contains("Car"))
        assertTrue(state.selectedSubUsers.contains("Gopal Verma"))
        assertEquals(2, state.totalSelectedCount)
    }
}
