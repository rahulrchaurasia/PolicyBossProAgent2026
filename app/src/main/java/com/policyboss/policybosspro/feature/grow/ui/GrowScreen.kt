package com.policyboss.policybosspro.feature.grow.ui

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.grow.model.GrowAction
import com.policyboss.policybosspro.feature.grow.model.GrowUiState
import com.policyboss.policybosspro.feature.grow.ui.components.GrowArticlesSection
import com.policyboss.policybosspro.feature.grow.ui.components.GrowBusinessBanner
import com.policyboss.policybosspro.feature.grow.ui.components.GrowContestSection
import com.policyboss.policybosspro.feature.grow.ui.components.GrowDailyPosterSection
import com.policyboss.policybosspro.feature.grow.ui.components.GrowGreetingSection
import com.policyboss.policybosspro.feature.grow.ui.components.GrowKnowledgeHubSection
import com.policyboss.policybosspro.feature.grow.ui.components.GrowLeadFormSection
import com.policyboss.policybosspro.feature.grow.ui.components.GrowTemplatesSection
import com.policyboss.policybosspro.ui.theme.AppColors
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun GrowScreen(
    uiState: GrowUiState,
    onAction: (GrowAction) -> Unit,
    modifier: Modifier = Modifier,
    events: SharedFlow<GrowNavigationEvent>? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val context = LocalContext.current
    val layoutDirection = LocalLayoutDirection.current

    LaunchedEffect(events) {
        events?.collectLatest { event ->
            when (event) {
                is GrowNavigationEvent.ShareText -> {
                    shareText(context, event.text)
                }
                is GrowNavigationEvent.OpenUrl -> {
                    // Open URL if required
                }
                is GrowNavigationEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    if (uiState.isLoading && uiState.banner == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding)
                .background(AppColors.Background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = AppColors.Primary)
        }
        return
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .verticalScroll(scrollState)
            .padding(
                start = contentPadding.calculateStartPadding(layoutDirection) + 16.dp,
                top = contentPadding.calculateTopPadding() + 16.dp,
                end = contentPadding.calculateEndPadding(layoutDirection) + 16.dp,
                bottom = contentPadding.calculateBottomPadding() + 24.dp
            ),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // Top Header
        Text(
            text = "Grow your business",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.Slate900
        )

        // 1. Promotional Business Banner
        uiState.banner?.let { banner ->
            GrowBusinessBanner(
                banner = banner,
                onWatchVideoClick = { onAction(GrowAction.OnWatchVideoClick) }
            )
        }

        // 2. Ongoing Contests
        GrowContestSection(
            contests = uiState.contests,
            onContestClick = { onAction(GrowAction.OnContestClick(it)) }
        )

        // 3. Templates
        GrowTemplatesSection(
            templates = uiState.templates,
            onTemplateClick = { onAction(GrowAction.OnTemplateClick(it)) }
        )

        // 4. Lead Forms
        GrowLeadFormSection(
            leadForm = uiState.leadForm,
            onPreviewClick = { onAction(GrowAction.OnLeadFormPreviewClick) },
            onViewAllClick = { onAction(GrowAction.OnLeadFormViewAllClick) }
        )

        // 5. Greetings
        GrowGreetingSection(
            greeting = uiState.greeting,
            onEditClick = { onAction(GrowAction.OnGreetingEditClick) },
            onShareClick = { onAction(GrowAction.OnGreetingShareClick) },
            onViewAllClick = { onAction(GrowAction.OnGreetingViewAllClick) }
        )

        // 6. Daily Posters
        GrowDailyPosterSection(
            poster = uiState.dailyPoster,
            onViewClick = { onAction(GrowAction.OnDailyPosterPreviewClick) },
            onShareClick = { onAction(GrowAction.OnDailyPosterShareClick) },
            onViewAllClick = { onAction(GrowAction.OnDailyPosterViewAllClick) }
        )

        // 7. Knowledge Hub Category Selector
        GrowKnowledgeHubSection(
            selectedCategory = uiState.selectedKnowledgeCategory,
            onCategorySelect = { onAction(GrowAction.OnSelectKnowledgeCategory(it)) }
        )

        // 8. Articles
        GrowArticlesSection(
            articles = uiState.articles,
            onReadArticleClick = { onAction(GrowAction.OnReadArticleClick(it)) },
            onViewAllClick = { onAction(GrowAction.OnArticlesViewAllClick) }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun shareText(context: Context, text: String) {
    try {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share via")
        context.startActivity(shareIntent)
    } catch (_: Exception) {
    }
}
