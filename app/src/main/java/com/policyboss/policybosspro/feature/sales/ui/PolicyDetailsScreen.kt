package com.policyboss.policybosspro.feature.sales.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.sales.model.PolicyDetailsUiState
import com.policyboss.policybosspro.feature.sales.ui.components.*
import com.policyboss.policybosspro.ui.theme.AppColors

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
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.Slate900
                        )
                    }
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
