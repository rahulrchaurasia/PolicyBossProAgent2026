package com.policyboss.policybosspro.feature.syncContact.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.policyboss.policybosspro.feature.syncContact.model.SyncProcessingAction
import com.policyboss.policybosspro.feature.syncContact.model.SyncProcessingEvent
import com.policyboss.policybosspro.feature.syncContact.viewmodel.SyncContactsProcessingViewModel

@Composable
fun SyncProcessingRoute(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SyncContactsProcessingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Prevent accidental back exit while synchronization is actively processing
    BackHandler(enabled = uiState.isSyncing) {
        // Ignored while sync is running to protect ongoing process
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is SyncProcessingEvent.NavigateToHome -> {
                    onNavigateToHome()
                }
                is SyncProcessingEvent.ShowError -> {
                    // Handle error if needed
                }
            }
        }
    }

    SyncProcessingScreen(
        uiState = uiState,
        onCompleteProfileClick = {
            viewModel.onAction(SyncProcessingAction.CompleteProfileClicked)
        },
        onRetryClick = {
            viewModel.onAction(SyncProcessingAction.RetrySync)
        },
        modifier = modifier
    )
}

