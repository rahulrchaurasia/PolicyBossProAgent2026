package com.policyboss.policybosspro.feature.carInsurance

import com.policyboss.policybosspro.feature.carInsurance.data.mapper.buildPremiumInitiateRequest
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.formatRegistrationNumber
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.splitCustomerName
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.toDomain
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.toNewCarUiState
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.toPremiumInitiateResult
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.vahanCcRange
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumInitiateRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumInitiateResponseDto
import com.policyboss.policybosspro.feature.carInsurance.data.model.VehicleInfoResponseDto
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiState
import com.policyboss.policybosspro.feature.carInsurance.model.OwnershipType
import com.policyboss.policybosspro.feature.carInsurance.model.PolicyType
import com.policyboss.policybosspro.feature.carInsurance.model.toPremiumInitiateInput
import com.policyboss.policybosspro.ui.components.dropDown.DropdownOption
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class PremiumInitiateRequestMapperTest {

    /** Trimmed copy of the live `vehicle_info_user` response for MH01BB0197. */
    private val vahanJson = """
        {
          "Registration_Number": "MH01BB0197", "Product_Id": 1, "Variant_Id": 6917, "Model_ID": 138,
          "Fuel_ID": 1, "Make_Name": "MARUTI", "Model_Name": "WAGON R", "Fuel_Type": "PETROL",
          "Variant_Name": "VXI+", "Seating_Capacity": 5, "Cubic_Capacity": "998",
          "Manufacture_Year": 2011, "Manufacture_Month": "Sep",
          "Chassis_Number": "MA3EWDE1S00330077", "Engin_Number": "K10BN7092461",
          "Registration_Date": "18/10/2011", "RTO_Code": 579, "RTO_Name": "Mumbai-Tardeo",
          "Permanent_Pincode": "415408", "Owner_Name": "VIJAYKUMAR PAWAR",
          "Pyp_Policy_Expiry_Date": "09/06/2030", "Pyp_Insurer_Name": "SBI GENERAL INS CO LTD",
          "Pyp_Insurer_Id": 17, "Pyp_Policy_Number": "POPMCAR00100050920",
          "FastlaneResponse_Obj": {
            "owner_name": "VIJAYKUMAR PAWAR", "maker_description": "MARUTI SUZUKI INDIA LTD",
            "maker_model": "MARUTI WAGON R VXI BSIV", "fuel_type": "PETROL", "cubic_capacity": "998",
            "registration_date": "2011-10-18", "manufacturing_date": "9/2011",
            "manufacturing_date_formatted": "2011-09"
          },
          "Source": "Surepass", "Result": "Success",
          "Vahan_Make": "MARUTI SUZUKI INDIA LTD", "Vahan_Model": "MARUTI WAGON R VXI BSIV",
          "Vahan_Variant": "NA", "Vahan_Fuel": "PETROL", "Vahan_Cubic_Capacity": 998,
          "Vahan_CC_Slab": "0_TO_1000", "Vehicle_Detail_Id": 6426386, "status": "Success"
        }
    """.trimIndent()

    private val today: LocalDate = LocalDate.of(2026, 11, 26)

    private val vehicle = testJson.decodeFromString(VehicleInfoResponseDto.serializer(), vahanJson)
        .toDomain("MH01BB0197")

    private fun formState(transform: NewCarUiState.() -> NewCarUiState = { this }): NewCarUiState =
        vehicle.toNewCarUiState(NewCarUiState()).transform()

    private fun build(state: NewCarUiState = formState()): PremiumInitiateRequest =
        buildPremiumInitiateRequest(state.toPremiumInitiateInput(vehicle), ssId = "150253", appVersion = "policyboss-1.5.6.1", today = today)

    @Test
    fun `sample vahan response maps to the documented request values`() {
        val request = build()

        assertEquals(1, request.productId)
        assertEquals(6917, request.vehicleId)
        assertEquals(579, request.rtoId)
        assertEquals("2011-10-18", request.vehicleRegistrationDate)
        assertEquals("2011-09-01", request.vehicleManfDate)
        assertEquals("2030-06-09", request.policyExpiryDate)
        assertEquals(17, request.prevInsurerId)
        assertEquals("individual", request.vehicleRegistrationType)
        assertEquals("0", request.vehicleNcbCurrent)
        assertEquals("yes", request.isFastlaneRto)
        assertEquals("MH-01-BB-0197", request.registrationNo)
        assertEquals("MH-01", request.regnoRtoCode)
        assertEquals("PETROL", request.fuelType)
        assertEquals("VIJAYKUMAR", request.firstName)
        assertEquals("", request.middleName)
        assertEquals("PAWAR", request.lastName)
        assertEquals("VIJAYKUMAR PAWAR", request.customerName)
        assertEquals("150253", request.ssId)
        assertEquals("policyboss-1.5.6.1", request.appVersion)
        assertEquals("1CH_0TP", request.vehicleInsuranceSubtype)
        assertEquals("MA3EWDE1S00330077", request.chassisNumber)
        assertEquals("K10BN7092461", request.enginNumber)
        assertEquals("yes", request.isFastlaneManufacture)
        assertEquals("yes", request.isFastlaneRegistration)
        assertEquals("", request.tpInsurerId)
        assertEquals("415408", request.permanentPincode)
        assertEquals("POPMCAR00100050920", request.previousPolicyNumber)
        assertEquals(600, request.vahanCcMin)
        assertEquals(1000, request.vahanCcMax)
        assertEquals(998, request.vahanCcNormal)
        assertEquals("0_TO_1000", request.vahanCcSlab)
        assertEquals("Surepass", request.vahanSource)
        assertEquals("MARUTI SUZUKI INDIA LTD|MARUTI WAGON R VXI BSIV|PETROL|998CC", request.vahanFull)
        assertEquals("MARUTI|WAGON R|VXI+|PETROL|998 CC", request.matchVehicleFull)
    }

    @Test
    fun `serialized body keeps backend key names and value types`() {
        val json = testJson.encodeToJsonElement(PremiumInitiateRequest.serializer(), build()).jsonObject

        assertEquals(6917, json.getValue("vehicle_id").jsonPrimitive.int)
        assertEquals(true, json.getValue("vehicle_ncb_current").jsonPrimitive.isString)
        assertEquals(1_500_000, json.getValue("pa_owner_driver_si").jsonPrimitive.int)
        assertEquals("renew", json.getValue("vehicle_insurance_type").jsonPrimitive.content)
        assertEquals("diy", json.getValue("search_from").jsonPrimitive.content)
        assertEquals(true, json.containsKey("g-recaptcha-response").not())
        assertEquals(true, json.containsKey("engin_number"))
    }

    @Test
    fun `expiry date drives only the breakin flag`() {
        val future = build()
        assertEquals("no", future.isBreakin)

        val todayRequest = build(formState { copy(policyExpiryDate = "26-Nov-2026") })
        assertEquals("no", todayRequest.isBreakin)

        val past = build(formState { copy(policyExpiryDate = "25-Nov-2026") })
        assertEquals("yes", past.isBreakin)
        assertEquals("no", past.isClaimExists)
        assertEquals("yes", past.isPolicyExist)
        assertEquals("2026-11-25", past.policyExpiryDate)
    }

    @Test
    fun `claim and existing-policy radios drive their flags`() {
        val claim = build(formState { copy(hasClaim = true) })
        assertEquals("yes", claim.isClaimExists)

        val noClaim = build(formState { copy(hasClaim = false) })
        assertEquals("no", noClaim.isClaimExists)

        val existing = build(formState { copy(hasExistingPolicy = true) })
        assertEquals("yes", existing.isPolicyExist)

        // No existing policy hides expiry and claim questions, so all three flags are "no"
        val noPolicy = build(formState { copy(hasExistingPolicy = false, hasClaim = true, policyExpiryDate = "25-Nov-2026") })
        assertEquals(listOf("no", "no", "no"), listOf(noPolicy.isPolicyExist, noPolicy.isClaimExists, noPolicy.isBreakin))

        // TP Only has no claim question and always sends "yes"
        val tp = build(formState { copy(policyType = PolicyType.TP_ONLY, hasClaim = false) })
        assertEquals("yes", tp.isClaimExists)

        // OD keeps the radio value
        val odNoClaim = build(formState { copy(policyType = PolicyType.OD_ONLY, hasClaim = false) })
        assertEquals("no", odNoClaim.isClaimExists)
    }

    @Test
    fun `form choices map to registration type, ncb, subtype and tp insurer`() {
        val corporate = build(formState { copy(ownershipType = OwnershipType.COMPANY) })
        assertEquals("corporate", corporate.vehicleRegistrationType)
        assertEquals("no", corporate.isPaOd)

        val ncb = build(formState { copy(selectedNcb = DropdownOption("25", "25%")) })
        assertEquals("25", ncb.vehicleNcbCurrent)

        val od = build(formState { copy(policyType = PolicyType.OD_ONLY, selectedTpInsurer = DropdownOption("12", "ICICI")) })
        assertEquals("1OD_0TP", od.vehicleInsuranceSubtype)
        assertEquals("12", od.tpInsurerId)

        val tp = build(formState { copy(policyType = PolicyType.TP_ONLY) })
        assertEquals("0CH_1TP", tp.vehicleInsuranceSubtype)
        assertEquals("", tp.tpInsurerId)
    }

    @Test
    fun `name splitting handles one, two and many words`() {
        assertEquals(Triple("VIJAYKUMAR", "", ""), splitCustomerName(" VIJAYKUMAR ").let { Triple(it.first, it.middle, it.last) })
        assertEquals(Triple("A", "", "B"), splitCustomerName("A  B").let { Triple(it.first, it.middle, it.last) })
        assertEquals(Triple("A", "B C", "D"), splitCustomerName("A B C D").let { Triple(it.first, it.middle, it.last) })
    }

    @Test
    fun `registration number and cc slab helpers`() {
        assertEquals("MH-01-BB-0197", formatRegistrationNumber("mh 01 bb 0197"))
        assertEquals("DL-03-C-1234", formatRegistrationNumber("DL3C1234"))
        assertEquals(1000 to 1500, vahanCcRange("1000_TO_1500"))
        assertEquals(1500 to 0, vahanCcRange("1500_ABOVE"))
        assertEquals(0 to 0, vahanCcRange(""))
    }

    @Test
    fun `response without a search reference is rejected`() {
        fun parse(json: String) =
            testJson.decodeFromString(PremiumInitiateResponseDto.serializer(), json).toPremiumInitiateResult()

        assertNull(parse("""{"Msg":"error"}"""))
        assertNull(parse("""{"Summary":{"Request_Unique_Id":"","crn":0}}"""))
        // CRN is optional; the search reference is what the follow-up APIs need
        assertEquals("", parse("""{"Summary":{"Request_Unique_Id":"SRN-1"}}""")?.crn)
    }
}
