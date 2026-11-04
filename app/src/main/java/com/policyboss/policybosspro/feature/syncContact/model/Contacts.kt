package com.policyboss.policybosspro.feature.syncContact.model

data class Contacts(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var birthdate: String = "",
    var nickname: String = ""
)

// Backwards-compatible alias for legacy lowercase class name
typealias contacts = Contacts

