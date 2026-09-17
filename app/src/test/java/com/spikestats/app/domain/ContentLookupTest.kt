package com.spikestats.app.domain

import com.spikestats.app.data.remote.dto.ContentCompetitiveTierDto
import com.spikestats.app.data.remote.dto.ContentCompetitiveTierSetDto
import com.spikestats.app.data.remote.dto.ContentCharacterDto
import com.spikestats.app.data.remote.dto.ContentDto
import com.spikestats.app.data.remote.dto.ContentMapDto
import com.spikestats.app.data.remote.dto.ContentSkinDto
import com.spikestats.app.data.remote.dto.ContentTierDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ContentLookupTest {

    @Test
    fun `mapName and agentName fall back to Unknown when content is null`() {
        val lookup = ContentLookup(null)

        assertEquals("Unknown Map", lookup.mapName("/Game/Maps/Bonsai/Bonsai"))
        assertEquals("Unknown Agent", lookup.agentName("some-agent-id"))
    }

    @Test
    fun `mapName and agentName resolve from content`() {
        val content = ContentDto(
            maps = listOf(ContentMapDto(name = "Split", assetPath = "/Game/Maps/Ascent/Ascent")),
            characters = listOf(ContentCharacterDto(id = "agent-1", name = "Jett"))
        )
        val lookup = ContentLookup(content)

        assertEquals("Split", lookup.mapName("/Game/Maps/Ascent/Ascent"))
        assertEquals("Jett", lookup.agentName("agent-1"))
    }

    @Test
    fun `tierName uses fallback tier names when content has no competitive tiers`() {
        val lookup = ContentLookup(ContentDto())

        assertEquals("Unranked", lookup.tierName(0))
        assertEquals("Radiant", lookup.tierName(27))
        assertEquals("Unranked", lookup.tierName(999))
    }

    @Test
    fun `tierName prefers tiers from content when present`() {
        val content = ContentDto(
            competitiveTiers = listOf(
                ContentCompetitiveTierSetDto(
                    tierSet = "e132a2",
                    tiers = listOf(ContentCompetitiveTierDto(tier = 24, tierName = "Custom Immortal"))
                )
            )
        )
        val lookup = ContentLookup(content)

        assertEquals("Custom Immortal", lookup.tierName(24))
    }

    @Test
    fun `skinItems filters blank names and sorts alphabetically`() {
        val content = ContentDto(
            skins = listOf(
                ContentSkinDto(id = "3", name = "Reaver Vandal"),
                ContentSkinDto(id = "1", name = ""),
                ContentSkinDto(id = "2", name = "Prime Phantom")
            )
        )
        val lookup = ContentLookup(content)

        assertEquals(listOf("Prime Phantom", "Reaver Vandal"), lookup.skinItems.map { it.name })
    }

    @Test
    fun `skinItems resolves rarity name from contentTiers`() {
        val content = ContentDto(
            skins = listOf(ContentSkinDto(id = "1", name = "Reaver Vandal", contentTierUuid = "tier-uuid")),
            contentTiers = listOf(ContentTierDto(id = "tier-uuid", name = "Deluxe"))
        )
        val lookup = ContentLookup(content)

        assertEquals("Deluxe", lookup.skinItems.single().rarityName)
    }

    @Test
    fun `skinItems has null rarity name when contentTierUuid is missing`() {
        val content = ContentDto(
            skins = listOf(ContentSkinDto(id = "1", name = "Reaver Vandal"))
        )
        val lookup = ContentLookup(content)

        assertNull(lookup.skinItems.single().rarityName)
    }
}
