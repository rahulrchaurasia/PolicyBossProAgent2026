package com.policyboss.policybosspro.feature.grow.model

sealed interface GrowAction {
    data object OnWatchVideoClick : GrowAction
    data class OnContestClick(val contest: GrowContestItem) : GrowAction
    data class OnTemplateClick(val template: GrowTemplateItem) : GrowAction
    data object OnLeadFormPreviewClick : GrowAction
    data object OnLeadFormViewAllClick : GrowAction
    data object OnGreetingEditClick : GrowAction
    data object OnGreetingShareClick : GrowAction
    data object OnGreetingViewAllClick : GrowAction
    data object OnDailyPosterPreviewClick : GrowAction
    data object OnDailyPosterShareClick : GrowAction
    data object OnDailyPosterViewAllClick : GrowAction
    data class OnSelectKnowledgeCategory(val category: KnowledgeCategory) : GrowAction
    data class OnReadArticleClick(val article: GrowArticleItem) : GrowAction
    data object OnArticlesViewAllClick : GrowAction
    data object OnRetry : GrowAction
}
