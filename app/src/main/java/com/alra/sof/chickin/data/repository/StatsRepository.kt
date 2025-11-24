package com.alra.sof.chickin.data.repository

import com.alra.sof.chickin.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Duration
import java.time.LocalDate

class StatsRepository(
    private val alarmRepository: AlarmRepository,
    private val sleepRepository: SleepRepository
) {
    
    fun getDailyStats(): Flow<List<DailyStats>> {
        return combine(
            sleepRepository.allSessions,
            alarmRepository.allAlarms
        ) { sessions, _ ->
            if (sessions.isEmpty()) {
                return@combine emptyList()
            }
            
            val today = LocalDate.now()
            val stats = mutableListOf<DailyStats>()
            
            // Generate stats only for days with actual data
            for (i in 0..6) {
                val date = today.minusDays(i.toLong())
                
                val daySessions = sessions.filter { session ->
                    session.startTime.toLocalDate() == date
                }
                
                if (daySessions.isNotEmpty()) {
                    val session = daySessions.first()
                    val sleepDuration = session.endTime?.let { end ->
                        Duration.between(session.startTime, end).toMinutes() / 60.0
                    } ?: 0.0
                    
                    stats.add(
                        DailyStats(
                            date = date,
                            wakeTime = session.endTime?.toLocalTime()?.toString() ?: "",
                            alarmTime = "",
                            snoozeCount = 0,
                            challengeCompleted = true,
                            morningTasksCompleted = 0,
                            morningTasksTotal = 5,
                            sleepDuration = sleepDuration,
                            sleepQuality = session.quality
                        )
                    )
                }
            }
            
            stats.reversed()
        }
    }
    
    fun getWeeklyStats(): Flow<WeeklyStats?> {
        return combine(
            sleepRepository.allSessions,
            alarmRepository.allAlarms
        ) { sessions, _ ->
            if (sessions.isEmpty()) return@combine null
            
            val today = LocalDate.now()
            val weekStart = today.minusDays(6)
            
            val weekSessions = sessions.filter { session ->
                val startDate = session.startTime.toLocalDate()
                !startDate.isBefore(weekStart) && !startDate.isAfter(today)
            }
            
            if (weekSessions.isEmpty()) return@combine null
            
            val avgSleepDuration = weekSessions.mapNotNull { session ->
                session.endTime?.let { end ->
                    Duration.between(session.startTime, end).toMinutes() / 60.0
                }
            }.average()
            
            val avgWakeTime = weekSessions.mapNotNull { it.endTime?.toLocalTime() }
                .let { times ->
                    if (times.isNotEmpty()) {
                        val avgMinutes = times.map { it.hour * 60 + it.minute }.average().toInt()
                        String.format("%02d:%02d", avgMinutes / 60, avgMinutes % 60)
                    } else "—"
                }
            
            WeeklyStats(
                startDate = weekStart,
                averageWakeTime = avgWakeTime,
                consistency = 75.0,
                totalSnoozes = 0,
                successfulWakeUps = weekSessions.size,
                averageSleepDuration = avgSleepDuration
            )
        }
    }
}

