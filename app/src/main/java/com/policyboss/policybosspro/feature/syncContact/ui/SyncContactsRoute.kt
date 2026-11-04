package com.policyboss.policybosspro.feature.syncContact.ui

import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.policyboss.policybosspro.feature.syncContact.model.SyncContactsAction
import com.policyboss.policybosspro.feature.syncContact.model.SyncContactsEvent
import com.policyboss.policybosspro.feature.syncContact.viewmodel.SyncContactsViewModel
import com.policyboss.policybosspro.utils.permission.PermissionRationaleDialog
import com.policyboss.policybosspro.utils.permission.PermissionSettingsDialog
import com.policyboss.policybosspro.utils.permission.na.AppPermissionManager
import com.policyboss.policybosspro.utils.permission.openAppSettings

@Composable
fun SyncContactsRoute(
    onBackClick: () -> Unit,
    onNavigateToProcessing: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SyncContactsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val lifecycleOwner = LocalLifecycleOwner.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showRationaleDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Reusing the existing AppPermissionManager configuration
    val requiredPermissions = remember {
        AppPermissionManager.contactsAndCallLog
    }

    fun hasAllRequiredPermissions(): Boolean {
        return requiredPermissions.all { perm ->
            ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
        }
    }

    // 1. Android Runtime Permission Launcher for Multiple Permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val allGranted = permissionsMap.values.isNotEmpty() && permissionsMap.values.all { it }
        if (allGranted) {
            showRationaleDialog = false
            showSettingsDialog = false
            viewModel.onPermissionGranted()
        } else {
            // Check if any denied permission is permanently denied
            val anyPermanentlyDenied = permissionsMap.entries.any { (perm, isGranted) ->
                !isGranted && activity != null && !ActivityCompat.shouldShowRequestPermissionRationale(activity, perm)
            }

            if (anyPermanentlyDenied) {
                showSettingsDialog = true
            } else {
                showRationaleDialog = true
            }
        }
    }

    // 2. Recheck permissions on screen resume (e.g. after returning from Settings)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (hasAllRequiredPermissions()) {
                    showRationaleDialog = false
                    showSettingsDialog = false
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // 3. Observe ViewModel Events
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is SyncContactsEvent.RequestContactPermission -> {
                    if (hasAllRequiredPermissions()) {
                        viewModel.onPermissionGranted()
                    } else {
                        permissionLauncher.launch(requiredPermissions)
                    }
                }
                is SyncContactsEvent.NavigateNext -> {
                    onNavigateToProcessing()
                }
                is SyncContactsEvent.ShowError -> {
                    // Handle error if needed
                }
            }
        }
    }

    // 4. Safe Back Navigation
    BackHandler {
        if (activity != null && !activity.isFinishing) {
            activity.finish()
        } else {
            onBackClick()
        }
    }

    // 5. Permission Rationale Dialog if denied with rationale
    if (showRationaleDialog) {
        PermissionRationaleDialog(
            title = "Permissions Required",
            description = "To calculate your earning potential, sync your network, and earn rewards, please grant Contact and Call Log permissions.",
            onDismiss = { showRationaleDialog = false }
        )
    }

    // 6. Permission Settings Dialog if permanently denied
    if (showSettingsDialog) {
        PermissionSettingsDialog(
            title = "Permissions Denied",
            message = "Contact and Call Log permissions were permanently denied. Please enable them in App Settings to proceed with synchronization.",
            onConfirm = {
                showSettingsDialog = false
                openAppSettings(context)
            },
            onDismiss = {
                showSettingsDialog = false
            }
        )
    }

    // 7. Render UI Screen
    SyncContactsScreen(
        uiState = uiState,
        onSyncClicked = {
            viewModel.onAction(SyncContactsAction.OnSyncContactsClick)
        },
        modifier = modifier
    )
}
