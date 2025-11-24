package com.alra.sof.chickin.data.models

import java.time.LocalDateTime
import java.util.UUID

data class SleepSession(
    val id: String = UUID.randomUUID().toString(),
    val startTime: LocalDateTime,
    val endTime: LocalDateTime? = null,
    val quality: SleepQuality = SleepQuality.UNKNOWN,
    val movements: Int = 0,
    val wakeUps: Int = 0,
    val notes: String = ""
)

enum class SleepQuality {
    EXCELLENT, GOOD, FAIR, POOR, UNKNOWN;
    
    fun getDescription(): String = when(this) {
        EXCELLENT -> "Excellent (7.5-9h)"
        GOOD -> "Good (6.5-7.5h)"
        FAIR -> "Needs Work (5.5-6.5h)"
        POOR -> "Poor (<5.5h or >9h)"
        UNKNOWN -> "Unknown"
    }
    
    fun getEmoji(): String = when(this) {
        EXCELLENT -> "😴"
        GOOD -> "😊"
        FAIR -> "😐"
        POOR -> "😴"
        UNKNOWN -> "❓"
    }
}

data class SleepStats(
    val averageDuration: Double = 0.0, // hours
    val averageQuality: SleepQuality = SleepQuality.UNKNOWN,
    val sleepDebt: Double = 0.0, // hours
    val consistency: Double = 0.0, // 0-100%
    val chronotype: Chronotype = Chronotype.INTERMEDIATE
)

enum class Chronotype {
    EARLY_BIRD, INTERMEDIATE, NIGHT_OWL
}

data class BreathingExercise(
    val name: String,
    val description: String,
    val inhale: Int, // seconds
    val hold: Int, // seconds
    val exhale: Int, // seconds
    val cycles: Int = 5
)

val defaultBreathingExercises = listOf(
    BreathingExercise(
        name = "4-7-8 Breathing",
        description = "Calming technique for sleep",
        inhale = 4,
        hold = 7,
        exhale = 8,
        cycles = 4
    ),
    BreathingExercise(
        name = "Box Breathing",
        description = "Equal breathing for relaxation",
        inhale = 4,
        hold = 4,
        exhale = 4,
        cycles = 5
    ),
    BreathingExercise(
        name = "Deep Relaxation",
        description = "Long exhale for deep calm",
        inhale = 4,
        hold = 2,
        exhale = 6,
        cycles = 6
    )
)

