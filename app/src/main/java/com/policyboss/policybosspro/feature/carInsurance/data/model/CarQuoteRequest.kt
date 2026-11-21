package com.policyboss.policybosspro.feature.carInsurance.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CarQuoteRequest(
    @SerialName("ss_id")
    val ssId: String = "150253",
    @SerialName("fba_id")
    val fbaId: String = "121743",
    @SerialName("sub_fba_id")
    val subFbaId: String = "0",
    @SerialName("product_id")
    val productId: Int = 1,
    @SerialName("customer_name")
    val customerName: String,
    @SerialName("rto_code")
    val rtoCode: String,
    @SerialName("rto_city")
    val rtoCity: String,
    @SerialName("registered_on")
    val registeredOn: String, // "Individual" or "Company"
    @SerialName("policy_type")
    val policyType: String,
    @SerialName("is_new_car")
    val isNewCar: Boolean,
    @SerialName("make")
    val make: String = "HONDA",
    @SerialName("model")
    val model: String,
    @SerialName("variant")
    val variant: String,
    @SerialName("fuel")
    val fuel: String,
    @SerialName("make_id")
    val makeId: String? = null,
    @SerialName("model_id")
    val modelId: String? = null,
    @SerialName("fuel_id")
    val fuelId: String? = null,
    @SerialName("variant_id")
    val variantId: String? = null,
    @SerialName("registration_date")
    val registrationDate: String,
    @SerialName("manufacturing_date")
    val manufacturingDate: String,
    @SerialName("has_existing_policy")
    val hasExistingPolicy: Boolean = false,
    @SerialName("previous_insurer")
    val previousInsurer: String? = null,
    @SerialName("tp_insurer")
    val tpInsurer: String? = null,
    @SerialName("policy_expiry_date")
    val policyExpiryDate: String? = null,
    @SerialName("has_claim")
    val hasClaim: Boolean? = null,
    @SerialName("ncb_status")
    val ncbStatus: String? = null,
    @SerialName("consent")
    val consent: Boolean = true
)
