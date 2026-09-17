package com.spikestats.app.data.remote

/**
 * Riot splits routing in two: a per-platform shard (used for match/content/ranked data)
 * and a wider "continent" routing value (used only by the cross-game Account-V1 API).
 */
enum class ValorantRegion(
    val platformShard: String,
    val accountRouting: String,
    val displayName: String
) {
    NA("na", "americas", "North America"),
    LATAM("latam", "americas", "Latin America"),
    BR("br", "americas", "Brazil"),
    EU("eu", "europe", "Europe"),
    KR("kr", "asia", "Korea"),
    AP("ap", "asia", "Asia Pacific");

    val accountBaseUrl: String get() = "https://$accountRouting.api.riotgames.com/"
    val platformBaseUrl: String get() = "https://$platformShard.api.riotgames.com/"
}
