package com.policyboss.policybosspro.feature.home.ui_home

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.policyboss.policybosspro.feature.home.component.home.bottomsheet.RenewPolicyBottomSheet
import com.policyboss.policybosspro.feature.home.component.home.bottomsheet.SyncIncentiveBottomSheet
import com.policyboss.policybosspro.feature.home.model.home.RenewalOption
import com.policyboss.policybosspro.feature.home.model.homeState.HomeAction
import com.policyboss.policybosspro.feature.home.model.homeState.HomeUiEvent

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    onNavigateToProfile: () -> Unit,
    onGetQuoteClick: (String) -> Unit,
    onNavigateToMenu: () -> Unit,
    onNavigateToNewCar: (Boolean) -> Unit = {},
    onNavigateToSyncContact: () -> Unit = {},
    onNavigateToWebView: (url: String, title: String) -> Unit = { _, _ -> },
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = com.policyboss.policybosspro.ui.components.snackBar.LocalAppSnackbar.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Trigger brief shimmer feedback whenever the Home tab is selected or resumed
    LifecycleResumeEffect(Unit) {
        viewModel.onHomeTabSelected()
        onPauseOrDispose { }
    }

    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is HomeUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                is HomeUiEvent.OpenDialer -> {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${event.phoneNumber}")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Cannot open dialer: ${event.phoneNumber}", Toast.LENGTH_SHORT).show()
                    }
                }

                is HomeUiEvent.OpenUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.url))
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Cannot open URL", Toast.LENGTH_SHORT).show()
                    }
                }

                else -> Unit
            }
        }
    }

    val onActionCallback: (HomeAction) -> Unit = remember(
        viewModel,
        context,
        onNavigateToProfile,
        onNavigateToMenu,
        onGetQuoteClick,
        onNavigateToNewCar,
        onNavigateToSyncContact,
        onNavigateToWebView
    ) {
        { action ->
            when (action) {
                is HomeAction.OnProfileClick -> onNavigateToProfile()
                is HomeAction.OnMenuClick -> onNavigateToMenu()
                is HomeAction.OnGetQuotesClick -> {
                    viewModel.onAction(action)
                    if (action.regNo.isNotBlank()) {
                        onGetQuoteClick(action.regNo)
                    }
                }
                is HomeAction.OnShareLeadClick -> {
                    viewModel.onAction(action)
                    val shareText = buildString {
                        append("Check out insurance quotes on PolicyBoss Pro!\n")
                        append("Customer: ${action.lead.name}\n")
                        append("Vehicle: ${action.lead.vehicleModel}\n")
                        append("Quotes Found: ${action.lead.quotesCount}\n")
                        append("Special Deals: ${action.lead.specialDealsCount}\n")
                        if (action.lead.quotes.isNotEmpty()) {
                            val topQuote = action.lead.quotes.first()
                            append("Top Insurer: ${topQuote.insurerName}\n")
                            append("Starting Premium: ${topQuote.premium}\n")
                            append("IDV: ${topQuote.idv}\n")
                        }
                    }
                    val sendIntent = Intent().apply {
                        this.action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Share Quotes for ${action.lead.name}")
                    try {
                        context.startActivity(shareIntent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Unable to share quotes", Toast.LENGTH_SHORT).show()
                    }
                }
                is HomeAction.OnViewAllSyncLeadsClick -> {
                    viewModel.onAction(action)
                    onNavigateToSyncContact()
                }
                is HomeAction.OnUnlockSyncRewardClick -> {
                    viewModel.onAction(action)
                    onNavigateToSyncContact()
                }
                is HomeAction.OnContinueRenewalClick -> {
                    viewModel.onAction(action)
                    if (action.option == RenewalOption.EXISTING_INSURER) {
                        onNavigateToWebView(
                            "https://www.policyboss.com/renewal",
                            "Renew Policy - ${action.lead.name}"
                        )
                    } else {
                        onNavigateToNewCar(false)
                    }
                }
                else -> viewModel.onAction(action)
            }
        }
    }

    HomeScreen(
        uiState = uiState,
        onAction = onActionCallback,
        modifier = modifier,
        contentPadding = contentPadding
    )

    // No Registration Bottom Sheet
    if (uiState.showNoRegistrationBottomSheet) {
        com.policyboss.policybosspro.feature.carInsurance.component.NoRegistrationBottomSheet(
            onDismiss = {
                viewModel.onAction(HomeAction.OnDismissNoRegistrationBottomSheet)
            },
            onOptionSelected = { isNewCar ->
                viewModel.onAction(HomeAction.OnDismissNoRegistrationBottomSheet)
                onNavigateToNewCar(isNewCar)
            }
        )
    }

    // Figma Sync Incentive Bottom Sheet
    if (uiState.showSyncIncentiveBottomSheet && uiState.selectedSyncIncentiveLead != null) {
        SyncIncentiveBottomSheet(
            lead = uiState.selectedSyncIncentiveLead!!,
            onDismiss = {
                viewModel.onAction(HomeAction.OnDismissSyncIncentiveBottomSheet)
            },
            onUnlockRewardClick = { lead ->
                onActionCallback(HomeAction.OnUnlockSyncRewardClick(lead))
            }
        )
    }

    // Figma Renew Policy Bottom Sheet
    if (uiState.showRenewPolicyBottomSheet && uiState.selectedRenewalLead != null) {
        RenewPolicyBottomSheet(
            lead = uiState.selectedRenewalLead!!,
            selectedOption = uiState.selectedRenewalOption,
            onOptionSelected = { option ->
                viewModel.onAction(HomeAction.OnSelectRenewalOption(option))
            },
            onDismiss = {
                viewModel.onAction(HomeAction.OnDismissRenewPolicyBottomSheet)
            },
            onContinueClick = { lead, option ->
                onActionCallback(HomeAction.OnContinueRenewalClick(lead, option))
            }
        )
    }
}
