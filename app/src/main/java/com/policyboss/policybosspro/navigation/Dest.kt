package com.policyboss.policybosspro.navigation



import com.policyboss.policybosspro.feature.login.model.verifyAccount.VerifyOtpSource


import kotlinx.serialization.Serializable

@Serializable
sealed class Dest {

    // =====================================
    // ROOT
    // =====================================


    // =====================================
    // GRAPHS : 3 Is Main Node
    // =====================================


    @Serializable
    data object CustomSplash : Dest()

    @Serializable data object AuthGraph : Dest()
    @Serializable data object MainGraph : Dest()

  //  Bottom Graphs

    //region  Graphs
    @Serializable
    data object HomeGraph : Dest()

    @Serializable
    data object ClaimGraph : Dest()
    
    @Serializable
    data object VaultGraph : Dest()

    @Serializable
    data object SalesGraph : Dest()

    @Serializable
    data object DashboardGraph : Dest()

    @Serializable
    data object PrivilegeGraph : Dest()

    //endregion

    // =====================================
    // AUTH DESTINATIONS
    // =====================================

     // region AUTH DESTINATIONS
    @Serializable
    data object Welcome : Dest()

    @Serializable
    data object Login : Dest()

    @Serializable
    data object Register : Dest()

    @Serializable
    data class VerifyAccount(
        val fullName: String,
        val mobileNumber: String,
        val source: VerifyOtpSource,
        val loginInput: String = "",
        val ssId: String = ""
    ) : Dest()

    //endregion


    // =====================================
    // MAIN APP DESTINATIONS
    // =====================================

    // region MAIN APP DESTINATIONS
    @Serializable
    data object MainScreen : Dest()

    // --- Standalone Screens (Accessible from Home, but not bottom tabs) ---

    @Serializable
    data object Profile : Dest()

    @Serializable
    data object EditPersonalInfo : Dest()

    @Serializable
    data object Menu : Dest()

    //endregion

    // --- Bottom Navigation Destinations Tabs ---
    // region Bottom Navigation
    @Serializable
    data object Home : Dest()

    @Serializable
    data object Leads : Dest()
    @Serializable
    data object Dashboard : Dest()

    @Serializable
    data object Sales : Dest()
    @Serializable
    data class PolicyDetails(val policyId: String) : Dest()
    @Serializable
    data object SalesFilter : Dest()
    @Serializable
    data object Grow : Dest()
    //endregion

    @Serializable
    data object SyncContact : Dest() // Formerly EmailPan

    @Serializable
    object SyncContactProcess : Dest()
    //endregion


    @Serializable
    object CashlessGarage : Dest()


    @Serializable
    data class CommonWebView(
        val url: String,
        val title: String
    ) : Dest()

    @Serializable
    data class NewCarInsurance(
        val isNewCar: Boolean = true
    ) : Dest()

    //endregion

    }

