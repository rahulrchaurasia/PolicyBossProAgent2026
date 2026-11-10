package com.policyboss.policybosspro.feature.lead.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.home.model.home.HomeLead
import com.policyboss.policybosspro.feature.lead.model.LeadAction
import com.policyboss.policybosspro.feature.lead.model.LeadDialogType
import com.policyboss.policybosspro.feature.lead.model.LeadUiState
import com.policyboss.policybosspro.feature.lead.ui.components.AddRelationshipDialog
import com.policyboss.policybosspro.feature.lead.ui.components.EditDetailsDialog
import com.policyboss.policybosspro.feature.lead.ui.components.MarkLeadLostDialog
import com.policyboss.policybosspro.ui.components.button.AppBackButton
import com.policyboss.policybosspro.ui.theme.AppColors

import androidx.compose.runtime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadDetailsScreen(
    lead: HomeLead,
    uiState: LeadUiState,
    onBack: () -> Unit,
    onAction: (LeadAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    AppBackButton(
                        onClick = onBack,
                        tint = AppColors.TextPrimary
                    )
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = AppColors.TextPrimary)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Mark as lost", color = AppColors.TextPrimary) },
                                onClick = { 
                                    showMenu = false
                                    onAction(LeadAction.OpenMarkAsLost(lead)) 
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Mark vehicle as sold", color = AppColors.TextPrimary) },
                                onClick = { 
                                    showMenu = false
                                    onAction(LeadAction.OpenMarkVehicleSold(lead)) 
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.Background)
            )
        },
        containerColor = AppColors.Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(lead.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = AppColors.TextPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AppColors.Surface
                        ) {
                            Text(lead.leadType, fontSize = 12.sp, color = AppColors.TextPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                    Text("+91 98765 43210", color = AppColors.TextSecondary, fontSize = 14.sp) // Dummy phone
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = {}, shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(8.dp)) {
                        Text("C", color = AppColors.PrimaryBlue) // Call
                    }
                    OutlinedButton(onClick = {}, shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(8.dp)) {
                        Text("W", color = AppColors.PrimaryBlue) // Whatsapp
                    }
                }
            }

            // Relation
            Column {
                Text("Relation", fontSize = 12.sp, color = AppColors.TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(shape = RoundedCornerShape(12.dp), color = AppColors.Surface, border = BorderStroke(1.dp, AppColors.BorderSecondary)) {
                            Text(lead.relationCategory ?: "Personal", fontSize = 12.sp, color = AppColors.TextPrimary, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                        }
                        Surface(shape = RoundedCornerShape(12.dp), color = AppColors.Surface, border = BorderStroke(1.dp, AppColors.BorderSecondary)) {
                            Text(lead.relationSubCategory ?: "Service-based", fontSize = 12.sp, color = AppColors.TextPrimary, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                        }
                    }
                    TextButton(onClick = { onAction(LeadAction.OpenAddRelationship(lead)) }) {
                        Text("Edit", color = AppColors.PrimaryBlue)
                    }
                }
            }

            // Potential Earnings
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AppColors.EarningsYellowBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(lead.minPremium, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AppColors.EarningsOrange)
                        Text("Potential Earnings", fontSize = 12.sp, color = AppColors.EarningsOrange)
                    }
                    Surface(shape = RoundedCornerShape(16.dp), color = AppColors.StarYellow) {
                        Text("+1250 BOSS coins", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                    }
                }
            }

            // Car Insurance Details
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = AppColors.Surface,
                border = BorderStroke(1.dp, AppColors.BorderSecondary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Car Insurance", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AppColors.TextPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val details = listOf(
                        "Vehicle Name" to lead.vehicleModel,
                        "Registration Number" to lead.registrationNumber,
                        "Expiring In" to lead.expiryText.replace("Expiring in ", "").replace("Expiring: ", ""),
                        "No claim bonus status" to lead.ncbStatus
                    )
                    
                    details.forEach { (label, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label, fontSize = 14.sp, color = AppColors.TextSecondary)
                            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = AppColors.TextPrimary)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { onAction(LeadAction.OpenEditDetails(lead)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Edit Details", color = AppColors.PrimaryBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Actions
            Button(
                onClick = { onAction(LeadAction.ContinueToQuotes(lead)) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryBlue)
            ) {
                Text("Continue to quotes")
            }
        }
        
        when (uiState.activeDialog) {
            LeadDialogType.MARK_LOST -> {
                MarkLeadLostDialog(
                    lead = lead,
                    onDismiss = { onAction(LeadAction.CloseMarkAsLost) },
                    onSubmit = { reason, other -> onAction(LeadAction.SubmitMarkAsLost(reason, other)) }
                )
            }
            LeadDialogType.MARK_SOLD -> {
                // simple alert or bottom sheet
                AlertDialog(
                    onDismissRequest = { onAction(LeadAction.CloseMarkVehicleSold) },
                    title = { Text("Mark vehicle as sold") },
                    text = { Text("Are you sure you want to mark this vehicle as sold?") },
                    confirmButton = {
                        TextButton(onClick = { onAction(LeadAction.SubmitMarkVehicleSold) }) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { onAction(LeadAction.CloseMarkVehicleSold) }) {
                            Text("Cancel")
                        }
                    }
                )
            }
            LeadDialogType.ADD_RELATIONSHIP -> {
                AddRelationshipDialog(
                    lead = lead,
                    onDismiss = { onAction(LeadAction.CloseAddRelationship) },
                    onSubmit = { cat, subCat -> onAction(LeadAction.SubmitRelationship(cat, subCat)) }
                )
            }
            LeadDialogType.EDIT_DETAILS -> {
                EditDetailsDialog(
                    lead = lead,
                    onDismiss = { onAction(LeadAction.CloseEditDetails) },
                    onSubmit = { ncb, expiry -> onAction(LeadAction.SubmitEditDetails(ncb, expiry)) }
                )
            }
            null -> {}
        }
    }
}
