package com.policyboss.policybosspro.feature.carInsurance.ui.carPolicyDetail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.carInsurance.model.MotorQuote
import com.policyboss.policybosspro.feature.carInsurance.ui.carPolicyDetail.component.PolicyDetailHeader
import com.policyboss.policybosspro.feature.carInsurance.ui.carPolicyDetail.component.PolicyVehicleInfoCard
import com.policyboss.policybosspro.feature.carInsurance.ui.carPolicyDetail.component.PremiumBreakupSection
import com.policyboss.policybosspro.feature.carInsurance.ui.carPolicyDetail.model.PolicyBreakupDetails
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun MotorPolicyDetailScreen(
    breakupDetails: PolicyBreakupDetails,
    onBackClick: () -> Unit,
    onBuyNowClick: () -> Unit,
    modifier: Modifier = Modifier,
    initialInfoExpanded: Boolean = false
) {
    var isInfoExpanded by remember { mutableStateOf(initialInfoExpanded) }

    BackHandler {
        onBackClick()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            PolicyDetailHeader(
                crnNumber = breakupDetails.vehicleInfo.crn,
                insurerName = breakupDetails.insurerName,
                insurerLogoType = breakupDetails.insurerLogoType,
                isInfoExpanded = isInfoExpanded,
                onToggleInfo = { isInfoExpanded = !isInfoExpanded },
                onCloseClick = onBackClick
            )
        },
        bottomBar = {
            // Pinned Bottom CTA Bar (Full-width Buy Now button)
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = onBuyNowClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.PrimaryBlue
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Buy Now",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            // Expandable Vehicle & Policy Summary Card
            item(key = "vehicle_info_card") {
                PolicyVehicleInfoCard(
                    vehicleInfo = breakupDetails.vehicleInfo,
                    isVisible = isInfoExpanded
                )
            }

            // Scrollable Premium Breakup Section
            item(key = "premium_breakup_section") {
                PremiumBreakupSection(
                    breakupDetails = breakupDetails
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Info Collapsed")
@Composable
private fun MotorPolicyDetailScreenCollapsedPreview() {
    MotorPolicyDetailScreen(
        breakupDetails = PolicyBreakupDetails.createSample(),
        onBackClick = {},
        onBuyNowClick = {},
        initialInfoExpanded = false
    )
}

@Preview(showBackground = true, name = "Info Expanded")
@Composable
private fun MotorPolicyDetailScreenExpandedPreview() {
    MotorPolicyDetailScreen(
        breakupDetails = PolicyBreakupDetails.createSample(),
        onBackClick = {},
        onBuyNowClick = {},
        initialInfoExpanded = true
    )
}
