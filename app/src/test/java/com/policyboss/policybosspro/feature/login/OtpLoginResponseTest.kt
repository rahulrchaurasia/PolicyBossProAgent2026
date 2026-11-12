package com.policyboss.policybosspro.feature.login

import com.policyboss.policybosspro.core.serializer.LenientNullableStringSerializer
import com.policyboss.policybosspro.feature.login.data.model.OtpLoginResponse
import com.policyboss.policybosspro.feature.login.data.model.OtpUserMsg
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OtpLoginResponseTest {

    private val json = OtpLoginResponse.defaultJson

    @Serializable
    private data class SerializerProbe(
        @Serializable(with = LenientNullableStringSerializer::class)
        val field: String? = null
    )

    // =========================================================================
    // 1. LenientNullableStringSerializer Tests
    // =========================================================================

    @Test
    fun `serializer parses valid string and strips surrounding quotes`() {
        assertEquals(
            "Rahul",
            json.decodeFromString(LenientNullableStringSerializer, "\"Rahul\"")
        )
        assertEquals(
            "Rahul",
            json.decodeFromString<SerializerProbe>("""{"field": "\"Rahul\""}""").field
        )
    }

    @Test
    fun `serializer converts numeric scalars to string and preserves zero`() {
        assertEquals(
            "9773113793",
            json.decodeFromString(LenientNullableStringSerializer, "9773113793")
        )
        assertEquals(
            "0",
            json.decodeFromString<SerializerProbe>("""{"field": "0"}""").field
        )
        assertEquals(
            "0",
            json.decodeFromString<SerializerProbe>("""{"field": 0}""").field
        )
    }

    @Test
    fun `serializer sanitizes NA, N_A, null, undefined, blank, and JsonNull to null`() {
        val dirtyInputs = listOf(
            "\"NA\"",
            "\"na\"",
            "\"N/A\"",
            "\"n/a\"",
            "\"null\"",
            "\"NULL\"",
            "\"undefined\"",
            "\"UNDEFINED\"",
            "\"\"",
            "\"   \"",
            "null"
        )

        dirtyInputs.forEach { raw ->
            val decoded = json.decodeFromString(LenientNullableStringSerializer, raw)
            assertNull("Expected null for raw input $raw", decoded)

            val wrapped = json.decodeFromString<SerializerProbe>("""{"field": $raw}""")
            assertNull("Expected null for field=$raw", wrapped.field)
        }

        assertNull(json.decodeFromJsonElement(LenientNullableStringSerializer, JsonNull))
    }

    @Test
    fun `serializer maps non-primitive JsonArray and JsonObject to null`() {
        assertNull(json.decodeFromString(LenientNullableStringSerializer, "[]"))
        assertNull(json.decodeFromString(LenientNullableStringSerializer, """["Rahul"]"""))
        assertNull(json.decodeFromString(LenientNullableStringSerializer, "{}"))
        assertNull(json.decodeFromString(LenientNullableStringSerializer, """{"nested":"value"}"""))

        assertNull(json.decodeFromJsonElement(LenientNullableStringSerializer, JsonArray(emptyList())))
        assertNull(
            json.decodeFromJsonElement(
                LenientNullableStringSerializer,
                buildJsonObject { put("key", "value") }
            )
        )

        assertNull(json.decodeFromString<SerializerProbe>("""{"field": []}""").field)
        assertNull(json.decodeFromString<SerializerProbe>("""{"field": {}}""").field)
    }

    // =========================================================================
    // 2. OtpUserMsg.ssIdString Extraction Tests
    // =========================================================================

    @Test
    fun `ssIdString accepts numeric strings and unquoted integers`() {
        val stringId = json.decodeFromString<OtpUserMsg>("""{"Ss_Id": "12345"}""")
        assertEquals("12345", stringId.ssIdString)

        val intId = json.decodeFromString<OtpUserMsg>("""{"Ss_Id": 12345}""")
        assertEquals("12345", intId.ssIdString)

        val lowerCaseId = json.decodeFromString<OtpUserMsg>("""{"ss_id": 67890}""")
        assertEquals("67890", lowerCaseId.ssIdString)
    }

    @Test
    fun `ssIdString strictly rejects arrays, objects, zero, and dirty literals`() {
        val invalidPayloads = listOf(
            """{"Ss_Id": []}""",
            """{"Ss_Id": ["12345"]}""",
            """{"Ss_Id": {}}""",
            """{"Ss_Id": {"id": 12345}}""",
            """{"Ss_Id": "0"}""",
            """{"Ss_Id": 0}""",
            """{"Ss_Id": "NA"}""",
            """{"Ss_Id": "N/A"}""",
            """{"Ss_Id": "null"}""",
            """{"Ss_Id": "undefined"}""",
            """{"Ss_Id": ""}""",
            """{"Ss_Id": null}"""
        )

        invalidPayloads.forEach { payload ->
            val msg = json.decodeFromString<OtpUserMsg>(payload)
            assertNull("Expected ssIdString to be null for $payload", msg.ssIdString)
        }

        // Direct construction guard with JsonNull / JsonObject / JsonArray
        assertNull(OtpUserMsg(ssId = JsonNull).ssIdString)
        assertNull(OtpUserMsg(ssId = JsonArray(listOf(JsonPrimitive("12345")))).ssIdString)
        assertNull(OtpUserMsg(ssId = JsonObject(emptyMap())).ssIdString)
    }

    // =========================================================================
    // 3. OtpLoginResponse Casing Aliases, Resilience & isSuccessful Tests
    // =========================================================================

    @Test
    fun `OtpLoginResponse resolves lowercase msg object and snake_case user fields`() {
        val payload = """
            {
                "status": "SUCCESS",
                "msg": {
                    "name": "Rahul Chaurasia",
                    "mobile_no": 9773113793,
                    "ss_id": 150252,
                    "otp_status": "SUCCESS",
                    "email_id": "rahul@policyboss.com"
                }
            }
        """.trimIndent()

        val response = json.decodeFromString<OtpLoginResponse>(payload)
        assertTrue(response.isSuccessful)
        assertNull(response.errorMessage)

        val user = response.getUserMsg()
        assertNotNull(user)
        assertEquals("Rahul Chaurasia", user?.effectiveName)
        assertEquals("9773113793", user?.effectiveMobileNo)
        assertEquals("150252", user?.ssIdString)
        assertEquals("SUCCESS", user?.effectiveOtpStatus)
        assertEquals("rahul@policyboss.com", user?.effectiveEmailId)
    }

    @Test
    fun `isSuccessful returns true when Status is SUCCESS and effectiveOtpStatus is SUCCESS or null`() {
        val explicitSuccess = """
            {
                "Status": "success",
                "Msg": {
                    "Name": "Rahul",
                    "Ss_Id": "150252",
                    "OTP_Status": "success"
                }
            }
        """.trimIndent()
        assertTrue(json.decodeFromString<OtpLoginResponse>(explicitSuccess).isSuccessful)

        val omittedOtpStatus = """
            {
                "Status": "SUCCESS",
                "Msg": {
                    "Name": "Rahul",
                    "Ss_Id": "150252"
                }
            }
        """.trimIndent()
        assertTrue(json.decodeFromString<OtpLoginResponse>(omittedOtpStatus).isSuccessful)

        val naOtpStatusSanitizedToNull = """
            {
                "Status": "SUCCESS",
                "Msg": {
                    "Name": "Rahul",
                    "Ss_Id": "150252",
                    "OTP_Status": "NA"
                }
            }
        """.trimIndent()
        assertTrue(json.decodeFromString<OtpLoginResponse>(naOtpStatusSanitizedToNull).isSuccessful)
    }

    @Test
    fun `isSuccessful returns false on non-SUCCESS status, failed otpStatus, or non-object msg`() {
        val failStatus = """
            {
                "Status": "FAIL",
                "Msg": {
                    "Name": "Rahul",
                    "OTP_Status": "SUCCESS"
                }
            }
        """.trimIndent()
        assertFalse(json.decodeFromString<OtpLoginResponse>(failStatus).isSuccessful)

        val failedOtpStatus = """
            {
                "Status": "SUCCESS",
                "Msg": {
                    "Name": "Rahul",
                    "OTP_Status": "FAILED"
                }
            }
        """.trimIndent()
        assertFalse(json.decodeFromString<OtpLoginResponse>(failedOtpStatus).isSuccessful)

        val stringMsgInsteadOfObject = """
            {
                "Status": "SUCCESS",
                "Msg": "Some message"
            }
        """.trimIndent()
        assertFalse(json.decodeFromString<OtpLoginResponse>(stringMsgInsteadOfObject).isSuccessful)

        val arrayMsgInsteadOfObject = """
            {
                "Status": "SUCCESS",
                "msg": []
            }
        """.trimIndent()
        assertFalse(json.decodeFromString<OtpLoginResponse>(arrayMsgInsteadOfObject).isSuccessful)
    }

    @Test
    fun `OtpLoginResponse safely handles malformed types on status and message and extracts lowercase msg errors`() {
        val malformedStatusAndMessage = """
            {
                "Status": [],
                "status": "N/A",
                "Message": {},
                "message": "undefined",
                "msg": "Mobile number not registered"
            }
        """.trimIndent()

        val parsed = json.decodeFromString<OtpLoginResponse>(malformedStatusAndMessage)
        assertFalse(parsed.isSuccessful)
        assertNull(parsed.effectiveStatus)
        assertEquals("Mobile number not registered", parsed.errorMessage)

        val nestedObjectErrorLowerMsg = """
            {
                "status": "FAIL",
                "msg": {
                    "msg": null,
                    "message": "Account locked due to invalid attempts"
                }
            }
        """.trimIndent()
        val nestedParsed = json.decodeFromString<OtpLoginResponse>(nestedObjectErrorLowerMsg)
        assertFalse(nestedParsed.isSuccessful)
        assertEquals("Account locked due to invalid attempts", nestedParsed.errorMessage)
    }
}

