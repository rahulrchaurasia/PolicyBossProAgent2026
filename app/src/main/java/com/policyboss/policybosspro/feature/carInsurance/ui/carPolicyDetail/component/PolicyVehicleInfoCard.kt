package com.policyboss.policybosspro.feature.carInsurance.ui.carPolicyDetail.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.carInsurance.model.MotorQuote
import com.policyboss.policybosspro.feature.carInsurance.ui.carPolicyDetail.model.PolicyVehicleInfo
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun PolicyVehicleInfoCard(
    vehicleInfo: PolicyVehicleInfo,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            shadowElevation = 4.dp,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Car Model (Full Width)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Car Model",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = AppColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = vehicleInfo.carModel,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.TextPrimary
                    )
                }

                // 2-3. Detail cells in 2 columns; IDV and New NCB are absent for TP-only
                val cells = buildList {
                    vehicleInfo.idv?.let { add(Triple("IDV", "₹${MotorQuote.formatIndianCurrency(it)}", FontWeight.Bold)) }
                    add(Triple("CRN", vehicleInfo.crn, FontWeight.Bold))
                    add(Triple("Reg Date", vehicleInfo.regDate, FontWeight.SemiBold))
                    vehicleInfo.newNcb?.let { add(Triple("New NCB", it, FontWeight.SemiBold)) }
                }
                cells.chunked(2).forEach { rowCells ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        rowCells.forEach { (label, value, weight) ->
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = AppColors.TextSecondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = value,
                                    fontSize = 14.sp,
                                    fontWeight = weight,
                                    color = AppColors.TextPrimary
                                )
                            }
                        }
                        if (rowCells.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }

                // 4. Plan Name (Full Width)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Plan Name",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = AppColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = vehicleInfo.planName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PolicyVehicleInfoCardPreview() {
    PolicyVehicleInfoCard(
        vehicleInfo = PolicyVehicleInfo(
            carModel = "Maruti, Wagon R, Petrol, 1.0 VXI PLUS (998 CC)",
            idv = 110831,
            crn = "12682505",
            regDate = "18 Oct, 2011",
            newNcb = "20%",
            planName = "GOLD"
        ),
        isVisible = true
    )
}

@Preview(name = "Vehicle Info - TP only", showBackground = true)
@Composable
private fun PolicyVehicleInfoCardPreviewTp() {
    PolicyVehicleInfoCard(
        vehicleInfo = PolicyVehicleInfo(
            carModel = "Maruti, Wagon R, Petrol, 1.0 VXI PLUS (998 CC)",
            idv = null,
            crn = "12682505",
            regDate = "18 Oct, 2011",
            newNcb = null,
            planName = "GOLD"
        ),
        isVisible = true
    )
}
