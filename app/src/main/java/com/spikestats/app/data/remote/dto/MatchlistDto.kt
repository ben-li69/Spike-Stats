package com.spikestats.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MatchlistDto(
    val puuid: String? = null,
    val history: List<MatchHistoryEntryDto> = emptyList()
)

@Serializable
data class MatchHistoryEntryDto(
    val matchId: String,
    val gameStartTimeMillis: Long = 0L,
    val queueId: String? = null
)
