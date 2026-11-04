package com.policyboss.policybosspro.ui.components.datePicker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.ui.theme.AppColors
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Reusable Date Range Selector Bar displaying the current selected range with an expandable arrow.
 */
@Composable
fun DateRangeBar(
    dateRange: String,
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    border: BorderStroke = BorderStroke(1.dp, AppColors.BorderSecondary),
    backgroundColor: Color = AppColors.Surface,
    iconTint: Color = AppColors.TextSecondary,
    arrowTint: Color = AppColors.TextSecondary
) {
    Surface(
        shape = shape,
        border = border,
        color = backgroundColor,
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = iconTint
                )
                Text(
                    text = dateRange,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextPrimary
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_down),
                contentDescription = if (isExpanded) "Collapse Calendar" else "Expand Calendar",
                tint = arrowTint,
                modifier = Modifier
                    .size(18.dp)
                    .rotate(if (isExpanded) 180f else 0f)
            )
        }
    }
}

/**
 * Reusable Date Range Calendar Card with presets, month navigation, calendar grid,
 * range highlighting, and Apply button.
 */
@Composable
fun DateRangeCalendarCard(
    currentRangeString: String,
    onApply: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val monthFormatter = remember { DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH) }
    val rangeFormatter = remember { DateTimeFormatter.ofPattern("d MMM, yy", Locale.ENGLISH) }

    // Parse currentRangeString if valid
    val parsedDates = remember(currentRangeString) {
        runCatching {
            val parts = currentRangeString.split(" - ")
            if (parts.size == 2) {
                val s = LocalDate.parse(parts[0].trim(), rangeFormatter)
                val e = LocalDate.parse(parts[1].trim(), rangeFormatter)
                Pair(s, e)
            } else {
                null
            }
        }.getOrNull()
    }

    // Selected range state
    var startDate by remember(parsedDates) {
        mutableStateOf(parsedDates?.first ?: LocalDate.of(2026, 7, 1))
    }
    var endDate by remember(parsedDates) {
        mutableStateOf(parsedDates?.second ?: LocalDate.of(2026, 7, 31))
    }

    // Current displayed month
    var currentYearMonth by remember(startDate) {
        mutableStateOf(startDate?.let { YearMonth.from(it) } ?: YearMonth.of(2026, 7))
    }

    // Presets
    val presets = listOf("This Week", "This Month", "This Year", "Custom")
    var selectedPreset by remember(parsedDates) {
        val s = parsedDates?.first
        val e = parsedDates?.second
        val preset = when {
            s == LocalDate.of(2026, 7, 1) && e == LocalDate.of(2026, 7, 31) -> "This Month"
            s == LocalDate.of(2026, 7, 12) && e == LocalDate.of(2026, 7, 18) -> "This Week"
            s == LocalDate.of(2026, 1, 1) && e == LocalDate.of(2026, 12, 31) -> "This Year"
            else -> "Custom"
        }
        mutableStateOf(preset)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, AppColors.BorderSecondary),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Presets Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { preset ->
                    val isSelected = selectedPreset == preset
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) AppColors.PrimaryBlue else AppColors.BorderSecondary
                        ),
                        color = if (isSelected) AppColors.BlueTintBackground else Color.Transparent,
                        onClick = {
                            selectedPreset = preset
                            when (preset) {
                                "This Week" -> {
                                    currentYearMonth = YearMonth.of(2026, 7)
                                    startDate = LocalDate.of(2026, 7, 12)
                                    endDate = LocalDate.of(2026, 7, 18)
                                }
                                "This Month" -> {
                                    currentYearMonth = YearMonth.of(2026, 7)
                                    startDate = LocalDate.of(2026, 7, 1)
                                    endDate = LocalDate.of(2026, 7, 31)
                                }
                                "This Year" -> {
                                    currentYearMonth = YearMonth.of(2026, 7)
                                    startDate = LocalDate.of(2026, 1, 1)
                                    endDate = LocalDate.of(2026, 12, 31)
                                }
                                "Custom" -> {
                                    // Keep current selection
                                }
                            }
                        }
                    ) {
                        Text(
                            text = preset,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) AppColors.PrimaryBlue else AppColors.TextPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            // 2. Month Navigation Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { currentYearMonth = currentYearMonth.minusMonths(1) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chevron_left),
                        contentDescription = "Previous Month",
                        tint = AppColors.TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = currentYearMonth.format(monthFormatter),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = AppColors.TextPrimary
                )

                IconButton(
                    onClick = { currentYearMonth = currentYearMonth.plusMonths(1) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chevron_right),
                        contentDescription = "Next Month",
                        tint = AppColors.TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 3. Days of the Week Header
            val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                daysOfWeek.forEach { dayName ->
                    Text(
                        text = dayName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Slate400,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 4. Calendar Days Grid
            val firstDayOfMonth = currentYearMonth.atDay(1)
            val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Sunday = 0, Monday = 1, etc.
            val daysInMonth = currentYearMonth.lengthOfMonth()
            val prevMonth = currentYearMonth.minusMonths(1)
            val daysInPrevMonth = prevMonth.lengthOfMonth()

            // 6 rows x 7 days
            val totalCells = 42
            val weeks = (0 until totalCells).chunked(7)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                weeks.forEach { week ->
                    val hasCurrentMonthDays = week.any { index ->
                        val dayOffset = index - firstDayOfWeek
                        dayOffset in 0 until daysInMonth
                    }
                    val isSixthRowEmpty = !hasCurrentMonthDays && week.first() >= firstDayOfWeek + daysInMonth

                    if (!isSixthRowEmpty) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            week.forEach { index ->
                                val dayOffset = index - firstDayOfWeek
                                val isPrevMonth = dayOffset < 0
                                val isNextMonth = dayOffset >= daysInMonth
                                val isCurrentMonth = !isPrevMonth && !isNextMonth

                                val date = when {
                                    isPrevMonth -> prevMonth.atDay(daysInPrevMonth + dayOffset + 1)
                                    isNextMonth -> currentYearMonth.plusMonths(1).atDay(dayOffset - daysInMonth + 1)
                                    else -> currentYearMonth.atDay(dayOffset + 1)
                                }

                                val isStart = startDate != null && date == startDate
                                val isEnd = endDate != null && date == endDate
                                val isInRange = startDate != null && endDate != null &&
                                        date.isAfter(startDate) && date.isBefore(endDate)

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Range highlight connector
                                    if (isInRange) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(30.dp)
                                                .background(AppColors.CalendarRangeHighlight)
                                        )
                                    } else if (isStart && endDate != null && endDate!!.isAfter(startDate)) {
                                        // Connector extending to right
                                        Row(modifier = Modifier.fillMaxSize()) {
                                            Spacer(modifier = Modifier.weight(0.5f))
                                            Box(
                                                modifier = Modifier
                                                    .weight(0.5f)
                                                    .height(30.dp)
                                                    .align(Alignment.CenterVertically)
                                                    .background(AppColors.CalendarRangeHighlight)
                                            )
                                        }
                                    } else if (isEnd && startDate != null && endDate!!.isAfter(startDate)) {
                                        // Connector extending to left
                                        Row(modifier = Modifier.fillMaxSize()) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(0.5f)
                                                    .height(30.dp)
                                                    .align(Alignment.CenterVertically)
                                                    .background(AppColors.CalendarRangeHighlight)
                                            )
                                            Spacer(modifier = Modifier.weight(0.5f))
                                        }
                                    }

                                    // Day Circle
                                    val circleBg = if (isStart || isEnd) AppColors.PrimaryBlue else Color.Transparent
                                    val textColor = when {
                                        isStart || isEnd -> Color.White
                                        isInRange -> AppColors.CalendarRangeText
                                        !isCurrentMonth -> AppColors.Slate300
                                        else -> AppColors.TextPrimary
                                    }
                                    val fontWeight = if (isStart || isEnd) FontWeight.Bold else FontWeight.Normal

                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(circleBg, CircleShape)
                                            .clickable {
                                                selectedPreset = "Custom"
                                                if (startDate == null || (startDate != null && endDate != null)) {
                                                    startDate = date
                                                    endDate = null
                                                } else {
                                                    if (date.isBefore(startDate)) {
                                                        endDate = startDate
                                                        startDate = date
                                                    } else {
                                                        endDate = date
                                                    }
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = date.dayOfMonth.toString(),
                                            fontSize = 13.sp,
                                            fontWeight = fontWeight,
                                            color = textColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. Apply Button Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, AppColors.PrimaryBlue),
                    color = Color.White,
                    onClick = {
                        val start = startDate
                        val end = endDate ?: startDate
                        if (start != null && end != null) {
                            val formatted = "${start.format(rangeFormatter)} - ${end.format(rangeFormatter)}"
                            onApply(formatted)
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Apply",
                            color = AppColors.PrimaryBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chevron_right),
                            contentDescription = "Apply",
                            tint = AppColors.PrimaryBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
