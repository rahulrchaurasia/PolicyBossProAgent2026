package com.policyboss.policybosspro.feature.home.ui_Menu

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.policyboss.policybosspro.ui.ObserveAsEvents

@Composable
fun MenuRoute(
    contentPadding: PaddingValues,
    onCloseClick: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogoutSuccess: () -> Unit,
    viewModel: MenuViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ObserveAsEvents(flow = viewModel.event) { event ->
        when (event) {
            is MenuEvent.NavigateToLogin -> {
                onLogoutSuccess()
            }
            is MenuEvent.ShowError -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    MenuScreen(
        contentPadding = contentPadding,
        onCloseClick = onCloseClick,
        onNavigateToProfile = onNavigateToProfile,
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

