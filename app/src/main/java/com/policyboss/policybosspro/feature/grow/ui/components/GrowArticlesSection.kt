package com.policyboss.policybosspro.feature.grow.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.grow.model.GrowArticleItem
import com.policyboss.policybosspro.ui.components.text.TextWithChevron
import com.policyboss.policybosspro.ui.theme.AppColors

@Composable
fun GrowArticlesSection(
    articles: List<GrowArticleItem>,
    onReadArticleClick: (GrowArticleItem) -> Unit,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (articles.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Articles",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Slate900
            )

            TextWithChevron(
                text = "View all",
                onClick = onViewAllClick,
                textStyle = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                ),
                textColor = AppColors.Slate600,
                iconTint = AppColors.Slate600,
                iconSize = 14.dp,
                spacing = 2.dp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            articles.forEach { article ->
                GrowArticleCard(
                    article = article,
                    onClick = { onReadArticleClick(article) }
                )
            }
        }
    }
}

@Composable
private fun GrowArticleCard(
    article: GrowArticleItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(20.dp)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(cardShape),
        shape = cardShape,
        color = AppColors.ArticleCardBackground,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Curved gradient decorative shape on the right side
            Canvas(
                modifier = Modifier.matchParentSize()
            ) {
                val width = size.width
                val height = size.height

                val path = Path().apply {
                    moveTo(width * 0.78f, 0f)
                    cubicTo(
                        width * 0.65f,
                        height * 0.15f,
                        width * 0.92f,
                        height * 0.35f,
                        width * 0.80f,
                        height
                    )
                    lineTo(width, height)
                    lineTo(width, 0f)
                    close()
                }

                drawPath(
                    path = path,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            AppColors.ArticleGradientStart,
                            AppColors.ArticleGradientEnd
                        ),
                        start = Offset(width * 0.7f, 0f),
                        end = Offset(width, height)
                    )
                )
            }

            // Card content layer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Read time & title on the left
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth(0.68f)
                ) {
                    Text(
                        text = article.readTime,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.ArticleReadTime
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = article.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.ArticleTitle,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                }

                // Read now button at the bottom-right
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AppColors.ArticleButtonBg)
                        .clickable(onClick = onClick)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Read now",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.ArticleButtonText
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun GrowArticleCardPreview() {
    GrowArticleCard(
        article = AppDummyData.growArticles.first(),
        onClick = {},
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun GrowArticlesSectionPreview() {
    GrowArticlesSection(
        articles = AppDummyData.growArticles.take(3),
        onReadArticleClick = {},
        onViewAllClick = {},
        modifier = Modifier.padding(16.dp)
    )
}
