package com.policyboss.policybosspro.root

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.policyboss.policybosspro.core.datastore.AppDataManager
import com.policyboss.policybosspro.navigation.Dest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
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
class RootViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createAppDataManager(): AppDataManager {
        val testFile = File.createTempFile("test_datastore_root", ".preferences_pb")
        testFile.deleteOnExit()
        val testDataStore = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(testDispatcher + Job()),
            produceFile = { testFile }
        )
        return AppDataManager(testDataStore)
    }

    @Test
    fun testUnauthenticatedLaunch_routesToCustomSplash() = runTest {
        val appDataManager = createAppDataManager()
        val viewModel = RootViewModel(appDataManager)

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse("Loading should be false after session check", state.isLoading)
        assertEquals(Dest.CustomSplash, state.startDestination)
        assertEquals(Dest.Welcome, state.authStartDestination)
    }

    @Test
    fun testAuthenticatedLaunch_whenSyncIncomplete_routesToWelcomeSyncContact() = runTest {
        val appDataManager = createAppDataManager()
        // Simulate authenticated user whose sync was not finished before app kill/close
        appDataManager.saveLoginState(true)
        appDataManager.saveUserSession(ssId = "150252")
        assertFalse(appDataManager.isContactSyncCompleted("150252"))

        val viewModel = RootViewModel(appDataManager)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(Dest.AuthGraph, state.startDestination)
        assertEquals(Dest.WelcomeSyncContact, state.authStartDestination)
    }

    @Test
    fun testAuthenticatedLaunch_whenAppKilledDuringSync_resumesAtWelcomeSyncContact() = runTest {
        val appDataManager = createAppDataManager()
        // Simulate app killed while on SyncContactsScreen or SyncProcessingScreen
        appDataManager.saveLoginState(true)
        appDataManager.saveUserSession(ssId = "150252", name = "Test User")

        val viewModel = RootViewModel(appDataManager)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(
            "User must never reach Home until contact sync is completed",
            Dest.AuthGraph,
            state.startDestination
        )
        assertEquals(
            "Cold-restart must reset step entry to WelcomeSyncContact",
            Dest.WelcomeSyncContact,
            state.authStartDestination
        )
    }

    @Test
    fun testAuthenticatedLaunch_whenSyncCompleted_routesToMainGraph() = runTest {
        val appDataManager = createAppDataManager()
        // Simulate authenticated user with completed sync
        appDataManager.saveLoginState(true)
        appDataManager.saveUserSession(ssId = "150252")
        appDataManager.setContactSyncCompleted("150252", true)
        assertTrue(appDataManager.isContactSyncCompleted("150252"))

        val viewModel = RootViewModel(appDataManager)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(Dest.MainGraph, state.startDestination)
    }

    @Test
    fun testLogoutAndColdRestart_forcesMandatorySyncFlowOnSubsequentLogin() = runTest {
        val appDataManager = createAppDataManager()
        // User logged in and completed sync
        appDataManager.saveLoginState(true)
        appDataManager.saveUserSession(ssId = "150252")
        appDataManager.setContactSyncCompleted("150252", true)

        // User logs out
        appDataManager.clearUserSession()

        // Cold restart 1: unauthenticated
        val vm1 = RootViewModel(appDataManager)
        testScheduler.advanceUntilIdle()
        assertEquals(Dest.CustomSplash, vm1.uiState.value.startDestination)

        // Subsequent login: fresh login
        appDataManager.saveLoginState(true)
        appDataManager.saveUserSession(ssId = "150252")
        assertFalse(appDataManager.isContactSyncCompleted("150252"))

        // Cold restart 2: authenticated but sync incomplete
        val vm2 = RootViewModel(appDataManager)
        testScheduler.advanceUntilIdle()
        assertEquals(Dest.AuthGraph, vm2.uiState.value.startDestination)
        assertEquals(Dest.WelcomeSyncContact, vm2.uiState.value.authStartDestination)
    }
}

