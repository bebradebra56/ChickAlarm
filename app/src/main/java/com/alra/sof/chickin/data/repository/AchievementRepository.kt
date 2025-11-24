package com.alra.sof.chickin.data.repository

import com.alra.sof.chickin.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class AchievementRepository(
    private val alarmRepository: AlarmRepository,
    private val sleepRepository: SleepRepository
) {
    
    fun getAchievements(): Flow<List<Achievement>> {
        return combine(
            alarmRepository.allAlarms,
            sleepRepository.allSessions
        ) { alarms, sessions ->
            calculateAchievements(alarms, sessions)
        }
    }
    
    private fun calculateAchievements(alarms: List<Alarm>, sessions: List<SleepSession>): List<Achievement> {
        val today = LocalDate.now()
        val last30Days = (0..29).map { today.minusDays(it.toLong()) }
        val last7Days = (0..6).map { today.minusDays(it.toLong()) }
        
        return defaultAchievements.map { achievement ->
            val progress = when (achievement.id) {
                "no_snooze_7" -> calculateNoSnoozeProgress(sessions, last7Days)
                "consistent_30" -> calculateConsistencyProgress(sessions, last30Days)
                "early_bird_7" -> calculateEarlyBirdProgress(sessions, last7Days)
                "challenge_master_14" -> calculateChallengeProgress(sessions, last30Days.take(14))
                "morning_routine_21" -> calculateRoutineProgress(sessions, last30Days.take(21))
                "sleep_quality_7" -> calculateSleepQualityProgress(sessions, last7Days)
                else -> 0
            }
            
            achievement.copy(
                progress = progress,
                isUnlocked = progress >= achievement.target,
                unlockedDate = if (progress >= achievement.target) today else null
            )
        }
    }
    
    private fun calculateNoSnoozeProgress(sessions: List<SleepSession>, days: List<LocalDate>): Int {
        val completedDays = days.count { date ->
            val session = sessions.find { it.startTime.toLocalDate() == date }
            session != null && session.wakeUps == 0
        }
        return (completedDays * 100 / days.size).coerceAtMost(100)
    }
    
    private fun calculateConsistencyProgress(sessions: List<SleepSession>, days: List<LocalDate>): Int {
        val wakeTimes = days.mapNotNull { date ->
            sessions.find { it.startTime.toLocalDate() == date }?.endTime?.toLocalTime()
        }
        
        if (wakeTimes.isEmpty()) return 0
        
        val avgWakeTime = wakeTimes.map { it.hour * 60 + it.minute }.average()
        val consistentDays = wakeTimes.count { wakeTime ->
            val minutes = wakeTime.hour * 60 + wakeTime.minute
            kotlin.math.abs(minutes - avgWakeTime) <= 30
        }
        
        return (consistentDays * 100 / days.size).coerceAtMost(100)
    }
    
    private fun calculateEarlyBirdProgress(sessions: List<SleepSession>, days: List<LocalDate>): Int {
        val earlyDays = days.count { date ->
            val session = sessions.find { it.startTime.toLocalDate() == date }
            session?.endTime?.toLocalTime()?.let { wakeTime ->
                wakeTime.hour < 6 || (wakeTime.hour == 6 && wakeTime.minute <= 30)
            } ?: false
        }
        return (earlyDays * 100 / days.size).coerceAtMost(100)
    }
    
    private fun calculateChallengeProgress(sessions: List<SleepSession>, days: List<LocalDate>): Int {
        val challengeDays = days.count { date ->
            val session = sessions.find { it.startTime.toLocalDate() == date }
            session != null && session.wakeUps == 0 // No snoozes = challenge completed
        }
        return (challengeDays * 100 / days.size).coerceAtMost(100)
    }
    
    private fun calculateRoutineProgress(sessions: List<SleepSession>, days: List<LocalDate>): Int {
        val routineDays = days.count { date ->
            val session = sessions.find { it.startTime.toLocalDate() == date }
            session != null && session.notes.contains("routine_completed")
        }
        return (routineDays * 100 / days.size).coerceAtMost(100)
    }
    
    private fun calculateSleepQualityProgress(sessions: List<SleepSession>, days: List<LocalDate>): Int {
        val excellentDays = days.count { date ->
            val session = sessions.find { it.startTime.toLocalDate() == date }
            session?.quality == SleepQuality.EXCELLENT
        }
        return (excellentDays * 100 / days.size).coerceAtMost(100)
    }
}
