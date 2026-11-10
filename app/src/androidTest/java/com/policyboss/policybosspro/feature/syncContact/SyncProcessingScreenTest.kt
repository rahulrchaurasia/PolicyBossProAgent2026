package com.policyboss.policybosspro.feature.syncContact

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.policyboss.policybosspro.feature.syncContact.model.SyncPhase
import com.policyboss.policybosspro.feature.syncContact.model.SyncProcessingState
import com.policyboss.policybosspro.feature.syncContact.ui.SyncProcessingScreen
import com.policyboss.policybosspro.ui.theme.PolicyBossTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SyncProcessingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test_syncProcessing_whenSyncing_rendersProgressAndRadar() {
        val uiState = SyncProcessingState(
            isSyncing = true,
            isSuccess = false,
            currentPhase = SyncPhase.CONTACT_UPLOADING,
            progress = 0.5f,
            contactsFound = 120,
            statusMessage = "Syncing your contacts..."
        )

        composeTestRule.setContent {
            PolicyBossTheme {
                SyncProcessingScreen(
                    uiState = uiState,
                    onCompleteProfileClick = {},
                    onRetryClick = {}
                )
            }
        }

        // Verify FetchingContactsContent is displayed
        composeTestRule.onNodeWithTag("FetchingContactsContent").assertIsDisplayed()
        composeTestRule.onNodeWithText("Syncing your contacts...").assertIsDisplayed()

        // Verify Success content is NOT displayed
        composeTestRule.onNodeWithTag("ContactsFetchedSuccessContent").assertDoesNotExist()
    }

    @Test
    fun test_syncProcessing_whenSuccess_switchesToSuccessScreenAndTriggersLottie() {
        val state = mutableStateOf(
            SyncProcessingState(
                isSyncing = true,
                isSuccess = false,
                currentPhase = SyncPhase.CONTACT_UPLOADING,
                progress = 0.5f,
                contactsFound = 120
            )
        )

        composeTestRule.setContent {
            PolicyBossTheme {
                SyncProcessingScreen(
                    uiState = state.value,
                    onCompleteProfileClick = {},
                    onRetryClick = {}
                )
            }
        }

        // 1. Initial State: Syncing
        composeTestRule.onNodeWithTag("FetchingContactsContent").assertIsDisplayed()

        // 2. State transition to Success
        composeTestRule.mainClock.autoAdvance = false
        state.value = SyncProcessingState(
            isSyncing = false,
            isSuccess = true,
            currentPhase = SyncPhase.CALL_LOG_COMPLETED,
            progress = 1.0f,
            contactsFound = 350
        )

        // Advance clock across Crossfade (400ms duration)
        composeTestRule.mainClock.advanceTimeBy(500)

        // 3. Verify Success Screen is displayed
        composeTestRule.onNodeWithTag("ContactsFetchedSuccessContent").assertIsDisplayed()
        composeTestRule.onNodeWithText("Congratulations!").assertIsDisplayed()
        composeTestRule.onNodeWithTag("DiscoverPolicyBossButton").assertIsDisplayed()

        // 4. Verify Lottie Animation Composables (Confetti + Checkmark badge) are present in node hierarchy and visible
        composeTestRule.onNodeWithTag("SyncSuccessLottieAnimation").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SyncSuccessConfettiAnimation").assertIsDisplayed()

        // 5. Advance clock across animation duration without crash or stall
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.onNodeWithTag("SyncSuccessLottieAnimation").assertIsDisplayed()

        composeTestRule.mainClock.autoAdvance = true
    }

    @Test
    fun test_syncProcessing_retryClick_invokesCallback() {
        var retryClicked = false
        val uiState = SyncProcessingState(
            isSyncing = false,
            isSuccess = false,
            errorMessage = "Network timeout. Please check your internet connection."
        )

        composeTestRule.setContent {
            PolicyBossTheme {
                SyncProcessingScreen(
                    uiState = uiState,
                    onCompleteProfileClick = {},
                    onRetryClick = { retryClicked = true }
                )
            }
        }

        // Verify error message is rendered
        composeTestRule.onNodeWithText("Network timeout. Please check your internet connection.").assertIsDisplayed()

        // Perform click on Retry Sync
        composeTestRule.onNodeWithTag("RetrySyncButton").performClick()

        // Assert callback invoked
        assertTrue("Expected onRetryClick to be invoked upon clicking Retry Sync", retryClicked)
    }
}

