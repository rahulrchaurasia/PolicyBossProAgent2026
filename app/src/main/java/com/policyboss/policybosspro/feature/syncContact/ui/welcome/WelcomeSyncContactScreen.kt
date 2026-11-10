package com.policyboss.policybosspro.feature.syncContact.ui.welcome

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.core.constant.SyncContactUrls
import com.policyboss.policybosspro.feature.syncContact.viewmodel.WelcomeSyncContactEvent
import com.policyboss.policybosspro.feature.syncContact.viewmodel.WelcomeSyncContactUiState
import com.policyboss.policybosspro.feature.syncContact.viewmodel.WelcomeSyncContactViewModel
import com.policyboss.policybosspro.ui.ObserveAsEvents
import com.policyboss.policybosspro.ui.components.button.PrimaryCTAButton
import com.policyboss.policybosspro.ui.components.loading.CustomAppLoader
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.PolicyBossTheme
import com.policyboss.policybosspro.utils.extension.openAppSettings
import kotlinx.coroutines.launch

@Composable
fun WelcomeSyncContactRoute(
    onNavigateToMainSyncContact: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WelcomeSyncContactViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? android.app.Activity
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Safe back navigation: exits the application instead of popping back to OTP screen
    BackHandler {
        if (activity != null && !activity.isFinishing) {
            activity.finish()
        } else {
            onBackClick()
        }
    }

    ObserveAsEvents(flow = viewModel.uiEvent) { event ->
        when (event) {
            is WelcomeSyncContactEvent.NavigateToMain -> {
                onNavigateToMainSyncContact()
            }
            is WelcomeSyncContactEvent.ShowMessage -> {
                // Surface error/message
            }
            is WelcomeSyncContactEvent.RequestPermissions -> {}
        }
    }

    WelcomeSyncContactScreen(
        uiState = uiState,
        onCheckboxToggled = viewModel::onCheckboxToggled,
        onGetStartedClick = { sms, call ->
            viewModel.saveAgreements(sms, call)
        },
        onOpenUrl = { url ->
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } catch (_: Exception) {}
        },
        onOpenSettings = {
            context.openAppSettings()
        },
        modifier = modifier
    )
}

@Composable
fun WelcomeSyncContactScreen(
    uiState: WelcomeSyncContactUiState,
    onCheckboxToggled: (Boolean, Boolean, Boolean) -> Unit,
    onGetStartedClick: (Boolean, Boolean) -> Unit,
    onOpenUrl: (String) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    val isLastPage = pagerState.currentPage == 3
    val backgroundColor = if (isLastPage) AppColors.FooterBlue else Color.White

    var isSmsChecked by remember(uiState.isSmsChecked) { mutableStateOf(uiState.isSmsChecked) }
    var isCallChecked by remember(uiState.isCallChecked) { mutableStateOf(uiState.isCallChecked) }
    var isAgreeChecked by remember(uiState.isAgreeChecked) { mutableStateOf(uiState.isAgreeChecked) }

    // Intercept back gesture on slides 2, 3, 4 to smoothly scroll to previous slide
    BackHandler(enabled = pagerState.currentPage > 0) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp), // Space for footer
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                when (page) {
                    0 -> Slide1Content()
                    1 -> Slide2Content()
                    2 -> Slide3Content()
                    3 -> Slide4Content(
                        isSmsChecked = isSmsChecked,
                        isCallChecked = isCallChecked,
                        isAgreeChecked = isAgreeChecked,
                        onSmsChange = {
                            isSmsChecked = it
                            onCheckboxToggled(it, isCallChecked, isAgreeChecked)
                        },
                        onCallChange = {
                            isCallChecked = it
                            onCheckboxToggled(isSmsChecked, it, isAgreeChecked)
                        },
                        onAgreeChange = {
                            isAgreeChecked = it
                            onCheckboxToggled(isSmsChecked, isCallChecked, it)
                        },
                        onOpenUrl = onOpenUrl,
                        onOpenSettings = onOpenSettings
                    )
                }
            }
        }

        // Footer Section (Next / Get Started button + Dots)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = backgroundColor
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PrimaryCTAButton(
                    text = if (isLastPage) "GET STARTED" else "NEXT",
                    enabled = !isLastPage || isAgreeChecked,
                    onClick = {
                        if (isLastPage) {
                            onGetStartedClick(isSmsChecked, isCallChecked)
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    // Fills the width constrained by the parent Column's 24.dp padding
                    modifier = Modifier.fillMaxWidth()
                )

                if (!isLastPage) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(3) { index ->
                            val isActive = when (index) {
                                0 -> pagerState.currentPage == 0
                                1 -> pagerState.currentPage == 1
                                else -> pagerState.currentPage >= 2
                            }
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(if (isActive) 10.dp else 8.dp)
                                    .clip(CircleShape)
                                    .background(if (isActive) AppColors.Primary else Color.LightGray)
                            )
                        }
                    }
                }
            }
        }

        if (uiState.isLoading || uiState.isSaving) {
            CustomAppLoader(showBackground = true)
        }
    }
}

@Composable
private fun Slide1Content() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "WHAT IS SYNC CONTACTS?",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            painter = painterResource(id = R.drawable.slide_2),
            contentDescription = null,
            modifier = Modifier.height(260.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Functionality developed to enhance business productivity by helping monetize PolicyBoss Pro user's phone contacts.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Using this exclusive feature, you get important motor insurance renewal related information and updates of own contacts.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Slide2Content() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "WHY SYNC CONTACTS?",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            painter = painterResource(id = R.drawable.slide_1),
            contentDescription = null,
            modifier = Modifier.height(260.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Your phone book contacts are essentially the office directory for you as an insurance agent.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Once you opt in for Sync Contacts, we will enrich all matches found from our end with vehicle and insurance details.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Slide3Content() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.insurance),
            contentDescription = null,
            modifier = Modifier.height(260.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Remember, upon sync, any new numbers will be fetched from your call logs and matches found will get shown in your lead dashboard.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Please note that your data will not be shared with any third party and is completely safe. Refer to our Privacy Policy and Terms & Conditions for further details.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Slide4Content(
    isSmsChecked: Boolean,
    isCallChecked: Boolean,
    isAgreeChecked: Boolean,
    onSmsChange: (Boolean) -> Unit,
    onCallChange: (Boolean) -> Unit,
    onAgreeChange: (Boolean) -> Unit,
    onOpenUrl: (String) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDetailsDialog by rememberSaveable { mutableStateOf(false) }

    val howItWorksText = remember {
        buildAnnotatedString {
            append("To understand how it works & more details, ")
            withLink(
                LinkAnnotation.Clickable(
                    tag = "SYNC_CONTACT_DETAILS",
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = AppColors.Primary,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        )
                    ),
                    linkInteractionListener = {
                        showDetailsDialog = true
                    }
                )
            ) {
                append("Click Here")
            }
        }
    }

    if (showDetailsDialog) {
        SyncContactDetailsDialog(
            onDismissRequest = { showDetailsDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Header Section ---
        Text(
            text = "SYNC CONTACTS",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "UNDERSTANDING WHAT, HOW & WHY",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 2.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Image(
            painter = painterResource(id = R.drawable.synccontactlogo),
            contentDescription = "Sync Contacts Illustration",
            modifier = Modifier.height(100.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(14.dp))

        // --- Core Value Proposition Card ---
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, AppColors.SegmentedBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Exclusive feature only for PolicyBoss Pro users",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Now, get important insurance-related information & updates for your phonebook contacts, pre-filled to enrich your app experience.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = howItWorksText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- Privacy & Settings Information ---
        Text(
            text = "Rest assured, no synced data will be shared with any third-party and is completely safe.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 17.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "If you wish to stop or discontinue sync of new contacts, toggle it off under App Settings.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 17.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Compact "Manage under App Settings" action link (avoids TextButton's 48.dp phantom gap)
        CompactActionLink(
            text = "Manage under App Settings",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            onClick = onOpenSettings
        )

        // Compact Footer Links (Terms & Privacy)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompactActionLink(
                text = "Terms & Conditions",
                style = MaterialTheme.typography.labelMedium,
                onClick = { onOpenUrl(SyncContactUrls.TERMS_AND_CONDITIONS) }
            )
            Text(
                text = "|",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.padding(horizontal = 2.dp)
            )
            CompactActionLink(
                text = "Privacy Policy",
                style = MaterialTheme.typography.labelMedium,
                onClick = { onOpenUrl(SyncContactUrls.PRIVACY_POLICY) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Preferences & Consent Section ---
        Text(
            text = "Communication Preferences",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        CheckboxRow(
            checked = isSmsChecked,
            onCheckedChange = onSmsChange,
            text = "I would like to avail communication support (SMS/Email/WhatsApp/Other modes) for my Sync Contact Leads."
        )

        CheckboxRow(
            checked = isCallChecked,
            onCheckedChange = onCallChange,
            text = "I would like to avail tele-calling support for my Sync Contact Leads."
        )

        CheckboxRow(
            checked = isAgreeChecked,
            onCheckedChange = onAgreeChange,
            text = "I hereby agree to the terms & conditions for using Sync Contacts.",
            isBold = true
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * Compact clickable text action preserving [Role.Button] accessibility semantics
 * without TextButton's 48.dp interactive component height and excessive content padding.
 */
@Composable
private fun CompactActionLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.labelMedium
) {
    Text(
        text = text,
        style = style,
        color = AppColors.Primary,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(
                role = Role.Button,
                onClickLabel = text,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
    )
}

/**
 * Reusable accessible Checkbox row with full-row toggle support and vertically centered text.
 */
@Composable
private fun CheckboxRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    isBold: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Checkbox
            )
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null, // Handled by row Modifier.toggleable for unified touch target & a11y
            colors = CheckboxDefaults.colors(
                checkedColor = AppColors.Primary,
                uncheckedColor = AppColors.Slate400
            )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.TextPrimary,
            fontWeight = if (isBold) FontWeight.SemiBold else FontWeight.Normal,
            lineHeight = 17.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

// ==========================================
// PREVIEWS
// ==========================================

@Preview(name = "Slide 1: What is Sync Contacts", showBackground = true, heightDp = 800)
@Composable
private fun WelcomeSyncSlide1Preview() {
    PolicyBossTheme {
        Surface {
            Slide1Content()
        }
    }
}

@Preview(name = "Slide 2: Why Sync Contacts", showBackground = true, heightDp = 800)
@Composable
private fun WelcomeSyncSlide2Preview() {
    PolicyBossTheme {
        Surface {
            Slide2Content()
        }
    }
}

@Preview(name = "Slide 3: Privacy & Data Info", showBackground = true, heightDp = 800)
@Composable
private fun WelcomeSyncSlide3Preview() {
    PolicyBossTheme {
        Surface {
            Slide3Content()
        }
    }
}

@Preview(name = "Slide 4: Agreement & Checkboxes (Scrollable)", showBackground = true, heightDp = 1000)
@Composable
private fun WelcomeSyncSlide4Preview() {
    PolicyBossTheme {
        Surface(color = AppColors.FooterBlue) {
            Slide4Content(
                isSmsChecked = true,
                isCallChecked = false,
                isAgreeChecked = true,
                onSmsChange = {},
                onCallChange = {},
                onAgreeChange = {},
                onOpenUrl = {},
                onOpenSettings = {}
            )
        }
    }
}

@Preview(name = "Full Screen: Slide 1 (Initial Idle State)", showBackground = true, heightDp = 840)
@Composable
private fun WelcomeSyncScreenFullInitialPreview() {
    PolicyBossTheme {
        WelcomeSyncContactScreen(
            uiState = WelcomeSyncContactUiState(
                isSmsChecked = true,
                isCallChecked = true,
                isAgreeChecked = false,
                isLoading = false,
                isSaving = false
            ),
            onCheckboxToggled = { _, _, _ -> },
            onGetStartedClick = { _, _ -> },
            onOpenUrl = {},
            onOpenSettings = {}
        )
    }
}

@Preview(name = "Full Screen: Loading / Saving State", showBackground = true, heightDp = 840)
@Composable
private fun WelcomeSyncScreenLoadingPreview() {
    PolicyBossTheme {
        WelcomeSyncContactScreen(
            uiState = WelcomeSyncContactUiState(
                isSmsChecked = true,
                isCallChecked = true,
                isAgreeChecked = true,
                isLoading = true,
                isSaving = false
            ),
            onCheckboxToggled = { _, _, _ -> },
            onGetStartedClick = { _, _ -> },
            onOpenUrl = {},
            onOpenSettings = {}
        )
    }
}