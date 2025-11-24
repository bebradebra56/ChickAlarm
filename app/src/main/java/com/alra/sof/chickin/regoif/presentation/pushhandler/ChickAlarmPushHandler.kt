package com.alra.sof.chickin.regoif.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.alra.sof.chickin.regoif.presentation.app.ChickAlarmApplication

class ChickAlarmPushHandler {
    fun chickAlarmHandlePush(extras: Bundle?) {
        Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = chickAlarmBundleToMap(extras)
            Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    ChickAlarmApplication.CHICK_ALARM_FB_LI = map["url"]
                    Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "Push data no!")
        }
    }

    private fun chickAlarmBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}