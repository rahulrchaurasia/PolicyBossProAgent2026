package com.policyboss.policybosspro.feature.home.component.home.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.home.model.menu.QuoteTabItem
import com.policyboss.policybosspro.feature.home.util.VehicleRegistrationValidator
import com.policyboss.policybosspro.ui.components.button.PrimaryCTAButton
import com.policyboss.policybosspro.ui.components.textfield.VehicleRegistrationTextField
import com.policyboss.policybosspro.ui.components.text.TextWithChevron
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.badgeMicro
import com.policyboss.policybosspro.ui.theme.headlineMediumBold
import com.policyboss.policybosspro.ui.theme.potentialEarningsLabel

@Composable
fun GetQuotesCard(
    tabs: List<QuoteTabItem>,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    registrationNumber: String,
    onRegistrationChange: (String) -> Unit,
    onGetQuoteClick: () -> Unit,
    onNoRegistrationClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Enter registration number",
    isGetQuotesEnabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        border = BorderStroke(1.dp, AppColors.BorderSecondary),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Get Quotes",
                style = MaterialTheme.typography.headlineMediumBold,
                color = AppColors.TextPrimary
            )

            // Vehicle Category Tabs with Horizontal Scroll
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabs.forEach { item ->
                    val isSelected = selectedTab == item.title
                    Box {
                        Surface(
                            modifier = Modifier
                                .clickable {
                                    if (!item.isComingSoon) {
                                        onTabSelected(item.title)
                                    }
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) AppColors.PrimaryBlue.copy(alpha = 0.1f) else AppColors.Background,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) AppColors.PrimaryBlue else AppColors.BorderSecondary
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = item.iconRes),
                                    contentDescription = item.title,
                                    tint = if (isSelected) AppColors.PrimaryBlue else AppColors.TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.potentialEarningsLabel,
                                    color = if (isSelected) AppColors.PrimaryBlue else AppColors.TextSecondary
                                )
                            }
                        }

                        if (item.isComingSoon) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = AppColors.LightGraySurface,
                                border = BorderStroke(0.5.dp, AppColors.BorderSecondary),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 2.dp, y = (-6).dp)
                            ) {
                                Text(
                                    text = "Coming soon",
                                    style = MaterialTheme.typography.badgeMicro,
                                    color = AppColors.TextSecondary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Reusable Dynamic Software Keyboard Vehicle Registration Field
            VehicleRegistrationTextField(
                value = registrationNumber,
                onValueChange = onRegistrationChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = placeholder,
                isError = isError,
                errorMessage = errorMessage,
                allowBhSeries = VehicleRegistrationValidator.allowsBhSeries(selectedTab),
                imeAction = ImeAction.Done,
                onDone = onGetQuoteClick
            )

            // Get Quotes Button
            PrimaryCTAButton(
                text = "Get quotes",
                onClick = onGetQuoteClick,
                enabled = isGetQuotesEnabled,
                containerColor = AppColors.PrimaryBlue,
                contentColor = AppColors.White,
                arrowBackgroundColor = AppColors.White,
                arrowTint = AppColors.PrimaryBlue,
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )

            // Centered Bottom Link
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TextWithChevron(
                    text = "I don’t have a registration number",
                    onClick = onNoRegistrationClick,
                    textStyle = MaterialTheme.typography.labelMedium,
                    textColor = AppColors.PrimaryBlue,
                    iconTint = AppColors.PrimaryBlue,
                    iconSize = 16.dp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GetQuotesCardPreview() {
    GetQuotesCard(
        tabs = AppDummyData.quoteVehicleTabs,
        selectedTab = "Car",
        onTabSelected = {},
        registrationNumber = "MH02AB1234",
        onRegistrationChange = {},
        onGetQuoteClick = {},
        onNoRegistrationClick = {}
    )
}
