package com.policyboss.policybosspro.feature.carInsurance

import kotlin.time.Duration.Companion.seconds
import com.policyboss.policybosspro.feature.carInsurance.data.repository.premiumPollDelay
import kotlinx.coroutines.flow.toList
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumQuotePollEvent
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumPollStatus
import com.policyboss.policybosspro.core.Resource
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.addonDisplayName
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.toPremiumInitiateResult
import com.policyboss.policybosspro.feature.carInsurance.data.mapper.toPremiumQuoteList
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumInitiateResponseDto
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumListResponseDto
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumSearchRequest
import com.policyboss.policybosspro.feature.carInsurance.data.model.PremiumSummaryResponseDto
import com.policyboss.policybosspro.feature.carInsurance.data.repository.CarInsuranceRepository
import com.policyboss.policybosspro.feature.carInsurance.data.repository.VehicleInfoSessionProvider
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumInitiateResult
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumProgress
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import kotlin.time.Duration

class PremiumQuoteListTest {

    /** Trimmed copy of a live premium_list_db response (TP only, MH01BB0197). */
    private val listJson = """
        {
          "Error": null,
          "Summary": {
            "Request_Unique_Id": "SRN-N3AZQFZP-S7TM-OQEH-UMET-IKYPNC0OYYGH_21024837",
            "PB_CRN": 12698778, "Status": "complete", "Total": 18, "Pending": 0,
            "Complete": 18, "Success": 14, "Fail": 4, "Idv_Min": 0, "Idv_Max": 0,
            "Premium_Min": 2471, "Premium_Max": 4031,
            "_id": "6b07fbe270a8d60e0c2d529a", "Request_Id": 41341670, "Client_Id": 2,
            "Request_Core": { "vehicle_id": 6917, "registration_no": "MH-01-BB-0197", "visitor_id": null,
              "recommended_vehicle_insurer_list": [], "vehicle_ncb_current": "0" },
            "Request_Product": { "policy_start_date": "2026-11-30", "policy_end_date": "2027-11-29",
              "vehicle_age_year": 15, "addon_premium": 680 },
            "Created_On": "2026-11-26 14:59:06", "Total_Execution_Time": 9.34, "__v": 0,
            "Inprogress_Insurer": {}, "Request_Unique_Id_Core": "SRN-N3AZQFZP-S7TM-OQEH-UMET-IKYPNC0OYYGH",
            "vehicle_min_idv": 0, "vehicle_max_idv": 0, "Actual_Time": 15.91, "Insurer_Cnt": 18,
            "Insurer_Success_Count": 14, "Plan_Success_Count": 14, "Common_Addon": {},
            "Last_Status": "SEARCH", "Prefered_Insurer_list": [1, 6, 5]
          },
          "Response": [
            {
              "Service_Log_Id": 502796061,
              "Service_Log_Unique_Id_Core": "ARN-DYS1BARO-EIVU-NQ6I-G6UL-GJSUX2N88NSB",
              "Insurer_Id": 1,
              "Insurer": {
                "_id": "5f22bef074a6e9039ab04675", "CreatedOn": "", "IsActive": 1, "IsInternal": null,
                "Insurer_Logo_Name": "BajajAllianzGeneral.png", "Product_Id": [1, 2, 10, 12],
                "Insurer_Code": "Bajaj Allianz", "Insurer_ID": 1,
                "Insurer_Name": "Bajaj Allianz General Insurance Co. Ltd.",
                "PreviousInsurer_Pincode": 411006,
                "Insurer_Logo_Name_Whatsapp": "https://origin-cdnh.policyboss.com/website/UI22/images/whatsaap_insurer_logo/BajajAllianzGeneral.jpg"
              },
              "Premium_Breakup": {
                "own_damage": { "od_basic": 0, "od_final_premium": 0, "": 0 },
                "liability": { "tp_basic": 3416, "tp_final_premium": 3416, "tp_GR36A-PA": null },
                "rsa_cpa_bundle_breakup": { "bundle_net_premium": 300, "bundle_final_premium": 354, "bundle_service_tax": 54 },
                "net_premium": 3416, "service_tax": 614.88, "final_premium": 4031,
                "DIGIT_PAYD": 0, "QUOTE_ID": 0, "RELIANCE_GARAGE": 0
              },
              "Premium_Rate": {
                "own_damage": { "od_basic": null, "od_disc_ncb": "0" },
                "liability": { "tp_basic": 0 },
                "addon": { "addon_zero_dep_cover": null },
                "rsa_cpa_bundle_breakup": { "bundle_net_premium": 0, "bundle_final_premium": 0, "bundle_service_tax": 0 }
              },
              "Addon_List": {}, "Addon_Mode": "BUFFET",
              "Plan_List": [
                {
                  "Plan_Id": 24, "Plan_Name": "TP", "Service_Log_Id": 502796061,
                  "Insurer_Transaction_Identifier": "11260044583129",
                  "Service_Log_Unique_Id": "ARN-DYS1BARO-EIVU-NQ6I-G6UL-GJSUX2N88NSB_502796061_21024837",
                  "Plan_Addon_Breakup": {}, "Plan_Addon_Premium": 0,
                  "Full_Premium_Breakup": { "net_premium": 3416, "service_tax": 614.88, "final_premium": 4031 },
                  "Proposal_Url": "https://www.policyboss.com/car-insurance/proposal?ClientID=2&ARN=ARN-DYS1BARO"
                }
              ],
              "LM_Custom_Request": { "vehicle_expected_idv": 0, "dbmaster_insurer_rto_city_code": "" },
              "Completion_Summary": { "Total": 1, "Completed": 1, "Status": "complete" },
              "Error_Code": "", "Is_Special_Price": false
            },
            {
              "Insurer_Id": 4,
              "Insurer": { "Insurer_Code": "Future Generali", "Insurer_ID": 4, "PreviousInsurer_Pincode": "400013" },
              "Premium_Breakup": null, "Plan_List": [], "LM_Custom_Request": null,
              "Error_Code": "LM009",
              "Error_Msg": "PREMIUM :: LM009 :: Vehicle Underwriting Restriction",
              "Error_Specific": "Referral due to: Declined Vehicle"
            },
            {
              "Insurer_Id": 7,
              "Insurer": { "Insurer_Code": "IFFCO Tokio", "Insurer_ID": 7 },
              "Premium_Breakup": { "net_premium": 2094, "service_tax": 376.92, "final_premium": 2471,
                "rsa_cpa_bundle_breakup": { "bundle_net_premium": 300, "bundle_final_premium": 354, "bundle_service_tax": 54 },
                "QUOTE_ID": "BA503150VPC15198449" },
              "Plan_List": [
                {
                  "Plan_Id": 65, "Plan_Name": "Comprehensive",
                  "Service_Log_Unique_Id": "ARN-XBPZHQDG_502796056_21024837",
                  "Plan_Addon_Breakup": { "addon_zero_dep_cover": 680, "addon_road_assist_cover": 0 },
                  "Full_Premium_Breakup": { "final_premium": 2470.92 },
                  "Proposal_Url": "https://www.policyboss.com/car-insurance/proposal?ARN=ARN-XBPZHQDG"
                }
              ],
              "LM_Custom_Request": { "vehicle_expected_idv": "412000" },
              "Error_Code": ""
            },
            {
              "Insurer_Id": 13,
              "Insurer": { "Insurer_Code": "Oriental Insurance", "Insurer_ID": 13 },
              "Plan_List": [], "Error_Code": "LM002", "Error_Msg": null
            }
            ,{
              "Insurer_Id": 44,
              "Insurer": { "Insurer_Code": "GoDigit", "Insurer_ID": 44 },
              "Premium_Breakup": { "net_premium": 0, "final_premium": 0 },
              "Plan_List": [], "Error_Code": ""
            }
          ],
          "Zextra_Response": null,
          "Addon_Request": {},
          "Error_Master": { "LM002": "Un-Identified Error", "LM009": "Vehicle Underwriting Restriction" }
        }
    """.trimIndent()

    private val srn = "SRN-N3AZQFZP-S7TM-OQEH-UMET-IKYPNC0OYYGH_21024837"

    private fun parseList() = testJson.decodeFromString(PremiumListResponseDto.serializer(), listJson)

    @Test
    fun `valid insurer rows become one quote each, cheapest first`() {
        val list = parseList().toPremiumQuoteList(srn)

        assertEquals("12698778", list.crn)
        assertEquals(srn, list.searchReferenceNumber)
        // Valid = no Error_Code, Premium_Breakup present, net_premium > 0 -> "2 Plans Found"
        assertEquals(listOf("IFFCO Tokio", "Bajaj Allianz"), list.quotes.map { it.insurerName })
        assertEquals(listOf(7, 1), list.quotes.map { it.insurerId })

        val iffco = list.quotes.first()
        assertEquals(2471, iffco.finalPremium)
        assertEquals(354, iffco.zextraBundlePremium)
        assertEquals(354, iffco.zextraSwitchCoverPrice)
        assertEquals(2471 + 354, iffco.effectivePremium(isZextraEnabled = true))
        assertEquals(2471, iffco.effectivePremium(isZextraEnabled = false))
        assertEquals("Comprehensive", iffco.planName)
        assertEquals(412000, iffco.idvValue)
        assertEquals(listOf("Zero Dep" to 680), iffco.addOns.map { it.name to it.price })

        val bajaj = list.quotes.last()
        assertEquals("ARN-DYS1BARO-EIVU-NQ6I-G6UL-GJSUX2N88NSB_502796061_21024837", bajaj.id)
        assertEquals(4031, bajaj.finalPremium)
        assertEquals("TP", bajaj.planName)
        assertEquals(0, bajaj.idvValue)
        assertEquals(0, bajaj.totalEarnings)
        assertEquals("https://www.policyboss.com/car-insurance/proposal?ClientID=2&ARN=ARN-DYS1BARO", bajaj.buyNowUrl)
        assertTrue(bajaj.insurerLogoUrl!!.endsWith("BajajAllianzGeneral.jpg"))
    }

    @Test
    fun `insurers without a plan are listed with their reason`() {
        val declined = parseList().toPremiumQuoteList(srn).declinedInsurers

        // GoDigit has no Error_Code but net_premium 0, so it is not a plan
        assertEquals(listOf("Future Generali", "Oriental Insurance", "GoDigit"), declined.map { it.insurerName })
        assertEquals("Vehicle Underwriting Restriction", declined[0].reason)
        // No Error_Msg: falls back to Error_Master
        assertEquals("Un-Identified Error", declined[1].reason)
    }

    @Test
    fun `summary progress and addon names`() {
        val progress = parseList().toPremiumQuoteList(srn).progress
        assertEquals(PremiumProgress("complete", total = 18, pending = 0, success = 14, fail = 4), progress)
        assertTrue(progress.isComplete)
        assertTrue(PremiumProgress(status = "pending", total = 5, pending = 0).isComplete)
        assertTrue(!PremiumProgress(status = "pending", total = 5, pending = 2).isComplete)

        assertEquals("Ncb Protection", addonDisplayName("addon_ncb_protection_cover"))
    }

    @Test
    fun `premium_initiate live response keeps only the search reference and crn`() {
        val result = testJson.decodeFromString(
            PremiumInitiateResponseDto.serializer(),
            """
            {"Summary":{"Request_Unique_Id":"SRN-ZOQOZNNW-NWWJ-AHVX-PLHM-CMLOFELG01KE_21025309",
              "customer_reference_number":"CRN-RRYYHNQL_12698956","Crn_Unique_Id":"CRN-RRYYHNQL",
              "crn":12698956,"quote_url":"/car-insurance-UI22/quotes?SID=CRN"},
             "Request":{"product_id":1,"vehicle_id":6917,"crn":12698956,"secret_key":"SECRET-X","visitor_id":null,
               "recommended_vehicle_insurer_list":[]},
             "Addon_Request":null,"Master":{"Vehicle":null,"Rto":null}}
            """.trimIndent()
        ).toPremiumInitiateResult()

        assertEquals(
            PremiumInitiateResult(crn = "12698956", requestUniqueId = "SRN-ZOQOZNNW-NWWJ-AHVX-PLHM-CMLOFELG01KE_21025309"),
            result
        )

        val dto = testJson.decodeFromString(
            PremiumInitiateResponseDto.serializer(),
            """{"Summary":{"Crn_Unique_Id":"CRN-RRYYHNQL","quote_url":"/quotes"},"Request":{"vehicle_id":1},"Master":{"Vehicle":null,"Rto":null}}"""
        )
        assertEquals("CRN-RRYYHNQL", dto.summary?.crnUniqueId)
        assertEquals("/quotes", dto.summary?.quoteUrl)
        assertTrue("Request is not mapped", "Request" !in testJson.encodeToJsonElement(PremiumInitiateResponseDto.serializer(), dto).jsonObject)
    }

    @Test
    fun `premium_list_db keeps the full response including Request_Core and Request_Product`() {
        val dto = parseList()
        val summary = dto.summary!!

        assertEquals("MH-01-BB-0197", summary.requestCore!!["registration_no"]!!.jsonPrimitive.content)
        assertEquals("2026-11-30", summary.requestProduct!!["policy_start_date"]!!.jsonPrimitive.content)
        assertEquals(41341670L, summary.requestId)
        assertEquals("SEARCH", summary.lastStatus)
        assertEquals(listOf(1, 6, 5), summary.preferredInsurerIds)

        val bajaj = dto.response.first()
        assertEquals(502796061L, bajaj.serviceLogId)
        assertEquals("BUFFET", bajaj.addonMode)
        assertEquals(false, bajaj.isSpecialPrice)
        assertEquals("complete", bajaj.completionSummary?.status)
        assertEquals(listOf(1, 2, 10, 12), bajaj.insurer?.productIds)
        assertEquals(null, bajaj.insurer?.isInternal)
        assertEquals("3416", bajaj.premiumBreakup!!.liability["tp_final_premium"]!!.jsonPrimitive.content)
        assertEquals(354.0, bajaj.premiumBreakup!!.rsaCpaBundle?.finalPremium)
        assertTrue(bajaj.premiumRate!!.ownDamage.containsKey("od_basic"))
        assertEquals("11260044583129", bajaj.planList.single().insurerTransactionIdentifier)
        assertEquals("Referral due to: Declined Vehicle", dto.response[1].errorSpecific)
    }

    @Test
    fun `premium_summary maps Summary, Request and typed Master`() {
        val dto = testJson.decodeFromString(
            PremiumSummaryResponseDto.serializer(),
            """
            {"Status":"Not_Cached",
             "Summary":{"Request_Unique_Id":"$srn","customer_reference_number":"CRN-3XG7Z6VO",
               "User_Data_Id":21024837,"Created_On":"2026-11-26T09:29:05.048Z","PB_CRN":12698778,
               "Client_Id":2,"Request_Id":41341670,"Status":"complete","Total":18,"Pending":0,
               "Complete":18,"Success":14,"Fail":4,"Total_Execution_Time":9.34},
             "Request":{"vehicle_id":6917,"rto_id":579,"crn":12698778,"registration_no":"MH-01-BB-0197",
               "rm_reporting_details_direct_reporting_uid":"107602","rm_reporting_details_uid":119591,
               "pa_owner_driver_si":1500000,"pa_paid_driver_si":"0","geo_lat":0,"visitor_id":null,
               "posp_gender":"","recommended_vehicle_insurer_list":[],"policy_expiry_date":"2026-11-29",
               "vehicle_full":"MARUTI|WAGON R|VXI+|PETROL|998|0","is_claim_exists":"yes"},
             "Addon_Request":{},
             "Master":{
               "Vehicle":{"Vehicle_ID":6917,"Model_Name":"WAGON R","ExShoroomPrice":407000,"Mapped_Insurer_Count":16,
                 "Is_Base":"Yes","Insurer_Vehicle_Insurer_Segmant":"0",
                 "Insurer_101":{"Premium_Status":1,"Status_Id":2,"Insurer_Vehicle_ID":221189,"Insurer_ID":101},
                 "Insurer_30":{"VI_Master":{"Insurer_Vehicle_Code":"CATEGORY 39","Insurer_Vehicle_ExShowRoom":515000,
                   "Is_Active":null,"MASTER CODE (IDV CODE)":"0"},"Premium_Status_CRN":11379205,"Insurer_ID":30},
                 "Insurer_17":{"VI_Master":{"Insurer_Vehicle_FuelType_Code":1,"Insurer_Vehicle_Insurer_BodyTypeCode":""}}},
               "Rto":{"VehicleCity_RTOCode":"MH01","IsActive":true,"VehicleTariff_Zone":"A",
                 "Insurer_8":{"RI_Master":{"Insurer_Rto_Code":24001,"Insurer_Rto_State_Code":24}},
                 "Insurer_14":{"RI_Master":{"Insurer_Rto_Code":"MH01","IsActive":"","Insurer_Rto_City_Code":1916}},
                 "Insurer_18":{"RI_Master":{"Insurer_Rto_City_Name":0,"Insurer_Rto_District_Name":"Mumbai Central"}}}}}
            """.trimIndent()
        )

        assertEquals("Not_Cached", dto.status)
        assertEquals(41341670L, dto.summary?.requestId)
        assertEquals(9.34, dto.summary?.totalExecutionTime)

        val request = dto.request!!
        assertEquals(6917L, request.vehicleId)
        assertEquals(579, request.rtoId)
        assertEquals("MH-01-BB-0197", request.registrationNo)
        assertEquals(107602L, request.rmReportingDirectReportingUid)
        assertEquals(119591L, request.rmReportingDetailsUid)
        assertEquals(1_500_000.0, request.paOwnerDriverSi)
        assertEquals(0.0, request.paPaidDriverSi)
        assertEquals(null, request.visitorId)
        assertEquals(null, request.pospGender)
        assertEquals("yes", request.isClaimExists)

        val vehicle = dto.master!!.vehicle!!
        assertEquals(6917L, vehicle.vehicleId)
        assertEquals(407000.0, vehicle.exShowroomPrice)
        assertEquals("0", vehicle.insurerSegment)
        assertEquals(setOf(101, 30, 17), vehicle.insurers.keys)
        assertEquals(221189L, vehicle.insurers[101]?.insurerVehicleId)
        assertEquals(null, vehicle.insurers[101]?.viMaster)
        assertEquals("CATEGORY 39", vehicle.insurers[30]?.viMaster?.code)
        assertEquals(515000.0, vehicle.insurers[30]?.viMaster?.exShowroom)
        assertEquals(11379205L, vehicle.insurers[30]?.premiumStatusCrn)
        assertEquals("1", vehicle.insurers[17]?.viMaster?.fuelTypeCode)

        val rto = dto.master!!.rto!!
        assertEquals("MH01", rto.rtoCode)
        assertEquals(true, rto.isActive)
        assertEquals("24001", rto.insurers[8]?.riMaster?.rtoCode)
        assertEquals("24", rto.insurers[8]?.riMaster?.stateCode)
        assertEquals(null, rto.insurers[14]?.riMaster?.isActive)
        assertEquals("1916", rto.insurers[14]?.riMaster?.cityCode)
        assertEquals("0", rto.insurers[18]?.riMaster?.cityName)

        // Encoding restores the backend's Insurer_<id> keys.
        val encodedVehicle = testJson.encodeToJsonElement(PremiumSummaryResponseDto.serializer(), dto)
            .jsonObject["Master"]!!.jsonObject["Vehicle"]!!.jsonObject
        assertTrue("Insurer_101" in encodedVehicle)
        assertTrue("insurers" !in encodedVehicle)
    }

    // -----------------------------------------------------------------
    // Repository polling
    // -----------------------------------------------------------------

    /** Answers premium_list_db calls in order; `null` = HTTP 500. The last answer repeats. */
    private class SequenceApi(private val answers: List<String?>) : ThrowingCarInsuranceApi() {
        val requests = mutableListOf<PremiumSearchRequest>()

        override suspend fun premiumList(request: PremiumSearchRequest): Response<PremiumListResponseDto> {
            requests += request
            val body = answers[(requests.size - 1).coerceAtMost(answers.lastIndex)]
                ?: return Response.error(500, "{}".toResponseBody())
            return Response.success(testJson.decodeFromString(PremiumListResponseDto.serializer(), body))
        }
    }

    private fun listBody(status: String, pending: Int) =
        listJson.replace(""""Status": "complete", "Total": 18, "Pending": 0,""", """"Status": "$status", "Total": 18, "Pending": $pending,""")

    private fun repository(api: SequenceApi) =
        CarInsuranceRepository(api, testAppDataManager(), testJson, VehicleInfoSessionProvider())

    private fun statuses(events: List<PremiumQuotePollEvent>) =
        events.map { (it as PremiumQuotePollEvent.Update).status }

    @Test
    fun `polls premium_list_db one call at a time until complete`() = runTest {
        val api = SequenceApi(listOf(listBody("pending", 10), listBody("pending", 3), listBody("complete", 0)))

        val events = repository(api).pollPremiumQuotes(srn).toList()

        assertEquals(listOf(PremiumPollStatus.POLLING, PremiumPollStatus.POLLING, PremiumPollStatus.COMPLETE), statuses(events))
        assertEquals(3, api.requests.size)
        assertEquals(2_000L, currentTime) // two 1 s waits in the fast phase
        val body = testJson.encodeToJsonElement(PremiumSearchRequest.serializer(), api.requests.first()).jsonObject
        assertEquals(setOf("search_reference_number", "secret_key", "client_key"), body.keys)
        assertEquals(srn, api.requests.first().searchReferenceNumber)
        assertEquals(2, (events.last() as PremiumQuotePollEvent.Update).quotes.quotes.size)
    }

    @Test
    fun `complete on the first answer stops at once`() = runTest {
        val events = repository(SequenceApi(listOf(listJson))).pollPremiumQuotes(srn).toList()
        assertEquals(listOf(PremiumPollStatus.COMPLETE), statuses(events))
        assertEquals(0L, currentTime)
    }

    @Test
    fun `stops after 60 s while still pending and keeps the plans`() = runTest {
        val api = SequenceApi(listOf(listBody("pending", 5)))

        val events = repository(api).pollPremiumQuotes(srn).toList()

        // 16 calls 1 s apart (0..15 s), then 15 calls 3 s apart (18..60 s)
        assertEquals(31, api.requests.size)
        assertEquals(60_000L, currentTime)
        assertEquals(PremiumPollStatus.TIMED_OUT, statuses(events).last())
        assertEquals(2, (events.last() as PremiumQuotePollEvent.Update).quotes.quotes.size)
    }

    @Test
    fun `calls are 1 s apart for the first 15 s, then 3 s apart`() {
        assertEquals(1.seconds, premiumPollDelay(0.seconds))
        assertEquals(1.seconds, premiumPollDelay(14.seconds))
        assertEquals(3.seconds, premiumPollDelay(15.seconds))
        assertEquals(3.seconds, premiumPollDelay(45.seconds))
    }

    @Test
    fun `a failed first call reports an error`() = runTest {
        val api = SequenceApi(listOf(null))
        val events = repository(api).pollPremiumQuotes(srn).toList()
        assertEquals(listOf(PremiumQuotePollEvent.Failed("Unable to fetch quotes (500). Please try again.")), events)
        assertEquals(1, api.requests.size)
    }

    @Test
    fun `one failed poll is skipped and retried`() = runTest {
        val api = SequenceApi(listOf(listBody("pending", 4), null, listBody("complete", 0)))
        val events = repository(api).pollPremiumQuotes(srn).toList()
        assertEquals(listOf(PremiumPollStatus.POLLING, PremiumPollStatus.COMPLETE), statuses(events))
        assertEquals(3, api.requests.size)
    }

    @Test
    fun `three failures in a row stop polling but keep the plans`() = runTest {
        val api = SequenceApi(listOf(listBody("pending", 4), null, null, null, listJson))
        val events = repository(api).pollPremiumQuotes(srn).toList()
        assertEquals(listOf(PremiumPollStatus.POLLING, PremiumPollStatus.STOPPED_ON_ERROR), statuses(events))
        assertEquals(4, api.requests.size)
        assertEquals(2, (events.last() as PremiumQuotePollEvent.Update).quotes.quotes.size)
    }

    @Test
    fun `newer snapshot wins and an insurer missing from it is kept`() {
        val older = samplePremiumQuoteList()
        val newer = older.copy(
            quotes = listOf(older.quotes.first().copy(finalPremium = 2500)), // IFFCO only, re-priced
            declinedInsurers = emptyList()
        )
        val merged = older.mergedWith(newer)
        assertEquals(listOf("ARN-IFFCO" to 2500, "ARN-BAJAJ" to 4031), merged.quotes.map { it.id to it.finalPremium })
        assertEquals(listOf("Future Generali"), merged.declinedInsurers.map { it.insurerName })
    }

    @Test
    fun `blank search reference is rejected without a network call`() = runTest {
        val result = CarInsuranceRepository(ThrowingCarInsuranceApi(), testAppDataManager(), testJson, VehicleInfoSessionProvider())
            .premiumList("")
        assertNull((result as? Resource.Success)?.data)
    }
}
