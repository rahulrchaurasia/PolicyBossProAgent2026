package com.policyboss.policybosspro.feature.carInsurance

import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteResult.ui.plansFoundText
import org.junit.Assert.assertEquals
import org.junit.Test

class PlansFoundTextTest {

    @Test
    fun `plans found text uses singular for one plan`() {
        assertEquals("14 Plans Found", plansFoundText(14))
        assertEquals("1 Plan Found", plansFoundText(1))
        assertEquals("0 Plans Found", plansFoundText(0))
    }
}
