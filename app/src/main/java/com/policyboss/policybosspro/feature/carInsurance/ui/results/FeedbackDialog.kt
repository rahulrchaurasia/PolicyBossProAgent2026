package com.policyboss.policybosspro.feature.carInsurance.ui.results

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun FeedbackDialog(
    rating: Int,
    feedbackText: String,
    onRatingChange: (Int) -> Unit,
    onFeedbackTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(16.dp),
        containerColor = AppColors.White,
        title = {
            Text(
                text = "Share Your Feedback",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Slate900
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "How satisfied are you with the quotes curated for you?",
                    fontSize = 13.sp,
                    color = AppColors.Slate600
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Star Rating Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..5) {
                        val isSelected = i <= rating
                        Icon(
                            imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Star $i",
                            tint = if (isSelected) Color(0xFFFBBF24) else AppColors.Slate300,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { onRatingChange(i) }
                                .padding(2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = onFeedbackTextChange,
                    placeholder = {
                        Text(
                            text = "Tell us what could be improved...",
                            fontSize = 13.sp,
                            color = AppColors.Slate400
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSubmit,
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryBlue)
            ) {
                Text("Submit", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel", color = AppColors.Slate600)
            }
        }
    )
}

