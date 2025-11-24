package com.alra.sof.chickin.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.alra.sof.chickin.data.models.AlarmProfile
import com.alra.sof.chickin.data.models.DayOfWeek
import com.alra.sof.chickin.data.models.VibratePattern
import com.alra.sof.chickin.data.models.WakeChallenge

@Entity(tableName = "alarms")
@TypeConverters(Converters::class)
data class AlarmEntity(
    @PrimaryKey
    val id: String,
    val hour: Int,
    val minute: Int,
    val label: String,
    val isEnabled: Boolean,
    val repeatDays: Set<DayOfWeek>,
    val smartWakeWindow: Int,
    val challengeType: String,
    val challengeData: String,
    val soundId: String,
    val vibratePattern: VibratePattern,
    val volumeGradual: Boolean,
    val profile: AlarmProfile
)

