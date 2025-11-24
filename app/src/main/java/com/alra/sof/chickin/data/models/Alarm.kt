package com.alra.sof.chickin.data.models

import java.time.LocalTime
import java.util.UUID

data class Alarm(
    val id: String = UUID.randomUUID().toString(),
    val time: LocalTime,
    val label: String = "",
    val isEnabled: Boolean = true,
    val repeatDays: Set<DayOfWeek> = emptySet(),
    val smartWakeWindow: Int = 0, // minutes before alarm time
    val challenge: WakeChallenge = WakeChallenge.None,
    val soundId: String = "default_rooster",
    val vibratePattern: VibratePattern = VibratePattern.GENTLE,
    val volumeGradual: Boolean = true,
    val profile: AlarmProfile = AlarmProfile.WEEKDAY
)

enum class DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

enum class AlarmProfile {
    WEEKDAY, WEEKEND, FLEXIBLE
}

sealed class WakeChallenge {
    object None : WakeChallenge()
    data class Puzzle(val difficulty: Int = 1) : WakeChallenge()
    data class QRScan(val qrCode: String = "") : WakeChallenge()
    data class PhotoMatch(val photoPath: String = "") : WakeChallenge()
    data class WalkSteps(val steps: Int = 30) : WakeChallenge()
    data class SpeechRecognition(val phrase: String = "I am awake") : WakeChallenge()
}

enum class VibratePattern {
    NONE, GENTLE, MEDIUM, STRONG
}

