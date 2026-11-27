package com.policyboss.policybosspro.feature.carInsurance

import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteResult.ui.plansFoundText
import com.policyboss.policybosspro.feature.carInsurance.model.quoteValidUptoLabel
import org.junit.Assert.assertEquals
import org.junit.Test

class PlansFoundTextTest {

    @Test
    fun `plans found text uses singular for one plan`() {
        assertEquals("14 Plans Found", plansFoundText(14))
        assertEquals("1 Plan Found", plansFoundText(1))
        assertEquals("0 Plans Found", plansFoundText(0))
    }

    @Test
    fun `quotes are valid until the end of tomorrow`() {
        assertEquals("27 Nov 2026, 11:59 PM", quoteValidUptoLabel(java.time.LocalDate.of(2026, 11, 26)))
        assertEquals("01 Jan 2027, 11:59 PM", quoteValidUptoLabel(java.time.LocalDate.of(2026, 12, 31)))
    }
}
