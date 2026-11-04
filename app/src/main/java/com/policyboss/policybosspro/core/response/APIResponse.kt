package com.policyboss.policybosspro.core.response

import kotlinx.serialization.Serializable

@Serializable
open class APIResponse(
    val Message: String = "",
    val Status: String = "",
    val StatusNo: Int = -1
)
