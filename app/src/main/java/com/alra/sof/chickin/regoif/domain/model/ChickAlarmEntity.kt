package com.alra.sof.chickin.regoif.domain.model

import com.google.gson.annotations.SerializedName


data class ChickAlarmEntity (
    @SerializedName("ok")
    val chickAlarmOk: String,
    @SerializedName("url")
    val chickAlarmUrl: String,
    @SerializedName("expires")
    val chickAlarmExpires: Long,
)