package com.policyboss.policybosspro.feature.grow.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.policyboss.policybosspro.feature.grow.ui.GrowScreen
import com.policyboss.policybosspro.feature.grow.ui.GrowViewModel
import com.policyboss.policybosspro.navigation.AppNavigator
import com.policyboss.policybosspro.navigation.Dest

fun NavGraphBuilder.growGraph(
    appNavigator: AppNavigator,
    padding: PaddingValues
) {
    navigation<Dest.VaultGraph>(
        startDestination = Dest.Grow
    ) {
        composable<Dest.Grow> {
            val viewModel: GrowViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            GrowScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
                events = viewModel.events,
                contentPadding = padding
            )
        }
    }
}
