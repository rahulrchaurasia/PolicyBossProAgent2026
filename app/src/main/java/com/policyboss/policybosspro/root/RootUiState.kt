package com.policyboss.policybosspro.root

import com.policyboss.policybosspro.navigation.Dest

data class RootUiState(

    val isLoading: Boolean = true,

    val startDestination: Dest? = null
)