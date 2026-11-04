package com.policyboss.policybosspro.feature.home.ui_Menu

data class MenuUiState(
    val userName: String = "",
    val userSsId: String = "",
    val userFbaId: String = "",
    val appVersion: String = "",
    val isLoggingOut: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val logoutError: String? = null
) {
    val displayName: String
        get() = userName.takeIf { it.isNotBlank() && it != "Guest User" } ?: "Dinesh Das"

    val displaySsId: String
        get() = userSsId.takeIf { it.isNotBlank() && it != "0" }?.let { if (it.startsWith("#")) it else "#$it" } ?: "#123456"

    val displayFbaId: String
        get() = userFbaId.takeIf { it.isNotBlank() && it != "0" }?.let { if (it.startsWith("#")) it else "#$it" } ?: "#123456"

    val userInitials: String
        get() {
            val name = displayName.trim()
            val parts = name.split("\\s+".toRegex()).filter { it.isNotBlank() }
            return when {
                parts.isEmpty() -> "DD"
                parts.size == 1 -> parts[0].take(2).uppercase()
                else -> "${parts[0].first()}${parts[1].first()}".uppercase()
            }
        }
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

