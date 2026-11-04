package com.policyboss.policybosspro.feature.grow.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.grow.model.GrowTemplateItem
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun GrowTemplatesSection(
    templates: List<GrowTemplateItem>,
    onTemplateClick: (GrowTemplateItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (templates.isEmpty()) return

    val featuredTemplate = templates.find { it.isFeatured } ?: templates.first()
    val otherTemplates = templates.filter { it.id != featuredTemplate.id }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Templates",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.Slate900
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Featured Template Card (Lead Forms)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(AppColors.Surface)
                .border(1.dp, AppColors.BorderSecondary, RoundedCornerShape(14.dp))
                .clickable { onTemplateClick(featuredTemplate) }
                .padding(vertical = 16.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = featuredTemplate.imageRes),
                contentDescription = featuredTemplate.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(46.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = featuredTemplate.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.Slate900
            )
        }

        if (otherTemplates.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            // Two bottom cards side-by-side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                otherTemplates.forEach { template ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(AppColors.Surface)
                            .border(1.dp, AppColors.BorderSecondary, RoundedCornerShape(14.dp))
                            .clickable { onTemplateClick(template) }
                            .padding(vertical = 16.dp, horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = template.imageRes),
                            contentDescription = template.title,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(44.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = template.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.Slate900
                        )
                    }
                }
            }
        }
    }
}
