package com.policyboss.policybosspro.feature.carInsurance.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CarQuoteResponse(
    @SerialName("status")
    val status: String? = null,
    @SerialName("message")
    val message: String? = null,
    @SerialName("quote_id")
    val quoteId: String? = null,
    @SerialName("quote_url")
    val quoteUrl: String? = null,
    @SerialName("quotes_count")
    val quotesCount: Int? = 0
)
