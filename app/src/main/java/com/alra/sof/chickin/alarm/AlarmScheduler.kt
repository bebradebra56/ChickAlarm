package com.alra.sof.chickin.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.alra.sof.chickin.data.models.Alarm
import com.alra.sof.chickin.data.models.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar

class AlarmScheduler(private val context: Context) {
    
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAlarm(alarm: Alarm) {
        if (!alarm.isEnabled) {
            cancelAlarm(alarm)
            return
        }

        val triggerTime = calculateNextAlarmTime(alarm)
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM_TRIGGER
            putExtra(EXTRA_ALARM_ID, alarm.id)
            putExtra(EXTRA_ALARM_LABEL, alarm.label)
            putExtra(EXTRA_ALARM_SOUND, alarm.soundId)
            putExtra(EXTRA_SMART_WAKE_WINDOW, alarm.smartWakeWindow)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setAlarmClock(
                        AlarmManager.AlarmClockInfo(triggerTime, pendingIntent),
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(triggerTime, pendingIntent),
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // Fallback to inexact alarm
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    fun cancelAlarm(alarm: Alarm) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM_TRIGGER
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    private fun calculateNextAlarmTime(alarm: Alarm): Long {
        val now = LocalDateTime.now()
        val alarmTime = alarm.time
        
        if (alarm.repeatDays.isEmpty()) {
            // One-time alarm
            var alarmDateTime = LocalDateTime.of(LocalDate.now(), alarmTime)
            if (alarmDateTime.isBefore(now)) {
                alarmDateTime = alarmDateTime.plusDays(1)
            }
            return alarmDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }

        // Repeating alarm - find next occurrence
        var daysToAdd = 0
        var foundDay = false
        
        while (!foundDay && daysToAdd < 7) {
            val checkDate = now.toLocalDate().plusDays(daysToAdd.toLong())
            val checkDateTime = LocalDateTime.of(checkDate, alarmTime)
            val dayOfWeek = mapDayOfWeek(checkDate.dayOfWeek)
            
            if (alarm.repeatDays.contains(dayOfWeek) && checkDateTime.isAfter(now)) {
                foundDay = true
            } else {
                daysToAdd++
            }
        }

        val targetDate = now.toLocalDate().plusDays(daysToAdd.toLong())
        val targetDateTime = LocalDateTime.of(targetDate, alarmTime)
        
        return targetDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    private fun mapDayOfWeek(javaDayOfWeek: java.time.DayOfWeek): DayOfWeek {
        return when (javaDayOfWeek) {
            java.time.DayOfWeek.MONDAY -> DayOfWeek.MONDAY
            java.time.DayOfWeek.TUESDAY -> DayOfWeek.TUESDAY
            java.time.DayOfWeek.WEDNESDAY -> DayOfWeek.WEDNESDAY
            java.time.DayOfWeek.THURSDAY -> DayOfWeek.THURSDAY
            java.time.DayOfWeek.FRIDAY -> DayOfWeek.FRIDAY
            java.time.DayOfWeek.SATURDAY -> DayOfWeek.SATURDAY
            java.time.DayOfWeek.SUNDAY -> DayOfWeek.SUNDAY
        }
    }

    companion object {
        const val ACTION_ALARM_TRIGGER = "com.alra.sof.chickin.ALARM_TRIGGER"
        const val EXTRA_ALARM_ID = "alarm_id"
        const val EXTRA_ALARM_LABEL = "alarm_label"
        const val EXTRA_ALARM_SOUND = "alarm_sound"
        const val EXTRA_SMART_WAKE_WINDOW = "smart_wake_window"
    }
}

