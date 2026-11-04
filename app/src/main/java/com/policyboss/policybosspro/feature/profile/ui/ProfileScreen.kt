package com.policyboss.policybosspro.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.profile.model.ProfileUiState
import com.policyboss.policybosspro.feature.profile.model.UserProfile
import com.policyboss.policybosspro.feature.profile.ui.components.AccountDetailsSection
import com.policyboss.policybosspro.feature.profile.ui.components.DisplayInformationSection
import com.policyboss.policybosspro.feature.profile.ui.components.PersonalInformationSection
import com.policyboss.policybosspro.feature.profile.ui.components.ProfileHeader
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onBackClick: () -> Unit,
    onToggleUseSameAsPosp: (Boolean) -> Unit,
    onEditDisplayInfo: () -> Unit,
    onEditPersonalInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AppColors.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AppColors.White)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AppColors.PrimaryBlue
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // Profile Top Header with Back button and Avatar
                    ProfileHeader(
                        fullName = uiState.profile.fullName,
                        initials = uiState.profile.initials,
                        onBackClick = onBackClick
                    )

                    // Card Content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        AccountDetailsSection(
                            accountDetails = uiState.profile.accountDetails
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        DisplayInformationSection(
                            displayInfo = uiState.profile.displayInfo,
                            useSameAsPosp = uiState.profile.useSameAsPosp,
                            onToggleUseSameAsPosp = onToggleUseSameAsPosp,
                            onEditClick = onEditDisplayInfo
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PersonalInformationSection(
                            personalInfo = uiState.profile.personalInfo,
                            onEditClick = onEditPersonalInfo
                        )

                        Spacer(modifier = Modifier.height(32.dp))
                        Spacer(modifier = Modifier.navigationBarsPadding())
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(
        uiState = ProfileUiState(profile = AppDummyData.defaultUserProfile),
        onBackClick = {},
        onToggleUseSameAsPosp = {},
        onEditDisplayInfo = {},
        onEditPersonalInfo = {}
    )
}

