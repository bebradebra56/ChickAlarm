package com.alra.sof.chickin.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.alra.sof.chickin.data.database.ChickAlarmDatabase
import com.alra.sof.chickin.data.repository.AlarmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            
            val database = ChickAlarmDatabase.getDatabase(context)
            val repository = AlarmRepository(database.alarmDao())
            val scheduler = AlarmScheduler(context)
            
            CoroutineScope(Dispatchers.IO).launch {
                repository.enabledAlarms.collect { alarms ->
                    alarms.forEach { alarm ->
                        scheduler.scheduleAlarm(alarm)
                    }
                }
            }
        }
    }
}

