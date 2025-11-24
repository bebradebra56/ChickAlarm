package com.alra.sof.chickin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alra.sof.chickin.data.models.*
import com.alra.sof.chickin.regoif.presentation.app.ChickAlarmApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class StatsViewModel : ViewModel() {
    private val statsRepository = ChickAlarmApplication.instance.statsRepository
    private val achievementRepository = ChickAlarmApplication.instance.achievementRepository
    
    private val _dailyStats = MutableStateFlow<List<DailyStats>>(emptyList())
    val dailyStats: StateFlow<List<DailyStats>> = _dailyStats.asStateFlow()

    private val _weeklyStats = MutableStateFlow<WeeklyStats?>(null)
    val weeklyStats: StateFlow<WeeklyStats?> = _weeklyStats.asStateFlow()

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()
    
    init {
        loadStats()
    }
    
    private fun loadStats() {
        viewModelScope.launch {
            statsRepository.getDailyStats().collect { stats ->
                _dailyStats.value = stats
            }
        }
        
        viewModelScope.launch {
            statsRepository.getWeeklyStats().collect { stats ->
                _weeklyStats.value = stats
            }
        }
        
        viewModelScope.launch {
            achievementRepository.getAchievements().collect { achievements ->
                _achievements.value = achievements
            }
        }
    }

    fun getAverageWakeTime(): String {
        val stats = _dailyStats.value
        if (stats.isEmpty()) return "—"
        return stats.first().wakeTime
    }

    fun getConsistencyPercentage(): Float {
        return _weeklyStats.value?.consistency?.toFloat() ?: 0f
    }

    fun getTotalSuccessfulDays(): Int {
        return _dailyStats.value.count { it.snoozeCount == 0 && it.challengeCompleted }
    }

    fun getUnlockedAchievements(): List<Achievement> {
        return _achievements.value.filter { it.isUnlocked }
    }

    fun getInProgressAchievements(): List<Achievement> {
        return _achievements.value.filter { !it.isUnlocked && it.progress > 0 }
    }
}

