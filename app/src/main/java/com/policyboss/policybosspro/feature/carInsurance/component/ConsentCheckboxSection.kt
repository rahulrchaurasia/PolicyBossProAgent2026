package com.policyboss.policybosspro.feature.carInsurance.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun ConsentCheckboxSection(
    consentGiven: Boolean,
    onConsentChange: (Boolean) -> Unit,
    onNoticeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = AppColors.Gray50
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(
                checked = consentGiven,
                onCheckedChange = onConsentChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = AppColors.PrimaryBlue,
                    uncheckedColor = AppColors.BorderPrimary
                )
            )

            val annotatedText = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = AppColors.TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                ) {
                    append("I hereby give consent to process my customers information, as specified in ")
                }
                pushStringAnnotation(tag = "NOTICE", annotation = "consent_notice")
                withStyle(
                    style = SpanStyle(
                        color = AppColors.PrimaryBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("Notice for Consent")
                }
                pop()
            }

            ClickableText(
                text = annotatedText,
                onClick = { offset ->
                    annotatedText.getStringAnnotations(tag = "NOTICE", start = offset, end = offset)
                        .firstOrNull()?.let {
                            onNoticeClick()
                        } ?: onConsentChange(!consentGiven)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConsentCheckboxSectionPreview() {
    ConsentCheckboxSection(
        consentGiven = true,
        onConsentChange = {},
        onNoticeClick = {}
    )
}
