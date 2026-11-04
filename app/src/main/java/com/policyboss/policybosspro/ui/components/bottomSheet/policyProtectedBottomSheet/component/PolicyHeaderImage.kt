package com.policyboss.policybosspro.ui.components.bottomSheet.policyProtectedBottomSheet.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.policyboss.policybosspro.R

@Composable
fun PolicyHeaderImage() {

    Box(
        contentAlignment = Alignment.Center
    ) {

        // Shield (background)
        Image(
            painter = painterResource(
                R.drawable.ic_security_check
            ),

            contentDescription = null,

            modifier = Modifier
                .size(120.dp)

                // Move slightly left
                .offset(
                    x = (-18).dp,
                    y = (-6).dp
                )

                // Slight left rotation
                .rotate(-12f)
        )

        Image(
            painter = painterResource(
                R.drawable.ic_car_protect
            ),

            contentDescription = null,

            modifier = Modifier
                .width(180.dp)
                .height(110.dp),

            contentScale = ContentScale.Fit
        )
    }
}