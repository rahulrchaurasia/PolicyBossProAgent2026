package com.policyboss.policybosspro.feature.carInsurance.model

import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.ui.components.dropDown.DropdownOption

object CarInsuranceData {

    val rtoOptions = listOf(
        DropdownOption("MH-01", "Mumbai - MH01"),
        DropdownOption("MH-02", "Mumbai (Andheri) - MH02"),
        DropdownOption("MH-03", "Mumbai (Wadala) - MH03"),
        DropdownOption("MH-04", "Thane - MH04"),
        DropdownOption("MH-12", "Pune - MH12"),
        DropdownOption("DL-01", "Delhi North - DL01"),
        DropdownOption("DL-03", "Delhi South - DL03"),
        DropdownOption("KA-01", "Bangalore Central - KA01"),
        DropdownOption("KA-03", "Bangalore East - KA03"),
        DropdownOption("GJ-01", "Ahmedabad - GJ01"),
        DropdownOption("TN-01", "Chennai Central - TN01"),
        DropdownOption("TS-09", "Hyderabad Central - TS09")
    )

    val carModels = listOf(
        DropdownOption("HONDA_AMAZE", "Honda Amaze"),
        DropdownOption("HONDA_CITY", "Honda City"),
        DropdownOption("MARUTI_SWIFT", "Maruti Suzuki Swift"),
        DropdownOption("MARUTI_BALENO", "Maruti Suzuki Baleno"),
        DropdownOption("HYUNDAI_CRETA", "Hyundai Creta"),
        DropdownOption("HYUNDAI_I20", "Hyundai i20"),
        DropdownOption("TATA_NEXON", "Tata Nexon"),
        DropdownOption("TATA_PUNCH", "Tata Punch"),
        DropdownOption("KIA_SELTOS", "Kia Seltos"),
        DropdownOption("MAHINDRA_THAR", "Mahindra Thar")
    )

    val variantsByModel = mapOf(
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
        DropdownOption("1.2_S_CVT", "1.2 S CVT (1198CC)"),
        DropdownOption("1.2_V_CVT", "1.2 V CVT (i-VTEC)"),
        DropdownOption("1.2_VX_MT", "1.2 VX MT (1198CC)"),
        DropdownOption("1.5_AUTOMATIC", "1.5 Automatic (1498CC)")
    )

    val fuelOptions = listOf(
        DropdownOption("PETROL", "Petrol"),
        DropdownOption("DIESEL", "Diesel"),
        DropdownOption("CNG", "CNG"),
        DropdownOption("ELECTRIC", "Electric")
    )

    val insurerOptions = listOf(
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
        "TP (3 Yr)",
        "OD (1 Yr) + TP (3 Yr)"
    )

    val existingCarPolicyTypes = listOf(
        "Comprehensive",
        "TP"
    )
}
