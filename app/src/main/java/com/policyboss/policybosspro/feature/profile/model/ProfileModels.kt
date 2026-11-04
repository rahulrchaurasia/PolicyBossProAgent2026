package com.policyboss.policybosspro.feature.profile.model

import androidx.annotation.DrawableRes
import com.policyboss.policybosspro.R

data class AccountDetails(
    val ssId: String = "1449",
    val plan: String = "NA",
    val rmName: String = "Varun Kaushik",
    val rmMobile: String = "99100 34425",
    val rmEmail: String = "varun.kaushik@policyboss.com"
)

data class DisplayInformation(
    val displayName: String = "Arvind Sharma",
    val displayMobile: String = "99345 34252",
    val displayEmail: String = "arvindsharma322@policyboss.com",
    @DrawableRes val displayPictureRes: Int? = R.drawable.avatar1
)

data class PersonalInformation(
    val fullName: String = "Dinesh Das",
    val mobile: String = "99100 34425",
    val dob: String = "24 - 01 - 1988",
    val email: String = "dineshdas@policyboss.com",
    val pan: String = "HPFOO4389L",
    val address: String = "12B/989, Poonam Nagar, Malad East",
    val city: String = "Mumbai_G",
    val state: String = "Maharastra",
    val pincode: String = "400063"
)

data class UserProfile(
    val fullName: String = "Dinesh Das",
    val initials: String = "DD",
    val accountDetails: AccountDetails = AccountDetails(),
    val displayInfo: DisplayInformation = DisplayInformation(),
    val personalInfo: PersonalInformation = PersonalInformation(),
    val useSameAsPosp: Boolean = false
)

