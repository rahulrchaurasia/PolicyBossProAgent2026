package com.policyboss.policybosspro.feature.carInsurance.ui.carQuoteResult.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.policyboss.policybosspro.R

/**
 * Official Zextra Switch logo badge.
 */
@Composable
fun ZextraSwitchLogo(
    modifier: Modifier = Modifier,
    height: Dp = 20.dp
) {
    Image(
        painter = painterResource(id = R.drawable.ic_zextra_switch_logo),
        contentDescription = "Zextra Switch",
        contentScale = ContentScale.Fit,
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(4.dp))
    )
}
