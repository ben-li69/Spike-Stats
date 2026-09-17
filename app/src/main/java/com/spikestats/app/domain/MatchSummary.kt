package com.spikestats.app.domain

data class MatchSummary(
    val matchId: String,
    val mapName: String,
    val agentName: String,
    val mode: String,
    val won: Boolean?,
    val kills: Int,
    val deaths: Int,
    val assists: Int,
    val startedAtMillis: Long,
    val rankTierName: String?
)

data class SkinItem(
    val id: String,
    val name: String,
    val iconUrl: String?,
    val rarityName: String?
)
