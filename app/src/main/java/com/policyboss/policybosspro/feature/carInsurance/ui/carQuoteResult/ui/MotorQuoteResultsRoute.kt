package com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteResult.ui

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiEvent
import com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteFilter.model.FilterCategory
import com.policyboss.policybosspro.feature.carInsurance.viewmodel.NewCarViewModel
import com.policyboss.policybosspro.ui.ObserveAsEvents

@Composable
fun MotorQuoteResultsRoute(
    viewModel: NewCarViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToForm: () -> Unit,
    onNavigateToUrl: (url: String, title: String) -> Unit,
    onNavigateToFilters: (FilterCategory) -> Unit = {},
    onNavigateToPolicyDetail: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.ensureQuotesLoaded()
    }

    BackHandler {
        onNavigateBack()
    }

    ObserveAsEvents(flow = viewModel.uiEvent) { event ->
        when (event) {
            is NewCarUiEvent.NavigateBack -> {
                onNavigateBack()
            }

            is NewCarUiEvent.NavigateBackToForm -> {
                onNavigateToForm()
            }

            is NewCarUiEvent.NavigateToPolicyDetail -> {
                onNavigateToPolicyDetail()
            }

            is NewCarUiEvent.OpenUrl -> {
                onNavigateToUrl(event.url, "Motor Insurance Quotes")
            }

            is NewCarUiEvent.ShareText -> {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, event.text)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, "Share Motor Quotes")
                context.startActivity(shareIntent)
            }

            is NewCarUiEvent.ShowSnackbar -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }

            else -> Unit
        }
    }

    MotorQuoteResultsScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        onBackClick = onNavigateBack,
        onNavigateToFilters = onNavigateToFilters,
        modifier = modifier
    )
}

