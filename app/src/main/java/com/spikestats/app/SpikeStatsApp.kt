package com.spikestats.app

import android.app.Application
import com.spikestats.app.data.local.UserPreferences
import com.spikestats.app.data.repository.RiotRepository

class SpikeStatsApp : Application() {

    lateinit var userPreferences: UserPreferences
        private set

    lateinit var riotRepository: RiotRepository
        private set

    override fun onCreate() {
        super.onCreate()
        userPreferences = UserPreferences(this)
        riotRepository = RiotRepository()
    }
}
