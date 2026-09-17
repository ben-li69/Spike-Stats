package com.spikestats.app.data.remote

import com.spikestats.app.data.remote.dto.ContentDto
import com.spikestats.app.data.remote.dto.MatchDto
import com.spikestats.app.data.remote.dto.MatchlistDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** val-match-v1 and val-content-v1 — hit via a platform shard (na/eu/ap/kr/latam/br). */
interface ValorantApi {
    @GET("val/match/v1/matchlists/by-puuid/{puuid}")
    suspend fun getMatchlist(@Path("puuid") puuid: String): MatchlistDto

    @GET("val/match/v1/matches/{matchId}")
    suspend fun getMatch(@Path("matchId") matchId: String): MatchDto

    @GET("val/content/v1/contents")
    suspend fun getContent(@Query("locale") locale: String = "en-US"): ContentDto
}
