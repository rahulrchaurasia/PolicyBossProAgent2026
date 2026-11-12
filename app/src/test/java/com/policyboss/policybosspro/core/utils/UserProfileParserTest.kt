package com.policyboss.policybosspro.core.utils

import com.policyboss.policybosspro.feature.login.data.model.EMP
import com.policyboss.policybosspro.feature.login.data.model.HR
import com.policyboss.policybosspro.feature.login.data.model.LoginNewResponse_DSAS_Horizon
import com.policyboss.policybosspro.feature.login.data.model.POSP
import com.policyboss.policybosspro.feature.login.data.model.POSP_USER
import org.junit.Assert.assertEquals
import org.junit.Test

class UserProfileParserTest {

    // =========================================================================
    // 1. INITIALS EXTRACTION TESTS
    // =========================================================================

    @Test
    fun extractInitials_threeOrMoreWords_usesFirstAndLastWord() {
        assertEquals("RC", UserProfileParser.extractInitials("Rahul Rajendra Chaurasia"))
        assertEquals("AK", UserProfileParser.extractInitials("Dr. A. P. J. Abdul Kalam".replace("Dr.", "Anoop")))
        assertEquals("DK", UserProfileParser.extractInitials("Dr. A. P. J. Abdul Kalam"))
    }

    @Test
    fun extractInitials_twoWords_usesFirstAndLastWord() {
        assertEquals("AK", UserProfileParser.extractInitials("Amit Kumar"))
        assertEquals("RC", UserProfileParser.extractInitials("rahul chaurasia"))
    }

    @Test
    fun extractInitials_singleWord_usesFirstTwoUppercaseChars() {
        assertEquals("SU", UserProfileParser.extractInitials("Suresh"))
        assertEquals("RA", UserProfileParser.extractInitials("rahul"))
        assertEquals("A", UserProfileParser.extractInitials("a"))
    }

    @Test
    fun extractInitials_emptyOrBlank_returnsDefaultFallbackPB() {
        assertEquals("PB", UserProfileParser.extractInitials(""))
        assertEquals("PB", UserProfileParser.extractInitials("   "))
        assertEquals("PB", UserProfileParser.extractInitials("\t\n"))
        assertEquals("XX", UserProfileParser.extractInitials("", defaultFallback = "XX"))
    }

    @Test
    fun extractInitials_extraWhitespace_isNormalizedCorrectly() {
        assertEquals("RC", UserProfileParser.extractInitials("   Rahul    Rajendra   Chaurasia  "))
    }

    // =========================================================================
    // 2. USER TYPE EXTRACTION TESTS (POSP, FOS, EMP, MISP)
    // =========================================================================

    @Test
    fun extractFields_forPospUser_prefersPospUserAndPospOverEmp() {
        val response = LoginNewResponse_DSAS_Horizon(
            status = "SUCCESS",
            user_type = "POSP",
            Ss_Id = "150252",
            POSP_USER = POSP_USER(
                Name_On_PAN = "Rahul Rajendra Chaurasia",
                Mobile_No = "9773113793",
                Email_Id = "rahul.posp@policyboss.com",
                Erp_Id = "ERP-7788",
                Permanant_Add1 = "101 Cyber Park",
                Permanant_Add2 = "Malad East",
                Permanant_Add3 = "",
                Permanant_City = "Mumbai",
                Permanant_State = "Maharashtra",
                Permanant_Pincode = "400097"
            ),
            POSP = POSP(
                Fba_Id = "1001",
                Ss_Id = "150252"
            ),
            EMP = EMP(
                Emp_Name = "Fallback Employee",
                Mobile_Number = "9000000000",
                Email_Id = "emp@policyboss.com",
                FBA_ID = "9999",
                Emp_Id = "8888"
            ),
            HR = HR(Designation = "Senior POSP Advisor")
        )

        assertEquals("Rahul Rajendra Chaurasia", UserProfileParser.extractName(response))
        assertEquals("RC", UserProfileParser.extractInitials(UserProfileParser.extractName(response)))
        assertEquals("9773113793", UserProfileParser.extractMobile(response))
        assertEquals("rahul.posp@policyboss.com", UserProfileParser.extractEmail(response))
        assertEquals("1001", UserProfileParser.extractFbaId(response))
        assertEquals("150252", UserProfileParser.extractSsId(response))
        assertEquals("ERP-7788", UserProfileParser.extractErpId(response))
        assertEquals("Senior POSP Advisor", UserProfileParser.extractDesignation(response))
        assertEquals(
            "101 Cyber Park, Malad East, Mumbai, Maharashtra, 400097",
            UserProfileParser.extractAddress(response)
        )
    }

    @Test
    fun extractFields_forPospAndFos_fallsBackToPospOrEmpWhenPospUserMissing() {
        val pospWithEmpFallback = LoginNewResponse_DSAS_Horizon(
            status = "SUCCESS",
            user_type = "POSP",
            Ss_Id = "150252",
            POSP_USER = POSP_USER(Name_On_PAN = "", Mobile_No = "", Email_Id = ""),
            POSP = POSP(Fba_Id = "1001", First_Name = "", Last_Name = ""),
            EMP = EMP(
                Emp_Name = "Amit Kumar",
                Mobile_Number = "9876543210",
                Email_Id = "amit@policyboss.com"
            )
        )

        assertEquals("Amit Kumar", UserProfileParser.extractName(pospWithEmpFallback))
        assertEquals("9876543210", UserProfileParser.extractMobile(pospWithEmpFallback))
        assertEquals("amit@policyboss.com", UserProfileParser.extractEmail(pospWithEmpFallback))
        assertEquals("1001", UserProfileParser.extractFbaId(pospWithEmpFallback))

        val fosUser = LoginNewResponse_DSAS_Horizon(
            status = "SUCCESS",
            user_type = "FOS",
            Ss_Id = "555",
            POSP_USER = POSP_USER(
                Name_On_PAN = "Suresh",
                Mobile_No = "9988776655",
                Email_Id = "suresh@policyboss.com",
                Erp_Id = "ERP-FOS-1"
            ),
            POSP = POSP(Fba_Id = "5005")
        )

        assertEquals("Suresh", UserProfileParser.extractName(fosUser))
        assertEquals("SU", UserProfileParser.extractInitials(UserProfileParser.extractName(fosUser)))
        assertEquals("9988776655", UserProfileParser.extractMobile(fosUser))
        assertEquals("suresh@policyboss.com", UserProfileParser.extractEmail(fosUser))
        assertEquals("5005", UserProfileParser.extractFbaId(fosUser))
        assertEquals("555", UserProfileParser.extractSsId(fosUser))
        assertEquals("ERP-FOS-1", UserProfileParser.extractErpId(fosUser))
    }

    @Test
    fun extractFields_forEmpAndMisp_readsFromEmpStructure() {
        val empResponse = LoginNewResponse_DSAS_Horizon(
            status = "SUCCESS",
            user_type = "EMP",
            Ss_Id = "2002",
            EMP = EMP(
                Emp_Id = "2002",
                FBA_ID = "3003",
                Emp_Name = "Vikram Dev Singh",
                Mobile_Number = "9820012345",
                Email_Id = "vikram.singh@policyboss.com"
            ),
            HR = HR(Designation = "Area Sales Manager")
        )

        assertEquals("Vikram Dev Singh", UserProfileParser.extractName(empResponse))
        assertEquals("VS", UserProfileParser.extractInitials(UserProfileParser.extractName(empResponse)))
        assertEquals("9820012345", UserProfileParser.extractMobile(empResponse))
        assertEquals("vikram.singh@policyboss.com", UserProfileParser.extractEmail(empResponse))
        assertEquals("3003", UserProfileParser.extractFbaId(empResponse))
        assertEquals("2002", UserProfileParser.extractSsId(empResponse))
        assertEquals("0", UserProfileParser.extractErpId(empResponse))
        assertEquals("Area Sales Manager", UserProfileParser.extractDesignation(empResponse))

        val mispResponse = LoginNewResponse_DSAS_Horizon(
            status = "SUCCESS",
            user_type = "MISP",
            EMP = EMP(
                Emp_Id = "888",
                FBA_ID = "6006",
                Emp_Name = "MISP Partner",
                Mobile_Number = "9111222333",
                Email_Id = "misp@policyboss.com"
            )
        )

        assertEquals("MISP Partner", UserProfileParser.extractName(mispResponse))
        assertEquals("6006", UserProfileParser.extractFbaId(mispResponse))
        assertEquals("888", UserProfileParser.extractSsId(mispResponse))
    }

    @Test
    fun extractFields_nullOrDirtyNAValues_returnCleanFallbacks() {
        assertEquals("", UserProfileParser.extractName(null))
        assertEquals("", UserProfileParser.extractMobile(null))
        assertEquals("", UserProfileParser.extractEmail(null))
        assertEquals("0", UserProfileParser.extractFbaId(null))
        assertEquals("0", UserProfileParser.extractSsId(null))
        assertEquals("0", UserProfileParser.extractErpId(null))
        assertEquals("", UserProfileParser.extractDesignation(null))
        assertEquals("", UserProfileParser.extractAddress(null))

        val dirtyResponse = LoginNewResponse_DSAS_Horizon(
            status = "SUCCESS",
            user_type = "POSP",
            Ss_Id = "NA",
            POSP_USER = POSP_USER(
                Name_On_PAN = "NA",
                Mobile_No = "null",
                Email_Id = " ",
                Erp_Id = "NA",
                Permanant_Add1 = "NA",
                Permanant_City = "Mumbai",
                Permanant_Pincode = "null"
            ),
            POSP = POSP(Fba_Id = "NA"),
            HR = HR(Designation = "NA")
        )

        assertEquals("", UserProfileParser.extractName(dirtyResponse))
        assertEquals("", UserProfileParser.extractMobile(dirtyResponse))
        assertEquals("", UserProfileParser.extractEmail(dirtyResponse))
        assertEquals("0", UserProfileParser.extractFbaId(dirtyResponse))
        assertEquals("0", UserProfileParser.extractSsId(dirtyResponse))
        assertEquals("0", UserProfileParser.extractErpId(dirtyResponse))
        assertEquals("", UserProfileParser.extractDesignation(dirtyResponse))
        assertEquals("Mumbai", UserProfileParser.extractAddress(dirtyResponse))
    }
}

