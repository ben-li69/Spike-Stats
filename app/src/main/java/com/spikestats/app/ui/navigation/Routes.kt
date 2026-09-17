package com.spikestats.app.ui.navigation

sealed class Routes(val route: String) {
    data object Login : Routes("login")
    data object Profile : Routes("profile")
    data object Skins : Routes("skins")
}
