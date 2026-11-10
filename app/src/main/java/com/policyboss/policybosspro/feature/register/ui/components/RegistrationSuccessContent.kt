package com.policyboss.policybosspro.feature.register.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.ui.components.button.PrimaryCTAButton
import com.policyboss.policybosspro.ui.components.animation.AppLottieAnimation
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.PolicyBossTheme
import com.policyboss.policybosspro.ui.theme.textPrimary
import com.policyboss.policybosspro.ui.theme.textSecondary

/**
 * Centered success content displayed immediately after a successful POSP registration.
 * Plays the professional Lottie success checkmark animation, shows the confirmation message,
 * and highlights the registered POSP / FBA ID.
 */
@Composable
fun RegistrationSuccessContent(
    fbaId: String?,
    message: String?,
    modifier: Modifier = Modifier,
    onContinueClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Lottie Success Animation Container
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(AppColors.SuccessContainerLight),
                contentAlignment = Alignment.Center
            ) {
                AppLottieAnimation(
                    rawResId = R.raw.registration_success,
                    modifier = Modifier.size(110.dp),
                    iterations = 1,
                    speed = 1.0f,
                    isPlaying = true,
                    restartOnPlay = true
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Success Title
            Text(
                text = "Registration Successful!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.textPrimary,
                textAlign = TextAlign.Center
            )

            // Registered FBA / POSP ID Badge
            if (!fbaId.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = AppColors.Primary.copy(alpha = 0.08f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "POSP ID: ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.textSecondary
                        )
                        Text(
                            text = fbaId,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Success Confirmation Message
            Text(
                text = message?.takeIf { it.isNotBlank() }
                    ?: "Your registration has been completed successfully.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Subtle Redirect Notice
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = AppColors.Primary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Redirecting automatically...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.textSecondary.copy(alpha = 0.8f)
                )
            }

            // Optional Manual Continue Button
            if (onContinueClick != null) {
                Spacer(modifier = Modifier.height(24.dp))
                PrimaryCTAButton(
                    text = "Continue to Login",
                    onClick = onContinueClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

@Preview(name = "Registration Success - With FBA ID", showBackground = true)
@Composable
private fun RegistrationSuccessContentPreview_WithFbaId() {
    PolicyBossTheme {
        RegistrationSuccessContent(
            fbaId = "150252",
            message = "Registration completed successfully! Welcome to PolicyBoss Pro."
        )
    }
}

@Preview(name = "Registration Success - Without FBA ID", showBackground = true)
@Composable
private fun RegistrationSuccessContentPreview_WithoutFbaId() {
    PolicyBossTheme {
        RegistrationSuccessContent(
            fbaId = null,
            message = "Your POSP registration is being processed."
        )
    }
}

