package com.policyboss.policybosspro.ui.components.button

import androidx.compose.material3.Text
import androidx.compose.material3.Icon

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.policyboss.policybosspro.ui.theme.AppColors
import com.policyboss.policybosspro.ui.theme.buttonLinkSmall
import com.policyboss.policybosspro.R

@Composable
fun CardButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = AppColors.BrandDark,
        onClick = onClick
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = text,
                style = MaterialTheme.typography.buttonLinkSmall,
                color = Color.White
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun CardButtonPreview() {

    MaterialTheme {

        CardButton(
            text = "Compare Now",
            onClick = {}
        )
    }
}