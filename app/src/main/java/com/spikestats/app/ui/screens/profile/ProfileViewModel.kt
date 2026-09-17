package com.spikestats.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spikestats.app.data.local.SavedRiotAccount
import com.spikestats.app.data.local.UserPreferences
import com.spikestats.app.data.repository.RiotRepository
import com.spikestats.app.domain.ContentLookup
import com.spikestats.app.domain.MatchSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Error(val message: String) : ProfileUiState
    data class Loaded(val currentRankName: String?, val matches: List<MatchSummary>) : ProfileUiState
}

class ProfileViewModel(
    private val riotRepository: RiotRepository,
    private val userPreferences: UserPreferences,
    private val account: SavedRiotAccount
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    val riotId: String = "${account.gameName}#${account.tagLine}"
    val regionName: String = account.region.displayName

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val content = runCatching { riotRepository.getContent(account.region) }.getOrNull()
                val lookup = ContentLookup(content)

                val matchIds = riotRepository.getRecentMatchIds(account.region, account.puuid, limit = 10)
                val matches = matchIds.mapNotNull { id ->
                    runCatching {
                        val match = riotRepository.getMatch(account.region, id)
                        val me = match.players.find { it.puuid == account.puuid } ?: return@runCatching null
                        val myTeam = match.teams.find { it.teamId == me.teamId }
                        MatchSummary(
                            matchId = match.matchInfo.matchId,
                            mapName = lookup.mapName(match.matchInfo.mapId),
                            agentName = lookup.agentName(me.characterId),
                            mode = match.matchInfo.queueId ?: "unknown",
                            won = myTeam?.won,
                            kills = me.stats?.kills ?: 0,
                            deaths = me.stats?.deaths ?: 0,
                            assists = me.stats?.assists ?: 0,
                            startedAtMillis = match.matchInfo.gameStartMillis,
                            rankTierName = if (match.matchInfo.isRanked) lookup.tierName(me.competitiveTier) else null
                        )
                    }.getOrNull()
                }.sortedByDescending { it.startedAtMillis }

                val currentRank = matches.firstOrNull { it.rankTierName != null }?.rankTierName

                _uiState.value = ProfileUiState.Loaded(currentRank, matches)
            } catch (e: HttpException) {
                _uiState.value = ProfileUiState.Error(
                    if (e.code() == 403) {
                        "Your Riot API key doesn't have match-history access yet. Riot restricts " +
                            "val/match/v1 to specially-approved keys — request access for your key " +
                            "at developer.riotgames.com (Register Product)."
                    } else {
                        "Riot API error (${e.code()}) while loading match history."
                    }
                )
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Couldn't load your stats")
            }
        }
    }

    fun signOut(onDone: () -> Unit) {
        viewModelScope.launch {
            userPreferences.clear()
            onDone()
        }
    }
}
