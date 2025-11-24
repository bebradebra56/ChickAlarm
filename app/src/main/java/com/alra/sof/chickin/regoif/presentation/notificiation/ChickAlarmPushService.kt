package com.alra.sof.chickin.regoif.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.alra.sof.chickin.ChickAlarmActivity
import com.alra.sof.chickin.R
import com.alra.sof.chickin.regoif.presentation.app.ChickAlarmApplication
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val CHICK_ALARM_CHANNEL_ID = "chick_alarm_notifications"
private const val CHICK_ALARM_CHANNEL_NAME = "ChickAlarm Notifications"
private const val CHICK_ALARM_NOT_TAG = "ChickAlarm"

class ChickAlarmPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                chickAlarmShowNotification(it.title ?: CHICK_ALARM_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                chickAlarmShowNotification(it.title ?: CHICK_ALARM_NOT_TAG, it.body ?: "", data = null)
            }
        }

        // Обработка data payload
        if (remoteMessage.data.isNotEmpty()) {
            chickAlarmHandleDataPayload(remoteMessage.data)
        }
    }

    private fun chickAlarmShowNotification(title: String, message: String, data: String?) {
        val chickAlarmNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHICK_ALARM_CHANNEL_ID,
                CHICK_ALARM_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            chickAlarmNotificationManager.createNotificationChannel(channel)
        }

        val chickAlarmIntent = Intent(this, ChickAlarmActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val chickAlarmPendingIntent = PendingIntent.getActivity(
            this,
            0,
            chickAlarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val chickAlarmNotification = NotificationCompat.Builder(this, CHICK_ALARM_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_chick_alaram_noti)
            .setAutoCancel(true)
            .setContentIntent(chickAlarmPendingIntent)
            .build()

        chickAlarmNotificationManager.notify(System.currentTimeMillis().toInt(), chickAlarmNotification)
    }

    private fun chickAlarmHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "Data key=$key value=$value")
        }
    }
}