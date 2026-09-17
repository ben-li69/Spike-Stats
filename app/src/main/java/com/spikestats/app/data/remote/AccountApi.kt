package com.spikestats.app.data.remote

import com.spikestats.app.data.remote.dto.AccountDto
import retrofit2.http.GET
import retrofit2.http.Path

/** Account-V1 — cross-game identity lookup, hit via the "americas/europe/asia" routing values. */
interface AccountApi {
    @GET("riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}")
    suspend fun getAccountByRiotId(
        @Path("gameName") gameName: String,
        @Path("tagLine") tagLine: String
    ): AccountDto
}
