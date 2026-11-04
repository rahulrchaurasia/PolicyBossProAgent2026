package com.policyboss.policybosspro.feature.profile.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.policyboss.policybosspro.feature.profile.model.ProfileAction

@Composable
fun EditPersonalInfoRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.consumeSaveSuccess()
            onNavigateBack()
        }
    }

    EditPersonalInfoScreen(
        personalInfo = uiState.profile.personalInfo,
        isSaving = uiState.isSaving,
        onSaveClick = { updatedInfo ->
            viewModel.onAction(ProfileAction.SavePersonalInfo(updatedInfo))
        },
        onBackClick = onNavigateBack,
        modifier = modifier
    )
}
