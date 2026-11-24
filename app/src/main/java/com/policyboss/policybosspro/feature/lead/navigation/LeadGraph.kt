package com.policyboss.policybosspro.feature.lead.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.policyboss.policybosspro.anim.NavigationAnimations
import com.policyboss.policybosspro.feature.home.model.home.HomeLead
import com.policyboss.policybosspro.feature.lead.model.LeadAction
import com.policyboss.policybosspro.feature.lead.model.LeadUiEvent
import com.policyboss.policybosspro.feature.lead.ui.LeadDetailsScreen
import com.policyboss.policybosspro.feature.lead.ui.LeadScreen
import com.policyboss.policybosspro.feature.lead.ui.LeadViewModel
import com.policyboss.policybosspro.navigation.AppNavigator
import com.policyboss.policybosspro.navigation.Dest
import com.policyboss.policybosspro.ui.ObserveAsEvents
import com.policyboss.policybosspro.ui.components.snackBar.LocalAppSnackbar
import kotlinx.coroutines.launch

/**
 * Leads tab flow: Leads (tab root, bottom bar visible) → LeadDetails (bottom bar hidden).
 *
 * [LeadViewModel] is scoped to the [Dest.ClaimGraph] entry so the list and the details
 * screen share the same state (selected lead, dialogs, cached leads).
 */
fun NavGraphBuilder.leadGraph(
    appNavigator: AppNavigator,
    padding: PaddingValues
) {
    navigation<Dest.ClaimGraph>(
        startDestination = Dest.Leads
    ) {
        composable<Dest.Leads> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                appNavigator.getBackStackEntry<Dest.ClaimGraph>()
            }
            val viewModel: LeadViewModel = hiltViewModel(parentEntry)
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val snackbarHostState = LocalAppSnackbar.current
            val scope = rememberCoroutineScope()

            ObserveAsEvents(flow = viewModel.uiEvent) { event ->
                when (event) {
                    is LeadUiEvent.ShowSnackbar -> scope.launch { snackbarHostState.showSnackbar(event.message) }
                }
            }

            LeadScreen(
                uiState = uiState,
                onAction = { action ->
                    viewModel.onAction(action)
                    if (action is LeadAction.OpenLeadDetails) {
                        appNavigator.navigateTo(Dest.LeadDetails(leadId = action.lead.id)) {
                            launchSingleTop = true
                        }
                    }
                },
                contentPadding = padding
            )
        }

        composable<Dest.LeadDetails>(
            enterTransition = { NavigationAnimations.slideInRight },
            exitTransition = { NavigationAnimations.slideOutLeft },
            popEnterTransition = { NavigationAnimations.slideInLeft },
            popExitTransition = { NavigationAnimations.slideOutRight }
        ) { backStackEntry ->
            val leadId = remember(backStackEntry) { backStackEntry.toRoute<Dest.LeadDetails>().leadId }
            val parentEntry = remember(backStackEntry) {
                appNavigator.getBackStackEntry<Dest.ClaimGraph>()
            }
            val viewModel: LeadViewModel = hiltViewModel(parentEntry)
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val snackbarHostState = LocalAppSnackbar.current
            val scope = rememberCoroutineScope()

            ObserveAsEvents(flow = viewModel.uiEvent) { event ->
                when (event) {
                    is LeadUiEvent.ShowSnackbar -> scope.launch { snackbarHostState.showSnackbar(event.message) }
                }
            }

            // Prefer the selected lead (it carries in-session edits), else resolve from the list.
            val resolvedLead = uiState.selectedLead?.takeIf { it.id == leadId }
                ?: uiState.allLeads.firstOrNull { it.id == leadId }

            // Keep the last known lead so the exit animation never renders an empty screen.
            var lastLead by remember { mutableStateOf<HomeLead?>(null) }
            SideEffect { if (resolvedLead != null) lastLead = resolvedLead }
            val displayLead = resolvedLead ?: lastLead

            // Restored without in-memory selection (e.g. after process death): re-select so
            // dialogs and submit actions operate on this lead.
            LaunchedEffect(leadId, resolvedLead != null) {
                if (resolvedLead != null && uiState.selectedLead?.id != leadId) {
                    viewModel.onAction(LeadAction.OpenLeadDetails(resolvedLead))
                }
            }

            // Lead no longer exists once data has loaded: leave the screen.
            LaunchedEffect(uiState.isInitialLoadCompleted, resolvedLead == null) {
                if (uiState.isInitialLoadCompleted && displayLead == null) {
                    appNavigator.navigateBack()
                }
            }

            displayLead?.let { lead ->
                LeadDetailsScreen(
                    lead = lead,
                    uiState = uiState,
                    onBack = { appNavigator.navigateBack() },
                    onAction = viewModel::onAction
                )
            }
        }
    }
}
