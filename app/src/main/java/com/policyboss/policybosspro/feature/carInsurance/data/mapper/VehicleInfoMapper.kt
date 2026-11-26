package com.policyboss.policybosspro.feature.carInsurance.data.mapper

import com.policyboss.policybosspro.feature.carInsurance.data.model.FuelVariantResponseDto
import com.policyboss.policybosspro.feature.carInsurance.data.model.PreferredMakeModelDto
import com.policyboss.policybosspro.feature.carInsurance.data.model.VehicleInfoResponseDto
import com.policyboss.policybosspro.feature.carInsurance.data.model.VehicleModelItemDto
import com.policyboss.policybosspro.feature.carInsurance.model.CarBrandModelItem
import com.policyboss.policybosspro.feature.carInsurance.model.CarDetailsStep
import com.policyboss.policybosspro.feature.carInsurance.model.CarEntryMode
import com.policyboss.policybosspro.feature.carInsurance.model.CarVariantItem
import com.policyboss.policybosspro.feature.carInsurance.model.FuelVariantCatalog
import com.policyboss.policybosspro.feature.carInsurance.model.NewCarUiState
import com.policyboss.policybosspro.feature.carInsurance.model.VehicleInfoDomain
import com.policyboss.policybosspro.feature.carInsurance.model.VehicleModel
import com.policyboss.policybosspro.feature.carInsurance.model.VehiclePremiumData
import com.policyboss.policybosspro.ui.components.dropDown.DropdownOption
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

// =====================================================================
// Vehicle info: DTO -> Domain
// =====================================================================

private val FAILURE_STATUS_KEYWORDS = listOf("fail", "error", "false", "not found", "invalid", "no record")

/**
 * True only when the response carries a usable vehicle: a non-failure Result/status and
 * the Make + Model needed to price a quote.
 */
fun VehicleInfoResponseDto.isVehicleFound(): Boolean {
    val statusText = listOfNotNull(status, result).joinToString(" ").lowercase(Locale.ENGLISH)
    if (FAILURE_STATUS_KEYWORDS.any { statusText.contains(it) }) return false
    return !makeName.isNullOrBlank() && !modelName.isNullOrBlank()
}

/**
 * Maps the raw response to [VehicleInfoDomain], preferring FastlaneResponse_Obj values for
 * the vahan banner/owner and falling back to the top-level fields, and normalising dates.
 *
 * @param enteredRegistrationNumber the number typed by the user, used when the response omits it.
 */
fun VehicleInfoResponseDto.toDomain(enteredRegistrationNumber: String): VehicleInfoDomain {
    val fastlane = fastlaneResponse

    val vahanMaker = fastlane?.makerDescription.orBlank().ifBlank { vahanMake.orBlank() }
    val vahanModelName = fastlane?.makerModel.orBlank().ifBlank { vahanModel.orBlank() }
    val vahanFuelName = fastlane?.fuelType.orBlank().ifBlank { vahanFuel.orBlank() }
    val vahanCc = normalizeCubicCapacity(
        fastlane?.cubicCapacity.orBlank().ifBlank { vahanCubicCapacity.orBlank() }
    )
    val vahanParts = listOf(vahanMaker, vahanModelName, vahanFuelName).filter { it.isNotBlank() }
    val vahanSummary = if (vahanParts.isEmpty()) {
        ""
    } else {
        vahanParts.joinToString(", ") + if (vahanCc.isNotBlank()) " ($vahanCc CC)" else ""
    }

    val (mfgMonth, mfgYear) = resolveManufacturing(
        month = manufactureMonth,
        year = manufactureYear,
        fallback = fastlane?.manufacturingDate
    )

    // Previous policy comes only from the top-level Pyp_* fields (not FastlaneResponse_Obj)
    val insurerName = pypInsurerName.orBlank()
    val expiry = if (insurerName.isBlank()) "" else VehicleDateFormatter.toDisplayDate(pypPolicyExpiryDate)

    return VehicleInfoDomain(
        registrationNumber = registrationNumber.orBlank().ifBlank { enteredRegistrationNumber }
            .compactRegistration(),
        vahanSummary = vahanSummary,
        ownerName = fastlane?.ownerName.orBlank().ifBlank { ownerName.orBlank() },
        makeName = makeName.orBlank(),
        modelName = modelName.orBlank(),
        variantName = variantName.orBlank(),
        fuelType = fuelType.orBlank(),
        modelId = modelId.orBlank(),
        variantId = variantId.orBlank(),
        fuelId = fuelId.orBlank(),
        cubicCapacity = normalizeCubicCapacity(cubicCapacity.orBlank().ifBlank { vahanCubicCapacity.orBlank() }),
        rtoCode = rtoCode.orBlank(),
        rtoName = rtoName.orBlank(),
        registrationDate = VehicleDateFormatter.toDisplayDate(registrationDate)
            .ifBlank { VehicleDateFormatter.toDisplayDate(fastlane?.registrationDate) },
        manufacturingMonth = mfgMonth,
        manufacturingYear = mfgYear,
        previousInsurerId = if (insurerName.isBlank()) "" else pypInsurerId.orBlank().takeIf { it != "0" }.orEmpty(),
        previousInsurerName = insurerName,
        policyExpiryDate = expiry,
        vehicleDetailId = vehicleDetailId.orBlank(),
        premiumData = toPremiumData(mfgMonth, mfgYear)
    )
}

private fun VehicleInfoResponseDto.toPremiumData(mfgMonth: String, mfgYear: String): VehiclePremiumData {
    val fastlane = fastlaneResponse
    val vahanCc = normalizeCubicCapacity(vahanCubicCapacity.orBlank().ifBlank { fastlane?.cubicCapacity.orBlank() })
    val matchCc = normalizeCubicCapacity(cubicCapacity.orBlank())

    val vahanFull = listOf(
        vahanMake.orBlank().ifBlank { fastlane?.makerDescription.orBlank() },
        vahanModel.orBlank().ifBlank { fastlane?.makerModel.orBlank() },
        vahanFuel.orBlank().ifBlank { fastlane?.fuelType.orBlank() },
        if (vahanCc.isNotBlank()) "${vahanCc}CC" else ""
    ).takeIf { parts -> parts.any { it.isNotBlank() } }?.joinToString("|").orEmpty()

    val matchVehicleFull = listOf(
        makeName.orBlank(),
        modelName.orBlank(),
        variantName.orBlank(),
        fuelType.orBlank(),
        if (matchCc.isNotBlank()) "$matchCc CC" else ""
    ).takeIf { parts -> parts.any { it.isNotBlank() } }?.joinToString("|").orEmpty()

    val statusText = listOfNotNull(status, result).joinToString(" ")
    val registrationApi = VehicleDateFormatter.toApiDate(registrationDate)
        .ifBlank { VehicleDateFormatter.toApiDate(fastlane?.registrationDate) }

    return VehiclePremiumData(
        registrationDateApi = registrationApi,
        manufactureDateApi = manufactureApiDate(fastlane?.manufacturingDateFormatted, mfgMonth, mfgYear),
        policyExpiryDateApi = VehicleDateFormatter.toApiDate(pypPolicyExpiryDate),
        previousPolicyNumber = pypPolicyNumber.orBlank(),
        chassisNumber = chassisNumber.orBlank(),
        engineNumber = engineNumber.orBlank(),
        permanentPincode = permanentPincode.orBlank(),
        vahanCcSlab = vahanCcSlab.orBlank(),
        vahanSource = source.orBlank(),
        vahanCubicCapacity = vahanCc,
        vahanFull = vahanFull,
        matchVehicleFull = matchVehicleFull,
        isFastlaneRto = statusText.contains("success", ignoreCase = true) && !rtoCode.isNullOrBlank(),
        isFastlaneManufacture = normalizeYear(manufactureYear).isNotBlank(),
        isFastlaneRegistration = registrationApi.isNotBlank()
    )
}

/**
 * `2011-09` (FastlaneResponse_Obj.manufacturing_date_formatted) -> `2011-09-01`; falls back
 * to the resolved Manufacture_Month/Year (`Sep`/`2011`).
 */
internal fun manufactureApiDate(formatted: String?, month: String, year: String): String {
    val match = Regex("^(\\d{4})-(\\d{1,2})").find(formatted?.trim().orEmpty())
    if (match != null) {
        val monthNumber = match.groupValues[2].toInt()
        if (monthNumber in 1..12) return "%s-%02d-01".format(Locale.ENGLISH, match.groupValues[1], monthNumber)
    }
    val monthIndex = MONTHS.indexOf(normalizeMonth(month))
    val normalizedYear = normalizeYear(year)
    if (monthIndex < 0 || normalizedYear.isBlank()) return ""
    return "%s-%02d-01".format(Locale.ENGLISH, normalizedYear, monthIndex + 1)
}

// =====================================================================
// Vehicle info: Domain -> NewCarUiState
// =====================================================================

/**
 * Applies the looked-up vehicle onto [baseState] (the freshly reset WITH_CAR_NUMBER form).
 *
 * If any value the WITH_CAR_NUMBER form locks is missing, the state is switched to
 * WITHOUT_CAR_NUMBER so the user can still complete those fields; everything that was
 * found stays pre-filled.
 */
fun VehicleInfoDomain.toNewCarUiState(baseState: NewCarUiState): NewCarUiState {
    val rtoOption = toRtoOption()
    val insurerOption = toInsurerOption()
    val entryMode = if (hasAllLockedFields) CarEntryMode.WITH_CAR_NUMBER else CarEntryMode.WITHOUT_CAR_NUMBER

    return baseState.copy(
        entryMode = entryMode,
        headerTitle = if (entryMode == CarEntryMode.WITH_CAR_NUMBER) {
            "Continue with Car Number"
        } else {
            "Continue without car number"
        },
        registrationNumber = registrationNumber,
        vahanData = vahanSummary,
        vahanDetails = vahanSummary,
        showVahanBanner = vahanSummary.isNotBlank(),
        carDetailsSummary = carDetailsSummary,

        customerName = ownerName,
        customerNameError = null,

        make = makeName,
        makeId = makeName,
        model = modelName,
        modelId = modelId.ifBlank { modelName },
        fuel = fuelType,
        fuelId = fuelId.ifBlank { fuelType },
        variant = variantName,
        variantId = variantId.ifBlank { variantName },
        cubicCapacity = cubicCapacity,
        selectedModel = DropdownOption(modelId.ifBlank { modelName }, modelName),
        selectedVariant = variantName.takeIf { it.isNotBlank() }
            ?.let { DropdownOption(variantId.ifBlank { it }, it) },
        selectedFuel = fuelType.takeIf { it.isNotBlank() }
            ?.let { DropdownOption(fuelId.ifBlank { it }, it) },
        isCarDetailsSheetOpen = false,
        sheetStep = CarDetailsStep.MAKE_MODEL,
        draftBrandModel = null,
        draftFuel = null,
        draftVariant = null,
        sheetSearchQuery = "",

        selectedRto = rtoOption,
        rtoOptions = rtoOption?.let { option ->
            if (baseState.rtoOptions.any { it.value == option.value }) baseState.rtoOptions
            else listOf(option) + baseState.rtoOptions
        } ?: baseState.rtoOptions,

        registrationDate = registrationDate,
        manufacturingMonth = manufacturingMonth,
        manufacturingYear = manufacturingYear,
        manufacturingDate = if (manufacturingMonth.isNotBlank() && manufacturingYear.isNotBlank()) {
            "01/$manufacturingMonth/$manufacturingYear"
        } else {
            ""
        },

        hasExistingPolicy = hasExistingPolicy,
        policyExpiryDate = if (hasExistingPolicy) policyExpiryDate else "",
        // Field shows Pyp_Insurer_Name as returned; the sheet list stays the getPrevInsurer master
        selectedInsurer = insurerOption
    )
}

/** `DropdownOption("579", "MH01BB0197-Mumbai-Tardeo")` */
internal fun VehicleInfoDomain.toRtoOption(): DropdownOption<String>? {
    if (rtoCode.isBlank() && rtoName.isBlank()) return null
    val label = listOf(registrationNumber, rtoName).filter { it.isNotBlank() }.joinToString("-")
    return DropdownOption(value = rtoCode.ifBlank { rtoName }, label = label)
}

/**
 * `DropdownOption("17", "SBI GENERAL INS CO LTD")`: Pyp_Insurer_Id as the value (so the
 * matching getPrevInsurer row is highlighted in the sheet) and Pyp_Insurer_Name, exactly as
 * returned, as the displayed label.
 */
internal fun VehicleInfoDomain.toInsurerOption(): DropdownOption<String>? {
    if (!hasExistingPolicy) return null
    return DropdownOption(value = previousInsurerId.ifBlank { previousInsurerName }, label = previousInsurerName)
}

// =====================================================================
// Model list: DTO -> Domain -> sheet items
// =====================================================================

fun VehicleModelItemDto.toDomain(): VehicleModel? {
    val id = modelId ?: return null
    val make = makeName.orBlank()
    val model = modelName.orBlank()
    if (make.isBlank() || model.isBlank()) return null
    return VehicleModel(
        modelId = id.toString(),
        makeName = make,
        modelName = model,
        vehicleName = vehicleName.orBlank().ifBlank { "$make $model" }
    )
}

/** Unique makes (by Make_Name) with their models, both sorted alphabetically. */
fun List<VehicleModel>.groupByMake(): Map<String, List<VehicleModel>> =
    distinctBy { it.modelId }
        .groupBy { it.makeName.trim().uppercase(Locale.ENGLISH) }
        .toSortedMap()
        .mapValues { (_, models) -> models.sortedBy { it.modelName.uppercase(Locale.ENGLISH) } }

/** Flattens the grouped catalogue into the bottom sheet's Make + Model cards. */
fun List<VehicleModel>.toBrandModelItems(): List<CarBrandModelItem> =
    groupByMake().flatMap { (make, models) ->
        models.map { model ->
            CarBrandModelItem(
                makeId = make,
                makeName = make,
                modelId = model.modelId,
                modelName = model.modelName,
                vehicleName = model.vehicleName
            )
        }
    }

fun PreferredMakeModelDto.toDomain(): VehicleModel? {
    val id = modelId?.trim().orEmpty()
    val make = makeName.orBlank()
    val model = modelName.orBlank()
    if (id.isBlank() || id == "0" || make.isBlank() || model.isBlank()) return null
    return VehicleModel(modelId = id, makeName = make, modelName = model, vehicleName = "$make, $model")
}

private fun VehicleModel.toBrandModelItem(isPreferred: Boolean) = CarBrandModelItem(
    makeId = makeName.trim().uppercase(Locale.ENGLISH),
    makeName = makeName.trim().uppercase(Locale.ENGLISH),
    modelId = modelId,
    modelName = modelName,
    isPopular = isPreferred,
    vehicleName = vehicleName
)

/**
 * Sheet order for step 1 (Make & Model):
 * 1. Preferred models (get_preferred/make_model), in API order.
 * 2. The full model_list catalogue, excluding Model_IDs already shown (no duplicates).
 *
 * When [priorityMake] is known (e.g. "MARUTI" from vehicle_info_user), that make is
 * surfaced first: its preferred models, then its remaining catalogue models, then the
 * other preferred models, then everything else.
 */
fun orderBrandModels(
    preferred: List<VehicleModel>,
    catalogue: List<VehicleModel>,
    priorityMake: String? = null
): List<CarBrandModelItem> {
    val preferredItems = preferred.distinctBy { it.modelId }.map { it.toBrandModelItem(isPreferred = true) }
    val preferredIds = preferredItems.mapTo(HashSet()) { it.modelId }
    val catalogueItems = catalogue.filterNot { it.modelId in preferredIds }.toBrandModelItems()

    val make = priorityMake?.trim().orEmpty()
    if (make.isBlank()) return preferredItems + catalogueItems

    fun CarBrandModelItem.isMake() = makeName.equals(make, ignoreCase = true)
    return preferredItems.filter { it.isMake() } +
        catalogueItems.filter { it.isMake() } +
        preferredItems.filterNot { it.isMake() } +
        catalogueItems.filterNot { it.isMake() }
}

/** Maps beta_GetFuelVariant into fuels (API order) and usable variants. */
fun FuelVariantResponseDto.toCatalog(requestedModelId: String): FuelVariantCatalog {
    val variants = variantList.mapNotNull { dto ->
        val id = dto.vehicleId?.trim().orEmpty()
        val name = dto.variantName.orBlank()
        if (id.isBlank() || name.isBlank()) return@mapNotNull null
        CarVariantItem(
            variantId = id,
            variantName = name,
            fuelType = dto.fuelName.orBlank(),
            modelId = dto.modelId.orBlank().ifBlank { requestedModelId },
            fuelId = dto.fuelId.orBlank(),
            cubicCapacity = normalizeCubicCapacity(dto.cubicCapacity.orBlank()),
            seatingCapacity = dto.seatingCapacity.orBlank(),
            exShowroomPrice = dto.exShowroomPrice.orBlank()
        )
    }.distinctBy { it.variantId }
    val fuels = fuelList.mapNotNull { it?.trim()?.takeIf { fuel -> fuel.isNotBlank() } }
        .distinctBy { it.uppercase(Locale.ENGLISH) }
    return FuelVariantCatalog(modelId = requestedModelId, fuels = fuels, variants = variants)
}

/** `MARUTI-WAGON R-1.0 LXI ABS-PETROL- (998 CC)` */
fun buildCarDetailsSummary(
    makeName: String,
    modelName: String,
    variantName: String,
    fuelType: String,
    cubicCapacity: String
): String {
    val base = listOf(makeName, modelName, variantName, fuelType)
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .joinToString("-")
    return if (cubicCapacity.isNotBlank()) "$base- ($cubicCapacity CC)" else base
}

// =====================================================================
// Helpers
// =====================================================================

/**
 * Converts the date formats returned by the vahan API (`18/10/2011`, `2011-10-18`, ...)
 * into the form's `dd-MMM-yyyy` (`18-Oct-2011`). Returns blank for missing/invalid input.
 */
object VehicleDateFormatter {
    private const val DISPLAY_PATTERN = "dd-MMM-yyyy"
    private val INPUT_PATTERNS = listOf(
        "dd/MM/yyyy",
        "yyyy-MM-dd",
        "dd-MM-yyyy",
        "yyyy/MM/dd",
        "dd-MMM-yyyy",
        "dd MMM yyyy",
        "yyyy-MM-dd'T'HH:mm:ss"
    )

    private const val API_PATTERN = "yyyy-MM-dd"

    fun toDisplayDate(raw: String?): String = format(raw, DISPLAY_PATTERN)

    /** Any supported input (`18/10/2011`, `18-Oct-2011`, ...) -> `2011-10-18`; blank if invalid. */
    fun toApiDate(raw: String?): String = format(raw, API_PATTERN)

    private fun format(raw: String?, outputPattern: String): String {
        val value = raw?.trim().orEmpty()
        if (value.isBlank()) return ""
        for (pattern in INPUT_PATTERNS) {
            val parser = SimpleDateFormat(pattern, Locale.ENGLISH).apply { isLenient = false }
            val date = try {
                parser.parse(value)
            } catch (_: ParseException) {
                null
            } ?: continue
            return SimpleDateFormat(outputPattern, Locale.ENGLISH).format(date)
        }
        return ""
    }
}

private val MONTHS = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

/** `Sep`, `SEP`, `September`, `9`, `09` -> `Sep`; anything else -> blank. */
internal fun normalizeMonth(raw: String?): String {
    val value = raw?.trim().orEmpty()
    if (value.isBlank()) return ""
    value.toIntOrNull()?.let { number -> return MONTHS.getOrNull(number - 1).orEmpty() }
    return MONTHS.firstOrNull { value.startsWith(it, ignoreCase = true) }.orEmpty()
}

internal fun normalizeYear(raw: String?): String {
    val value = raw?.trim().orEmpty()
    return if (value.length == 4 && value.all { it.isDigit() }) value else ""
}

/**
 * Manufacture_Month/Year first; otherwise FastlaneResponse_Obj.manufacturing_date
 * (`9/2011`, `09/2011`, `2011-09`). Returns blanks when nothing valid is available.
 */
internal fun resolveManufacturing(month: String?, year: String?, fallback: String?): Pair<String, String> {
    val primaryMonth = normalizeMonth(month)
    val primaryYear = normalizeYear(year)
    if (primaryMonth.isNotBlank() && primaryYear.isNotBlank()) return primaryMonth to primaryYear

    val parts = fallback?.trim().orEmpty().split('/', '-').map { it.trim() }.filter { it.isNotBlank() }
    if (parts.size == 2) {
        val (first, second) = parts
        val (fallbackMonth, fallbackYear) = if (first.length == 4) second to first else first to second
        val m = normalizeMonth(fallbackMonth)
        val y = normalizeYear(fallbackYear)
        if (m.isNotBlank() && y.isNotBlank()) return m to y
    }
    return primaryMonth to primaryYear
}

/** `998`, `998.0`, `998 CC` -> `998`. */
internal fun normalizeCubicCapacity(raw: String): String {
    val number = Regex("\\d+(\\.\\d+)?").find(raw)?.value ?: return ""
    val asDouble = number.toDoubleOrNull() ?: return ""
    return if (asDouble % 1.0 == 0.0) asDouble.toLong().toString() else number
}

private fun String?.orBlank(): String = this?.trim().orEmpty()

internal fun String.compactRegistration(): String =
    filter { it.isLetterOrDigit() }.uppercase(Locale.ENGLISH)

