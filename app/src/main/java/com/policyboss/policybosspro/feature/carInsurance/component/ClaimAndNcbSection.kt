package com.policyboss.policybosspro.feature.carInsurance.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.carInsurance.model.CarInsuranceData
import com.policyboss.policybosspro.ui.components.dropDown.DropdownOption
import com.policyboss.policybosspro.ui.components.dropDown.GenericM3Dropdown
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun ClaimAndNcbSection(
    hasClaim: Boolean,
    onClaimToggle: (Boolean) -> Unit,
    selectedNcb: DropdownOption<String>?,
    ncbOptions: List<DropdownOption<String>>,
    onNcbSelected: (DropdownOption<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Question: Have you made a claim?
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Have you made a claim?",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, AppColors.BorderSecondary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // "Yes"
                    val isYesSelected = hasClaim
                    val yesSource = remember { MutableInteractionSource() }
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = yesSource,
                                indication = null,
                                onClick = { onClaimToggle(true) }
                            ),
                        shape = RoundedCornerShape(8.dp),
                        color = if (isYesSelected) AppColors.ChipBlueLight else Color.Transparent
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Yes",
                                fontSize = 14.sp,
                                fontWeight = if (isYesSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isYesSelected) AppColors.ChipBlueDark else AppColors.TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // "No"
                    val isNoSelected = !hasClaim
                    val noSource = remember { MutableInteractionSource() }
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = noSource,
                                indication = null,
                                onClick = { onClaimToggle(false) }
                            ),
                        shape = RoundedCornerShape(8.dp),
                        color = if (isNoSelected) AppColors.ChipBlueLight else Color.Transparent
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No",
                                fontSize = 14.sp,
                                fontWeight = if (isNoSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isNoSelected) AppColors.ChipBlueDark else AppColors.TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // NCB Bonus status
        GenericM3Dropdown(
            label = "Your ‘No claim bonus status’ is",
            items = ncbOptions,
            selectedItem = selectedNcb,
            onItemSelected = onNcbSelected,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ClaimAndNcbSectionPreview() {
    ClaimAndNcbSection(
        hasClaim = true,
        onClaimToggle = {},
        selectedNcb = DropdownOption("0", "0%"),
        ncbOptions = CarInsuranceData.ncbOptions,
        onNcbSelected = {}
    )
}
