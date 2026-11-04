package com.policyboss.policybosspro.feature.profile.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.policyboss.policybosspro.feature.profile.model.ProfileAction

@Composable
fun ProfileRoute(
    onNavigateBack: () -> Unit,
    onNavigateToEditPersonalInfo: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ProfileScreen(
        uiState = uiState,
        onBackClick = onNavigateBack,
        onToggleUseSameAsPosp = { isSame ->
            viewModel.onAction(ProfileAction.ToggleUseSameAsPosp(isSame))
        },
        onEditDisplayInfo = {
            Toast.makeText(context, "Edit display details", Toast.LENGTH_SHORT).show()
        },
        onEditPersonalInfo = onNavigateToEditPersonalInfo,
        modifier = modifier
    )
}
