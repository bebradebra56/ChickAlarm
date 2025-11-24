package com.alra.sof.chickin.regoif.data.utils

import android.util.Log
import com.alra.sof.chickin.regoif.presentation.app.ChickAlarmApplication
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ChickAlarmPushToken {

    suspend fun chickAlarmGetToken(): String = suspendCoroutine { continuation ->
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (!it.isSuccessful) {
                    continuation.resume(it.result)
                    Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "Token error: ${it.exception}")
                } else {
                    continuation.resume(it.result)
                }
            }
        } catch (e: Exception) {
            Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "FirebaseMessagingPushToken = null")
            continuation.resume("")
        }
    }


}