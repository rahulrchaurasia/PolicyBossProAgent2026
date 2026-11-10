package com.policyboss.policybosspro.feature.syncContact

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performFirstLinkClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.policyboss.policybosspro.core.constant.SyncContactUrls
import com.policyboss.policybosspro.feature.syncContact.ui.welcome.Slide4Content
import com.policyboss.policybosspro.feature.syncContact.ui.welcome.SyncContactDetailsDialog
import com.policyboss.policybosspro.ui.theme.PolicyBossTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Slide4ContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test_slide4_checkboxRows_toggleViaLabelText() {
        var isSmsChecked by mutableStateOf(false)
        var isCallChecked by mutableStateOf(false)
        var isAgreeChecked by mutableStateOf(false)

        composeTestRule.setContent {
            PolicyBossTheme {
                Slide4Content(
                    isSmsChecked = isSmsChecked,
                    isCallChecked = isCallChecked,
                    isAgreeChecked = isAgreeChecked,
                    onSmsChange = { isSmsChecked = it },
                    onCallChange = { isCallChecked = it },
                    onAgreeChange = { isAgreeChecked = it },
                    onOpenUrl = {},
                    onOpenSettings = {}
                )
            }
        }

        val agreeNode = composeTestRule.onNodeWithText(
            "I hereby agree to the terms & conditions for using Sync Contacts."
        )
        agreeNode.performScrollTo()
        agreeNode.assertIsOff()
        agreeNode.performClick()
        agreeNode.assertIsOn()
        assertTrue(isAgreeChecked)
    }

    @Test
    fun test_slide4_clickHereOpensDetailsDialog_andOtherLinksTriggerCallbacks() {
        val openedUrls = mutableListOf<String>()
        var settingsOpened = false

        composeTestRule.setContent {
            PolicyBossTheme {
                Slide4Content(
                    isSmsChecked = true,
                    isCallChecked = true,
                    isAgreeChecked = false,
                    onSmsChange = {},
                    onCallChange = {},
                    onAgreeChange = {},
                    onOpenUrl = { openedUrls.add(it) },
                    onOpenSettings = { settingsOpened = true }
                )
            }
        }

        // Inline LinkAnnotation opens the modal dialog rather than an external URL
        composeTestRule
            .onNodeWithText("Click Here", substring = true)
            .assertIsDisplayed()
            .performFirstLinkClick()

        composeTestRule.onNodeWithTag("SyncContactDetailsDialog").assertIsDisplayed()
        composeTestRule.onNodeWithText("SYNC CONTACT DETAILS").assertIsDisplayed()
        composeTestRule.onNodeWithText("#RightInsuranceZarooriHai").performScrollTo().assertIsDisplayed()

        // Dismiss dialog via accessible Close button
        composeTestRule.onNodeWithContentDescription("Close Sync Contact Details").performClick()
        composeTestRule.onNodeWithTag("SyncContactDetailsDialog").assertDoesNotExist()

        composeTestRule
            .onNodeWithText("Manage under App Settings")
            .performScrollTo()
            .performClick()

        composeTestRule
            .onNodeWithText("Terms & Conditions")
            .performScrollTo()
            .performClick()

        composeTestRule
            .onNodeWithText("Privacy Policy")
            .performScrollTo()
            .performClick()

        assertTrue(settingsOpened)
        assertEquals(
            listOf(
                SyncContactUrls.TERMS_AND_CONDITIONS,
                SyncContactUrls.PRIVACY_POLICY
            ),
            openedUrls
        )
    }

    @Test
    fun test_syncContactDetailsDialog_displaysAllSevenPointsAndDismisses() {
        var dismissed = false

        composeTestRule.setContent {
            PolicyBossTheme {
                SyncContactDetailsDialog(
                    onDismissRequest = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("SYNC CONTACT DETAILS").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sync Corner > My Contacts", substring = true)
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("'Sales Material'", substring = true)
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("\"Greentick Symbol\"", substring = true)
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("#RightInsuranceZarooriHai")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("SyncContactDetailsCloseButton").performClick()
        assertTrue(dismissed)
    }
}
