package com.policyboss.policybosspro.feature.syncContact.ui.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.PolicyBossTheme

/**
 * Accessible modal dialog displaying detailed Sync Contacts benefits, workflow guidelines,
 * and PolicyBoss advisory commitment.
 */
@Composable
fun SyncContactDetailsDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val detailBullets = remember { buildSyncContactDetailBullets() }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .widthIn(max = 520.dp)
                .testTag("SyncContactDetailsDialog"),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Sticky Dialog Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppColors.Gray)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "SYNC CONTACT DETAILS",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.4.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 48.dp)
                            .semantics { heading() }
                    )

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .testTag("SyncContactDetailsCloseButton")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .border(1.2.dp, Color.White.copy(alpha = 0.9f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Sync Contact Details",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Scrollable Body + Commitment Footer
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        detailBullets.forEach { bulletText ->
                            SyncDetailBulletItem(text = bulletText)
                        }
                    }

                    HorizontalDivider(
                        thickness = 1.dp,
                        color = AppColors.BorderSecondary
                    )

                    // Commitment Footer
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppColors.Slate50)
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "We are driven by our commitment to ensure no Indian compromises on trusted insurance advisory for the sake of convenience.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppColors.TextSecondaryDark,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "#RightInsuranceZarooriHai",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = AppColors.TextPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SyncDetailBulletItem(
    text: AnnotatedString,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "\u2022",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 18.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            color = AppColors.TextPrimary,
            modifier = Modifier
                .padding(end = 10.dp)
                .clearAndSetSemantics {}
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.TextPrimary,
            lineHeight = 20.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

private fun buildSyncContactDetailBullets(): List<AnnotatedString> = listOf(
    buildAnnotatedString {
        append(
            "Unique way of enhancing the business potential & user experience for all PolicyBoss Pro users, from their own phone book."
        )
    },
    buildAnnotatedString {
        append(
            "Using this exclusive feature, you get important insurance-related information and updates of your own contacts, pre-filled to enrich your journey experience."
        )
    },
    buildAnnotatedString {
        append("Access phone book contacts in app under ")
        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
            append("\"Sync Corner > My Contacts\"")
        }
        append(". Simply choose the contact & initiate your insurance solicitation on the fly.")
    },
    buildAnnotatedString {
        append(
            "Convert your phone book contacts into business leads & contact proactively only around the time of policy renewal."
        )
    },
    buildAnnotatedString {
        append("Option to communicate using any of the promotional creatives made available inside ")
        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
            append("'Sales Material'")
        }
        append(" section of app.")
    },
    buildAnnotatedString {
        append("Remember, existing customers of yours are highlighted with a ")
        withStyle(
            SpanStyle(
                fontWeight = FontWeight.SemiBold,
                color = AppColors.SuccessGreenDark
            )
        ) {
            append("\"Greentick Symbol\"")
        }
        append(" to help you easily identify.")
    },
    buildAnnotatedString {
        append(
            "Option to avail communication and/or tele-calling support to qualify & convert Sync Contact Leads, thereby getting the right business support to maximise your business potential."
        )
    }
)

@Preview(name = "Sync Contact Details Dialog", showBackground = true, heightDp = 800)
@Composable
private fun SyncContactDetailsDialogPreview() {
    PolicyBossTheme {
        SyncContactDetailsDialog(onDismissRequest = {})
    }
}

