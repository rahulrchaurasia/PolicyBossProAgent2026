package com.policyboss.policybosspro.feature.register.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CheckPospDataRequest(
    val email: String,
    val mobile: String,
    val pan: String
)

