package com.spikestats.app.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spikestats.app.data.local.SavedRiotAccount
import com.spikestats.app.data.local.UserPreferences
import com.spikestats.app.data.remote.ValorantRegion
import com.spikestats.app.data.repository.RiotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data class Error(val message: String) : LoginUiState
}

class LoginViewModel(
    private val riotRepository: RiotRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun findAccount(riotId: String, region: ValorantRegion, onSuccess: () -> Unit) {
        val parts = riotId.split("#")
        if (parts.size != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            _uiState.value = LoginUiState.Error("Enter your Riot ID like Name#Tag")
            return
        }
        val gameName = parts[0].trim()
        val tagLine = parts[1].trim()

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val account = riotRepository.findAccount(region, gameName, tagLine)
                userPreferences.saveAccount(
                    SavedRiotAccount(
                        gameName = account.gameName ?: gameName,
                        tagLine = account.tagLine ?: tagLine,
                        region = region,
                        puuid = account.puuid
                    )
                )
                _uiState.value = LoginUiState.Idle
                onSuccess()
            } catch (e: HttpException) {
                _uiState.value = LoginUiState.Error(
                    when (e.code()) {
                        404 -> "No Riot account found for that ID in this region"
                        401, 403 -> "Riot API key missing or expired — check local.properties"
                        else -> "Riot API error (${e.code()})"
                    }
                )
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }
}
