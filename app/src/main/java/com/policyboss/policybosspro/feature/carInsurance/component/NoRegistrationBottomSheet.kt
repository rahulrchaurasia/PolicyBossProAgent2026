package com.policyboss.policybosspro.feature.carInsurance.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.carInsurance.model.CarEntryMode
import com.policyboss.policybosspro.ui.components.button.PrimaryCTAButton
import com.policyboss.policybosspro.ui.components.radio.AppRadioButton
import com.policyboss.policybosspro.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoRegistrationBottomSheet(
    onDismiss: () -> Unit,
    onOptionSelected: (entryMode: CarEntryMode) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        dragHandle = null
    ) {
        NoRegistrationBottomSheetContent(
            onDismiss = onDismiss,
            onOptionSelected = onOptionSelected
        )
    }
}

@Composable
fun NoRegistrationBottomSheetContent(
    onDismiss: () -> Unit,
    onOptionSelected: (entryMode: CarEntryMode) -> Unit
) {
    // CarEntryMode.WITHOUT_CAR_NUMBER = Continue without a registration number
    // CarEntryMode.NEW_CAR = Bought a new car? No registration number yet
    var selectedMode by remember { mutableStateOf(CarEntryMode.WITHOUT_CAR_NUMBER) }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        // Main white bottom sheet content
        Column(
            modifier = Modifier
                .padding(top = 22.dp)
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(horizontal = 24.dp)
                .padding(top = 36.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "How would you like to continue?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary,
                textAlign = TextAlign.Center
            )

            // Blue tinted container holding both cards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = AppColors.BadgeIndigoLight,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Option 1: Continue without a registration number
                val interaction1 = remember { MutableInteractionSource() }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = interaction1,
                            indication = null,
                            onClick = { selectedMode = CarEntryMode.WITHOUT_CAR_NUMBER }
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AppRadioButton(
                            selected = selectedMode == CarEntryMode.WITHOUT_CAR_NUMBER,
                            onClick = null
                        )
                        Text(
                            text = "Continue without a registration number",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.TextPrimary,
                            lineHeight = 20.sp
                        )
                    }
                }

                // Option 2: Bought a new car? No registration number yet
                val interaction2 = remember { MutableInteractionSource() }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = interaction2,
                            indication = null,
                            onClick = { selectedMode = CarEntryMode.NEW_CAR }
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AppRadioButton(
                            selected = selectedMode == CarEntryMode.NEW_CAR,
                            onClick = null
                        )
                        Text(
                            text = "Bought a new car? No registration number yet",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.TextPrimary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Continue CTA Button with circular arrow
            PrimaryCTAButton(
                text = "Continue",
                onClick = {
                    onDismiss()
                    onOptionSelected(selectedMode)
                },
                containerColor = AppColors.PrimaryBlue,
                contentColor = Color.White,
                arrowBackgroundColor = Color.White,
                arrowTint = AppColors.PrimaryBlue,
                shape = RoundedCornerShape(999.dp)
            )
        }

        // Floating Close Button
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color.White, CircleShape)
                .border(1.dp, AppColors.BorderSecondary, CircleShape)
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = AppColors.TextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoRegistrationBottomSheetContentPreview() {
    NoRegistrationBottomSheetContent(
        onDismiss = {},
        onOptionSelected = {}
    )
}
