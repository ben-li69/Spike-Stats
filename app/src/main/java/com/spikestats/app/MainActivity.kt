package com.spikestats.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.spikestats.app.ui.navigation.SpikeNavGraph
import com.spikestats.app.ui.theme.SpikeStatsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as SpikeStatsApp
        setContent {
            SpikeStatsTheme {
                SpikeNavGraph(
                    userPreferences = app.userPreferences,
                    riotRepository = app.riotRepository
                )
            }
        }
    }
}
