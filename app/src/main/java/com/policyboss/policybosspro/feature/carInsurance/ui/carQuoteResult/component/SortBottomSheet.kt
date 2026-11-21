package com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteResult.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.carInsurance.model.MotorQuoteSortOption
import com.policyboss.policybosspro.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    selectedOption: MotorQuoteSortOption,
    onOptionSelected: (MotorQuoteSortOption) -> Unit,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
            Text(
                text = "Sort Quotes",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Slate900
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = AppColors.Slate200)

            MotorQuoteSortOption.entries.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onOptionSelected(option)
                            onDismissRequest()
                        }
                        .padding(vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = option.displayName,
                        fontSize = 15.sp,
                        fontWeight = if (selectedOption == option) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedOption == option) AppColors.PrimaryBlue else AppColors.Slate800
                    )

                    RadioButton(
                        selected = selectedOption == option,
                        onClick = {
                            onOptionSelected(option)
                            onDismissRequest()
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = AppColors.PrimaryBlue,
                            unselectedColor = AppColors.Slate400
                        )
                    )
                }
                HorizontalDivider(color = AppColors.Slate100)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

