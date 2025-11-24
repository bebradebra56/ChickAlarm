package com.alra.sof.chickin.data.models

import java.time.LocalDate

data class DailyStats(
    val date: LocalDate,
    val wakeTime: String = "",
    val alarmTime: String = "",
    val snoozeCount: Int = 0,
    val challengeCompleted: Boolean = false,
    val morningTasksCompleted: Int = 0,
    val morningTasksTotal: Int = 0,
    val sleepDuration: Double = 0.0, // hours
    val sleepQuality: SleepQuality = SleepQuality.UNKNOWN
)

data class WeeklyStats(
    val startDate: LocalDate,
    val averageWakeTime: String = "",
    val consistency: Double = 0.0, // 0-100%
    val totalSnoozes: Int = 0,
    val successfulWakeUps: Int = 0,
    val averageSleepDuration: Double = 0.0,
    val achievements: List<Achievement> = emptyList()
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val unlockedDate: LocalDate? = null,
    val isUnlocked: Boolean = false,
    val progress: Int = 0, // 0-100%
    val target: Int = 100
)

val defaultAchievements = listOf(
    Achievement(
        id = "no_snooze_7",
        title = "Snooze-Free Week",
        description = "No snoozes for 7 days straight",
        icon = "🏆",
        target = 7
    ),
    Achievement(
        id = "consistent_30",
        title = "Steady Riser",
        description = "Wake up within 30 min window for 30 days",
        icon = "⏰",
        target = 30
    ),
    Achievement(
        id = "early_bird_7",
        title = "Early Bird",
        description = "Wake up before 6:30 AM for 7 days",
        icon = "🌅",
        target = 7
    ),
    Achievement(
        id = "challenge_master_14",
        title = "Challenge Master",
        description = "Complete wake challenges 14 days in a row",
        icon = "🎯",
        target = 14
    ),
    Achievement(
        id = "morning_routine_21",
        title = "Routine Champion",
        description = "Complete morning routine 21 days straight",
        icon = "✨",
        target = 21
    ),
    Achievement(
        id = "sleep_quality_7",
        title = "Sleep Expert",
        description = "Excellent sleep quality for 7 nights",
        icon = "😴",
        target = 7
    )
)

