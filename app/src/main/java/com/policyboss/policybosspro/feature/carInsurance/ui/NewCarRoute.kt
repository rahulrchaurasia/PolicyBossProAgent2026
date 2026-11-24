package com.policyboss.policybosspro.feature.carInsurance.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.policyboss.policybosspro.feature.carInsurance.model.CarEntryMode
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarAction
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiEvent
import com.policyboss.policybosspro.feature.carInsurance.model.QuoteFetchState
import com.policyboss.policybosspro.feature.carInsurance.model.VehicleLookupState
import com.policyboss.policybosspro.feature.carInsurance.viewmodel.NewCarViewModel
import com.policyboss.policybosspro.ui.ObserveAsEvents

@Composable
fun NewCarRoute(
    entryMode: CarEntryMode,
    onNavigateBack: () -> Unit,
    onNavigateToQuoteLoading: () -> Unit,
    modifier: Modifier = Modifier,
    carNumber: String? = null,
    viewModel: NewCarViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(entryMode, carNumber) {
        viewModel.initForMode(entryMode = entryMode, carNumber = carNumber)
    }

    ObserveAsEvents(flow = viewModel.uiEvent) { event ->
        when (event) {
            is NewCarUiEvent.NavigateBack -> {
                onNavigateBack()
            }

            is NewCarUiEvent.NavigateToQuoteLoading -> {
                onNavigateToQuoteLoading()
            }

            is NewCarUiEvent.ShowSnackbar -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }

            is NewCarUiEvent.OpenUrl -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.url))
                try {
                    context.startActivity(intent)
                } catch (_: Exception) {
                    Toast.makeText(context, "Cannot open URL", Toast.LENGTH_SHORT).show()
                }
            }

            else -> Unit
        }
    }

    // Registration lookup (vehicle_info_user + model_list) reuses the quote loader UI
    if (uiState.isVehicleLookupInProgress) {
        BackHandler {
            viewModel.onAction(NewCarAction.OnCancelVehicleLookup)
        }
        val lookupState = uiState.vehicleLookupState
        QuoteLoadingScreen(
            customerFirstName = "",
            quoteState = if (lookupState is VehicleLookupState.Error) {
                QuoteFetchState.Error(lookupState.message)
            } else {
                QuoteFetchState.Loading
            },
            title = "We are fetching your\ncar details!",
            errorTitle = "Unable to fetch car details",
            secondaryActionLabel = "Enter details manually",
            progressDescription = "Fetching car details",
            onCloseClick = { viewModel.onAction(NewCarAction.OnCancelVehicleLookup) },
            onRetryClick = { viewModel.onAction(NewCarAction.OnRetryVehicleLookup) },
            onSecondaryClick = { viewModel.onAction(NewCarAction.OnContinueWithoutVehicleInfo) },
            modifier = modifier
        )
        return
    }

    NewCarScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}
