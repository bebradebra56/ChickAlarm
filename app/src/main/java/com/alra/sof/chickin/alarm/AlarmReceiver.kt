package com.alra.sof.chickin.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AlarmScheduler.ACTION_ALARM_TRIGGER) {
            val alarmId = intent.getStringExtra(AlarmScheduler.EXTRA_ALARM_ID) ?: return
            val alarmLabel = intent.getStringExtra(AlarmScheduler.EXTRA_ALARM_LABEL) ?: "Alarm"
            val soundId = intent.getStringExtra(AlarmScheduler.EXTRA_ALARM_SOUND) ?: "default_rooster"
            
            // Start foreground service to handle alarm
            val serviceIntent = Intent(context, AlarmService::class.java).apply {
                putExtra(AlarmScheduler.EXTRA_ALARM_ID, alarmId)
                putExtra(AlarmScheduler.EXTRA_ALARM_LABEL, alarmLabel)
                putExtra(AlarmScheduler.EXTRA_ALARM_SOUND, soundId)
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }

            // Start alarm ringing activity
            val activityIntent = Intent(context, AlarmRingingActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(AlarmScheduler.EXTRA_ALARM_ID, alarmId)
                putExtra(AlarmScheduler.EXTRA_ALARM_LABEL, alarmLabel)
            }
            context.startActivity(activityIntent)
        }
    }
}

