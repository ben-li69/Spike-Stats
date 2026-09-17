package com.spikestats.app.ui.screens.skins

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.spikestats.app.domain.SkinItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkinsScreen(viewModel: SkinsViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Weapon Skins") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text(
                "Full skin catalog from Riot's public game data — not your personal daily store, " +
                    "which requires a real Riot client login.",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(16.dp)
            )
            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is SkinsUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is SkinsUiState.Error -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center).padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(state.message)
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = { viewModel.load() }) { Text("Retry") }
                        }
                    }
                    is SkinsUiState.Loaded -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.skins, key = { it.id }) { skin ->
                                SkinCard(skin)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SkinCard(skin: SkinItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = skin.iconUrl,
                contentDescription = skin.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth().height(72.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(skin.name, fontWeight = FontWeight.SemiBold, maxLines = 1)
            skin.rarityName?.let {
                Text(it, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
