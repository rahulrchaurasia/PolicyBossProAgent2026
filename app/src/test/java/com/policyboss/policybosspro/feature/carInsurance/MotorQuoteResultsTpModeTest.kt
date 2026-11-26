package com.policyboss.policybosspro.feature.carInsurance

import com.policyboss.policybosspro.feature.carInsurance.model.MotorQuote
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiState
import com.policyboss.policybosspro.feature.carInsurance.model.PolicyType
import com.policyboss.policybosspro.feature.carInsurance.ui.carPolicyDetail.model.PolicyBreakupDetails
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.QuoteFilterState
import com.policyboss.policybosspro.ui.components.dropDown.DropdownOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Results screen and Policy Details for a third-party-only policy. */
class MotorQuoteResultsTpModeTest {

    private fun state(type: PolicyType) = NewCarUiState(
        policyType = type,
        selectedPolicyType = type.displayName,
        selectedModel = DropdownOption("138", "WAGON R"),
        selectedFuel = DropdownOption("1", "PETROL"),
        selectedVariant = DropdownOption("6917", "VXI+"),
        selectedNcb = DropdownOption("0", "0%")
    )

    private val quote = MotorQuote(id = "ARN-1", insurerName = "Bajaj Allianz", finalPremium = 4031, idvValue = 110831)

    @Test
    fun `summary strip drops NCB for TP only`() {
        assertEquals("WAGON R • PETROL • VXI+ • ${PolicyType.TP_ONLY.displayName}", state(PolicyType.TP_ONLY).vehicleSummaryText)
        listOf(PolicyType.OD_ONLY, PolicyType.COMPREHENSIVE).forEach { type ->
            assertTrue(state(type).vehicleSummaryText.endsWith("NCB status: 0%"))
        }
    }

    @Test
    fun `TP policy details have no own damage, discounts, NCB, add-ons or IDV`() {
        val breakup = PolicyBreakupDetails.createSample(quote = quote, uiState = state(PolicyType.TP_ONLY))

        assertNull(breakup.baseCoverGroup)
        assertNull(breakup.addonGroup)
        assertNull(breakup.vehicleInfo.idv)
        assertNull(breakup.vehicleInfo.newNcb)
        assertEquals("Third Party Premium (A)", breakup.thirdPartyGroup.title)

        val labels = listOf(breakup.thirdPartyGroup, breakup.bundleGroup, breakup.taxesGroup)
            .flatMap { group -> group.items.map { it.label } + listOfNotNull(group.standaloneItem?.label) }
        assertFalse(labels.any { it.contains("Discount") || it.contains("No Claim Bonus") || it.contains("OD") })
    }

    @Test
    fun `comprehensive policy details keep own damage, add-ons, IDV and NCB`() {
        val breakup = PolicyBreakupDetails.createSample(quote = quote, uiState = state(PolicyType.COMPREHENSIVE))

        assertNotNull(breakup.baseCoverGroup)
        assertNotNull(breakup.addonGroup)
        assertEquals(110831, breakup.vehicleInfo.idv)
        assertEquals("20%", breakup.vehicleInfo.newNcb)
        assertEquals("Third Party Premium (B)", breakup.thirdPartyGroup.title)
    }

    @Test
    fun `TP fallback total adds up from the remaining groups only`() {
        // finalPremium 0 forces the computed total
        val breakup = PolicyBreakupDetails.createSample(quote = quote.copy(finalPremium = 0), uiState = state(PolicyType.TP_ONLY))
        val net = breakup.thirdPartyGroup.totalAmount + breakup.bundleGroup.totalAmount
        assertEquals(net + breakup.taxesGroup.totalAmount, breakup.finalPremium)
    }

    @Test
    fun `TP additional cover summary ignores accessories`() {
        val filter = QuoteFilterState(
            selectedLiabilityCoverIds = setOf("owner_driver_pa"),
            selectedUnNamedPassengerCover = "No",
            electricalAccessoriesAmount = "15000",
            nonElectricalAccessoriesAmount = "20000"
        )
        assertEquals(1, filter.additionalCoverFilterCount)
    }
}
