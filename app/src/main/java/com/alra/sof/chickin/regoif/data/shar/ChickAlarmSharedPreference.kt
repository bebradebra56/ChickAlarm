package com.alra.sof.chickin.regoif.data.shar

import android.content.Context
import androidx.core.content.edit

class ChickAlarmSharedPreference(context: Context) {
    private val chickAlarmPrefs = context.getSharedPreferences("chickAlarmSharedPrefsAb", Context.MODE_PRIVATE)

    var chickAlarmSavedUrl: String
        get() = chickAlarmPrefs.getString(CHICK_ALARM_SAVED_URL, "") ?: ""
        set(value) = chickAlarmPrefs.edit { putString(CHICK_ALARM_SAVED_URL, value) }

    var chickAlarmExpired : Long
        get() = chickAlarmPrefs.getLong(CHICK_ALARM_EXPIRED, 0L)
        set(value) = chickAlarmPrefs.edit { putLong(CHICK_ALARM_EXPIRED, value) }

    var chickAlarmAppState: Int
        get() = chickAlarmPrefs.getInt(CHICK_ALARM_APPLICATION_STATE, 0)
        set(value) = chickAlarmPrefs.edit { putInt(CHICK_ALARM_APPLICATION_STATE, value) }

    var chickAlarmNotificationRequest: Long
        get() = chickAlarmPrefs.getLong(CHICK_ALARM_NOTIFICAITON_REQUEST, 0L)
        set(value) = chickAlarmPrefs.edit { putLong(CHICK_ALARM_NOTIFICAITON_REQUEST, value) }

    var chickAlarmNotificationRequestedBefore: Boolean
        get() = chickAlarmPrefs.getBoolean(CHICK_ALARM_NOTIFICATION_REQUEST_BEFORE, false)
        set(value) = chickAlarmPrefs.edit { putBoolean(
            CHICK_ALARM_NOTIFICATION_REQUEST_BEFORE, value) }

    companion object {
        private const val CHICK_ALARM_SAVED_URL = "chickAlarmSavedUrl"
        private const val CHICK_ALARM_EXPIRED = "chickAlarmExpired"
        private const val CHICK_ALARM_APPLICATION_STATE = "chickAlarmApplicationState"
        private const val CHICK_ALARM_NOTIFICAITON_REQUEST = "chickAlarmNotificationRequest"
        private const val CHICK_ALARM_NOTIFICATION_REQUEST_BEFORE = "chickAlarmNotificationRequestedBefore"
    }
}