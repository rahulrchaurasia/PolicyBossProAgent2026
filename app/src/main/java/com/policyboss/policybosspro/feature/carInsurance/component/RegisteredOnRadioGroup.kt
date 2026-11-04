package com.policyboss.policybosspro.feature.carInsurance.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.carInsurance.model.OwnershipType
import com.policyboss.policybosspro.ui.components.radio.AppRadioButtonOption
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun RegisteredOnRadioGroup(
    selectedOwnership: OwnershipType,
    onOwnershipSelected: (OwnershipType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Registered on",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OwnershipType.entries.forEach { type ->
                AppRadioButtonOption(
                    selected = selectedOwnership == type,
                    label = type.displayName,
                    onClick = { onOwnershipSelected(type) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisteredOnRadioGroupPreview() {
    RegisteredOnRadioGroup(
        selectedOwnership = OwnershipType.COMPANY,
        onOwnershipSelected = {}
    )
}
