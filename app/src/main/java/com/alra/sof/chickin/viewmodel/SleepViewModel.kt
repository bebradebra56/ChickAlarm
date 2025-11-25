package com.alra.sof.chickin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alra.sof.chickin.data.models.*
import com.alra.sof.chickin.regoif.presentation.app.ChickAlarmApplication
import com.alra.sof.chickin.sleep.SleepTrackingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class SleepViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ChickAlarmApplication.instance.sleepRepository
    private val context = application.applicationContext

    private val _sleepSessions = MutableStateFlow<List<SleepSession>>(emptyList())
    val sleepSessions: StateFlow<List<SleepSession>> = _sleepSessions.asStateFlow()

    private val _currentSession = MutableStateFlow<SleepSession?>(null)
    val currentSession: StateFlow<SleepSession?> = _currentSession.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _sleepStats = MutableStateFlow(SleepStats())
    val sleepStats: StateFlow<SleepStats> = _sleepStats.asStateFlow()

    private val _breathingExercises = MutableStateFlow(defaultBreathingExercises)
    val breathingExercises: StateFlow<List<BreathingExercise>> = _breathingExercises.asStateFlow()

    private val _selectedExercise = MutableStateFlow<BreathingExercise?>(null)
    val selectedExercise: StateFlow<BreathingExercise?> = _selectedExercise.asStateFlow()

    init {
        loadSessions()
        checkActiveSession()
    }

    private fun loadSessions() {
        viewModelScope.launch {
            repository.getRecentSessions(10).collect { sessions ->
                _sleepSessions.value = sessions
                updateSleepStats()
            }
        }
    }

    private fun checkActiveSession() {
        viewModelScope.launch {
            val activeSession = repository.getActiveSession()
            if (activeSession != null) {
                _currentSession.value = activeSession
                _isTracking.value = true
            }
        }
    }

    fun startTracking() {
        viewModelScope.launch {
            val session = SleepSession(startTime = LocalDateTime.now())
            repository.insertSession(session)
            _currentSession.value = session
            _isTracking.value = true
            SleepTrackingService.startTracking(context)
        }
    }

    fun stopTracking() {
        viewModelScope.launch {
            _currentSession.value?.let { session ->
                val endTime = LocalDateTime.now()
                val duration = java.time.Duration.between(session.startTime, endTime).toMinutes() / 60.0
                
                // Calculate quality based on sleep duration
                val quality = when {
                    duration >= 7.5 && duration <= 9.0 -> SleepQuality.EXCELLENT
                    duration >= 6.5 && duration < 7.5 -> SleepQuality.GOOD
                    duration >= 5.5 && duration < 6.5 -> SleepQuality.FAIR
                    else -> SleepQuality.POOR
                }
                
                val completedSession = session.copy(
                    endTime = endTime,
                    quality = quality
                )
                repository.updateSession(completedSession)
                _currentSession.value = null
                _isTracking.value = false
//                SleepTrackingService.stopTracking(context)
            }
        }
    }

    fun selectBreathingExercise(exercise: BreathingExercise?) {
        _selectedExercise.value = exercise
    }

    private fun updateSleepStats() {
        val sessions = _sleepSessions.value.filter { it.endTime != null }
        if (sessions.isEmpty()) return

        val avgDuration = sessions.mapNotNull { session ->
            session.endTime?.let { end ->
                java.time.Duration.between(session.startTime, end).toMinutes() / 60.0
            }
        }.average()

        _sleepStats.value = SleepStats(
            averageDuration = avgDuration,
            averageQuality = SleepQuality.GOOD,
            sleepDebt = maxOf(0.0, (8.0 - avgDuration) * sessions.size),
            consistency = 85.0,
            chronotype = Chronotype.INTERMEDIATE
        )
    }
}

