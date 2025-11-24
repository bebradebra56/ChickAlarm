package com.alra.sof.chickin.regoif.domain.model

import com.google.gson.annotations.SerializedName


private const val CHICK_ALARM_A = "com.alra.sof.chickin"
private const val CHICK_ALARM_B = "chickalarm-ea57c"
data class ChickAlarmParam (
    @SerializedName("af_id")
    val chickAlarmAfId: String,
    @SerializedName("bundle_id")
    val chickAlarmBundleId: String = CHICK_ALARM_A,
    @SerializedName("os")
    val chickAlarmOs: String = "Android",
    @SerializedName("store_id")
    val chickAlarmStoreId: String = CHICK_ALARM_A,
    @SerializedName("locale")
    val chickAlarmLocale: String,
    @SerializedName("push_token")
    val chickAlarmPushToken: String,
    @SerializedName("firebase_project_id")
    val chickAlarmFirebaseProjectId: String = CHICK_ALARM_B,

    )