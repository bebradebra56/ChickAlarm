package com.alra.sof.chickin

import android.Manifest
import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.alra.sof.chickin.navigation.NavigationHost
import com.alra.sof.chickin.ui.components.BottomNavigationBar
import com.alra.sof.chickin.ui.theme.ChickAlarmTheme
import com.alra.sof.chickin.viewmodel.*

class MainActivity : ComponentActivity() {

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            ChickAlarmTheme {
                ChickAlarmApp()
            }
        }
    }
}

@Composable
fun ChickAlarmApp() {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    
    val navController = rememberNavController()
    val alarmViewModel: AlarmViewModel = viewModel()
    val sleepViewModel: SleepViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return SleepViewModel(application) as T
            }
        }
    )
    val morningViewModel: MorningViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return MorningViewModel(application) as T
            }
        }
    )
    val soundViewModel: SoundViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return SoundViewModel(application) as T
            }
        }
    )
    val statsViewModel: StatsViewModel = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        NavigationHost(
            navController = navController,
            paddingValues = paddingValues,
            alarmViewModel = alarmViewModel,
            sleepViewModel = sleepViewModel,
            morningViewModel = morningViewModel,
            soundViewModel = soundViewModel,
            statsViewModel = statsViewModel
        )
    }
}