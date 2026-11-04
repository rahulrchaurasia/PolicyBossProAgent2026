package com.policyboss.policybosspro.feature.lead.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.home.model.home.HomeLead
import com.policyboss.policybosspro.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkLeadLostDialog(
    lead: HomeLead,
    onDismiss: () -> Unit,
    onSubmit: (reason: String, otherDesc: String?) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = AppColors.Surface) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Mark lead as lost", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = AppColors.TextPrimary)
            Text("Select reason for losing this lead", color = AppColors.TextSecondary, fontSize = 14.sp)
            
            var expanded by remember { mutableStateOf(false) }
            var selectedReason by remember { mutableStateOf("") }
            val reasons = listOf(
                "Customer renewed directly with insurance company",
                "Customer renewed with another agent",
                "Customer renewed with another broker",
                "Other"
            )
            var otherText by remember { mutableStateOf("") }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedReason,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select reason") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    reasons.forEach { reason ->
                        DropdownMenuItem(
                            text = { Text(reason, color = AppColors.TextPrimary) },
                            onClick = {
                                selectedReason = reason
                                expanded = false
                            }
                        )
                    }
                }
            }

            if (selectedReason == "Other") {
                OutlinedTextField(
                    value = otherText,
                    onValueChange = { otherText = it },
                    label = { Text("Additional information") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = { onSubmit(selectedReason, if (selectedReason == "Other") otherText else null) },
                enabled = selectedReason.isNotEmpty() && (selectedReason != "Other" || otherText.isNotEmpty()),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryBlue)
            ) {
                Text("Submit Reason")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRelationshipDialog(
    lead: HomeLead,
    onDismiss: () -> Unit,
    onSubmit: (category: String, subCategory: String) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = AppColors.Surface) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Add Relationship", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = AppColors.TextPrimary)
            Text(lead.name, color = AppColors.TextSecondary, fontSize = 14.sp)
            
            var selectedCategory by remember { mutableStateOf("") }
            val categories = listOf("Personal", "Business", "Family", "Random/Others")
            
            var expandedCategory by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = it }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat, color = AppColors.TextPrimary) },
                            onClick = {
                                selectedCategory = cat
                                expandedCategory = false
                            }
                        )
                    }
                }
            }

            var selectedSubCategory by remember { mutableStateOf("") }
            val subCategories = listOf(
                "Retail", "Service-based", "Technology", "Agriculture", 
                "Construction & Real Estate", "Hospitality", "Arts & Crafts", 
                "Non-profit", "Entertainment & Media", "Finance & Insurance", 
                "Client", "Customer"
            )
            var expandedSubCategory by remember { mutableStateOf(false) }
            
            ExposedDropdownMenuBox(
                expanded = expandedSubCategory,
                onExpandedChange = { expandedSubCategory = it }
            ) {
                OutlinedTextField(
                    value = selectedSubCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Subcategory") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubCategory) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedSubCategory,
                    onDismissRequest = { expandedSubCategory = false }
                ) {
                    subCategories.forEach { subCat ->
                        DropdownMenuItem(
                            text = { Text(subCat, color = AppColors.TextPrimary) },
                            onClick = {
                                selectedSubCategory = subCat
                                expandedSubCategory = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = { onSubmit(selectedCategory, selectedSubCategory) },
                enabled = selectedCategory.isNotEmpty() && selectedSubCategory.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryBlue)
            ) {
                Text("Save")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDetailsDialog(
    lead: HomeLead,
    onDismiss: () -> Unit,
    onSubmit: (ncbStatus: String, expiryDate: String) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = AppColors.Surface) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Edit Details", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = AppColors.TextPrimary)
            
            var ncbStatus by remember { mutableStateOf(lead.ncbStatus) }
            var expiryDate by remember { mutableStateOf(lead.expiryText.replace("Expiring in ", "").replace("Expiring: ", "")) }
            
            OutlinedTextField(
                value = ncbStatus,
                onValueChange = { ncbStatus = it },
                label = { Text("NCB Status") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = expiryDate,
                onValueChange = { expiryDate = it },
                label = { Text("Expiry Date") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Button(
                onClick = { onSubmit(ncbStatus, expiryDate) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryBlue)
            ) {
                Text("Save")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
