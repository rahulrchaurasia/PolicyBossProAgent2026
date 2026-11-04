package com.policyboss.policybosspro.feature.dashboard.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.policyboss.policybosspro.feature.dashboard.ui.DashboardScreen
import com.policyboss.policybosspro.feature.dashboard.ui.DashboardViewModel
import com.policyboss.policybosspro.navigation.AppNavigator
import com.policyboss.policybosspro.navigation.Dest

fun NavGraphBuilder.dashboardGraph(
    appNavigator: AppNavigator,
    padding: PaddingValues
) {
    navigation<Dest.DashboardGraph>(
        startDestination = Dest.Dashboard
    ) {
        composable<Dest.Dashboard> {
            val viewModel: DashboardViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()
            
            DashboardScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
                contentPadding = padding
            )
        }
    }
}
