package com.policyboss.policybosspro.feature.carInsurance.model

import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.ui.components.dropDown.DropdownOption

object CarInsuranceData {

    val rtoOptions = listOf(
        DropdownOption("RJ-14", "RJ14 - Jaipur"),
        DropdownOption("MH-01", "MH01 - Mumbai"),
        DropdownOption("MH-02", "MH02 - Mumbai (Andheri)"),
        DropdownOption("MH-03", "MH03 - Mumbai (Wadala)"),
        DropdownOption("MH-04", "MH04 - Thane"),
        DropdownOption("MH-12", "MH12 - Pune"),
        DropdownOption("DL-01", "DL01 - Delhi North"),
        DropdownOption("DL-03", "DL03 - Delhi South"),
        DropdownOption("KA-01", "KA01 - Bangalore Central"),
        DropdownOption("KA-03", "KA03 - Bangalore East"),
        DropdownOption("GJ-01", "GJ01 - Ahmedabad"),
        DropdownOption("TN-01", "TN01 - Chennai Central"),
        DropdownOption("TS-09", "TS09 - Hyderabad Central")
    )

    val carModels = listOf(
        DropdownOption("KIA_SELTOS", "Kia Seltos"),
        DropdownOption("HONDA_AMAZE", "Honda Amaze"),
        DropdownOption("HONDA_CITY", "Honda City"),
        DropdownOption("MARUTI_SWIFT", "Maruti Suzuki Swift"),
        DropdownOption("MARUTI_BALENO", "Maruti Suzuki Baleno"),
        DropdownOption("HYUNDAI_CRETA", "Hyundai Creta"),
        DropdownOption("HYUNDAI_I20", "Hyundai i20"),
        DropdownOption("TATA_NEXON", "Tata Nexon"),
        DropdownOption("TATA_PUNCH", "Tata Punch"),
        DropdownOption("MAHINDRA_THAR", "Mahindra Thar")
    )

    val variantsByModel = mapOf(
        "KIA_SELTOS" to listOf(
            DropdownOption("SELTOS_PLUS_1.5_GTX_AT", "Plus 1.5 GTX AT - Diesel - (1493 CC)"),
            DropdownOption("SELTOS_1.5_HTX_MT", "1.5 HTX MT (1497CC)"),
            DropdownOption("SELTOS_1.5_GTX_DCT", "1.5 Turbo GTX+ DCT (1482CC)")
        ),
        "HONDA_AMAZE" to listOf(
            DropdownOption("1.2_S_CVT", "1.2 S CVT (1198CC)"),
            DropdownOption("1.2_V_CVT", "1.2 V CVT (i-VTEC)"),
            DropdownOption("1.2_VX_MT", "1.2 VX MT (1198CC)"),
            DropdownOption("1.2_E_MT", "1.2 E MT (1198CC)")
        ),
        "HONDA_CITY" to listOf(
            DropdownOption("1.5_V_CVT", "1.5 V CVT (1498CC)"),
            DropdownOption("1.5_VX_CVT", "1.5 VX CVT (1498CC)"),
            DropdownOption("1.5_ZX_CVT", "1.5 ZX CVT (1498CC)")
        ),
        "MARUTI_SWIFT" to listOf(
            DropdownOption("VXI_AMT", "VXI AMT (1197CC)"),
            DropdownOption("ZXI_AMT", "ZXI AMT (1197CC)"),
            DropdownOption("ZXI_PLUS", "ZXI Plus AMT (1197CC)")
        ),
        "MARUTI_BALENO" to listOf(
            DropdownOption("DELTA_AGS", "Delta AGS (1197CC)"),
            DropdownOption("ZETA_AGS", "Zeta AGS (1197CC)"),
            DropdownOption("ALPHA_AGS", "Alpha AGS (1197CC)")
        ),
        "HYUNDAI_CRETA" to listOf(
            DropdownOption("SX_TECH_IVT", "SX (O) Tech IVT (1497CC)"),
            DropdownOption("SX_PETROL_MT", "SX Petrol MT (1497CC)"),
            DropdownOption("SX_DIESEL_AT", "SX Diesel AT (1493CC)")
        )
    )

    val defaultVariants = listOf(
        DropdownOption("SELTOS_PLUS_1.5_GTX_AT", "Plus 1.5 GTX AT - Diesel - (1493 CC)"),
        DropdownOption("1.2_S_CVT", "1.2 S CVT (1198CC)"),
        DropdownOption("1.2_V_CVT", "1.2 V CVT (i-VTEC)"),
        DropdownOption("1.2_VX_MT", "1.2 VX MT (1198CC)"),
        DropdownOption("1.5_AUTOMATIC", "1.5 Automatic (1498CC)")
    )

    val fuelOptions = listOf(
        DropdownOption("DIESEL", "Diesel"),
        DropdownOption("PETROL", "Petrol"),
        DropdownOption("CNG", "CNG"),
        DropdownOption("ELECTRIC", "Electric")
    )

    val insurerOptions = listOf(
        DropdownOption("ORIENTAL", "ORIENTAL INSURANCE CO. LTD.", R.drawable.ic_acko_logo),
        DropdownOption("ACKO", "Acko General Insurance", R.drawable.ic_acko_logo),
        DropdownOption("TATA_AIG", "Tata AIG General Insurance", R.drawable.img_tata),
        DropdownOption("HDFC_ERGO", "HDFC ERGO General Insurance", R.drawable.ic_acko_logo),
        DropdownOption("ICICI_LOMBARD", "ICICI Lombard General Insurance", R.drawable.ic_acko_logo),
        DropdownOption("BAJAJ_ALLIANZ", "Bajaj Allianz General Insurance", R.drawable.ic_acko_logo),
        DropdownOption("NEW_INDIA", "The New India Assurance Co. Ltd.", R.drawable.ic_acko_logo)
    )

    val ncbOptions = listOf(
        DropdownOption("0", "0%"),
        DropdownOption("20", "20%"),
        DropdownOption("25", "25%"),
        DropdownOption("35", "35%"),
        DropdownOption("45", "45%"),
        DropdownOption("50", "50%")
    )

    val newCarPolicyTypes = listOf(
        "TP Only",
        "OD Only",
        "Comprehensive"
    )

    val existingCarPolicyTypes = listOf(
        "TP Only",
        "OD Only",
        "Comprehensive"
    )

    val months = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    val years = (2026 downTo 2005).map { it.toString() }
}
