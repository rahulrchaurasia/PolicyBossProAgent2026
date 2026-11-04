package com.policyboss.policybosspro.core.repository.grow

import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.grow.model.GrowArticleItem
import com.policyboss.policybosspro.feature.grow.model.GrowBannerItem
import com.policyboss.policybosspro.feature.grow.model.GrowContestItem
import com.policyboss.policybosspro.feature.grow.model.GrowGreetingItem
import com.policyboss.policybosspro.feature.grow.model.GrowLeadFormItem
import com.policyboss.policybosspro.feature.grow.model.GrowPosterItem
import com.policyboss.policybosspro.feature.grow.model.GrowTemplateItem
import com.policyboss.policybosspro.feature.grow.model.KnowledgeCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GrowRepositoryImpl @Inject constructor() : GrowRepository {

    override fun getBanner(): Flow<Result<GrowBannerItem>> = flow {
        emit(Result.success(AppDummyData.growBanner))
    }

    override fun getContests(): Flow<Result<List<GrowContestItem>>> = flow {
        emit(Result.success(AppDummyData.growContests))
    }

    override fun getTemplates(): Flow<Result<List<GrowTemplateItem>>> = flow {
        emit(Result.success(AppDummyData.growTemplates))
    }

    override fun getLeadForm(): Flow<Result<GrowLeadFormItem>> = flow {
        emit(Result.success(AppDummyData.growLeadForm))
    }

    override fun getGreeting(): Flow<Result<GrowGreetingItem>> = flow {
        emit(Result.success(AppDummyData.growGreeting))
    }

    override fun getDailyPoster(): Flow<Result<GrowPosterItem>> = flow {
        emit(Result.success(AppDummyData.growDailyPoster))
    }

    override fun getArticles(category: KnowledgeCategory): Flow<Result<List<GrowArticleItem>>> = flow {
        val filtered = AppDummyData.growArticles.filter { it.category == category }
        emit(Result.success(filtered))
    }
}
