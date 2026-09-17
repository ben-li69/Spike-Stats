package com.spikestats.app.ui.screens.skins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spikestats.app.data.remote.ValorantRegion
import com.spikestats.app.data.repository.RiotRepository
import com.spikestats.app.domain.ContentLookup
import com.spikestats.app.domain.SkinItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SkinsUiState {
    data object Loading : SkinsUiState
    data class Error(val message: String) : SkinsUiState
    data class Loaded(val skins: List<SkinItem>) : SkinsUiState
}

class SkinsViewModel(
    private val riotRepository: RiotRepository,
    private val region: ValorantRegion
) : ViewModel() {

    private val _uiState = MutableStateFlow<SkinsUiState>(SkinsUiState.Loading)
    val uiState: StateFlow<SkinsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = SkinsUiState.Loading
            try {
                val content = riotRepository.getContent(region)
                val lookup = ContentLookup(content)
                _uiState.value = SkinsUiState.Loaded(lookup.skinItems)
            } catch (e: Exception) {
                _uiState.value = SkinsUiState.Error(e.message ?: "Couldn't load the skins catalog")
            }
        }
    }
}
