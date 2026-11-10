package com.policyboss.policybosspro.feature.syncContact.viewmodel



import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.policyboss.policybosspro.core.datastore.AppDataManager
import com.policyboss.policybosspro.core.repository.contact.ContactRepository
import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.syncContact.model.SyncPhase
import com.policyboss.policybosspro.feature.syncContact.model.SyncProcessingAction
import com.policyboss.policybosspro.feature.syncContact.model.SyncProcessingEvent
import com.policyboss.policybosspro.feature.syncContact.model.SyncProcessingState
import com.policyboss.policybosspro.feature.syncContact.worker.CallLogWorkManager
import com.policyboss.policybosspro.feature.syncContact.worker.ContactLogWorkManager
import com.policyboss.policybosspro.utils.AppVersionProvider
import com.policyboss.policybosspro.utils.Constant
import com.policyboss.policybosspro.utils.DeviceIdProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SyncContactsProcessingViewModel @Inject constructor(
    private val workManager: WorkManager,
    private val appDataManager: AppDataManager,
    private val deviceIdProvider: DeviceIdProvider,
    private val appVersionProvider: AppVersionProvider,
    private val contactRepository: ContactRepository
) : ViewModel() {

    companion object {
        private const val TAG = "SyncProcessingVM"
    }

    private val _uiState = MutableStateFlow(SyncProcessingState())
    val uiState: StateFlow<SyncProcessingState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<SyncProcessingEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private var contactSyncJob: Job? = null
    private var callLogSyncJob: Job? = null
    private var contactWorkId: UUID? = null
    private var callLogWorkId: UUID? = null

    init {
        onAction(SyncProcessingAction.StartSync)
    }

    fun onAction(action: SyncProcessingAction) {
        when (action) {
            is SyncProcessingAction.StartSync,
            is SyncProcessingAction.RetrySync -> startSyncProcess()
            is SyncProcessingAction.CompleteProfileClicked -> handleCompleteProfile()
        }
    }

    private fun startSyncProcess() {
        contactSyncJob?.cancel()
        callLogSyncJob?.cancel()

        _uiState.update {
            it.copy(
                isSyncing = true,
                isSuccess = false,
                currentPhase = SyncPhase.CONTACT_FETCHING,
                progress = 0f,
                processedRecords = 0,
                totalRecords = 0,
                statusMessage = "Getting your contacts ready...",
                errorMessage = null
            )
        }

        viewModelScope.launch {
            val localCountResult = contactRepository.getLocalContactsCount()
            val initialDiscoveredCount = localCountResult.getOrDefault(0)
            val fallbackCount = if (initialDiscoveredCount > 0) {
                initialDiscoveredCount
            } else {
                AppDummyData.SyncContacts.DEFAULT_CONTACTS_COUNT
            }

            _uiState.update { it.copy(contactsFound = fallbackCount) }

            val fbaIdStr = appDataManager.getFbaId()
            val fbaIdInt = fbaIdStr.toIntOrNull() ?: 0
            val ssId = appDataManager.getSsId()
            val deviceId = deviceIdProvider.getDeviceId()
            val appVersion = appVersionProvider.getAppVersion()

            val inputData = Data.Builder()
                .putInt(Constant.KEY_fbaid, fbaIdInt)
                .putString(Constant.KEY_parentid, fbaIdStr)
                .putString(Constant.KEY_ssid, ssId)
                .putString(Constant.KEY_deviceid, deviceId)
                .putString(Constant.KEY_appversion, appVersion)
                .build()

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val contactWorkRequest = OneTimeWorkRequestBuilder<ContactLogWorkManager>()
                .setConstraints(constraints)
                .addTag(Constant.TAG_SAVING_CONTACT_LOG)
                .setInputData(inputData)
                .build()

            contactWorkId = contactWorkRequest.id

            // ==========================================
            // PHASE 1: Contacts Sync
            // ==========================================
            val phase1StartTime = System.currentTimeMillis()
            workManager.enqueue(contactWorkRequest)

            contactSyncJob = launch {
                runCatching {
                    workManager.getWorkInfoByIdFlow(contactWorkRequest.id)
                        .collectLatest { workInfo ->
                            if (workInfo != null) {
                                when (workInfo.state) {
                                    WorkInfo.State.ENQUEUED -> {
                                        _uiState.update {
                                            it.copy(
                                                currentPhase = SyncPhase.CONTACT_FETCHING,
                                                statusMessage = "Getting your contacts ready...",
                                                progress = 0f
                                            )
                                        }
                                    }
                                    WorkInfo.State.RUNNING -> {
                                        val processed = workInfo.progress.getInt(Constant.CONTACT_LOG_Progress, 0)
                                        val total = workInfo.progress.getInt(Constant.CONTACT_LOG_MAXProgress, 0)

                                        if (total > 0) {
                                            val calculatedFraction = (processed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
                                            _uiState.update { current ->
                                                current.copy(
                                                    currentPhase = SyncPhase.CONTACT_UPLOADING,
                                                    statusMessage = "Syncing your contacts...",
                                                    progress = maxOf(current.progress, calculatedFraction),
                                                    processedRecords = processed,
                                                    totalRecords = total,
                                                    contactsFound = total
                                                )
                                            }
                                        } else {
                                            _uiState.update { current ->
                                                current.copy(
                                                    currentPhase = SyncPhase.CONTACT_FETCHING,
                                                    statusMessage = "Getting your contacts ready...",
                                                    progress = 0f
                                                )
                                            }
                                        }
                                    }
                                    WorkInfo.State.SUCCEEDED -> {
                                        val countStr = workInfo.outputData.getString(Constant.KEY_result)
                                        val uploadedCount = countStr?.toIntOrNull() ?: _uiState.value.totalRecords

                                        _uiState.update { current ->
                                            current.copy(
                                                currentPhase = SyncPhase.CONTACT_COMPLETED,
                                                contactsFound = if (uploadedCount >= 0) uploadedCount else current.contactsFound,
                                                progress = 1.0f,
                                                statusMessage = "Syncing your contacts...",
                                                errorMessage = null
                                            )
                                        }

                                        val elapsed1 = System.currentTimeMillis() - phase1StartTime
                                        if (uploadedCount < 1000) {
                                            val remainingVisualTime = (1000L - elapsed1).coerceAtLeast(0L) // Fixed arithmetic operator
                                            if (remainingVisualTime > 0) {
                                                delay(remainingVisualTime.milliseconds)
                                            }
                                        }
                                        delay(250.milliseconds)

                                        // Start Phase 2: Call Log Sync
                                        startPhase2CallLogSync(inputData, constraints)
                                    }
                                    WorkInfo.State.FAILED -> {
                                        val errorMsg = workInfo.outputData.getString(Constant.KEY_error_result)
                                            ?: "Contact sync failed. Please check internet connection."
                                        _uiState.update {
                                            it.copy(
                                                isSyncing = false,
                                                isSuccess = false,
                                                errorMessage = errorMsg
                                            )
                                        }
                                        _uiEvent.send(SyncProcessingEvent.ShowError(errorMsg))
                                        Log.e(TAG, "Primary contact sync failed: $errorMsg")
                                    }
                                    WorkInfo.State.CANCELLED -> {
                                        _uiState.update {
                                            it.copy(
                                                isSyncing = false,
                                                errorMessage = "Contact sync was cancelled."
                                            )
                                        }
                                    }
                                    WorkInfo.State.BLOCKED -> {}
                                }
                            }
                        }
                }.onFailure { throwable ->
                    val errorMsg = throwable.localizedMessage ?: "Unexpected error during contact synchronization."
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            isSuccess = false,
                            errorMessage = errorMsg
                        )
                    }
                    _uiEvent.send(SyncProcessingEvent.ShowError(errorMsg))
                }
            }
        }
    }

    private fun startPhase2CallLogSync(inputData: Data, constraints: Constraints) {
        _uiState.update {
            it.copy(
                currentPhase = SyncPhase.CALL_LOG_FETCHING,
                progress = 0f,
                processedRecords = 0,
                totalRecords = 0,
                statusMessage = "Preparing your call log information..."
            )
        }

        val phase2StartTime = System.currentTimeMillis()

        val callLogWorkRequest = OneTimeWorkRequestBuilder<CallLogWorkManager>()
            .setConstraints(constraints)
            .addTag(Constant.TAG_SAVING_CALL_LOG)
            .setInputData(inputData)
            .build()

        callLogWorkId = callLogWorkRequest.id
        workManager.enqueue(callLogWorkRequest)

        callLogSyncJob = viewModelScope.launch {
            runCatching {
                workManager.getWorkInfoByIdFlow(callLogWorkRequest.id)
                    .collectLatest { workInfo ->
                        if (workInfo != null) {
                            when (workInfo.state) {
                                WorkInfo.State.ENQUEUED -> {
                                    _uiState.update {
                                        it.copy(
                                            currentPhase = SyncPhase.CALL_LOG_FETCHING,
                                            statusMessage = "Preparing your call log information...",
                                            progress = 0f
                                        )
                                    }
                                }
                                WorkInfo.State.RUNNING -> {
                                    val processed = workInfo.progress.getInt(Constant.CALL_LOG_Progress, 0)
                                    val total = workInfo.progress.getInt(Constant.CALL_LOG_MAXProgress, 0)

                                    if (total > 0) {
                                        val callLogFraction = (processed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
                                        _uiState.update { current ->
                                            current.copy(
                                                currentPhase = SyncPhase.CALL_LOG_UPLOADING,
                                                statusMessage = "Syncing your call logs...",
                                                progress = maxOf(current.progress, callLogFraction),
                                                processedRecords = processed,
                                                totalRecords = total
                                            )
                                        }
                                    } else {
                                        _uiState.update { current ->
                                            current.copy(
                                                currentPhase = SyncPhase.CALL_LOG_FETCHING,
                                                statusMessage = "Preparing your call log information...",
                                                progress = 0f
                                            )
                                        }
                                    }
                                }
                                WorkInfo.State.SUCCEEDED -> {
                                    val total = _uiState.value.totalRecords
                                    _uiState.update { current ->
                                        current.copy(
                                            currentPhase = SyncPhase.CALL_LOG_COMPLETED,
                                            progress = 1.0f,
                                            statusMessage = "Syncing your call logs..."
                                        )
                                    }

                                    val elapsed2 = System.currentTimeMillis() - phase2StartTime
                                    if (total < 1000) {
                                        val remainingVisualTime = (1000L - elapsed2).coerceAtLeast(0L) // Fixed arithmetic operator
                                        if (remainingVisualTime > 0) {
                                            delay(remainingVisualTime.milliseconds)
                                        }
                                    }
                                    delay(250.milliseconds)

                                    val currentSsId = appDataManager.getSsId()
                                    appDataManager.setContactSyncCompleted(ssId = currentSsId, completed = true)

                                    _uiState.update { current ->
                                        current.copy(
                                            isSyncing = false,
                                            isSuccess = true,
                                            statusMessage = "Contacts fetched",
                                            errorMessage = null
                                        )
                                    }
                                }
                                WorkInfo.State.FAILED,
                                WorkInfo.State.CANCELLED -> {
                                    val currentSsId = appDataManager.getSsId()
                                    appDataManager.setContactSyncCompleted(ssId = currentSsId, completed = true)

                                    _uiState.update { current ->
                                        current.copy(
                                            isSyncing = false,
                                            isSuccess = true,
                                            statusMessage = "Contacts fetched",
                                            errorMessage = null
                                        )
                                    }
                                }
                                WorkInfo.State.BLOCKED -> {}
                            }
                        }
                    }
            }.onFailure {
                val currentSsId = appDataManager.getSsId()
                appDataManager.setContactSyncCompleted(ssId = currentSsId, completed = true)

                _uiState.update { current ->
                    current.copy(
                        isSyncing = false,
                        isSuccess = true,
                        statusMessage = "Contacts fetched",
                        errorMessage = null
                    )
                }
            }
        }
    }

    private fun handleCompleteProfile() {
        viewModelScope.launch {
            val currentSsId = appDataManager.getSsId()
            appDataManager.setContactSyncCompleted(ssId = currentSsId, completed = true)
            _uiEvent.send(SyncProcessingEvent.NavigateToHome)
        }
    }

    override fun onCleared() {
        super.onCleared()
        contactSyncJob?.cancel()
        callLogSyncJob?.cancel()
        if (_uiState.value.isSyncing) {
            contactWorkId?.let { workManager.cancelWorkById(it) }
            callLogWorkId?.let { workManager.cancelWorkById(it) }
        }
    }
}