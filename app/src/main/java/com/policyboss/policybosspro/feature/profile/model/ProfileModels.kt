package com.policyboss.policybosspro.feature.profile.model

import androidx.annotation.DrawableRes
import com.policyboss.policybosspro.core.utils.UserProfileParser

data class AccountDetails(
    val ssId: String = "",
    val plan: String = "NA",
    val rmName: String = "",
    val rmMobile: String = "",
    val rmEmail: String = ""
)

data class DisplayInformation(
    val displayName: String = "",
    val displayMobile: String = "",
    val displayEmail: String = "",
    @DrawableRes val displayPictureRes: Int? = null
)

data class PersonalInformation(
    val fullName: String = "",
    val mobile: String = "",
    val dob: String = "",
    val email: String = "",
    val pan: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = ""
)

data class UserProfile(
    val fullName: String = "",
    val initials: String = UserProfileParser.DEFAULT_INITIALS,
    val accountDetails: AccountDetails = AccountDetails(),
    val displayInfo: DisplayInformation = DisplayInformation(),
    val personalInfo: PersonalInformation = PersonalInformation(),
    val useSameAsPosp: Boolean = false
)

