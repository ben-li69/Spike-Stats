package com.spikestats.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.spikestats.app.data.local.UserPreferences
import com.spikestats.app.data.repository.RiotRepository
import com.spikestats.app.ui.screens.login.LoginScreen
import com.spikestats.app.ui.screens.login.LoginViewModel
import com.spikestats.app.ui.screens.profile.ProfileScreen
import com.spikestats.app.ui.screens.profile.ProfileViewModel
import com.spikestats.app.ui.screens.skins.SkinsScreen
import com.spikestats.app.ui.screens.skins.SkinsViewModel

/**
 * Manual DI on purpose: this is a small MVP scaffold, so screens build their
 * ViewModels via `remember` instead of a ViewModelProvider.Factory. That means
 * screen state won't survive a configuration change (e.g. rotation) -- swap in
 * a proper factory (with SavedStateHandle) before shipping this for real.
 */
@Composable
fun SpikeNavGraph(
    userPreferences: UserPreferences,
    riotRepository: RiotRepository
) {
    val savedAccount by userPreferences.savedAccount.collectAsState(initial = null)
    val navController = rememberNavController()

    val startDestination = if (savedAccount == null) Routes.Login.route else Routes.Profile.route
    val showBottomBar = savedAccount != null

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Routes.Profile.route,
                        onClick = {
                            navController.navigate(Routes.Profile.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Profile") },
                        label = { Text("Profile") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.Skins.route,
                        onClick = {
                            navController.navigate(Routes.Skins.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Style, contentDescription = "Skins") },
                        label = { Text("Skins") }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.Login.route) {
                val viewModel = remember(riotRepository, userPreferences) {
                    LoginViewModel(riotRepository, userPreferences)
                }
                LoginScreen(
                    viewModel = viewModel,
                    onLoggedIn = {
                        navController.navigate(Routes.Profile.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.Profile.route) {
                savedAccount?.let { account ->
                    val viewModel = remember(account) {
                        ProfileViewModel(riotRepository, userPreferences, account)
                    }
                    ProfileScreen(
                        viewModel = viewModel,
                        onSignOut = {
                            navController.navigate(Routes.Login.route) {
                                popUpTo(0)
                            }
                        }
                    )
                }
            }
            composable(Routes.Skins.route) {
                savedAccount?.let { account ->
                    val viewModel = remember(account) {
                        SkinsViewModel(riotRepository, account.region)
                    }
                    SkinsScreen(viewModel = viewModel)
                }
            }
        }
    }
}
