package com.policyboss.policybosspro.feature.carInsurance.ui.carPolicyDetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.policyboss.policybosspro.feature.carInsurance.model.MotorQuote
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarAction
import com.policyboss.policybosspro.feature.carInsurance.ui.carPolicyDetail.model.PolicyBreakupDetails
import com.policyboss.policybosspro.feature.carInsurance.viewmodel.NewCarViewModel

@Composable
fun MotorPolicyDetailRoute(
    viewModel: NewCarViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToBuyNow: (MotorQuote) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedQuote = uiState.selectedPolicyDetailQuote
        ?: uiState.recommendedQuote
        ?: uiState.quotes.firstOrNull()

    val breakupDetails = PolicyBreakupDetails.createSample(
        quote = selectedQuote,
        uiState = uiState
    )

    MotorPolicyDetailScreen(
        breakupDetails = breakupDetails,
        onBackClick = onNavigateBack,
        onBuyNowClick = {
            selectedQuote?.let { quote ->
                viewModel.onAction(NewCarAction.OnBuyNowClick(quote))
                onNavigateToBuyNow(quote)
            }
        },
        modifier = modifier
    )
}
