package com.policyboss.policybosspro.feature.lead.model

sealed interface LeadUiEvent {
    data class ShowSnackbar(val message: String) : LeadUiEvent
}

