package com.policyboss.policybosspro.feature.sales.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.sales.model.InsuredDetails
import com.policyboss.policybosspro.feature.sales.model.PolicyDetailsUiState
import com.policyboss.policybosspro.feature.sales.model.SalePolicy
import com.policyboss.policybosspro.feature.sales.ui.components.*
import com.policyboss.policybosspro.ui.components.button.AppBackButton
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.PolicyBossTheme

@Composable
fun PolicyDetailsScreen(
    uiState: PolicyDetailsUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppBackButton(
                        onClick = onBack,
                        tint = AppColors.Slate900
                    )
                    Text(
                        text = "Policy Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Slate900,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                HorizontalDivider(thickness = 0.5.dp, color = AppColors.Slate200)
            }
        },
        containerColor = AppColors.Slate50
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AppColors.Primary
                    )
                }
                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = onBack) {
                            Text("Go Back")
                        }
                    }
                }
                uiState.policy != null -> {
                    val policy = uiState.policy
                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 1. Customer Summary Card
                        CustomerSummaryCard(policy = policy)

                        // 2. Agent Information Card
                        AgentInformationCard(policy = policy)

                        // 3. Premium & Boss Coins Card
                        PremiumRewardsCard(policy = policy)

                        // 4. Total Earnings Card
                        TotalEarningsCard(policy = policy)

                        // 5. Customer Details Card
                        CustomerDetailsCard(customerDetailsId = policy.customerDetailsId)

                        // 6. Insured Details Card
                        InsuredDetailsCard(insuredDetails = policy.insuredDetails)

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

// ==========================================
// Previews
// ==========================================

private val sampleInsuredDetails = InsuredDetails(
    title = "YR-2025::SAOD::TVS::NTORQ::KL21::KERALA",
    vahanData = "HERO HONDA MOTORS LTD::SPLENDOR PLUS::PETROL::100CC",
    quoteOn = "17-Aug-26 13:02",
    saleOn = "17-Aug-26 13:10",
    crn = "12078562",
    cs = "CS3549723",
    zaxtra = "NA",
    premiumLevel = "Level 1",
    brokerageLevel = "Level 1",
    saleDuration = "7 Minute",
    sync = "No",
    renew = "YES",
    source = "POSP-POSP-AGENCY",
    insurer = "Edelweiss",
    customer = "Suresh Bandhaiya",
    reporting = "Umesh Pawar Uid-120572",
    ssId = "122378"
)

private val samplePolicy = SalePolicy(
    id = "19251610",
    customerName = "Rajesh Chadha",
    customerBadge = "Friend",
    insuranceCategory = "Car Insurance",
    vehicleModel = "HONDA CITY INDIA (P) LTD: AC...",
    detailsVehicleModel = "Maruti Suzuki Baleno",
    agentName = "Rashmi Shah",
    subUserName = "Mukesh Agarwal",
    premiumAmount = "₹6,500",
    bossCoins = "25+",
    totalEarnings = "1,403",
    standardBrokerage = "₹1,000",
    specialDeal = "₹103",
    specialDealBy = "Special Deal By HDFC",
    extraSyncEarnings = "₹200",
    zaxtraEarnings = "₹100 ↑",
    customerDetailsId = "19251610",
    insuredDetails = sampleInsuredDetails
)

@Preview(name = "Policy Details - Success", showBackground = true, showSystemUi = true)
@Composable
private fun PolicyDetailsScreenSuccessPreview() {
    PolicyBossTheme {
        PolicyDetailsScreen(
            uiState = PolicyDetailsUiState(
                isLoading = false,
                policy = samplePolicy,
                errorMessage = null
            ),
            onBack = {}
        )
    }
}

@Preview(name = "Policy Details - Loading", showBackground = true)
@Composable
private fun PolicyDetailsScreenLoadingPreview() {
    PolicyBossTheme {
        PolicyDetailsScreen(
            uiState = PolicyDetailsUiState(
                isLoading = true,
                policy = null,
                errorMessage = null
            ),
            onBack = {}
        )
    }
}

@Preview(name = "Policy Details - Error", showBackground = true)
@Composable
private fun PolicyDetailsScreenErrorPreview() {
    PolicyBossTheme {
        PolicyDetailsScreen(
            uiState = PolicyDetailsUiState(
                isLoading = false,
                policy = null,
                errorMessage = "Failed to load policy details. Please check your network and try again."
            ),
            onBack = {}
        )
    }
}
