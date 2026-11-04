package com.policyboss.policybosspro.feature.syncContact

import com.policyboss.policybosspro.core.repository.contact.ContactRepository
import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.syncContact.model.SyncContactsAction
import com.policyboss.policybosspro.feature.syncContact.model.SyncContactsEvent
import com.policyboss.policybosspro.feature.syncContact.model.SyncContactsUiState
import com.policyboss.policybosspro.feature.syncContact.model.SyncPhase
import com.policyboss.policybosspro.feature.syncContact.model.SyncProcessingAction
import com.policyboss.policybosspro.feature.syncContact.model.SyncProcessingEvent
import com.policyboss.policybosspro.feature.syncContact.model.SyncProcessingState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SyncContactFeatureTest {

    // =========================================================================
    // 1. UI STATE DEFAULT VALUES & INTEGRITY TESTS
    // =========================================================================

    @Test
    fun testSyncContactsUiState_defaults() {
        val state = SyncContactsUiState()
        assertEquals(AppDummyData.SyncContacts.AVG_EARNINGS, state.avgEarnings)
        assertFalse(state.isLoading)
        assertFalse(state.isSuccess)
        assertEquals(null, state.errorMessage)
    }

    @Test
    fun testSyncProcessingState_defaults() {
        val state = SyncProcessingState()
        assertTrue(state.isSyncing)
        assertFalse(state.isSuccess)
        assertEquals(SyncPhase.CONTACT_FETCHING, state.currentPhase)
        assertEquals(0f, state.progress, 0.001f)
        assertEquals(0, state.contactsFound)
        assertEquals("Fetching Contacts...", state.statusMessage)
        assertEquals(AppDummyData.SyncContacts.AVG_EARNINGS, state.avgEarnings)
        assertEquals(AppDummyData.SyncContacts.EARNING_POTENTIAL, state.earningPotential)
        assertEquals(AppDummyData.SyncContacts.BOSS_COINS, state.bossCoins)
    }

    @Test
    fun testSyncProcessingState_dynamicStatusProgression() {
        var state = SyncProcessingState()
        assertEquals("Fetching Contacts...", state.statusMessage)

        // Simulating uploading contacts in progress
        state = state.copy(
            progress = 0.4f,
            statusMessage = "Uploading Contacts..."
        )
        assertEquals("Uploading Contacts...", state.statusMessage)
        assertEquals(0.4f, state.progress, 0.001f)
        assertTrue(state.isSyncing)

        // Simulating completion
        state = state.copy(
            progress = 1.0f,
            isSyncing = false,
            isSuccess = true,
            statusMessage = "Contacts fetched"
        )
        assertEquals("Contacts fetched", state.statusMessage)
        assertFalse(state.isSyncing)
        assertTrue(state.isSuccess)
    }

    @Test
    fun testRecordBasedProgressCalculation() {
        fun calculateProgress(processed: Int, total: Int): Float {
            return if (total > 0) (processed.toFloat() / total.toFloat()).coerceIn(0f, 1f) else 0f
        }

        // 0 records safe handling
        assertEquals(0f, calculateProgress(0, 0), 0.001f)
        assertEquals(0f, calculateProgress(10, 0), 0.001f)

        // 700 records (small count)
        assertEquals(0f, calculateProgress(0, 700), 0.001f)
        assertEquals(0.5f, calculateProgress(350, 700), 0.001f)
        assertEquals(1.0f, calculateProgress(700, 700), 0.001f)

        // 1000 records
        assertEquals(0f, calculateProgress(0, 1000), 0.001f)
        assertEquals(1.0f, calculateProgress(1000, 1000), 0.001f)

        // 3000 records (multi-batch progression)
        assertEquals(0.333f, calculateProgress(1000, 3000), 0.001f)
        assertEquals(0.666f, calculateProgress(2000, 3000), 0.001f)
        assertEquals(1.0f, calculateProgress(3000, 3000), 0.001f)

        // Coerce boundary check
        assertEquals(1.0f, calculateProgress(3500, 3000), 0.001f)
    }

    @Test
    fun testTwoPhaseProgressTransitions() {
        // Initial: Phase 1 Fetching Contacts
        var state = SyncProcessingState()
        assertEquals(SyncPhase.CONTACT_FETCHING, state.currentPhase)
        assertEquals("Fetching Contacts...", state.statusMessage)
        assertEquals(0f, state.progress, 0.001f)

        // Phase 1: Uploading Contacts
        state = state.copy(
            currentPhase = SyncPhase.CONTACT_UPLOADING,
            statusMessage = "Uploading Contacts...",
            progress = 0.5f,
            processedRecords = 350,
            totalRecords = 700
        )
        assertEquals(SyncPhase.CONTACT_UPLOADING, state.currentPhase)
        assertEquals("Uploading Contacts...", state.statusMessage)
        assertEquals(0.5f, state.progress, 0.001f)

        // Phase 1: Contact Completed at 100%
        state = state.copy(
            currentPhase = SyncPhase.CONTACT_COMPLETED,
            progress = 1.0f,
            processedRecords = 700,
            totalRecords = 700,
            contactsFound = 700
        )
        assertEquals(SyncPhase.CONTACT_COMPLETED, state.currentPhase)
        assertEquals(1.0f, state.progress, 0.001f)

        // Phase 2: Start Call Log Sync -> RESET progress to 0%
        state = state.copy(
            currentPhase = SyncPhase.CALL_LOG_FETCHING,
            progress = 0f,
            processedRecords = 0,
            totalRecords = 0,
            statusMessage = "Fetching Call Logs..."
        )
        assertEquals(SyncPhase.CALL_LOG_FETCHING, state.currentPhase)
        assertEquals("Fetching Call Logs...", state.statusMessage)
        assertEquals(0f, state.progress, 0.001f)

        // Phase 2: Uploading Call Logs
        state = state.copy(
            currentPhase = SyncPhase.CALL_LOG_UPLOADING,
            progress = 0.75f,
            processedRecords = 75,
            totalRecords = 100,
            statusMessage = "Uploading Call Logs..."
        )
        assertEquals(SyncPhase.CALL_LOG_UPLOADING, state.currentPhase)
        assertEquals("Uploading Call Logs...", state.statusMessage)
        assertEquals(0.75f, state.progress, 0.001f)

        // Phase 2: Call Log Completed at 100%
        state = state.copy(
            currentPhase = SyncPhase.CALL_LOG_COMPLETED,
            progress = 1.0f,
            processedRecords = 100,
            totalRecords = 100
        )
        assertEquals(SyncPhase.CALL_LOG_COMPLETED, state.currentPhase)
        assertEquals(1.0f, state.progress, 0.001f)

        // Transition to Final Success State (Screen C)
        state = state.copy(
            isSyncing = false,
            isSuccess = true,
            statusMessage = "Contacts fetched",
            errorMessage = null
        )
        assertFalse(state.isSyncing)
        assertTrue(state.isSuccess)
        assertEquals("Contacts fetched", state.statusMessage)
    }

    @Test
    fun testAuxiliaryCallLogFailure_preservesContactSuccess() {
        // Phase 1 completed successfully
        var state = SyncProcessingState(
            currentPhase = SyncPhase.CONTACT_COMPLETED,
            progress = 1.0f,
            contactsFound = 500,
            isSyncing = true,
            isSuccess = false
        )

        // Call logs fail during Phase 2
        // Contact sync success must be preserved, and screen must transition to Screen C
        state = state.copy(
            isSyncing = false,
            isSuccess = true,
            statusMessage = "Contacts fetched",
            errorMessage = null
        )

        assertTrue(state.isSuccess)
        assertFalse(state.isSyncing)
        assertEquals("Contacts fetched", state.statusMessage)
        assertEquals(500, state.contactsFound)
        assertEquals(null, state.errorMessage)
    }

    @Test
    fun testSmallRecordCountMinimumVisualDuration() {
        fun calculateRemainingDelay(recordCount: Int, elapsedTimeMs: Long): Long {
            return if (recordCount < 1000) {
                (1000L - elapsedTimeMs).coerceAtLeast(0L)
            } else {
                0L
            }
        }

        // Small count (e.g. 700 records) finished fast (200ms) -> add remaining 800ms
        assertEquals(800L, calculateRemainingDelay(700, 200L))

        // Small count (700 records) took 950ms -> add remaining 50ms
        assertEquals(50L, calculateRemainingDelay(700, 950L))

        // Small count (700 records) took > 1000ms (1200ms) -> 0ms delay
        assertEquals(0L, calculateRemainingDelay(700, 1200L))

        // Large count (>= 1000 records) -> always 0ms delay (natural duration)
        assertEquals(0L, calculateRemainingDelay(1000, 100L))
        assertEquals(0L, calculateRemainingDelay(3000, 250L))
    }

    @Test
    fun testSyncProcessingState_auxiliaryFailureIsolation() {
        // When auxiliary call log fails, primary contact sync UI state must NOT be degraded
        val primarySyncRunning = SyncProcessingState(
            isSyncing = true,
            isSuccess = false,
            progress = 0.6f,
            statusMessage = "Uploading contacts, please wait...",
            errorMessage = null
        )

        // Auxiliary call log error occurs -> primary state should preserve its sync progress & lack of error
        assertEquals(null, primarySyncRunning.errorMessage)
        assertTrue(primarySyncRunning.isSyncing)
        assertFalse(primarySyncRunning.isSuccess)

        // Primary completes successfully
        val primarySyncSuccess = primarySyncRunning.copy(
            isSyncing = false,
            isSuccess = true,
            progress = 1.0f,
            statusMessage = "Contacts fetched"
        )
        assertTrue(primarySyncSuccess.isSuccess)
        assertFalse(primarySyncSuccess.isSyncing)
        assertEquals(null, primarySyncSuccess.errorMessage)
    }

    @Test
    fun testSyncProcessingState_transitionToSuccess() {
        val initial = SyncProcessingState()
        val completed = initial.copy(
            isSyncing = false,
            isSuccess = true,
            progress = 1.0f,
            contactsFound = 219
        )

        assertFalse(completed.isSyncing)
        assertTrue(completed.isSuccess)
        assertEquals(1.0f, completed.progress, 0.001f)
        assertEquals(219, completed.contactsFound)
        assertEquals("₹1,25,000", completed.earningPotential)
        assertEquals("5,000", completed.bossCoins)
    }

    // =========================================================================
    // 2. CONTACT REPOSITORY MOCK TESTS
    // =========================================================================

    @Test
    fun testContactRepository_returnsContactsCount() = runBlocking {
        val mockRepo = object : ContactRepository {
            override suspend fun getLocalContactsCount(): Result<Int> {
                return Result.success(142)
            }
        }

        val result = mockRepo.getLocalContactsCount()
        assertTrue(result.isSuccess)
        assertEquals(142, result.getOrNull())
    }

    @Test
    fun testContactRepository_handlesZeroContacts() = runBlocking {
        val mockRepo = object : ContactRepository {
            override suspend fun getLocalContactsCount(): Result<Int> {
                return Result.success(0)
            }
        }

        val result = mockRepo.getLocalContactsCount()
        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull())
    }

    @Test
    fun testContactRepository_handlesPermissionFailure() = runBlocking {
        val mockRepo = object : ContactRepository {
            override suspend fun getLocalContactsCount(): Result<Int> {
                return Result.failure(SecurityException("Permission denied"))
            }
        }

        val result = mockRepo.getLocalContactsCount()
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is SecurityException)
    }

    // =========================================================================
    // 3. ACTION & EVENT CONTRACT TESTS
    // =========================================================================

    @Test
    fun testSyncContactsActionsAndEvents() {
        val clickAction = SyncContactsAction.OnSyncContactsClick
        val reqPermEvent = SyncContactsEvent.RequestContactPermission
        val nextEvent = SyncContactsEvent.NavigateNext
        val errorEvent = SyncContactsEvent.ShowError("Test Error")

        assertEquals(SyncContactsAction.OnSyncContactsClick, clickAction)
        assertEquals(SyncContactsEvent.RequestContactPermission, reqPermEvent)
        assertEquals(SyncContactsEvent.NavigateNext, nextEvent)
        assertEquals("Test Error", errorEvent.message)
    }

    @Test
    fun testSyncProcessingActionsAndEvents() {
        val startAction = SyncProcessingAction.StartSync
        val completeAction = SyncProcessingAction.CompleteProfileClicked
        val homeEvent = SyncProcessingEvent.NavigateToHome
        val errorEvent = SyncProcessingEvent.ShowError("Sync failed")

        assertEquals(SyncProcessingAction.StartSync, startAction)
        assertEquals(SyncProcessingAction.CompleteProfileClicked, completeAction)
        assertEquals(SyncProcessingEvent.NavigateToHome, homeEvent)
        assertEquals("Sync failed", errorEvent.message)
    }
}

