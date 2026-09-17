package com.spikestats.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MatchDto(
    val matchInfo: MatchInfoDto,
    val players: List<MatchPlayerDto> = emptyList(),
    val teams: List<MatchTeamDto> = emptyList()
)

@Serializable
data class MatchInfoDto(
    val matchId: String,
    val mapId: String? = null,
    val gameLengthMillis: Long = 0L,
    val gameStartMillis: Long = 0L,
    val queueId: String? = null,
    val gameMode: String? = null,
    val isRanked: Boolean = false,
    val seasonId: String? = null
)

@Serializable
data class MatchPlayerDto(
    val puuid: String,
    val gameName: String? = null,
    val tagLine: String? = null,
    val teamId: String? = null,
    val characterId: String? = null,
    val competitiveTier: Int = 0,
    val stats: MatchPlayerStatsDto? = null
)

@Serializable
data class MatchPlayerStatsDto(
    val score: Int = 0,
    val roundsPlayed: Int = 0,
    val kills: Int = 0,
    val deaths: Int = 0,
    val assists: Int = 0,
    val playtimeMillis: Long = 0L
)

@Serializable
data class MatchTeamDto(
    val teamId: String,
    val won: Boolean = false,
    val roundsPlayed: Int = 0,
    val roundsWon: Int = 0
)
