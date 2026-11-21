package com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteResult.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    onDismissRequest: () -> Unit,
    onApplyFilters: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedInsurers by remember { mutableStateOf(setOf("All Insurers")) }
    var zeroDepOnly by remember { mutableStateOf(false) }
    var cpaRequired by remember { mutableStateOf(false) }

    val insurerOptions = listOf("All Insurers", "Acko", "Tata AIG", "HDFC ERGO", "ICICI Lombard", "Bajaj Allianz")

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = AppColors.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Filter Quotes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Slate900
                )
                Text(
                    text = "Reset All",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.PrimaryBlue,
                    modifier = Modifier.clickable {
                        selectedInsurers = setOf("All Insurers")
                        zeroDepOnly = false
                        cpaRequired = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = AppColors.Slate200)
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Insurers",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Slate800
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                insurerOptions.forEach { insurer ->
                    val isSelected = selectedInsurers.contains(insurer)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (isSelected) AppColors.BlueTintBackground else AppColors.Slate50)
                            .clickable {
                                selectedInsurers = if (insurer == "All Insurers") {
                                    setOf("All Insurers")
                                } else {
                                    val next = selectedInsurers.toMutableSet().apply { remove("All Insurers") }
                                    if (next.contains(insurer)) next.remove(insurer) else next.add(insurer)
                                    if (next.isEmpty()) setOf("All Insurers") else next
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = insurer,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) AppColors.PrimaryBlue else AppColors.Slate700
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Popular Add-ons",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Slate800
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (zeroDepOnly) AppColors.BlueTintBackground else AppColors.Slate50)
                        .clickable { zeroDepOnly = !zeroDepOnly }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "Zero Depreciation",
                        fontSize = 12.sp,
                        fontWeight = if (zeroDepOnly) FontWeight.Bold else FontWeight.Medium,
                        color = if (zeroDepOnly) AppColors.PrimaryBlue else AppColors.Slate700
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (cpaRequired) AppColors.BlueTintBackground else AppColors.Slate50)
                        .clickable { cpaRequired = !cpaRequired }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "CPA Cover",
                        fontSize = 12.sp,
                        fontWeight = if (cpaRequired) FontWeight.Bold else FontWeight.Medium,
                        color = if (cpaRequired) AppColors.PrimaryBlue else AppColors.Slate700
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(999.dp),
                    border = BorderStroke(1.dp, AppColors.Slate300)
                ) {
                    Text("Close", color = AppColors.Slate700, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        onApplyFilters()
                        onDismissRequest()
                    },
                    modifier = Modifier.weight(1.5f).height(46.dp),
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryBlue)
                ) {
                    Text("Apply Filters", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

