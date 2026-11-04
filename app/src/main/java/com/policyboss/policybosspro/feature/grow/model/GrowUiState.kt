package com.policyboss.policybosspro.feature.grow.model

enum class KnowledgeCategory(val title: String) {
    INSIGHTS("Insights"),
    VIDEOS("Videos")
}

data class GrowUiState(
    val isLoading: Boolean = false,
    val banner: GrowBannerItem? = null,
    val contests: List<GrowContestItem> = emptyList(),
    val templates: List<GrowTemplateItem> = emptyList(),
    val leadForm: GrowLeadFormItem? = null,
    val greeting: GrowGreetingItem? = null,
    val dailyPoster: GrowPosterItem? = null,
    val selectedKnowledgeCategory: KnowledgeCategory = KnowledgeCategory.INSIGHTS,
    val articles: List<GrowArticleItem> = emptyList(),
    val error: String? = null
)
