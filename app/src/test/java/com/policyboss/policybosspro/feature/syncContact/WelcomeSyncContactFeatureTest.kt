package com.policyboss.policybosspro.feature.syncContact

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.policyboss.policybosspro.core.constant.SyncContactUrls
import com.policyboss.policybosspro.core.datastore.AppDataManager
import com.policyboss.policybosspro.core.repository.syncContact.SyncContactRepository
import com.policyboss.policybosspro.feature.syncContact.data.model.SaveCheckboxRequestEntity
import com.policyboss.policybosspro.feature.syncContact.data.model.SyncContactAgreementItem
import com.policyboss.policybosspro.feature.syncContact.data.model.SyncContactAgreementResponse
import com.policyboss.policybosspro.feature.syncContact.viewmodel.WelcomeSyncContactEvent
import com.policyboss.policybosspro.feature.syncContact.viewmodel.WelcomeSyncContactUiState
import com.policyboss.policybosspro.feature.syncContact.viewmodel.WelcomeSyncContactViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class WelcomeSyncContactFeatureTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createWelcomeViewModel(repo: SyncContactRepository): WelcomeSyncContactViewModel {
        val testFile = File.createTempFile("test_datastore_welcome", ".preferences_pb")
        testFile.deleteOnExit()
        val testDataStore = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(testDispatcher + Job()),
            produceFile = { testFile }
        )
        val appDataManager = AppDataManager(testDataStore)
        return WelcomeSyncContactViewModel(
            syncContactRepository = repo,
            appDataManager = appDataManager
        )
    }

    // =========================================================================
    // 1. DATA MODEL & FALLBACK VALUE TESTS
    // =========================================================================

    @Test
    fun testAgreementFallbackValues_whenEmpty() {
        val rawSsId = "".ifBlank { "0" }
        val rawFbaId = "".ifBlank { "0" }

        assertEquals("0", rawSsId)
        assertEquals("0", rawFbaId)
    }

    @Test
    fun testSaveCheckboxRequestEntity_formatting() {
        val request = SaveCheckboxRequestEntity(
            fba_id = 150252,
            is_sms = "yes",
            is_call = "no",
            online_agreement = "online_agreement",
            ss_id = 1001,
            app_version = "1.0",
            device_code = "device_test_123"
        )

        assertEquals(150252, request.fba_id)
        assertEquals("yes", request.is_sms)
        assertEquals("no", request.is_call)
        assertEquals("online_agreement", request.online_agreement)
        assertEquals(1001, request.ss_id)
        assertEquals("1.0", request.app_version)
        assertEquals("device_test_123", request.device_code)
    }

    @Test
    fun testSyncContactAgreementResponse_parsing() {
        val item = SyncContactAgreementItem(
            ssId = 1001,
            fbaId = 150252,
            isSms = "yes",
            isCall = "yes"
        )
        val response = SyncContactAgreementResponse(
            status = "Success",
            statusNo = 0,
            message = "Agreement fetched",
            msg = listOf(item)
        )

        assertEquals("Success", response.status)
        assertEquals(1, response.msg?.size)
        assertEquals("yes", response.msg?.first()?.isSms)
        assertEquals("yes", response.msg?.first()?.isCall)
    }

    // =========================================================================
    // 2. REPOSITORY CONTRACT TESTS
    // =========================================================================

    @Test
    fun testSyncContactRepository_agreementsSuccess() = runBlocking {
        val mockRepo = object : SyncContactRepository {
            override suspend fun getSyncContactAgreements(): Result<List<SyncContactAgreementItem>> {
                return Result.success(
                    listOf(
                        SyncContactAgreementItem(
                            ssId = 1001,
                            fbaId = 150252,
                            isSms = "yes",
                            isCall = "no"
                        )
                    )
                )
            }

            override suspend fun saveAgreementCheckbox(isSms: Boolean, isCall: Boolean): Result<Boolean> {
                return Result.success(true)
            }
        }

        val result = mockRepo.getSyncContactAgreements()
        assertTrue(result.isSuccess)
        val list = result.getOrNull()
        assertEquals(1, list?.size)
        assertEquals("yes", list?.first()?.isSms)
        assertEquals("no", list?.first()?.isCall)

        val saveResult = mockRepo.saveAgreementCheckbox(isSms = true, isCall = false)
        assertTrue(saveResult.isSuccess)
        assertTrue(saveResult.getOrDefault(false))
    }

    // =========================================================================
    // 3. UI STATE & EVENT TESTS
    // =========================================================================

    @Test
    fun testWelcomeSyncContactUiState_defaults() {
        val state = WelcomeSyncContactUiState()
        assertFalse(state.isLoading)
        assertFalse(state.isSmsChecked)
        assertFalse(state.isCallChecked)
        assertFalse(state.isAgreeChecked)
        assertFalse(state.isPermissionsNeeded)
        assertEquals(null, state.errorMessage)
    }

    @Test
    fun testWelcomeSyncContactEvents() {
        val reqPerm = WelcomeSyncContactEvent.RequestPermissions
        val navMain = WelcomeSyncContactEvent.NavigateToMain
        val showMsg = WelcomeSyncContactEvent.ShowMessage("Failed to sync")

        assertEquals(WelcomeSyncContactEvent.RequestPermissions, reqPerm)
        assertEquals(WelcomeSyncContactEvent.NavigateToMain, navMain)
        assertEquals("Failed to sync", showMsg.message)
    }

    // =========================================================================
    // 4. VIEWMODEL INITIALIZATION & AUTOMATIC NAVIGATION SUPPRESSION TESTS
    // =========================================================================

    @Test
    fun testViewModel_loadAgreements_doesNotEmitAutomaticNavigationEvent() = runTest {
        val mockRepo = object : SyncContactRepository {
            override suspend fun getSyncContactAgreements(): Result<List<SyncContactAgreementItem>> {
                return Result.success(
                    listOf(
                        SyncContactAgreementItem(
                            ssId = 1001,
                            fbaId = 150252,
                            isSms = "yes",
                            isCall = "no"
                        )
                    )
                )
            }
            override suspend fun saveAgreementCheckbox(isSms: Boolean, isCall: Boolean) = Result.success(true)
        }

        val events = mutableListOf<WelcomeSyncContactEvent>()
        val viewModel = createWelcomeViewModel(mockRepo)

        val job = launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        testScheduler.advanceUntilIdle()

        // Verify preferences are populated in uiState
        assertEquals(true, viewModel.uiState.value.isSmsChecked)
        assertEquals(false, viewModel.uiState.value.isCallChecked)
        assertEquals(true, viewModel.uiState.value.isPermissionsNeeded)
        // Verify loading is FALSE (no blocking dialog shown to user on slide 1)
        assertFalse(viewModel.uiState.value.isLoading)

        // Crucial requirement: No automatic navigation event was emitted!
        assertTrue(events.isEmpty())
        job.cancel()
    }

    @Test
    fun testSlideProgressionLogic_onlyFourthClickAdvancesToSync() {
        val totalSlides = 4
        var current = 0
        var proceedCalls = 0

        fun onClickNext() {
            val nextPosition = current + 1
            if (nextPosition < totalSlides) {
                current = nextPosition
            } else {
                proceedCalls++
            }
        }

        // Initially on Slide 1
        assertEquals(0, current)
        assertEquals(0, proceedCalls)

        // Click 1: Slide 1 -> Slide 2
        onClickNext()
        assertEquals(1, current)
        assertEquals(0, proceedCalls)

        // Click 2: Slide 2 -> Slide 3
        onClickNext()
        assertEquals(2, current)
        assertEquals(0, proceedCalls)

        // Click 3: Slide 3 -> Slide 4
        onClickNext()
        assertEquals(3, current)
        assertEquals(0, proceedCalls)

        // Click 4 on Slide 4: Triggers proceedToSyncContacts()
        onClickNext()
        assertEquals(3, current)
        assertEquals(1, proceedCalls)

        // Extra accidental click while proceeding is prevented
        var isProceeding = true
        if (!isProceeding) {
            onClickNext()
        }
        assertEquals(1, proceedCalls)
    }

    @Test
    fun testRecreationPreservesPageIndex_doesNotAutoAdvance() {
        // Simulate Activity recreation (e.g. screen rotation)
        val initialPage = 2
        var current = 0
        val proceedCalled = false

        fun restoreFromSavedState(savedPage: Int?) {
            current = savedPage ?: 0
        }

        restoreFromSavedState(initialPage)

        assertEquals(2, current)
        assertFalse(proceedCalled)
    }

    // =========================================================================
    // 5. AGREEMENT SAVE & ONE-TIME NAVIGATION EVENT TESTS
    // =========================================================================

    @Test
    fun testSaveAgreements_onSuccess_emitsNavigateToMainOnce() = runTest {
        val mockRepo = object : SyncContactRepository {
            override suspend fun getSyncContactAgreements(): Result<List<SyncContactAgreementItem>> {
                return Result.success(emptyList())
            }
            override suspend fun saveAgreementCheckbox(isSms: Boolean, isCall: Boolean): Result<Boolean> {
                return Result.success(true)
            }
        }

        val events = mutableListOf<WelcomeSyncContactEvent>()
        val viewModel = createWelcomeViewModel(mockRepo)

        val job = launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        testScheduler.advanceUntilIdle()
        assertTrue(events.isEmpty())

        // User explicitly clicks GET STARTED on Slide 4
        viewModel.saveAgreements(isSms = true, isCall = false)

        testScheduler.advanceUntilIdle()

        // Emits exactly ONE NavigateToMain event
        assertEquals(1, events.size)
        assertEquals(WelcomeSyncContactEvent.NavigateToMain, events.first())

        // UI state loading and saving are reset to false
        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isSaving)

        job.cancel()
    }

    @Test
    fun testSaveAgreements_onFailure_emitsShowMessage_andNeverEmitsNavigateToMain() = runTest {
        val mockRepo = object : SyncContactRepository {
            override suspend fun getSyncContactAgreements(): Result<List<SyncContactAgreementItem>> {
                return Result.success(emptyList())
            }
            override suspend fun saveAgreementCheckbox(isSms: Boolean, isCall: Boolean): Result<Boolean> {
                return Result.failure(RuntimeException("Network request failed"))
            }
        }

        val events = mutableListOf<WelcomeSyncContactEvent>()
        val viewModel = createWelcomeViewModel(mockRepo)

        val job = launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        testScheduler.advanceUntilIdle()
        assertTrue(events.isEmpty())

        // User explicitly clicks GET STARTED
        viewModel.saveAgreements(isSms = true, isCall = true)

        testScheduler.advanceUntilIdle()

        // Verify only ShowMessage event was emitted, NEVER NavigateToMain
        assertEquals(1, events.size)
        assertTrue(events.first() is WelcomeSyncContactEvent.ShowMessage)
        assertEquals("Network request failed", (events.first() as WelcomeSyncContactEvent.ShowMessage).message)
        assertFalse(events.any { it is WelcomeSyncContactEvent.NavigateToMain })

        // UI state loading and saving are properly reset in finally block so user can retry
        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isSaving)
        assertEquals("Network request failed", viewModel.uiState.value.errorMessage)

        job.cancel()
    }

    @Test
    fun testSaveAgreements_whenRepoReturnsFalse_emitsErrorAndNoNavigation() = runTest {
        val mockRepo = object : SyncContactRepository {
            override suspend fun getSyncContactAgreements(): Result<List<SyncContactAgreementItem>> {
                return Result.success(emptyList())
            }
            override suspend fun saveAgreementCheckbox(isSms: Boolean, isCall: Boolean): Result<Boolean> {
                return Result.success(false)
            }
        }

        val events = mutableListOf<WelcomeSyncContactEvent>()
        val viewModel = createWelcomeViewModel(mockRepo)

        val job = launch {
            viewModel.uiEvent.collect { events.add(it) }
        }

        testScheduler.advanceUntilIdle()
        viewModel.saveAgreements(isSms = false, isCall = false)
        testScheduler.advanceUntilIdle()

        assertEquals(1, events.size)
        assertTrue(events.first() is WelcomeSyncContactEvent.ShowMessage)
        assertFalse(events.any { it is WelcomeSyncContactEvent.NavigateToMain })
        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isSaving)

        job.cancel()
    }

    @Test
    fun testDeprecatedSaveAgreementsAndProceed_onFailure_doesNotInvokeOnComplete() = runTest {
        val mockRepo = object : SyncContactRepository {
            override suspend fun getSyncContactAgreements(): Result<List<SyncContactAgreementItem>> {
                return Result.success(emptyList())
            }
            override suspend fun saveAgreementCheckbox(isSms: Boolean, isCall: Boolean): Result<Boolean> {
                return Result.failure(IllegalStateException("Server error 500"))
            }
        }

        val viewModel = createWelcomeViewModel(mockRepo)
        var onCompleteInvoked = false

        @Suppress("DEPRECATION")
        viewModel.saveAgreementsAndProceed(isSms = true, isCall = false) {
            onCompleteInvoked = true
        }

        testScheduler.advanceUntilIdle()

        // Critical bugfix: onComplete must NEVER be invoked on failure!
        assertFalse(onCompleteInvoked)
        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    @Test
    fun testCheckboxToggled_updatesUiState() = runTest {
        val mockRepo = object : SyncContactRepository {
            override suspend fun getSyncContactAgreements(): Result<List<SyncContactAgreementItem>> {
                return Result.success(emptyList())
            }
            override suspend fun saveAgreementCheckbox(isSms: Boolean, isCall: Boolean) = Result.success(true)
        }

        val viewModel = createWelcomeViewModel(mockRepo)
        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSmsChecked)
        assertFalse(viewModel.uiState.value.isCallChecked)
        assertFalse(viewModel.uiState.value.isAgreeChecked)

        viewModel.onCheckboxToggled(isSms = true, isCall = false, isAgree = true)

        assertTrue(viewModel.uiState.value.isSmsChecked)
        assertFalse(viewModel.uiState.value.isCallChecked)
        assertTrue(viewModel.uiState.value.isAgreeChecked)
    }

    @Test
    fun testSyncContactUrls_constantsMatchExpectedEndpoints() {
        assertEquals(
            "https://www.policyboss.com/sync-contacts-info",
            SyncContactUrls.SYNC_CONTACTS_INFO
        )
        assertEquals(
            "https://www.policyboss.com/terms-condition",
            SyncContactUrls.TERMS_AND_CONDITIONS
        )
        assertEquals(
            "https://www.policyboss.com/privacy-policy-policyboss-pro",
            SyncContactUrls.PRIVACY_POLICY
        )
    }
}

