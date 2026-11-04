package com.policyboss.policybosspro.core.repository.contact

interface ContactRepository {
    /**
     * Safely queries the local device ContactsContract for total contact count.
     * Executes strictly on Dispatchers.IO.
     * Returns Result.success(count) or Result.failure on query or security exception.
     * Does NOT transmit, log, or persist contact details.
     */
    suspend fun getLocalContactsCount(): Result<Int>
}

