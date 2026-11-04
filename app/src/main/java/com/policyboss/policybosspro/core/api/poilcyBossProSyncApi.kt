package com.policyboss.policybosspro.core.api

import com.policyboss.policybosspro.core.requestbuilder.ContactLeadRequestEntity
import com.policyboss.policybosspro.core.requestbuilder.syncContact.CallLogRequestEntity
import com.policyboss.policybosspro.core.response.ContactLeadResponse
import com.policyboss.policybosspro.core.response.syncContact.ContactLogResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface poilcyBossProSyncApi {

    @POST
    suspend fun saveContactLead(
        @Url url: String,
        @Body body: ContactLeadRequestEntity
    ): Response<ContactLeadResponse?>?

    @POST
    suspend fun saveCallLog(
        @Url url: String,
        @Body body: CallLogRequestEntity
    ): Response<ContactLogResponse?>?
}

