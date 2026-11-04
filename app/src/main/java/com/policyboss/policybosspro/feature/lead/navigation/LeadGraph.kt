package com.policyboss.policybosspro.feature.lead.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.policyboss.policybosspro.feature.lead.ui.LeadDetailsScreen
import com.policyboss.policybosspro.feature.lead.ui.LeadScreen
import com.policyboss.policybosspro.feature.lead.ui.LeadViewModel
import com.policyboss.policybosspro.navigation.AppNavigator
import com.policyboss.policybosspro.navigation.Dest

fun NavGraphBuilder.leadGraph(
    appNavigator: AppNavigator,
    padding: PaddingValues
) {
    navigation<Dest.ClaimGraph>(
        startDestination = Dest.Leads
    ) {
        composable<Dest.Leads> {
            val viewModel: LeadViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()
            val snackbarHostState = com.policyboss.policybosspro.ui.components.snackBar.LocalAppSnackbar.current
            
            androidx.compose.runtime.LaunchedEffect(viewModel.uiEvent) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is com.policyboss.policybosspro.feature.lead.model.LeadUiEvent.ShowSnackbar -> {
                            snackbarHostState.showSnackbar(event.message)
                        }
                    }
                }
            }
            
            if (uiState.selectedLead == null) {
                LeadScreen(
                    uiState = uiState,
                    onAction = viewModel::onAction,
                    contentPadding = padding
                )
            } else {
                LeadDetailsScreen(
                    lead = uiState.selectedLead!!,
                    uiState = uiState,
                    onBack = { viewModel.onAction(com.policyboss.policybosspro.feature.lead.model.LeadAction.CloseLeadDetails) },
                    onAction = viewModel::onAction
                )
            }
        }
    }
}
