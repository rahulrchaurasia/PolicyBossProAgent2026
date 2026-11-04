package com.policyboss.policybosspro.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.repository.profile.ProfileRepository
import com.policyboss.policybosspro.feature.profile.model.PersonalInformation
import com.policyboss.policybosspro.feature.profile.model.ProfileAction
import com.policyboss.policybosspro.feature.profile.model.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getUserProfile().collect { result ->
                result.onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            profile = profile,
                            errorMessage = null
                        )
                    }
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Failed to load profile"
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.ToggleUseSameAsPosp -> {
                viewModelScope.launch {
                    repository.toggleUseSameAsPosp(action.isSame)
                }
            }
            is ProfileAction.SavePersonalInfo -> {
                savePersonalInfo(action.personalInfo)
            }
            ProfileAction.Refresh -> {
                loadProfile()
            }
        }
    }

    private fun savePersonalInfo(info: PersonalInformation) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val result = repository.updatePersonalInfo(info)
            result.onSuccess { updatedProfile ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        profile = updatedProfile,
                        saveSuccess = true
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.localizedMessage ?: "Failed to save personal info"
                    )
                }
            }
        }
    }

    fun consumeSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
}

