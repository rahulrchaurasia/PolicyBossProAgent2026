package com.policyboss.policybosspro.feature.carInsurance.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiEvent
import com.policyboss.policybosspro.feature.carInsurance.viewmodel.NewCarViewModel
import com.policyboss.policybosspro.ui.ObserveAsEvents

@Composable
fun NewCarRoute(
    isNewCar: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToQuoteLoading: () -> Unit,
    modifier: Modifier = Modifier,
    carNumber: String? = null,
    viewModel: NewCarViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(isNewCar, carNumber) {
        viewModel.init(isNewCar = isNewCar, carNumber = carNumber)
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

    NewCarScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}
