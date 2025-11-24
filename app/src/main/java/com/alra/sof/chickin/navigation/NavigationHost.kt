package com.alra.sof.chickin.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.alra.sof.chickin.ui.screens.*
import com.alra.sof.chickin.viewmodel.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    alarmViewModel: AlarmViewModel = viewModel(),
    sleepViewModel: SleepViewModel = viewModel(),
    morningViewModel: MorningViewModel = viewModel(),
    soundViewModel: SoundViewModel = viewModel(),
    statsViewModel: StatsViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Alarms.route
    ) {
        composable(Screen.Alarms.route) {
            AlarmsScreen(viewModel = alarmViewModel, paddingValues = paddingValues)
        }
        composable(Screen.Sleep.route) {
            SleepScreen(viewModel = sleepViewModel, paddingValues = paddingValues)
        }
        composable(Screen.Morning.route) {
            MorningScreen(viewModel = morningViewModel, paddingValues = paddingValues)
        }
        composable(Screen.Stats.route) {
            StatsScreen(viewModel = statsViewModel, paddingValues = paddingValues)
        }
    }
}

