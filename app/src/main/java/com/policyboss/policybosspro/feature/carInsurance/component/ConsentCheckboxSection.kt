package com.policyboss.policybosspro.feature.carInsurance.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.ui.theme.AppColors

/**
 * Standardized Consent Checkbox Section.
 * Encapsulated inside a soft neutral gray container (Color(0xFFF3F4F6)) with 12.dp rounded corners
 * and 14.dp padding.
 */
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
        color = Color(0xFFF3F4F6)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Checkbox(
                checked = consentGiven,
                onCheckedChange = onConsentChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = AppColors.PrimaryBlue,
                    uncheckedColor = AppColors.BorderPrimary,
                    checkmarkColor = Color.White
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
                val link = LinkAnnotation.Clickable(
                    tag = "NOTICE",
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = AppColors.PrimaryBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                ) {
                    onNoticeClick()
                }
                withLink(link) {
                    append("Notice for Consent")
                }
            }

            Text(
                text = annotatedText,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onConsentChange(!consentGiven) },
                lineHeight = 17.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConsentCheckboxSectionPreview() {
    ConsentCheckboxSection(
        consentGiven = false,
        onConsentChange = {},
        onNoticeClick = {}
    )
}
