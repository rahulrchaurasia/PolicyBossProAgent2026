package com.policyboss.policybosspro.feature.home.component.home.bottomsheet

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.home.model.home.HomeLead
import com.policyboss.policybosspro.feature.home.model.home.RenewalOption
import com.policyboss.policybosspro.ui.components.bottomSheet.policyProtectedBottomSheet.component.FloatingCloseButton
import com.policyboss.policybosspro.ui.components.radio.AppRadioButton
import com.policyboss.policybosspro.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenewPolicyBottomSheet(
    lead: HomeLead,
    selectedOption: RenewalOption,
    onOptionSelected: (RenewalOption) -> Unit,
    onContinueClick: (HomeLead, RenewalOption) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = Color.Transparent,
        scrimColor = Color.Black.copy(alpha = 0.65f),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            // Main Bottom Sheet Body
            Column(
                modifier = Modifier
                    .padding(top = 26.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Title and Subtitle
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Renew Policy",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                    Text(
                        text = "Select an option to continue with your renewal",
                        fontSize = 13.sp,
                        color = AppColors.TextSecondary
                    )
                }

                // Options list
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    RenewalOptionCard(
                        option = RenewalOption.EXISTING_INSURER,
                        isSelected = selectedOption == RenewalOption.EXISTING_INSURER,
                        onSelect = { onOptionSelected(RenewalOption.EXISTING_INSURER) }
                    )

                    RenewalOptionCard(
                        option = RenewalOption.OTHER_INSURERS,
                        isSelected = selectedOption == RenewalOption.OTHER_INSURERS,
                        onSelect = { onOptionSelected(RenewalOption.OTHER_INSURERS) }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Continue Button
                Button(
                    onClick = { onContinueClick(lead, selectedOption) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.PrimaryBlue
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Continue",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Continue",
                                tint = AppColors.PrimaryBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }

            // Top Floating Circular Close Button
            FloatingCloseButton(
                onDismiss = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun RenewalOptionCard(
    option: RenewalOption,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) AppColors.PrimaryBlue else AppColors.BorderSecondary,
        animationSpec = tween(durationMillis = 200),
        label = "renewalOptionBorder"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) AppColors.FilterPanelBackground else Color.White,
        animationSpec = tween(durationMillis = 200),
        label = "renewalOptionBackground"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppRadioButton(
                selected = isSelected,
                onClick = null,
                unselectedBorderColor = AppColors.RadioUnselected,
                size = 20.dp
            )

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = option.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                Text(
                    text = option.subtitle,
                    fontSize = 12.sp,
                    color = AppColors.TextSecondary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RenewPolicyBottomSheetPreview() {
    RenewPolicyBottomSheet(
        lead = AppDummyData.renewalLeads.first(),
        selectedOption = RenewalOption.EXISTING_INSURER,
        onOptionSelected = {},
        onContinueClick = { _, _ -> },
        onDismiss = {}
    )
}
