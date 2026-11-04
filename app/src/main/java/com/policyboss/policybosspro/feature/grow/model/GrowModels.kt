package com.policyboss.policybosspro.feature.grow.model

import androidx.annotation.DrawableRes
import com.policyboss.policybosspro.R

data class GrowBannerItem(
    val id: String = "grow_banner_1",
    val tag: String = "LEARN AND GROW",
    val title: String = "How to grow your\nsales - Expert tips\nby PolicyBoss",
    val buttonText: String = "Watch video",
    @DrawableRes val imageRes: Int = R.drawable.banner_grow_business
)

data class GrowContestItem(
    val id: String,
    val title: String,
    @DrawableRes val bannerRes: Int = R.drawable.banner_grow_ongoing
)

data class GrowTemplateItem(
    val id: String,
    val title: String,
    @DrawableRes val imageRes: Int,
    val isFeatured: Boolean = false
)

data class GrowLeadFormItem(
    val id: String = "lead_form_1",
    val title: String = "Zero Depreciation & Engine Protect.",
    @DrawableRes val imageRes: Int = R.drawable.ic_grow_car
)

data class GrowGreetingItem(
    val id: String = "greeting_1",
    val title: String = "Happy Birthday 🎂",
    val subtitle: String = "Hi Everett! 🎉 Hope you have an amazing day ahead. May all your dreams come true.",
    @DrawableRes val imageRes: Int = R.drawable.ic_grow_birthday
)

data class GrowPosterItem(
    val id: String = "poster_1",
    val title: String = "Comprehensive Car Insurance",
    @DrawableRes val imageRes: Int = R.drawable.ic_grow_daily_posture
)

data class GrowArticleItem(
    val id: String,
    val readTime: String,
    val title: String,
    val category: KnowledgeCategory = KnowledgeCategory.INSIGHTS,
    val url: String? = null
)
