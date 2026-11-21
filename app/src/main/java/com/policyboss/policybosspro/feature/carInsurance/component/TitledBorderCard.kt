package com.policyboss.policybosspro.feature.carInsurance.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.ui.theme.AppColors

/**
 * A container card with a notched top-left border title (fieldset legend / OutlinedTextField style).
 * The title sits centered on the top border line with a background cutout to mask the border behind it.
 */
@Composable
fun TitledBorderCard(
    title: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White,
    borderColor: Color = AppColors.BorderSecondary,
    titleColor: Color = AppColors.PrimaryBlue,
    cutoutBackgroundColor: Color = Color.White,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = containerColor,
            border = BorderStroke(1.dp, borderColor),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 9.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                content = content
            )
        }

        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = titleColor,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 14.dp)
                .background(cutoutBackgroundColor)
                .padding(horizontal = 6.dp)
        )
    }
}
