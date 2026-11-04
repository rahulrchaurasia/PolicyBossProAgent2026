package com.policyboss.policybosspro.core.requestbuilder

import kotlinx.serialization.Serializable

@Serializable
data class ContactLeadRequestEntity(
    var fbaid: String,
    var ssid: String,
    val sub_fba_id: String,
    var contactlist: List<ContactlistEntity>? = null,
    var device_id: String,
    var app_version: String,
    var raw_data: String = ""
)

@Serializable
data class ContactlistEntity(
    var mobileno: String,
    var name: String,
    var id: Int
)
