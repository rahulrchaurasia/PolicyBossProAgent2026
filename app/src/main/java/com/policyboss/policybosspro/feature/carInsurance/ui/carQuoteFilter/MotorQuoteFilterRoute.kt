package com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarAction
import com.policyboss.policybosspro.feature.carInsurance.viewmodel.NewCarViewModel

@Composable
fun MotorQuoteFilterRoute(
    viewModel: NewCarViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // System Back discards draft filter modifications and returns
    BackHandler {
        viewModel.onAction(NewCarAction.DiscardDraftFilter)
        onNavigateBack()
    }

    MotorQuoteFilterScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        onCloseClick = {
            viewModel.onAction(NewCarAction.DiscardDraftFilter)
            onNavigateBack()
        },
        onApplyClick = {
            viewModel.onAction(NewCarAction.ApplyFilter)
            onNavigateBack()
        },
        modifier = modifier
    )
}
