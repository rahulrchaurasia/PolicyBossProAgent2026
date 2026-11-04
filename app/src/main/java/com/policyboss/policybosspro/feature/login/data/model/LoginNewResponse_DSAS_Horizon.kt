package com.policyboss.policybosspro.feature.login.data.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Custom lenient serializer that prevents crashes when the backend returns:
 * - A valid JSON object `{ ... }` -> parsed to [T]
 * - A String like `"NA"`, `""`, or `"null"` -> gracefully returns null
 * - An empty Array `[]` -> gracefully returns null
 * - null -> returns null
 */
@OptIn(ExperimentalSerializationApi::class)
open class LenientNullableSerializer<T : Any>(
    private val elementSerializer: KSerializer<T>
) : KSerializer<T?> {

    override val descriptor: SerialDescriptor = elementSerializer.descriptor

    override fun deserialize(decoder: Decoder): T? {
        val jsonDecoder = decoder as? JsonDecoder ?: return if (decoder.decodeNotNullMark()) elementSerializer.deserialize(decoder) else null
        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonObject -> jsonDecoder.json.decodeFromJsonElement(elementSerializer, element)
            is kotlinx.serialization.json.JsonArray -> {
                // Empty array [] represents empty PHP associative array
                if (element.isEmpty()) {
                    null
                } else if (element.firstOrNull() is JsonObject) {
                    // Single record wrapped in an array [{ ... }]
                    jsonDecoder.json.decodeFromJsonElement(elementSerializer, element.first())
                } else {
                    null
                }
            }
            else -> null // Safely handles "NA", "", null, false, etc.
        }
    }

    override fun serialize(encoder: Encoder, value: T?) {
        if (value == null) {
            encoder.encodeNull()
        } else {
            elementSerializer.serialize(encoder, value)
        }
    }
}

/**
 * Custom serializer for strings that may arrive as numbers, "NA", "null", or null.
 */
object LenientStringSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LenientString", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String {
        val jsonDecoder = decoder as? JsonDecoder ?: return decoder.decodeString()
        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive -> {
                val str = element.content.trim()
                if (str.equals("NA", ignoreCase = true) || str.equals("null", ignoreCase = true)) "" else str
            }
            is JsonNull -> ""
            else -> ""
        }
    }

    override fun serialize(encoder: Encoder, value: String) {
        encoder.encodeString(value)
    }
}

/**
 * Custom serializer for longs that may arrive as String, "NA", or null.
 */
@OptIn(ExperimentalSerializationApi::class)
object LenientLongSerializer : KSerializer<Long?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LenientLong", PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): Long? {
        val jsonDecoder = decoder as? JsonDecoder ?: return decoder.decodeLong()
        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive -> element.content.trim().toLongOrNull()
            else -> null
        }
    }

    override fun serialize(encoder: Encoder, value: Long?) {
        if (value == null) encoder.encodeNull() else encoder.encodeLong(value)
    }
}

/**
 * Custom serializer for booleans that may arrive as String ("true", "1", "yes"), Int (1, 0), "NA", or null.
 */
@OptIn(ExperimentalSerializationApi::class)
object LenientBooleanSerializer : KSerializer<Boolean?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LenientBoolean", PrimitiveKind.BOOLEAN)

    override fun deserialize(decoder: Decoder): Boolean? {
        val jsonDecoder = decoder as? JsonDecoder ?: return decoder.decodeBoolean()
        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive -> {
                val str = element.content.trim()
                when {
                    str.equals("true", ignoreCase = true) || str == "1" || str.equals("yes", ignoreCase = true) -> true
                    str.equals("false", ignoreCase = true) || str == "0" || str.equals("no", ignoreCase = true) -> false
                    else -> null
                }
            }
            else -> null
        }
    }

    override fun serialize(encoder: Encoder, value: Boolean?) {
        if (value == null) encoder.encodeNull() else encoder.encodeBoolean(value)
    }
}

// Concrete serializers for Horizon response fields
object PospSerializer : LenientNullableSerializer<POSP>(POSP.serializer())
object PospUserSerializer : LenientNullableSerializer<POSP_USER>(POSP_USER.serializer())
object EmpSerializer : LenientNullableSerializer<EMP>(EMP.serializer())
object SubUserSerializer : LenientNullableSerializer<SUB_USER>(SUB_USER.serializer())
object DeviceSerializer : LenientNullableSerializer<DEVICE>(DEVICE.serializer())
object HrSerializer : LenientNullableSerializer<HR>(HR.serializer())

/**
 * Strongly typed response model for Horizon DSAS User Details API (GET posps/dsas/view/{userId}).
 * Safely handles polymorphic "NA", [], and dirty types without throwing runtime exceptions.
 */
@Serializable
data class LoginNewResponse_DSAS_Horizon(
    @SerialName("status")
    val status: String? = null,

    @SerialName("user_type")
    val user_type: String? = null,

    @SerialName("Ss_Id")
    @Serializable(with = LenientStringSerializer::class)
    val Ss_Id: String? = null,

    @SerialName("channel")
    val channel: String? = null,

    @SerialName("product")
    val product: String? = null,

    @SerialName("POSP")
    @Serializable(with = PospSerializer::class)
    val POSP: POSP? = null,

    @SerialName("POSP_USER")
    @Serializable(with = PospUserSerializer::class)
    val POSP_USER: POSP_USER? = null,

    @SerialName("EMP")
    @Serializable(with = EmpSerializer::class)
    val EMP: EMP? = null,

    @SerialName("SUB_USER")
    @Serializable(with = SubUserSerializer::class)
    val SUB_USER: SUB_USER? = null,

    @SerialName("DEVICE")
    @Serializable(with = DeviceSerializer::class)
    val DEVICE: DEVICE? = null,

    @SerialName("HR")
    @Serializable(with = HrSerializer::class)
    val HR: HR? = null
) {
    val isSuccessful: Boolean
        get() = status?.equals("SUCCESS", ignoreCase = true) == true

    val userType: String?
        get() = user_type

    // =========================================================================
    // TYPE-SAFE CONVENIENCE ACCESSORS (Replaces fragile Map<*, *> casting)
    // =========================================================================

    /**
     * Resolves SS ID across root Ss_Id, POSP, and EMP structures.
     */
    val extractedSsId: String
        get() {
            if (!Ss_Id.isNullOrBlank()) return Ss_Id.trim()
            if (!POSP?.Ss_Id.isNullOrBlank()) return POSP.Ss_Id.trim()
            if (!EMP?.Emp_Id.isNullOrBlank()) return EMP.Emp_Id.trim()
            return "0"
        }

    /**
     * Resolves FBA ID across POSP and EMP structures. Fallback is "0".
     */
    val extractedFbaId: String
        get() {
            val pospFba = POSP?.Fba_Id?.trim()
            if (!pospFba.isNullOrBlank()) return pospFba
            val empFba = EMP?.FBA_ID?.trim()
            if (!empFba.isNullOrBlank()) return empFba
            return "0"
        }

    /**
     * Resolves user full name across POSP_USER, POSP, and EMP.
     */
    val extractedName: String
        get() {
            val nameOnPan = POSP_USER?.Name_On_PAN?.trim()
            if (!nameOnPan.isNullOrBlank()) return nameOnPan

            val firstName = POSP?.First_Name.orEmpty().trim()
            val lastName = POSP?.Last_Name.orEmpty().trim()
            val combined = "$firstName $lastName".trim()
            if (combined.isNotBlank()) return combined

            val empName = EMP?.Emp_Name?.trim()
            if (!empName.isNullOrBlank()) return empName

            return ""
        }

    /**
     * Resolves primary registered mobile number.
     */
    val extractedMobile: String
        get() {
            val pospUserMobile = POSP_USER?.Mobile_No?.trim()
            if (!pospUserMobile.isNullOrBlank()) return pospUserMobile

            val pospMobile = POSP?.Mobile_No?.trim()
            if (!pospMobile.isNullOrBlank()) return pospMobile

            return EMP?.Mobile_Number?.trim().orEmpty()
        }

    /**
     * Resolves primary registered email address.
     */
    val extractedEmail: String
        get() {
            val pospUserEmail = POSP_USER?.Email_Id?.trim()
            if (!pospUserEmail.isNullOrBlank()) return pospUserEmail

            val pospEmail = POSP?.Email_Id?.trim()
            if (!pospEmail.isNullOrBlank()) return pospEmail

            return EMP?.Email_Id?.trim().orEmpty()
        }

    /**
     * Resolves permanent pincode safely as a clean String.
     */
    val extractedPincode: String
        get() = POSP_USER?.Permanant_Pincode?.trim().orEmpty()

    /**
     * Resolves designation from HR.
     */
    val extractedDesignation: String
        get() = HR?.Designation?.trim().orEmpty()
}

@Serializable
data class EMP(
    @SerialName("AdharCardNumber") var AdharCardNumber: String = "",
    @SerialName("Alternate_Email") var Alternate_Email: String = "",
    @SerialName("Alternate_Number") var Alternate_Number: String = "",
    @SerialName("BloodGroup") var BloodGroup: String = "",
    @SerialName("Branch") var Branch: String = "",
    @SerialName("CreatedOn") var CreatedOn: String = "",
    @SerialName("DOB") var DOB: String = "",
    @SerialName("DOJ") var DOJ: String = "",
    @SerialName("Dailer_Id") var Dailer_Id: String = "",
    @SerialName("DateOfBirth") var DateOfBirth: String = "",
    @SerialName("Department_ID") var Department_ID: String = "",
    @SerialName("Designation_ID") var Designation_ID: String = "",
    @SerialName("Email_Id") var Email_Id: String = "",
    @SerialName("Emp_Code") var Emp_Code: String = "",
    @SerialName("Emp_Id") @Serializable(with = LenientStringSerializer::class) var Emp_Id: String = "",
    @SerialName("Emp_Name") var Emp_Name: String = "",
    @SerialName("Emp_Password") var Emp_Password: String = "",
    @SerialName("FBA_ID") @Serializable(with = LenientStringSerializer::class) var FBA_ID: String = "",
    @SerialName("IsActive") var IsActive: String = "",
    @SerialName("IsPasswordChanged") var IsPasswordChanged: String = "",
    @SerialName("LastPasswordChangedDate") var LastPasswordChangedDate: String = "",
    @SerialName("Mobile_Number") var Mobile_Number: String = "",
    @SerialName("Pan") var Pan: String = "",
    @SerialName("Reporting_Manager_Emp_Id") var Reporting_Manager_Emp_Id: String = "",
    @SerialName("Role_ID") var Role_ID: String = "",
    @SerialName("UID") var UID: String = "",
    @SerialName("Updated_On") var Updated_On: String = "",
    @SerialName("VendorCode") var VendorCode: String = "",
    @SerialName("_id") var _id: String = ""
)

@Serializable
data class POSP(
    @SerialName("Agent_City") var Agent_City: String = "",
    @SerialName("App_Installed_On") var App_Installed_On: String = "",
    @SerialName("Channel") var Channel: String = "",
    @SerialName("Contact_Sync_On") var Contact_Sync_On: String = "",
    @SerialName("Created_On") var Created_On: String = "",
    @SerialName("Cust_Id") var Cust_Id: String = "",
    @SerialName("ERPID_CreatedDate") var ERPID_CreatedDate: String = "",
    @SerialName("Email_Id") var Email_Id: String = "",
    @SerialName("Erp_Id") var Erp_Id: String = "",
    @SerialName("FOS_Code") var FOS_Code: String = "",
    @SerialName("Father_Name") var Father_Name: String = "",
    @SerialName("Fba_Id") @Serializable(with = LenientStringSerializer::class) var Fba_Id: String? = "",
    @SerialName("First_Name") var First_Name: String = "",
    @SerialName("Gender") var Gender: String = "",
    @SerialName("IsFOS") var IsFOS: String = "",
    @SerialName("Is_Active") @Serializable(with = LenientBooleanSerializer::class) var Is_Active: Boolean? = false,
    @SerialName("Is_App_Installed") var Is_App_Installed: String = "",
    @SerialName("Is_Contact_Sync") var Is_Contact_Sync: String = "",
    @SerialName("Last_Name") var Last_Name: String = "",
    @SerialName("Middle_Name") var Middle_Name: String = "",
    @SerialName("Mobile_No") var Mobile_No: String = "",
    @SerialName("Posp_Id") var Posp_Id: String = "",
    @SerialName("Posp_Onboarding_Photo") var Posp_Onboarding_Photo: String = "",
    @SerialName("Recruitment_Status") var Recruitment_Status: String = "",
    @SerialName("RegAmount") var RegAmount: String = "",
    @SerialName("Sm_Posp_Id") var Sm_Posp_Id: String = "",
    @SerialName("Sm_Posp_Name") var Sm_Posp_Name: String = "",
    @SerialName("Ss_Id") @Serializable(with = LenientStringSerializer::class) var Ss_Id: String = "",
    @SerialName("Telephone_No") var Telephone_No: String = "",
    @SerialName("_id") var _id: String = ""
)

@Serializable
data class POSP_USER(
    @SerialName("Name_On_PAN") var Name_On_PAN: String = "",
    @SerialName("User_Id") var User_Id: String = "",
    @SerialName("Mobile_No") var Mobile_No: String = "",
    @SerialName("Email_Id") var Email_Id: String = "",
    @SerialName("Erp_Id") var Erp_Id: String = "",
    @SerialName("Permanant_Add1") var Permanant_Add1: String = "",
    @SerialName("Permanant_Add2") var Permanant_Add2: String = "",
    @SerialName("Permanant_Add3") var Permanant_Add3: String = "",
    @SerialName("Permanant_City") var Permanant_City: String = "",
    @SerialName("Permanant_Pincode") @Serializable(with = LenientStringSerializer::class) var Permanant_Pincode: String = "",
    @SerialName("Permanant_State") var Permanant_State: String = "",
    @SerialName("Permanant_Country") var Permanant_Country: String = "",
    @SerialName("Gender") var Gender: String = "",
    @SerialName("Birthdate") var Birthdate: String = ""
)

@Serializable
data class SUB_USER(
    @SerialName("Ss_Id") @Serializable(with = LenientLongSerializer::class) val Ss_Id: Long? = null,
    @SerialName("Sub_FBA_ID") @Serializable(with = LenientLongSerializer::class) val Sub_FBA_ID: Long? = null,
    @SerialName("First_Name") val First_Name: String? = null,
    @SerialName("Last_Name") val Last_Name: String? = null,
    @SerialName("Email_ID") val Email_ID: String? = null,
    @SerialName("Mobile") val Mobile: String? = null
)

@Serializable
data class DEVICE(
    @SerialName("OS_Detail") var OS_Detail: String = "",
    @SerialName("App_Version") var App_Version: String = "",
    @SerialName("Device_Name") var Device_Name: String = "",
    @SerialName("Device_Identifier") var Device_Identifier: String = "",
    @SerialName("Created_On") var Created_On: String = "",
    @SerialName("Activated_On") var Activated_On: String = ""
)

@Serializable
data class HR(
    @SerialName("Designation") var Designation: String? = null
)
