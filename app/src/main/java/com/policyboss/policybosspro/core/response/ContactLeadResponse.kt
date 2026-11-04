package com.policyboss.policybosspro.core.response

import kotlinx.serialization.Serializable

@Serializable
data class ContactLeadResponse(
    val masterData: String? = null
) : APIResponse()
