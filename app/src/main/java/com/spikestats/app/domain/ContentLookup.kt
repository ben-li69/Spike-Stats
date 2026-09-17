package com.spikestats.app.domain

import com.spikestats.app.data.remote.dto.ContentDto

/**
 * Turns a raw val-content-v1 response into fast id -> display-name lookups.
 * Content is locale-specific and mostly static, so callers should fetch it once
 * per session rather than per match.
 */
class ContentLookup(content: ContentDto?) {

    private val mapNames: Map<String, String> = buildMap {
        content?.maps?.forEach { map ->
            map.assetPath?.let { put(it, map.name) }
            map.assetName?.let { put(it, map.name) }
        }
    }

    private val characterNames: Map<String, String> =
        content?.characters?.associate { it.id to it.name } ?: emptyMap()

    private val tierNames: Map<Int, String> =
        content?.competitiveTiers?.lastOrNull()?.tiers?.associate { it.tier to it.tierName }
            ?.takeIf { it.isNotEmpty() }
            ?: FALLBACK_TIER_NAMES

    private val rarityNames: Map<String, String> =
        content?.contentTiers?.associate { it.id to it.name } ?: emptyMap()

    val skinItems: List<SkinItem> = content?.skins
        ?.filter { it.name.isNotBlank() }
        ?.map { skin ->
            SkinItem(
                id = skin.id,
                name = skin.name,
                iconUrl = skin.displayIcon ?: skin.levels.firstOrNull()?.displayIcon,
                rarityName = skin.contentTierUuid?.let { rarityNames[it] }
            )
        }
        ?.sortedBy { it.name }
        ?: emptyList()

    fun mapName(mapId: String?): String = mapId?.let { mapNames[it] } ?: "Unknown Map"

    fun agentName(characterId: String?): String = characterId?.let { characterNames[it] } ?: "Unknown Agent"

    fun tierName(tier: Int): String = tierNames[tier] ?: "Unranked"

    companion object {
        // Fallback for the standard 0-27 competitive tier scale (Iron 1 - Radiant),
        // used only if val-content-v1 doesn't return a usable tier set.
        private val FALLBACK_TIER_NAMES: Map<Int, String> = mapOf(
            0 to "Unranked",
            3 to "Iron 1", 4 to "Iron 2", 5 to "Iron 3",
            6 to "Bronze 1", 7 to "Bronze 2", 8 to "Bronze 3",
            9 to "Silver 1", 10 to "Silver 2", 11 to "Silver 3",
            12 to "Gold 1", 13 to "Gold 2", 14 to "Gold 3",
            15 to "Platinum 1", 16 to "Platinum 2", 17 to "Platinum 3",
            18 to "Diamond 1", 19 to "Diamond 2", 20 to "Diamond 3",
            21 to "Ascendant 1", 22 to "Ascendant 2", 23 to "Ascendant 3",
            24 to "Immortal 1", 25 to "Immortal 2", 26 to "Immortal 3",
            27 to "Radiant"
        )
    }
}
