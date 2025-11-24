package com.alra.sof.chickin.data.models

import java.util.UUID

data class MorningTask(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val icon: TaskIcon,
    val duration: Int = 5, // minutes
    val isCompleted: Boolean = false,
    val order: Int = 0
)

enum class TaskIcon {
    WATER, EXERCISE, SHOWER, BREAKFAST, MEDITATION, READING, WEATHER, CALENDAR
}

data class MorningScenario(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Default",
    val tasks: List<MorningTask> = defaultMorningTasks,
    val autoStart: Boolean = false
)

val defaultMorningTasks = listOf(
    MorningTask(title = "Drink Water", icon = TaskIcon.WATER, duration = 2, order = 0),
    MorningTask(title = "Morning Exercise", icon = TaskIcon.EXERCISE, duration = 10, order = 1),
    MorningTask(title = "Take a Shower", icon = TaskIcon.SHOWER, duration = 15, order = 2),
    MorningTask(title = "Have Breakfast", icon = TaskIcon.BREAKFAST, duration = 20, order = 3),
    MorningTask(title = "Meditation", icon = TaskIcon.MEDITATION, duration = 5, order = 4)
)

data class WeatherInfo(
    val temperature: Int,
    val condition: String,
    val suggestion: String
)

data class CalendarEvent(
    val title: String,
    val time: String,
    val location: String = ""
)

