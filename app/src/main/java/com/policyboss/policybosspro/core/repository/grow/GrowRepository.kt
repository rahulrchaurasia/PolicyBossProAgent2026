package com.policyboss.policybosspro.core.repository.grow

import com.policyboss.policybosspro.feature.grow.model.GrowArticleItem
import com.policyboss.policybosspro.feature.grow.model.GrowBannerItem
import com.policyboss.policybosspro.feature.grow.model.GrowContestItem
import com.policyboss.policybosspro.feature.grow.model.GrowGreetingItem
import com.policyboss.policybosspro.feature.grow.model.GrowLeadFormItem
import com.policyboss.policybosspro.feature.grow.model.GrowPosterItem
import com.policyboss.policybosspro.feature.grow.model.GrowTemplateItem
import com.policyboss.policybosspro.feature.grow.model.KnowledgeCategory
import kotlinx.coroutines.flow.Flow

interface GrowRepository {
    fun getBanner(): Flow<Result<GrowBannerItem>>
    fun getContests(): Flow<Result<List<GrowContestItem>>>
    fun getTemplates(): Flow<Result<List<GrowTemplateItem>>>
    fun getLeadForm(): Flow<Result<GrowLeadFormItem>>
    fun getGreeting(): Flow<Result<GrowGreetingItem>>
    fun getDailyPoster(): Flow<Result<GrowPosterItem>>
    fun getArticles(category: KnowledgeCategory): Flow<Result<List<GrowArticleItem>>>
}
