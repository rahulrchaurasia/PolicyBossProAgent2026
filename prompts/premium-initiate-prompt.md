# Prompt: integrate `premium_initiate` into MotorQuoteResultsScreen

Act as a Senior Android Engineer (Kotlin, Jetpack Compose, Material 3, MVVM, Hilt, StateFlow,
kotlinx.serialization, Retrofit). Follow the existing car-insurance architecture exactly. Do not
introduce new libraries, a new ViewModel, or a new navigation graph.

## Goal

Call `POST https://api.policyboss.com:5443/quote/premium_initiate` when the user taps **Get Quotes**
(replacing the dummy `car-insurance/quote` call for the WITH_CAR_NUMBER flow), build its request from
the already-fetched `quote/vehicle_info_user` response plus the form state, and bind the response to
`MotorQuoteResultsScreen`. Only a few screen fields are bound now; the rest stays on the existing
model/defaults until the follow-up APIs are given.

## Existing code to build on (read these first)

Base package: `app/src/main/java/com/policyboss/policybosspro/`

| Layer | File | Notes |
|---|---|---|
| API | `feature/carInsurance/data/api/CarInsuranceApi.kt` | Full-URL constants in `companion object` built from `NetworkConstants.QUOTE_API_BASE_URL`. Add `PREMIUM_INITIATE_URL = QUOTE_API_BASE_URL + "quote/premium_initiate"` the same way. |
| Request model | `feature/carInsurance/data/model/VehicleInfoRequest.kt` | Pattern for `@Serializable` + `@SerialName`, keys from `NetworkConstants.SECRET_KEY` / `CLIENT_KEY`. Never hard-code the keys. |
| Response DTO | `feature/carInsurance/data/model/VehicleInfoResponseDto.kt` | All scalars use `LenientNullableStringSerializer`; `FastlaneResponse_Obj` is nested. |
| Mapper | `feature/carInsurance/data/mapper/VehicleInfoMapper.kt` | `toDomain()`, `compactRegistration()`, `buildCarDetailsSummary()`. |
| Domain | `feature/carInsurance/model/VehicleInfoDomain.kt` | `@Immutable`, blank-not-null fields. |
| Repository | `feature/carInsurance/data/repository/CarInsuranceRepository.kt` | `open suspend fun …(): Resource<T>`, `withContext(Dispatchers.IO)`, rethrow `CancellationException`, `NetworkError.fromThrowable(e)`, debug logging via `VehicleApiLogger`. `appDataManager.getSsId()` is already injected. |
| ViewModel | `feature/carInsurance/viewmodel/NewCarViewModel.kt` | Shared by form, loading and results screens. `startVehicleLookup()` (line ~199) receives `VehicleInfoDomain`; `handleGetQuotes()` (line ~1169) builds the request and drives `QuoteFetchState` + `QuoteLoadingUiEvent.NavigateToQuotes`; `ensureQuotesLoaded()` guards the results screen. |
| UI state | `feature/carInsurance/model/NewCarUiState.kt` | `ownershipType` (`OwnershipType.INDIVIDUAL/COMPANY`), `policyType` (`PolicyType`), `selectedNcb`, `policyExpiryDate`, `selectedTpInsurer`, `quotes`, `crnNumber`, `vehicleSummaryText`. |
| Results UI | `feature/carInsurance/ui/carQuoteResult/ui/MotorQuoteResultsRoute.kt`, `MotorQuoteResultsScreen.kt`, components in `ui/carQuoteResult/component/` (`QuoteSummaryCard`, `VahanDataCard`, `InsurerQuoteCard`, `QuoteValidityBanner`). |
| App version | `utils/AppVersionProvider.kt` | Inject and use `getAppVersion()` for `app_version`. |
| Tests | `app/src/test/java/.../feature/carInsurance/` | `CarInsuranceTestFakes.kt`, `NewCarQuoteLoadingTest.kt`, `MotorQuoteResultsTest.kt`, `VehicleInfoIntegrationTest.kt`. |

## Step 1 — keep the full vahan response

The premium request needs fields the DTO/domain do not carry yet.

1. Add to `VehicleInfoResponseDto` (lenient serializer, nullable): `Chassis_Number`, `Engin_Number`,
   `Permanent_Pincode`, `Vahan_CC_Slab`, `Source`, `Purchase_Date`.
   Add `manufacturing_date_formatted` to the nested FastlaneResponse_Obj DTO.
2. Add matching fields to `VehicleInfoDomain` plus raw/API-format values the form format loses:
   `registrationDateApi` (`yyyy-MM-dd`), `manufactureDateApi` (`yyyy-MM-01`),
   `policyExpiryDateApi` (`yyyy-MM-dd`), `chassisNumber`, `engineNumber`, `permanentPincode`,
   `vahanCcSlab`, `vahanSource`, `vahanCubicCapacity`, `vahanFull`, `matchVehicleFull`,
   `isFastlaneSuccess`. Map them in `VehicleInfoMapper.toDomain()`.
3. In `NewCarViewModel`, store the successful `VehicleInfoDomain` (e.g. `private var vehicleInfo:
   VehicleInfoDomain?`, cleared on cancel/reset/mode change) so `handleGetQuotes` can use it.

## Step 2 — request model `PremiumInitiateRequest`

Create `feature/carInsurance/data/model/PremiumInitiateRequest.kt` (`@Serializable`, every key via
`@SerialName`, constants as defaults in a `companion object` like `VehicleInfoRequest`). Keep the
JSON types exactly as below (some values are strings, some numbers).

### Mapping

| Key | Source |
|---|---|
| `product_id` | `1` (`VehicleInfoRequest.CAR_PRODUCT_ID`) |
| `vehicle_id` | `Variant_Id` (Int) |
| `rto_id` | `RTO_Code` (Int) |
| `vehicle_insurance_type` | `"renew"` |
| `vehicle_registration_date` | `Registration_Date` `dd/MM/yyyy` → `yyyy-MM-dd` |
| `vehicle_manf_date` | `FastlaneResponse_Obj.manufacturing_date_formatted` (`2011-09`) + `"-01"` → `2011-09-01`; fall back to form `manufacturingYear`/`manufacturingMonth` |
| `policy_expiry_date` | `Pyp_Policy_Expiry_Date` `dd/MM/yyyy` → `yyyy-MM-dd`; if the user edited the expiry date on the form, use the form value |
| `prev_insurer_id` | `Pyp_Insurer_Id` (Int); prefer the form's `selectedInsurer.value` if changed |
| `vehicle_registration_type` | `OwnershipType.INDIVIDUAL` → `"individual"`, `COMPANY` → `"corporate"` (from `RegisteredOnRadioGroup`) |
| `vehicle_ncb_current` | `selectedNcb.value` (`"0"`, `"20"`, `"25"` …) as String |
| `is_claim_exists` | from policy expiry date vs today (`LocalDate.now()`): past → `"yes"`, today/future → `"no"` |
| `is_breakin` | past → `"yes"`, today/future → `"no"` |
| `is_policy_exist` | past → `"yes"`, today/future → `"no"` (as specified — see open questions) |
| `is_ownership_transfer` | `"no"` |
| `method_type` | `"Premium"` |
| `execution_async` | `"yes"` |
| `electrical_accessory`, `non_electrical_accessory` | `0` |
| `is_fastlane_rto` | `"yes"` when vahan `status == "Success"` and `RTO_Code` present, else `"no"` |
| `registration_no` | `Registration_Number` formatted `MH-01-BB-0197` (add a mapper helper; compact `MH01BB0197` → `XX-NN-XX…-NNNN`) |
| `regno_rtocode` | first two parts of the formatted number: `MH-01` |
| `is_llpd`, `is_antitheft_fit`, `is_external_bifuel`, `is_aai_member` | `"no"` |
| `fuel_type` | `Fuel_Type` |
| `external_bifuel_type` | `""` |
| `external_bifuel_value` | `"0"` |
| `pa_owner_driver_si` | `1500000` |
| `is_pa_od`, `is_having_valid_dl` | `"no"` |
| `is_opted_standalone_cpa` | `"yes"` |
| `pa_paid_driver_si` | `"0"` |
| `first_name` / `middle_name` / `last_name` | split `Owner_Name` (or edited `customerName`) on whitespace: 1 word → first only; 2 → first + last; 3+ → first, middle = inner words joined, last |
| `customer_name` | full owner/customer name |
| `mobile` | `"9999999999"` |
| `email` | `"abc@gmail.com"` |
| `crn` | `0` |
| `ss_id` | `appDataManager.getSsId()` as String (`"0"` when blank) |
| `sub_fba_id`, `fba_id` | `"0"` |
| `client_id` | `2` |
| `geo_lat`, `geo_long` | `0` |
| `agent_source` | `""` |
| `app_version` | `AppVersionProvider.getAppVersion()` |
| `vehicle_insurance_subtype` | from the selected policy-type tab, built at runtime (see below) |
| `secret_key` / `client_key` | `NetworkConstants.SECRET_KEY` / `CLIENT_KEY` |
| `is_inspection_done` | `"no"` |
| `client_name` | `"PolicyBoss"` |
| `udid`, `visitor_id`, `ip_address`, `ip_city_state`, `pucc_end_date`, `pucc_number` | `""` |
| `ui_source` | `"UI24"` |
| `is_financed`, `is_oslc`, `is_helmet_cover`, `is_quick_quote`, `addon_applied`, `is_tppd` | `"no"` |
| `oslc_si`, `helmet_cover_si`, `vehicle_expected_idv` | `0` |
| `search_from` | `"diy"` |
| `chassis_number` | `Chassis_Number` |
| `engin_number` | `Engin_Number` |
| `is_fastlane_manufacture` | `"yes"` if `Manufacture_Year` present, else `"no"` |
| `is_fastlane_registration` | `"yes"` if `Registration_Date` present, else `"no"` |
| `pa_unnamed_passenger_si`, `voluntary_deductible` | `"0"` |
| `tp_insurer_id` | only for `PolicyType.OD_ONLY`: `selectedTpInsurer.value`; otherwise `""` |
| `permanent_pincode` | `Permanent_Pincode` |
| `previous_policy_number` | `Pyp_Policy_Number` |
| `vahan_cc_min` / `vahan_cc_max` | from `Vahan_CC_Slab`: `0_TO_1000` → 600/1000, `1000_TO_1500` → 1000/1500, `1500_ABOVE` → 1500/0 (put the table in one function, IRDAI TP slab) |
| `vahan_cc_normal` | `Vahan_Cubic_Capacity` (Int) |
| `vahan_cc_slab` | `Vahan_CC_Slab` |
| `vahan_source` | `Source` (e.g. `Surepass`) |
| `vahan_full` | `"$Vahan_Make|$Vahan_Model|$Vahan_Fuel|${Vahan_Cubic_Capacity}CC"` |
| `match_vehicle_full` | `"$Make_Name|$Model_Name|$Variant_Name|$Fuel_Type|$Cubic_Capacity CC"` |

### `vehicle_insurance_subtype` per tab

Add `apiSubtype` to `PolicyType` (single source of truth):
`COMPREHENSIVE → "1CH_0TP"`, `OD_ONLY → "1OD_0TP"`, `TP_ONLY → "0CH_1TP"`.
Mark the CH/TP values with a `// TODO confirm with backend` comment; only `1OD_0TP` is confirmed.

Put all of the above in a pure builder, e.g. `feature/carInsurance/data/mapper/PremiumInitiateRequestMapper.kt`:
`fun buildPremiumInitiateRequest(vehicle: VehicleInfoDomain, form: NewCarUiState, ssId: String, appVersion: String, today: LocalDate): PremiumInitiateRequest`.
`today` is a parameter so the date rules are unit-testable. Date parsing must never throw — return blank and log.

## Step 3 — API + repository

- `CarInsuranceApi`: `@POST(PREMIUM_INITIATE_URL) suspend fun premiumInitiate(@Body request: PremiumInitiateRequest): Response<JsonElement>`.
  The response schema is not final, so keep it as raw `JsonElement` (same approach as `getModelList`).
- `CarInsuranceRepository.premiumInitiate(request)`: `Resource<PremiumInitiateResult>`, same
  error/log/cancellation handling as `getVehicleInfo` (log `--> POST …premium_initiate` with body and
  `<--` with HTTP code, time and body under `VehicleApiLogger`).
- Parse defensively into a small domain model `PremiumInitiateResult` (in `model/`): whatever
  identifiers the response has (e.g. CRN / `Request_Unique_Id` / `Summary` id — look for them, null if absent)
  plus the raw `JsonElement` for later use. If the call succeeds but nothing usable comes back,
  return `Resource.Error` with a user-friendly message. Never fabricate premiums outside
  `AppConfig.USE_DUMMY_DATA`.

## Step 4 — ViewModel

In `handleGetQuotes()`:
- If a `VehicleInfoDomain` is stored (WITH_CAR_NUMBER flow) → build `PremiumInitiateRequest` and call
  `repository.premiumInitiate`. Otherwise keep the current `getCarQuotes` path unchanged.
- Keep the existing flow: `QuoteFetchState.Loading` → navigate to loading → on success
  `QuoteFetchState.Success` + `QuoteLoadingUiEvent.NavigateToQuotes`; on error `QuoteFetchState.Error`.
  Respect `quoteJob` duplicate-submit guard and `cancelQuoteLoading()`.
- On success store the result in `NewCarUiState` (new field `premiumInitiate: PremiumInitiateResult?`),
  set `crnNumber` from the response, and keep `quotes` non-empty so `ensureQuotesLoaded()` does not bounce
  back (use the existing defaults only when `USE_DUMMY_DATA`, otherwise an empty-state list until the
  quote-list API arrives — adjust `ensureQuotesLoaded()` to also accept a non-null `premiumInitiate`).
- Inject `AppVersionProvider` into the ViewModel (or the repository — pick one, keep the builder pure).

## Step 5 — bind on MotorQuoteResultsScreen (only these fields now)

- Header / `QuoteSummaryCard`: CRN from the response, customer name, registration number
  (`MH-01-BB-0197`), policy type tab, NCB, previous policy expiry.
- `VahanDataCard`: vahan make/model/fuel/CC and the matched vehicle (`match_vehicle_full`).
- `QuoteResultsBottomBar.vehicleSummary`: existing `vehicleSummaryText`.
- Insurer cards keep current behaviour; leave a `// TODO bind from quote-list API` where real premiums go.
Expose derived display strings as `NewCarUiState` getters, not logic in composables. Keep `@Preview`s compiling.

## Step 6 — tests

Add `PremiumInitiateRequestMapperTest` using the sample vahan response (MH01BB0197, WAGON R) and assert:
`vehicle_id=6917`, `rto_id=579`, `vehicle_registration_date=2011-10-18`, `vehicle_manf_date=2011-09-01`,
`policy_expiry_date=2030-06-09`, `prev_insurer_id=17`, `registration_no=MH-01-BB-0197`,
`regno_rtocode=MH-01`, `first_name=VIJAYKUMAR`, `middle_name=""`, `last_name=PAWAR`,
`vahan_cc_min=600`, `vahan_cc_max=1000`, `vahan_cc_slab=0_TO_1000`,
`vahan_full=MARUTI SUZUKI INDIA LTD|MARUTI WAGON R VXI BSIV|PETROL|998CC`,
`match_vehicle_full=MARUTI|WAGON R|VXI+|PETROL|998 CC`; expiry past/today/future → claim/breakin/policy_exist flags;
Individual/Company → individual/corporate; each `PolicyType` → subtype; 1/2/3-word names.
Extend `CarInsuranceTestFakes` with a fake `premiumInitiate` and add a ViewModel test that
Get Quotes on the WITH_CAR_NUMBER path calls `premiumInitiate` (not `getCarQuotes`) and reaches
`QuoteFetchState.Success`. Run `./gradlew :app:testDebugUnitTest` and `./gradlew :app:compileDebugKotlin`
and fix failures.

## Rules

- Do not commit. Do not change unrelated files or the existing `vehicle_info_user` behaviour.
- No secrets in code beyond the existing `NetworkConstants` references; no PII in release logs (`VehicleApiLogger` is debug-only).
- Ask before guessing anything listed under open questions if it blocks you; otherwise use the defaults above and list them in your summary.

## Open questions (use the default, flag in the summary)

1. `is_claim_exists` / `is_policy_exist` derived from the expiry date (past → yes) — confirm; normally claim comes from the form's `hasClaim`.
2. `vehicle_insurance_subtype` values for Comprehensive and TP tabs.
3. `tp_insurer_id` rule for OD-only ("explain by Vijay").
4. `vahan_cc_min/max` for `1500_ABOVE`.
5. Which `premium_initiate` response fields the results screen should show (response sample not yet provided).
