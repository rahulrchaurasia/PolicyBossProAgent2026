package com.policyboss.policybosspro.feature.grow.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.repository.grow.GrowRepository
import com.policyboss.policybosspro.feature.grow.model.GrowAction
import com.policyboss.policybosspro.feature.grow.model.GrowUiState
import com.policyboss.policybosspro.feature.grow.model.KnowledgeCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface GrowNavigationEvent {
    data class ShareText(val text: String) : GrowNavigationEvent
    data class OpenUrl(val url: String) : GrowNavigationEvent
    data class ShowToast(val message: String) : GrowNavigationEvent
}

@HiltViewModel
class GrowViewModel @Inject constructor(
    private val repository: GrowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GrowUiState())
    val uiState: StateFlow<GrowUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<GrowNavigationEvent>()
    val events: SharedFlow<GrowNavigationEvent> = _events.asSharedFlow()

    init {
        loadGrowData()
    }

    private fun loadGrowData() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.getBanner().collect { result ->
                result.onSuccess { banner -> _uiState.update { it.copy(banner = banner) } }
            }
            repository.getContests().collect { result ->
                result.onSuccess { contests -> _uiState.update { it.copy(contests = contests) } }
            }
            repository.getTemplates().collect { result ->
                result.onSuccess { templates -> _uiState.update { it.copy(templates = templates) } }
            }
            repository.getLeadForm().collect { result ->
                result.onSuccess { leadForm -> _uiState.update { it.copy(leadForm = leadForm) } }
            }
            repository.getGreeting().collect { result ->
                result.onSuccess { greeting -> _uiState.update { it.copy(greeting = greeting) } }
            }
            repository.getDailyPoster().collect { result ->
                result.onSuccess { poster -> _uiState.update { it.copy(dailyPoster = poster) } }
            }
            loadArticles(_uiState.value.selectedKnowledgeCategory)

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun loadArticles(category: KnowledgeCategory) {
        repository.getArticles(category).collect { result ->
            result.onSuccess { articles ->
                _uiState.update { it.copy(articles = articles) }
            }
        }
    }

    fun onAction(action: GrowAction) {
        when (action) {
            GrowAction.OnWatchVideoClick -> {
                viewModelScope.launch {
                    _events.emit(GrowNavigationEvent.ShowToast("Playing tutorial video..."))
                }
            }
            is GrowAction.OnContestClick -> {
                viewModelScope.launch {
                    _events.emit(GrowNavigationEvent.ShowToast("Selected Contest: ${action.contest.title}"))
                }
            }
            is GrowAction.OnTemplateClick -> {
                viewModelScope.launch {
                    _events.emit(GrowNavigationEvent.ShowToast("Opening Template: ${action.template.title}"))
                }
            }
            GrowAction.OnLeadFormPreviewClick -> {
                viewModelScope.launch {
                    _events.emit(GrowNavigationEvent.ShowToast("Previewing Lead Form"))
                }
            }
            GrowAction.OnLeadFormViewAllClick -> {
                viewModelScope.launch {
                    _events.emit(GrowNavigationEvent.ShowToast("Viewing all Lead Forms"))
                }
            }
            GrowAction.OnGreetingEditClick -> {
                viewModelScope.launch {
                    _events.emit(GrowNavigationEvent.ShowToast("Editing Greeting"))
                }
            }
            GrowAction.OnGreetingShareClick -> {
                viewModelScope.launch {
                    val greeting = _uiState.value.greeting
                    val text = "${greeting?.title ?: "Happy Birthday"}\n${greeting?.subtitle ?: ""}"
                    _events.emit(GrowNavigationEvent.ShareText(text))
                }
            }
            GrowAction.OnGreetingViewAllClick -> {
                viewModelScope.launch {
                    _events.emit(GrowNavigationEvent.ShowToast("Viewing all Greetings"))
                }
            }
            GrowAction.OnDailyPosterPreviewClick -> {
                viewModelScope.launch {
                    _events.emit(GrowNavigationEvent.ShowToast("Viewing Daily Poster"))
                }
            }
            GrowAction.OnDailyPosterShareClick -> {
                viewModelScope.launch {
                    val poster = _uiState.value.dailyPoster
                    val text = "Check out our latest Insurance update: ${poster?.title ?: "Car Insurance"}"
                    _events.emit(GrowNavigationEvent.ShareText(text))
                }
            }
            GrowAction.OnDailyPosterViewAllClick -> {
                viewModelScope.launch {
                    _events.emit(GrowNavigationEvent.ShowToast("Viewing all Daily Posters"))
                }
            }
            is GrowAction.OnSelectKnowledgeCategory -> {
                _uiState.update { it.copy(selectedKnowledgeCategory = action.category) }
                viewModelScope.launch {
                    loadArticles(action.category)
                }
            }
            is GrowAction.OnReadArticleClick -> {
                viewModelScope.launch {
                    _events.emit(GrowNavigationEvent.ShowToast("Opening Article: ${action.article.title}"))
                }
            }
            GrowAction.OnArticlesViewAllClick -> {
                viewModelScope.launch {
                    _events.emit(GrowNavigationEvent.ShowToast("Viewing all Knowledge Hub items"))
                }
            }
            GrowAction.OnRetry -> {
                loadGrowData()
            }
        }
    }
}
