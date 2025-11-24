package com.alra.sof.chickin.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.alra.sof.chickin.data.models.SleepQuality

@Entity(tableName = "sleep_sessions")
@TypeConverters(Converters::class)
data class SleepSessionEntity(
    @PrimaryKey
    val id: String,
    val startTime: Long,
    val endTime: Long?,
    val quality: SleepQuality,
    val movements: Int,
    val wakeUps: Int,
    val notes: String
)

