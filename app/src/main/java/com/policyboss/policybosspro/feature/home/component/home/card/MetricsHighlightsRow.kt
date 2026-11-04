package com.policyboss.policybosspro.feature.home.component.home.card

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.home.model.home.HomeMetric
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.titleLargeBold

@Composable
fun MetricsHighlightsRow(
    metrics: List<HomeMetric>,
    onMetricClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (metrics.isEmpty()) return

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        metrics.forEach { metric ->
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onMetricClick(metric.id) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (metric.isAlert) AppColors.AlertCardBackground else AppColors.Surface
                ),
                border = BorderStroke(
                    1.dp,
                    if (metric.isAlert) AppColors.AlertCardBorder else AppColors.BorderSecondary
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Text(
                        text = metric.value,
                        style = MaterialTheme.typography.titleLargeBold,
                        color = if (metric.isAlert) AppColors.ErrorRed else AppColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = metric.label.removeSuffix(" >").removeSuffix(">").trim(),
                            style = MaterialTheme.typography.labelSmall,
                            color = AppColors.TextSecondary,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chevron_right),
                            contentDescription = null,
                            tint = AppColors.TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MetricsHighlightsRowPreview() {
    MetricsHighlightsRow(
        metrics = AppDummyData.homeMetrics,
        onMetricClick = {}
    )
}
