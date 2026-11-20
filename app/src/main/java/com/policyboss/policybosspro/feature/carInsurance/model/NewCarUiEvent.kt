package com.policyboss.policybosspro.feature.carInsurance.model

sealed interface NewCarUiEvent {
    data object NavigateBack : NewCarUiEvent
    data object NavigateBackToForm : NewCarUiEvent
    data object NavigateToQuoteLoading : NewCarUiEvent
    data class ShowSnackbar(val message: String) : NewCarUiEvent
    data class OpenUrl(val url: String) : NewCarUiEvent
    data class ShareText(val text: String) : NewCarUiEvent
}

sealed interface QuoteLoadingUiEvent {
    data class NavigateToQuotes(val quoteId: String, val quoteUrl: String?) : QuoteLoadingUiEvent
    data object CloseLoading : QuoteLoadingUiEvent
    data class ShowSnackbar(val message: String) : QuoteLoadingUiEvent
}
