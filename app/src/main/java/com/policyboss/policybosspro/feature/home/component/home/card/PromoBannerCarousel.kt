package com.policyboss.policybosspro.feature.home.component.home.card



import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.feature.dummyData.AppDummyData
import com.policyboss.policybosspro.feature.home.model.home.PromoBanner
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.bodyLargeBold
import com.policyboss.policybosspro.ui.theme.captionSmall
import com.policyboss.policybosspro.ui.theme.gradients.AppGradients
import com.policyboss.policybosspro.ui.theme.labelMediumSemiBold
import com.policyboss.policybosspro.ui.theme.labelSmallBold

@Composable
fun PromoBannerCarousel(
    banners: List<PromoBanner>,
    onBannerClick: (PromoBanner) -> Unit,
    onSellPolicyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (banners.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { banners.size })

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            key = { page -> banners[page].id },
            beyondViewportPageCount = 1
        ) { page ->
            val banner = banners[page]
            val bannerShape = RoundedCornerShape(20.dp)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp) // Fixed height to comfortably fit the 145.dp image + padding
                    .clip(bannerShape)
                    .clickable { onBannerClick(banner) },
                shape = bannerShape,
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                // Main Box to allow overlapping elements
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppGradients.PromoBannerGradient)
                ) {
                    // 1. Top Right Logo
                    Image(
                        painter = painterResource(id = R.drawable.ic_policyboss_transparent),
                        contentDescription = "PolicyBoss",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 16.dp, end = 16.dp)
                            .height(18.dp),
                        contentScale = ContentScale.Fit
                    )

                    // 2. Left Side Content (Text & Button)
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .fillMaxHeight()
                            .fillMaxWidth(0.55f) // Restrict width to leave room for the right image
                            .padding(start = 16.dp, top = 20.dp, bottom = 20.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = banner.tag,
                                color = AppColors.White.copy(alpha = 0.85f),
                                style = MaterialTheme.typography.labelSmallBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = banner.title,
                                color = AppColors.White,
                                style = MaterialTheme.typography.bodyLargeBold
                            )
                        }

                        // Sell Policy CTA Button
                        Surface(
                            modifier = Modifier.clickable { onSellPolicyClick(banner.id) },
                            shape = RoundedCornerShape(24.dp),
                            color = AppColors.BrandDark
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = banner.buttonText,
                                    color = AppColors.White,
                                    style = MaterialTheme.typography.labelMediumSemiBold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_chevron_right), // Use standard arrow
                                    contentDescription = null,
                                    tint = AppColors.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // 3. Right Side Images (Coin, Man, Badge)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp) // Base padding from right edge
                    ) {
                        // Background Gold Coin
                        Image(
                            painter = painterResource(id = R.drawable.img_gold_coin),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .offset(x = 26.dp, y = 14.dp) // Pushed out to bleed off the edge
                                .size(62.dp)
                        )

                        // Main Person Image
                        Image(
                            painter = painterResource(id = banner.imageRes),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                // Specific requested size
                                .size(width = 127.dp, height = 145.dp)
                        )

                        // Floating Earning Badge Overlapping Image
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = AppColors.White,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = (-16).dp, x = (-16).dp) // Adjust floating position
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Little Blue Circle to represent the icon in Figma
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .background(AppColors.PrimaryBlue, CircleShape)
                                        .padding(2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_money), // Optional internal icon
                                        contentDescription = null,
                                        tint = AppColors.White
                                    )
                                }
                                Text(
                                    text = banner.earningTag,
                                    style = MaterialTheme.typography.labelSmallBold,
                                    color = AppColors.BrandDark
                                )
                            }
                        }
                    }
                }
            }
        }

        // Pager indicator dots
        if (banners.size > 1) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(banners.size) { iteration ->
                    val isCurrent = pagerState.currentPage == iteration
                    Box(
                        modifier = Modifier
                            .size(if (isCurrent) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCurrent) AppColors.PrimaryBlue else AppColors.BorderPrimary.copy(alpha = 0.6f)
                            )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7FAFC)
@Composable
private fun PromoBannerCarouselPreview() {
    PromoBannerCarousel(
        banners = AppDummyData.promoBanners,
        onBannerClick = {},
        onSellPolicyClick = {}
    )
}