package com.policyboss.policybosspro.feature.carInsurance

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
              "Premium_Breakup": { "final_premium": 2471, "QUOTE_ID": "BA503150VPC15198449" },
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
          ],
          "Zextra_Response": null,
          "Addon_Request": {},
          "Error_Master": { "LM002": "Un-Identified Error", "LM009": "Vehicle Underwriting Restriction" }
        }
    """.trimIndent()

    private val srn = "SRN-N3AZQFZP-S7TM-OQEH-UMET-IKYPNC0OYYGH_21024837"

    private fun parseList() = testJson.decodeFromString(PremiumListResponseDto.serializer(), listJson)

    @Test
    fun `insurer plans become quotes, cheapest first`() {
        val list = parseList().toPremiumQuoteList(srn)

        assertEquals("12698778", list.crn)
        assertEquals(srn, list.searchReferenceNumber)
        assertEquals(listOf("IFFCO Tokio", "Bajaj Allianz"), list.quotes.map { it.insurerName })

        val iffco = list.quotes.first()
        assertEquals(2471, iffco.finalPremium)
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

        assertEquals(listOf("Future Generali", "Oriental Insurance"), declined.map { it.insurerName })
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
    fun `premium_summary keeps Summary and Master but not Request`() {
        val dto = testJson.decodeFromString(
            PremiumSummaryResponseDto.serializer(),
            """
            {"Status":"Not_Cached",
             "Summary":{"Request_Unique_Id":"$srn","customer_reference_number":"CRN-3XG7Z6VO",
               "User_Data_Id":21024837,"Created_On":"2026-11-26T09:29:05.048Z","PB_CRN":12698778,
               "Client_Id":2,"Request_Id":41341670,"Status":"complete","Total":18,"Pending":0,
               "Complete":18,"Success":14,"Fail":4,"Total_Execution_Time":9.34},
             "Request":{"vehicle_id":6917,"secret_key":"SECRET-X"},
             "Addon_Request":{},
             "Master":{"Vehicle":{"Vehicle_ID":6917,"Insurer_101":{"Premium_Status":1}},
                       "Rto":{"VehicleCity_RTOCode":"MH01","Insurer_2":{"RI_Master":{"Insurer_Rto_Code":"MH01"}}}}}
            """.trimIndent()
        )

        assertEquals("Not_Cached", dto.status)
        assertEquals(41341670L, dto.summary?.requestId)
        assertEquals(9.34, dto.summary?.totalExecutionTime)
        assertTrue(dto.master!!.vehicle!!.containsKey("Insurer_101"))
        assertEquals("MH01", dto.master!!.rto!!["VehicleCity_RTOCode"]!!.jsonPrimitive.content)
        val encoded = testJson.encodeToJsonElement(PremiumSummaryResponseDto.serializer(), dto).jsonObject
        assertTrue("Request is not mapped", "Request" !in encoded)
    }

    // -----------------------------------------------------------------
    // Repository polling
    // -----------------------------------------------------------------

    private class PollingApi(private val summaries: List<String>, private val listBody: String) : ThrowingCarInsuranceApi() {
        val summaryRequests = mutableListOf<PremiumSearchRequest>()
        val listRequests = mutableListOf<PremiumSearchRequest>()

        override suspend fun premiumSummary(request: PremiumSearchRequest): Response<PremiumSummaryResponseDto> {
            summaryRequests += request
            val json = summaries[(summaryRequests.size - 1).coerceAtMost(summaries.lastIndex)]
            return Response.success(testJson.decodeFromString(PremiumSummaryResponseDto.serializer(), json))
        }

        override suspend fun premiumList(request: PremiumSearchRequest): Response<PremiumListResponseDto> {
            listRequests += request
            return Response.success(testJson.decodeFromString(PremiumListResponseDto.serializer(), listBody))
        }
    }

    private fun summary(status: String, pending: Int) =
        """{"Status":"Not_Cached","Summary":{"Request_Unique_Id":"$srn","Status":"$status","Total":18,"Pending":$pending}}"""

    private fun repository(api: PollingApi) =
        CarInsuranceRepository(api, testAppDataManager(), testJson, VehicleInfoSessionProvider())

    @Test
    fun `polls summary until complete, then loads the list with the search reference`() = runTest {
        val api = PollingApi(
            summaries = listOf(summary("pending", 10), summary("pending", 3), summary("complete", 0)),
            listBody = listJson
        )
        val progress = mutableListOf<Int>()

        val result = repository(api).awaitPremiumQuotes(srn, pollInterval = Duration.ZERO) { progress += it.pending }

        assertEquals(listOf(10, 3, 0), progress)
        assertEquals(3, api.summaryRequests.size)
        assertEquals(1, api.listRequests.size)
        val body = testJson.encodeToJsonElement(PremiumSearchRequest.serializer(), api.listRequests.single()).jsonObject
        assertEquals(setOf("search_reference_number", "secret_key", "client_key"), body.keys)
        assertEquals(srn, api.listRequests.single().searchReferenceNumber)
        assertEquals(2, (result as Resource.Success).data.quotes.size)
    }

    @Test
    fun `timeout still loads the quotes received so far`() = runTest {
        val api = PollingApi(summaries = listOf(summary("pending", 5)), listBody = listJson)

        val result = repository(api).awaitPremiumQuotes(srn, pollInterval = Duration.ZERO, maxPolls = 4)

        assertEquals(4, api.summaryRequests.size)
        assertEquals(1, api.listRequests.size)
        assertTrue(result is Resource.Success)
    }

    @Test
    fun `summary http error stops before the list call`() = runTest {
        val api = object : ThrowingCarInsuranceApi() {
            override suspend fun premiumSummary(request: PremiumSearchRequest): Response<PremiumSummaryResponseDto> =
                Response.error(500, "{}".toResponseBody())
        }
        val result = CarInsuranceRepository(api, testAppDataManager(), testJson, VehicleInfoSessionProvider())
            .awaitPremiumQuotes(srn, pollInterval = Duration.ZERO)

        assertEquals(Resource.Error("Unable to fetch quotes (500). Please try again."), result)
    }

    @Test
    fun `blank search reference is rejected without a network call`() = runTest {
        val result = CarInsuranceRepository(ThrowingCarInsuranceApi(), testAppDataManager(), testJson, VehicleInfoSessionProvider())
            .premiumList("")
        assertNull((result as? Resource.Success)?.data)
    }
}
