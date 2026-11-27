package com.policyboss.policybosspro.feature.carInsurance.data.repository

import com.policyboss.policybosspro.core.Resource
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumInitiateInput
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumInitiateResult
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumPollStatus
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumProgress
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumQuoteList
import com.policyboss.policybosspro.feature.carInsurance.model.PremiumQuotePollEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * The three quote APIs, always run together and in this order:
 *
 *   1. `premium_initiate`  -> search reference (`Summary.Request_Unique_Id`)
 *   2. `premium_summary`   -> insurer progress, called once; a failure is logged, not fatal
 *   3. `premium_list_db`   -> polled via [CarInsuranceRepository.pollPremiumQuotes]
 *                             (same merge / stop / timeout rules as the first screen load)
 *
 * Every quote refresh (Get Quotes, variant change, later IDV / add-on / CPA changes) runs this
 * bundle. The flow is cold and cancellable: collecting it again starts a fresh run, and
 * cancelling the collector stops the in-flight call and any polling delay.
 */
class PremiumQuoteBundle(private val repository: CarInsuranceRepository) {

    fun run(input: PremiumInitiateInput): Flow<PremiumQuoteBundleEvent> = flow {
        // 1. premium_initiate
        val initiate = try {
            repository.premiumInitiate(input)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage?.takeIf { it.isNotBlank() } ?: GENERIC_ERROR)
        }
        val result = when (initiate) {
            is Resource.Success -> initiate.data
            is Resource.Error -> {
                emit(PremiumQuoteBundleEvent.Failed(PremiumQuoteBundleEvent.Step.INITIATE, initiate.message))
                return@flow
            }
        }
        val searchReference = result.requestUniqueId
        if (searchReference.isBlank()) {
            emit(PremiumQuoteBundleEvent.Failed(PremiumQuoteBundleEvent.Step.INITIATE, NO_SEARCH_REFERENCE))
            return@flow
        }
        emit(PremiumQuoteBundleEvent.Initiated(result))

        // 2. premium_summary (progress only; the plans come from premium_list_db)
        val summary = try {
            repository.premiumSummary(searchReference)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage.orEmpty())
        }
        when (summary) {
            is Resource.Success -> emit(PremiumQuoteBundleEvent.Summary(summary.data))
            is Resource.Error -> VehicleApiLogger.e("premium_summary failed, continuing to premium_list_db: ${summary.message}")
        }

        // 3. premium_list_db polling
        repository.pollPremiumQuotes(searchReference).collect { event ->
            when (event) {
                is PremiumQuotePollEvent.Update ->
                    emit(PremiumQuoteBundleEvent.Quotes(event.quotes, event.status))
                is PremiumQuotePollEvent.Failed ->
                    emit(PremiumQuoteBundleEvent.Failed(PremiumQuoteBundleEvent.Step.LIST, event.message))
            }
        }
    }

    companion object {
        const val GENERIC_ERROR = "Something went wrong while curating your quotes. Please try again."
        const val NO_SEARCH_REFERENCE = "We couldn't start your quotes. Please try again."
    }
}

sealed interface PremiumQuoteBundleEvent {
    /** premium_initiate succeeded with a search reference. */
    data class Initiated(val result: PremiumInitiateResult) : PremiumQuoteBundleEvent
    /** premium_summary progress (emitted at most once per run). */
    data class Summary(val progress: PremiumProgress) : PremiumQuoteBundleEvent
    /** One merged premium_list_db snapshot. */
    data class Quotes(val list: PremiumQuoteList, val status: PremiumPollStatus) : PremiumQuoteBundleEvent
    /** The run stopped at [step]. SUMMARY is never emitted: its failure does not stop the run. */
    data class Failed(val step: Step, val message: String) : PremiumQuoteBundleEvent

    enum class Step { INITIATE, SUMMARY, LIST }
}
