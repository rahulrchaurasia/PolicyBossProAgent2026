package com.policyboss.policybosspro.feature.carInsurance.model

sealed interface NewCarUiEvent {
    data object NavigateBack : NewCarUiEvent
    data class NavigateToQuotes(val quoteId: String, val quoteUrl: String?) : NewCarUiEvent
    data class ShowSnackbar(val message: String) : NewCarUiEvent
    data class OpenUrl(val url: String) : NewCarUiEvent
}
