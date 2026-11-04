package com.policyboss.policybosspro.feature.lead.model

import com.policyboss.policybosspro.feature.home.model.home.HomeLead

sealed interface LeadAction {
    data class SelectLeadTab(val tab: String) : LeadAction
    data class SelectStatusFilter(val status: String?) : LeadAction
    data class SelectExpiryFilter(val filter: String?) : LeadAction
    data class ToggleVehicleFilter(val filter: String) : LeadAction
    
    data class OpenLeadDetails(val lead: HomeLead) : LeadAction
    object CloseLeadDetails : LeadAction
    
    data class OpenMarkAsLost(val lead: HomeLead) : LeadAction
    object CloseMarkAsLost : LeadAction
    data class SubmitMarkAsLost(val reason: String, val otherDescription: String?) : LeadAction
    
    data class OpenMarkVehicleSold(val lead: HomeLead) : LeadAction
    object CloseMarkVehicleSold : LeadAction
    object SubmitMarkVehicleSold : LeadAction
    
    data class OpenAddRelationship(val lead: HomeLead) : LeadAction
    object CloseAddRelationship : LeadAction
    data class SubmitRelationship(val category: String, val subCategory: String) : LeadAction
    
    data class OpenEditDetails(val lead: HomeLead) : LeadAction
    object CloseEditDetails : LeadAction
    data class SubmitEditDetails(val ncbStatus: String, val expiryDate: String) : LeadAction
    
    data class ContinueToQuotes(val lead: HomeLead) : LeadAction
    data class ShareLead(val lead: HomeLead) : LeadAction
    
    object LoadLeads : LeadAction
    object OnRetryClick : LeadAction
}
