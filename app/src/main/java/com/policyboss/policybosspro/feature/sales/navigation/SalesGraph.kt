package com.policyboss.policybosspro.feature.sales.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.compose.runtime.remember
import com.policyboss.policybosspro.anim.NavigationAnimations
import com.policyboss.policybosspro.feature.sales.model.SalesAction
import com.policyboss.policybosspro.feature.sales.ui.PolicyDetailsScreen
import com.policyboss.policybosspro.feature.sales.ui.PolicyDetailsViewModel
import com.policyboss.policybosspro.feature.sales.ui.SalesFilterScreen
import com.policyboss.policybosspro.feature.sales.ui.SalesScreen
import com.policyboss.policybosspro.feature.sales.ui.SalesViewModel
import com.policyboss.policybosspro.navigation.AppNavigator
import com.policyboss.policybosspro.navigation.Dest

fun NavGraphBuilder.salesGraph(
    appNavigator: AppNavigator,
    padding: PaddingValues
) {
    navigation<Dest.SalesGraph>(
        startDestination = Dest.Sales
    ) {
        composable<Dest.Sales> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                appNavigator.getBackStackEntry<Dest.SalesGraph>()
            }
            val viewModel: SalesViewModel = hiltViewModel(parentEntry)
            val uiState by viewModel.uiState.collectAsState()

            SalesScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
                onViewDetails = { policyId ->
                    appNavigator.navigateTo(Dest.PolicyDetails(policyId = policyId))
                },
                onOpenFilter = {
                    viewModel.onAction(SalesAction.OpenFilter)
                    appNavigator.navigateTo(Dest.SalesFilter)
                },
                contentPadding = padding
            )
        }

        composable<Dest.SalesFilter>(
            enterTransition = { NavigationAnimations.slideInBottom },
            exitTransition = { NavigationAnimations.slideOutTop },
            popEnterTransition = { NavigationAnimations.slideInTop },
            popExitTransition = { NavigationAnimations.slideOutBottom }
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                appNavigator.getBackStackEntry<Dest.SalesGraph>()
            }
            val viewModel: SalesViewModel = hiltViewModel(parentEntry)
            val uiState by viewModel.uiState.collectAsState()

            SalesFilterScreen(
                filterState = uiState.filterState,
                onAction = viewModel::onAction,
                onClose = {
                    viewModel.onAction(SalesAction.CloseFilter)
                    appNavigator.navigateBack()
                },
                onApply = {
                    viewModel.onAction(SalesAction.ApplyFilters)
                    appNavigator.navigateBack()
                }
            )
        }

        composable<Dest.PolicyDetails>(
            enterTransition = { NavigationAnimations.slideInRight },
            exitTransition = { NavigationAnimations.slideOutLeft },
            popEnterTransition = { NavigationAnimations.slideInLeft },
            popExitTransition = { NavigationAnimations.slideOutRight }
        ) {
            val viewModel: PolicyDetailsViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            PolicyDetailsScreen(
                uiState = uiState,
                onBack = {
                    appNavigator.navigateBack()
                }
            )
        }
    }
}
