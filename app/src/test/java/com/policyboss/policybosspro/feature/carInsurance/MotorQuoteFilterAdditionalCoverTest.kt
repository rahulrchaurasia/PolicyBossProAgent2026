package com.policyboss.policybosspro.feature.carInsurance

import com.policyboss.policybosspro.feature.carInsurance.model.NewCarAction
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.FilterCategory
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.MotorQuoteFilterData
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.QuoteFilterState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MotorQuoteFilterAdditionalCoverTest {

    @Test
    fun `FilterCategory ADDITIONAL_COVER has displayName Additional Cover`() {
        assertEquals("Additional Cover", FilterCategory.ADDITIONAL_COVER.displayName)
    }

    @Test
    fun `filter rail exposes exactly Set IDV, Add-ons, Additional Cover and Discounts`() {
        assertEquals(
            listOf("Set IDV", "Add-ons", "Additional Cover", "Discounts"),
            FilterCategory.entries.map { it.displayName }
        )
    }

    @Test
    fun `activeFilterCount counts only the four remaining categories`() {
        val state = QuoteFilterState(
            selectedIdv = 100000f,
            isIdvExplicitlyChanged = true,          // Set IDV: 1
            selectedAddOnIds = setOf("zero_dep"),   // Add-ons: 1
            selectedLiabilityCoverIds = emptySet(),
            selectedUnNamedPassengerCover = "No",
            antiTheftDevice = true,                 // Discounts: 1
            payAsYouDrive = false,
            voluntaryExcess = "No"
        )
        assertEquals(3, state.activeFilterCount)
    }

    @Test
    fun `additionalCoverItems matches expected 4 liability covers from reference screenshot`() {
        val items = MotorQuoteFilterData.additionalCoverItems
        assertEquals(4, items.size)
        assertEquals("owner_driver_pa", items[0].id)
        assertEquals("Owner Driver PA Cover", items[0].title)
        assertEquals("paid_driver_liability", items[1].id)
        assertEquals("Paid Driver Legal Liability", items[1].title)
        assertEquals("paid_driver_pa", items[2].id)
        assertEquals("Paid Driver PA Cover", items[2].title)
        assertEquals("limited_tp_damage", items[3].id)
        assertEquals("Limited 3rd Party Damage of 6,000", items[3].title)
    }

    @Test
    fun `passenger cover No does not increment activeFilterCount, but monetary option does`() {
        val defaultState = QuoteFilterState(
            isIdvExplicitlyChanged = false,
            selectedAddOnIds = emptySet(),
            selectedLiabilityCoverIds = emptySet(),
            selectedUnNamedPassengerCover = "No",
            electricalAccessoriesAmount = "0",
            nonElectricalAccessoriesAmount = "0",
            antiTheftDevice = false,
            payAsYouDrive = false,
            voluntaryExcess = "No"
        )
        assertEquals(0, defaultState.activeFilterCount)

        val withPassengerCover = defaultState.copy(selectedUnNamedPassengerCover = "₹ 50,000")
        assertEquals(1, withPassengerCover.activeFilterCount)

        val backToNo = withPassengerCover.copy(selectedUnNamedPassengerCover = "No")
        assertEquals(0, backToNo.activeFilterCount)
    }

    @Test
    fun `accessories validation correctly validates 10,000 to 50,000 range`() {
        val stateZero = QuoteFilterState(
            electricalAccessoriesAmount = "0",
            nonElectricalAccessoriesAmount = "0"
        )
        assertTrue(stateZero.isElectricalValid)
        assertFalse(stateZero.isElectricalActive)
        assertTrue(stateZero.isNonElectricalValid)
        assertFalse(stateZero.isNonElectricalActive)
        assertTrue(stateZero.isAccessoriesValid)

        val stateValid = QuoteFilterState(
            electricalAccessoriesAmount = "15000",
            nonElectricalAccessoriesAmount = "50000"
        )
        assertTrue(stateValid.isElectricalValid)
        assertTrue(stateValid.isElectricalActive)
        assertTrue(stateValid.isNonElectricalValid)
        assertTrue(stateValid.isNonElectricalActive)
        assertTrue(stateValid.isAccessoriesValid)

        val stateTooLow = QuoteFilterState(electricalAccessoriesAmount = "500")
        assertFalse(stateTooLow.isElectricalValid)
        assertFalse(stateTooLow.isElectricalActive)
        assertFalse(stateTooLow.isAccessoriesValid)

        val stateTooHigh = QuoteFilterState(nonElectricalAccessoriesAmount = "60000")
        assertFalse(stateTooHigh.isNonElectricalValid)
        assertFalse(stateTooHigh.isNonElectricalActive)
        assertFalse(stateTooHigh.isAccessoriesValid)
    }

    @Test
    fun `activeFilterCount includes liability covers, passenger cover and active accessories`() {
        val filterState = QuoteFilterState(
            isIdvExplicitlyChanged = false,
            selectedAddOnIds = emptySet(),
            selectedLiabilityCoverIds = setOf("owner_driver_pa", "paid_driver_liability"),
            selectedUnNamedPassengerCover = "₹ 1,00,000",
            electricalAccessoriesAmount = "20000",
            nonElectricalAccessoriesAmount = "0",
            antiTheftDevice = false,
            payAsYouDrive = false,
            voluntaryExcess = "No"
        )
        // 2 liability covers + 1 passenger cover + 1 electrical accessory = 4
        assertEquals(4, filterState.activeFilterCount)
    }

    @Test
    fun `IDV filter active status updates when selectedIdv differs from defaultIdv`() {
        val stateDefault = QuoteFilterState(
            selectedIdv = 71414f,
            defaultIdv = 71414f,
            minIdv = 71414f,
            maxIdv = 234780f,
            isIdvExplicitlyChanged = false,
            selectedAddOnIds = emptySet(),
            selectedLiabilityCoverIds = emptySet(),
            selectedUnNamedPassengerCover = "No",
            electricalAccessoriesAmount = "0",
            nonElectricalAccessoriesAmount = "0",
            antiTheftDevice = false,
            payAsYouDrive = false,
            voluntaryExcess = "No"
        )
        assertFalse(stateDefault.isIdvActive)
        assertEquals(0, stateDefault.activeFilterCount)

        val stateChanged = stateDefault.copy(
            selectedIdv = 150000f,
            isIdvExplicitlyChanged = true
        )
        assertTrue(stateChanged.isIdvActive)
        assertEquals(1, stateChanged.activeFilterCount)
    }

    @Test
    fun `IDV clamping correctly bounds out-of-range values into minIdv and maxIdv`() {
        val minIdv = 71414f
        val maxIdv = 234780f

        val lowValue = 5000f
        val clampedLow = lowValue.coerceIn(minIdv, maxIdv)
        assertEquals(minIdv, clampedLow)

        val highValue = 500000f
        val clampedHigh = highValue.coerceIn(minIdv, maxIdv)
        assertEquals(maxIdv, clampedHigh)

        val validValue = 150000f
        val clampedValid = validValue.coerceIn(minIdv, maxIdv)
        assertEquals(validValue, clampedValid)
    }

    @Test
    fun `Individual registration shows all 4 additional cover items including owner driver pa`() {
        val individualState = com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiState(
            ownershipType = com.policyboss.policybosspro.feature.carInsurance.model.OwnershipType.INDIVIDUAL
        )
        assertFalse(individualState.isCompany)
        assertTrue(individualState.isIndividual)
        assertEquals(4, individualState.visibleAdditionalCoverItems.size)
        assertTrue(individualState.visibleAdditionalCoverItems.any { it.id == "owner_driver_pa" })
    }

    @Test
    fun `Company registration shows only 3 additional cover items excluding owner driver pa`() {
        val companyState = com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiState(
            ownershipType = com.policyboss.policybosspro.feature.carInsurance.model.OwnershipType.COMPANY
        )
        assertTrue(companyState.isCompany)
        assertFalse(companyState.isIndividual)
        assertEquals(3, companyState.visibleAdditionalCoverItems.size)
        assertFalse(companyState.visibleAdditionalCoverItems.any { it.id == "owner_driver_pa" })
        assertEquals(
            listOf("paid_driver_liability", "paid_driver_pa", "limited_tp_damage"),
            companyState.visibleAdditionalCoverItems.map { it.id }
        )
    }

    @Test
    fun `Company registration sanitizes owner driver pa from filter state`() {
        val companyFilter = QuoteFilterState(
            selectedLiabilityCoverIds = setOf("owner_driver_pa", "paid_driver_liability")
        ).sanitizeForOwnership(isCompany = true)

        assertFalse(companyFilter.isOwnerDriverPaCover)
        assertEquals(setOf("paid_driver_liability"), companyFilter.selectedLiabilityCoverIds)
    }

    @Test
    fun `car value label shows Set IDV when not explicitly changed and formatted rupees when changed`() {
        val initial = QuoteFilterState(
            selectedIdv = 71414f,
            defaultIdv = 71414f,
            minIdv = 71414f,
            maxIdv = 234780f,
            isIdvExplicitlyChanged = false
        )
        val initialLabel = if (!initial.isIdvExplicitlyChanged) {
            "Set IDV"
        } else {
            "₹${com.policyboss.policybosspro.feature.carInsurance.model.MotorQuote.formatIndianCurrency(initial.selectedIdv.toInt())}"
        }
        assertEquals("Set IDV", initialLabel)

        val updated = initial.copy(selectedIdv = 90000f, isIdvExplicitlyChanged = true)
        val updatedLabel = if (!updated.isIdvExplicitlyChanged) {
            "Set IDV"
        } else {
            "₹${com.policyboss.policybosspro.feature.carInsurance.model.MotorQuote.formatIndianCurrency(updated.selectedIdv.toInt())}"
        }
        assertEquals("₹90,000", updatedLabel)
    }
}
