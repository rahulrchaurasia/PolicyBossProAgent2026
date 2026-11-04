
package com.policyboss.policybosspro.feature.home.model.menu

import androidx.compose.runtime.Immutable

@Immutable
data class MenuProductItem(
    val title: String,
    val iconRes: Int,
    val isComingSoon: Boolean = false
)

@Immutable
data class QuoteTabItem(
    val title: String,
    val iconRes: Int,
    val isComingSoon: Boolean = false
)