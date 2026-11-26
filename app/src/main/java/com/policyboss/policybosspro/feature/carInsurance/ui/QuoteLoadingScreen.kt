package com.policyboss.policybosspro.feature.carInsurance.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarAction
import com.policyboss.policybosspro.feature.carInsurance.model.QuoteFetchState
import com.policyboss.policybosspro.feature.carInsurance.model.QuoteLoadingUiEvent
import com.policyboss.policybosspro.feature.carInsurance.viewmodel.NewCarViewModel
import com.policyboss.policybosspro.ui.ObserveAsEvents
import com.policyboss.policybosspro.ui.components.button.PrimaryCTAButton
import com.policyboss.policybosspro.ui.theme.AppColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun QuoteLoadingRoute(
    viewModel: NewCarViewModel,
    onCloseLoading: () -> Unit,
    onNavigateToQuotes: (quoteId: String, quoteUrl: String?) -> Unit,
    onBackToForm: () -> Unit = onCloseLoading,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler {
        viewModel.onAction(NewCarAction.OnCancelQuoteLoading)
        onCloseLoading()
    }

    ObserveAsEvents(flow = viewModel.quoteLoadingEvent) { event ->
        when (event) {
            is QuoteLoadingUiEvent.NavigateToQuotes -> {
                viewModel.onAction(NewCarAction.OnQuoteNavigationHandled)
                onNavigateToQuotes(event.quoteId, event.quoteUrl)
            }

            is QuoteLoadingUiEvent.CloseLoading -> {
                onCloseLoading()
            }

            is QuoteLoadingUiEvent.ShowSnackbar -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    QuoteLoadingScreen(
        customerFirstName = uiState.displayCustomerFirstName,
        quoteState = uiState.quoteState,
        progressText = uiState.premiumProgressText,
        onCloseClick = {
            viewModel.onAction(NewCarAction.OnCancelQuoteLoading)
            onCloseLoading()
        },
        onRetryClick = { viewModel.onAction(NewCarAction.OnRetryGetQuotes) },
        onSecondaryClick = {
            viewModel.onAction(NewCarAction.OnCancelQuoteLoading)
            onBackToForm()
        },
        modifier = modifier
    )
}

@Composable
fun QuoteLoadingScreen(
    customerFirstName: String,
    quoteState: QuoteFetchState,
    onCloseClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    errorTitle: String = "Unable to fetch quotes",
    /** Text button under "Try again"; null hides it. */
    secondaryActionLabel: String? = "Back to form",
    progressDescription: String = "Curating car insurance quotes",
    /** Live insurer progress, e.g. "6 of 18 insurers responded"; null shows the default text. */
    progressText: String? = null,
    onSecondaryClick: (() -> Unit)? = null
) {
    val headingText = title ?: if (customerFirstName.isNotBlank()) {
        "$customerFirstName, we are curating the\nbest quotes for you!"
    } else {
        "We are curating the\nbest quotes for you!"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.White)
    ) {
        // Soft sky-blue top gradient fading smoothly into pure white (edge-to-edge)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.36f)
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color(0xFFCAE4FF),
                            0.38f to Color(0xFFE7F4FF),
                            0.74f to Color(0xFFF6FAFF),
                            1.0f to AppColors.White
                        )
                    )
                )
        )

        // Top-right Close (X) action respecting status bar inset
        IconButton(
            onClick = onCloseClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 8.dp, end = 12.dp)
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close quote loading",
                tint = AppColors.TextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }

        // Main centered content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top balance spacer
            Spacer(modifier = Modifier.height(56.dp))

            // Center state content
            when (quoteState) {
                is QuoteFetchState.Error -> {
                    QuoteLoadingErrorContent(
                        errorTitle = errorTitle,
                        errorMessage = quoteState.message,
                        secondaryActionLabel = secondaryActionLabel,
                        onRetryClick = onRetryClick,
                        onSecondaryClick = onSecondaryClick ?: onCloseClick
                    )
                }

                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        QuoteCuratingIllustration(
                            modifier = Modifier.size(width = 176.dp, height = 148.dp)
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        Text(
                            text = headingText,
                            fontSize = 20.sp,
                            lineHeight = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = progressText ?: "Please be patient...",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = AppColors.TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .semantics { contentDescription = progressDescription },
                            color = AppColors.PrimaryBlue,
                            trackColor = AppColors.PrimaryBlue.copy(alpha = 0.14f),
                            strokeWidth = 2.5.dp
                        )
                    }
                }
            }

            // Bottom PolicyBoss watermark branding preserving original vector aspect ratio (64:20)
            PolicyBossWatermarkLogo(
                modifier = Modifier
                    .padding(bottom = 28.dp)
                    .height(36.dp)
            )
        }
    }
}

@Composable
private fun QuoteLoadingErrorContent(
    errorTitle: String,
    errorMessage: String,
    secondaryActionLabel: String?,
    onRetryClick: () -> Unit,
    onSecondaryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 320.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = AppColors.AlertCardBackground,
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = "Quote error",
                    tint = AppColors.ErrorRed,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = errorTitle,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = errorMessage.ifBlank { "Something went wrong. Please try again." },
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = AppColors.TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        PrimaryCTAButton(
            text = "Try again",
            onClick = onRetryClick,
            showArrow = false,
            containerColor = AppColors.PrimaryBlue,
            contentColor = AppColors.White
        )

        if (secondaryActionLabel != null) {
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onSecondaryClick) {
                Text(
                    text = secondaryActionLabel,
                    color = AppColors.TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Custom animated illustration matching the quote curating reference:
 * - Blue document folder with soft shadows and translucent inner bars
 * - Center white document with blue checkmark gently lifting
 * - Left & right tilted white documents with muted X marks gently floating
 * - Golden-yellow circular star badge pulsing subtly at bottom-right
 */
@Composable
fun QuoteCuratingIllustration(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "quoteCuratingTransition")

    val centerPaperFloat by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "centerPaperFloat"
    )

    val sidePaperFloat by infiniteTransition.animateFloat(
        initialValue = 2.5f,
        targetValue = -3.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sidePaperFloat"
    )

    val starScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "starScale"
    )

    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.06f,
        targetValue = 0.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraAlpha"
    )

    Canvas(
        modifier = modifier.semantics {
            contentDescription = "Curating best insurance quotes illustration"
        }
    ) {
        val w = size.width
        val h = size.height
        val floatPx = centerPaperFloat.dp.toPx()
        val sideFloatPx = sidePaperFloat.dp.toPx()

        // 1. Subtle ambient blue aura behind illustration
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF42A0FF).copy(alpha = auraAlpha),
                    Color.Transparent
                ),
                center = Offset(w * 0.50f, h * 0.54f),
                radius = w * 0.48f
            ),
            radius = w * 0.48f,
            center = Offset(w * 0.50f, h * 0.54f)
        )

        // 2. Soft floor shadow under folder
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF1E6CE0).copy(alpha = 0.14f),
                    Color.Transparent
                ),
                center = Offset(w * 0.50f, h * 0.90f),
                radius = w * 0.36f
            ),
            topLeft = Offset(w * 0.18f, h * 0.84f),
            size = Size(w * 0.64f, h * 0.12f)
        )

        // 3. Left tilted rejected quote paper (with soft grey X)
        drawDocumentCard(
            center = Offset(w * 0.25f, h * 0.41f + sideFloatPx),
            cardWidth = w * 0.22f,
            cardHeight = h * 0.30f,
            rotationDegrees = -22f,
            isSelected = false
        )

        // 4. Right tilted rejected quote paper (with soft grey X)
        drawDocumentCard(
            center = Offset(w * 0.76f, h * 0.39f - sideFloatPx * 0.8f),
            cardWidth = w * 0.22f,
            cardHeight = h * 0.30f,
            rotationDegrees = 20f,
            isSelected = false
        )

        // 5. Blue folder back layer (with upper-left tab notch)
        val folderLeft = w * 0.24f
        val folderRight = w * 0.76f
        val folderWidth = folderRight - folderLeft
        val folderBottom = h * 0.85f
        val backTop = h * 0.35f
        val tabHeight = h * 0.07f
        val corner = 12.dp.toPx()

        val backFolderPath = Path().apply {
            moveTo(folderLeft + corner, backTop)
            lineTo(folderLeft + folderWidth * 0.42f, backTop)
            quadraticTo(
                folderLeft + folderWidth * 0.48f, backTop,
                folderLeft + folderWidth * 0.52f, backTop + tabHeight
            )
            lineTo(folderRight - corner, backTop + tabHeight)
            quadraticTo(folderRight, backTop + tabHeight, folderRight, backTop + tabHeight + corner)
            lineTo(folderRight, folderBottom - corner)
            quadraticTo(folderRight, folderBottom, folderRight - corner, folderBottom)
            lineTo(folderLeft + corner, folderBottom)
            quadraticTo(folderLeft, folderBottom, folderLeft, folderBottom - corner)
            lineTo(folderLeft, backTop + corner)
            quadraticTo(folderLeft, backTop, folderLeft + corner, backTop)
            close()
        }
        drawPath(
            path = backFolderPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF4BA2FF), Color(0xFF2882EE)),
                startY = backTop,
                endY = folderBottom
            )
        )

        // 6. Center hero white quote paper (with blue checkmark & folded top-right corner)
        drawDocumentCard(
            center = Offset(w * 0.50f, h * 0.36f + floatPx),
            cardWidth = w * 0.29f,
            cardHeight = h * 0.40f,
            rotationDegrees = -7f,
            isSelected = true
        )

        // 7. Blue folder front pocket overlapping bottom of papers
        val frontTop = h * 0.46f
        val frontTabTop = h * 0.42f
        val frontFolderPath = Path().apply {
            moveTo(folderLeft + corner, frontTabTop)
            lineTo(folderLeft + folderWidth * 0.36f, frontTabTop)
            quadraticTo(
                folderLeft + folderWidth * 0.43f, frontTabTop,
                folderLeft + folderWidth * 0.48f, frontTop
            )
            lineTo(folderRight - corner, frontTop)
            quadraticTo(folderRight, frontTop, folderRight, frontTop + corner)
            lineTo(folderRight, folderBottom - corner)
            quadraticTo(folderRight, folderBottom, folderRight - corner, folderBottom)
            lineTo(folderLeft + corner, folderBottom)
            quadraticTo(folderLeft, folderBottom, folderLeft, folderBottom - corner)
            lineTo(folderLeft, frontTabTop + corner)
            quadraticTo(folderLeft, frontTabTop, folderLeft + corner, frontTabTop)
            close()
        }
        drawPath(
            path = frontFolderPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF66B8FF), Color(0xFF2E90FA), Color(0xFF217DF2)),
                startY = frontTabTop,
                endY = folderBottom
            )
        )

        // Subtle frosted document lines on the folder front face
        val barLeft = folderLeft + folderWidth * 0.14f
        val barHeight = 5.5.dp.toPx()
        val barCorner = CornerRadius(barHeight / 2f, barHeight / 2f)
        drawRoundRect(
            color = Color.White.copy(alpha = 0.32f),
            topLeft = Offset(barLeft, frontTop + (folderBottom - frontTop) * 0.25f),
            size = Size(folderWidth * 0.62f, barHeight),
            cornerRadius = barCorner
        )
        drawRoundRect(
            color = Color.White.copy(alpha = 0.24f),
            topLeft = Offset(barLeft, frontTop + (folderBottom - frontTop) * 0.46f),
            size = Size(folderWidth * 0.44f, barHeight),
            cornerRadius = barCorner
        )

        // 8. Yellow circular star badge at bottom-right corner
        val badgeCenter = Offset(folderRight - w * 0.04f, folderBottom - h * 0.05f)
        val badgeRadius = (w * 0.115f) * starScale

        // Soft shadow under yellow badge
        drawCircle(
            color = Color(0xFF8A4B08).copy(alpha = 0.16f),
            radius = badgeRadius * 1.08f,
            center = badgeCenter.copy(y = badgeCenter.y + 2.5.dp.toPx())
        )

        // Golden circular badge fill
        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFDE067), Color(0xFFF6C13B)),
                startY = badgeCenter.y - badgeRadius,
                endY = badgeCenter.y + badgeRadius
            ),
            radius = badgeRadius,
            center = badgeCenter
        )

        // Centered 5-point dark amber star
        drawFivePointStar(
            center = badgeCenter,
            outerRadius = badgeRadius * 0.54f,
            innerRadius = badgeRadius * 0.25f,
            color = Color(0xFF854D0E)
        )
    }
}

private fun DrawScope.drawDocumentCard(
    center: Offset,
    cardWidth: Float,
    cardHeight: Float,
    rotationDegrees: Float,
    isSelected: Boolean
) {
    rotate(degrees = rotationDegrees, pivot = center) {
        val left = center.x - cardWidth / 2f
        val top = center.y - cardHeight / 2f
        val radius = 7.dp.toPx()
        val foldSize = cardWidth * 0.26f

        // Soft drop shadow
        drawRoundRect(
            color = Color(0xFF0F172A).copy(alpha = if (isSelected) 0.08f else 0.06f),
            topLeft = Offset(left + 1.5.dp.toPx(), top + 3.dp.toPx()),
            size = Size(cardWidth, cardHeight),
            cornerRadius = CornerRadius(radius, radius)
        )

        // Paper shape with clipped top-right dog-ear corner
        val paperPath = Path().apply {
            moveTo(left + radius, top)
            lineTo(left + cardWidth - foldSize, top)
            lineTo(left + cardWidth, top + foldSize)
            lineTo(left + cardWidth, top + cardHeight - radius)
            quadraticTo(left + cardWidth, top + cardHeight, left + cardWidth - radius, top + cardHeight)
            lineTo(left + radius, top + cardHeight)
            quadraticTo(left, top + cardHeight, left, top + cardHeight - radius)
            lineTo(left, top + radius)
            quadraticTo(left, top, left + radius, top)
            close()
        }
        drawPath(path = paperPath, color = Color.White)

        // Subtle paper outline for definition against white background
        drawPath(
            path = paperPath,
            color = Color(0xFFE6EEF8),
            style = Stroke(width = 1.dp.toPx())
        )

        // Top-right folded corner triangle
        val foldPath = Path().apply {
            moveTo(left + cardWidth - foldSize, top)
            lineTo(left + cardWidth - foldSize, top + foldSize)
            lineTo(left + cardWidth, top + foldSize)
            close()
        }
        drawPath(path = foldPath, color = Color(0xFFE4ECF7))

        if (isSelected) {
            // Crisp blue checkmark inside the hero paper
            val checkPath = Path().apply {
                moveTo(center.x - cardWidth * 0.17f, center.y + cardHeight * 0.01f)
                lineTo(center.x - cardWidth * 0.03f, center.y + cardHeight * 0.13f)
                lineTo(center.x + cardWidth * 0.20f, center.y - cardHeight * 0.12f)
            }
            drawPath(
                path = checkPath,
                color = Color(0xFF217DF2),
                style = Stroke(
                    width = 3.2.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        } else {
            // Subtle muted grey X mark inside side papers
            val half = cardWidth * 0.17f
            val strokeWidth = 2.2.dp.toPx()
            val markCenterY = center.y + cardHeight * 0.04f
            drawLine(
                color = Color(0xFFB5BDC9),
                start = Offset(center.x - half, markCenterY - half),
                end = Offset(center.x + half, markCenterY + half),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color(0xFFB5BDC9),
                start = Offset(center.x + half, markCenterY - half),
                end = Offset(center.x - half, markCenterY + half),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

private fun DrawScope.drawFivePointStar(
    center: Offset,
    outerRadius: Float,
    innerRadius: Float,
    color: Color
) {
    val path = Path()
    val startAngleRad = -PI / 2.0
    val stepRad = PI / 5.0
    for (i in 0 until 10) {
        val r = if (i % 2 == 0) outerRadius else innerRadius
        val angle = startAngleRad + i * stepRad
        val x = center.x + (r * cos(angle)).toFloat()
        val y = center.y + (r * sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path = path, color = color)
}

@Composable
private fun PolicyBossWatermarkLogo(
    modifier: Modifier = Modifier
) {
    val watermarkBrush = Brush.verticalGradient(
        colorStops = arrayOf(
            0.0f to Color(0xFFE4ECF6),
            0.58f to Color(0xFFD0E2F8),
            1.0f to AppColors.PrimaryBlue
        )
    )

    Image(
        painter = painterResource(id = R.drawable.ic_policyboss_transparent),
        contentDescription = "PolicyBoss",
        contentScale = ContentScale.Fit,
        modifier = modifier
            .aspectRatio(64f / 20f)
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = watermarkBrush,
                    blendMode = BlendMode.SrcIn
                )
            }
    )
}

@Preview(name = "Quote Loading - With Customer Name", showBackground = true, showSystemUi = true)
@Composable
private fun QuoteLoadingScreenWithNamePreview() {
    QuoteLoadingScreen(
        customerFirstName = "Dhruvi",
        quoteState = QuoteFetchState.Loading,
        onCloseClick = {},
        onRetryClick = {}
    )
}

@Preview(name = "Quote Loading - Fallback Name", showBackground = true, showSystemUi = true)
@Composable
private fun QuoteLoadingScreenFallbackPreview() {
    QuoteLoadingScreen(
        customerFirstName = "",
        quoteState = QuoteFetchState.Loading,
        onCloseClick = {},
        onRetryClick = {}
    )
}

@Preview(name = "Quote Loading - Error State", showBackground = true, showSystemUi = true)
@Composable
private fun QuoteLoadingScreenErrorPreview() {
    QuoteLoadingScreen(
        customerFirstName = "Dhruvi",
        quoteState = QuoteFetchState.Error("Unable to connect to quote server. Please check your internet connection."),
        onCloseClick = {},
        onRetryClick = {}
    )
}

