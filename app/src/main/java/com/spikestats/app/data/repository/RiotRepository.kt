package com.spikestats.app.data.repository

import com.spikestats.app.data.remote.AccountApi
import com.spikestats.app.data.remote.ValorantApi
import com.spikestats.app.data.remote.ValorantRegion
import com.spikestats.app.data.remote.dto.AccountDto
import com.spikestats.app.data.remote.dto.ContentDto
import com.spikestats.app.data.remote.dto.MatchDto
import com.spikestats.app.data.remote.riotRetrofit

/**
 * Thin wrapper around the Riot Account-V1 and Valorant APIs. One Retrofit
 * instance is cached per routing value so region switches don't rebuild clients
 * unnecessarily.
 */
class RiotRepository {

    private val accountApiCache = mutableMapOf<String, AccountApi>()
    private val valorantApiCache = mutableMapOf<ValorantRegion, ValorantApi>()

    private fun accountApi(region: ValorantRegion): AccountApi =
        accountApiCache.getOrPut(region.accountRouting) {
            riotRetrofit(region.accountBaseUrl).create(AccountApi::class.java)
        }

    private fun valorantApi(region: ValorantRegion): ValorantApi =
        valorantApiCache.getOrPut(region) {
            riotRetrofit(region.platformBaseUrl).create(ValorantApi::class.java)
        }

    suspend fun findAccount(region: ValorantRegion, gameName: String, tagLine: String): AccountDto =
        accountApi(region).getAccountByRiotId(gameName, tagLine)

    suspend fun getRecentMatchIds(region: ValorantRegion, puuid: String, limit: Int = 10): List<String> =
        valorantApi(region).getMatchlist(puuid).history
            .sortedByDescending { it.gameStartTimeMillis }
            .take(limit)
            .map { it.matchId }

    suspend fun getMatch(region: ValorantRegion, matchId: String): MatchDto =
        valorantApi(region).getMatch(matchId)

    suspend fun getContent(region: ValorantRegion): ContentDto =
        valorantApi(region).getContent()
}
