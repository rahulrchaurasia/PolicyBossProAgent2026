package com.policyboss.policybosspro.feature.carInsurance.component

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.carInsurance.model.CarBrandModelItem
import com.policyboss.policybosspro.feature.carInsurance.model.CarDetailsStep
import com.policyboss.policybosspro.feature.carInsurance.model.CarInsuranceData
import com.policyboss.policybosspro.feature.carInsurance.model.CarVariantItem
import com.policyboss.policybosspro.feature.carInsurance.model.NavigationDirection
import com.policyboss.policybosspro.feature.carInsurance.model.ModelListState
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarAction
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiState
import com.policyboss.policybosspro.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailsBottomSheet(
    uiState: NewCarUiState,
    onAction: (NewCarAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val isStandardPortrait = isPortrait && configuration.screenHeightDp >= 640
    val sheetHeightFraction = if (isStandardPortrait) 0.82f else 0.90f

    ModalBottomSheet(
        onDismissRequest = { onAction(NewCarAction.DismissCarDetailsSheet) },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 36.dp, height = 4.dp)
                        .background(color = Color(0xFFCBD5E1), shape = CircleShape)
                )
            }
        },
        modifier = modifier
    ) {
        // System Back Handling inside sheet: Step backward (Variant -> Fuel -> Make/Model) or dismiss
        BackHandler(enabled = uiState.canStepBackInsideSheet) {
            onAction(NewCarAction.OnSheetBackClick)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(sheetHeightFraction)
                .navigationBarsPadding()
                .imePadding()
                .padding(top = 4.dp)
        ) {
            // =====================================================================
            // ROW 1: Navigation & 3-Step Indicator (←   (1) ─── (2) ─── (3)   ✕)
            // =====================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button (Returns to previous step or dismisses sheet at Step 1)
                IconButton(
                    onClick = {
                        if (uiState.canStepBackInsideSheet) {
                            onAction(NewCarAction.OnSheetBackClick)
                        } else {
                            onAction(NewCarAction.DismissCarDetailsSheet)
                        }
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AppColors.TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // 3-Step Stepper Indicator
                CarDetailsStepIndicator(
                    currentStep = uiState.sheetStep,
                    hasBrandModel = uiState.draftBrandModel != null || uiState.model.isNotBlank(),
                    hasFuel = !uiState.draftFuel.isNullOrBlank() || uiState.fuel.isNotBlank(),
                    onStepClick = { step -> onAction(NewCarAction.OnSheetStepClick(step)) }
                )

                // Close Button (✕)
                IconButton(
                    onClick = { onAction(NewCarAction.DismissCarDetailsSheet) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = AppColors.TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // =====================================================================
            // ROW 2: Interactive Context Summary / Breadcrumbs
            // =====================================================================
            CarDetailsContextSummary(
                step = uiState.sheetStep,
                brandModel = uiState.draftBrandModel ?: if (uiState.model.isNotBlank()) CarBrandModelItem(uiState.makeId, uiState.make, uiState.modelId, uiState.model) else null,
                fuel = uiState.draftFuel ?: uiState.fuel.takeIf { it.isNotBlank() },
                onStepClick = { step -> onAction(NewCarAction.OnSheetStepClick(step)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // =====================================================================
            // ROW 3: Persistent Search Bar (BasicTextField-inside-Surface)
            // =====================================================================
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, AppColors.BorderSecondary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(48.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Search",
                        tint = AppColors.TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (uiState.sheetSearchQuery.isEmpty()) {
                            Text(
                                text = uiState.sheetSearchPlaceholder,
                                fontSize = 14.sp,
                                color = AppColors.TextPlaceholder,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        BasicTextField(
                            value = uiState.sheetSearchQuery,
                            onValueChange = { onAction(NewCarAction.OnSheetSearchQueryChange(it)) },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = AppColors.TextPrimary
                            ),
                            cursorBrush = SolidColor(AppColors.PrimaryBlue),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (uiState.sheetSearchQuery.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { onAction(NewCarAction.OnSheetSearchQueryChange("")) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = AppColors.TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stage Title (e.g. "Select Car Brand", "Select Car Fuel Type", "Select Car Variant")
            Text(
                text = uiState.sheetTitle,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // =====================================================================
            // SELECTION LIST AREA: Animated Horizontal Slide Content
            // =====================================================================
            AnimatedContent(
                targetState = uiState.sheetStep,
                transitionSpec = {
                    if (uiState.sheetNavigationDirection == NavigationDirection.FORWARD) {
                        slideInHorizontally(
                            animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
                        ) { it } + fadeIn(animationSpec = tween(180)) togetherWith
                                slideOutHorizontally(
                                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
                                ) { -it } + fadeOut(animationSpec = tween(180))
                    } else {
                        slideInHorizontally(
                            animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
                        ) { -it } + fadeIn(animationSpec = tween(180)) togetherWith
                                slideOutHorizontally(
                                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
                                ) { it } + fadeOut(animationSpec = tween(180))
                    }
                },
                label = "CarDetailsStageTransition",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { targetStep ->
                when (targetStep) {
                    CarDetailsStep.MAKE_MODEL -> {
                        val modelListState = uiState.modelListState
                        when {
                            uiState.brandModels.isEmpty() && modelListState is ModelListState.Error -> {
                                CatalogueStatusContent(
                                    message = modelListState.message,
                                    actionLabel = "Try again",
                                    onActionClick = { onAction(NewCarAction.OnRetryModelList) }
                                )
                            }

                            uiState.brandModels.isEmpty() && modelListState !is ModelListState.Success -> {
                                CatalogueStatusContent(message = "Loading car brands...", isLoading = true)
                            }

                            uiState.filteredBrandModels.isEmpty() -> {
                                CatalogueStatusContent(
                                    message = if (uiState.sheetSearchQuery.isBlank()) {
                                        "No car models are available right now."
                                    } else {
                                        "No car models match \"${uiState.sheetSearchQuery.trim()}\"."
                                    }
                                )
                            }

                            else -> {
                                BrandModelGrid(
                                    items = uiState.filteredBrandModels,
                                    selectedItem = uiState.draftBrandModel,
                                    onItemSelected = { onAction(NewCarAction.OnDraftBrandModelSelected(it)) }
                                )
                            }
                        }
                    }

                    CarDetailsStep.FUEL -> {
                        FuelGrid(
                            items = uiState.filteredFuelTypes,
                            selectedItem = uiState.draftFuel,
                            onItemSelected = { onAction(NewCarAction.OnDraftFuelSelected(it)) }
                        )
                    }

                    CarDetailsStep.VARIANT -> {
                        VariantGrid(
                            items = uiState.filteredVariants,
                            selectedItem = uiState.draftVariant,
                            onItemSelected = { onAction(NewCarAction.OnDraftVariantSelected(it)) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Loading / empty / error placeholder for the live Make & Model catalogue.
 */
@Composable
private fun CatalogueStatusContent(
    message: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    actionLabel: String? = null,
    onActionClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = AppColors.PrimaryBlue,
                trackColor = AppColors.PrimaryBlue.copy(alpha = 0.14f),
                strokeWidth = 2.5.dp
            )
        }
        Text(
            text = message,
            fontSize = 14.sp,
            color = AppColors.TextSecondary,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null) {
            TextButton(onClick = onActionClick) {
                Text(
                    text = actionLabel,
                    color = AppColors.PrimaryBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * 3-Step State-Driven Stepper Indicator: (1) ─── (2) ─── (3)
 */
@Composable
private fun CarDetailsStepIndicator(
    currentStep: CarDetailsStep,
    hasBrandModel: Boolean,
    hasFuel: Boolean,
    onStepClick: (CarDetailsStep) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Step 1
        StepCircle(
            stepNumber = 1,
            isActive = currentStep == CarDetailsStep.MAKE_MODEL,
            isCompleted = hasBrandModel && currentStep != CarDetailsStep.MAKE_MODEL,
            onClick = { onStepClick(CarDetailsStep.MAKE_MODEL) }
        )

        // Connector 1-2
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(2.dp)
                .background(if (hasBrandModel) AppColors.PrimaryBlue else Color(0xFFCBD5E1))
        )

        // Step 2
        StepCircle(
            stepNumber = 2,
            isActive = currentStep == CarDetailsStep.FUEL,
            isCompleted = hasFuel && currentStep == CarDetailsStep.VARIANT,
            onClick = { if (hasBrandModel) onStepClick(CarDetailsStep.FUEL) }
        )

        // Connector 2-3
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(2.dp)
                .background(if (hasFuel && currentStep == CarDetailsStep.VARIANT) AppColors.PrimaryBlue else Color(0xFFCBD5E1))
        )

        // Step 3
        StepCircle(
            stepNumber = 3,
            isActive = currentStep == CarDetailsStep.VARIANT,
            isCompleted = false,
            onClick = { if (hasBrandModel && hasFuel) onStepClick(CarDetailsStep.VARIANT) }
        )
    }
}

@Composable
private fun StepCircle(
    stepNumber: Int,
    isActive: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isActive -> AppColors.PrimaryBlue
        isCompleted -> AppColors.PrimaryBlue
        else -> Color.White
    }
    val contentColor = when {
        isActive || isCompleted -> Color.White
        else -> AppColors.TextSecondary
    }
    val borderColor = when {
        isActive || isCompleted -> AppColors.PrimaryBlue
        else -> Color(0xFFCBD5E1)
    }

    Box(
        modifier = Modifier
            .size(30.dp)
            .background(backgroundColor, CircleShape)
            .border(1.5.dp, borderColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stepNumber.toString(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}

/**
 * Interactive Context Summary / Breadcrumbs (Row 2)
 */
@Composable
private fun CarDetailsContextSummary(
    step: CarDetailsStep,
    brandModel: CarBrandModelItem?,
    fuel: String?,
    onStepClick: (CarDetailsStep) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        when (step) {
            CarDetailsStep.MAKE_MODEL -> {
                Text(
                    text = "STEP 1: SELECT CAR BRAND & MODEL",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextSecondary,
                    letterSpacing = 0.5.sp
                )
            }

            CarDetailsStep.FUEL -> {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AppColors.BadgeIndigoLight,
                    modifier = Modifier.clickable { onStepClick(CarDetailsStep.MAKE_MODEL) }
                ) {
                    Text(
                        text = brandModel?.displayName ?: "Brand & Model",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.PrimaryBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = "• Select Fuel",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.TextSecondary
                )
            }

            CarDetailsStep.VARIANT -> {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AppColors.BadgeIndigoLight,
                    modifier = Modifier.clickable { onStepClick(CarDetailsStep.MAKE_MODEL) }
                ) {
                    Text(
                        text = brandModel?.displayName ?: "Brand & Model",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.PrimaryBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "•",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextSecondary
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AppColors.BadgeIndigoLight,
                    modifier = Modifier.clickable { onStepClick(CarDetailsStep.FUEL) }
                ) {
                    Text(
                        text = fuel?.uppercase() ?: "Fuel",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.PrimaryBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "• Select Variant",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.TextSecondary
                )
            }
        }
    }
}

/**
 * 2-Column Grid for Stage 1: Brand & Model
 */
@Composable
private fun BrandModelGrid(
    items: List<CarBrandModelItem>,
    selectedItem: CarBrandModelItem?,
    onItemSelected: (CarBrandModelItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No car brands found",
                fontSize = 14.sp,
                color = AppColors.TextSecondary
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            modifier = modifier.fillMaxSize()
        ) {
            items(items, key = { it.key }) { item ->
                val isSelected = selectedItem?.key == item.key
                CatalogueItemCard(
                    title = item.displayName,
                    isSelected = isSelected,
                    onClick = { onItemSelected(item) }
                )
            }
        }
    }
}

/**
 * 2-Column Grid for Stage 2: Fuel Type
 */
@Composable
private fun FuelGrid(
    items: List<String>,
    selectedItem: String?,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No fuel types found",
                fontSize = 14.sp,
                color = AppColors.TextSecondary
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            modifier = modifier.fillMaxSize()
        ) {
            items(items, key = { it }) { fuel ->
                val isSelected = selectedItem?.equals(fuel, ignoreCase = true) == true
                CatalogueItemCard(
                    title = fuel,
                    isSelected = isSelected,
                    onClick = { onItemSelected(fuel) }
                )
            }
        }
    }
}

/**
 * 2-Column Grid for Stage 3: Variant
 */
@Composable
private fun VariantGrid(
    items: List<CarVariantItem>,
    selectedItem: CarVariantItem?,
    onItemSelected: (CarVariantItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No variants found for this selection",
                fontSize = 14.sp,
                color = AppColors.TextSecondary
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            modifier = modifier.fillMaxSize()
        ) {
            items(items, key = { it.variantId }) { variant ->
                val isSelected = selectedItem?.variantId == variant.variantId
                CatalogueItemCard(
                    title = variant.variantName,
                    isSelected = isSelected,
                    onClick = { onItemSelected(variant) }
                )
            }
        }
    }
}

/**
 * Standardized Catalogue Selection Card (2-Column Grid Item)
 */
@Composable
private fun CatalogueItemCard(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Color(0xFFF0F5FF) else Color.White,
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) AppColors.PrimaryBlue else AppColors.BorderSecondary
        ),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title.uppercase(),
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) AppColors.PrimaryBlue else AppColors.TextPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CarDetailsBottomSheetPreview() {
    CarDetailsBottomSheet(
        uiState = NewCarUiState(
            isCarDetailsSheetOpen = true,
            sheetStep = CarDetailsStep.MAKE_MODEL
        ),
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun CarDetailsStepIndicatorPreview() {
    CarDetailsStepIndicator(
        currentStep = CarDetailsStep.MAKE_MODEL,
        hasBrandModel = false,
        hasFuel = false,
        onStepClick = {}
    )
}
