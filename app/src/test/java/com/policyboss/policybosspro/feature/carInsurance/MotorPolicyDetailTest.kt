package com.policyboss.policybosspro.feature.carInsurance

import com.policyboss.policybosspro.feature.carInsurance.model.MotorAddOn
import com.policyboss.policybosspro.feature.carInsurance.model.MotorQuote
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiState
import com.policyboss.policybosspro.feature.carInsurance.ui.carPolicyDetail.model.PolicyBreakupDetails
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MotorPolicyDetailTest {

    @Test
    fun `createSample generates accurate breakdown matching reference`() {
        val quote = MotorQuote(
            id = "TATA-01",
            insurerName = "Tata AIG",
            insurerLogoType = "tata",
            finalPremium = 4122,
            idvValue = 110831,
            zextraSwitchCoverPrice = 400,
            addOns = listOf(
                MotorAddOn("1", "Zero Depreciation", 501, isSelected = true),
                MotorAddOn("2", "Consumables", 200, isSelected = true)
            )
        )
        val uiState = NewCarUiState(
            make = "Maruti",
            model = "Wagon R",
            fuel = "Petrol",
            variant = "1.0 VXI PLUS (998 CC)",
            crnNumber = "12682505",
            registrationDate = "18 Oct, 2011"
        )

        val breakup = PolicyBreakupDetails.createSample(quote = quote, uiState = uiState)

        // Verify Vehicle Info
        assertEquals("Maruti, Wagon R, Petrol, 1.0 VXI PLUS (998 CC)", breakup.vehicleInfo.carModel)
        assertEquals(110831, breakup.vehicleInfo.idv)
        assertEquals("12682505", breakup.vehicleInfo.crn)
        assertEquals("18 Oct, 2011", breakup.vehicleInfo.regDate)
        assertEquals("20%", breakup.vehicleInfo.newNcb)

        // Verify Base Cover
        assertEquals("Base Cover", breakup.baseCoverGroup!!.categoryCaption)
        assertEquals("Own Damage Premium (A)", breakup.baseCoverGroup!!.title)
        assertEquals(298, breakup.baseCoverGroup!!.totalAmount)
        assertEquals("₹298", breakup.baseCoverGroup!!.formattedTotalAmount)
        assertNotNull(breakup.baseCoverGroup!!.standaloneItem)
        assertEquals("No Claim Bonus", breakup.baseCoverGroup!!.standaloneItem?.label)

        // Verify Third Party Cover
        assertEquals("Third Party Cover", breakup.thirdPartyGroup.categoryCaption)
        assertEquals("Third Party Premium (B)", breakup.thirdPartyGroup.title)
        assertEquals(2094, breakup.thirdPartyGroup.totalAmount)
        assertEquals("₹2,094", breakup.thirdPartyGroup.formattedTotalAmount)

        // Verify Bundle
        assertEquals("Bundle", breakup.bundleGroup.categoryCaption)
        assertEquals("Bundle Details", breakup.bundleGroup.title)
        assertEquals(400, breakup.bundleGroup.totalAmount)

        // Verify Addons
        assertEquals("Addons", breakup.addonGroup!!.categoryCaption)
        assertEquals("Addon Details", breakup.addonGroup!!.title)
        assertEquals(701, breakup.addonGroup!!.totalAmount)

        // Verify Taxes
        assertEquals("Taxes", breakup.taxesGroup.categoryCaption)
        assertEquals("GST@18", breakup.taxesGroup.title)
        assertTrue(breakup.taxesGroup.totalAmount >= 629)

        // Verify Final Premium
        assertEquals(4122, breakup.finalPremium)
        assertEquals("₹4,122", breakup.formattedFinalPremium)
    }
}
