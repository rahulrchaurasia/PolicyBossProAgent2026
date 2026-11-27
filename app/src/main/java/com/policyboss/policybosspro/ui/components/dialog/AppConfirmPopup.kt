package com.policyboss.policybosspro.ui.components.dialog

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.ui.components.button.PrimaryCTAButton
import com.policyboss.policybosspro.ui.theme.AppColors

/**
 * Animated confirm popup: scrim fades in while the card springs from 85% to full size; on
 * close (X, back, outside tap) or confirm the exit animation plays first, then [onDismiss] /
 * [onConfirm] is called. Holds no texts of its own: callers pass them (car quote popups take
 * theirs from QuotePopup).
 */
@Composable
fun AppConfirmPopup(
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    title: String? = null,
    @DrawableRes iconRes: Int? = null
) {
    // Target set once, inside remember: re-applying it on recomposition would undo a close
    val visibility = remember { MutableTransitionState(false).apply { targetState = true } }
    var isClosing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val currentOnConfirm by rememberUpdatedState(onConfirm)
    val currentOnDismiss by rememberUpdatedState(onDismiss)

    /**
     * Plays the exit animation, then calls [then]. Capped at [EXIT_TIMEOUT_MS] so the popup can
     * never get stuck open (e.g. animations disabled in developer options).
     */
    fun close(then: () -> Unit) {
        if (isClosing) return // ignore double taps
        isClosing = true
        visibility.targetState = false
        scope.launch {
            withTimeoutOrNull(EXIT_TIMEOUT_MS) {
                snapshotFlow { visibility.isIdle && !visibility.currentState }.first { it }
            }
            then()
        }
    }
    val dismiss = { close { currentOnDismiss() } }
    val confirm = { close { currentOnConfirm() } }

    Dialog(
        onDismissRequest = dismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        // The scrim is drawn (and animated) here, so drop the window's own dim
        val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
        SideEffect { dialogWindow?.setDimAmount(0f) }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AnimatedVisibility(
                visibleState = visibility,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(160))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { dismiss() }
                )
            }

            AnimatedVisibility(
                visibleState = visibility,
                enter = scaleIn(
                    initialScale = 0.85f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)
                ) + fadeIn(tween(180)),
                exit = scaleOut(targetScale = 0.9f, animationSpec = tween(160)) + fadeOut(tween(160))
            ) {
                PopupCard(
                    title = title,
                    message = message,
                    confirmLabel = confirmLabel,
                    iconRes = iconRes,
                    onConfirm = confirm,
                    onClose = dismiss
                )
            }
        }
    }
}

/** Longest wait for the exit animation before the callback runs anyway. */
private const val EXIT_TIMEOUT_MS = 400L

@Composable
private fun PopupCard(
    title: String?,
    message: String,
    confirmLabel: String,
    @DrawableRes iconRes: Int?,
    onConfirm: () -> Unit,
    onClose: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 12.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            // Taps on the card must not reach the scrim behind it
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
    ) {
        Box {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(36.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Close",
                    tint = AppColors.TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (iconRes != null) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                if (!title.isNullOrBlank()) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(
                    text = message,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = AppColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                PrimaryCTAButton(
                    text = confirmLabel,
                    onClick = onConfirm,
                    showArrow = false,
                    containerColor = AppColors.PrimaryBlue,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppConfirmPopupCardPreview() {
    PopupCard(
        title = "Variant changed",
        message = "You have changed the vehicle variant. Please confirm if you want to re-calculate quotes.",
        confirmLabel = "Re-calculate",
        iconRes = null,
        onConfirm = {},
        onClose = {}
    )
}
