package com.policyboss.policybosspro.feature.grow

import com.policyboss.policybosspro.core.repository.grow.GrowRepositoryImpl
import com.policyboss.policybosspro.feature.grow.model.KnowledgeCategory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GrowFeatureTest {

    private val repository = GrowRepositoryImpl()

    @Test
    fun testRepository_loadsBannerSuccessfully() = runBlocking {
        val result = repository.getBanner().first()
        assertTrue(result.isSuccess)
        val banner = result.getOrNull()
        assertNotNull(banner)
        assertEquals("LEARN AND GROW", banner?.tag)
    }

    @Test
    fun testRepository_loadsContestsSuccessfully() = runBlocking {
        val result = repository.getContests().first()
        assertTrue(result.isSuccess)
        val contests = result.getOrNull().orEmpty()
        assertTrue(contests.isNotEmpty())
        assertEquals("Win a trip to Dubai", contests[0].title)
    }

    @Test
    fun testRepository_loadsTemplatesSuccessfully() = runBlocking {
        val result = repository.getTemplates().first()
        assertTrue(result.isSuccess)
        val templates = result.getOrNull().orEmpty()
        assertEquals(3, templates.size)
        assertTrue(templates.any { it.isFeatured && it.title == "Lead Forms" })
    }

    @Test
    fun testRepository_filtersArticlesByCategory() = runBlocking {
        val insightsResult = repository.getArticles(KnowledgeCategory.INSIGHTS).first()
        assertTrue(insightsResult.isSuccess)
        val insights = insightsResult.getOrNull().orEmpty()
        assertTrue(insights.all { it.category == KnowledgeCategory.INSIGHTS })
        assertEquals(3, insights.size)

        val videosResult = repository.getArticles(KnowledgeCategory.VIDEOS).first()
        assertTrue(videosResult.isSuccess)
        val videos = videosResult.getOrNull().orEmpty()
        assertTrue(videos.all { it.category == KnowledgeCategory.VIDEOS })
        assertEquals(3, videos.size)
    }

    @Test
    fun testRepository_loadsLeadFormGreetingAndPoster() = runBlocking {
        val leadFormResult = repository.getLeadForm().first()
        assertTrue(leadFormResult.isSuccess)
        assertNotNull(leadFormResult.getOrNull())

        val greetingResult = repository.getGreeting().first()
        assertTrue(greetingResult.isSuccess)
        assertTrue(greetingResult.getOrNull()?.title?.contains("Happy Birthday") == true)

        val posterResult = repository.getDailyPoster().first()
        assertTrue(posterResult.isSuccess)
        assertEquals("Comprehensive Car Insurance", posterResult.getOrNull()?.title)
    }
}
