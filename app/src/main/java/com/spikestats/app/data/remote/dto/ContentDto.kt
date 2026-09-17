package com.spikestats.app.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Maps to Riot's val-content-v1 "contents" endpoint. Only the fields this app
 * actually uses are declared; everything else is ignored (ignoreUnknownKeys = true).
 */
@Serializable
data class ContentDto(
    val maps: List<ContentMapDto> = emptyList(),
    val characters: List<ContentCharacterDto> = emptyList(),
    val competitiveTiers: List<ContentCompetitiveTierSetDto> = emptyList(),
    val skins: List<ContentSkinDto> = emptyList(),
    val contentTiers: List<ContentTierDto> = emptyList()
)

@Serializable
data class ContentMapDto(
    val name: String,
    val id: String? = null,
    val assetName: String? = null,
    val assetPath: String? = null
)

@Serializable
data class ContentCharacterDto(
    val name: String,
    val id: String
)

@Serializable
data class ContentCompetitiveTierSetDto(
    val tierSet: String? = null,
    val tiers: List<ContentCompetitiveTierDto> = emptyList()
)

@Serializable
data class ContentCompetitiveTierDto(
    val tier: Int,
    val tierName: String
)

@Serializable
data class ContentSkinDto(
    val id: String,
    val name: String,
    val contentTierUuid: String? = null,
    val displayIcon: String? = null,
    val levels: List<ContentSkinLevelDto> = emptyList()
)

@Serializable
data class ContentSkinLevelDto(
    val id: String,
    val name: String? = null,
    val displayIcon: String? = null
)

@Serializable
data class ContentTierDto(
    val id: String,
    val name: String
)
