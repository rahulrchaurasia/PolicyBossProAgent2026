package com.policyboss.policybosspro.feature.home.ui_Menu

import com.policyboss.policybosspro.core.utils.UserProfileParser

data class MenuUiState(
    val userName: String = "",
    val initials: String = "",
    val userSsId: String = "",
    val userFbaId: String = "",
    val userDesignation: String = "",
    val userType: String = "",
    val appVersion: String = "",
    val isLoggingOut: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val logoutError: String? = null
) {
    val displayName: String
        get() = userName.takeIf { it.isNotBlank() && it != "Guest User" } ?: "Guest User"

    val displaySsId: String
        get() = userSsId.takeIf { it.isNotBlank() && it != "0" }?.let { if (it.startsWith("#")) it else "#$it" } ?: "NA"

    val displayFbaId: String
        get() = userFbaId.takeIf { it.isNotBlank() && it != "0" }?.let { if (it.startsWith("#")) it else "#$it" } ?: "NA"

    val userInitials: String
        get() = initials.takeIf { it.isNotBlank() }
            ?: UserProfileParser.extractInitials(
                fullName = userName.takeIf { it.isNotBlank() && it != "Guest User" }.orEmpty()
            )
}

sealed interface MenuAction {
    data object OnLogoutClick : MenuAction
    data object OnConfirmLogout : MenuAction
    data object OnDismissLogoutDialog : MenuAction
}

sealed interface MenuEvent {
    data object NavigateToLogin : MenuEvent
    data class ShowError(val message: String) : MenuEvent
}

