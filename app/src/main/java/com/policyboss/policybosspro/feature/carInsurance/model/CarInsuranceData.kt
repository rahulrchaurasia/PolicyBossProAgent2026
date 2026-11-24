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

    // Master list of combined Make & Model items (matching screenshot reference)
    val brandModels: List<CarBrandModelItem> = listOf(
        CarBrandModelItem(makeId = "MARUTI", makeName = "MARUTI", modelId = "SWIFT", modelName = "SWIFT", isPopular = true),
        CarBrandModelItem(makeId = "MARUTI", makeName = "MARUTI", modelId = "WAGON_R", modelName = "WAGON R", isPopular = true),
        CarBrandModelItem(makeId = "MARUTI", makeName = "MARUTI", modelId = "EECO", modelName = "EECO", isPopular = true),
        CarBrandModelItem(makeId = "HYUNDAI", makeName = "HYUNDAI", modelId = "CRETA", modelName = "CRETA", isPopular = true),
        CarBrandModelItem(makeId = "MARUTI", makeName = "MARUTI", modelId = "ERTIGA", modelName = "ERTIGA", isPopular = true),
        CarBrandModelItem(makeId = "MARUTI", makeName = "MARUTI", modelId = "BALENO", modelName = "BALENO", isPopular = true),
        CarBrandModelItem(makeId = "ASHOK_LEYLAND", makeName = "ASHOK LEYLAND", modelId = "STILE", modelName = "STILE"),
        CarBrandModelItem(makeId = "ASTON_MARTIN", makeName = "ASTON MARTIN", modelId = "DB11", modelName = "DB11"),
        CarBrandModelItem(makeId = "ASTON_MARTIN", makeName = "ASTON MARTIN", modelId = "DB12", modelName = "DB12"),
        CarBrandModelItem(makeId = "ASTON_MARTIN", makeName = "ASTON MARTIN", modelId = "DB9", modelName = "DB9"),
        CarBrandModelItem(makeId = "ASTON_MARTIN", makeName = "ASTON MARTIN", modelId = "DBS", modelName = "DBS"),
        CarBrandModelItem(makeId = "ASTON_MARTIN", makeName = "ASTON MARTIN", modelId = "DBX", modelName = "DBX"),
        CarBrandModelItem(makeId = "ASTON_MARTIN", makeName = "ASTON MARTIN", modelId = "ONE_77", modelName = "ONE 77"),
        CarBrandModelItem(makeId = "ASTON_MARTIN", makeName = "ASTON MARTIN", modelId = "RAPIDE", modelName = "RAPIDE"),
        CarBrandModelItem(makeId = "ASTON_MARTIN", makeName = "ASTON MARTIN", modelId = "V12", modelName = "V12"),
        CarBrandModelItem(makeId = "KIA", makeName = "KIA", modelId = "SELTOS", modelName = "SELTOS", isPopular = true),
        CarBrandModelItem(makeId = "HONDA", makeName = "HONDA", modelId = "AMAZE", modelName = "AMAZE", isPopular = true),
        CarBrandModelItem(makeId = "HONDA", makeName = "HONDA", modelId = "CITY", modelName = "CITY", isPopular = true),
        CarBrandModelItem(makeId = "TATA", makeName = "TATA", modelId = "NEXON", modelName = "NEXON", isPopular = true),
        CarBrandModelItem(makeId = "TATA", makeName = "TATA", modelId = "PUNCH", modelName = "PUNCH", isPopular = true),
        CarBrandModelItem(makeId = "MAHINDRA", makeName = "MAHINDRA", modelId = "THAR", modelName = "THAR", isPopular = true)
    )

    // Master fuel types (matching screenshot reference)
    val fuelTypes: List<String> = listOf(
        "DIESEL",
        "PETROL",
        "EXTERNAL FITTED CNG",
        "EXTERNAL FITTED LPG",
        "ELECTRIC"
    )

    // Variants catalogue keyed by MAKE_MODEL
    val variantsByBrandModel: Map<String, List<CarVariantItem>> = mapOf(
        "HYUNDAI_CRETA" to listOf(
            CarVariantItem("CRETA_1.4_S_PLUS_DCT", "1.4 S PLUS TURBO DCT (1353CC)", "PETROL"),
            CarVariantItem("CRETA_1.4_S_PLUS_DCT_DT", "1.4 S PLUS TURBO DCT DUAL TONE (1353CC)", "PETROL"),
            CarVariantItem("CRETA_1.4_SX_O_DCT_DT", "1.4 SX (O) DCT DT TURBO (1353CC)", "PETROL"),
            CarVariantItem("CRETA_1.4_SX_O_DCT_TURBO", "1.4 SX (O) DCT TURBO (1353CC)", "PETROL"),
            CarVariantItem("CRETA_1.4_SX_DCT_DT", "1.4 SX DCT DT TURBO (1353CC)", "PETROL"),
            CarVariantItem("CRETA_1.4_SX_DCT_TURBO", "1.4 SX DCT TURBO (1353CC)", "PETROL"),
            CarVariantItem("CRETA_1.5_E", "1.5 E (1497CC)", "PETROL"),
            CarVariantItem("CRETA_1.5_EX", "1.5 EX (1497CC)", "PETROL"),
            CarVariantItem("CRETA_1.5_MPI_MT_E", "1.5 MPI MT E (1497CC)", "PETROL"),
            CarVariantItem("CRETA_1.5_S", "1.5 S (1497CC)", "PETROL"),
            CarVariantItem("CRETA_1.5_S_PLUS_MT_DT_KNIGHT", "1.5 S PLUS MT DUAL TONE KNIGHT (1497CC)", "PETROL"),
            CarVariantItem("CRETA_1.5_S_PLUS_MT_KNIGHT", "1.5 S PLUS MT KNIGHT (1497CC)", "PETROL"),
            CarVariantItem("CRETA_1.5_SX", "1.5 SX (1497CC)", "PETROL"),
            CarVariantItem("CRETA_1.5_SX_O_CVT", "1.5 SX (O) CVT (1497CC)", "PETROL"),
            CarVariantItem("CRETA_1.5_CRDI_SX_AT", "1.5 CRDI SX AT (1493CC)", "DIESEL"),
            CarVariantItem("CRETA_1.5_CRDI_SX_O_AT", "1.5 CRDI SX (O) AT (1493CC)", "DIESEL"),
            CarVariantItem("CRETA_1.5_CRDI_S", "1.5 CRDI S (1493CC)", "DIESEL"),
            CarVariantItem("CRETA_1.5_CNG", "1.5 SX CNG (1497CC)", "EXTERNAL FITTED CNG")
        ),
        "KIA_SELTOS" to listOf(
            CarVariantItem("SELTOS_PLUS_1.5_GTX_AT", "Plus 1.5 GTX AT (1493CC)", "DIESEL"),
            CarVariantItem("SELTOS_1.5_GTX_AT", "1.5 GTX AT (1493CC)", "DIESEL"),
            CarVariantItem("SELTOS_1.5_HTX_MT", "1.5 HTX MT (1497CC)", "PETROL"),
            CarVariantItem("SELTOS_1.5_GTX_DCT", "1.5 Turbo GTX+ DCT (1482CC)", "PETROL"),
            CarVariantItem("SELTOS_1.5_HTE_MT", "1.5 HTE MT (1497CC)", "PETROL"),
            CarVariantItem("SELTOS_1.5_CRDI_HTK", "1.5 CRDI HTK (1493CC)", "DIESEL"),
            CarVariantItem("SELTOS_CNG_FIT", "1.5 HTE CNG (1497CC)", "EXTERNAL FITTED CNG")
        ),
        "HONDA_AMAZE" to listOf(
            CarVariantItem("AMAZE_1.2_S_CVT", "1.2 S CVT (1198CC)", "PETROL"),
            CarVariantItem("AMAZE_1.2_V_CVT", "1.2 V CVT (i-VTEC)", "PETROL"),
            CarVariantItem("AMAZE_1.2_VX_MT", "1.2 VX MT (1198CC)", "PETROL"),
            CarVariantItem("AMAZE_1.2_E_MT", "1.2 E MT (1198CC)", "PETROL"),
            CarVariantItem("AMAZE_1.5_V_MT", "1.5 V MT (1498CC)", "DIESEL"),
            CarVariantItem("AMAZE_1.5_VX_CVT", "1.5 VX CVT (1498CC)", "DIESEL"),
            CarVariantItem("AMAZE_CNG", "1.2 S CNG (1198CC)", "EXTERNAL FITTED CNG")
        ),
        "MARUTI_SWIFT" to listOf(
            CarVariantItem("SWIFT_LXI", "LXI (1197CC)", "PETROL"),
            CarVariantItem("SWIFT_VXI", "VXI (1197CC)", "PETROL"),
            CarVariantItem("SWIFT_VXI_AMT", "VXI AMT (1197CC)", "PETROL"),
            CarVariantItem("SWIFT_ZXI", "ZXI (1197CC)", "PETROL"),
            CarVariantItem("SWIFT_ZXI_AMT", "ZXI AMT (1197CC)", "PETROL"),
            CarVariantItem("SWIFT_ZXI_PLUS", "ZXI Plus AMT (1197CC)", "PETROL"),
            CarVariantItem("SWIFT_VXI_CNG", "VXI CNG (1197CC)", "EXTERNAL FITTED CNG"),
            CarVariantItem("SWIFT_ZXI_CNG", "ZXI CNG (1197CC)", "EXTERNAL FITTED CNG")
        ),
        "MARUTI_WAGON_R" to listOf(
            CarVariantItem("WAGON_R_LXI", "LXI 1.0 (998CC)", "PETROL"),
            CarVariantItem("WAGON_R_VXI", "VXI 1.0 (998CC)", "PETROL"),
            CarVariantItem("WAGON_R_ZXI", "ZXI 1.2 (1197CC)", "PETROL"),
            CarVariantItem("WAGON_R_LXI_CNG", "LXI CNG (998CC)", "EXTERNAL FITTED CNG"),
            CarVariantItem("WAGON_R_VXI_CNG", "VXI CNG (998CC)", "EXTERNAL FITTED CNG")
        ),
        "HONDA_CITY" to listOf(
            CarVariantItem("CITY_1.5_V_CVT", "1.5 V CVT (1498CC)", "PETROL"),
            CarVariantItem("CITY_1.5_VX_CVT", "1.5 VX CVT (1498CC)", "PETROL"),
            CarVariantItem("CITY_1.5_ZX_CVT", "1.5 ZX CVT (1498CC)", "PETROL"),
            CarVariantItem("CITY_1.5_V_MT", "1.5 V MT (1498CC)", "PETROL"),
            CarVariantItem("CITY_HYBRID_ZX", "e:HEV ZX Hybrid (1498CC)", "PETROL")
        )
    )

    fun getVariantsFor(makeId: String, modelId: String, fuelType: String? = null): List<CarVariantItem> {
        val key = "${makeId}_${modelId}".uppercase()
        val allVariants = variantsByBrandModel[key] ?: run {
            // Generative fallback for any other Make/Model
            listOf(
                CarVariantItem("${key}_BASE", "Base MT (1198CC)", "PETROL"),
                CarVariantItem("${key}_MID", "Mid V MT (1198CC)", "PETROL"),
                CarVariantItem("${key}_TOP", "Top ZX AT (1498CC)", "PETROL"),
                CarVariantItem("${key}_DIESEL_MT", "Diesel SX MT (1493CC)", "DIESEL"),
                CarVariantItem("${key}_DIESEL_AT", "Diesel SX (O) AT (1493CC)", "DIESEL"),
                CarVariantItem("${key}_CNG", "CNG SX (1198CC)", "EXTERNAL FITTED CNG")
            )
        }
        return if (!fuelType.isNullOrBlank()) {
            val normalized = fuelType.trim().uppercase()
            val filtered = allVariants.filter {
                it.fuelType.equals(normalized, ignoreCase = true) ||
                        (normalized.contains("CNG") && it.fuelType.contains("CNG")) ||
                        (normalized.contains("LPG") && it.fuelType.contains("LPG")) ||
                        (normalized.contains("DIESEL") && it.fuelType.contains("DIESEL")) ||
                        (normalized.contains("PETROL") && it.fuelType.contains("PETROL"))
            }
            if (filtered.isNotEmpty()) filtered else allVariants
        } else {
            allVariants
        }
    }

    // Legacy compatibility options retained for non-car specs (RTO, Insurers, etc.)
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
            DropdownOption("SELTOS_PLUS_1.5_GTX_AT", "PLUS 1.5 GTX AT (1493 CC)"),
            DropdownOption("SELTOS_1.5_ANNIVERSARY", "1.5 ANNIVERSARY ED 1493 CC"),
            DropdownOption("SELTOS_1.5_GTX_PLUS_DT", "1.5 GTX PLUS AT DUAL TONE 1493 CC"),
            DropdownOption("SELTOS_1.5_GTX_PLUS_S", "1.5 GTX PLUS S AT 1493 CC"),
            DropdownOption("SELTOS_1.5_HTE", "1.5 HTE 1493 CC"),
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
