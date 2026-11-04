package com.policyboss.policybosspro.core.repository.lead

import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.home.model.home.HomeLead
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LeadRepositoryImpl @Inject constructor() : LeadRepository {
    
    // In-memory cache for updates to persist locally across screen navigations
    private val localLeads = AppDummyData.topLeads.toMutableList()

    override fun getLeads(): Flow<Result<List<HomeLead>>> = flow {
        delay(300) // simulate network delay
        emit(Result.success(localLeads.toList()))
    }

    override fun getLeadById(id: String): Flow<Result<HomeLead>> = flow {
        delay(200)
        val lead = localLeads.find { it.id == id }
        if (lead != null) {
            emit(Result.success(lead))
        } else {
            emit(Result.failure(Exception("Lead not found")))
        }
    }

    override suspend fun updateLead(lead: HomeLead): Result<HomeLead> {
        delay(200)
        val index = localLeads.indexOfFirst { it.id == lead.id }
        return if (index != -1) {
            localLeads[index] = lead
            Result.success(lead)
        } else {
            Result.failure(Exception("Lead not found"))
        }
    }

    override suspend fun markLeadAsLost(leadId: String, reason: String, otherDescription: String?): Result<Unit> {
        delay(300)
        val index = localLeads.indexOfFirst { it.id == leadId }
        return if (index != -1) {
            val lead = localLeads[index]
            localLeads[index] = lead.copy(status = "Lost")
            Result.success(Unit)
        } else {
            Result.failure(Exception("Lead not found"))
        }
    }

    override suspend fun markVehicleAsSold(leadId: String): Result<Unit> {
        delay(300)
        val index = localLeads.indexOfFirst { it.id == leadId }
        return if (index != -1) {
            val lead = localLeads[index]
            localLeads[index] = lead.copy(status = "Won")
            Result.success(Unit)
        } else {
            Result.failure(Exception("Lead not found"))
        }
    }

    override suspend fun saveRelationship(leadId: String, category: String, subCategory: String): Result<Unit> {
        delay(300)
        return if (localLeads.any { it.id == leadId }) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Lead not found"))
        }
    }
}
