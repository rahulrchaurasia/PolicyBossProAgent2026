package com.policyboss.policybosspro.core.repository.lead

import com.policyboss.policybosspro.feature.home.model.home.HomeLead
import kotlinx.coroutines.flow.Flow

interface LeadRepository {
    fun getLeads(): Flow<Result<List<HomeLead>>>
    fun getLeadById(id: String): Flow<Result<HomeLead>>
    
    // Simulate updating a lead (like changing NCB or Expiry, or marking as lost/sold)
    suspend fun updateLead(lead: HomeLead): Result<HomeLead>
    suspend fun markLeadAsLost(leadId: String, reason: String, otherDescription: String?): Result<Unit>
    suspend fun markVehicleAsSold(leadId: String): Result<Unit>
    suspend fun saveRelationship(leadId: String, category: String, subCategory: String): Result<Unit>
}
