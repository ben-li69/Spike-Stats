package com.spikestats.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.spikestats.app.data.remote.ValorantRegion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "spike_stats_prefs")

data class SavedRiotAccount(
    val gameName: String,
    val tagLine: String,
    val region: ValorantRegion,
    val puuid: String
)

/** Stores only the Riot ID, region and PUUID the user searched for. No credentials, ever. */
class UserPreferences(private val context: Context) {

    private object Keys {
        val GAME_NAME = stringPreferencesKey("game_name")
        val TAG_LINE = stringPreferencesKey("tag_line")
        val REGION = stringPreferencesKey("region")
        val PUUID = stringPreferencesKey("puuid")
    }

    val savedAccount: Flow<SavedRiotAccount?> = context.dataStore.data.map { prefs ->
        val gameName = prefs[Keys.GAME_NAME]
        val tagLine = prefs[Keys.TAG_LINE]
        val region = prefs[Keys.REGION]?.let { runCatching { ValorantRegion.valueOf(it) }.getOrNull() }
        val puuid = prefs[Keys.PUUID]
        if (gameName != null && tagLine != null && region != null && puuid != null) {
            SavedRiotAccount(gameName, tagLine, region, puuid)
        } else {
            null
        }
    }

    suspend fun saveAccount(account: SavedRiotAccount) {
        context.dataStore.edit { prefs ->
            prefs[Keys.GAME_NAME] = account.gameName
            prefs[Keys.TAG_LINE] = account.tagLine
            prefs[Keys.REGION] = account.region.name
            prefs[Keys.PUUID] = account.puuid
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
