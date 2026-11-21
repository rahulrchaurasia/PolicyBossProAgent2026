package com.policyboss.policybosspro.feature.carInsurance.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
enum class CarEntryMode {
    WITH_CAR_NUMBER,
    WITHOUT_CAR_NUMBER,
    NEW_CAR
}
