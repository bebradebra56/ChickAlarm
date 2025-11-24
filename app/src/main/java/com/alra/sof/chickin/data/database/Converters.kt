package com.alra.sof.chickin.data.database

import androidx.room.TypeConverter
import com.alra.sof.chickin.data.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromDayOfWeekSet(value: Set<DayOfWeek>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toDayOfWeekSet(value: String): Set<DayOfWeek> {
        val type = object : TypeToken<Set<DayOfWeek>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromVibratePattern(value: VibratePattern): String {
        return value.name
    }

    @TypeConverter
    fun toVibratePattern(value: String): VibratePattern {
        return VibratePattern.valueOf(value)
    }

    @TypeConverter
    fun fromAlarmProfile(value: AlarmProfile): String {
        return value.name
    }

    @TypeConverter
    fun toAlarmProfile(value: String): AlarmProfile {
        return AlarmProfile.valueOf(value)
    }

    @TypeConverter
    fun fromSleepQuality(value: SleepQuality): String {
        return value.name
    }

    @TypeConverter
    fun toSleepQuality(value: String): SleepQuality {
        return SleepQuality.valueOf(value)
    }

    @TypeConverter
    fun fromLongList(value: List<Long>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toLongList(value: String): List<Long> {
        val type = object : TypeToken<List<Long>>() {}.type
        return gson.fromJson(value, type)
    }
}

