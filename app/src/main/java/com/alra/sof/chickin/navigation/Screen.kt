package com.alra.sof.chickin.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Alarms : Screen("alarms", "🐓 Roosters", Icons.Default.Alarm)
    object Sleep : Screen("sleep", "🌙 Roost", Icons.Default.Bedtime)
    object Morning : Screen("morning", "🌅 Sunrise", Icons.Default.WbSunny)
    object Sounds : Screen("sounds", "🎵 Clucks", Icons.Default.MusicNote)
    object Stats : Screen("stats", "🏆 Stats", Icons.Default.BarChart)
}

val bottomNavigationItems = listOf(
    Screen.Alarms,
    Screen.Sleep,
    Screen.Morning,
    Screen.Stats
)

